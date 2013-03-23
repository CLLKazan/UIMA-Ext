

/* First created by JCasGen Fri Mar 22 16:53:11 MSK 2013 */
package ru.kfu.itis.issst.uima.phrrecog.cas;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.opencorpora.cas.Word;


/** Represents Noun Phrase
 * Updated by JCasGen Fri Mar 22 16:53:11 MSK 2013
 * XML source: src/main/resources/ru/kfu/itis/issst/uima/phrrecog/ts-phrase-recognizer.xml
 * @generated */
public class NounPhrase extends Phrase {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(NounPhrase.class);
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
  protected NounPhrase() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public NounPhrase(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public NounPhrase(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public NounPhrase(JCas jcas, int begin, int end) {
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
  //* Feature: preposition

  /** getter for preposition - gets 
   * @generated */
  public Word getPreposition() {
    if (NounPhrase_Type.featOkTst && ((NounPhrase_Type)jcasType).casFeat_preposition == null)
      jcasType.jcas.throwFeatMissing("preposition", "ru.kfu.itis.issst.uima.phrrecog.cas.NounPhrase");
    return (Word)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((NounPhrase_Type)jcasType).casFeatCode_preposition)));}
    
  /** setter for preposition - sets  
   * @generated */
  public void setPreposition(Word v) {
    if (NounPhrase_Type.featOkTst && ((NounPhrase_Type)jcasType).casFeat_preposition == null)
      jcasType.jcas.throwFeatMissing("preposition", "ru.kfu.itis.issst.uima.phrrecog.cas.NounPhrase");
    jcasType.ll_cas.ll_setRefValue(addr, ((NounPhrase_Type)jcasType).casFeatCode_preposition, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: particle

  /** getter for particle - gets 
   * @generated */
  public Word getParticle() {
    if (NounPhrase_Type.featOkTst && ((NounPhrase_Type)jcasType).casFeat_particle == null)
      jcasType.jcas.throwFeatMissing("particle", "ru.kfu.itis.issst.uima.phrrecog.cas.NounPhrase");
    return (Word)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((NounPhrase_Type)jcasType).casFeatCode_particle)));}
    
  /** setter for particle - sets  
   * @generated */
  public void setParticle(Word v) {
    if (NounPhrase_Type.featOkTst && ((NounPhrase_Type)jcasType).casFeat_particle == null)
      jcasType.jcas.throwFeatMissing("particle", "ru.kfu.itis.issst.uima.phrrecog.cas.NounPhrase");
    jcasType.ll_cas.ll_setRefValue(addr, ((NounPhrase_Type)jcasType).casFeatCode_particle, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    