
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
public class HL_PersonAnnouncement_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (HL_PersonAnnouncement_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = HL_PersonAnnouncement_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new HL_PersonAnnouncement(addr, HL_PersonAnnouncement_Type.this);
  			   HL_PersonAnnouncement_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new HL_PersonAnnouncement(addr, HL_PersonAnnouncement_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = HL_PersonAnnouncement.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ru.kfu.itis.issst.ner.typesystem.HL_PersonAnnouncement");
 
  /** @generated */
  final Feature casFeat_person;
  /** @generated */
  final int     casFeatCode_person;
  /** @generated */ 
  public int getPerson(int addr) {
        if (featOkTst && casFeat_person == null)
      jcas.throwFeatMissing("person", "ru.kfu.itis.issst.ner.typesystem.HL_PersonAnnouncement");
    return ll_cas.ll_getRefValue(addr, casFeatCode_person);
  }
  /** @generated */    
  public void setPerson(int addr, int v) {
        if (featOkTst && casFeat_person == null)
      jcas.throwFeatMissing("person", "ru.kfu.itis.issst.ner.typesystem.HL_PersonAnnouncement");
    ll_cas.ll_setRefValue(addr, casFeatCode_person, v);}
    
  
 
  /** @generated */
  final Feature casFeat_date;
  /** @generated */
  final int     casFeatCode_date;
  /** @generated */ 
  public int getDate(int addr) {
        if (featOkTst && casFeat_date == null)
      jcas.throwFeatMissing("date", "ru.kfu.itis.issst.ner.typesystem.HL_PersonAnnouncement");
    return ll_cas.ll_getRefValue(addr, casFeatCode_date);
  }
  /** @generated */    
  public void setDate(int addr, int v) {
        if (featOkTst && casFeat_date == null)
      jcas.throwFeatMissing("date", "ru.kfu.itis.issst.ner.typesystem.HL_PersonAnnouncement");
    ll_cas.ll_setRefValue(addr, casFeatCode_date, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public HL_PersonAnnouncement_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_person = jcas.getRequiredFeatureDE(casType, "person", "ru.kfu.itis.issst.ner.typesystem.HL_Person", featOkTst);
    casFeatCode_person  = (null == casFeat_person) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_person).getCode();

 
    casFeat_date = jcas.getRequiredFeatureDE(casType, "date", "ru.kfu.itis.issst.ner.typesystem.temporal.TE", featOkTst);
    casFeatCode_date  = (null == casFeat_date) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_date).getCode();

  }
}



    