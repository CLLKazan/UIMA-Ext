

/* First created by JCasGen Sun Feb 24 14:06:58 SAMT 2013 */
package ru.kfu.itis.cll.uima.commons;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Sun Feb 24 14:06:58 SAMT 2013
 * XML source: /home/vladimir/workspace-git/uima-ext/UIMA-Ext/UIMA.Ext.Commons/src/main/resources/ru/kfu/itis/cll/uima/commons/Commons-TypeSystem.xml
 * @generated */
public class MisisDocumentMetadata extends Annotation {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(MisisDocumentMetadata.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected MisisDocumentMetadata() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public MisisDocumentMetadata(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public MisisDocumentMetadata(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public MisisDocumentMetadata(JCas jcas, int begin, int end) {
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
  //* Feature: sourceUri

  /** getter for sourceUri - gets 
   * @generated */
  public String getSourceUri() {
    if (MisisDocumentMetadata_Type.featOkTst && ((MisisDocumentMetadata_Type)jcasType).casFeat_sourceUri == null)
      jcasType.jcas.throwFeatMissing("sourceUri", "ru.kfu.itis.cll.uima.commons.MisisDocumentMetadata");
    return jcasType.ll_cas.ll_getStringValue(addr, ((MisisDocumentMetadata_Type)jcasType).casFeatCode_sourceUri);}
    
  /** setter for sourceUri - sets  
   * @generated */
  public void setSourceUri(String v) {
    if (MisisDocumentMetadata_Type.featOkTst && ((MisisDocumentMetadata_Type)jcasType).casFeat_sourceUri == null)
      jcasType.jcas.throwFeatMissing("sourceUri", "ru.kfu.itis.cll.uima.commons.MisisDocumentMetadata");
    jcasType.ll_cas.ll_setStringValue(addr, ((MisisDocumentMetadata_Type)jcasType).casFeatCode_sourceUri, v);}    
   
    
  //*--------------*
  //* Feature: offsetInSource

  /** getter for offsetInSource - gets 
   * @generated */
  public long getOffsetInSource() {
    if (MisisDocumentMetadata_Type.featOkTst && ((MisisDocumentMetadata_Type)jcasType).casFeat_offsetInSource == null)
      jcasType.jcas.throwFeatMissing("offsetInSource", "ru.kfu.itis.cll.uima.commons.MisisDocumentMetadata");
    return jcasType.ll_cas.ll_getLongValue(addr, ((MisisDocumentMetadata_Type)jcasType).casFeatCode_offsetInSource);}
    
  /** setter for offsetInSource - sets  
   * @generated */
  public void setOffsetInSource(long v) {
    if (MisisDocumentMetadata_Type.featOkTst && ((MisisDocumentMetadata_Type)jcasType).casFeat_offsetInSource == null)
      jcasType.jcas.throwFeatMissing("offsetInSource", "ru.kfu.itis.cll.uima.commons.MisisDocumentMetadata");
    jcasType.ll_cas.ll_setLongValue(addr, ((MisisDocumentMetadata_Type)jcasType).casFeatCode_offsetInSource, v);}    
   
    
  //*--------------*
  //* Feature: documentSize

  /** getter for documentSize - gets 
   * @generated */
  public long getDocumentSize() {
    if (MisisDocumentMetadata_Type.featOkTst && ((MisisDocumentMetadata_Type)jcasType).casFeat_documentSize == null)
      jcasType.jcas.throwFeatMissing("documentSize", "ru.kfu.itis.cll.uima.commons.MisisDocumentMetadata");
    return jcasType.ll_cas.ll_getLongValue(addr, ((MisisDocumentMetadata_Type)jcasType).casFeatCode_documentSize);}
    
  /** setter for documentSize - sets  
   * @generated */
  public void setDocumentSize(long v) {
    if (MisisDocumentMetadata_Type.featOkTst && ((MisisDocumentMetadata_Type)jcasType).casFeat_documentSize == null)
      jcasType.jcas.throwFeatMissing("documentSize", "ru.kfu.itis.cll.uima.commons.MisisDocumentMetadata");
    jcasType.ll_cas.ll_setLongValue(addr, ((MisisDocumentMetadata_Type)jcasType).casFeatCode_documentSize, v);}    
   
    
  //*--------------*
  //* Feature: startProcessingTime

  /** getter for startProcessingTime - gets 
   * @generated */
  public long getStartProcessingTime() {
    if (MisisDocumentMetadata_Type.featOkTst && ((MisisDocumentMetadata_Type)jcasType).casFeat_startProcessingTime == null)
      jcasType.jcas.throwFeatMissing("startProcessingTime", "ru.kfu.itis.cll.uima.commons.MisisDocumentMetadata");
    return jcasType.ll_cas.ll_getLongValue(addr, ((MisisDocumentMetadata_Type)jcasType).casFeatCode_startProcessingTime);}
    
  /** setter for startProcessingTime - sets  
   * @generated */
  public void setStartProcessingTime(long v) {
    if (MisisDocumentMetadata_Type.featOkTst && ((MisisDocumentMetadata_Type)jcasType).casFeat_startProcessingTime == null)
      jcasType.jcas.throwFeatMissing("startProcessingTime", "ru.kfu.itis.cll.uima.commons.MisisDocumentMetadata");
    jcasType.ll_cas.ll_setLongValue(addr, ((MisisDocumentMetadata_Type)jcasType).casFeatCode_startProcessingTime, v);}    
   
    
  //*--------------*
  //* Feature: language

  /** getter for language - gets 
   * @generated */
  public String getLanguage() {
    if (MisisDocumentMetadata_Type.featOkTst && ((MisisDocumentMetadata_Type)jcasType).casFeat_language == null)
      jcasType.jcas.throwFeatMissing("language", "ru.kfu.itis.cll.uima.commons.MisisDocumentMetadata");
    return jcasType.ll_cas.ll_getStringValue(addr, ((MisisDocumentMetadata_Type)jcasType).casFeatCode_language);}
    
  /** setter for language - sets  
   * @generated */
  public void setLanguage(String v) {
    if (MisisDocumentMetadata_Type.featOkTst && ((MisisDocumentMetadata_Type)jcasType).casFeat_language == null)
      jcasType.jcas.throwFeatMissing("language", "ru.kfu.itis.cll.uima.commons.MisisDocumentMetadata");
    jcasType.ll_cas.ll_setStringValue(addr, ((MisisDocumentMetadata_Type)jcasType).casFeatCode_language, v);}    
   
    
  //*--------------*
  //* Feature: format

  /** getter for format - gets MIME-type of document: Plaintext, DOC, XLS, PDF.
   * @generated */
  public String getFormat() {
    if (MisisDocumentMetadata_Type.featOkTst && ((MisisDocumentMetadata_Type)jcasType).casFeat_format == null)
      jcasType.jcas.throwFeatMissing("format", "ru.kfu.itis.cll.uima.commons.MisisDocumentMetadata");
    return jcasType.ll_cas.ll_getStringValue(addr, ((MisisDocumentMetadata_Type)jcasType).casFeatCode_format);}
    
  /** setter for format - sets MIME-type of document: Plaintext, DOC, XLS, PDF. 
   * @generated */
  public void setFormat(String v) {
    if (MisisDocumentMetadata_Type.featOkTst && ((MisisDocumentMetadata_Type)jcasType).casFeat_format == null)
      jcasType.jcas.throwFeatMissing("format", "ru.kfu.itis.cll.uima.commons.MisisDocumentMetadata");
    jcasType.ll_cas.ll_setStringValue(addr, ((MisisDocumentMetadata_Type)jcasType).casFeatCode_format, v);}    
   
    
  //*--------------*
  //* Feature: documentRawText

  /** getter for documentRawText - gets 
   * @generated */
  public String getDocumentRawText() {
    if (MisisDocumentMetadata_Type.featOkTst && ((MisisDocumentMetadata_Type)jcasType).casFeat_documentRawText == null)
      jcasType.jcas.throwFeatMissing("documentRawText", "ru.kfu.itis.cll.uima.commons.MisisDocumentMetadata");
    return jcasType.ll_cas.ll_getStringValue(addr, ((MisisDocumentMetadata_Type)jcasType).casFeatCode_documentRawText);}
    
  /** setter for documentRawText - sets  
   * @generated */
  public void setDocumentRawText(String v) {
    if (MisisDocumentMetadata_Type.featOkTst && ((MisisDocumentMetadata_Type)jcasType).casFeat_documentRawText == null)
      jcasType.jcas.throwFeatMissing("documentRawText", "ru.kfu.itis.cll.uima.commons.MisisDocumentMetadata");
    jcasType.ll_cas.ll_setStringValue(addr, ((MisisDocumentMetadata_Type)jcasType).casFeatCode_documentRawText, v);}    
  }

    