
/* First created by JCasGen Tue Mar 12 00:30:02 MSK 2013 */
package ru.kfu.itis.issst.ner.typesystem;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;

/** An abbreviation is a letter or group of letters, taken from a word or words. For example, the word "abbreviation" can be abbreviated as "abbr." or "abbrev."
 * Updated by JCasGen Tue Mar 12 00:30:02 MSK 2013
 * @generated */
public class Abbreviation_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Abbreviation_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Abbreviation_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Abbreviation(addr, Abbreviation_Type.this);
  			   Abbreviation_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Abbreviation(addr, Abbreviation_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Abbreviation.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ru.kfu.itis.issst.ner.typesystem.Abbreviation");
 
  /** @generated */
  final Feature casFeat_expan;
  /** @generated */
  final int     casFeatCode_expan;
  /** @generated */ 
  public String getExpan(int addr) {
        if (featOkTst && casFeat_expan == null)
      jcas.throwFeatMissing("expan", "ru.kfu.itis.issst.ner.typesystem.Abbreviation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_expan);
  }
  /** @generated */    
  public void setExpan(int addr, String v) {
        if (featOkTst && casFeat_expan == null)
      jcas.throwFeatMissing("expan", "ru.kfu.itis.issst.ner.typesystem.Abbreviation");
    ll_cas.ll_setStringValue(addr, casFeatCode_expan, v);}
    
  
 
  /** @generated */
  final Feature casFeat_textReference;
  /** @generated */
  final int     casFeatCode_textReference;
  /** @generated */ 
  public int getTextReference(int addr) {
        if (featOkTst && casFeat_textReference == null)
      jcas.throwFeatMissing("textReference", "ru.kfu.itis.issst.ner.typesystem.Abbreviation");
    return ll_cas.ll_getRefValue(addr, casFeatCode_textReference);
  }
  /** @generated */    
  public void setTextReference(int addr, int v) {
        if (featOkTst && casFeat_textReference == null)
      jcas.throwFeatMissing("textReference", "ru.kfu.itis.issst.ner.typesystem.Abbreviation");
    ll_cas.ll_setRefValue(addr, casFeatCode_textReference, v);}
    
  
 
  /** @generated */
  final Feature casFeat_definedHere;
  /** @generated */
  final int     casFeatCode_definedHere;
  /** @generated */ 
  public boolean getDefinedHere(int addr) {
        if (featOkTst && casFeat_definedHere == null)
      jcas.throwFeatMissing("definedHere", "ru.kfu.itis.issst.ner.typesystem.Abbreviation");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_definedHere);
  }
  /** @generated */    
  public void setDefinedHere(int addr, boolean v) {
        if (featOkTst && casFeat_definedHere == null)
      jcas.throwFeatMissing("definedHere", "ru.kfu.itis.issst.ner.typesystem.Abbreviation");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_definedHere, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Abbreviation_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_expan = jcas.getRequiredFeatureDE(casType, "expan", "uima.cas.String", featOkTst);
    casFeatCode_expan  = (null == casFeat_expan) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_expan).getCode();

 
    casFeat_textReference = jcas.getRequiredFeatureDE(casType, "textReference", "ru.kfu.itis.issst.ner.typesystem.Annotation", featOkTst);
    casFeatCode_textReference  = (null == casFeat_textReference) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_textReference).getCode();

 
    casFeat_definedHere = jcas.getRequiredFeatureDE(casType, "definedHere", "uima.cas.Boolean", featOkTst);
    casFeatCode_definedHere  = (null == casFeat_definedHere) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_definedHere).getCode();

  }
}



    