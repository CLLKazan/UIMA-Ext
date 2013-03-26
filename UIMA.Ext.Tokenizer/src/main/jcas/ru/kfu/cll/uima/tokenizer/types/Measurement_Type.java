
/* First created by JCasGen Tue Mar 26 13:55:53 SAMT 2013 */
package ru.kfu.cll.uima.tokenizer.types;

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
 * Updated by JCasGen Tue Mar 26 13:55:53 SAMT 2013
 * @generated */
public class Measurement_Type extends Token_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Measurement_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Measurement_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Measurement(addr, Measurement_Type.this);
  			   Measurement_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Measurement(addr, Measurement_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Measurement.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ru.kfu.cll.uima.tokenizer.types.Measurement");
 
  /** @generated */
  final Feature casFeat_UnitName;
  /** @generated */
  final int     casFeatCode_UnitName;
  /** @generated */ 
  public String getUnitName(int addr) {
        if (featOkTst && casFeat_UnitName == null)
      jcas.throwFeatMissing("UnitName", "ru.kfu.cll.uima.tokenizer.types.Measurement");
    return ll_cas.ll_getStringValue(addr, casFeatCode_UnitName);
  }
  /** @generated */    
  public void setUnitName(int addr, String v) {
        if (featOkTst && casFeat_UnitName == null)
      jcas.throwFeatMissing("UnitName", "ru.kfu.cll.uima.tokenizer.types.Measurement");
    ll_cas.ll_setStringValue(addr, casFeatCode_UnitName, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Value;
  /** @generated */
  final int     casFeatCode_Value;
  /** @generated */ 
  public String getValue(int addr) {
        if (featOkTst && casFeat_Value == null)
      jcas.throwFeatMissing("Value", "ru.kfu.cll.uima.tokenizer.types.Measurement");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Value);
  }
  /** @generated */    
  public void setValue(int addr, String v) {
        if (featOkTst && casFeat_Value == null)
      jcas.throwFeatMissing("Value", "ru.kfu.cll.uima.tokenizer.types.Measurement");
    ll_cas.ll_setStringValue(addr, casFeatCode_Value, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Measurement_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_UnitName = jcas.getRequiredFeatureDE(casType, "UnitName", "uima.cas.String", featOkTst);
    casFeatCode_UnitName  = (null == casFeat_UnitName) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_UnitName).getCode();

 
    casFeat_Value = jcas.getRequiredFeatureDE(casType, "Value", "uima.cas.String", featOkTst);
    casFeatCode_Value  = (null == casFeat_Value) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Value).getCode();

  }
}



    