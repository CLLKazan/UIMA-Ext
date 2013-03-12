
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
public class ParseNode_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (ParseNode_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = ParseNode_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new ParseNode(addr, ParseNode_Type.this);
  			   ParseNode_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new ParseNode(addr, ParseNode_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = ParseNode.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ru.kfu.itis.issst.ner.typesystem.ParseNode");
 
  /** @generated */
  final Feature casFeat_parentNode;
  /** @generated */
  final int     casFeatCode_parentNode;
  /** @generated */ 
  public int getParentNode(int addr) {
        if (featOkTst && casFeat_parentNode == null)
      jcas.throwFeatMissing("parentNode", "ru.kfu.itis.issst.ner.typesystem.ParseNode");
    return ll_cas.ll_getRefValue(addr, casFeatCode_parentNode);
  }
  /** @generated */    
  public void setParentNode(int addr, int v) {
        if (featOkTst && casFeat_parentNode == null)
      jcas.throwFeatMissing("parentNode", "ru.kfu.itis.issst.ner.typesystem.ParseNode");
    ll_cas.ll_setRefValue(addr, casFeatCode_parentNode, v);}
    
  
 
  /** @generated */
  final Feature casFeat_parseType;
  /** @generated */
  final int     casFeatCode_parseType;
  /** @generated */ 
  public String getParseType(int addr) {
        if (featOkTst && casFeat_parseType == null)
      jcas.throwFeatMissing("parseType", "ru.kfu.itis.issst.ner.typesystem.ParseNode");
    return ll_cas.ll_getStringValue(addr, casFeatCode_parseType);
  }
  /** @generated */    
  public void setParseType(int addr, String v) {
        if (featOkTst && casFeat_parseType == null)
      jcas.throwFeatMissing("parseType", "ru.kfu.itis.issst.ner.typesystem.ParseNode");
    ll_cas.ll_setStringValue(addr, casFeatCode_parseType, v);}
    
  
 
  /** @generated */
  final Feature casFeat_childrenNodes;
  /** @generated */
  final int     casFeatCode_childrenNodes;
  /** @generated */ 
  public int getChildrenNodes(int addr) {
        if (featOkTst && casFeat_childrenNodes == null)
      jcas.throwFeatMissing("childrenNodes", "ru.kfu.itis.issst.ner.typesystem.ParseNode");
    return ll_cas.ll_getRefValue(addr, casFeatCode_childrenNodes);
  }
  /** @generated */    
  public void setChildrenNodes(int addr, int v) {
        if (featOkTst && casFeat_childrenNodes == null)
      jcas.throwFeatMissing("childrenNodes", "ru.kfu.itis.issst.ner.typesystem.ParseNode");
    ll_cas.ll_setRefValue(addr, casFeatCode_childrenNodes, v);}
    
   /** @generated */
  public int getChildrenNodes(int addr, int i) {
        if (featOkTst && casFeat_childrenNodes == null)
      jcas.throwFeatMissing("childrenNodes", "ru.kfu.itis.issst.ner.typesystem.ParseNode");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_childrenNodes), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_childrenNodes), i);
	return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_childrenNodes), i);
  }
   
  /** @generated */ 
  public void setChildrenNodes(int addr, int i, int v) {
        if (featOkTst && casFeat_childrenNodes == null)
      jcas.throwFeatMissing("childrenNodes", "ru.kfu.itis.issst.ner.typesystem.ParseNode");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_childrenNodes), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_childrenNodes), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_childrenNodes), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public ParseNode_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_parentNode = jcas.getRequiredFeatureDE(casType, "parentNode", "ru.kfu.itis.issst.ner.typesystem.ParseNode", featOkTst);
    casFeatCode_parentNode  = (null == casFeat_parentNode) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_parentNode).getCode();

 
    casFeat_parseType = jcas.getRequiredFeatureDE(casType, "parseType", "uima.cas.String", featOkTst);
    casFeatCode_parseType  = (null == casFeat_parseType) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_parseType).getCode();

 
    casFeat_childrenNodes = jcas.getRequiredFeatureDE(casType, "childrenNodes", "uima.cas.FSArray", featOkTst);
    casFeatCode_childrenNodes  = (null == casFeat_childrenNodes) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_childrenNodes).getCode();

  }
}



    