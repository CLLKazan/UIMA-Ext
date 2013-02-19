

/* First created by JCasGen Tue Feb 05 17:20:42 MSK 2013 */
package com.hp.hplabs.lim2.ie.text.typesystem;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Tue Feb 05 18:06:06 MSK 2013
 * XML source: /home/pathfinder/Projects/BRATWorkspace/git/UIMA.Ext.Brat.Integration/desc/an-desc-HL.xml
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
      jcasType.jcas.throwFeatMissing("person", "com.hp.hplabs.lim2.ie.text.typesystem.HL_PositionChange");
    return (Annotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((HL_PositionChange_Type)jcasType).casFeatCode_person)));}
    
  /** setter for person - sets  
   * @generated */
  public void setPerson(Annotation v) {
    if (HL_PositionChange_Type.featOkTst && ((HL_PositionChange_Type)jcasType).casFeat_person == null)
      jcasType.jcas.throwFeatMissing("person", "com.hp.hplabs.lim2.ie.text.typesystem.HL_PositionChange");
    jcasType.ll_cas.ll_setRefValue(addr, ((HL_PositionChange_Type)jcasType).casFeatCode_person, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: newPosition

  /** getter for newPosition - gets 
   * @generated */
  public Annotation getNewPosition() {
    if (HL_PositionChange_Type.featOkTst && ((HL_PositionChange_Type)jcasType).casFeat_newPosition == null)
      jcasType.jcas.throwFeatMissing("newPosition", "com.hp.hplabs.lim2.ie.text.typesystem.HL_PositionChange");
    return (Annotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((HL_PositionChange_Type)jcasType).casFeatCode_newPosition)));}
    
  /** setter for newPosition - sets  
   * @generated */
  public void setNewPosition(Annotation v) {
    if (HL_PositionChange_Type.featOkTst && ((HL_PositionChange_Type)jcasType).casFeat_newPosition == null)
      jcasType.jcas.throwFeatMissing("newPosition", "com.hp.hplabs.lim2.ie.text.typesystem.HL_PositionChange");
    jcasType.ll_cas.ll_setRefValue(addr, ((HL_PositionChange_Type)jcasType).casFeatCode_newPosition, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: oldPosition

  /** getter for oldPosition - gets 
   * @generated */
  public Annotation getOldPosition() {
    if (HL_PositionChange_Type.featOkTst && ((HL_PositionChange_Type)jcasType).casFeat_oldPosition == null)
      jcasType.jcas.throwFeatMissing("oldPosition", "com.hp.hplabs.lim2.ie.text.typesystem.HL_PositionChange");
    return (Annotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((HL_PositionChange_Type)jcasType).casFeatCode_oldPosition)));}
    
  /** setter for oldPosition - sets  
   * @generated */
  public void setOldPosition(Annotation v) {
    if (HL_PositionChange_Type.featOkTst && ((HL_PositionChange_Type)jcasType).casFeat_oldPosition == null)
      jcasType.jcas.throwFeatMissing("oldPosition", "com.hp.hplabs.lim2.ie.text.typesystem.HL_PositionChange");
    jcasType.ll_cas.ll_setRefValue(addr, ((HL_PositionChange_Type)jcasType).casFeatCode_oldPosition, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    