
/* First created by JCasGen Tue Mar 12 00:30:02 MSK 2013 */
package ru.kfu.itis.issst.ner.typesystem;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;

/** 
 * Updated by JCasGen Tue Mar 12 00:30:02 MSK 2013
 * @generated */
public class DocumentMetadata_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (DocumentMetadata_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = DocumentMetadata_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new DocumentMetadata(addr, DocumentMetadata_Type.this);
  			   DocumentMetadata_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new DocumentMetadata(addr, DocumentMetadata_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = DocumentMetadata.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
 
  /** @generated */
  final Feature casFeat_uri;
  /** @generated */
  final int     casFeatCode_uri;
  /** @generated */ 
  public String getUri(int addr) {
        if (featOkTst && casFeat_uri == null)
      jcas.throwFeatMissing("uri", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    return ll_cas.ll_getStringValue(addr, casFeatCode_uri);
  }
  /** @generated */    
  public void setUri(int addr, String v) {
        if (featOkTst && casFeat_uri == null)
      jcas.throwFeatMissing("uri", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    ll_cas.ll_setStringValue(addr, casFeatCode_uri, v);}
    
  
 
  /** @generated */
  final Feature casFeat_offset;
  /** @generated */
  final int     casFeatCode_offset;
  /** @generated */ 
  public int getOffset(int addr) {
        if (featOkTst && casFeat_offset == null)
      jcas.throwFeatMissing("offset", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    return ll_cas.ll_getIntValue(addr, casFeatCode_offset);
  }
  /** @generated */    
  public void setOffset(int addr, int v) {
        if (featOkTst && casFeat_offset == null)
      jcas.throwFeatMissing("offset", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    ll_cas.ll_setIntValue(addr, casFeatCode_offset, v);}
    
  
 
  /** @generated */
  final Feature casFeat_documentSize;
  /** @generated */
  final int     casFeatCode_documentSize;
  /** @generated */ 
  public int getDocumentSize(int addr) {
        if (featOkTst && casFeat_documentSize == null)
      jcas.throwFeatMissing("documentSize", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    return ll_cas.ll_getIntValue(addr, casFeatCode_documentSize);
  }
  /** @generated */    
  public void setDocumentSize(int addr, int v) {
        if (featOkTst && casFeat_documentSize == null)
      jcas.throwFeatMissing("documentSize", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    ll_cas.ll_setIntValue(addr, casFeatCode_documentSize, v);}
    
  
 
  /** @generated */
  final Feature casFeat_lastSegment;
  /** @generated */
  final int     casFeatCode_lastSegment;
  /** @generated */ 
  public boolean getLastSegment(int addr) {
        if (featOkTst && casFeat_lastSegment == null)
      jcas.throwFeatMissing("lastSegment", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_lastSegment);
  }
  /** @generated */    
  public void setLastSegment(int addr, boolean v) {
        if (featOkTst && casFeat_lastSegment == null)
      jcas.throwFeatMissing("lastSegment", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_lastSegment, v);}
    
  
 
  /** @generated */
  final Feature casFeat_title;
  /** @generated */
  final int     casFeatCode_title;
  /** @generated */ 
  public String getTitle(int addr) {
        if (featOkTst && casFeat_title == null)
      jcas.throwFeatMissing("title", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    return ll_cas.ll_getStringValue(addr, casFeatCode_title);
  }
  /** @generated */    
  public void setTitle(int addr, String v) {
        if (featOkTst && casFeat_title == null)
      jcas.throwFeatMissing("title", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    ll_cas.ll_setStringValue(addr, casFeatCode_title, v);}
    
  
 
  /** @generated */
  final Feature casFeat_encoding;
  /** @generated */
  final int     casFeatCode_encoding;
  /** @generated */ 
  public String getEncoding(int addr) {
        if (featOkTst && casFeat_encoding == null)
      jcas.throwFeatMissing("encoding", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    return ll_cas.ll_getStringValue(addr, casFeatCode_encoding);
  }
  /** @generated */    
  public void setEncoding(int addr, String v) {
        if (featOkTst && casFeat_encoding == null)
      jcas.throwFeatMissing("encoding", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    ll_cas.ll_setStringValue(addr, casFeatCode_encoding, v);}
    
  
 
  /** @generated */
  final Feature casFeat_startProcessingTicks;
  /** @generated */
  final int     casFeatCode_startProcessingTicks;
  /** @generated */ 
  public long getStartProcessingTicks(int addr) {
        if (featOkTst && casFeat_startProcessingTicks == null)
      jcas.throwFeatMissing("startProcessingTicks", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    return ll_cas.ll_getLongValue(addr, casFeatCode_startProcessingTicks);
  }
  /** @generated */    
  public void setStartProcessingTicks(int addr, long v) {
        if (featOkTst && casFeat_startProcessingTicks == null)
      jcas.throwFeatMissing("startProcessingTicks", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    ll_cas.ll_setLongValue(addr, casFeatCode_startProcessingTicks, v);}
    
  
 
  /** @generated */
  final Feature casFeat_stopProcessingTicks;
  /** @generated */
  final int     casFeatCode_stopProcessingTicks;
  /** @generated */ 
  public long getStopProcessingTicks(int addr) {
        if (featOkTst && casFeat_stopProcessingTicks == null)
      jcas.throwFeatMissing("stopProcessingTicks", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    return ll_cas.ll_getLongValue(addr, casFeatCode_stopProcessingTicks);
  }
  /** @generated */    
  public void setStopProcessingTicks(int addr, long v) {
        if (featOkTst && casFeat_stopProcessingTicks == null)
      jcas.throwFeatMissing("stopProcessingTicks", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    ll_cas.ll_setLongValue(addr, casFeatCode_stopProcessingTicks, v);}
    
  
 
  /** @generated */
  final Feature casFeat_tags;
  /** @generated */
  final int     casFeatCode_tags;
  /** @generated */ 
  public int getTags(int addr) {
        if (featOkTst && casFeat_tags == null)
      jcas.throwFeatMissing("tags", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    return ll_cas.ll_getRefValue(addr, casFeatCode_tags);
  }
  /** @generated */    
  public void setTags(int addr, int v) {
        if (featOkTst && casFeat_tags == null)
      jcas.throwFeatMissing("tags", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    ll_cas.ll_setRefValue(addr, casFeatCode_tags, v);}
    
   /** @generated */
  public String getTags(int addr, int i) {
        if (featOkTst && casFeat_tags == null)
      jcas.throwFeatMissing("tags", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_tags), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_tags), i);
	return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_tags), i);
  }
   
  /** @generated */ 
  public void setTags(int addr, int i, String v) {
        if (featOkTst && casFeat_tags == null)
      jcas.throwFeatMissing("tags", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    if (lowLevelTypeChecks)
      ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_tags), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_tags), i);
    ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_tags), i, v);
  }
 
 
  /** @generated */
  final Feature casFeat_classes;
  /** @generated */
  final int     casFeatCode_classes;
  /** @generated */ 
  public int getClasses(int addr) {
        if (featOkTst && casFeat_classes == null)
      jcas.throwFeatMissing("classes", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    return ll_cas.ll_getRefValue(addr, casFeatCode_classes);
  }
  /** @generated */    
  public void setClasses(int addr, int v) {
        if (featOkTst && casFeat_classes == null)
      jcas.throwFeatMissing("classes", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    ll_cas.ll_setRefValue(addr, casFeatCode_classes, v);}
    
   /** @generated */
  public String getClasses(int addr, int i) {
        if (featOkTst && casFeat_classes == null)
      jcas.throwFeatMissing("classes", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_classes), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_classes), i);
	return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_classes), i);
  }
   
  /** @generated */ 
  public void setClasses(int addr, int i, String v) {
        if (featOkTst && casFeat_classes == null)
      jcas.throwFeatMissing("classes", "ru.kfu.itis.issst.ner.typesystem.DocumentMetadata");
    if (lowLevelTypeChecks)
      ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_classes), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_classes), i);
    ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_classes), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public DocumentMetadata_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_uri = jcas.getRequiredFeatureDE(casType, "uri", "uima.cas.String", featOkTst);
    casFeatCode_uri  = (null == casFeat_uri) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_uri).getCode();

 
    casFeat_offset = jcas.getRequiredFeatureDE(casType, "offset", "uima.cas.Integer", featOkTst);
    casFeatCode_offset  = (null == casFeat_offset) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_offset).getCode();

 
    casFeat_documentSize = jcas.getRequiredFeatureDE(casType, "documentSize", "uima.cas.Integer", featOkTst);
    casFeatCode_documentSize  = (null == casFeat_documentSize) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_documentSize).getCode();

 
    casFeat_lastSegment = jcas.getRequiredFeatureDE(casType, "lastSegment", "uima.cas.Boolean", featOkTst);
    casFeatCode_lastSegment  = (null == casFeat_lastSegment) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_lastSegment).getCode();

 
    casFeat_title = jcas.getRequiredFeatureDE(casType, "title", "uima.cas.String", featOkTst);
    casFeatCode_title  = (null == casFeat_title) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_title).getCode();

 
    casFeat_encoding = jcas.getRequiredFeatureDE(casType, "encoding", "uima.cas.String", featOkTst);
    casFeatCode_encoding  = (null == casFeat_encoding) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_encoding).getCode();

 
    casFeat_startProcessingTicks = jcas.getRequiredFeatureDE(casType, "startProcessingTicks", "uima.cas.Long", featOkTst);
    casFeatCode_startProcessingTicks  = (null == casFeat_startProcessingTicks) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_startProcessingTicks).getCode();

 
    casFeat_stopProcessingTicks = jcas.getRequiredFeatureDE(casType, "stopProcessingTicks", "uima.cas.Long", featOkTst);
    casFeatCode_stopProcessingTicks  = (null == casFeat_stopProcessingTicks) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_stopProcessingTicks).getCode();

 
    casFeat_tags = jcas.getRequiredFeatureDE(casType, "tags", "uima.cas.StringArray", featOkTst);
    casFeatCode_tags  = (null == casFeat_tags) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_tags).getCode();

 
    casFeat_classes = jcas.getRequiredFeatureDE(casType, "classes", "uima.cas.StringArray", featOkTst);
    casFeatCode_classes  = (null == casFeat_classes) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_classes).getCode();

  }
}



    