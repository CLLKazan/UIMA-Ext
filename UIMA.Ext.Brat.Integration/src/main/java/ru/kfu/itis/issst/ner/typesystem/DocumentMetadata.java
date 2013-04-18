

/* First created by JCasGen Tue Mar 12 00:30:02 MSK 2013 */
package ru.kfu.itis.issst.ner.typesystem;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.StringArray;


/** 
 * Updated by JCasGen Tue Mar 12 00:30:02 MSK 2013
 * XML source: /home/pathfinder/_WORK/Projects/BRATWorkspace/git/UIMA-Ext/UIMA.Ext.Brat.Integration/desc/UIMA2BratAnnotatorDescriptor.xml
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
  //* Feature: uri

  /** getter for uri - gets 
   * @generated */
  public String getUri() {
    if (DocumentMetadata_Type.featOkTst && ((DocumentMetadata_Type)jcasType).casFeat_uri == null)
      jcasType.jcas.throwFeatMissing("uri", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DocumentMetadata_Type)jcasType).casFeatCode_uri);}
    
  /** setter for uri - sets  
   * @generated */
  public void setUri(String v) {
    if (DocumentMetadata_Type.featOkTst && ((DocumentMetadata_Type)jcasType).casFeat_uri == null)
      jcasType.jcas.throwFeatMissing("uri", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    jcasType.ll_cas.ll_setStringValue(addr, ((DocumentMetadata_Type)jcasType).casFeatCode_uri, v);}    
   
    
  //*--------------*
  //* Feature: offset

  /** getter for offset - gets 
   * @generated */
  public int getOffset() {
    if (DocumentMetadata_Type.featOkTst && ((DocumentMetadata_Type)jcasType).casFeat_offset == null)
      jcasType.jcas.throwFeatMissing("offset", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    return jcasType.ll_cas.ll_getIntValue(addr, ((DocumentMetadata_Type)jcasType).casFeatCode_offset);}
    
  /** setter for offset - sets  
   * @generated */
  public void setOffset(int v) {
    if (DocumentMetadata_Type.featOkTst && ((DocumentMetadata_Type)jcasType).casFeat_offset == null)
      jcasType.jcas.throwFeatMissing("offset", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    jcasType.ll_cas.ll_setIntValue(addr, ((DocumentMetadata_Type)jcasType).casFeatCode_offset, v);}    
   
    
  //*--------------*
  //* Feature: documentSize

  /** getter for documentSize - gets 
   * @generated */
  public int getDocumentSize() {
    if (DocumentMetadata_Type.featOkTst && ((DocumentMetadata_Type)jcasType).casFeat_documentSize == null)
      jcasType.jcas.throwFeatMissing("documentSize", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    return jcasType.ll_cas.ll_getIntValue(addr, ((DocumentMetadata_Type)jcasType).casFeatCode_documentSize);}
    
  /** setter for documentSize - sets  
   * @generated */
  public void setDocumentSize(int v) {
    if (DocumentMetadata_Type.featOkTst && ((DocumentMetadata_Type)jcasType).casFeat_documentSize == null)
      jcasType.jcas.throwFeatMissing("documentSize", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    jcasType.ll_cas.ll_setIntValue(addr, ((DocumentMetadata_Type)jcasType).casFeatCode_documentSize, v);}    
   
    
  //*--------------*
  //* Feature: lastSegment

  /** getter for lastSegment - gets 
   * @generated */
  public boolean getLastSegment() {
    if (DocumentMetadata_Type.featOkTst && ((DocumentMetadata_Type)jcasType).casFeat_lastSegment == null)
      jcasType.jcas.throwFeatMissing("lastSegment", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((DocumentMetadata_Type)jcasType).casFeatCode_lastSegment);}
    
  /** setter for lastSegment - sets  
   * @generated */
  public void setLastSegment(boolean v) {
    if (DocumentMetadata_Type.featOkTst && ((DocumentMetadata_Type)jcasType).casFeat_lastSegment == null)
      jcasType.jcas.throwFeatMissing("lastSegment", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((DocumentMetadata_Type)jcasType).casFeatCode_lastSegment, v);}    
   
    
  //*--------------*
  //* Feature: title

  /** getter for title - gets 
   * @generated */
  public String getTitle() {
    if (DocumentMetadata_Type.featOkTst && ((DocumentMetadata_Type)jcasType).casFeat_title == null)
      jcasType.jcas.throwFeatMissing("title", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DocumentMetadata_Type)jcasType).casFeatCode_title);}
    
  /** setter for title - sets  
   * @generated */
  public void setTitle(String v) {
    if (DocumentMetadata_Type.featOkTst && ((DocumentMetadata_Type)jcasType).casFeat_title == null)
      jcasType.jcas.throwFeatMissing("title", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    jcasType.ll_cas.ll_setStringValue(addr, ((DocumentMetadata_Type)jcasType).casFeatCode_title, v);}    
   
    
  //*--------------*
  //* Feature: encoding

  /** getter for encoding - gets 
   * @generated */
  public String getEncoding() {
    if (DocumentMetadata_Type.featOkTst && ((DocumentMetadata_Type)jcasType).casFeat_encoding == null)
      jcasType.jcas.throwFeatMissing("encoding", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DocumentMetadata_Type)jcasType).casFeatCode_encoding);}
    
  /** setter for encoding - sets  
   * @generated */
  public void setEncoding(String v) {
    if (DocumentMetadata_Type.featOkTst && ((DocumentMetadata_Type)jcasType).casFeat_encoding == null)
      jcasType.jcas.throwFeatMissing("encoding", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    jcasType.ll_cas.ll_setStringValue(addr, ((DocumentMetadata_Type)jcasType).casFeatCode_encoding, v);}    
   
    
  //*--------------*
  //* Feature: startProcessingTicks

  /** getter for startProcessingTicks - gets 
   * @generated */
  public long getStartProcessingTicks() {
    if (DocumentMetadata_Type.featOkTst && ((DocumentMetadata_Type)jcasType).casFeat_startProcessingTicks == null)
      jcasType.jcas.throwFeatMissing("startProcessingTicks", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    return jcasType.ll_cas.ll_getLongValue(addr, ((DocumentMetadata_Type)jcasType).casFeatCode_startProcessingTicks);}
    
  /** setter for startProcessingTicks - sets  
   * @generated */
  public void setStartProcessingTicks(long v) {
    if (DocumentMetadata_Type.featOkTst && ((DocumentMetadata_Type)jcasType).casFeat_startProcessingTicks == null)
      jcasType.jcas.throwFeatMissing("startProcessingTicks", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    jcasType.ll_cas.ll_setLongValue(addr, ((DocumentMetadata_Type)jcasType).casFeatCode_startProcessingTicks, v);}    
   
    
  //*--------------*
  //* Feature: stopProcessingTicks

  /** getter for stopProcessingTicks - gets 
   * @generated */
  public long getStopProcessingTicks() {
    if (DocumentMetadata_Type.featOkTst && ((DocumentMetadata_Type)jcasType).casFeat_stopProcessingTicks == null)
      jcasType.jcas.throwFeatMissing("stopProcessingTicks", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    return jcasType.ll_cas.ll_getLongValue(addr, ((DocumentMetadata_Type)jcasType).casFeatCode_stopProcessingTicks);}
    
  /** setter for stopProcessingTicks - sets  
   * @generated */
  public void setStopProcessingTicks(long v) {
    if (DocumentMetadata_Type.featOkTst && ((DocumentMetadata_Type)jcasType).casFeat_stopProcessingTicks == null)
      jcasType.jcas.throwFeatMissing("stopProcessingTicks", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    jcasType.ll_cas.ll_setLongValue(addr, ((DocumentMetadata_Type)jcasType).casFeatCode_stopProcessingTicks, v);}    
   
    
  //*--------------*
  //* Feature: tags

  /** getter for tags - gets 
   * @generated */
  public StringArray getTags() {
    if (DocumentMetadata_Type.featOkTst && ((DocumentMetadata_Type)jcasType).casFeat_tags == null)
      jcasType.jcas.throwFeatMissing("tags", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    return (StringArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((DocumentMetadata_Type)jcasType).casFeatCode_tags)));}
    
  /** setter for tags - sets  
   * @generated */
  public void setTags(StringArray v) {
    if (DocumentMetadata_Type.featOkTst && ((DocumentMetadata_Type)jcasType).casFeat_tags == null)
      jcasType.jcas.throwFeatMissing("tags", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    jcasType.ll_cas.ll_setRefValue(addr, ((DocumentMetadata_Type)jcasType).casFeatCode_tags, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for tags - gets an indexed value - 
   * @generated */
  public String getTags(int i) {
    if (DocumentMetadata_Type.featOkTst && ((DocumentMetadata_Type)jcasType).casFeat_tags == null)
      jcasType.jcas.throwFeatMissing("tags", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((DocumentMetadata_Type)jcasType).casFeatCode_tags), i);
    return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((DocumentMetadata_Type)jcasType).casFeatCode_tags), i);}

  /** indexed setter for tags - sets an indexed value - 
   * @generated */
  public void setTags(int i, String v) { 
    if (DocumentMetadata_Type.featOkTst && ((DocumentMetadata_Type)jcasType).casFeat_tags == null)
      jcasType.jcas.throwFeatMissing("tags", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((DocumentMetadata_Type)jcasType).casFeatCode_tags), i);
    jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((DocumentMetadata_Type)jcasType).casFeatCode_tags), i, v);}
   
    
  //*--------------*
  //* Feature: classes

  /** getter for classes - gets 
   * @generated */
  public StringArray getClasses() {
    if (DocumentMetadata_Type.featOkTst && ((DocumentMetadata_Type)jcasType).casFeat_classes == null)
      jcasType.jcas.throwFeatMissing("classes", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    return (StringArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((DocumentMetadata_Type)jcasType).casFeatCode_classes)));}
    
  /** setter for classes - sets  
   * @generated */
  public void setClasses(StringArray v) {
    if (DocumentMetadata_Type.featOkTst && ((DocumentMetadata_Type)jcasType).casFeat_classes == null)
      jcasType.jcas.throwFeatMissing("classes", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    jcasType.ll_cas.ll_setRefValue(addr, ((DocumentMetadata_Type)jcasType).casFeatCode_classes, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for classes - gets an indexed value - 
   * @generated */
  public String getClasses(int i) {
    if (DocumentMetadata_Type.featOkTst && ((DocumentMetadata_Type)jcasType).casFeat_classes == null)
      jcasType.jcas.throwFeatMissing("classes", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((DocumentMetadata_Type)jcasType).casFeatCode_classes), i);
    return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((DocumentMetadata_Type)jcasType).casFeatCode_classes), i);}

  /** indexed setter for classes - sets an indexed value - 
   * @generated */
  public void setClasses(int i, String v) { 
    if (DocumentMetadata_Type.featOkTst && ((DocumentMetadata_Type)jcasType).casFeat_classes == null)
      jcasType.jcas.throwFeatMissing("classes", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((DocumentMetadata_Type)jcasType).casFeatCode_classes), i);
    jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((DocumentMetadata_Type)jcasType).casFeatCode_classes), i, v);}
  }

    