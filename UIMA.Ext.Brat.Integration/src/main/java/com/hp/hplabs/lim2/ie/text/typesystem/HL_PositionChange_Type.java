
/* First created by JCasGen Tue Feb 05 17:20:42 MSK 2013 */
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
public class HL_PositionChange_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (HL_PositionChange_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = HL_PositionChange_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new HL_PositionChange(addr, HL_PositionChange_Type.this);
  			   HL_PositionChange_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new HL_PositionChange(addr, HL_PositionChange_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = HL_PositionChange.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("com.hp.hplabs.lim2.ie.text.typesystem.HL_PositionChange");
 
  /** @generated */
  final Feature casFeat_person;
  /** @generated */
  final int     casFeatCode_person;
  /** @generated */ 
  public int getPerson(int addr) {
        if (featOkTst && casFeat_person == null)
      jcas.throwFeatMissing("person", "com.hp.hplabs.lim2.ie.text.typesystem.HL_PositionChange");
    return ll_cas.ll_getRefValue(addr, casFeatCode_person);
  }
  /** @generated */    
  public void setPerson(int addr, int v) {
        if (featOkTst && casFeat_person == null)
      jcas.throwFeatMissing("person", "com.hp.hplabs.lim2.ie.text.typesystem.HL_PositionChange");
    ll_cas.ll_setRefValue(addr, casFeatCode_person, v);}
    
  
 
  /** @generated */
  final Feature casFeat_newPosition;
  /** @generated */
  final int     casFeatCode_newPosition;
  /** @generated */ 
  public int getNewPosition(int addr) {
        if (featOkTst && casFeat_newPosition == null)
      jcas.throwFeatMissing("newPosition", "com.hp.hplabs.lim2.ie.text.typesystem.HL_PositionChange");
    return ll_cas.ll_getRefValue(addr, casFeatCode_newPosition);
  }
  /** @generated */    
  public void setNewPosition(int addr, int v) {
        if (featOkTst && casFeat_newPosition == null)
      jcas.throwFeatMissing("newPosition", "com.hp.hplabs.lim2.ie.text.typesystem.HL_PositionChange");
    ll_cas.ll_setRefValue(addr, casFeatCode_newPosition, v);}
    
  
 
  /** @generated */
  final Feature casFeat_oldPosition;
  /** @generated */
  final int     casFeatCode_oldPosition;
  /** @generated */ 
  public int getOldPosition(int addr) {
        if (featOkTst && casFeat_oldPosition == null)
      jcas.throwFeatMissing("oldPosition", "com.hp.hplabs.lim2.ie.text.typesystem.HL_PositionChange");
    return ll_cas.ll_getRefValue(addr, casFeatCode_oldPosition);
  }
  /** @generated */    
  public void setOldPosition(int addr, int v) {
        if (featOkTst && casFeat_oldPosition == null)
      jcas.throwFeatMissing("oldPosition", "com.hp.hplabs.lim2.ie.text.typesystem.HL_PositionChange");
    ll_cas.ll_setRefValue(addr, casFeatCode_oldPosition, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public HL_PositionChange_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_person = jcas.getRequiredFeatureDE(casType, "person", "com.hp.hplabs.lim2.ie.text.typesystem.Annotation", featOkTst);
    casFeatCode_person  = (null == casFeat_person) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_person).getCode();

 
    casFeat_newPosition = jcas.getRequiredFeatureDE(casType, "newPosition", "com.hp.hplabs.lim2.ie.text.typesystem.Annotation", featOkTst);
    casFeatCode_newPosition  = (null == casFeat_newPosition) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_newPosition).getCode();

 
    casFeat_oldPosition = jcas.getRequiredFeatureDE(casType, "oldPosition", "com.hp.hplabs.lim2.ie.text.typesystem.Annotation", featOkTst);
    casFeatCode_oldPosition  = (null == casFeat_oldPosition) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_oldPosition).getCode();

  }
}



    