
/* First created by JCasGen Sun Mar 10 01:48:22 MSK 2013 */
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

/** 
 * Updated by JCasGen Sun Mar 10 20:12:40 MSK 2013
 * @generated */
public class Date_Type extends Token_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Date_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Date_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Date(addr, Date_Type.this);
  			   Date_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Date(addr, Date_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Date.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("tokenization.types.Date");
 
  /** @generated */
  final Feature casFeat_Year;
  /** @generated */
  final int     casFeatCode_Year;
  /** @generated */ 
  public String getYear(int addr) {
        if (featOkTst && casFeat_Year == null)
      jcas.throwFeatMissing("Year", "tokenization.types.Date");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Year);
  }
  /** @generated */    
  public void setYear(int addr, String v) {
        if (featOkTst && casFeat_Year == null)
      jcas.throwFeatMissing("Year", "tokenization.types.Date");
    ll_cas.ll_setStringValue(addr, casFeatCode_Year, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Mounth;
  /** @generated */
  final int     casFeatCode_Mounth;
  /** @generated */ 
  public String getMounth(int addr) {
        if (featOkTst && casFeat_Mounth == null)
      jcas.throwFeatMissing("Mounth", "tokenization.types.Date");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Mounth);
  }
  /** @generated */    
  public void setMounth(int addr, String v) {
        if (featOkTst && casFeat_Mounth == null)
      jcas.throwFeatMissing("Mounth", "tokenization.types.Date");
    ll_cas.ll_setStringValue(addr, casFeatCode_Mounth, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Day;
  /** @generated */
  final int     casFeatCode_Day;
  /** @generated */ 
  public String getDay(int addr) {
        if (featOkTst && casFeat_Day == null)
      jcas.throwFeatMissing("Day", "tokenization.types.Date");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Day);
  }
  /** @generated */    
  public void setDay(int addr, String v) {
        if (featOkTst && casFeat_Day == null)
      jcas.throwFeatMissing("Day", "tokenization.types.Date");
    ll_cas.ll_setStringValue(addr, casFeatCode_Day, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Date_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Year = jcas.getRequiredFeatureDE(casType, "Year", "uima.cas.String", featOkTst);
    casFeatCode_Year  = (null == casFeat_Year) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Year).getCode();

 
    casFeat_Mounth = jcas.getRequiredFeatureDE(casType, "Mounth", "uima.cas.String", featOkTst);
    casFeatCode_Mounth  = (null == casFeat_Mounth) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Mounth).getCode();

 
    casFeat_Day = jcas.getRequiredFeatureDE(casType, "Day", "uima.cas.String", featOkTst);
    casFeatCode_Day  = (null == casFeat_Day) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Day).getCode();

  }
}



    