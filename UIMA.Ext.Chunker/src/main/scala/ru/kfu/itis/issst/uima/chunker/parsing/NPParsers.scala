/**
 *
 */
package ru.kfu.itis.issst.uima.chunker.parsing
import scala.util.parsing.combinator.Parsers
import org.opencorpora.cas.Word
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.{ MorphConstants => M }
import WordUtils._

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

  // repetitions
  def pNom = rep(aNom) ~ noun(M.nomn) ^^ { case deps ~ n => new NP(n, deps) }
  def pGen = rep(aGen) ~ noun(M.gent) ^^ { case deps ~ n => new NP(n, deps) }
  def pDat = rep(aDat) ~ noun(M.datv) ^^ { case deps ~ n => new NP(n, deps) }
  def pAcc = rep(aAcc) ~ noun(M.accs) ^^ { case deps ~ n => new NP(n, deps) }
  def pAbl = rep(aAbl) ~ noun(M.ablt) ^^ { case deps ~ n => new NP(n, deps) }
  def pLoc = rep(aLoc) ~ noun(M.loct) ^^ { case deps ~ n => new NP(n, deps) }

  // NP
  def np = pNom | pGen | pDat | pAcc | pAbl | pLoc

  // atomic
  def adjf(grs: String*): Parser[Elem] = posParser(M.ADJF, grs: _*)
  def prtf(grs: String*): Parser[Elem] = posParser(M.PRTF, grs: _*)
  def noun(grs: String*): Parser[Elem] = posParser(M.NOUN, grs: _*)

  def posParser(pos: String, grs: String*) = new Parser[Elem] {
    def apply(in: Input) =
      if (in.atEnd) Failure("end of sequence detected", in)
      else if (checkGrammems(in.first, pos, grs: _*)) Success(in.first, in.rest)
      else Failure("%s with grammems {%s} expected".format(pos, grs), in)
  }
}

class NP(val noun: Word, val deps: List[Word])