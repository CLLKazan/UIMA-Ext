

/* First created by JCasGen Tue Mar 12 00:30:02 MSK 2013 */
package ru.kfu.itis.issst.ner.typesystem;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Tue Mar 12 00:30:02 MSK 2013
 * XML source: /home/pathfinder/_WORK/Projects/BRATWorkspace/git/UIMA-Ext/UIMA.Ext.Brat.Integration/desc/UIMA2BratAnnotatorDescriptor.xml
 * @generated */
public class HL_Resignation extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(HL_Resignation.class);
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
  protected HL_Resignation() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public HL_Resignation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public HL_Resignation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public HL_Resignation(JCas jcas, int begin, int end) {
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
    if (HL_Resignation_Type.featOkTst && ((HL_Resignation_Type)jcasType).casFeat_person == null)
      jcasType.jcas.throwFeatMissing("person", "ru.kfu.itis.issst.ner.typesystem.HL_Resignation");
    return (Annotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((HL_Resignation_Type)jcasType).casFeatCode_person)));}
    
  /** setter for person - sets  
   * @generated */
  public void setPerson(Annotation v) {
    if (HL_Resignation_Type.featOkTst && ((HL_Resignation_Type)jcasType).casFeat_person == null)
      jcasType.jcas.throwFeatMissing("person", "ru.kfu.itis.issst.ner.typesystem.HL_Resignation");
    jcasType.ll_cas.ll_setRefValue(addr, ((HL_Resignation_Type)jcasType).casFeatCode_person, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: position

  /** getter for position - gets 
   * @generated */
  public Annotation getPosition() {
    if (HL_Resignation_Type.featOkTst && ((HL_Resignation_Type)jcasType).casFeat_position == null)
      jcasType.jcas.throwFeatMissing("position", "ru.kfu.itis.issst.ner.typesystem.HL_Resignation");
    return (Annotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((HL_Resignation_Type)jcasType).casFeatCode_position)));}
    
  /** setter for position - sets  
   * @generated */
  public void setPosition(Annotation v) {
    if (HL_Resignation_Type.featOkTst && ((HL_Resignation_Type)jcasType).casFeat_position == null)
      jcasType.jcas.throwFeatMissing("position", "ru.kfu.itis.issst.ner.typesystem.HL_Resignation");
    jcasType.ll_cas.ll_setRefValue(addr, ((HL_Resignation_Type)jcasType).casFeatCode_position, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: company

  /** getter for company - gets 
   * @generated */
  public Annotation getCompany() {
    if (HL_Resignation_Type.featOkTst && ((HL_Resignation_Type)jcasType).casFeat_company == null)
      jcasType.jcas.throwFeatMissing("company", "ru.kfu.itis.issst.ner.typesystem.HL_Resignation");
    return (Annotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((HL_Resignation_Type)jcasType).casFeatCode_company)));}
    
  /** setter for company - sets  
   * @generated */
  public void setCompany(Annotation v) {
    if (HL_Resignation_Type.featOkTst && ((HL_Resignation_Type)jcasType).casFeat_company == null)
      jcasType.jcas.throwFeatMissing("company", "ru.kfu.itis.issst.ner.typesystem.HL_Resignation");
    jcasType.ll_cas.ll_setRefValue(addr, ((HL_Resignation_Type)jcasType).casFeatCode_company, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: approvedBy

  /** getter for approvedBy - gets 
   * @generated */
  public Annotation getApprovedBy() {
    if (HL_Resignation_Type.featOkTst && ((HL_Resignation_Type)jcasType).casFeat_approvedBy == null)
      jcasType.jcas.throwFeatMissing("approvedBy", "ru.kfu.itis.issst.ner.typesystem.HL_Resignation");
    return (Annotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((HL_Resignation_Type)jcasType).casFeatCode_approvedBy)));}
    
  /** setter for approvedBy - sets  
   * @generated */
  public void setApprovedBy(Annotation v) {
    if (HL_Resignation_Type.featOkTst && ((HL_Resignation_Type)jcasType).casFeat_approvedBy == null)
      jcasType.jcas.throwFeatMissing("approvedBy", "ru.kfu.itis.issst.ner.typesystem.HL_Resignation");
    jcasType.ll_cas.ll_setRefValue(addr, ((HL_Resignation_Type)jcasType).casFeatCode_approvedBy, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    