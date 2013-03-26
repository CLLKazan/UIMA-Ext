

/* First created by JCasGen Tue Mar 26 20:01:13 SAMT 2013 */
package ru.kfu.cll.uima.segmentation.fstype;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Tue Mar 26 20:01:13 SAMT 2013
 * XML source: /home/fsqcds/idea-projects/UIMA-Ext/UIMA.Ext.Tokenizer/src/main/resources/ru/kfu/cll/uima/segmentation/segmentation-TypeSystem.xml
 * @generated */
public class PMSegment extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(PMSegment.class);
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
  protected PMSegment() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public PMSegment(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public PMSegment(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public PMSegment(JCas jcas, int begin, int end) {
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
  //* Feature: contentBegin

  /** getter for contentBegin - gets 
   * @generated */
  public int getContentBegin() {
    if (PMSegment_Type.featOkTst && ((PMSegment_Type)jcasType).casFeat_contentBegin == null)
      jcasType.jcas.throwFeatMissing("contentBegin", "ru.kfu.cll.uima.segmentation.fstype.PMSegment");
    return jcasType.ll_cas.ll_getIntValue(addr, ((PMSegment_Type)jcasType).casFeatCode_contentBegin);}
    
  /** setter for contentBegin - sets  
   * @generated */
  public void setContentBegin(int v) {
    if (PMSegment_Type.featOkTst && ((PMSegment_Type)jcasType).casFeat_contentBegin == null)
      jcasType.jcas.throwFeatMissing("contentBegin", "ru.kfu.cll.uima.segmentation.fstype.PMSegment");
    jcasType.ll_cas.ll_setIntValue(addr, ((PMSegment_Type)jcasType).casFeatCode_contentBegin, v);}    
   
    
  //*--------------*
  //* Feature: contentEnd

  /** getter for contentEnd - gets 
   * @generated */
  public int getContentEnd() {
    if (PMSegment_Type.featOkTst && ((PMSegment_Type)jcasType).casFeat_contentEnd == null)
      jcasType.jcas.throwFeatMissing("contentEnd", "ru.kfu.cll.uima.segmentation.fstype.PMSegment");
    return jcasType.ll_cas.ll_getIntValue(addr, ((PMSegment_Type)jcasType).casFeatCode_contentEnd);}
    
  /** setter for contentEnd - sets  
   * @generated */
  public void setContentEnd(int v) {
    if (PMSegment_Type.featOkTst && ((PMSegment_Type)jcasType).casFeat_contentEnd == null)
      jcasType.jcas.throwFeatMissing("contentEnd", "ru.kfu.cll.uima.segmentation.fstype.PMSegment");
    jcasType.ll_cas.ll_setIntValue(addr, ((PMSegment_Type)jcasType).casFeatCode_contentEnd, v);}    
   
    
  //*--------------*
  //* Feature: parentSegment

  /** getter for parentSegment - gets 
   * @generated */
  public Annotation getParentSegment() {
    if (PMSegment_Type.featOkTst && ((PMSegment_Type)jcasType).casFeat_parentSegment == null)
      jcasType.jcas.throwFeatMissing("parentSegment", "ru.kfu.cll.uima.segmentation.fstype.PMSegment");
    return (Annotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((PMSegment_Type)jcasType).casFeatCode_parentSegment)));}
    
  /** setter for parentSegment - sets  
   * @generated */
  public void setParentSegment(Annotation v) {
    if (PMSegment_Type.featOkTst && ((PMSegment_Type)jcasType).casFeat_parentSegment == null)
      jcasType.jcas.throwFeatMissing("parentSegment", "ru.kfu.cll.uima.segmentation.fstype.PMSegment");
    jcasType.ll_cas.ll_setRefValue(addr, ((PMSegment_Type)jcasType).casFeatCode_parentSegment, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    