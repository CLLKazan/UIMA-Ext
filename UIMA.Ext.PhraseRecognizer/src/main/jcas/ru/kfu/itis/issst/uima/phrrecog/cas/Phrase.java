

/* First created by JCasGen Fri Mar 01 17:08:28 MSK 2013 */
package ru.kfu.itis.issst.uima.phrrecog.cas;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSArray;
import org.opencorpora.cas.Word;
import org.apache.uima.jcas.tcas.Annotation;


/** Represents typed 'phrase', i.e. a head word with its dependents.
				Phrase annotation should have the same borders with its head word.
 * Updated by JCasGen Fri Mar 01 17:08:28 MSK 2013
 * XML source: src/main/resources/ru/kfu/itis/issst/uima/phrrecog/ts-phrase-recognizer.xml
 * @generated */
public class Phrase extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Phrase.class);
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
  protected Phrase() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Phrase(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Phrase(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Phrase(JCas jcas, int begin, int end) {
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
  //* Feature: head

  /** getter for head - gets 
   * @generated */
  public Word getHead() {
    if (Phrase_Type.featOkTst && ((Phrase_Type)jcasType).casFeat_head == null)
      jcasType.jcas.throwFeatMissing("head", "ru.kfu.itis.issst.uima.phrrecog.cas.Phrase");
    return (Word)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Phrase_Type)jcasType).casFeatCode_head)));}
    
  /** setter for head - sets  
   * @generated */
  public void setHead(Word v) {
    if (Phrase_Type.featOkTst && ((Phrase_Type)jcasType).casFeat_head == null)
      jcasType.jcas.throwFeatMissing("head", "ru.kfu.itis.issst.uima.phrrecog.cas.Phrase");
    jcasType.ll_cas.ll_setRefValue(addr, ((Phrase_Type)jcasType).casFeatCode_head, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: dependents

  /** getter for dependents - gets 
   * @generated */
  public FSArray getDependents() {
    if (Phrase_Type.featOkTst && ((Phrase_Type)jcasType).casFeat_dependents == null)
      jcasType.jcas.throwFeatMissing("dependents", "ru.kfu.itis.issst.uima.phrrecog.cas.Phrase");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Phrase_Type)jcasType).casFeatCode_dependents)));}
    
  /** setter for dependents - sets  
   * @generated */
  public void setDependents(FSArray v) {
    if (Phrase_Type.featOkTst && ((Phrase_Type)jcasType).casFeat_dependents == null)
      jcasType.jcas.throwFeatMissing("dependents", "ru.kfu.itis.issst.uima.phrrecog.cas.Phrase");
    jcasType.ll_cas.ll_setRefValue(addr, ((Phrase_Type)jcasType).casFeatCode_dependents, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for dependents - gets an indexed value - 
   * @generated */
  public Word getDependents(int i) {
    if (Phrase_Type.featOkTst && ((Phrase_Type)jcasType).casFeat_dependents == null)
      jcasType.jcas.throwFeatMissing("dependents", "ru.kfu.itis.issst.uima.phrrecog.cas.Phrase");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Phrase_Type)jcasType).casFeatCode_dependents), i);
    return (Word)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Phrase_Type)jcasType).casFeatCode_dependents), i)));}

  /** indexed setter for dependents - sets an indexed value - 
   * @generated */
  public void setDependents(int i, Word v) { 
    if (Phrase_Type.featOkTst && ((Phrase_Type)jcasType).casFeat_dependents == null)
      jcasType.jcas.throwFeatMissing("dependents", "ru.kfu.itis.issst.uima.phrrecog.cas.Phrase");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Phrase_Type)jcasType).casFeatCode_dependents), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Phrase_Type)jcasType).casFeatCode_dependents), i, jcasType.ll_cas.ll_getFSRef(v));}
  }

    