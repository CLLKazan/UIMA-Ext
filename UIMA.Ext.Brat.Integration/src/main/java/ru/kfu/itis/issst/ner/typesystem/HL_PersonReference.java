

/* First created by JCasGen Tue Mar 12 00:30:02 MSK 2013 */
package ru.kfu.itis.issst.ner.typesystem;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Tue Mar 12 00:30:02 MSK 2013
 * XML source: /home/pathfinder/_WORK/Projects/BRATWorkspace/git/UIMA-Ext/UIMA.Ext.Brat.Integration/desc/UIMA2BratAnnotatorDescriptor.xml
 * @generated */
public class HL_PersonReference extends HL_EntityReference {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(HL_PersonReference.class);
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
  protected HL_PersonReference() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public HL_PersonReference(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public HL_PersonReference(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public HL_PersonReference(JCas jcas, int begin, int end) {
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
  public HL_Person getPerson() {
    if (HL_PersonReference_Type.featOkTst && ((HL_PersonReference_Type)jcasType).casFeat_person == null)
      jcasType.jcas.throwFeatMissing("person", "ru.kfu.itis.issst.ner.typesystem.HL_PersonReference");
    return (HL_Person)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((HL_PersonReference_Type)jcasType).casFeatCode_person)));}
    
  /** setter for person - sets  
   * @generated */
  public void setPerson(HL_Person v) {
    if (HL_PersonReference_Type.featOkTst && ((HL_PersonReference_Type)jcasType).casFeat_person == null)
      jcasType.jcas.throwFeatMissing("person", "ru.kfu.itis.issst.ner.typesystem.HL_PersonReference");
    jcasType.ll_cas.ll_setRefValue(addr, ((HL_PersonReference_Type)jcasType).casFeatCode_person, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    