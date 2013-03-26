
/* First created by JCasGen Tue Mar 26 14:06:51 SAMT 2013 */
package ru.kfu.cll.uima.segmentation.fstype;

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
 * Updated by JCasGen Tue Mar 26 14:06:51 SAMT 2013
 * @generated */
public class Token_Type extends TokenBase_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Token_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Token_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Token(addr, Token_Type.this);
  			   Token_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Token(addr, Token_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Token.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ru.kfu.cll.uima.segmentation.fstype.Token");
 
  /** @generated */
  final Feature casFeat_TypeName;
  /** @generated */
  final int     casFeatCode_TypeName;
  /** @generated */ 
  public String getTypeName(int addr) {
        if (featOkTst && casFeat_TypeName == null)
      jcas.throwFeatMissing("TypeName", "ru.kfu.cll.uima.segmentation.fstype.Token");
    return ll_cas.ll_getStringValue(addr, casFeatCode_TypeName);
  }
  /** @generated */    
  public void setTypeName(int addr, String v) {
        if (featOkTst && casFeat_TypeName == null)
      jcas.throwFeatMissing("TypeName", "ru.kfu.cll.uima.segmentation.fstype.Token");
    ll_cas.ll_setStringValue(addr, casFeatCode_TypeName, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Token_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_TypeName = jcas.getRequiredFeatureDE(casType, "TypeName", "uima.cas.String", featOkTst);
    casFeatCode_TypeName  = (null == casFeat_TypeName) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_TypeName).getCode();

  }
}



    