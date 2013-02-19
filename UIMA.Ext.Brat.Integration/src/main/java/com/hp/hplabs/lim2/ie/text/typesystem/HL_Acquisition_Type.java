
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
 * Updated by JCasGen Tue Feb 05 18:06:05 MSK 2013
 * @generated */
public class HL_Acquisition_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (HL_Acquisition_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = HL_Acquisition_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new HL_Acquisition(addr, HL_Acquisition_Type.this);
  			   HL_Acquisition_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new HL_Acquisition(addr, HL_Acquisition_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = HL_Acquisition.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("com.hp.hplabs.lim2.ie.text.typesystem.HL_Acquisition");
 
  /** @generated */
  final Feature casFeat_slot1;
  /** @generated */
  final int     casFeatCode_slot1;
  /** @generated */ 
  public int getSlot1(int addr) {
        if (featOkTst && casFeat_slot1 == null)
      jcas.throwFeatMissing("slot1", "com.hp.hplabs.lim2.ie.text.typesystem.HL_Acquisition");
    return ll_cas.ll_getRefValue(addr, casFeatCode_slot1);
  }
  /** @generated */    
  public void setSlot1(int addr, int v) {
        if (featOkTst && casFeat_slot1 == null)
      jcas.throwFeatMissing("slot1", "com.hp.hplabs.lim2.ie.text.typesystem.HL_Acquisition");
    ll_cas.ll_setRefValue(addr, casFeatCode_slot1, v);}
    
  
 
  /** @generated */
  final Feature casFeat_slot2;
  /** @generated */
  final int     casFeatCode_slot2;
  /** @generated */ 
  public int getSlot2(int addr) {
        if (featOkTst && casFeat_slot2 == null)
      jcas.throwFeatMissing("slot2", "com.hp.hplabs.lim2.ie.text.typesystem.HL_Acquisition");
    return ll_cas.ll_getRefValue(addr, casFeatCode_slot2);
  }
  /** @generated */    
  public void setSlot2(int addr, int v) {
        if (featOkTst && casFeat_slot2 == null)
      jcas.throwFeatMissing("slot2", "com.hp.hplabs.lim2.ie.text.typesystem.HL_Acquisition");
    ll_cas.ll_setRefValue(addr, casFeatCode_slot2, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public HL_Acquisition_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_slot1 = jcas.getRequiredFeatureDE(casType, "slot1", "com.hp.hplabs.lim2.ie.text.typesystem.Annotation", featOkTst);
    casFeatCode_slot1  = (null == casFeat_slot1) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_slot1).getCode();

 
    casFeat_slot2 = jcas.getRequiredFeatureDE(casType, "slot2", "com.hp.hplabs.lim2.ie.text.typesystem.Annotation", featOkTst);
    casFeatCode_slot2  = (null == casFeat_slot2) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_slot2).getCode();

  }
}



    