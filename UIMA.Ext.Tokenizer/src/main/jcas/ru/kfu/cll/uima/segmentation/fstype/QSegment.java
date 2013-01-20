

/* First created by JCasGen Sun Jan 20 15:26:39 SAMT 2013 */
package ru.kfu.cll.uima.segmentation.fstype;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Sun Jan 20 15:26:39 SAMT 2013
 * XML source: /home/vladimir/workspace-git/uima-ext/UIMA-Ext/UIMA.Ext.Tokenizer/src/main/resources/ru/kfu/cll/uima/segmentation/segmentation-TypeSystem.xml
 * @generated */
public class QSegment extends Annotation {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(QSegment.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected QSegment() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public QSegment(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public QSegment(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public QSegment(JCas jcas, int begin, int end) {
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
    if (QSegment_Type.featOkTst && ((QSegment_Type)jcasType).casFeat_contentBegin == null)
      jcasType.jcas.throwFeatMissing("contentBegin", "ru.kfu.cll.uima.segmentation.fstype.QSegment");
    return jcasType.ll_cas.ll_getIntValue(addr, ((QSegment_Type)jcasType).casFeatCode_contentBegin);}
    
  /** setter for contentBegin - sets  
   * @generated */
  public void setContentBegin(int v) {
    if (QSegment_Type.featOkTst && ((QSegment_Type)jcasType).casFeat_contentBegin == null)
      jcasType.jcas.throwFeatMissing("contentBegin", "ru.kfu.cll.uima.segmentation.fstype.QSegment");
    jcasType.ll_cas.ll_setIntValue(addr, ((QSegment_Type)jcasType).casFeatCode_contentBegin, v);}    
   
    
  //*--------------*
  //* Feature: contentEnd

  /** getter for contentEnd - gets 
   * @generated */
  public int getContentEnd() {
    if (QSegment_Type.featOkTst && ((QSegment_Type)jcasType).casFeat_contentEnd == null)
      jcasType.jcas.throwFeatMissing("contentEnd", "ru.kfu.cll.uima.segmentation.fstype.QSegment");
    return jcasType.ll_cas.ll_getIntValue(addr, ((QSegment_Type)jcasType).casFeatCode_contentEnd);}
    
  /** setter for contentEnd - sets  
   * @generated */
  public void setContentEnd(int v) {
    if (QSegment_Type.featOkTst && ((QSegment_Type)jcasType).casFeat_contentEnd == null)
      jcasType.jcas.throwFeatMissing("contentEnd", "ru.kfu.cll.uima.segmentation.fstype.QSegment");
    jcasType.ll_cas.ll_setIntValue(addr, ((QSegment_Type)jcasType).casFeatCode_contentEnd, v);}    
   
    
  //*--------------*
  //* Feature: parentSegment

  /** getter for parentSegment - gets 
   * @generated */
  public Annotation getParentSegment() {
    if (QSegment_Type.featOkTst && ((QSegment_Type)jcasType).casFeat_parentSegment == null)
      jcasType.jcas.throwFeatMissing("parentSegment", "ru.kfu.cll.uima.segmentation.fstype.QSegment");
    return (Annotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((QSegment_Type)jcasType).casFeatCode_parentSegment)));}
    
  /** setter for parentSegment - sets  
   * @generated */
  public void setParentSegment(Annotation v) {
    if (QSegment_Type.featOkTst && ((QSegment_Type)jcasType).casFeat_parentSegment == null)
      jcasType.jcas.throwFeatMissing("parentSegment", "ru.kfu.cll.uima.segmentation.fstype.QSegment");
    jcasType.ll_cas.ll_setRefValue(addr, ((QSegment_Type)jcasType).casFeatCode_parentSegment, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    