

/* First created by JCasGen Wed Feb 27 15:40:38 SAMT 2013 */
package ru.kfu.cll.uima.tokenizer.fstype;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Wed Feb 27 15:40:38 SAMT 2013
 * XML source: /home/vladimir/workspace-git/uima-ext/UIMA-Ext/UIMA.Ext.Term/desc/tokenizer-TypeSystem-complexTypes.xml
 * @generated */
public class Word extends Token {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(Word.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Word() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Word(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Word(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Word(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {}
     
 
    
  //*--------------*
  //* Feature: language

  /** getter for language - gets 
   * @generated */
  public String getLanguage() {
    if (Word_Type.featOkTst && ((Word_Type)jcasType).casFeat_language == null)
      jcasType.jcas.throwFeatMissing("language", "ru.kfu.cll.uima.tokenizer.fstype.Word");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Word_Type)jcasType).casFeatCode_language);}
    
  /** setter for language - sets  
   * @generated */
  public void setLanguage(String v) {
    if (Word_Type.featOkTst && ((Word_Type)jcasType).casFeat_language == null)
      jcasType.jcas.throwFeatMissing("language", "ru.kfu.cll.uima.tokenizer.fstype.Word");
    jcasType.ll_cas.ll_setStringValue(addr, ((Word_Type)jcasType).casFeatCode_language, v);}    
   
    
  //*--------------*
  //* Feature: case

  /** getter for case - gets 
   * @generated */
  public String getCase() {
    if (Word_Type.featOkTst && ((Word_Type)jcasType).casFeat_case == null)
      jcasType.jcas.throwFeatMissing("case", "ru.kfu.cll.uima.tokenizer.fstype.Word");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Word_Type)jcasType).casFeatCode_case);}
    
  /** setter for case - sets  
   * @generated */
  public void setCase(String v) {
    if (Word_Type.featOkTst && ((Word_Type)jcasType).casFeat_case == null)
      jcasType.jcas.throwFeatMissing("case", "ru.kfu.cll.uima.tokenizer.fstype.Word");
    jcasType.ll_cas.ll_setStringValue(addr, ((Word_Type)jcasType).casFeatCode_case, v);}    
   
    
  //*--------------*
  //* Feature: norm

  /** getter for norm - gets 
   * @generated */
  public String getNorm() {
    if (Word_Type.featOkTst && ((Word_Type)jcasType).casFeat_norm == null)
      jcasType.jcas.throwFeatMissing("norm", "ru.kfu.cll.uima.tokenizer.fstype.Word");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Word_Type)jcasType).casFeatCode_norm);}
    
  /** setter for norm - sets  
   * @generated */
  public void setNorm(String v) {
    if (Word_Type.featOkTst && ((Word_Type)jcasType).casFeat_norm == null)
      jcasType.jcas.throwFeatMissing("norm", "ru.kfu.cll.uima.tokenizer.fstype.Word");
    jcasType.ll_cas.ll_setStringValue(addr, ((Word_Type)jcasType).casFeatCode_norm, v);}    
   
    
  //*--------------*
  //* Feature: isCompound

  /** getter for isCompound - gets 
   * @generated */
  public String getIsCompound() {
    if (Word_Type.featOkTst && ((Word_Type)jcasType).casFeat_isCompound == null)
      jcasType.jcas.throwFeatMissing("isCompound", "ru.kfu.cll.uima.tokenizer.fstype.Word");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Word_Type)jcasType).casFeatCode_isCompound);}
    
  /** setter for isCompound - sets  
   * @generated */
  public void setIsCompound(String v) {
    if (Word_Type.featOkTst && ((Word_Type)jcasType).casFeat_isCompound == null)
      jcasType.jcas.throwFeatMissing("isCompound", "ru.kfu.cll.uima.tokenizer.fstype.Word");
    jcasType.ll_cas.ll_setStringValue(addr, ((Word_Type)jcasType).casFeatCode_isCompound, v);}    
  }

    