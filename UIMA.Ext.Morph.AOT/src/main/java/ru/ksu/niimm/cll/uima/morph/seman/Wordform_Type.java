
/* First created by JCasGen Fri Apr 20 11:23:25 MSK 2012 */
package ru.ksu.niimm.cll.uima.morph.seman;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** Wordform object
 * Updated by JCasGen Fri Apr 20 11:23:25 MSK 2012
 * @generated */
public class Wordform_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Wordform_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Wordform_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Wordform(addr, Wordform_Type.this);
  			   Wordform_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Wordform(addr, Wordform_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Wordform.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ru.ksu.niimm.cll.uima.morph.seman.Wordform");
 
  /** @generated */
  final Feature casFeat_paradigm;
  /** @generated */
  final int     casFeatCode_paradigm;
  /** @generated */ 
  public int getParadigm(int addr) {
        if (featOkTst && casFeat_paradigm == null)
      jcas.throwFeatMissing("paradigm", "ru.ksu.niimm.cll.uima.morph.seman.Wordform");
    return ll_cas.ll_getRefValue(addr, casFeatCode_paradigm);
  }
  /** @generated */    
  public void setParadigm(int addr, int v) {
        if (featOkTst && casFeat_paradigm == null)
      jcas.throwFeatMissing("paradigm", "ru.ksu.niimm.cll.uima.morph.seman.Wordform");
    ll_cas.ll_setRefValue(addr, casFeatCode_paradigm, v);}
    
  
 
  /** @generated */
  final Feature casFeat_grammems;
  /** @generated */
  final int     casFeatCode_grammems;
  /** @generated */ 
  public int getGrammems(int addr) {
        if (featOkTst && casFeat_grammems == null)
      jcas.throwFeatMissing("grammems", "ru.ksu.niimm.cll.uima.morph.seman.Wordform");
    return ll_cas.ll_getRefValue(addr, casFeatCode_grammems);
  }
  /** @generated */    
  public void setGrammems(int addr, int v) {
        if (featOkTst && casFeat_grammems == null)
      jcas.throwFeatMissing("grammems", "ru.ksu.niimm.cll.uima.morph.seman.Wordform");
    ll_cas.ll_setRefValue(addr, casFeatCode_grammems, v);}
    
   /** @generated */
  public String getGrammems(int addr, int i) {
        if (featOkTst && casFeat_grammems == null)
      jcas.throwFeatMissing("grammems", "ru.ksu.niimm.cll.uima.morph.seman.Wordform");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_grammems), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_grammems), i);
	return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_grammems), i);
  }
   
  /** @generated */ 
  public void setGrammems(int addr, int i, String v) {
        if (featOkTst && casFeat_grammems == null)
      jcas.throwFeatMissing("grammems", "ru.ksu.niimm.cll.uima.morph.seman.Wordform");
    if (lowLevelTypeChecks)
      ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_grammems), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_grammems), i);
    ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_grammems), i, v);
  }
 
 
  /** @generated */
  final Feature casFeat_flexionNo;
  /** @generated */
  final int     casFeatCode_flexionNo;
  /** @generated */ 
  public int getFlexionNo(int addr) {
        if (featOkTst && casFeat_flexionNo == null)
      jcas.throwFeatMissing("flexionNo", "ru.ksu.niimm.cll.uima.morph.seman.Wordform");
    return ll_cas.ll_getIntValue(addr, casFeatCode_flexionNo);
  }
  /** @generated */    
  public void setFlexionNo(int addr, int v) {
        if (featOkTst && casFeat_flexionNo == null)
      jcas.throwFeatMissing("flexionNo", "ru.ksu.niimm.cll.uima.morph.seman.Wordform");
    ll_cas.ll_setIntValue(addr, casFeatCode_flexionNo, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Wordform_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_paradigm = jcas.getRequiredFeatureDE(casType, "paradigm", "ru.ksu.niimm.cll.uima.morph.seman.Paradigm", featOkTst);
    casFeatCode_paradigm  = (null == casFeat_paradigm) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_paradigm).getCode();

 
    casFeat_grammems = jcas.getRequiredFeatureDE(casType, "grammems", "uima.cas.StringArray", featOkTst);
    casFeatCode_grammems  = (null == casFeat_grammems) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_grammems).getCode();

 
    casFeat_flexionNo = jcas.getRequiredFeatureDE(casType, "flexionNo", "uima.cas.Integer", featOkTst);
    casFeatCode_flexionNo  = (null == casFeat_flexionNo) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_flexionNo).getCode();

  }
}



    