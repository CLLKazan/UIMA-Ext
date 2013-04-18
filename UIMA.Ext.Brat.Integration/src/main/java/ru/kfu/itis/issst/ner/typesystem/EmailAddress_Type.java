
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

/** 
 * Updated by JCasGen Tue Mar 12 00:30:02 MSK 2013
 * @generated */
public class EmailAddress_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (EmailAddress_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = EmailAddress_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new EmailAddress(addr, EmailAddress_Type.this);
  			   EmailAddress_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new EmailAddress(addr, EmailAddress_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = EmailAddress.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ru.kfu.itis.issst.ner.typesystem.EmailAddress");
 
  /** @generated */
  final Feature casFeat_localPart;
  /** @generated */
  final int     casFeatCode_localPart;
  /** @generated */ 
  public String getLocalPart(int addr) {
        if (featOkTst && casFeat_localPart == null)
      jcas.throwFeatMissing("localPart", "ru.kfu.itis.issst.ner.typesystem.EmailAddress");
    return ll_cas.ll_getStringValue(addr, casFeatCode_localPart);
  }
  /** @generated */    
  public void setLocalPart(int addr, String v) {
        if (featOkTst && casFeat_localPart == null)
      jcas.throwFeatMissing("localPart", "ru.kfu.itis.issst.ner.typesystem.EmailAddress");
    ll_cas.ll_setStringValue(addr, casFeatCode_localPart, v);}
    
  
 
  /** @generated */
  final Feature casFeat_domainPart;
  /** @generated */
  final int     casFeatCode_domainPart;
  /** @generated */ 
  public String getDomainPart(int addr) {
        if (featOkTst && casFeat_domainPart == null)
      jcas.throwFeatMissing("domainPart", "ru.kfu.itis.issst.ner.typesystem.EmailAddress");
    return ll_cas.ll_getStringValue(addr, casFeatCode_domainPart);
  }
  /** @generated */    
  public void setDomainPart(int addr, String v) {
        if (featOkTst && casFeat_domainPart == null)
      jcas.throwFeatMissing("domainPart", "ru.kfu.itis.issst.ner.typesystem.EmailAddress");
    ll_cas.ll_setStringValue(addr, casFeatCode_domainPart, v);}
    
  
 
  /** @generated */
  final Feature casFeat_normalizedEmail;
  /** @generated */
  final int     casFeatCode_normalizedEmail;
  /** @generated */ 
  public String getNormalizedEmail(int addr) {
        if (featOkTst && casFeat_normalizedEmail == null)
      jcas.throwFeatMissing("normalizedEmail", "ru.kfu.itis.issst.ner.typesystem.EmailAddress");
    return ll_cas.ll_getStringValue(addr, casFeatCode_normalizedEmail);
  }
  /** @generated */    
  public void setNormalizedEmail(int addr, String v) {
        if (featOkTst && casFeat_normalizedEmail == null)
      jcas.throwFeatMissing("normalizedEmail", "ru.kfu.itis.issst.ner.typesystem.EmailAddress");
    ll_cas.ll_setStringValue(addr, casFeatCode_normalizedEmail, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public EmailAddress_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_localPart = jcas.getRequiredFeatureDE(casType, "localPart", "uima.cas.String", featOkTst);
    casFeatCode_localPart  = (null == casFeat_localPart) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_localPart).getCode();

 
    casFeat_domainPart = jcas.getRequiredFeatureDE(casType, "domainPart", "uima.cas.String", featOkTst);
    casFeatCode_domainPart  = (null == casFeat_domainPart) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_domainPart).getCode();

 
    casFeat_normalizedEmail = jcas.getRequiredFeatureDE(casType, "normalizedEmail", "uima.cas.String", featOkTst);
    casFeatCode_normalizedEmail  = (null == casFeat_normalizedEmail) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_normalizedEmail).getCode();

  }
}



    