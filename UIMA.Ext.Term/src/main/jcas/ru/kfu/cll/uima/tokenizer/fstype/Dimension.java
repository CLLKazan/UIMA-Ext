

/* First created by JCasGen Wed Feb 27 15:40:38 SAMT 2013 */
package ru.kfu.cll.uima.tokenizer.fstype;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Wed Feb 27 15:40:38 SAMT 2013
 * XML source: /home/vladimir/workspace-git/uima-ext/UIMA-Ext/UIMA.Ext.Term/desc/tokenizer-TypeSystem-complexTypes.xml
 * @generated */
public class Dimension extends Token {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(Dimension.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Dimension() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Dimension(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Dimension(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Dimension(JCas jcas, int begin, int end) {
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
  //* Feature: value

  /** getter for value - gets 
   * @generated */
  public float getValue() {
    if (Dimension_Type.featOkTst && ((Dimension_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "ru.kfu.cll.uima.tokenizer.fstype.Dimension");
    return jcasType.ll_cas.ll_getFloatValue(addr, ((Dimension_Type)jcasType).casFeatCode_value);}
    
  /** setter for value - sets  
   * @generated */
  public void setValue(float v) {
    if (Dimension_Type.featOkTst && ((Dimension_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "ru.kfu.cll.uima.tokenizer.fstype.Dimension");
    jcasType.ll_cas.ll_setFloatValue(addr, ((Dimension_Type)jcasType).casFeatCode_value, v);}    
   
    
  //*--------------*
  //* Feature: unit

  /** getter for unit - gets 
   * @generated */
  public String getUnit() {
    if (Dimension_Type.featOkTst && ((Dimension_Type)jcasType).casFeat_unit == null)
      jcasType.jcas.throwFeatMissing("unit", "ru.kfu.cll.uima.tokenizer.fstype.Dimension");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Dimension_Type)jcasType).casFeatCode_unit);}
    
  /** setter for unit - sets  
   * @generated */
  public void setUnit(String v) {
    if (Dimension_Type.featOkTst && ((Dimension_Type)jcasType).casFeat_unit == null)
      jcasType.jcas.throwFeatMissing("unit", "ru.kfu.cll.uima.tokenizer.fstype.Dimension");
    jcasType.ll_cas.ll_setStringValue(addr, ((Dimension_Type)jcasType).casFeatCode_unit, v);}    
  }

    