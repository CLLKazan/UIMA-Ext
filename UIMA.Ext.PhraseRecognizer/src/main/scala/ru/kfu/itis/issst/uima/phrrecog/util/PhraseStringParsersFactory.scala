package ru.kfu.itis.issst.uima.phrrecog.util

import org.apache.uima.jcas.JCas
import org.apache.uima.cas.text.AnnotationFS

trait PhraseStringParsersFactory {

  def createParser(jCas: JCas, tokens: Array[AnnotationFS]): PhraseStringParsers

}