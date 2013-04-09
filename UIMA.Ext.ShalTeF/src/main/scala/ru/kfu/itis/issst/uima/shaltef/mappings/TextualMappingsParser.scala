package ru.kfu.itis.issst.uima.shaltef.mappings

import java.net.URL
import org.apache.uima.cas.Type
import java.io.InputStreamReader
import java.io.BufferedReader
import scala.util.parsing.combinator.JavaTokenParsers
import org.apache.uima.cas.Feature
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary
import scala.collection.JavaConversions.iterableAsScalaIterable
import scala.collection.mutable.HashSet
import ru.kfu.itis.issst.uima.shaltef.mappings.impl.DefaultDepToArgMapping
import java.io.Reader
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.ConstraintValue
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.ConstraintValueFactory
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.Equals
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.ConstraintOperator
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.ConstraintConjunctionPhrasePattern
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.PhraseConstraint
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.ConstraintTarget
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.ConstraintTargetFactory
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.PhraseConstraintFactory
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.BinaryConstraintOperator
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.UnaryConstraintOperator
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.HasHeadsPath
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.PhraseConstraint

private[mappings] class TextualMappingsParser(config: MappingsParserConfig) extends MappingsParser {

  import config._

  override def parse(url: URL, templateAnnoType: Type, mappingsHolder: DepToArgMappingsBuilder) {
    val is = url.openStream()
    val reader = new BufferedReader(new InputStreamReader(is, "utf-8"))
    try {
      val mappings = new DocParsers(templateAnnoType, url).parseDoc(reader)
      mappings.foreach(mappingsHolder.add(_))
    } finally {
      reader.close()
    }
  }

  private class DocParsers(templateType: Type, url: URL) extends JavaTokenParsers {
    def parseDoc(reader: Reader): List[DepToArgMapping] = parseAll(mappingsDoc, reader) match {
      case Success(mappings, _) => mappings
      case NoSuccess(msg, remainingInput) => throw new IllegalStateException(
        "Unsuccessful parsing of %s at line %s & column %s:\n%s".format(
          url, remainingInput.pos.line, remainingInput.pos.column, msg))
    }

    private def mappingsDoc: Parser[List[DepToArgMapping]] =
      rep(rep(comment) ~> mappingDecl <~ rep(comment))

    private def comment: Parser[String] = """#[^\n]*""".r

    private def mappingDecl: Parser[DepToArgMapping] = triggerDecl ~ rep1(slotMapping) ^^ {
      case lemmaIdSet ~ slotMappings => new DefaultDepToArgMapping(templateType,
        lemmaIdSet, slotMappings)
    }

    private def triggerDecl = "[" ~> triggerConstraint <~ "]"

    private def triggerConstraint = "lemma(" ~> rep1sep(identifiedWordformLiteral, ",") <~ ")" ^^ {
      _.toSet.flatten
    }

    private def identifiedWordformLiteral = stringLiteral ^? {
      case strLiteral => {
        val str = strLiteral.substring(1, strLiteral.length - 1)
        val strWordforms = morphDict.getEntries(str)
        val lemmaIdSet = new HashSet[Int]
        if (strWordforms != null)
          for (strWf <- strWordforms; if (strWf.getLemmaId >= 0))
            lemmaIdSet.add(strWf.getLemmaId)
        if (strWordforms.isEmpty)
          throw new IllegalArgumentException("Can't find lemmaId for word '%s'".format(str))
        else lemmaIdSet.toSet
      }
    }

    private def slotMapping: Parser[SlotMapping] =
      slotPattern ~ slotMappingOptionality ~ (templateFeatureName | templateFeatureStub) ^^ {
        case pattern ~ optionality ~ slotFeatureOpt =>
          new SlotMapping(pattern, optionality, slotFeatureOpt)
      }

    private def slotMappingOptionality: Parser[Boolean] = ("=>" | "?=>") ^^ {
      case "=>" => false
      case "?=>" => true
    }

    private def templateFeatureName: Parser[Option[Feature]] = ident ^? ({
      case featName if templateType.getFeatureByBaseName(featName) != null =>
        Some(templateType.getFeatureByBaseName(featName))
    }, "Type %s does not have feature '%s'".format(templateType, _))

    private def templateFeatureStub: Parser[Option[Feature]] = "_" ^^ { _ => None }

    private def slotPattern = rep1sep(slotConstraint, "&") ^^ {
      new ConstraintConjunctionPhrasePattern(_)
    }

    private def slotConstraint = slotConstraintBinOp | slotConstraintUnOp

    private def slotConstraintBinOp = constraintTarget ~ constraintBinOp ~ constraintValue ^^ {
      case target ~ op ~ value => constraintFactory.phraseConstraint(target, op, value)
    }

    private def slotConstraintUnOp = unOpConstraint(headPathOp, constantMatrix)

    import constraintValueFactory._
    import constraintTargetFactory._

    private def constraintTarget: Parser[ConstraintTarget] = rep1sep(ident, ".") ^? ({
      case List("head", gramCat) => headFeature(gramCat)
      case List("prep") => prepositionTarget
    }, "Unknown constraint target: %s".format(_))

    private def constraintBinOp: Parser[BinaryConstraintOperator] =
      "=" ^^ { _ => Equals }

    private def unOpConstraint(
      unOpParser: Parser[UnaryConstraintOperator],
      valueParser: Parser[ConstraintValue]): Parser[PhraseConstraint] =
      (unOpParser <~ "(") ~ valueParser <~ ")" ^^ {
        case unOp ~ value => constraintFactory.phraseConstraint(unOp, value)
      }

    private def headPathOp: Parser[UnaryConstraintOperator] = "headPath" ^^ {
      _ => HasHeadsPath
    }

    private def constraintValue: Parser[ConstraintValue] = (constantValue
      | triggerValueRef
      | constantList
      | constantMatrix)

    private def constantValue = constantParser ^^ {
      str => constant(str)
    }

    private def constantParser = stringLiteral ^^ {
      str => str.substring(1, str.length() - 1)
    }

    private def triggerValueRef = "$trigger." ~> ident ^^ {
      triggerFeatureReference(_)
    }

    private def constantList = rep1sep(constantParser, ",") ^^ {
      constantCollection(_)
    }

    private def constantMatrix = rep1sep(rep1sep(constantParser, ","), "|") ^^ {
      listList => constraintValueFactory.constantCollectionAlternatives(listList.toSet)
    }
  }
}

object TextualMappingsParser {
  def apply(config: MappingsParserConfig): MappingsParser = new TextualMappingsParser(config)
}