

/* First created by JCasGen Mon Mar 17 18:18:37 MSK 2014 */
package ru.kfu.itis.issst.uima.depparser;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.opencorpora.cas.Word;
import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Mon Mar 17 18:18:37 MSK 2014
 * XML source: src/main/resources/ru/kfu/itis/issst/uima/depparser/dependency-ts.xml
 * @generated */
public class Dependency extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Dependency.class);
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
  protected Dependency() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Dependency(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Dependency(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Dependency(JCas jcas, int begin, int end) {
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
  //* Feature: dependent

  /** getter for dependent - gets 
   * @generated */
  public Word getDependent() {
    if (Dependency_Type.featOkTst && ((Dependency_Type)jcasType).casFeat_dependent == null)
      jcasType.jcas.throwFeatMissing("dependent", "ru.kfu.itis.issst.uima.depparser.Dependency");
    return (Word)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Dependency_Type)jcasType).casFeatCode_dependent)));}
    
  /** setter for dependent - sets  
   * @generated */
  public void setDependent(Word v) {
    if (Dependency_Type.featOkTst && ((Dependency_Type)jcasType).casFeat_dependent == null)
      jcasType.jcas.throwFeatMissing("dependent", "ru.kfu.itis.issst.uima.depparser.Dependency");
    jcasType.ll_cas.ll_setRefValue(addr, ((Dependency_Type)jcasType).casFeatCode_dependent, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: head

  /** getter for head - gets 
   * @generated */
  public Word getHead() {
    if (Dependency_Type.featOkTst && ((Dependency_Type)jcasType).casFeat_head == null)
      jcasType.jcas.throwFeatMissing("head", "ru.kfu.itis.issst.uima.depparser.Dependency");
    return (Word)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Dependency_Type)jcasType).casFeatCode_head)));}
    
  /** setter for head - sets  
   * @generated */
  public void setHead(Word v) {
    if (Dependency_Type.featOkTst && ((Dependency_Type)jcasType).casFeat_head == null)
      jcasType.jcas.throwFeatMissing("head", "ru.kfu.itis.issst.uima.depparser.Dependency");
    jcasType.ll_cas.ll_setRefValue(addr, ((Dependency_Type)jcasType).casFeatCode_head, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: label

  /** getter for label - gets 
   * @generated */
  public String getLabel() {
    if (Dependency_Type.featOkTst && ((Dependency_Type)jcasType).casFeat_label == null)
      jcasType.jcas.throwFeatMissing("label", "ru.kfu.itis.issst.uima.depparser.Dependency");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Dependency_Type)jcasType).casFeatCode_label);}
    
  /** setter for label - sets  
   * @generated */
  public void setLabel(String v) {
    if (Dependency_Type.featOkTst && ((Dependency_Type)jcasType).casFeat_label == null)
      jcasType.jcas.throwFeatMissing("label", "ru.kfu.itis.issst.uima.depparser.Dependency");
    jcasType.ll_cas.ll_setStringValue(addr, ((Dependency_Type)jcasType).casFeatCode_label, v);}    
  }

    