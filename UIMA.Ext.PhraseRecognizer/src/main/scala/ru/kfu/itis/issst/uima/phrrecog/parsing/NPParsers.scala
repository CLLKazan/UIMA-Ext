/**
 *
 */
package ru.kfu.itis.issst.uima.phrrecog.parsing
import scala.util.parsing.combinator.Parsers
import org.opencorpora.cas.Word
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.{ MorphConstants => M }
import WordUtils._
import NPParsers._
import scala.collection.mutable.ListBuffer
import ru.kfu.cll.uima.tokenizer.fstype.NUM
import org.apache.uima.cas.text.AnnotationFS
import scala.collection.immutable.Queue
import org.opencorpora.cas.Wordform

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
trait NPParsers extends Parsers {

  type Elem = Word

  // atomic
  def adjf(grs: GrammemeMatcher*) = posParser(M.ADJF, grs: _*)
  def prtf(grs: GrammemeMatcher*) = posParser(M.PRTF, grs: _*)
  def noun(grs: GrammemeMatcher*) = posParser(M.NOUN, grs: _*)
  def pronoun(grs: GrammemeMatcher*) = posParser(M.NPRO, grs: _*)

  // adjective or perfective
  def aNom = adjf(M.nomn) | prtf(M.nomn)
  def aGen = adjf(M.gent) | prtf(M.gent)
  def aDat = adjf(M.datv) | prtf(M.datv)
  def aAcc = adjf(M.accs) | prtf(M.accs)
  def aAbl = adjf(M.ablt) | prtf(M.ablt)
  def aLoc = adjf(M.loct) | prtf(M.loct)

  // Noun base
  def nounBase(grs: GrammemeMatcher*) = noun(grs: _*) | pronoun(grs: _*)

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

  // prepositions
  def gentPrep = textParser(gentPrepositions, M.PREP)
  def datPrep = textParser(datPrepositions, M.PREP)
  def accPrep = textParser(accPrepositions, M.PREP)
  def ablPrep = textParser(ablPrepositions, M.PREP)
  def locPrep = textParser(locPrepositions, M.PREP)

  // Prepositional CAN
  def pCANNom = nUNom | cANNom()
  def pCANGen = opt(gentPrep) ~ (nUGen | cANGen()) ^^ {
    case Some(prep) ~ np => np.setPreposition(prep) //new NP(noun = np.noun, prepOpt = Some(prep), depWords = np.depWords)
    case None ~ np => np
  }
  def pCANDat = opt(datPrep) ~ (nUDat | cANDat()) ^^ {
    case Some(prep) ~ np => np.setPreposition(prep) // new NP(np.noun, Some(prep), np.deps)
    case None ~ np => np
  }
  def pCANAcc = opt(accPrep) ~ (nUAcc | cANAcc()) ^^ {
    case Some(prep) ~ np => np.setPreposition(prep) // new NP(np.noun, Some(prep), np.deps)
    case None ~ np => np
  }
  def pCANAbl = opt(ablPrep) ~ (nUAbl | cANAbl()) ^^ {
    case Some(prep) ~ np => np.setPreposition(prep) // new NP(np.noun, Some(prep), np.deps)
    case None ~ np => np
  }
  def pCANLoc = opt(locPrep) ~ (nULoc | cANLoc()) ^^ {
    case Some(prep) ~ np => np.setPreposition(prep) // new NP(np.noun, Some(prep), np.deps)
    case None ~ np => np
  }

  // NP = pCAN + genitives
  def np = (pCANNom | pCANGen | pCANDat | pCANAcc | pCANAbl | pCANLoc) ~ rep(cANGen()) ^^ {
    case headNP ~ depNPList => {
      val genHeadOpt = toDependentNPChain(depNPList)
      genHeadOpt match {
        case None => headNP
        case Some(genHead) =>
          if (headNP.depNPs.isEmpty)
            headNP.addDependentNP(genHeadOpt)
          else new NP(headNP.noun, headNP.prepOpt, headNP.particleOpt, headNP.depWords,
            // add genitive NP chain head to last
            headNP.depNPs.init + headNP.depNPs.last.addDependentNP(genHeadOpt))
      }
    }
  }

