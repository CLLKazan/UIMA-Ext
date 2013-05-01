

/* First created by JCasGen Wed Feb 27 15:40:38 SAMT 2013 */
package ru.kfu.cll.uima.tokenizer.fstype;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Wed Feb 27 15:40:38 SAMT 2013
 * XML source: /home/vladimir/workspace-git/uima-ext/UIMA-Ext/UIMA.Ext.Term/desc/tokenizer-TypeSystem-complexTypes.xml
 * @generated */
public class Time extends Token {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(Time.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Time() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Time(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Time(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Time(JCas jcas, int begin, int end) {
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
  //* Feature: format

  /** getter for format - gets 
   * @generated */
  public String getFormat() {
    if (Time_Type.featOkTst && ((Time_Type)jcasType).casFeat_format == null)
      jcasType.jcas.throwFeatMissing("format", "ru.kfu.cll.uima.tokenizer.fstype.Time");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Time_Type)jcasType).casFeatCode_format);}
    
  /** setter for format - sets  
   * @generated */
  public void setFormat(String v) {
    if (Time_Type.featOkTst && ((Time_Type)jcasType).casFeat_format == null)
      jcasType.jcas.throwFeatMissing("format", "ru.kfu.cll.uima.tokenizer.fstype.Time");
    jcasType.ll_cas.ll_setStringValue(addr, ((Time_Type)jcasType).casFeatCode_format, v);}    
  }

    