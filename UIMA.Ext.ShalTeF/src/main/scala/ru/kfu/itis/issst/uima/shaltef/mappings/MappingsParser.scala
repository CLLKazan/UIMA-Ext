package ru.kfu.itis.issst.uima.shaltef.mappings

import java.net.URL
import org.apache.uima.cas.Type

trait MappingsParser {
  def parse(url: URL, templateAnnoType: Type, mappingsHolder: DepToArgMappingsHolder)
}

object MappingsParser {
  def getInstance(): MappingsParser = throw new UnsupportedOperationException
}