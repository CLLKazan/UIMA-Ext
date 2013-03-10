
/* First created by JCasGen Sat Mar 02 18:51:31 MSK 2013 */
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
 * Updated by JCasGen Sun Mar 10 20:12:40 MSK 2013
 * @generated */
public class Token_Type extends Annotation_Type {
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
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("tokenization.types.Token");
 
  /** @generated */
  final Feature casFeat_Text;
  /** @generated */
  final int     casFeatCode_Text;
  /** @generated */ 
  public String getText(int addr) {
        if (featOkTst && casFeat_Text == null)
      jcas.throwFeatMissing("Text", "tokenization.types.Token");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Text);
  }
  /** @generated */    
  public void setText(int addr, String v) {
        if (featOkTst && casFeat_Text == null)
      jcas.throwFeatMissing("Text", "tokenization.types.Token");
    ll_cas.ll_setStringValue(addr, casFeatCode_Text, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Norm;
  /** @generated */
  final int     casFeatCode_Norm;
  /** @generated */ 
  public String getNorm(int addr) {
        if (featOkTst && casFeat_Norm == null)
      jcas.throwFeatMissing("Norm", "tokenization.types.Token");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Norm);
  }
  /** @generated */    
  public void setNorm(int addr, String v) {
        if (featOkTst && casFeat_Norm == null)
      jcas.throwFeatMissing("Norm", "tokenization.types.Token");
    ll_cas.ll_setStringValue(addr, casFeatCode_Norm, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Token_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Text = jcas.getRequiredFeatureDE(casType, "Text", "uima.cas.String", featOkTst);
    casFeatCode_Text  = (null == casFeat_Text) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Text).getCode();

 
    casFeat_Norm = jcas.getRequiredFeatureDE(casType, "Norm", "uima.cas.String", featOkTst);
    casFeatCode_Norm  = (null == casFeat_Norm) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Norm).getCode();

  }
}



    