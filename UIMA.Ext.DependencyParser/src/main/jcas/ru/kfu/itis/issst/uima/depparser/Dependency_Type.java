
/* First created by JCasGen Mon Mar 17 18:18:37 MSK 2014 */
package ru.kfu.itis.issst.uima.depparser;

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
 * Updated by JCasGen Mon Mar 17 18:18:37 MSK 2014
 * @generated */
public class Dependency_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Dependency_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Dependency_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Dependency(addr, Dependency_Type.this);
  			   Dependency_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Dependency(addr, Dependency_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Dependency.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ru.kfu.itis.issst.uima.depparser.Dependency");
 
  /** @generated */
  final Feature casFeat_dependent;
  /** @generated */
  final int     casFeatCode_dependent;
  /** @generated */ 
  public int getDependent(int addr) {
        if (featOkTst && casFeat_dependent == null)
      jcas.throwFeatMissing("dependent", "ru.kfu.itis.issst.uima.depparser.Dependency");
    return ll_cas.ll_getRefValue(addr, casFeatCode_dependent);
  }
  /** @generated */    
  public void setDependent(int addr, int v) {
        if (featOkTst && casFeat_dependent == null)
      jcas.throwFeatMissing("dependent", "ru.kfu.itis.issst.uima.depparser.Dependency");
    ll_cas.ll_setRefValue(addr, casFeatCode_dependent, v);}
    
  
 
  /** @generated */
  final Feature casFeat_head;
  /** @generated */
  final int     casFeatCode_head;
  /** @generated */ 
  public int getHead(int addr) {
        if (featOkTst && casFeat_head == null)
      jcas.throwFeatMissing("head", "ru.kfu.itis.issst.uima.depparser.Dependency");
    return ll_cas.ll_getRefValue(addr, casFeatCode_head);
  }
  /** @generated */    
  public void setHead(int addr, int v) {
        if (featOkTst && casFeat_head == null)
      jcas.throwFeatMissing("head", "ru.kfu.itis.issst.uima.depparser.Dependency");
    ll_cas.ll_setRefValue(addr, casFeatCode_head, v);}
    
  
 
  /** @generated */
  final Feature casFeat_label;
  /** @generated */
  final int     casFeatCode_label;
  /** @generated */ 
  public String getLabel(int addr) {
        if (featOkTst && casFeat_label == null)
      jcas.throwFeatMissing("label", "ru.kfu.itis.issst.uima.depparser.Dependency");
    return ll_cas.ll_getStringValue(addr, casFeatCode_label);
  }
  /** @generated */    
  public void setLabel(int addr, String v) {
        if (featOkTst && casFeat_label == null)
      jcas.throwFeatMissing("label", "ru.kfu.itis.issst.uima.depparser.Dependency");
    ll_cas.ll_setStringValue(addr, casFeatCode_label, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Dependency_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_dependent = jcas.getRequiredFeatureDE(casType, "dependent", "org.opencorpora.cas.Word", featOkTst);
    casFeatCode_dependent  = (null == casFeat_dependent) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_dependent).getCode();

 
    casFeat_head = jcas.getRequiredFeatureDE(casType, "head", "org.opencorpora.cas.Word", featOkTst);
    casFeatCode_head  = (null == casFeat_head) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_head).getCode();

 
    casFeat_label = jcas.getRequiredFeatureDE(casType, "label", "uima.cas.String", featOkTst);
    casFeatCode_label  = (null == casFeat_label) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_label).getCode();

  }
}



    