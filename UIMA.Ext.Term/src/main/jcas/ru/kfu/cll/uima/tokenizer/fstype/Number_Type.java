
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
public class Number_Type extends Token_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Number_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Number_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Number(addr, Number_Type.this);
  			   Number_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Number(addr, Number_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = Number.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ru.kfu.cll.uima.tokenizer.fstype.Number");
 
  /** @generated */
  final Feature casFeat_sign;
  /** @generated */
  final int     casFeatCode_sign;
  /** @generated */ 
  public String getSign(int addr) {
        if (featOkTst && casFeat_sign == null)
      jcas.throwFeatMissing("sign", "ru.kfu.cll.uima.tokenizer.fstype.Number");
    return ll_cas.ll_getStringValue(addr, casFeatCode_sign);
  }
  /** @generated */    
  public void setSign(int addr, String v) {
        if (featOkTst && casFeat_sign == null)
      jcas.throwFeatMissing("sign", "ru.kfu.cll.uima.tokenizer.fstype.Number");
    ll_cas.ll_setStringValue(addr, casFeatCode_sign, v);}
    
  
 
  /** @generated */
  final Feature casFeat_kind;
  /** @generated */
  final int     casFeatCode_kind;
  /** @generated */ 
  public String getKind(int addr) {
        if (featOkTst && casFeat_kind == null)
      jcas.throwFeatMissing("kind", "ru.kfu.cll.uima.tokenizer.fstype.Number");
    return ll_cas.ll_getStringValue(addr, casFeatCode_kind);
  }
  /** @generated */    
  public void setKind(int addr, String v) {
        if (featOkTst && casFeat_kind == null)
      jcas.throwFeatMissing("kind", "ru.kfu.cll.uima.tokenizer.fstype.Number");
    ll_cas.ll_setStringValue(addr, casFeatCode_kind, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Number_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_sign = jcas.getRequiredFeatureDE(casType, "sign", "ru.kfu.cll.uima.tokenizer.fstype.NumberSignType", featOkTst);
    casFeatCode_sign  = (null == casFeat_sign) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_sign).getCode();

 
    casFeat_kind = jcas.getRequiredFeatureDE(casType, "kind", "ru.kfu.cll.uima.tokenizer.fstype.NumberKindType", featOkTst);
    casFeatCode_kind  = (null == casFeat_kind) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_kind).getCode();

  }
}



    