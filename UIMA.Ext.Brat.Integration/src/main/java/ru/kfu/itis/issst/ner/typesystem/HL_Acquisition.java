

/* First created by JCasGen Tue Mar 12 00:30:02 MSK 2013 */
package ru.kfu.itis.issst.ner.typesystem;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Tue Mar 12 00:30:02 MSK 2013
 * XML source: /home/pathfinder/_WORK/Projects/BRATWorkspace/git/UIMA-Ext/UIMA.Ext.Brat.Integration/desc/UIMA2BratAnnotatorDescriptor.xml
 * @generated */
public class HL_Acquisition extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(HL_Acquisition.class);
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
  protected HL_Acquisition() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public HL_Acquisition(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public HL_Acquisition(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public HL_Acquisition(JCas jcas, int begin, int end) {
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
  //* Feature: slot1

  /** getter for slot1 - gets 
   * @generated */
  public Annotation getSlot1() {
    if (HL_Acquisition_Type.featOkTst && ((HL_Acquisition_Type)jcasType).casFeat_slot1 == null)
      jcasType.jcas.throwFeatMissing("slot1", "ru.kfu.itis.issst.ner.typesystem.HL_Acquisition");
    return (Annotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((HL_Acquisition_Type)jcasType).casFeatCode_slot1)));}
    
  /** setter for slot1 - sets  
   * @generated */
  public void setSlot1(Annotation v) {
    if (HL_Acquisition_Type.featOkTst && ((HL_Acquisition_Type)jcasType).casFeat_slot1 == null)
      jcasType.jcas.throwFeatMissing("slot1", "ru.kfu.itis.issst.ner.typesystem.HL_Acquisition");
    jcasType.ll_cas.ll_setRefValue(addr, ((HL_Acquisition_Type)jcasType).casFeatCode_slot1, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: slot2

  /** getter for slot2 - gets 
   * @generated */
  public Annotation getSlot2() {
    if (HL_Acquisition_Type.featOkTst && ((HL_Acquisition_Type)jcasType).casFeat_slot2 == null)
      jcasType.jcas.throwFeatMissing("slot2", "ru.kfu.itis.issst.ner.typesystem.HL_Acquisition");
    return (Annotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((HL_Acquisition_Type)jcasType).casFeatCode_slot2)));}
    
  /** setter for slot2 - sets  
   * @generated */
  public void setSlot2(Annotation v) {
    if (HL_Acquisition_Type.featOkTst && ((HL_Acquisition_Type)jcasType).casFeat_slot2 == null)
      jcasType.jcas.throwFeatMissing("slot2", "ru.kfu.itis.issst.ner.typesystem.HL_Acquisition");
    jcasType.ll_cas.ll_setRefValue(addr, ((HL_Acquisition_Type)jcasType).casFeatCode_slot2, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    