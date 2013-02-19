

/* First created by JCasGen Tue Feb 05 17:20:29 MSK 2013 */
package com.hp.hplabs.lim2.ie.text.typesystem;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Tue Feb 05 18:06:06 MSK 2013
 * XML source: /home/pathfinder/Projects/BRATWorkspace/git/UIMA.Ext.Brat.Integration/desc/an-desc-HL.xml
 * @generated */
public class TopParseNode extends ParseNode {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(TopParseNode.class);
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
  protected TopParseNode() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public TopParseNode(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public TopParseNode(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public TopParseNode(JCas jcas, int begin, int end) {
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
  //* Feature: parseView

  /** getter for parseView - gets 
   * @generated */
  public String getParseView() {
    if (TopParseNode_Type.featOkTst && ((TopParseNode_Type)jcasType).casFeat_parseView == null)
      jcasType.jcas.throwFeatMissing("parseView", "com.hp.hplabs.lim2.ie.text.typesystem.TopParseNode");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TopParseNode_Type)jcasType).casFeatCode_parseView);}
    
  /** setter for parseView - sets  
   * @generated */
  public void setParseView(String v) {
    if (TopParseNode_Type.featOkTst && ((TopParseNode_Type)jcasType).casFeat_parseView == null)
      jcasType.jcas.throwFeatMissing("parseView", "com.hp.hplabs.lim2.ie.text.typesystem.TopParseNode");
    jcasType.ll_cas.ll_setStringValue(addr, ((TopParseNode_Type)jcasType).casFeatCode_parseView, v);}    
  }

    