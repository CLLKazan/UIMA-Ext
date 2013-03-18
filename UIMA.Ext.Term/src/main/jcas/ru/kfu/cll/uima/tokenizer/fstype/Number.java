

/* First created by JCasGen Wed Feb 27 15:40:38 SAMT 2013 */
package ru.kfu.cll.uima.tokenizer.fstype;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Wed Feb 27 15:40:38 SAMT 2013
 * XML source: /home/vladimir/workspace-git/uima-ext/UIMA-Ext/UIMA.Ext.Term/desc/tokenizer-TypeSystem-complexTypes.xml
 * @generated */
public class Number extends Token {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(Number.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Number() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Number(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Number(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Number(JCas jcas, int begin, int end) {
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
  //* Feature: sign

  /** getter for sign - gets 
   * @generated */
  public String getSign() {
    if (Number_Type.featOkTst && ((Number_Type)jcasType).casFeat_sign == null)
      jcasType.jcas.throwFeatMissing("sign", "ru.kfu.cll.uima.tokenizer.fstype.Number");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Number_Type)jcasType).casFeatCode_sign);}
    
  /** setter for sign - sets  
   * @generated */
  public void setSign(String v) {
    if (Number_Type.featOkTst && ((Number_Type)jcasType).casFeat_sign == null)
      jcasType.jcas.throwFeatMissing("sign", "ru.kfu.cll.uima.tokenizer.fstype.Number");
    jcasType.ll_cas.ll_setStringValue(addr, ((Number_Type)jcasType).casFeatCode_sign, v);}    
   
    
  //*--------------*
  //* Feature: kind

  /** getter for kind - gets 
   * @generated */
  public String getKind() {
    if (Number_Type.featOkTst && ((Number_Type)jcasType).casFeat_kind == null)
      jcasType.jcas.throwFeatMissing("kind", "ru.kfu.cll.uima.tokenizer.fstype.Number");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Number_Type)jcasType).casFeatCode_kind);}
    
  /** setter for kind - sets  
   * @generated */
  public void setKind(String v) {
    if (Number_Type.featOkTst && ((Number_Type)jcasType).casFeat_kind == null)
      jcasType.jcas.throwFeatMissing("kind", "ru.kfu.cll.uima.tokenizer.fstype.Number");
    jcasType.ll_cas.ll_setStringValue(addr, ((Number_Type)jcasType).casFeatCode_kind, v);}    
  }

    