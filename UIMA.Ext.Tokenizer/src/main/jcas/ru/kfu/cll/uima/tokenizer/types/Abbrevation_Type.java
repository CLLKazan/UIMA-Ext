
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
public class Abbrevation_Type extends Token_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Abbrevation_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Abbrevation_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Abbrevation(addr, Abbrevation_Type.this);
  			   Abbrevation_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Abbrevation(addr, Abbrevation_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Abbrevation.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ru.kfu.cll.uima.tokenizer.types.Abbrevation");
 
  /** @generated */
  final Feature casFeat_Language;
  /** @generated */
  final int     casFeatCode_Language;
  /** @generated */ 
  public String getLanguage(int addr) {
        if (featOkTst && casFeat_Language == null)
      jcas.throwFeatMissing("Language", "ru.kfu.cll.uima.tokenizer.types.Abbrevation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Language);
  }
  /** @generated */    
  public void setLanguage(int addr, String v) {
        if (featOkTst && casFeat_Language == null)
      jcas.throwFeatMissing("Language", "ru.kfu.cll.uima.tokenizer.types.Abbrevation");
    ll_cas.ll_setStringValue(addr, casFeatCode_Language, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Abbrevation_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Language = jcas.getRequiredFeatureDE(casType, "Language", "uima.cas.String", featOkTst);
    casFeatCode_Language  = (null == casFeat_Language) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Language).getCode();

  }
}



    