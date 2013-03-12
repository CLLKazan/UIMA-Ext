

/* First created by JCasGen Tue Mar 12 00:30:02 MSK 2013 */
package ru.kfu.itis.issst.ner.typesystem;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Tue Mar 12 00:30:02 MSK 2013
 * XML source: /home/pathfinder/_WORK/Projects/BRATWorkspace/git/UIMA-Ext/UIMA.Ext.Brat.Integration/desc/UIMA2BratAnnotatorDescriptor.xml
 * @generated */
public class HL_PositionChange extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(HL_PositionChange.class);
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
  protected HL_PositionChange() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public HL_PositionChange(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public HL_PositionChange(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public HL_PositionChange(JCas jcas, int begin, int end) {
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
  //* Feature: person

  /** getter for person - gets 
   * @generated */
  public Annotation getPerson() {
    if (HL_PositionChange_Type.featOkTst && ((HL_PositionChange_Type)jcasType).casFeat_person == null)
      jcasType.jcas.throwFeatMissing("person", "ru.kfu.itis.issst.ner.typesystem.HL_PositionChange");
    return (Annotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((HL_PositionChange_Type)jcasType).casFeatCode_person)));}
    
  /** setter for person - sets  
   * @generated */
  public void setPerson(Annotation v) {
    if (HL_PositionChange_Type.featOkTst && ((HL_PositionChange_Type)jcasType).casFeat_person == null)
      jcasType.jcas.throwFeatMissing("person", "ru.kfu.itis.issst.ner.typesystem.HL_PositionChange");
    jcasType.ll_cas.ll_setRefValue(addr, ((HL_PositionChange_Type)jcasType).casFeatCode_person, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: newPosition

  /** getter for newPosition - gets 
   * @generated */
  public Annotation getNewPosition() {
    if (HL_PositionChange_Type.featOkTst && ((HL_PositionChange_Type)jcasType).casFeat_newPosition == null)
      jcasType.jcas.throwFeatMissing("newPosition", "ru.kfu.itis.issst.ner.typesystem.HL_PositionChange");
    return (Annotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((HL_PositionChange_Type)jcasType).casFeatCode_newPosition)));}
    
  /** setter for newPosition - sets  
   * @generated */
  public void setNewPosition(Annotation v) {
    if (HL_PositionChange_Type.featOkTst && ((HL_PositionChange_Type)jcasType).casFeat_newPosition == null)
      jcasType.jcas.throwFeatMissing("newPosition", "ru.kfu.itis.issst.ner.typesystem.HL_PositionChange");
    jcasType.ll_cas.ll_setRefValue(addr, ((HL_PositionChange_Type)jcasType).casFeatCode_newPosition, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: oldPosition

  /** getter for oldPosition - gets 
   * @generated */
  public Annotation getOldPosition() {
    if (HL_PositionChange_Type.featOkTst && ((HL_PositionChange_Type)jcasType).casFeat_oldPosition == null)
      jcasType.jcas.throwFeatMissing("oldPosition", "ru.kfu.itis.issst.ner.typesystem.HL_PositionChange");
    return (Annotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((HL_PositionChange_Type)jcasType).casFeatCode_oldPosition)));}
    
  /** setter for oldPosition - sets  
   * @generated */
  public void setOldPosition(Annotation v) {
    if (HL_PositionChange_Type.featOkTst && ((HL_PositionChange_Type)jcasType).casFeat_oldPosition == null)
      jcasType.jcas.throwFeatMissing("oldPosition", "ru.kfu.itis.issst.ner.typesystem.HL_PositionChange");
    jcasType.ll_cas.ll_setRefValue(addr, ((HL_PositionChange_Type)jcasType).casFeatCode_oldPosition, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: newCompany

  /** getter for newCompany - gets 
   * @generated */
  public Annotation getNewCompany() {
    if (HL_PositionChange_Type.featOkTst && ((HL_PositionChange_Type)jcasType).casFeat_newCompany == null)
      jcasType.jcas.throwFeatMissing("newCompany", "ru.kfu.itis.issst.ner.typesystem.HL_PositionChange");
    return (Annotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((HL_PositionChange_Type)jcasType).casFeatCode_newCompany)));}
    
  /** setter for newCompany - sets  
   * @generated */
  public void setNewCompany(Annotation v) {
    if (HL_PositionChange_Type.featOkTst && ((HL_PositionChange_Type)jcasType).casFeat_newCompany == null)
      jcasType.jcas.throwFeatMissing("newCompany", "ru.kfu.itis.issst.ner.typesystem.HL_PositionChange");
    jcasType.ll_cas.ll_setRefValue(addr, ((HL_PositionChange_Type)jcasType).casFeatCode_newCompany, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: oldCompany

  /** getter for oldCompany - gets 
   * @generated */
  public Annotation getOldCompany() {
    if (HL_PositionChange_Type.featOkTst && ((HL_PositionChange_Type)jcasType).casFeat_oldCompany == null)
      jcasType.jcas.throwFeatMissing("oldCompany", "ru.kfu.itis.issst.ner.typesystem.HL_PositionChange");
    return (Annotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((HL_PositionChange_Type)jcasType).casFeatCode_oldCompany)));}
    
  /** setter for oldCompany - sets  
   * @generated */
  public void setOldCompany(Annotation v) {
    if (HL_PositionChange_Type.featOkTst && ((HL_PositionChange_Type)jcasType).casFeat_oldCompany == null)
      jcasType.jcas.throwFeatMissing("oldCompany", "ru.kfu.itis.issst.ner.typesystem.HL_PositionChange");
    jcasType.ll_cas.ll_setRefValue(addr, ((HL_PositionChange_Type)jcasType).casFeatCode_oldCompany, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    