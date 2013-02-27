
/* First created by JCasGen Wed Feb 27 15:40:38 SAMT 2013 */
package ru.kfu.cll.uima.tokenizer.fstype;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;

/** 
 * Updated by JCasGen Wed Feb 27 15:40:38 SAMT 2013
 * @generated */
public class WebDomain_Type extends Token_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (WebDomain_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = WebDomain_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new WebDomain(addr, WebDomain_Type.this);
  			   WebDomain_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new WebDomain(addr, WebDomain_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = WebDomain.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ru.kfu.cll.uima.tokenizer.fstype.WebDomain");



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public WebDomain_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

  }
}



    