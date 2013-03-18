
/* First created by JCasGen Sat Mar 09 01:46:51 MSK 2013 */
package tokenization.types;

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
 * Updated by JCasGen Sun Mar 10 20:12:40 MSK 2013
 * @generated */
public class Currensy_Type extends Token_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Currensy_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Currensy_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Currensy(addr, Currensy_Type.this);
  			   Currensy_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Currensy(addr, Currensy_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Currensy.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("tokenization.types.Currensy");
 
  /** @generated */
  final Feature casFeat_Value;
  /** @generated */
  final int     casFeatCode_Value;
  /** @generated */ 
  public String getValue(int addr) {
        if (featOkTst && casFeat_Value == null)
      jcas.throwFeatMissing("Value", "tokenization.types.Currensy");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Value);
  }
  /** @generated */    
  public void setValue(int addr, String v) {
        if (featOkTst && casFeat_Value == null)
      jcas.throwFeatMissing("Value", "tokenization.types.Currensy");
    ll_cas.ll_setStringValue(addr, casFeatCode_Value, v);}
    
  
 
  /** @generated */
  final Feature casFeat_CurrensySymbol;
  /** @generated */
  final int     casFeatCode_CurrensySymbol;
  /** @generated */ 
  public String getCurrensySymbol(int addr) {
        if (featOkTst && casFeat_CurrensySymbol == null)
      jcas.throwFeatMissing("CurrensySymbol", "tokenization.types.Currensy");
    return ll_cas.ll_getStringValue(addr, casFeatCode_CurrensySymbol);
  }
  /** @generated */    
  public void setCurrensySymbol(int addr, String v) {
        if (featOkTst && casFeat_CurrensySymbol == null)
      jcas.throwFeatMissing("CurrensySymbol", "tokenization.types.Currensy");
    ll_cas.ll_setStringValue(addr, casFeatCode_CurrensySymbol, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Currensy_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Value = jcas.getRequiredFeatureDE(casType, "Value", "uima.cas.String", featOkTst);
    casFeatCode_Value  = (null == casFeat_Value) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Value).getCode();

 
    casFeat_CurrensySymbol = jcas.getRequiredFeatureDE(casType, "CurrensySymbol", "uima.cas.String", featOkTst);
    casFeatCode_CurrensySymbol  = (null == casFeat_CurrensySymbol) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_CurrensySymbol).getCode();

  }
}



    