

/* First created by JCasGen Tue Feb 05 17:20:29 MSK 2013 */
package com.hp.hplabs.lim2.ie.text.typesystem;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Tue Feb 05 18:06:06 MSK 2013
 * XML source: /home/pathfinder/Projects/BRATWorkspace/git/UIMA.Ext.Brat.Integration/desc/an-desc-HL.xml
 * @generated */
public class Token extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Token.class);
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
  protected Token() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Token(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Token(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Token(JCas jcas, int begin, int end) {
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
  //* Feature: posTag

  /** getter for posTag - gets Contains part-of-speech of a corresponding token
   * @generated */
  public String getPosTag() {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_posTag == null)
      jcasType.jcas.throwFeatMissing("posTag", "com.hp.hplabs.lim2.ie.text.typesystem.Token");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Token_Type)jcasType).casFeatCode_posTag);}
    
  /** setter for posTag - sets Contains part-of-speech of a corresponding token 
   * @generated */
  public void setPosTag(String v) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_posTag == null)
      jcasType.jcas.throwFeatMissing("posTag", "com.hp.hplabs.lim2.ie.text.typesystem.Token");
    jcasType.ll_cas.ll_setStringValue(addr, ((Token_Type)jcasType).casFeatCode_posTag, v);}    
   
    
  //*--------------*
  //* Feature: SemClass

  /** getter for SemClass - gets semantic class of token
   * @generated */
  public String getSemClass() {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_SemClass == null)
      jcasType.jcas.throwFeatMissing("SemClass", "com.hp.hplabs.lim2.ie.text.typesystem.Token");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Token_Type)jcasType).casFeatCode_SemClass);}
    
  /** setter for SemClass - sets semantic class of token 
   * @generated */
  public void setSemClass(String v) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_SemClass == null)
      jcasType.jcas.throwFeatMissing("SemClass", "com.hp.hplabs.lim2.ie.text.typesystem.Token");
    jcasType.ll_cas.ll_setStringValue(addr, ((Token_Type)jcasType).casFeatCode_SemClass, v);}    
   
    
  //*--------------*
  //* Feature: POS

  /** getter for POS - gets Part of SPeech of term to which this
								token is a part
   * @generated */
  public String getPOS() {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_POS == null)
      jcasType.jcas.throwFeatMissing("POS", "com.hp.hplabs.lim2.ie.text.typesystem.Token");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Token_Type)jcasType).casFeatCode_POS);}
    
  /** setter for POS - sets Part of SPeech of term to which this
								token is a part 
   * @generated */
  public void setPOS(String v) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_POS == null)
      jcasType.jcas.throwFeatMissing("POS", "com.hp.hplabs.lim2.ie.text.typesystem.Token");
    jcasType.ll_cas.ll_setStringValue(addr, ((Token_Type)jcasType).casFeatCode_POS, v);}    
   
    
  //*--------------*
  //* Feature: frost_TokenType

  /** getter for frost_TokenType - gets 
   * @generated */
  public int getFrost_TokenType() {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_frost_TokenType == null)
      jcasType.jcas.throwFeatMissing("frost_TokenType", "com.hp.hplabs.lim2.ie.text.typesystem.Token");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Token_Type)jcasType).casFeatCode_frost_TokenType);}
    
  /** setter for frost_TokenType - sets  
   * @generated */
  public void setFrost_TokenType(int v) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_frost_TokenType == null)
      jcasType.jcas.throwFeatMissing("frost_TokenType", "com.hp.hplabs.lim2.ie.text.typesystem.Token");
    jcasType.ll_cas.ll_setIntValue(addr, ((Token_Type)jcasType).casFeatCode_frost_TokenType, v);}    
   
    
  //*--------------*
  //* Feature: text

  /** getter for text - gets 
   * @generated */
  public String getText() {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "com.hp.hplabs.lim2.ie.text.typesystem.Token");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Token_Type)jcasType).casFeatCode_text);}
    
  /** setter for text - sets  
   * @generated */
  public void setText(String v) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "com.hp.hplabs.lim2.ie.text.typesystem.Token");
    jcasType.ll_cas.ll_setStringValue(addr, ((Token_Type)jcasType).casFeatCode_text, v);}    
   
    
  //*--------------*
  //* Feature: stemForm

  /** getter for stemForm - gets Stemmed form of a token.
   * @generated */
  public String getStemForm() {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_stemForm == null)
      jcasType.jcas.throwFeatMissing("stemForm", "com.hp.hplabs.lim2.ie.text.typesystem.Token");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Token_Type)jcasType).casFeatCode_stemForm);}
    
  /** setter for stemForm - sets Stemmed form of a token. 
   * @generated */
  public void setStemForm(String v) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_stemForm == null)
      jcasType.jcas.throwFeatMissing("stemForm", "com.hp.hplabs.lim2.ie.text.typesystem.Token");
    jcasType.ll_cas.ll_setStringValue(addr, ((Token_Type)jcasType).casFeatCode_stemForm, v);}    
  }

    