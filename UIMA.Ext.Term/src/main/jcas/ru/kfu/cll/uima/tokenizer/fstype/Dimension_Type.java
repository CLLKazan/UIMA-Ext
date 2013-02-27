
/* First created by JCasGen Wed Feb 27 15:40:38 SAMT 2013 */
package ru.kfu.cll.uima.tokenizer.fstype;

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
 * Updated by JCasGen Wed Feb 27 15:40:38 SAMT 2013
 * @generated */
public class Dimension_Type extends Token_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Dimension_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Dimension_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Dimension(addr, Dimension_Type.this);
  			   Dimension_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Dimension(addr, Dimension_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = Dimension.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ru.kfu.cll.uima.tokenizer.fstype.Dimension");
 
  /** @generated */
  final Feature casFeat_value;
  /** @generated */
  final int     casFeatCode_value;
  /** @generated */ 
  public float getValue(int addr) {
        if (featOkTst && casFeat_value == null)
      jcas.throwFeatMissing("value", "ru.kfu.cll.uima.tokenizer.fstype.Dimension");
    return ll_cas.ll_getFloatValue(addr, casFeatCode_value);
  }
  /** @generated */    
  public void setValue(int addr, float v) {
        if (featOkTst && casFeat_value == null)
      jcas.throwFeatMissing("value", "ru.kfu.cll.uima.tokenizer.fstype.Dimension");
    ll_cas.ll_setFloatValue(addr, casFeatCode_value, v);}
    
  
 
  /** @generated */
  final Feature casFeat_unit;
  /** @generated */
  final int     casFeatCode_unit;
  /** @generated */ 
  public String getUnit(int addr) {
        if (featOkTst && casFeat_unit == null)
      jcas.throwFeatMissing("unit", "ru.kfu.cll.uima.tokenizer.fstype.Dimension");
    return ll_cas.ll_getStringValue(addr, casFeatCode_unit);
  }
  /** @generated */    
  public void setUnit(int addr, String v) {
        if (featOkTst && casFeat_unit == null)
      jcas.throwFeatMissing("unit", "ru.kfu.cll.uima.tokenizer.fstype.Dimension");
    ll_cas.ll_setStringValue(addr, casFeatCode_unit, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Dimension_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_value = jcas.getRequiredFeatureDE(casType, "value", "uima.cas.Float", featOkTst);
    casFeatCode_value  = (null == casFeat_value) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_value).getCode();

 
    casFeat_unit = jcas.getRequiredFeatureDE(casType, "unit", "uima.cas.String", featOkTst);
    casFeatCode_unit  = (null == casFeat_unit) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_unit).getCode();

  }
}



    