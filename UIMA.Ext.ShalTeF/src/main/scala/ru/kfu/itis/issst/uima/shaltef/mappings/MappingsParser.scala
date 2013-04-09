package ru.kfu.itis.issst.uima.shaltef.mappings

import java.net.URL
import org.apache.uima.cas.Type
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.ConstraintValueFactory
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.ConstraintTargetFactory
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.PhraseConstraintFactory

trait MappingsParser {
  def parse(url: URL, templateAnnoType: Type, mappingsHolder: DepToArgMappingsBuilder)
}

class MappingsParserConfig(val morphDict: MorphDictionary) {
  val constraintValueFactory = new ConstraintValueFactory(morphDict)
  val constraintTargetFactory = new ConstraintTargetFactory(morphDict)
  val constraintFactory = new PhraseConstraintFactory
}