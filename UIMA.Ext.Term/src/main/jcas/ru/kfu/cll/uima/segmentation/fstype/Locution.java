

/* First created by JCasGen Wed Feb 27 15:40:38 SAMT 2013 */
package ru.kfu.cll.uima.segmentation.fstype;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Wed Feb 27 15:40:38 SAMT 2013
 * XML source: /home/vladimir/workspace-git/uima-ext/UIMA-Ext/UIMA.Ext.Term/desc/tokenizer-TypeSystem-complexTypes.xml
 * @generated */
public class Locution extends Annotation {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(Locution.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Locution() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Locution(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Locution(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Locution(JCas jcas, int begin, int end) {
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
  //* Feature: contentBegin

  /** getter for contentBegin - gets 
   * @generated */
  public int getContentBegin() {
    if (Locution_Type.featOkTst && ((Locution_Type)jcasType).casFeat_contentBegin == null)
      jcasType.jcas.throwFeatMissing("contentBegin", "ru.kfu.cll.uima.segmentation.fstype.Locution");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Locution_Type)jcasType).casFeatCode_contentBegin);}
    
  /** setter for contentBegin - sets  
   * @generated */
  public void setContentBegin(int v) {
    if (Locution_Type.featOkTst && ((Locution_Type)jcasType).casFeat_contentBegin == null)
      jcasType.jcas.throwFeatMissing("contentBegin", "ru.kfu.cll.uima.segmentation.fstype.Locution");
    jcasType.ll_cas.ll_setIntValue(addr, ((Locution_Type)jcasType).casFeatCode_contentBegin, v);}    
   
    
  //*--------------*
  //* Feature: contentEnd

  /** getter for contentEnd - gets 
   * @generated */
  public int getContentEnd() {
    if (Locution_Type.featOkTst && ((Locution_Type)jcasType).casFeat_contentEnd == null)
      jcasType.jcas.throwFeatMissing("contentEnd", "ru.kfu.cll.uima.segmentation.fstype.Locution");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Locution_Type)jcasType).casFeatCode_contentEnd);}
    
  /** setter for contentEnd - sets  
   * @generated */
  public void setContentEnd(int v) {
    if (Locution_Type.featOkTst && ((Locution_Type)jcasType).casFeat_contentEnd == null)
      jcasType.jcas.throwFeatMissing("contentEnd", "ru.kfu.cll.uima.segmentation.fstype.Locution");
    jcasType.ll_cas.ll_setIntValue(addr, ((Locution_Type)jcasType).casFeatCode_contentEnd, v);}    
  }

    