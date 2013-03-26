

/* First created by JCasGen Tue Mar 26 13:55:53 SAMT 2013 */
package ru.kfu.cll.uima.tokenizer.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Tue Mar 26 13:55:53 SAMT 2013
 * XML source: /home/fsqcds/idea-projects/UIMA-Ext/UIMA.Ext.Tokenizer/src/main/resources/ru/kfu/cll/uima/tokenizer/jflex-tokenizer-ts.xml
 * @generated */
public class Abbrevation extends Token {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Abbrevation.class);
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
  protected Abbrevation() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Abbrevation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Abbrevation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Abbrevation(JCas jcas, int begin, int end) {
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
  //* Feature: Language

  /** getter for Language - gets 
   * @generated */
  public String getLanguage() {
    if (Abbrevation_Type.featOkTst && ((Abbrevation_Type)jcasType).casFeat_Language == null)
      jcasType.jcas.throwFeatMissing("Language", "ru.kfu.cll.uima.tokenizer.types.Abbrevation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Abbrevation_Type)jcasType).casFeatCode_Language);}
    
  /** setter for Language - sets  
   * @generated */
  public void setLanguage(String v) {
    if (Abbrevation_Type.featOkTst && ((Abbrevation_Type)jcasType).casFeat_Language == null)
      jcasType.jcas.throwFeatMissing("Language", "ru.kfu.cll.uima.tokenizer.types.Abbrevation");
    jcasType.ll_cas.ll_setStringValue(addr, ((Abbrevation_Type)jcasType).casFeatCode_Language, v);}    
  }

    