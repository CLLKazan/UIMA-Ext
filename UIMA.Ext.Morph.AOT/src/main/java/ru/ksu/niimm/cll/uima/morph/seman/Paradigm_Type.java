
/* First created by JCasGen Mon Apr 16 22:33:21 MSK 2012 */
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

/** Wordform paradigm object
 * Updated by JCasGen Mon Apr 16 22:33:21 MSK 2012
 * @generated */
public class Paradigm_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Paradigm_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Paradigm_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Paradigm(addr, Paradigm_Type.this);
  			   Paradigm_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Paradigm(addr, Paradigm_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Paradigm.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ru.ksu.niimm.cll.uima.morph.seman.Paradigm");
 
  /** @generated */
  final Feature casFeat_pos;
  /** @generated */
  final int     casFeatCode_pos;
  /** @generated */ 
  public String getPos(int addr) {
        if (featOkTst && casFeat_pos == null)
      jcas.throwFeatMissing("pos", "ru.ksu.niimm.cll.uima.morph.seman.Paradigm");
    return ll_cas.ll_getStringValue(addr, casFeatCode_pos);
  }
  /** @generated */    
  public void setPos(int addr, String v) {
        if (featOkTst && casFeat_pos == null)
      jcas.throwFeatMissing("pos", "ru.ksu.niimm.cll.uima.morph.seman.Paradigm");
    ll_cas.ll_setStringValue(addr, casFeatCode_pos, v);}
    
  
 
  /** @generated */
  final Feature casFeat_grammems;
  /** @generated */
  final int     casFeatCode_grammems;
  /** @generated */ 
  public int getGrammems(int addr) {
        if (featOkTst && casFeat_grammems == null)
      jcas.throwFeatMissing("grammems", "ru.ksu.niimm.cll.uima.morph.seman.Paradigm");
    return ll_cas.ll_getRefValue(addr, casFeatCode_grammems);
  }
  /** @generated */    
  public void setGrammems(int addr, int v) {
        if (featOkTst && casFeat_grammems == null)
      jcas.throwFeatMissing("grammems", "ru.ksu.niimm.cll.uima.morph.seman.Paradigm");
    ll_cas.ll_setRefValue(addr, casFeatCode_grammems, v);}
    
   /** @generated */
  public String getGrammems(int addr, int i) {
        if (featOkTst && casFeat_grammems == null)
      jcas.throwFeatMissing("grammems", "ru.ksu.niimm.cll.uima.morph.seman.Paradigm");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_grammems), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_grammems), i);
	return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_grammems), i);
  }
   
  /** @generated */ 
  public void setGrammems(int addr, int i, String v) {
        if (featOkTst && casFeat_grammems == null)
      jcas.throwFeatMissing("grammems", "ru.ksu.niimm.cll.uima.morph.seman.Paradigm");
    if (lowLevelTypeChecks)
      ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_grammems), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_grammems), i);
    ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_grammems), i, v);
  }
 
 
  /** @generated */
  final Feature casFeat_lemma;
  /** @generated */
  final int     casFeatCode_lemma;
  /** @generated */ 
  public String getLemma(int addr) {
        if (featOkTst && casFeat_lemma == null)
      jcas.throwFeatMissing("lemma", "ru.ksu.niimm.cll.uima.morph.seman.Paradigm");
    return ll_cas.ll_getStringValue(addr, casFeatCode_lemma);
  }
  /** @generated */    
  public void setLemma(int addr, String v) {
        if (featOkTst && casFeat_lemma == null)
      jcas.throwFeatMissing("lemma", "ru.ksu.niimm.cll.uima.morph.seman.Paradigm");
    ll_cas.ll_setStringValue(addr, casFeatCode_lemma, v);}
    
  
 
  /** @generated */
  final Feature casFeat_paradigmId;
  /** @generated */
  final int     casFeatCode_paradigmId;
  /** @generated */ 
  public long getParadigmId(int addr) {
        if (featOkTst && casFeat_paradigmId == null)
      jcas.throwFeatMissing("paradigmId", "ru.ksu.niimm.cll.uima.morph.seman.Paradigm");
    return ll_cas.ll_getLongValue(addr, casFeatCode_paradigmId);
  }
  /** @generated */    
  public void setParadigmId(int addr, long v) {
        if (featOkTst && casFeat_paradigmId == null)
      jcas.throwFeatMissing("paradigmId", "ru.ksu.niimm.cll.uima.morph.seman.Paradigm");
    ll_cas.ll_setLongValue(addr, casFeatCode_paradigmId, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Paradigm_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_pos = jcas.getRequiredFeatureDE(casType, "pos", "ru.ksu.niimm.cll.uima.morph.seman.PartOfSpeech", featOkTst);
    casFeatCode_pos  = (null == casFeat_pos) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_pos).getCode();

 
    casFeat_grammems = jcas.getRequiredFeatureDE(casType, "grammems", "uima.cas.StringArray", featOkTst);
    casFeatCode_grammems  = (null == casFeat_grammems) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_grammems).getCode();

 
    casFeat_lemma = jcas.getRequiredFeatureDE(casType, "lemma", "uima.cas.String", featOkTst);
    casFeatCode_lemma  = (null == casFeat_lemma) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_lemma).getCode();

 
    casFeat_paradigmId = jcas.getRequiredFeatureDE(casType, "paradigmId", "uima.cas.Long", featOkTst);
    casFeatCode_paradigmId  = (null == casFeat_paradigmId) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_paradigmId).getCode();

  }
}



    