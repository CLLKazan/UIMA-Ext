
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
public class Separator_Type extends Token_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Separator_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Separator_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Separator(addr, Separator_Type.this);
  			   Separator_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Separator(addr, Separator_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Separator.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ru.kfu.cll.uima.tokenizer.types.Separator");
 
  /** @generated */
  final Feature casFeat_Kind;
  /** @generated */
  final int     casFeatCode_Kind;
  /** @generated */ 
  public String getKind(int addr) {
        if (featOkTst && casFeat_Kind == null)
      jcas.throwFeatMissing("Kind", "ru.kfu.cll.uima.tokenizer.types.Separator");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Kind);
  }
  /** @generated */    
  public void setKind(int addr, String v) {
        if (featOkTst && casFeat_Kind == null)
      jcas.throwFeatMissing("Kind", "ru.kfu.cll.uima.tokenizer.types.Separator");
    ll_cas.ll_setStringValue(addr, casFeatCode_Kind, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Separator_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Kind = jcas.getRequiredFeatureDE(casType, "Kind", "uima.cas.String", featOkTst);
    casFeatCode_Kind  = (null == casFeat_Kind) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Kind).getCode();

  }
}



    