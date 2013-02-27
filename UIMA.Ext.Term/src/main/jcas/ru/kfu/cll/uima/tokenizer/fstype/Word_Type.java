
/* First created by JCasGen Wed Feb 27 15:40:38 SAMT 2013 */
package ru.kfu.cll.uima.tokenizer.fstype;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;

/** 
 * Updated by JCasGen Wed Feb 27 15:40:38 SAMT 2013
 * @generated */
public class Word_Type extends Token_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Word_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Word_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Word(addr, Word_Type.this);
  			   Word_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Word(addr, Word_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = Word.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ru.kfu.cll.uima.tokenizer.fstype.Word");
 
  /** @generated */
  final Feature casFeat_language;
  /** @generated */
  final int     casFeatCode_language;
  /** @generated */ 
  public String getLanguage(int addr) {
        if (featOkTst && casFeat_language == null)
      jcas.throwFeatMissing("language", "ru.kfu.cll.uima.tokenizer.fstype.Word");
    return ll_cas.ll_getStringValue(addr, casFeatCode_language);
  }
  /** @generated */    
  public void setLanguage(int addr, String v) {
        if (featOkTst && casFeat_language == null)
      jcas.throwFeatMissing("language", "ru.kfu.cll.uima.tokenizer.fstype.Word");
    ll_cas.ll_setStringValue(addr, casFeatCode_language, v);}
    
  
 
  /** @generated */
  final Feature casFeat_case;
  /** @generated */
  final int     casFeatCode_case;
  /** @generated */ 
  public String getCase(int addr) {
        if (featOkTst && casFeat_case == null)
      jcas.throwFeatMissing("case", "ru.kfu.cll.uima.tokenizer.fstype.Word");
    return ll_cas.ll_getStringValue(addr, casFeatCode_case);
  }
  /** @generated */    
  public void setCase(int addr, String v) {
        if (featOkTst && casFeat_case == null)
      jcas.throwFeatMissing("case", "ru.kfu.cll.uima.tokenizer.fstype.Word");
    ll_cas.ll_setStringValue(addr, casFeatCode_case, v);}
    
  
 
  /** @generated */
  final Feature casFeat_norm;
  /** @generated */
  final int     casFeatCode_norm;
  /** @generated */ 
  public String getNorm(int addr) {
        if (featOkTst && casFeat_norm == null)
      jcas.throwFeatMissing("norm", "ru.kfu.cll.uima.tokenizer.fstype.Word");
    return ll_cas.ll_getStringValue(addr, casFeatCode_norm);
  }
  /** @generated */    
  public void setNorm(int addr, String v) {
        if (featOkTst && casFeat_norm == null)
      jcas.throwFeatMissing("norm", "ru.kfu.cll.uima.tokenizer.fstype.Word");
    ll_cas.ll_setStringValue(addr, casFeatCode_norm, v);}
    
  
 
  /** @generated */
  final Feature casFeat_isCompound;
  /** @generated */
  final int     casFeatCode_isCompound;
  /** @generated */ 
  public String getIsCompound(int addr) {
        if (featOkTst && casFeat_isCompound == null)
      jcas.throwFeatMissing("isCompound", "ru.kfu.cll.uima.tokenizer.fstype.Word");
    return ll_cas.ll_getStringValue(addr, casFeatCode_isCompound);
  }
  /** @generated */    
  public void setIsCompound(int addr, String v) {
        if (featOkTst && casFeat_isCompound == null)
      jcas.throwFeatMissing("isCompound", "ru.kfu.cll.uima.tokenizer.fstype.Word");
    ll_cas.ll_setStringValue(addr, casFeatCode_isCompound, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Word_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_language = jcas.getRequiredFeatureDE(casType, "language", "uima.cas.String", featOkTst);
    casFeatCode_language  = (null == casFeat_language) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_language).getCode();

 
    casFeat_case = jcas.getRequiredFeatureDE(casType, "case", "ru.kfu.cll.uima.tokenizer.fstype.WordCaseType", featOkTst);
    casFeatCode_case  = (null == casFeat_case) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_case).getCode();

 
    casFeat_norm = jcas.getRequiredFeatureDE(casType, "norm", "uima.cas.String", featOkTst);
    casFeatCode_norm  = (null == casFeat_norm) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_norm).getCode();

 
    casFeat_isCompound = jcas.getRequiredFeatureDE(casType, "isCompound", "uima.cas.String", featOkTst);
    casFeatCode_isCompound  = (null == casFeat_isCompound) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_isCompound).getCode();

  }
}



    