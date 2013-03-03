

/* First created by JCasGen Sun Mar 03 18:46:12 MSK 2013 */
package ru.kfu.itis.issst.uima.ner.cas;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSArray;
import org.opencorpora.cas.Word;
import org.apache.uima.jcas.tcas.Annotation;


/** Possible Named Entity, represents boundaries of a name
 * Updated by JCasGen Sun Mar 03 18:46:12 MSK 2013
 * XML source: src/main/resources/ru/kfu/itis/issst/uima/ner/ts-ner.xml
 * @generated */
public class PossibleNE extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(PossibleNE.class);
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
  protected PossibleNE() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public PossibleNE(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public PossibleNE(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public PossibleNE(JCas jcas, int begin, int end) {
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
  //* Feature: words

  /** getter for words - gets 
   * @generated */
  public FSArray getWords() {
    if (PossibleNE_Type.featOkTst && ((PossibleNE_Type)jcasType).casFeat_words == null)
      jcasType.jcas.throwFeatMissing("words", "ru.kfu.itis.issst.uima.ner.cas.PossibleNE");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((PossibleNE_Type)jcasType).casFeatCode_words)));}
    
  /** setter for words - sets  
   * @generated */
  public void setWords(FSArray v) {
    if (PossibleNE_Type.featOkTst && ((PossibleNE_Type)jcasType).casFeat_words == null)
      jcasType.jcas.throwFeatMissing("words", "ru.kfu.itis.issst.uima.ner.cas.PossibleNE");
    jcasType.ll_cas.ll_setRefValue(addr, ((PossibleNE_Type)jcasType).casFeatCode_words, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for words - gets an indexed value - 
   * @generated */
  public Word getWords(int i) {
    if (PossibleNE_Type.featOkTst && ((PossibleNE_Type)jcasType).casFeat_words == null)
      jcasType.jcas.throwFeatMissing("words", "ru.kfu.itis.issst.uima.ner.cas.PossibleNE");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((PossibleNE_Type)jcasType).casFeatCode_words), i);
    return (Word)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((PossibleNE_Type)jcasType).casFeatCode_words), i)));}

  /** indexed setter for words - sets an indexed value - 
   * @generated */
  public void setWords(int i, Word v) { 
    if (PossibleNE_Type.featOkTst && ((PossibleNE_Type)jcasType).casFeat_words == null)
      jcasType.jcas.throwFeatMissing("words", "ru.kfu.itis.issst.uima.ner.cas.PossibleNE");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((PossibleNE_Type)jcasType).casFeatCode_words), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((PossibleNE_Type)jcasType).casFeatCode_words), i, jcasType.ll_cas.ll_getFSRef(v));}
   
    
  //*--------------*
  //* Feature: head

  /** getter for head - gets 
   * @generated */
  public Word getHead() {
    if (PossibleNE_Type.featOkTst && ((PossibleNE_Type)jcasType).casFeat_head == null)
      jcasType.jcas.throwFeatMissing("head", "ru.kfu.itis.issst.uima.ner.cas.PossibleNE");
    return (Word)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((PossibleNE_Type)jcasType).casFeatCode_head)));}
    
  /** setter for head - sets  
   * @generated */
  public void setHead(Word v) {
    if (PossibleNE_Type.featOkTst && ((PossibleNE_Type)jcasType).casFeat_head == null)
      jcasType.jcas.throwFeatMissing("head", "ru.kfu.itis.issst.uima.ner.cas.PossibleNE");
    jcasType.ll_cas.ll_setRefValue(addr, ((PossibleNE_Type)jcasType).casFeatCode_head, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    