
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
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ru.kfu.itis.issst.ner.typesystem.HL_PositionChange");
 
  /** @generated */
  final Feature casFeat_person;
  /** @generated */
  final int     casFeatCode_person;
  /** @generated */ 
  public int getPerson(int addr) {
        if (featOkTst && casFeat_person == null)
      jcas.throwFeatMissing("person", "ru.kfu.itis.issst.ner.typesystem.HL_PositionChange");
    return ll_cas.ll_getRefValue(addr, casFeatCode_person);
  }
  /** @generated */    
  public void setPerson(int addr, int v) {
        if (featOkTst && casFeat_person == null)
      jcas.throwFeatMissing("person", "ru.kfu.itis.issst.ner.typesystem.HL_PositionChange");
    ll_cas.ll_setRefValue(addr, casFeatCode_person, v);}
    
  
 
  /** @generated */
  final Feature casFeat_newPosition;
  /** @generated */
  final int     casFeatCode_newPosition;
  /** @generated */ 
  public int getNewPosition(int addr) {
        if (featOkTst && casFeat_newPosition == null)
      jcas.throwFeatMissing("newPosition", "ru.kfu.itis.issst.ner.typesystem.HL_PositionChange");
    return ll_cas.ll_getRefValue(addr, casFeatCode_newPosition);
  }
  /** @generated */    
  public void setNewPosition(int addr, int v) {
        if (featOkTst && casFeat_newPosition == null)
      jcas.throwFeatMissing("newPosition", "ru.kfu.itis.issst.ner.typesystem.HL_PositionChange");
    ll_cas.ll_setRefValue(addr, casFeatCode_newPosition, v);}
    
  
 
  /** @generated */
  final Feature casFeat_oldPosition;
  /** @generated */
  final int     casFeatCode_oldPosition;
  /** @generated */ 
  public int getOldPosition(int addr) {
        if (featOkTst && casFeat_oldPosition == null)
      jcas.throwFeatMissing("oldPosition", "ru.kfu.itis.issst.ner.typesystem.HL_PositionChange");
    return ll_cas.ll_getRefValue(addr, casFeatCode_oldPosition);
  }
  /** @generated */    
  public void setOldPosition(int addr, int v) {
        if (featOkTst && casFeat_oldPosition == null)
      jcas.throwFeatMissing("oldPosition", "ru.kfu.itis.issst.ner.typesystem.HL_PositionChange");
    ll_cas.ll_setRefValue(addr, casFeatCode_oldPosition, v);}
    
  
 
  /** @generated */
  final Feature casFeat_newCompany;
  /** @generated */
  final int     casFeatCode_newCompany;
  /** @generated */ 
  public int getNewCompany(int addr) {
        if (featOkTst && casFeat_newCompany == null)
      jcas.throwFeatMissing("newCompany", "ru.kfu.itis.issst.ner.typesystem.HL_PositionChange");
    return ll_cas.ll_getRefValue(addr, casFeatCode_newCompany);
  }
  /** @generated */    
  public void setNewCompany(int addr, int v) {
        if (featOkTst && casFeat_newCompany == null)
      jcas.throwFeatMissing("newCompany", "ru.kfu.itis.issst.ner.typesystem.HL_PositionChange");
    ll_cas.ll_setRefValue(addr, casFeatCode_newCompany, v);}
    
  
 
  /** @generated */
  final Feature casFeat_oldCompany;
  /** @generated */
  final int     casFeatCode_oldCompany;
  /** @generated */ 
  public int getOldCompany(int addr) {
        if (featOkTst && casFeat_oldCompany == null)
      jcas.throwFeatMissing("oldCompany", "ru.kfu.itis.issst.ner.typesystem.HL_PositionChange");
    return ll_cas.ll_getRefValue(addr, casFeatCode_oldCompany);
  }
  /** @generated */    
  public void setOldCompany(int addr, int v) {
        if (featOkTst && casFeat_oldCompany == null)
      jcas.throwFeatMissing("oldCompany", "ru.kfu.itis.issst.ner.typesystem.HL_PositionChange");
    ll_cas.ll_setRefValue(addr, casFeatCode_oldCompany, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public HL_PositionChange_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_person = jcas.getRequiredFeatureDE(casType, "person", "ru.kfu.itis.issst.ner.typesystem.Annotation", featOkTst);
    casFeatCode_person  = (null == casFeat_person) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_person).getCode();

 
    casFeat_newPosition = jcas.getRequiredFeatureDE(casType, "newPosition", "ru.kfu.itis.issst.ner.typesystem.Annotation", featOkTst);
    casFeatCode_newPosition  = (null == casFeat_newPosition) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_newPosition).getCode();

 
    casFeat_oldPosition = jcas.getRequiredFeatureDE(casType, "oldPosition", "ru.kfu.itis.issst.ner.typesystem.Annotation", featOkTst);
    casFeatCode_oldPosition  = (null == casFeat_oldPosition) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_oldPosition).getCode();

 
    casFeat_newCompany = jcas.getRequiredFeatureDE(casType, "newCompany", "ru.kfu.itis.issst.ner.typesystem.Annotation", featOkTst);
    casFeatCode_newCompany  = (null == casFeat_newCompany) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_newCompany).getCode();

 
    casFeat_oldCompany = jcas.getRequiredFeatureDE(casType, "oldCompany", "ru.kfu.itis.issst.ner.typesystem.Annotation", featOkTst);
    casFeatCode_oldCompany  = (null == casFeat_oldCompany) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_oldCompany).getCode();

  }
}



    