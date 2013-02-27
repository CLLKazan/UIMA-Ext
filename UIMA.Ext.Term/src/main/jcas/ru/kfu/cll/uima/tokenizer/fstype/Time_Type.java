
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
public class Time_Type extends Token_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Time_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Time_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Time(addr, Time_Type.this);
  			   Time_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Time(addr, Time_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = Time.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ru.kfu.cll.uima.tokenizer.fstype.Time");
 
  /** @generated */
  final Feature casFeat_format;
  /** @generated */
  final int     casFeatCode_format;
  /** @generated */ 
  public String getFormat(int addr) {
        if (featOkTst && casFeat_format == null)
      jcas.throwFeatMissing("format", "ru.kfu.cll.uima.tokenizer.fstype.Time");
    return ll_cas.ll_getStringValue(addr, casFeatCode_format);
  }
  /** @generated */    
  public void setFormat(int addr, String v) {
        if (featOkTst && casFeat_format == null)
      jcas.throwFeatMissing("format", "ru.kfu.cll.uima.tokenizer.fstype.Time");
    ll_cas.ll_setStringValue(addr, casFeatCode_format, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Time_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_format = jcas.getRequiredFeatureDE(casType, "format", "uima.cas.String", featOkTst);
    casFeatCode_format  = (null == casFeat_format) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_format).getCode();

  }
}



    