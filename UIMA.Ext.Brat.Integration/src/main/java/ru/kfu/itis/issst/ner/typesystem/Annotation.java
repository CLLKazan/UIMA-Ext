

/* First created by JCasGen Tue Mar 12 00:30:02 MSK 2013 */
package ru.kfu.itis.issst.ner.typesystem;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** .
 * Updated by JCasGen Tue Mar 12 00:30:02 MSK 2013
 * XML source: /home/pathfinder/_WORK/Projects/BRATWorkspace/git/UIMA-Ext/UIMA.Ext.Brat.Integration/desc/UIMA2BratAnnotatorDescriptor.xml
 * @generated */
public class Annotation extends org.apache.uima.jcas.tcas.Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Annotation.class);
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
  protected Annotation() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Annotation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Annotation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Annotation(JCas jcas, int begin, int end) {
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
  //* Feature: annotatorID

  /** getter for annotatorID - gets The identifier of the annotator made this annotation. Can be null.
   * @generated */
  public String getAnnotatorID() {
    if (Annotation_Type.featOkTst && ((Annotation_Type)jcasType).casFeat_annotatorID == null)
      jcasType.jcas.throwFeatMissing("annotatorID", "ru.kfu.itis.issst.ner.typesystem.Annotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Annotation_Type)jcasType).casFeatCode_annotatorID);}
    
  /** setter for annotatorID - sets The identifier of the annotator made this annotation. Can be null. 
   * @generated */
  public void setAnnotatorID(String v) {
    if (Annotation_Type.featOkTst && ((Annotation_Type)jcasType).casFeat_annotatorID == null)
      jcasType.jcas.throwFeatMissing("annotatorID", "ru.kfu.itis.issst.ner.typesystem.Annotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((Annotation_Type)jcasType).casFeatCode_annotatorID, v);}    
   
    
  //*--------------*
  //* Feature: confidence

  /** getter for confidence - gets The confidence of the annotation. Zero (0) or negative value means that this value is not available for the annotation.
   * @generated */
  public double getConfidence() {
    if (Annotation_Type.featOkTst && ((Annotation_Type)jcasType).casFeat_confidence == null)
      jcasType.jcas.throwFeatMissing("confidence", "ru.kfu.itis.issst.ner.typesystem.Annotation");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((Annotation_Type)jcasType).casFeatCode_confidence);}
    
  /** setter for confidence - sets The confidence of the annotation. Zero (0) or negative value means that this value is not available for the annotation. 
   * @generated */
  public void setConfidence(double v) {
    if (Annotation_Type.featOkTst && ((Annotation_Type)jcasType).casFeat_confidence == null)
      jcasType.jcas.throwFeatMissing("confidence", "ru.kfu.itis.issst.ner.typesystem.Annotation");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((Annotation_Type)jcasType).casFeatCode_confidence, v);}    
  }

    