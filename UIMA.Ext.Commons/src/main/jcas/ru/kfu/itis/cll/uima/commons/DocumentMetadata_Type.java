
/* First created by JCasGen Fri Jul 06 01:31:15 MSK 2012 */
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
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Fri Jul 06 01:31:15 MSK 2012
 * @generated */
public class DocumentMetadata_Type extends Annotation_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (DocumentMetadata_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = DocumentMetadata_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new DocumentMetadata(addr, DocumentMetadata_Type.this);
  			   DocumentMetadata_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new DocumentMetadata(addr, DocumentMetadata_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = DocumentMetadata.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ru.kfu.itis.cll.uima.commons.DocumentMetadata");
 
  /** @generated */
  final Feature casFeat_sourceUri;
  /** @generated */
  final int     casFeatCode_sourceUri;
  /** @generated */ 
  public String getSourceUri(int addr) {
        if (featOkTst && casFeat_sourceUri == null)
      jcas.throwFeatMissing("sourceUri", "ru.kfu.itis.cll.uima.commons.DocumentMetadata");
    return ll_cas.ll_getStringValue(addr, casFeatCode_sourceUri);
  }
  /** @generated */    
  public void setSourceUri(int addr, String v) {
        if (featOkTst && casFeat_sourceUri == null)
      jcas.throwFeatMissing("sourceUri", "ru.kfu.itis.cll.uima.commons.DocumentMetadata");
    ll_cas.ll_setStringValue(addr, casFeatCode_sourceUri, v);}
    
  
 
  /** @generated */
  final Feature casFeat_offsetInSource;
  /** @generated */
  final int     casFeatCode_offsetInSource;
  /** @generated */ 
  public long getOffsetInSource(int addr) {
        if (featOkTst && casFeat_offsetInSource == null)
      jcas.throwFeatMissing("offsetInSource", "ru.kfu.itis.cll.uima.commons.DocumentMetadata");
    return ll_cas.ll_getLongValue(addr, casFeatCode_offsetInSource);
  }
  /** @generated */    
  public void setOffsetInSource(int addr, long v) {
        if (featOkTst && casFeat_offsetInSource == null)
      jcas.throwFeatMissing("offsetInSource", "ru.kfu.itis.cll.uima.commons.DocumentMetadata");
    ll_cas.ll_setLongValue(addr, casFeatCode_offsetInSource, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public DocumentMetadata_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_sourceUri = jcas.getRequiredFeatureDE(casType, "sourceUri", "uima.cas.String", featOkTst);
    casFeatCode_sourceUri  = (null == casFeat_sourceUri) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_sourceUri).getCode();

 
    casFeat_offsetInSource = jcas.getRequiredFeatureDE(casType, "offsetInSource", "uima.cas.Long", featOkTst);
    casFeatCode_offsetInSource  = (null == casFeat_offsetInSource) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_offsetInSource).getCode();

  }
}



    