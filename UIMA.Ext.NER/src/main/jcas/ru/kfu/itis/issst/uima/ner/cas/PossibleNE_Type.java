
/* First created by JCasGen Sun Mar 03 18:46:12 MSK 2013 */
package ru.kfu.itis.issst.uima.ner.cas;

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

/** Possible Named Entity, represents boundaries of a name
 * Updated by JCasGen Sun Mar 03 18:46:12 MSK 2013
 * @generated */
public class PossibleNE_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (PossibleNE_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = PossibleNE_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new PossibleNE(addr, PossibleNE_Type.this);
  			   PossibleNE_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new PossibleNE(addr, PossibleNE_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = PossibleNE.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ru.kfu.itis.issst.uima.ner.cas.PossibleNE");
 
  /** @generated */
  final Feature casFeat_words;
  /** @generated */
  final int     casFeatCode_words;
  /** @generated */ 
  public int getWords(int addr) {
        if (featOkTst && casFeat_words == null)
      jcas.throwFeatMissing("words", "ru.kfu.itis.issst.uima.ner.cas.PossibleNE");
    return ll_cas.ll_getRefValue(addr, casFeatCode_words);
  }
  /** @generated */    
  public void setWords(int addr, int v) {
        if (featOkTst && casFeat_words == null)
      jcas.throwFeatMissing("words", "ru.kfu.itis.issst.uima.ner.cas.PossibleNE");
    ll_cas.ll_setRefValue(addr, casFeatCode_words, v);}
    
   /** @generated */
  public int getWords(int addr, int i) {
        if (featOkTst && casFeat_words == null)
      jcas.throwFeatMissing("words", "ru.kfu.itis.issst.uima.ner.cas.PossibleNE");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_words), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_words), i);
	return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_words), i);
  }
   
  /** @generated */ 
  public void setWords(int addr, int i, int v) {
        if (featOkTst && casFeat_words == null)
      jcas.throwFeatMissing("words", "ru.kfu.itis.issst.uima.ner.cas.PossibleNE");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_words), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_words), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_words), i, v);
  }
 
 
  /** @generated */
  final Feature casFeat_head;
  /** @generated */
  final int     casFeatCode_head;
  /** @generated */ 
  public int getHead(int addr) {
        if (featOkTst && casFeat_head == null)
      jcas.throwFeatMissing("head", "ru.kfu.itis.issst.uima.ner.cas.PossibleNE");
    return ll_cas.ll_getRefValue(addr, casFeatCode_head);
  }
  /** @generated */    
  public void setHead(int addr, int v) {
        if (featOkTst && casFeat_head == null)
      jcas.throwFeatMissing("head", "ru.kfu.itis.issst.uima.ner.cas.PossibleNE");
    ll_cas.ll_setRefValue(addr, casFeatCode_head, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public PossibleNE_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_words = jcas.getRequiredFeatureDE(casType, "words", "uima.cas.FSArray", featOkTst);
    casFeatCode_words  = (null == casFeat_words) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_words).getCode();

 
    casFeat_head = jcas.getRequiredFeatureDE(casType, "head", "org.opencorpora.cas.Word", featOkTst);
    casFeatCode_head  = (null == casFeat_head) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_head).getCode();

  }
}



    