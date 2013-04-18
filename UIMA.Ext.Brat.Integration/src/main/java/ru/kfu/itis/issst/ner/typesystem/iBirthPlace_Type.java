
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
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Tue Mar 12 00:30:02 MSK 2013
 * @generated */
public class iBirthPlace_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (iBirthPlace_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = iBirthPlace_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new iBirthPlace(addr, iBirthPlace_Type.this);
  			   iBirthPlace_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new iBirthPlace(addr, iBirthPlace_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = iBirthPlace.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ru.kfu.itis.issst.ner.typesystem.iBirthPlace");
 
  /** @generated */
  final Feature casFeat_person;
  /** @generated */
  final int     casFeatCode_person;
  /** @generated */ 
  public int getPerson(int addr) {
        if (featOkTst && casFeat_person == null)
      jcas.throwFeatMissing("person", "ru.kfu.itis.issst.ner.typesystem.iBirthPlace");
    return ll_cas.ll_getRefValue(addr, casFeatCode_person);
  }
  /** @generated */    
  public void setPerson(int addr, int v) {
        if (featOkTst && casFeat_person == null)
      jcas.throwFeatMissing("person", "ru.kfu.itis.issst.ner.typesystem.iBirthPlace");
    ll_cas.ll_setRefValue(addr, casFeatCode_person, v);}
    
  
 
  /** @generated */
  final Feature casFeat_city;
  /** @generated */
  final int     casFeatCode_city;
  /** @generated */ 
  public int getCity(int addr) {
        if (featOkTst && casFeat_city == null)
      jcas.throwFeatMissing("city", "ru.kfu.itis.issst.ner.typesystem.iBirthPlace");
    return ll_cas.ll_getRefValue(addr, casFeatCode_city);
  }
  /** @generated */    
  public void setCity(int addr, int v) {
        if (featOkTst && casFeat_city == null)
      jcas.throwFeatMissing("city", "ru.kfu.itis.issst.ner.typesystem.iBirthPlace");
    ll_cas.ll_setRefValue(addr, casFeatCode_city, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public iBirthPlace_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_person = jcas.getRequiredFeatureDE(casType, "person", "ru.kfu.itis.issst.ner.typesystem.iPerson", featOkTst);
    casFeatCode_person  = (null == casFeat_person) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_person).getCode();

 
    casFeat_city = jcas.getRequiredFeatureDE(casType, "city", "ru.kfu.itis.issst.ner.typesystem.iCity", featOkTst);
    casFeatCode_city  = (null == casFeat_city) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_city).getCode();

  }
}



    