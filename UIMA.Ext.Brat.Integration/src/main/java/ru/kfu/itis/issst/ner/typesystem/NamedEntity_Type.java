
/* First created by JCasGen Tue Mar 12 00:30:02 MSK 2013 */
package ru.kfu.itis.issst.ner.typesystem;

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
 * Updated by JCasGen Tue Mar 12 00:30:02 MSK 2013
 * @generated */
public class NamedEntity_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (NamedEntity_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = NamedEntity_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new NamedEntity(addr, NamedEntity_Type.this);
  			   NamedEntity_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new NamedEntity(addr, NamedEntity_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = NamedEntity.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ru.kfu.itis.issst.ner.typesystem.NamedEntity");
 
  /** @generated */
  final Feature casFeat_namedEntityType;
  /** @generated */
  final int     casFeatCode_namedEntityType;
  /** @generated */ 
  public String getNamedEntityType(int addr) {
        if (featOkTst && casFeat_namedEntityType == null)
      jcas.throwFeatMissing("namedEntityType", "ru.kfu.itis.issst.ner.typesystem.NamedEntity");
    return ll_cas.ll_getStringValue(addr, casFeatCode_namedEntityType);
  }
  /** @generated */    
  public void setNamedEntityType(int addr, String v) {
        if (featOkTst && casFeat_namedEntityType == null)
      jcas.throwFeatMissing("namedEntityType", "ru.kfu.itis.issst.ner.typesystem.NamedEntity");
    ll_cas.ll_setStringValue(addr, casFeatCode_namedEntityType, v);}
    
  
 
  /** @generated */
  final Feature casFeat_canonical;
  /** @generated */
  final int     casFeatCode_canonical;
  /** @generated */ 
  public String getCanonical(int addr) {
        if (featOkTst && casFeat_canonical == null)
      jcas.throwFeatMissing("canonical", "ru.kfu.itis.issst.ner.typesystem.NamedEntity");
    return ll_cas.ll_getStringValue(addr, casFeatCode_canonical);
  }
  /** @generated */    
  public void setCanonical(int addr, String v) {
        if (featOkTst && casFeat_canonical == null)
      jcas.throwFeatMissing("canonical", "ru.kfu.itis.issst.ner.typesystem.NamedEntity");
    ll_cas.ll_setStringValue(addr, casFeatCode_canonical, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public NamedEntity_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_namedEntityType = jcas.getRequiredFeatureDE(casType, "namedEntityType", "uima.cas.String", featOkTst);
    casFeatCode_namedEntityType  = (null == casFeat_namedEntityType) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_namedEntityType).getCode();

 
    casFeat_canonical = jcas.getRequiredFeatureDE(casType, "canonical", "uima.cas.String", featOkTst);
    casFeatCode_canonical  = (null == casFeat_canonical) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_canonical).getCode();

  }
}



    