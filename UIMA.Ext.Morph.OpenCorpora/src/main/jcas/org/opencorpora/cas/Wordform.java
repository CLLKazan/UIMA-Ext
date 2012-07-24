

/* First created by JCasGen Tue Jul 24 17:36:26 MSK 2012 */
package org.opencorpora.cas;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.cas.StringArray;


/** 
 * Updated by JCasGen Tue Jul 24 17:36:26 MSK 2012
 * XML source: D:/projects/uima-ext/UIMA.Ext.Morph.OpenCorpora/src/main/resources/org/opencorpora/morphology-ts.xml
 * @generated */
public class Wordform extends TOP {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(Wordform.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Wordform() {}
    
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

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {}
     
 
    
  //*--------------*
  //* Feature: pos

  /** getter for pos - gets 
   * @generated */
  public String getPos() {
    if (Wordform_Type.featOkTst && ((Wordform_Type)jcasType).casFeat_pos == null)
      jcasType.jcas.throwFeatMissing("pos", "org.opencorpora.cas.Wordform");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Wordform_Type)jcasType).casFeatCode_pos);}
    
  /** setter for pos - sets  
   * @generated */
  public void setPos(String v) {
    if (Wordform_Type.featOkTst && ((Wordform_Type)jcasType).casFeat_pos == null)
      jcasType.jcas.throwFeatMissing("pos", "org.opencorpora.cas.Wordform");
    jcasType.ll_cas.ll_setStringValue(addr, ((Wordform_Type)jcasType).casFeatCode_pos, v);}    
   
    
  //*--------------*
  //* Feature: lemma

  /** getter for lemma - gets 
   * @generated */
  public String getLemma() {
    if (Wordform_Type.featOkTst && ((Wordform_Type)jcasType).casFeat_lemma == null)
      jcasType.jcas.throwFeatMissing("lemma", "org.opencorpora.cas.Wordform");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Wordform_Type)jcasType).casFeatCode_lemma);}
    
  /** setter for lemma - sets  
   * @generated */
  public void setLemma(String v) {
    if (Wordform_Type.featOkTst && ((Wordform_Type)jcasType).casFeat_lemma == null)
      jcasType.jcas.throwFeatMissing("lemma", "org.opencorpora.cas.Wordform");
    jcasType.ll_cas.ll_setStringValue(addr, ((Wordform_Type)jcasType).casFeatCode_lemma, v);}    
   
    
  //*--------------*
  //* Feature: lemmaId

  /** getter for lemmaId - gets 
   * @generated */
  public int getLemmaId() {
    if (Wordform_Type.featOkTst && ((Wordform_Type)jcasType).casFeat_lemmaId == null)
      jcasType.jcas.throwFeatMissing("lemmaId", "org.opencorpora.cas.Wordform");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Wordform_Type)jcasType).casFeatCode_lemmaId);}
    
  /** setter for lemmaId - sets  
   * @generated */
  public void setLemmaId(int v) {
    if (Wordform_Type.featOkTst && ((Wordform_Type)jcasType).casFeat_lemmaId == null)
      jcasType.jcas.throwFeatMissing("lemmaId", "org.opencorpora.cas.Wordform");
    jcasType.ll_cas.ll_setIntValue(addr, ((Wordform_Type)jcasType).casFeatCode_lemmaId, v);}    
   
    
  //*--------------*
  //* Feature: grammems

  /** getter for grammems - gets 
   * @generated */
  public StringArray getGrammems() {
    if (Wordform_Type.featOkTst && ((Wordform_Type)jcasType).casFeat_grammems == null)
      jcasType.jcas.throwFeatMissing("grammems", "org.opencorpora.cas.Wordform");
    return (StringArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Wordform_Type)jcasType).casFeatCode_grammems)));}
    
  /** setter for grammems - sets  
   * @generated */
  public void setGrammems(StringArray v) {
    if (Wordform_Type.featOkTst && ((Wordform_Type)jcasType).casFeat_grammems == null)
      jcasType.jcas.throwFeatMissing("grammems", "org.opencorpora.cas.Wordform");
    jcasType.ll_cas.ll_setRefValue(addr, ((Wordform_Type)jcasType).casFeatCode_grammems, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for grammems - gets an indexed value - 
   * @generated */
  public String getGrammems(int i) {
    if (Wordform_Type.featOkTst && ((Wordform_Type)jcasType).casFeat_grammems == null)
      jcasType.jcas.throwFeatMissing("grammems", "org.opencorpora.cas.Wordform");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Wordform_Type)jcasType).casFeatCode_grammems), i);
    return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Wordform_Type)jcasType).casFeatCode_grammems), i);}

  /** indexed setter for grammems - sets an indexed value - 
   * @generated */
  public void setGrammems(int i, String v) { 
    if (Wordform_Type.featOkTst && ((Wordform_Type)jcasType).casFeat_grammems == null)
      jcasType.jcas.throwFeatMissing("grammems", "org.opencorpora.cas.Wordform");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Wordform_Type)jcasType).casFeatCode_grammems), i);
    jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Wordform_Type)jcasType).casFeatCode_grammems), i, v);}
  }

    