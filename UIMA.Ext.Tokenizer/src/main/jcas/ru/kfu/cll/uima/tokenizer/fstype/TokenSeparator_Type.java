
/* First created by JCasGen Thu Feb 14 01:21:33 MSK 2013 */
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
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Thu Feb 14 01:21:33 MSK 2013
 * @generated */
public class TokenSeparator_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (TokenSeparator_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = TokenSeparator_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new TokenSeparator(addr, TokenSeparator_Type.this);
  			   TokenSeparator_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new TokenSeparator(addr, TokenSeparator_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = TokenSeparator.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("tokenization.types.TokenSeparator");
 
  /** @generated */
  final Feature casFeat_TypeOfSeparator;
  /** @generated */
  final int     casFeatCode_TypeOfSeparator;
  /** @generated */ 
  public String getTypeOfSeparator(int addr) {
        if (featOkTst && casFeat_TypeOfSeparator == null)
      jcas.throwFeatMissing("TypeOfSeparator", "tokenization.types.TokenSeparator");
    return ll_cas.ll_getStringValue(addr, casFeatCode_TypeOfSeparator);
  }
  /** @generated */    
  public void setTypeOfSeparator(int addr, String v) {
        if (featOkTst && casFeat_TypeOfSeparator == null)
      jcas.throwFeatMissing("TypeOfSeparator", "tokenization.types.TokenSeparator");
    ll_cas.ll_setStringValue(addr, casFeatCode_TypeOfSeparator, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public TokenSeparator_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_TypeOfSeparator = jcas.getRequiredFeatureDE(casType, "TypeOfSeparator", "uima.cas.String", featOkTst);
    casFeatCode_TypeOfSeparator  = (null == casFeat_TypeOfSeparator) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_TypeOfSeparator).getCode();

  }
}



    