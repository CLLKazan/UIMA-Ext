

/* First created by JCasGen Tue Feb 05 17:20:29 MSK 2013 */
package com.hp.hplabs.lim2.ie.text.typesystem;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSArray;


/** 
 * Updated by JCasGen Tue Feb 05 18:06:06 MSK 2013
 * XML source: /home/pathfinder/Projects/BRATWorkspace/git/UIMA.Ext.Brat.Integration/desc/an-desc-HL.xml
 * @generated */
public class ParseNode extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(ParseNode.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated  */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected ParseNode() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public ParseNode(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public ParseNode(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public ParseNode(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: parentNode

  /** getter for parentNode - gets 
   * @generated */
  public ParseNode getParentNode() {
    if (ParseNode_Type.featOkTst && ((ParseNode_Type)jcasType).casFeat_parentNode == null)
      jcasType.jcas.throwFeatMissing("parentNode", "com.hp.hplabs.lim2.ie.text.typesystem.ParseNode");
    return (ParseNode)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ParseNode_Type)jcasType).casFeatCode_parentNode)));}
    
  /** setter for parentNode - sets  
   * @generated */
  public void setParentNode(ParseNode v) {
    if (ParseNode_Type.featOkTst && ((ParseNode_Type)jcasType).casFeat_parentNode == null)
      jcasType.jcas.throwFeatMissing("parentNode", "com.hp.hplabs.lim2.ie.text.typesystem.ParseNode");
    jcasType.ll_cas.ll_setRefValue(addr, ((ParseNode_Type)jcasType).casFeatCode_parentNode, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: parseType

  /** getter for parseType - gets Constituent label for this node of the parse.
   * @generated */
  public String getParseType() {
    if (ParseNode_Type.featOkTst && ((ParseNode_Type)jcasType).casFeat_parseType == null)
      jcasType.jcas.throwFeatMissing("parseType", "com.hp.hplabs.lim2.ie.text.typesystem.ParseNode");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ParseNode_Type)jcasType).casFeatCode_parseType);}
    
  /** setter for parseType - sets Constituent label for this node of the parse. 
   * @generated */
  public void setParseType(String v) {
    if (ParseNode_Type.featOkTst && ((ParseNode_Type)jcasType).casFeat_parseType == null)
      jcasType.jcas.throwFeatMissing("parseType", "com.hp.hplabs.lim2.ie.text.typesystem.ParseNode");
    jcasType.ll_cas.ll_setStringValue(addr, ((ParseNode_Type)jcasType).casFeatCode_parseType, v);}    
   
    
  //*--------------*
  //* Feature: childrenNodes

  /** getter for childrenNodes - gets The children nodes of this parse node
   * @generated */
  public FSArray getChildrenNodes() {
    if (ParseNode_Type.featOkTst && ((ParseNode_Type)jcasType).casFeat_childrenNodes == null)
      jcasType.jcas.throwFeatMissing("childrenNodes", "com.hp.hplabs.lim2.ie.text.typesystem.ParseNode");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ParseNode_Type)jcasType).casFeatCode_childrenNodes)));}
    
  /** setter for childrenNodes - sets The children nodes of this parse node 
   * @generated */
  public void setChildrenNodes(FSArray v) {
    if (ParseNode_Type.featOkTst && ((ParseNode_Type)jcasType).casFeat_childrenNodes == null)
      jcasType.jcas.throwFeatMissing("childrenNodes", "com.hp.hplabs.lim2.ie.text.typesystem.ParseNode");
    jcasType.ll_cas.ll_setRefValue(addr, ((ParseNode_Type)jcasType).casFeatCode_childrenNodes, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for childrenNodes - gets an indexed value - The children nodes of this parse node
   * @generated */
  public ParseNode getChildrenNodes(int i) {
    if (ParseNode_Type.featOkTst && ((ParseNode_Type)jcasType).casFeat_childrenNodes == null)
      jcasType.jcas.throwFeatMissing("childrenNodes", "com.hp.hplabs.lim2.ie.text.typesystem.ParseNode");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((ParseNode_Type)jcasType).casFeatCode_childrenNodes), i);
    return (ParseNode)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((ParseNode_Type)jcasType).casFeatCode_childrenNodes), i)));}

  /** indexed setter for childrenNodes - sets an indexed value - The children nodes of this parse node
   * @generated */
  public void setChildrenNodes(int i, ParseNode v) { 
    if (ParseNode_Type.featOkTst && ((ParseNode_Type)jcasType).casFeat_childrenNodes == null)
      jcasType.jcas.throwFeatMissing("childrenNodes", "com.hp.hplabs.lim2.ie.text.typesystem.ParseNode");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((ParseNode_Type)jcasType).casFeatCode_childrenNodes), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((ParseNode_Type)jcasType).casFeatCode_childrenNodes), i, jcasType.ll_cas.ll_getFSRef(v));}
  }

    