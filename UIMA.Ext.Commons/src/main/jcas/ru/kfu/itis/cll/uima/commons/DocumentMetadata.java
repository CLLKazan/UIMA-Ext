

/* First created by JCasGen Fri Jul 06 01:31:15 MSK 2012 */
package ru.kfu.itis.cll.uima.commons;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Fri Jul 06 01:31:15 MSK 2012
 * XML source: D:/projects/uima-ext/UIMA.Ext.Commons/src/main/resources/ru/kfu/itis/cll/uima/commons/Commons-TypeSystem.xml
 * @generated */
public class DocumentMetadata extends Annotation {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(DocumentMetadata.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected DocumentMetadata() {}
    
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
  private void readObject() {}
     
 
    
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
  }

    