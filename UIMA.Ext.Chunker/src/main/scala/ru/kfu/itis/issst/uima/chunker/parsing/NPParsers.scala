/**
 *
 */
package ru.kfu.itis.issst.uima.chunker.parsing
import scala.util.parsing.combinator.Parsers
import org.opencorpora.cas.Word
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.{ MorphConstants => M }
import WordUtils._
import NPParsers._
import scala.collection.mutable.ListBuffer
import ru.kfu.cll.uima.tokenizer.fstype.NUM
import org.apache.uima.cas.text.AnnotationFS

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
trait NPParsers extends Parsers {

  type Elem = Word

  // adjective or perfective
  def aNom: Parser[Elem] = adjf(M.nomn) | prtf(M.nomn)
  def aGen: Parser[Elem] = adjf(M.gent) | prtf(M.gent)
  def aDat: Parser[Elem] = adjf(M.datv) | prtf(M.datv)
  def aAcc: Parser[Elem] = adjf(M.accs) | prtf(M.accs)
  def aAbl: Parser[Elem] = adjf(M.ablt) | prtf(M.ablt)
  def aLoc: Parser[Elem] = adjf(M.loct) | prtf(M.loct)

  // Coordinated Adjective + Noun
  def cANNom(grs: GrammemeMatcher*) =
    rep(aNom) ~ nounBase(has(M.nomn) +: grs: _*) ^^ { case deps ~ n => new NP(n, deps) }
  def cANGen(grs: GrammemeMatcher*) =
    rep(aGen) ~ nounBase(has(M.gent) +: grs: _*) ^^ { case deps ~ n => new NP(n, deps) }
  def cANDat(grs: GrammemeMatcher*) =
    rep(aDat) ~ nounBase(has(M.datv) +: grs: _*) ^^ { case deps ~ n => new NP(n, deps) }
  def cANAcc(grs: GrammemeMatcher*) =
    rep(aAcc) ~ nounBase(has(M.accs) +: grs: _*) ^^ { case deps ~ n => new NP(n, deps) }
  def cANAbl(grs: GrammemeMatcher*) =
    rep(aAbl) ~ nounBase(has(M.ablt) +: grs: _*) ^^ { case deps ~ n => new NP(n, deps) }
  def cANLoc(grs: GrammemeMatcher*) =
    rep(aLoc) ~ nounBase(has(M.loct) +: grs: _*) ^^ { case deps ~ n => new NP(n, deps) }

  // Noun base
  def nounBase(grs: GrammemeMatcher*) = noun(grs: _*) | pronoun(grs: _*)

  // NU = Numeral + Unit
  def nUNom = (numNot1 ~ cANGen() ^^ { case n ~ can => new NP(n, can) }
    | num1 ~ cANNom() ^^ { case n ~ can => new NP(n, can) })

  def nUGen = num ~ cANGen() ^^ { case n ~ can => new NP(n, can) }

  def nUDat = num ~ cANDat() ^^ { case n ~ can => new NP(n, can) }

  def nUAcc = (num24 ~ cANAcc(M.anim) ^^ { case n ~ can => new NP(n, can) }
    | num24 ~ cANGen(hasNot(M.anim)) ^^ { case n ~ can => new NP(n, can) }
    | num059 ~ cANGen() ^^ { case n ~ can => new NP(n, can) }
    | num1 ~ cANAcc() ^^ { case n ~ can => new NP(n, can) })

  def nUAbl = num ~ cANAbl() ^^ { case n ~ can => new NP(n, can) }

  def nULoc = num ~ cANLoc() ^^ { case n ~ can => new NP(n, can) }

  // Prepositional CAN
  def pCANNom = nUNom | cANNom()
  def pCANGen = opt(gentPrep) ~ (nUGen | cANGen()) ^^ {
    case Some(prep) ~ np => new NP(np.noun, prep :: np.deps)
    case None ~ np => np
  }
  def pCANDat = opt(datPrep) ~ (nUDat | cANDat()) ^^ {
    case Some(prep) ~ np => new NP(np.noun, prep :: np.deps)
    case None ~ np => np
  }
  def pCANAcc = opt(accPrep) ~ (nUAcc | cANAcc()) ^^ {
    case Some(prep) ~ np => new NP(np.noun, prep :: np.deps)
    case None ~ np => np
  }
  def pCANAbl = opt(ablPrep) ~ (nUAbl | cANAbl()) ^^ {
    case Some(prep) ~ np => new NP(np.noun, prep :: np.deps)
    case None ~ np => np
  }
  def pCANLoc = opt(locPrep) ~ (nULoc | cANLoc()) ^^ {
    case Some(prep) ~ np => new NP(np.noun, prep :: np.deps)
    case None ~ np => np
  }

