
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
public class ComplexWord_Type extends Token_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (ComplexWord_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = ComplexWord_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new ComplexWord(addr, ComplexWord_Type.this);
  			   ComplexWord_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new ComplexWord(addr, ComplexWord_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = ComplexWord.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ru.kfu.cll.uima.tokenizer.types.ComplexWord");
 
  /** @generated */
  final Feature casFeat_Left;
  /** @generated */
  final int     casFeatCode_Left;
  /** @generated */ 
  public String getLeft(int addr) {
        if (featOkTst && casFeat_Left == null)
      jcas.throwFeatMissing("Left", "ru.kfu.cll.uima.tokenizer.types.ComplexWord");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Left);
  }
  /** @generated */    
  public void setLeft(int addr, String v) {
        if (featOkTst && casFeat_Left == null)
      jcas.throwFeatMissing("Left", "ru.kfu.cll.uima.tokenizer.types.ComplexWord");
    ll_cas.ll_setStringValue(addr, casFeatCode_Left, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Right;
  /** @generated */
  final int     casFeatCode_Right;
  /** @generated */ 
  public String getRight(int addr) {
        if (featOkTst && casFeat_Right == null)
      jcas.throwFeatMissing("Right", "ru.kfu.cll.uima.tokenizer.types.ComplexWord");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Right);
  }
  /** @generated */    
  public void setRight(int addr, String v) {
        if (featOkTst && casFeat_Right == null)
      jcas.throwFeatMissing("Right", "ru.kfu.cll.uima.tokenizer.types.ComplexWord");
    ll_cas.ll_setStringValue(addr, casFeatCode_Right, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public ComplexWord_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Left = jcas.getRequiredFeatureDE(casType, "Left", "uima.cas.String", featOkTst);
    casFeatCode_Left  = (null == casFeat_Left) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Left).getCode();

 
    casFeat_Right = jcas.getRequiredFeatureDE(casType, "Right", "uima.cas.String", featOkTst);
    casFeatCode_Right  = (null == casFeat_Right) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Right).getCode();

  }
}



    