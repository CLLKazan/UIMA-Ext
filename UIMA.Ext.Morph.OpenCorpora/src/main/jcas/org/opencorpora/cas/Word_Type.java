
/* First created by JCasGen Tue Jul 24 17:36:26 MSK 2012 */
package org.opencorpora.cas;

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
 * Updated by JCasGen Tue Jul 24 17:36:26 MSK 2012
 * @generated */
public class Word_Type extends Annotation_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Word_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Word_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Word(addr, Word_Type.this);
  			   Word_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Word(addr, Word_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = Word.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.opencorpora.cas.Word");
 
  /** @generated */
  final Feature casFeat_wordforms;
  /** @generated */
  final int     casFeatCode_wordforms;
  /** @generated */ 
  public int getWordforms(int addr) {
        if (featOkTst && casFeat_wordforms == null)
      jcas.throwFeatMissing("wordforms", "org.opencorpora.cas.Word");
    return ll_cas.ll_getRefValue(addr, casFeatCode_wordforms);
  }
  /** @generated */    
  public void setWordforms(int addr, int v) {
        if (featOkTst && casFeat_wordforms == null)
      jcas.throwFeatMissing("wordforms", "org.opencorpora.cas.Word");
    ll_cas.ll_setRefValue(addr, casFeatCode_wordforms, v);}
    
   /** @generated */
  public int getWordforms(int addr, int i) {
        if (featOkTst && casFeat_wordforms == null)
      jcas.throwFeatMissing("wordforms", "org.opencorpora.cas.Word");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_wordforms), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_wordforms), i);
	return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_wordforms), i);
  }
   
  /** @generated */ 
  public void setWordforms(int addr, int i, int v) {
        if (featOkTst && casFeat_wordforms == null)
      jcas.throwFeatMissing("wordforms", "org.opencorpora.cas.Word");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_wordforms), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_wordforms), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_wordforms), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Word_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_wordforms = jcas.getRequiredFeatureDE(casType, "wordforms", "uima.cas.FSArray", featOkTst);
    casFeatCode_wordforms  = (null == casFeat_wordforms) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_wordforms).getCode();

  }
}



    