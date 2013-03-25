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

private[mappings] class TextualMappingsParser(morphDict: MorphDictionary) extends MappingsParser {

  override def parse(url: URL, templateAnnoType: Type, mappingsHolder: DepToArgMappingsBuilder) {
    val is = url.openStream()
    val reader = new BufferedReader(new InputStreamReader(is, "utf-8"))
    try {
      val mappings = new DocParsers(templateAnnoType).parseDoc(reader)
      mappings.foreach(mappingsHolder.add(_))
    } finally {
      reader.close()
    }
  }

  private class DocParsers(templateType: Type) extends JavaTokenParsers {
    def parseDoc(reader: Reader): List[DepToArgMapping] = parseAll(mappingsDoc, reader) match {
      case Success(mappings, _) => mappings
      case NoSuccess(msg, remainingInput) => throw new IllegalStateException(
        "Unsuccessful parsing: %s\n".format(msg))
    }

    private def mappingsDoc: Parser[List[DepToArgMapping]] = rep(mappingDecl)

    private def mappingDecl: Parser[DepToArgMapping] = triggerDecl ~ rep1sep(slotMapping, ",") ^^ {
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
      slotPattern ~ slotMappingOptionality ~ templateFeatureName ^^ {
        case pattern ~ optionality ~ slotFeature =>
          new SlotMapping(pattern, optionality, slotFeature)
      }

    private def slotMappingOptionality: Parser[Boolean] = ("=>" | "?=>") ^^ {
      case "=>" => false
      case "?=>" => true
    }

    private def templateFeatureName: Parser[Feature] = ident ^? ({
      case featName if templateType.getFeatureByBaseName(featName) != null =>
        templateType.getFeatureByBaseName(featName)
    }, "Type %s does not have feature '%s'".format(templateType, _))

    private def slotPattern = rep1sep(slotConstraint, "&") ^^ {
      new ConstraintConjunctionPhrasePattern(_)
    }

    private def slotConstraint = constraintTarget ~ constraintOp ~ constraintValue ^^ {
      case target ~ op ~ value => new PhraseConstraint(target, op, value)
    }

    private def constraintTarget: Parser[ConstraintTarget] = rep1sep(ident, ".") ^? ({
      case List("head", gramCat) => new HeadGrammemeConstraint(gramCat)
      case List("words") => WordsConstraint
      case List("prep") => PrepositionConstraint
    }, "Unknown constraint target: %s".format(_))

    private def constraintOp: Parser[ConstraintOperator] = "=" ^^ { _ => Equals }

    private def constraintValue: Parser[ConstraintValue] = constantValue | triggerValueRef

    private def constantValue = stringLiteral ^^ {
      str => ConstantValue(str.substring(1, str.length() - 1))
    }

    private def triggerValueRef = "$trigger." ~> ident ^^ { new TriggerFeatureReference(_) }
  }
}

object TextualMappingsParser {
  def apply(morphDict: MorphDictionary): MappingsParser = new TextualMappingsParser(morphDict)
}