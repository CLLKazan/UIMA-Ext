package ru.kfu.itis.issst.uima.shaltef.util

import org.apache.uima.cas.TypeSystem
import org.uimafit.factory.TypeSystemDescriptionFactory._
import org.apache.uima.util.CasCreationUtils
import org.apache.uima.jcas.JCas

trait CasTestUtils {

  protected def loadTypeSystem(names: String*): TypeSystem = {
    val tsDesc = createTypeSystemDescription(names: _*)
    val dumbCas = CasCreationUtils.createCas(tsDesc, null, null)
    dumbCas.getTypeSystem()
  }

  protected def createJCas(ts: TypeSystem): JCas =
    CasCreationUtils.createCas(ts, null, null, null).getJCas()
}