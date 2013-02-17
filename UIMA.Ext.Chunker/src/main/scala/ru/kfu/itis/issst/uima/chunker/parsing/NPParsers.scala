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
  def cANNom = rep(aNom) ~ nounBase(M.nomn) ^^ { case deps ~ n => new NP(n, deps) }
  def cANGen = rep(aGen) ~ nounBase(M.gent) ^^ { case deps ~ n => new NP(n, deps) }
  def cANDat = rep(aDat) ~ nounBase(M.datv) ^^ { case deps ~ n => new NP(n, deps) }
  def cANAcc = rep(aAcc) ~ nounBase(M.accs) ^^ { case deps ~ n => new NP(n, deps) }
  def cANAbl = rep(aAbl) ~ nounBase(M.ablt) ^^ { case deps ~ n => new NP(n, deps) }
  def cANLoc = rep(aLoc) ~ nounBase(M.loct) ^^ { case deps ~ n => new NP(n, deps) }

  // Noun base
  def nounBase(grs: String*) = noun(grs: _*) | pronoun(grs: _*)

  // Prepositional CAN
  def pCANNom = cANNom
  def pCANGen = opt(gentPrep) ~ cANGen ^^ {
    case Some(prep) ~ np => new NP(np.noun, prep :: np.deps)
    case None ~ np => np
  }
  def pCANDat = opt(datPrep) ~ cANDat ^^ {
    case Some(prep) ~ np => new NP(np.noun, prep :: np.deps)
    case None ~ np => np
  }
  def pCANAcc = opt(accPrep) ~ cANAcc ^^ {
    case Some(prep) ~ np => new NP(np.noun, prep :: np.deps)
    case None ~ np => np
  }
  def pCANAbl = opt(ablPrep) ~ cANAbl ^^ {
    case Some(prep) ~ np => new NP(np.noun, prep :: np.deps)
    case None ~ np => np
  }
  def pCANLoc = opt(locPrep) ~ cANLoc ^^ {
    case Some(prep) ~ np => new NP(np.noun, prep :: np.deps)
    case None ~ np => np
  }

  // NP = pCAN + genitives
  def np = (pCANNom | pCANGen | pCANDat | pCANAcc | pCANAbl | pCANLoc) ~ rep(cANGen) ^^ {
    case headNP ~ depNPList => new NP(headNP.noun, headNP.deps ::: flatten(depNPList))
  }

  // atomic
  def adjf(grs: String*): Parser[Elem] = posParser(M.ADJF, grs: _*)
  def prtf(grs: String*): Parser[Elem] = posParser(M.PRTF, grs: _*)
  def noun(grs: String*): Parser[Elem] = posParser(M.NOUN, grs: _*)
  def pronoun(grs: String*): Parser[Elem] = posParser(M.NPRO, grs: _*)

  def gentPrep: Parser[Elem] = textParser(gentPrepositions)
  def datPrep: Parser[Elem] = textParser(datPrepositions)
  def accPrep: Parser[Elem] = textParser(accPrepositions)
  def ablPrep: Parser[Elem] = textParser(ablPrepositions)
  def locPrep: Parser[Elem] = textParser(locPrepositions)

  def posParser(pos: String, grs: String*) = new Parser[Elem] {
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

  private def flatten(nps: List[NP]): List[Elem] = {
    val result = new ListBuffer[Elem]
    for (np <- nps) {
      result += np.noun
      result ++= np.deps
    }
    result.toList
  }
}

class NP(val noun: Word, val deps: List[Word])

object NPParsers {
  private val gentPrepositions = Set("без", "до", "из", "от", "у", "для", "ради", "между", "с")
  private val datPrepositions = Set("к", "по")
  private val accPrepositions = Set("про", "через", "сквозь", "в", "на", "о", "за", "под", "по", "с")
  private val ablPrepositions = Set("над", "перед", "между", "за", "под", "с")
  private val locPrepositions = Set("при", "в", "на", "о", "по")
}