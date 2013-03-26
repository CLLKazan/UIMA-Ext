

/* First created by JCasGen Tue Mar 26 13:55:53 SAMT 2013 */
package ru.kfu.cll.uima.tokenizer.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Tue Mar 26 13:55:53 SAMT 2013
 * XML source: /home/fsqcds/idea-projects/UIMA-Ext/UIMA.Ext.Tokenizer/src/main/resources/ru/kfu/cll/uima/tokenizer/jflex-tokenizer-ts.xml
 * @generated */
public class Letters extends Token {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Letters.class);
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
  protected Letters() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Letters(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Letters(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Letters(JCas jcas, int begin, int end) {
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
  //* Feature: LetterCase

  /** getter for LetterCase - gets 
   * @generated */
  public String getLetterCase() {
    if (Letters_Type.featOkTst && ((Letters_Type)jcasType).casFeat_LetterCase == null)
      jcasType.jcas.throwFeatMissing("LetterCase", "ru.kfu.cll.uima.tokenizer.types.Letters");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Letters_Type)jcasType).casFeatCode_LetterCase);}
    
  /** setter for LetterCase - sets  
   * @generated */
  public void setLetterCase(String v) {
    if (Letters_Type.featOkTst && ((Letters_Type)jcasType).casFeat_LetterCase == null)
      jcasType.jcas.throwFeatMissing("LetterCase", "ru.kfu.cll.uima.tokenizer.types.Letters");
    jcasType.ll_cas.ll_setStringValue(addr, ((Letters_Type)jcasType).casFeatCode_LetterCase, v);}    
   
    
  //*--------------*
  //* Feature: Language

  /** getter for Language - gets 
   * @generated */
  public String getLanguage() {
    if (Letters_Type.featOkTst && ((Letters_Type)jcasType).casFeat_Language == null)
      jcasType.jcas.throwFeatMissing("Language", "ru.kfu.cll.uima.tokenizer.types.Letters");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Letters_Type)jcasType).casFeatCode_Language);}
    
  /** setter for Language - sets  
   * @generated */
  public void setLanguage(String v) {
    if (Letters_Type.featOkTst && ((Letters_Type)jcasType).casFeat_Language == null)
      jcasType.jcas.throwFeatMissing("Language", "ru.kfu.cll.uima.tokenizer.types.Letters");
    jcasType.ll_cas.ll_setStringValue(addr, ((Letters_Type)jcasType).casFeatCode_Language, v);}    
  }

    