
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
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("com.hp.hplabs.lim2.ie.text.typesystem.NamedEntity");
 
  /** @generated */
  final Feature casFeat_namedEntityType;
  /** @generated */
  final int     casFeatCode_namedEntityType;
  /** @generated */ 
  public String getNamedEntityType(int addr) {
        if (featOkTst && casFeat_namedEntityType == null)
      jcas.throwFeatMissing("namedEntityType", "com.hp.hplabs.lim2.ie.text.typesystem.NamedEntity");
    return ll_cas.ll_getStringValue(addr, casFeatCode_namedEntityType);
  }
  /** @generated */    
  public void setNamedEntityType(int addr, String v) {
        if (featOkTst && casFeat_namedEntityType == null)
      jcas.throwFeatMissing("namedEntityType", "com.hp.hplabs.lim2.ie.text.typesystem.NamedEntity");
    ll_cas.ll_setStringValue(addr, casFeatCode_namedEntityType, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public NamedEntity_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_namedEntityType = jcas.getRequiredFeatureDE(casType, "namedEntityType", "uima.cas.String", featOkTst);
    casFeatCode_namedEntityType  = (null == casFeat_namedEntityType) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_namedEntityType).getCode();

  }
}



    