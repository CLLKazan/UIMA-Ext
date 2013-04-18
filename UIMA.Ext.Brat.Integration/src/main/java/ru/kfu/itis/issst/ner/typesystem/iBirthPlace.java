

/* First created by JCasGen Tue Mar 12 00:30:02 MSK 2013 */
package ru.kfu.itis.issst.ner.typesystem;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Tue Mar 12 00:30:02 MSK 2013
 * XML source: /home/pathfinder/_WORK/Projects/BRATWorkspace/git/UIMA-Ext/UIMA.Ext.Brat.Integration/desc/UIMA2BratAnnotatorDescriptor.xml
 * @generated */
public class iBirthPlace extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(iBirthPlace.class);
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
  protected iBirthPlace() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public iBirthPlace(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public iBirthPlace(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public iBirthPlace(JCas jcas, int begin, int end) {
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
  public iPerson getPerson() {
    if (iBirthPlace_Type.featOkTst && ((iBirthPlace_Type)jcasType).casFeat_person == null)
      jcasType.jcas.throwFeatMissing("person", "ru.kfu.itis.issst.ner.typesystem.iBirthPlace");
    return (iPerson)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((iBirthPlace_Type)jcasType).casFeatCode_person)));}
    
  /** setter for person - sets  
   * @generated */
  public void setPerson(iPerson v) {
    if (iBirthPlace_Type.featOkTst && ((iBirthPlace_Type)jcasType).casFeat_person == null)
      jcasType.jcas.throwFeatMissing("person", "ru.kfu.itis.issst.ner.typesystem.iBirthPlace");
    jcasType.ll_cas.ll_setRefValue(addr, ((iBirthPlace_Type)jcasType).casFeatCode_person, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: city

  /** getter for city - gets 
   * @generated */
  public iCity getCity() {
    if (iBirthPlace_Type.featOkTst && ((iBirthPlace_Type)jcasType).casFeat_city == null)
      jcasType.jcas.throwFeatMissing("city", "ru.kfu.itis.issst.ner.typesystem.iBirthPlace");
    return (iCity)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((iBirthPlace_Type)jcasType).casFeatCode_city)));}
    
  /** setter for city - sets  
   * @generated */
  public void setCity(iCity v) {
    if (iBirthPlace_Type.featOkTst && ((iBirthPlace_Type)jcasType).casFeat_city == null)
      jcasType.jcas.throwFeatMissing("city", "ru.kfu.itis.issst.ner.typesystem.iBirthPlace");
    jcasType.ll_cas.ll_setRefValue(addr, ((iBirthPlace_Type)jcasType).casFeatCode_city, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    