
/* First created by JCasGen Tue Feb 05 17:20:42 MSK 2013 */
package com.hp.hplabs.lim2.ie.text.typesystem;

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
 * Updated by JCasGen Tue Feb 05 18:06:06 MSK 2013
 * @generated */
public class HL_WordNumber_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (HL_WordNumber_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = HL_WordNumber_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new HL_WordNumber(addr, HL_WordNumber_Type.this);
  			   HL_WordNumber_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new HL_WordNumber(addr, HL_WordNumber_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = HL_WordNumber.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("com.hp.hplabs.lim2.ie.text.typesystem.HL_WordNumber");
 
  /** @generated */
  final Feature casFeat_num;
  /** @generated */
  final int     casFeatCode_num;
  /** @generated */ 
  public String getNum(int addr) {
        if (featOkTst && casFeat_num == null)
      jcas.throwFeatMissing("num", "com.hp.hplabs.lim2.ie.text.typesystem.HL_WordNumber");
    return ll_cas.ll_getStringValue(addr, casFeatCode_num);
  }
  /** @generated */    
  public void setNum(int addr, String v) {
        if (featOkTst && casFeat_num == null)
      jcas.throwFeatMissing("num", "com.hp.hplabs.lim2.ie.text.typesystem.HL_WordNumber");
    ll_cas.ll_setStringValue(addr, casFeatCode_num, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public HL_WordNumber_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_num = jcas.getRequiredFeatureDE(casType, "num", "uima.cas.String", featOkTst);
    casFeatCode_num  = (null == casFeat_num) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_num).getCode();

  }
}



    