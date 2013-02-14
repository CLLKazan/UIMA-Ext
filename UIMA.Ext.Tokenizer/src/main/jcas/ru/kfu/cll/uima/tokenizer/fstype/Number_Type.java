
/* First created by JCasGen Mon Feb 11 15:36:42 MSK 2013 */
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
public class Number_Type extends Annotation_Type {
  /** @generated */
  @Override
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
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Number.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("tokenization.types.Number");
 
  /** @generated */
  final Feature casFeat_Sign;
  /** @generated */
  final int     casFeatCode_Sign;
  /** @generated */ 
  public String getSign(int addr) {
        if (featOkTst && casFeat_Sign == null)
      jcas.throwFeatMissing("Sign", "tokenization.types.Number");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Sign);
  }
  /** @generated */    
  public void setSign(int addr, String v) {
        if (featOkTst && casFeat_Sign == null)
      jcas.throwFeatMissing("Sign", "tokenization.types.Number");
    ll_cas.ll_setStringValue(addr, casFeatCode_Sign, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Kind;
  /** @generated */
  final int     casFeatCode_Kind;
  /** @generated */ 
  public String getKind(int addr) {
        if (featOkTst && casFeat_Kind == null)
      jcas.throwFeatMissing("Kind", "tokenization.types.Number");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Kind);
  }
  /** @generated */    
  public void setKind(int addr, String v) {
        if (featOkTst && casFeat_Kind == null)
      jcas.throwFeatMissing("Kind", "tokenization.types.Number");
    ll_cas.ll_setStringValue(addr, casFeatCode_Kind, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Number_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Sign = jcas.getRequiredFeatureDE(casType, "Sign", "uima.cas.String", featOkTst);
    casFeatCode_Sign  = (null == casFeat_Sign) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Sign).getCode();

 
    casFeat_Kind = jcas.getRequiredFeatureDE(casType, "Kind", "uima.cas.String", featOkTst);
    casFeatCode_Kind  = (null == casFeat_Kind) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Kind).getCode();

  }
}



    