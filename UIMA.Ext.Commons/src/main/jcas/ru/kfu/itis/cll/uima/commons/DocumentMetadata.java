

/* First created by JCasGen Thu Sep 27 23:32:25 MSK 2012 */
package ru.kfu.itis.cll.uima.commons;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Thu Sep 27 23:32:25 MSK 2012
 * XML source: resources/ru/kfu/itis/cll/uima/commons/Commons-TypeSystem.xml
 * @generated */
public class DocumentMetadata extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(DocumentMetadata.class);
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
  protected DocumentMetadata() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public DocumentMetadata(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public DocumentMetadata(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public DocumentMetadata(JCas jcas, int begin, int end) {
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
  //* Feature: sourceUri

  /** getter for sourceUri - gets 
   * @generated */
  public String getSourceUri() {
    if (DocumentMetadata_Type.featOkTst && ((DocumentMetadata_Type)jcasType).casFeat_sourceUri == null)
      jcasType.jcas.throwFeatMissing("sourceUri", "ru.kfu.itis.cll.uima.commons.DocumentMetadata");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DocumentMetadata_Type)jcasType).casFeatCode_sourceUri);}
    
  /** setter for sourceUri - sets  
   * @generated */
  public void setSourceUri(String v) {
    if (DocumentMetadata_Type.featOkTst && ((DocumentMetadata_Type)jcasType).casFeat_sourceUri == null)
      jcasType.jcas.throwFeatMissing("sourceUri", "ru.kfu.itis.cll.uima.commons.DocumentMetadata");
    jcasType.ll_cas.ll_setStringValue(addr, ((DocumentMetadata_Type)jcasType).casFeatCode_sourceUri, v);}    
   
    
  //*--------------*
  //* Feature: offsetInSource

  /** getter for offsetInSource - gets 
   * @generated */
  public long getOffsetInSource() {
    if (DocumentMetadata_Type.featOkTst && ((DocumentMetadata_Type)jcasType).casFeat_offsetInSource == null)
      jcasType.jcas.throwFeatMissing("offsetInSource", "ru.kfu.itis.cll.uima.commons.DocumentMetadata");
    return jcasType.ll_cas.ll_getLongValue(addr, ((DocumentMetadata_Type)jcasType).casFeatCode_offsetInSource);}
    
  /** setter for offsetInSource - sets  
   * @generated */
  public void setOffsetInSource(long v) {
    if (DocumentMetadata_Type.featOkTst && ((DocumentMetadata_Type)jcasType).casFeat_offsetInSource == null)
      jcasType.jcas.throwFeatMissing("offsetInSource", "ru.kfu.itis.cll.uima.commons.DocumentMetadata");
    jcasType.ll_cas.ll_setLongValue(addr, ((DocumentMetadata_Type)jcasType).casFeatCode_offsetInSource, v);}    
   
    
  //*--------------*
  //* Feature: documentSize

  /** getter for documentSize - gets 
   * @generated */
  public long getDocumentSize() {
    if (DocumentMetadata_Type.featOkTst && ((DocumentMetadata_Type)jcasType).casFeat_documentSize == null)
      jcasType.jcas.throwFeatMissing("documentSize", "ru.kfu.itis.cll.uima.commons.DocumentMetadata");
    return jcasType.ll_cas.ll_getLongValue(addr, ((DocumentMetadata_Type)jcasType).casFeatCode_documentSize);}
    
  /** setter for documentSize - sets  
   * @generated */
  public void setDocumentSize(long v) {
    if (DocumentMetadata_Type.featOkTst && ((DocumentMetadata_Type)jcasType).casFeat_documentSize == null)
      jcasType.jcas.throwFeatMissing("documentSize", "ru.kfu.itis.cll.uima.commons.DocumentMetadata");
    jcasType.ll_cas.ll_setLongValue(addr, ((DocumentMetadata_Type)jcasType).casFeatCode_documentSize, v);}    
   
    
  //*--------------*
  //* Feature: startProcessingTime

  /** getter for startProcessingTime - gets 
   * @generated */
  public long getStartProcessingTime() {
    if (DocumentMetadata_Type.featOkTst && ((DocumentMetadata_Type)jcasType).casFeat_startProcessingTime == null)
      jcasType.jcas.throwFeatMissing("startProcessingTime", "ru.kfu.itis.cll.uima.commons.DocumentMetadata");
    return jcasType.ll_cas.ll_getLongValue(addr, ((DocumentMetadata_Type)jcasType).casFeatCode_startProcessingTime);}
    
  /** setter for startProcessingTime - sets  
   * @generated */
  public void setStartProcessingTime(long v) {
    if (DocumentMetadata_Type.featOkTst && ((DocumentMetadata_Type)jcasType).casFeat_startProcessingTime == null)
      jcasType.jcas.throwFeatMissing("startProcessingTime", "ru.kfu.itis.cll.uima.commons.DocumentMetadata");
    jcasType.ll_cas.ll_setLongValue(addr, ((DocumentMetadata_Type)jcasType).casFeatCode_startProcessingTime, v);}    
  }

    