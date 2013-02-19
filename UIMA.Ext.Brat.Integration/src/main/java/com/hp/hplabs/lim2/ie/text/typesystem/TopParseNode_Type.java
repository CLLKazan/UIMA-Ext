
/* First created by JCasGen Tue Feb 05 17:20:29 MSK 2013 */
package com.hp.hplabs.lim2.ie.text.typesystem;

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
 * Updated by JCasGen Tue Feb 05 18:06:06 MSK 2013
 * @generated */
public class TopParseNode_Type extends ParseNode_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (TopParseNode_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = TopParseNode_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new TopParseNode(addr, TopParseNode_Type.this);
  			   TopParseNode_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new TopParseNode(addr, TopParseNode_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = TopParseNode.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("com.hp.hplabs.lim2.ie.text.typesystem.TopParseNode");
 
  /** @generated */
  final Feature casFeat_parseView;
  /** @generated */
  final int     casFeatCode_parseView;
  /** @generated */ 
  public String getParseView(int addr) {
        if (featOkTst && casFeat_parseView == null)
      jcas.throwFeatMissing("parseView", "com.hp.hplabs.lim2.ie.text.typesystem.TopParseNode");
    return ll_cas.ll_getStringValue(addr, casFeatCode_parseView);
  }
  /** @generated */    
  public void setParseView(int addr, String v) {
        if (featOkTst && casFeat_parseView == null)
      jcas.throwFeatMissing("parseView", "com.hp.hplabs.lim2.ie.text.typesystem.TopParseNode");
    ll_cas.ll_setStringValue(addr, casFeatCode_parseView, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public TopParseNode_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_parseView = jcas.getRequiredFeatureDE(casType, "parseView", "uima.cas.String", featOkTst);
    casFeatCode_parseView  = (null == casFeat_parseView) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_parseView).getCode();

  }
}



    