  def posParser(pos: String, grs: GrammemeMatcher*) = new Parser[Wordform] {
    override def apply(in: Input) =
      if (in.atEnd) Failure("end of sequence detected", in)
      else findWordform(in.first, pos, grs: _*) match {
        case Some(wf) => Success(wf, in.rest)
        case None => Failure("%s with grammems {%s} expected".format(pos, grs), in)
      }
  }

  def textParser(variants: Set[String], requiredPos: String) = new Parser[Wordform] {
    def apply(in: Input) =
      if (in.atEnd) Failure("end of sequence detected", in)
      else if (variants.contains(in.first.getCoveredText))
        findWordform(in.first, requiredPos) match {
          case Some(wf) => Success(wf, in.rest)
          case None => Failure(
            "Found word '%s' does not have expected pos '%s'".format(in.first.getCoveredText, requiredPos),
            in)
        }
      else Failure("One of %s was expected".format(variants), in)
  }

  // num ends on 1
  def num1 = num(endsOn(Set('1'))(_), M.NUMR)
  // num ends on 2,3,4
  def num24 = num(endsOn(Set('2', '3', '4'))(_), M.NUMR)
  // num ends on 0,5-9
  def num059 = num(endsOn(Set('0', '5', '6', '7', '8', '9'))(_), M.NUMR)
  // num ends on 0,2-9
  def numNot1 = num(n => !(endsOn(Set('1'))(n)), M.NUMR)

  def num: Parser[Wordform] = num(n => true, M.NUMR)

  def num(matcher: NUM => Boolean, requiredPos: String) = new Parser[Wordform] {
    def apply(in: Input) =
      if (in.atEnd) Failure("end of sequence detected", in)
      else in.first.getToken() match {
        case n: NUM => if (matcher(n))
          findWordform(in.first, requiredPos) match {
            case Some(wf) => Success(wf, in.rest)
            case None => Failure(
              "NUM word '%s' does not have required pos '%s'"
                .format(in.first.getCoveredText, requiredPos),
              in)
          }
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

class NP(val noun: Wordform,
  val prepOpt: Option[Wordform] = None, val particleOpt: Option[Wordform] = None,
  val depWords: List[Wordform] = Nil, val depNPs: Queue[NP] = Queue()) {
  // aux constructor
  def this(noun: Wordform, nps: NP*) = this(noun, None, None, Nil, Queue() ++ nps)
  // aux constructor
  def this(noun: Wordform, deps: List[Wordform]) = this(noun, None, None, deps, Queue())
  // clone and change
  def setPreposition(newPrep: Wordform): NP =
    if (prepOpt.isDefined) throw new IllegalStateException(
      "Can't add preposition '%s' because NP already has one: '%s'".format(
        newPrep.getWord.getCoveredText, prepOpt.get.getWord.getCoveredText))
    else new NP(noun, Some(newPrep), particleOpt, depWords, depNPs)
  // clone and change
  def addDependentNP(newDepNPOpt: Option[NP]): NP = newDepNPOpt match {
    case None => this
    case Some(newDepNP) => new NP(noun, prepOpt, particleOpt, depWords, depNPs.enqueue(newDepNP))
  }
}

object NPParsers {
  private val gentPrepositions = generateCommonWordsSet("без", "до", "из", "от", "у", "для", "ради", "между", "с")
  private val datPrepositions = generateCommonWordsSet("к", "по")
  private val accPrepositions = generateCommonWordsSet("про", "через", "сквозь", "в", "на", "о", "за", "под", "по", "с")
  private val ablPrepositions = generateCommonWordsSet("над", "перед", "между", "за", "под", "с")
  private val locPrepositions = generateCommonWordsSet("при", "в", "на", "о", "по")

  /*
  private[parsing] def flatten(nps: TraversableOnce[NP]): List[Word] = {
    val result = new ListBuffer[Word]
    for (np <- nps) {
      result += np.noun
      result ++= np.deps
    }
    result.toList
  }
  */
  private def toDependentNPChain(nps: List[NP]): Option[NP] =
    if (nps == null || nps.isEmpty) None
    else Some(nps.head.addDependentNP(toDependentNPChain(nps.tail)))

  private def generateCommonWordsSet(words: String*): Set[String] =
    Set() ++ words ++ words.map(_.capitalize)
}