  // NP = pCAN + genitives
  def np = (pCANNom | pCANGen | pCANDat | pCANAcc | pCANAbl | pCANLoc) ~ rep(cANGen()) ^^ {
    case headNP ~ depNPList => new NP(headNP.noun, headNP.deps ::: flatten(depNPList))
  }

  // atomic
  def adjf(grs: GrammemeMatcher*): Parser[Elem] = posParser(M.ADJF, grs: _*)
  def prtf(grs: GrammemeMatcher*): Parser[Elem] = posParser(M.PRTF, grs: _*)
  def noun(grs: GrammemeMatcher*): Parser[Elem] = posParser(M.NOUN, grs: _*)
  def pronoun(grs: GrammemeMatcher*): Parser[Elem] = posParser(M.NPRO, grs: _*)

  def gentPrep: Parser[Elem] = textParser(gentPrepositions)
  def datPrep: Parser[Elem] = textParser(datPrepositions)
  def accPrep: Parser[Elem] = textParser(accPrepositions)
  def ablPrep: Parser[Elem] = textParser(ablPrepositions)
  def locPrep: Parser[Elem] = textParser(locPrepositions)

  def posParser(pos: String, grs: GrammemeMatcher*) = new Parser[Elem] {
    def apply(in: Input) =
      if (in.atEnd) Failure("end of sequence detected", in)
      else if (checkGrammems(in.first, pos, grs: _*)) Success(in.first, in.rest)
      else Failure("%s with grammems {%s} expected".format(pos, grs), in)
  }

  def textParser(variants: Set[String]) = new Parser[Elem] {
    def apply(in: Input) =
      if (in.atEnd) Failure("end of sequence detected", in)
      else if (variants.contains(in.first.getCoveredText())) Success(in.first, in.rest)
      else Failure("One of %s was expected".format(variants), in)
  }

  // num ends on 1
  def num1 = num(endsOn(Set('1')) _)
  // num ends on 2,3,4
  def num24 = num(endsOn(Set('2', '3', '4')) _)
  // num ends on 0,5-9
  def num059 = num(endsOn(Set('0', '5', '6', '7', '8', '9')) _)
  // num ends on 0,2-9
  def numNot1 = num(n => !(endsOn(Set('1'))(n)))

  def num: Parser[Elem] = num(n => true)

  def num(matcher: NUM => Boolean) = new Parser[Elem] {
    def apply(in: Input) =
      if (in.atEnd) Failure("end of sequence detected", in)
      else in.first.getToken() match {
        case n: NUM => if (matcher(n)) Success(in.first, in.rest)
        else Failure("num does not match condition", in)
        case _ => Failure("NUM was expected", in)
      }
  }

  private def endsOn(requiredEnds: Set[Char])(anno: AnnotationFS): Boolean = {
    val annoTxt = anno.getCoveredText()
    requiredEnds.contains(annoTxt.last)
  }

  private implicit def stringToReqGramemme(grString: String): GrammemeRequired =
    has(grString)
}

class NP(val noun: Word, val deps: List[Word]) {
  def this(noun: Word, nps: NP*) = this(noun, flatten(nps))
}

object NPParsers {
  private val gentPrepositions = Set("без", "до", "из", "от", "у", "для", "ради", "между", "с")
  private val datPrepositions = Set("к", "по")
  private val accPrepositions = Set("про", "через", "сквозь", "в", "на", "о", "за", "под", "по", "с")
  private val ablPrepositions = Set("над", "перед", "между", "за", "под", "с")
  private val locPrepositions = Set("при", "в", "на", "о", "по")

  private[parsing] def flatten(nps: TraversableOnce[NP]): List[Word] = {
    val result = new ListBuffer[Word]
    for (np <- nps) {
      result += np.noun
      result ++= np.deps
    }
    result.toList
  }
}