
/* First created by JCasGen Tue Feb 05 17:20:29 MSK 2013 */
package com.hp.hplabs.lim2.ie.text.typesystem;

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
 * Updated by JCasGen Tue Feb 05 18:06:06 MSK 2013
 * @generated */
public class Token_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Token_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Token_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Token(addr, Token_Type.this);
  			   Token_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Token(addr, Token_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Token.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("com.hp.hplabs.lim2.ie.text.typesystem.Token");
 
  /** @generated */
  final Feature casFeat_posTag;
  /** @generated */
  final int     casFeatCode_posTag;
  /** @generated */ 
  public String getPosTag(int addr) {
        if (featOkTst && casFeat_posTag == null)
      jcas.throwFeatMissing("posTag", "com.hp.hplabs.lim2.ie.text.typesystem.Token");
    return ll_cas.ll_getStringValue(addr, casFeatCode_posTag);
  }
  /** @generated */    
  public void setPosTag(int addr, String v) {
        if (featOkTst && casFeat_posTag == null)
      jcas.throwFeatMissing("posTag", "com.hp.hplabs.lim2.ie.text.typesystem.Token");
    ll_cas.ll_setStringValue(addr, casFeatCode_posTag, v);}
    
  
 
  /** @generated */
  final Feature casFeat_SemClass;
  /** @generated */
  final int     casFeatCode_SemClass;
  /** @generated */ 
  public String getSemClass(int addr) {
        if (featOkTst && casFeat_SemClass == null)
      jcas.throwFeatMissing("SemClass", "com.hp.hplabs.lim2.ie.text.typesystem.Token");
    return ll_cas.ll_getStringValue(addr, casFeatCode_SemClass);
  }
  /** @generated */    
  public void setSemClass(int addr, String v) {
        if (featOkTst && casFeat_SemClass == null)
      jcas.throwFeatMissing("SemClass", "com.hp.hplabs.lim2.ie.text.typesystem.Token");
    ll_cas.ll_setStringValue(addr, casFeatCode_SemClass, v);}
    
  
 
  /** @generated */
  final Feature casFeat_POS;
  /** @generated */
  final int     casFeatCode_POS;
  /** @generated */ 
  public String getPOS(int addr) {
        if (featOkTst && casFeat_POS == null)
      jcas.throwFeatMissing("POS", "com.hp.hplabs.lim2.ie.text.typesystem.Token");
    return ll_cas.ll_getStringValue(addr, casFeatCode_POS);
  }
  /** @generated */    
  public void setPOS(int addr, String v) {
        if (featOkTst && casFeat_POS == null)
      jcas.throwFeatMissing("POS", "com.hp.hplabs.lim2.ie.text.typesystem.Token");
    ll_cas.ll_setStringValue(addr, casFeatCode_POS, v);}
    
  
 
  /** @generated */
  final Feature casFeat_frost_TokenType;
  /** @generated */
  final int     casFeatCode_frost_TokenType;
  /** @generated */ 
  public int getFrost_TokenType(int addr) {
        if (featOkTst && casFeat_frost_TokenType == null)
      jcas.throwFeatMissing("frost_TokenType", "com.hp.hplabs.lim2.ie.text.typesystem.Token");
    return ll_cas.ll_getIntValue(addr, casFeatCode_frost_TokenType);
  }
  /** @generated */    
  public void setFrost_TokenType(int addr, int v) {
        if (featOkTst && casFeat_frost_TokenType == null)
      jcas.throwFeatMissing("frost_TokenType", "com.hp.hplabs.lim2.ie.text.typesystem.Token");
    ll_cas.ll_setIntValue(addr, casFeatCode_frost_TokenType, v);}
    
  
 
  /** @generated */
  final Feature casFeat_text;
  /** @generated */
  final int     casFeatCode_text;
  /** @generated */ 
  public String getText(int addr) {
        if (featOkTst && casFeat_text == null)
      jcas.throwFeatMissing("text", "com.hp.hplabs.lim2.ie.text.typesystem.Token");
    return ll_cas.ll_getStringValue(addr, casFeatCode_text);
  }
  /** @generated */    
  public void setText(int addr, String v) {
        if (featOkTst && casFeat_text == null)
      jcas.throwFeatMissing("text", "com.hp.hplabs.lim2.ie.text.typesystem.Token");
    ll_cas.ll_setStringValue(addr, casFeatCode_text, v);}
    
  
 
  /** @generated */
  final Feature casFeat_stemForm;
  /** @generated */
  final int     casFeatCode_stemForm;
  /** @generated */ 
  public String getStemForm(int addr) {
        if (featOkTst && casFeat_stemForm == null)
      jcas.throwFeatMissing("stemForm", "com.hp.hplabs.lim2.ie.text.typesystem.Token");
    return ll_cas.ll_getStringValue(addr, casFeatCode_stemForm);
  }
  /** @generated */    
  public void setStemForm(int addr, String v) {
        if (featOkTst && casFeat_stemForm == null)
      jcas.throwFeatMissing("stemForm", "com.hp.hplabs.lim2.ie.text.typesystem.Token");
    ll_cas.ll_setStringValue(addr, casFeatCode_stemForm, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Token_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_posTag = jcas.getRequiredFeatureDE(casType, "posTag", "uima.cas.String", featOkTst);
    casFeatCode_posTag  = (null == casFeat_posTag) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_posTag).getCode();

 
    casFeat_SemClass = jcas.getRequiredFeatureDE(casType, "SemClass", "uima.cas.String", featOkTst);
    casFeatCode_SemClass  = (null == casFeat_SemClass) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_SemClass).getCode();

 
    casFeat_POS = jcas.getRequiredFeatureDE(casType, "POS", "uima.cas.String", featOkTst);
    casFeatCode_POS  = (null == casFeat_POS) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_POS).getCode();

 
    casFeat_frost_TokenType = jcas.getRequiredFeatureDE(casType, "frost_TokenType", "uima.cas.Integer", featOkTst);
    casFeatCode_frost_TokenType  = (null == casFeat_frost_TokenType) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_frost_TokenType).getCode();

 
    casFeat_text = jcas.getRequiredFeatureDE(casType, "text", "uima.cas.String", featOkTst);
    casFeatCode_text  = (null == casFeat_text) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_text).getCode();

 
    casFeat_stemForm = jcas.getRequiredFeatureDE(casType, "stemForm", "uima.cas.String", featOkTst);
    casFeatCode_stemForm  = (null == casFeat_stemForm) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_stemForm).getCode();

  }
}



    