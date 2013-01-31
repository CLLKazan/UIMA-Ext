
/* First created by JCasGen Sat Jan 19 17:59:32 MSK 2013 */
package ru.kfu.itis.cll.uima.commons;

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
 * Updated by JCasGen Sat Jan 19 17:59:32 MSK 2013
 * @generated */
public class MisisDocumentMetadata_Type extends DocumentMetadata_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (MisisDocumentMetadata_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = MisisDocumentMetadata_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new MisisDocumentMetadata(addr, MisisDocumentMetadata_Type.this);
  			   MisisDocumentMetadata_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new MisisDocumentMetadata(addr, MisisDocumentMetadata_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = MisisDocumentMetadata.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ru.kfu.itis.cll.uima.commons.MisisDocumentMetadata");
 
  /** @generated */
  final Feature casFeat_language;
  /** @generated */
  final int     casFeatCode_language;
  /** @generated */ 
  public String getLanguage(int addr) {
        if (featOkTst && casFeat_language == null)
      jcas.throwFeatMissing("language", "ru.kfu.itis.cll.uima.commons.MisisDocumentMetadata");
    return ll_cas.ll_getStringValue(addr, casFeatCode_language);
  }
  /** @generated */    
  public void setLanguage(int addr, String v) {
        if (featOkTst && casFeat_language == null)
      jcas.throwFeatMissing("language", "ru.kfu.itis.cll.uima.commons.MisisDocumentMetadata");
    ll_cas.ll_setStringValue(addr, casFeatCode_language, v);}
    
  
 
  /** @generated */
  final Feature casFeat_format;
  /** @generated */
  final int     casFeatCode_format;
  /** @generated */ 
  public String getFormat(int addr) {
        if (featOkTst && casFeat_format == null)
      jcas.throwFeatMissing("format", "ru.kfu.itis.cll.uima.commons.MisisDocumentMetadata");
    return ll_cas.ll_getStringValue(addr, casFeatCode_format);
  }
  /** @generated */    
  public void setFormat(int addr, String v) {
        if (featOkTst && casFeat_format == null)
      jcas.throwFeatMissing("format", "ru.kfu.itis.cll.uima.commons.MisisDocumentMetadata");
    ll_cas.ll_setStringValue(addr, casFeatCode_format, v);}
    
  
 
  /** @generated */
  final Feature casFeat_documentRawText;
  /** @generated */
  final int     casFeatCode_documentRawText;
  /** @generated */ 
  public String getDocumentRawText(int addr) {
        if (featOkTst && casFeat_documentRawText == null)
      jcas.throwFeatMissing("documentRawText", "ru.kfu.itis.cll.uima.commons.MisisDocumentMetadata");
    return ll_cas.ll_getStringValue(addr, casFeatCode_documentRawText);
  }
  /** @generated */    
  public void setDocumentRawText(int addr, String v) {
        if (featOkTst && casFeat_documentRawText == null)
      jcas.throwFeatMissing("documentRawText", "ru.kfu.itis.cll.uima.commons.MisisDocumentMetadata");
    ll_cas.ll_setStringValue(addr, casFeatCode_documentRawText, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public MisisDocumentMetadata_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_language = jcas.getRequiredFeatureDE(casType, "language", "uima.cas.String", featOkTst);
    casFeatCode_language  = (null == casFeat_language) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_language).getCode();

 
    casFeat_format = jcas.getRequiredFeatureDE(casType, "format", "uima.cas.String", featOkTst);
    casFeatCode_format  = (null == casFeat_format) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_format).getCode();

 
    casFeat_documentRawText = jcas.getRequiredFeatureDE(casType, "documentRawText", "uima.cas.String", featOkTst);
    casFeatCode_documentRawText  = (null == casFeat_documentRawText) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_documentRawText).getCode();

  }
}



    