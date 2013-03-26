

/* First created by JCasGen Tue Mar 26 13:55:53 SAMT 2013 */
package ru.kfu.cll.uima.tokenizer.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Tue Mar 26 13:55:53 SAMT 2013
 * XML source: /home/fsqcds/idea-projects/UIMA-Ext/UIMA.Ext.Tokenizer/src/main/resources/ru/kfu/cll/uima/tokenizer/jflex-tokenizer-ts.xml
 * @generated */
public class Date extends Token {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Date.class);
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
  protected Date() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Date(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Date(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Date(JCas jcas, int begin, int end) {
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
  //* Feature: Year

  /** getter for Year - gets 
   * @generated */
  public String getYear() {
    if (Date_Type.featOkTst && ((Date_Type)jcasType).casFeat_Year == null)
      jcasType.jcas.throwFeatMissing("Year", "ru.kfu.cll.uima.tokenizer.types.Date");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Date_Type)jcasType).casFeatCode_Year);}
    
  /** setter for Year - sets  
   * @generated */
  public void setYear(String v) {
    if (Date_Type.featOkTst && ((Date_Type)jcasType).casFeat_Year == null)
      jcasType.jcas.throwFeatMissing("Year", "ru.kfu.cll.uima.tokenizer.types.Date");
    jcasType.ll_cas.ll_setStringValue(addr, ((Date_Type)jcasType).casFeatCode_Year, v);}    
   
    
  //*--------------*
  //* Feature: Mounth

  /** getter for Mounth - gets 
   * @generated */
  public String getMounth() {
    if (Date_Type.featOkTst && ((Date_Type)jcasType).casFeat_Mounth == null)
      jcasType.jcas.throwFeatMissing("Mounth", "ru.kfu.cll.uima.tokenizer.types.Date");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Date_Type)jcasType).casFeatCode_Mounth);}
    
  /** setter for Mounth - sets  
   * @generated */
  public void setMounth(String v) {
    if (Date_Type.featOkTst && ((Date_Type)jcasType).casFeat_Mounth == null)
      jcasType.jcas.throwFeatMissing("Mounth", "ru.kfu.cll.uima.tokenizer.types.Date");
    jcasType.ll_cas.ll_setStringValue(addr, ((Date_Type)jcasType).casFeatCode_Mounth, v);}    
   
    
  //*--------------*
  //* Feature: Day

  /** getter for Day - gets 
   * @generated */
  public String getDay() {
    if (Date_Type.featOkTst && ((Date_Type)jcasType).casFeat_Day == null)
      jcasType.jcas.throwFeatMissing("Day", "ru.kfu.cll.uima.tokenizer.types.Date");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Date_Type)jcasType).casFeatCode_Day);}
    
  /** setter for Day - sets  
   * @generated */
  public void setDay(String v) {
    if (Date_Type.featOkTst && ((Date_Type)jcasType).casFeat_Day == null)
      jcasType.jcas.throwFeatMissing("Day", "ru.kfu.cll.uima.tokenizer.types.Date");
    jcasType.ll_cas.ll_setStringValue(addr, ((Date_Type)jcasType).casFeatCode_Day, v);}    
  }

    