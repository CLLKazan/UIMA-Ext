
/* First created by JCasGen Sun Jan 20 15:26:39 SAMT 2013 */
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
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Sun Jan 20 15:26:39 SAMT 2013
 * @generated */
public class PMSegment_Type extends Annotation_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (PMSegment_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = PMSegment_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new PMSegment(addr, PMSegment_Type.this);
  			   PMSegment_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new PMSegment(addr, PMSegment_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = PMSegment.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ru.kfu.cll.uima.segmentation.fstype.PMSegment");
 
  /** @generated */
  final Feature casFeat_contentBegin;
  /** @generated */
  final int     casFeatCode_contentBegin;
  /** @generated */ 
  public int getContentBegin(int addr) {
        if (featOkTst && casFeat_contentBegin == null)
      jcas.throwFeatMissing("contentBegin", "ru.kfu.cll.uima.segmentation.fstype.PMSegment");
    return ll_cas.ll_getIntValue(addr, casFeatCode_contentBegin);
  }
  /** @generated */    
  public void setContentBegin(int addr, int v) {
        if (featOkTst && casFeat_contentBegin == null)
      jcas.throwFeatMissing("contentBegin", "ru.kfu.cll.uima.segmentation.fstype.PMSegment");
    ll_cas.ll_setIntValue(addr, casFeatCode_contentBegin, v);}
    
  
 
  /** @generated */
  final Feature casFeat_contentEnd;
  /** @generated */
  final int     casFeatCode_contentEnd;
  /** @generated */ 
  public int getContentEnd(int addr) {
        if (featOkTst && casFeat_contentEnd == null)
      jcas.throwFeatMissing("contentEnd", "ru.kfu.cll.uima.segmentation.fstype.PMSegment");
    return ll_cas.ll_getIntValue(addr, casFeatCode_contentEnd);
  }
  /** @generated */    
  public void setContentEnd(int addr, int v) {
        if (featOkTst && casFeat_contentEnd == null)
      jcas.throwFeatMissing("contentEnd", "ru.kfu.cll.uima.segmentation.fstype.PMSegment");
    ll_cas.ll_setIntValue(addr, casFeatCode_contentEnd, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public PMSegment_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_contentBegin = jcas.getRequiredFeatureDE(casType, "contentBegin", "uima.cas.Integer", featOkTst);
    casFeatCode_contentBegin  = (null == casFeat_contentBegin) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_contentBegin).getCode();

 
    casFeat_contentEnd = jcas.getRequiredFeatureDE(casType, "contentEnd", "uima.cas.Integer", featOkTst);
    casFeatCode_contentEnd  = (null == casFeat_contentEnd) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_contentEnd).getCode();

  }
}



    