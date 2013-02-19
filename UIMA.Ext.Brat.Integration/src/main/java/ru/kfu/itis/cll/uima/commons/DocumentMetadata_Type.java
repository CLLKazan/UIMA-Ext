
/* First created by JCasGen Wed Feb 06 17:09:31 MSK 2013 */
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
 * Updated by JCasGen Thu Feb 07 16:11:41 MSK 2013
 * @generated */
public class DocumentMetadata_Type extends Annotation_Type {
  /** @generated */
  @Override
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
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = DocumentMetadata.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
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
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public DocumentMetadata_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_sourceUri = jcas.getRequiredFeatureDE(casType, "sourceUri", "uima.cas.String", featOkTst);
    casFeatCode_sourceUri  = (null == casFeat_sourceUri) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_sourceUri).getCode();

  }
}



    