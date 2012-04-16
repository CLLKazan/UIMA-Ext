

/* First created by JCasGen Mon Apr 16 22:33:21 MSK 2012 */
package ru.ksu.niimm.cll.uima.morph.seman;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.jcas.cas.StringArray;


/** Wordform object
 * Updated by JCasGen Mon Apr 16 22:33:21 MSK 2012
 * XML source: D:/projects/uima-ext/UIMA.Ext.Morph.AOT/src/main/resources/ru/ksu/niimm/cll/uima/morph/seman/MorphologyTypeSystem.xml
 * @generated */
public class Wordform extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Wordform.class);
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
  protected Wordform() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Wordform(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Wordform(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Wordform(JCas jcas, int begin, int end) {
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
  //* Feature: paradigm

  /** getter for paradigm - gets paradigm
   * @generated */
  public Paradigm getParadigm() {
    if (Wordform_Type.featOkTst && ((Wordform_Type)jcasType).casFeat_paradigm == null)
      jcasType.jcas.throwFeatMissing("paradigm", "ru.ksu.niimm.cll.uima.morph.seman.Wordform");
    return (Paradigm)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Wordform_Type)jcasType).casFeatCode_paradigm)));}
    
  /** setter for paradigm - sets paradigm 
   * @generated */
  public void setParadigm(Paradigm v) {
    if (Wordform_Type.featOkTst && ((Wordform_Type)jcasType).casFeat_paradigm == null)
      jcasType.jcas.throwFeatMissing("paradigm", "ru.ksu.niimm.cll.uima.morph.seman.Wordform");
    jcasType.ll_cas.ll_setRefValue(addr, ((Wordform_Type)jcasType).casFeatCode_paradigm, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: grammems

  /** getter for grammems - gets wordform-specific grammems
   * @generated */
  public StringArray getGrammems() {
    if (Wordform_Type.featOkTst && ((Wordform_Type)jcasType).casFeat_grammems == null)
      jcasType.jcas.throwFeatMissing("grammems", "ru.ksu.niimm.cll.uima.morph.seman.Wordform");
    return (StringArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Wordform_Type)jcasType).casFeatCode_grammems)));}
    
  /** setter for grammems - sets wordform-specific grammems 
   * @generated */
  public void setGrammems(StringArray v) {
    if (Wordform_Type.featOkTst && ((Wordform_Type)jcasType).casFeat_grammems == null)
      jcasType.jcas.throwFeatMissing("grammems", "ru.ksu.niimm.cll.uima.morph.seman.Wordform");
    jcasType.ll_cas.ll_setRefValue(addr, ((Wordform_Type)jcasType).casFeatCode_grammems, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for grammems - gets an indexed value - wordform-specific grammems
   * @generated */
  public String getGrammems(int i) {
    if (Wordform_Type.featOkTst && ((Wordform_Type)jcasType).casFeat_grammems == null)
      jcasType.jcas.throwFeatMissing("grammems", "ru.ksu.niimm.cll.uima.morph.seman.Wordform");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Wordform_Type)jcasType).casFeatCode_grammems), i);
    return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Wordform_Type)jcasType).casFeatCode_grammems), i);}

  /** indexed setter for grammems - sets an indexed value - wordform-specific grammems
   * @generated */
  public void setGrammems(int i, String v) { 
    if (Wordform_Type.featOkTst && ((Wordform_Type)jcasType).casFeat_grammems == null)
      jcasType.jcas.throwFeatMissing("grammems", "ru.ksu.niimm.cll.uima.morph.seman.Wordform");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Wordform_Type)jcasType).casFeatCode_grammems), i);
    jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Wordform_Type)jcasType).casFeatCode_grammems), i, v);}
   
    
  //*--------------*
  //* Feature: flexionNo

  /** getter for flexionNo - gets flexion number
   * @generated */
  public long getFlexionNo() {
    if (Wordform_Type.featOkTst && ((Wordform_Type)jcasType).casFeat_flexionNo == null)
      jcasType.jcas.throwFeatMissing("flexionNo", "ru.ksu.niimm.cll.uima.morph.seman.Wordform");
    return jcasType.ll_cas.ll_getLongValue(addr, ((Wordform_Type)jcasType).casFeatCode_flexionNo);}
    
  /** setter for flexionNo - sets flexion number 
   * @generated */
  public void setFlexionNo(long v) {
    if (Wordform_Type.featOkTst && ((Wordform_Type)jcasType).casFeat_flexionNo == null)
      jcasType.jcas.throwFeatMissing("flexionNo", "ru.ksu.niimm.cll.uima.morph.seman.Wordform");
    jcasType.ll_cas.ll_setLongValue(addr, ((Wordform_Type)jcasType).casFeatCode_flexionNo, v);}    
  }

    