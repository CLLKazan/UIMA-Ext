
/* First created by JCasGen Tue Feb 05 17:27:15 MSK 2013 */
package org.apache.uima.tutorial;

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
 * Updated by JCasGen Thu Feb 07 16:11:41 MSK 2013
 * @generated */
public class RoomNumber_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (RoomNumber_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = RoomNumber_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new RoomNumber(addr, RoomNumber_Type.this);
  			   RoomNumber_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new RoomNumber(addr, RoomNumber_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = RoomNumber.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.apache.uima.tutorial.RoomNumber");
 
  /** @generated */
  final Feature casFeat_building;
  /** @generated */
  final int     casFeatCode_building;
  /** @generated */ 
  public String getBuilding(int addr) {
        if (featOkTst && casFeat_building == null)
      jcas.throwFeatMissing("building", "org.apache.uima.tutorial.RoomNumber");
    return ll_cas.ll_getStringValue(addr, casFeatCode_building);
  }
  /** @generated */    
  public void setBuilding(int addr, String v) {
        if (featOkTst && casFeat_building == null)
      jcas.throwFeatMissing("building", "org.apache.uima.tutorial.RoomNumber");
    ll_cas.ll_setStringValue(addr, casFeatCode_building, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public RoomNumber_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_building = jcas.getRequiredFeatureDE(casType, "building", "uima.cas.String", featOkTst);
    casFeatCode_building  = (null == casFeat_building) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_building).getCode();

  }
}



    