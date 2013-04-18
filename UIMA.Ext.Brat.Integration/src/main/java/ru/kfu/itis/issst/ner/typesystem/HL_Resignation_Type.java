
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
public class HL_Resignation_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (HL_Resignation_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = HL_Resignation_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new HL_Resignation(addr, HL_Resignation_Type.this);
  			   HL_Resignation_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new HL_Resignation(addr, HL_Resignation_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = HL_Resignation.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ru.kfu.itis.issst.ner.typesystem.HL_Resignation");
 
  /** @generated */
  final Feature casFeat_person;
  /** @generated */
  final int     casFeatCode_person;
  /** @generated */ 
  public int getPerson(int addr) {
        if (featOkTst && casFeat_person == null)
      jcas.throwFeatMissing("person", "ru.kfu.itis.issst.ner.typesystem.HL_Resignation");
    return ll_cas.ll_getRefValue(addr, casFeatCode_person);
  }
  /** @generated */    
  public void setPerson(int addr, int v) {
        if (featOkTst && casFeat_person == null)
      jcas.throwFeatMissing("person", "ru.kfu.itis.issst.ner.typesystem.HL_Resignation");
    ll_cas.ll_setRefValue(addr, casFeatCode_person, v);}
    
  
 
  /** @generated */
  final Feature casFeat_position;
  /** @generated */
  final int     casFeatCode_position;
  /** @generated */ 
  public int getPosition(int addr) {
        if (featOkTst && casFeat_position == null)
      jcas.throwFeatMissing("position", "ru.kfu.itis.issst.ner.typesystem.HL_Resignation");
    return ll_cas.ll_getRefValue(addr, casFeatCode_position);
  }
  /** @generated */    
  public void setPosition(int addr, int v) {
        if (featOkTst && casFeat_position == null)
      jcas.throwFeatMissing("position", "ru.kfu.itis.issst.ner.typesystem.HL_Resignation");
    ll_cas.ll_setRefValue(addr, casFeatCode_position, v);}
    
  
 
  /** @generated */
  final Feature casFeat_company;
  /** @generated */
  final int     casFeatCode_company;
  /** @generated */ 
  public int getCompany(int addr) {
        if (featOkTst && casFeat_company == null)
      jcas.throwFeatMissing("company", "ru.kfu.itis.issst.ner.typesystem.HL_Resignation");
    return ll_cas.ll_getRefValue(addr, casFeatCode_company);
  }
  /** @generated */    
  public void setCompany(int addr, int v) {
        if (featOkTst && casFeat_company == null)
      jcas.throwFeatMissing("company", "ru.kfu.itis.issst.ner.typesystem.HL_Resignation");
    ll_cas.ll_setRefValue(addr, casFeatCode_company, v);}
    
  
 
  /** @generated */
  final Feature casFeat_approvedBy;
  /** @generated */
  final int     casFeatCode_approvedBy;
  /** @generated */ 
  public int getApprovedBy(int addr) {
        if (featOkTst && casFeat_approvedBy == null)
      jcas.throwFeatMissing("approvedBy", "ru.kfu.itis.issst.ner.typesystem.HL_Resignation");
    return ll_cas.ll_getRefValue(addr, casFeatCode_approvedBy);
  }
  /** @generated */    
  public void setApprovedBy(int addr, int v) {
        if (featOkTst && casFeat_approvedBy == null)
      jcas.throwFeatMissing("approvedBy", "ru.kfu.itis.issst.ner.typesystem.HL_Resignation");
    ll_cas.ll_setRefValue(addr, casFeatCode_approvedBy, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public HL_Resignation_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_person = jcas.getRequiredFeatureDE(casType, "person", "ru.kfu.itis.issst.ner.typesystem.Annotation", featOkTst);
    casFeatCode_person  = (null == casFeat_person) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_person).getCode();

 
    casFeat_position = jcas.getRequiredFeatureDE(casType, "position", "ru.kfu.itis.issst.ner.typesystem.Annotation", featOkTst);
    casFeatCode_position  = (null == casFeat_position) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_position).getCode();

 
    casFeat_company = jcas.getRequiredFeatureDE(casType, "company", "ru.kfu.itis.issst.ner.typesystem.Annotation", featOkTst);
    casFeatCode_company  = (null == casFeat_company) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_company).getCode();

 
    casFeat_approvedBy = jcas.getRequiredFeatureDE(casType, "approvedBy", "ru.kfu.itis.issst.ner.typesystem.Annotation", featOkTst);
    casFeatCode_approvedBy  = (null == casFeat_approvedBy) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_approvedBy).getCode();

  }
}



    