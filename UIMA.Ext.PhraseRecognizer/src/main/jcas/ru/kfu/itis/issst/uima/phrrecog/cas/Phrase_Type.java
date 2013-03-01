
/* First created by JCasGen Fri Mar 01 17:08:28 MSK 2013 */
package ru.kfu.itis.issst.uima.phrrecog.cas;

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

/** Represents typed 'phrase', i.e. a head word with its dependents.
				Phrase annotation should have the same borders with its head word.
 * Updated by JCasGen Fri Mar 01 17:08:28 MSK 2013
 * @generated */
public class Phrase_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Phrase_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Phrase_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Phrase(addr, Phrase_Type.this);
  			   Phrase_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Phrase(addr, Phrase_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Phrase.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ru.kfu.itis.issst.uima.phrrecog.cas.Phrase");
 
  /** @generated */
  final Feature casFeat_head;
  /** @generated */
  final int     casFeatCode_head;
  /** @generated */ 
  public int getHead(int addr) {
        if (featOkTst && casFeat_head == null)
      jcas.throwFeatMissing("head", "ru.kfu.itis.issst.uima.phrrecog.cas.Phrase");
    return ll_cas.ll_getRefValue(addr, casFeatCode_head);
  }
  /** @generated */    
  public void setHead(int addr, int v) {
        if (featOkTst && casFeat_head == null)
      jcas.throwFeatMissing("head", "ru.kfu.itis.issst.uima.phrrecog.cas.Phrase");
    ll_cas.ll_setRefValue(addr, casFeatCode_head, v);}
    
  
 
  /** @generated */
  final Feature casFeat_dependents;
  /** @generated */
  final int     casFeatCode_dependents;
  /** @generated */ 
  public int getDependents(int addr) {
        if (featOkTst && casFeat_dependents == null)
      jcas.throwFeatMissing("dependents", "ru.kfu.itis.issst.uima.phrrecog.cas.Phrase");
    return ll_cas.ll_getRefValue(addr, casFeatCode_dependents);
  }
  /** @generated */    
  public void setDependents(int addr, int v) {
        if (featOkTst && casFeat_dependents == null)
      jcas.throwFeatMissing("dependents", "ru.kfu.itis.issst.uima.phrrecog.cas.Phrase");
    ll_cas.ll_setRefValue(addr, casFeatCode_dependents, v);}
    
   /** @generated */
  public int getDependents(int addr, int i) {
        if (featOkTst && casFeat_dependents == null)
      jcas.throwFeatMissing("dependents", "ru.kfu.itis.issst.uima.phrrecog.cas.Phrase");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_dependents), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_dependents), i);
	return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_dependents), i);
  }
   
  /** @generated */ 
  public void setDependents(int addr, int i, int v) {
        if (featOkTst && casFeat_dependents == null)
      jcas.throwFeatMissing("dependents", "ru.kfu.itis.issst.uima.phrrecog.cas.Phrase");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_dependents), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_dependents), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_dependents), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Phrase_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_head = jcas.getRequiredFeatureDE(casType, "head", "org.opencorpora.cas.Word", featOkTst);
    casFeatCode_head  = (null == casFeat_head) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_head).getCode();

 
    casFeat_dependents = jcas.getRequiredFeatureDE(casType, "dependents", "uima.cas.FSArray", featOkTst);
    casFeatCode_dependents  = (null == casFeat_dependents) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_dependents).getCode();

  }
}



    