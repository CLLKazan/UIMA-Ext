
/* First created by JCasGen Sun May 05 02:07:47 MSK 2013 */
package ru.kfu.cll.uima.stemmer.types;

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
 * Updated by JCasGen Sun May 05 02:07:47 MSK 2013
 * @generated */
public class StemID_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (StemID_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = StemID_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new StemID(addr, StemID_Type.this);
  			   StemID_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new StemID(addr, StemID_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = StemID.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ru.kfu.cll.uima.stemmer.types.StemID");
 
  /** @generated */
  final Feature casFeat_Index;
  /** @generated */
  final int     casFeatCode_Index;
  /** @generated */ 
  public String getIndex(int addr) {
        if (featOkTst && casFeat_Index == null)
      jcas.throwFeatMissing("Index", "ru.kfu.cll.uima.stemmer.types.StemID");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Index);
  }
  /** @generated */    
  public void setIndex(int addr, String v) {
        if (featOkTst && casFeat_Index == null)
      jcas.throwFeatMissing("Index", "ru.kfu.cll.uima.stemmer.types.StemID");
    ll_cas.ll_setStringValue(addr, casFeatCode_Index, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public StemID_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Index = jcas.getRequiredFeatureDE(casType, "Index", "uima.cas.String", featOkTst);
    casFeatCode_Index  = (null == casFeat_Index) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Index).getCode();

  }
}



    