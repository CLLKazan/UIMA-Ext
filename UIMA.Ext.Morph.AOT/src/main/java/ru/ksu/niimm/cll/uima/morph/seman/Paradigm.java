

/* First created by JCasGen Fri Apr 20 11:23:25 MSK 2012 */
package ru.ksu.niimm.cll.uima.morph.seman;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.jcas.cas.StringArray;


/** Wordform paradigm object
 * Updated by JCasGen Fri Apr 20 11:23:25 MSK 2012
 * XML source: D:/projects/uima-ext/UIMA.Ext.Morph.AOT/src/main/resources/ru/ksu/niimm/cll/uima/morph/seman/MorphologyTypeSystem.xml
 * @generated */
public class Paradigm extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Paradigm.class);
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
  protected Paradigm() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Paradigm(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Paradigm(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Paradigm(JCas jcas, int begin, int end) {
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
  //* Feature: pos

  /** getter for pos - gets part of speech
   * @generated */
  public String getPos() {
    if (Paradigm_Type.featOkTst && ((Paradigm_Type)jcasType).casFeat_pos == null)
      jcasType.jcas.throwFeatMissing("pos", "ru.ksu.niimm.cll.uima.morph.seman.Paradigm");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Paradigm_Type)jcasType).casFeatCode_pos);}
    
  /** setter for pos - sets part of speech 
   * @generated */
  public void setPos(String v) {
    if (Paradigm_Type.featOkTst && ((Paradigm_Type)jcasType).casFeat_pos == null)
      jcasType.jcas.throwFeatMissing("pos", "ru.ksu.niimm.cll.uima.morph.seman.Paradigm");
    jcasType.ll_cas.ll_setStringValue(addr, ((Paradigm_Type)jcasType).casFeatCode_pos, v);}    
   
    
  //*--------------*
  //* Feature: grammems

  /** getter for grammems - gets paradigm-specific grammems
   * @generated */
  public StringArray getGrammems() {
    if (Paradigm_Type.featOkTst && ((Paradigm_Type)jcasType).casFeat_grammems == null)
      jcasType.jcas.throwFeatMissing("grammems", "ru.ksu.niimm.cll.uima.morph.seman.Paradigm");
    return (StringArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Paradigm_Type)jcasType).casFeatCode_grammems)));}
    
  /** setter for grammems - sets paradigm-specific grammems 
   * @generated */
  public void setGrammems(StringArray v) {
    if (Paradigm_Type.featOkTst && ((Paradigm_Type)jcasType).casFeat_grammems == null)
      jcasType.jcas.throwFeatMissing("grammems", "ru.ksu.niimm.cll.uima.morph.seman.Paradigm");
    jcasType.ll_cas.ll_setRefValue(addr, ((Paradigm_Type)jcasType).casFeatCode_grammems, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for grammems - gets an indexed value - paradigm-specific grammems
   * @generated */
  public String getGrammems(int i) {
    if (Paradigm_Type.featOkTst && ((Paradigm_Type)jcasType).casFeat_grammems == null)
      jcasType.jcas.throwFeatMissing("grammems", "ru.ksu.niimm.cll.uima.morph.seman.Paradigm");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Paradigm_Type)jcasType).casFeatCode_grammems), i);
    return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Paradigm_Type)jcasType).casFeatCode_grammems), i);}

  /** indexed setter for grammems - sets an indexed value - paradigm-specific grammems
   * @generated */
  public void setGrammems(int i, String v) { 
    if (Paradigm_Type.featOkTst && ((Paradigm_Type)jcasType).casFeat_grammems == null)
      jcasType.jcas.throwFeatMissing("grammems", "ru.ksu.niimm.cll.uima.morph.seman.Paradigm");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Paradigm_Type)jcasType).casFeatCode_grammems), i);
    jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Paradigm_Type)jcasType).casFeatCode_grammems), i, v);}
   
    
  //*--------------*
  //* Feature: lemma

  /** getter for lemma - gets lemma, canonical form of lexem
   * @generated */
  public String getLemma() {
    if (Paradigm_Type.featOkTst && ((Paradigm_Type)jcasType).casFeat_lemma == null)
      jcasType.jcas.throwFeatMissing("lemma", "ru.ksu.niimm.cll.uima.morph.seman.Paradigm");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Paradigm_Type)jcasType).casFeatCode_lemma);}
    
  /** setter for lemma - sets lemma, canonical form of lexem 
   * @generated */
  public void setLemma(String v) {
    if (Paradigm_Type.featOkTst && ((Paradigm_Type)jcasType).casFeat_lemma == null)
      jcasType.jcas.throwFeatMissing("lemma", "ru.ksu.niimm.cll.uima.morph.seman.Paradigm");
    jcasType.ll_cas.ll_setStringValue(addr, ((Paradigm_Type)jcasType).casFeatCode_lemma, v);}    
   
    
  //*--------------*
  //* Feature: paradigmId

  /** getter for paradigmId - gets dictionary paradigm id. If paradigm is predicted value will be negative
   * @generated */
  public int getParadigmId() {
    if (Paradigm_Type.featOkTst && ((Paradigm_Type)jcasType).casFeat_paradigmId == null)
      jcasType.jcas.throwFeatMissing("paradigmId", "ru.ksu.niimm.cll.uima.morph.seman.Paradigm");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Paradigm_Type)jcasType).casFeatCode_paradigmId);}
    
  /** setter for paradigmId - sets dictionary paradigm id. If paradigm is predicted value will be negative 
   * @generated */
  public void setParadigmId(int v) {
    if (Paradigm_Type.featOkTst && ((Paradigm_Type)jcasType).casFeat_paradigmId == null)
      jcasType.jcas.throwFeatMissing("paradigmId", "ru.ksu.niimm.cll.uima.morph.seman.Paradigm");
    jcasType.ll_cas.ll_setIntValue(addr, ((Paradigm_Type)jcasType).casFeatCode_paradigmId, v);}    
  }

    