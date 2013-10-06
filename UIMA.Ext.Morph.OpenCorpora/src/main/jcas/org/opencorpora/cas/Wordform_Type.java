
/* First created by JCasGen Tue Jul 16 19:01:56 MSD 2013 */
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
import org.apache.uima.jcas.cas.TOP_Type;

/** 
 * Updated by JCasGen Tue Jul 16 19:01:56 MSD 2013
 * @generated */
public class Wordform_Type extends TOP_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Wordform_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Wordform_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Wordform(addr, Wordform_Type.this);
  			   Wordform_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Wordform(addr, Wordform_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Wordform.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.opencorpora.cas.Wordform");
 
  /** @generated */
  final Feature casFeat_pos;
  /** @generated */
  final int     casFeatCode_pos;
  /** @generated */ 
  public String getPos(int addr) {
        if (featOkTst && casFeat_pos == null)
      jcas.throwFeatMissing("pos", "org.opencorpora.cas.Wordform");
    return ll_cas.ll_getStringValue(addr, casFeatCode_pos);
  }
  /** @generated */    
  public void setPos(int addr, String v) {
        if (featOkTst && casFeat_pos == null)
      jcas.throwFeatMissing("pos", "org.opencorpora.cas.Wordform");
    ll_cas.ll_setStringValue(addr, casFeatCode_pos, v);}
    
  
 
  /** @generated */
  final Feature casFeat_posBits;
  /** @generated */
  final int     casFeatCode_posBits;
  /** @generated */ 
  public int getPosBits(int addr) {
        if (featOkTst && casFeat_posBits == null)
      jcas.throwFeatMissing("posBits", "org.opencorpora.cas.Wordform");
    return ll_cas.ll_getRefValue(addr, casFeatCode_posBits);
  }
  /** @generated */    
  public void setPosBits(int addr, int v) {
        if (featOkTst && casFeat_posBits == null)
      jcas.throwFeatMissing("posBits", "org.opencorpora.cas.Wordform");
    ll_cas.ll_setRefValue(addr, casFeatCode_posBits, v);}
    
   /** @generated */
  public long getPosBits(int addr, int i) {
        if (featOkTst && casFeat_posBits == null)
      jcas.throwFeatMissing("posBits", "org.opencorpora.cas.Wordform");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getLongArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_posBits), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_posBits), i);
	return ll_cas.ll_getLongArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_posBits), i);
  }
   
  /** @generated */ 
  public void setPosBits(int addr, int i, long v) {
        if (featOkTst && casFeat_posBits == null)
      jcas.throwFeatMissing("posBits", "org.opencorpora.cas.Wordform");
    if (lowLevelTypeChecks)
      ll_cas.ll_setLongArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_posBits), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_posBits), i);
    ll_cas.ll_setLongArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_posBits), i, v);
  }
 
 
  /** @generated */
  final Feature casFeat_lemma;
  /** @generated */
  final int     casFeatCode_lemma;
  /** @generated */ 
  public String getLemma(int addr) {
        if (featOkTst && casFeat_lemma == null)
      jcas.throwFeatMissing("lemma", "org.opencorpora.cas.Wordform");
    return ll_cas.ll_getStringValue(addr, casFeatCode_lemma);
  }
  /** @generated */    
  public void setLemma(int addr, String v) {
        if (featOkTst && casFeat_lemma == null)
      jcas.throwFeatMissing("lemma", "org.opencorpora.cas.Wordform");
    ll_cas.ll_setStringValue(addr, casFeatCode_lemma, v);}
    
  
 
  /** @generated */
  final Feature casFeat_lemmaId;
  /** @generated */
  final int     casFeatCode_lemmaId;
  /** @generated */ 
  public int getLemmaId(int addr) {
        if (featOkTst && casFeat_lemmaId == null)
      jcas.throwFeatMissing("lemmaId", "org.opencorpora.cas.Wordform");
    return ll_cas.ll_getIntValue(addr, casFeatCode_lemmaId);
  }
  /** @generated */    
  public void setLemmaId(int addr, int v) {
        if (featOkTst && casFeat_lemmaId == null)
      jcas.throwFeatMissing("lemmaId", "org.opencorpora.cas.Wordform");
    ll_cas.ll_setIntValue(addr, casFeatCode_lemmaId, v);}
    
  
 
  /** @generated */
  final Feature casFeat_grammems;
  /** @generated */
  final int     casFeatCode_grammems;
  /** @generated */ 
  public int getGrammems(int addr) {
        if (featOkTst && casFeat_grammems == null)
      jcas.throwFeatMissing("grammems", "org.opencorpora.cas.Wordform");
    return ll_cas.ll_getRefValue(addr, casFeatCode_grammems);
  }
  /** @generated */    
  public void setGrammems(int addr, int v) {
        if (featOkTst && casFeat_grammems == null)
      jcas.throwFeatMissing("grammems", "org.opencorpora.cas.Wordform");
    ll_cas.ll_setRefValue(addr, casFeatCode_grammems, v);}
    
   /** @generated */
  public String getGrammems(int addr, int i) {
        if (featOkTst && casFeat_grammems == null)
      jcas.throwFeatMissing("grammems", "org.opencorpora.cas.Wordform");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_grammems), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_grammems), i);
	return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_grammems), i);
  }
   
  /** @generated */ 
  public void setGrammems(int addr, int i, String v) {
        if (featOkTst && casFeat_grammems == null)
      jcas.throwFeatMissing("grammems", "org.opencorpora.cas.Wordform");
    if (lowLevelTypeChecks)
      ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_grammems), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_grammems), i);
    ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_grammems), i, v);
  }
 
 
  /** @generated */
  final Feature casFeat_word;
  /** @generated */
  final int     casFeatCode_word;
  /** @generated */ 
  public int getWord(int addr) {
        if (featOkTst && casFeat_word == null)
      jcas.throwFeatMissing("word", "org.opencorpora.cas.Wordform");
    return ll_cas.ll_getRefValue(addr, casFeatCode_word);
  }
  /** @generated */    
  public void setWord(int addr, int v) {
        if (featOkTst && casFeat_word == null)
      jcas.throwFeatMissing("word", "org.opencorpora.cas.Wordform");
    ll_cas.ll_setRefValue(addr, casFeatCode_word, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Wordform_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_pos = jcas.getRequiredFeatureDE(casType, "pos", "uima.cas.String", featOkTst);
    casFeatCode_pos  = (null == casFeat_pos) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_pos).getCode();

 
    casFeat_posBits = jcas.getRequiredFeatureDE(casType, "posBits", "uima.cas.LongArray", featOkTst);
    casFeatCode_posBits  = (null == casFeat_posBits) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_posBits).getCode();

 
    casFeat_lemma = jcas.getRequiredFeatureDE(casType, "lemma", "uima.cas.String", featOkTst);
    casFeatCode_lemma  = (null == casFeat_lemma) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_lemma).getCode();

 
    casFeat_lemmaId = jcas.getRequiredFeatureDE(casType, "lemmaId", "uima.cas.Integer", featOkTst);
    casFeatCode_lemmaId  = (null == casFeat_lemmaId) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_lemmaId).getCode();

 
    casFeat_grammems = jcas.getRequiredFeatureDE(casType, "grammems", "uima.cas.StringArray", featOkTst);
    casFeatCode_grammems  = (null == casFeat_grammems) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_grammems).getCode();

 
    casFeat_word = jcas.getRequiredFeatureDE(casType, "word", "org.opencorpora.cas.Word", featOkTst);
    casFeatCode_word  = (null == casFeat_word) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_word).getCode();

  }
}



    