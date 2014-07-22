

/* First created by JCasGen Tue Jul 16 19:01:56 MSD 2013 */
package org.opencorpora.cas;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Tue Jul 16 19:01:56 MSD 2013
 * XML source: src/main/resources/org/opencorpora/morphology-ts.xml
 * @generated */
public class Word extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Word.class);
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
  protected Word() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Word(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Word(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Word(JCas jcas, int begin, int end) {
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
  //* Feature: wordforms

  /** getter for wordforms - gets 
   * @generated */
  public FSArray getWordforms() {
    if (Word_Type.featOkTst && ((Word_Type)jcasType).casFeat_wordforms == null)
      jcasType.jcas.throwFeatMissing("wordforms", "org.opencorpora.cas.Word");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Word_Type)jcasType).casFeatCode_wordforms)));}
    
  /** setter for wordforms - sets  
   * @generated */
  public void setWordforms(FSArray v) {
    if (Word_Type.featOkTst && ((Word_Type)jcasType).casFeat_wordforms == null)
      jcasType.jcas.throwFeatMissing("wordforms", "org.opencorpora.cas.Word");
    jcasType.ll_cas.ll_setRefValue(addr, ((Word_Type)jcasType).casFeatCode_wordforms, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for wordforms - gets an indexed value - 
   * @generated */
  public Wordform getWordforms(int i) {
    if (Word_Type.featOkTst && ((Word_Type)jcasType).casFeat_wordforms == null)
      jcasType.jcas.throwFeatMissing("wordforms", "org.opencorpora.cas.Word");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Word_Type)jcasType).casFeatCode_wordforms), i);
    return (Wordform)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Word_Type)jcasType).casFeatCode_wordforms), i)));}

  /** indexed setter for wordforms - sets an indexed value - 
   * @generated */
  public void setWordforms(int i, Wordform v) { 
    if (Word_Type.featOkTst && ((Word_Type)jcasType).casFeat_wordforms == null)
      jcasType.jcas.throwFeatMissing("wordforms", "org.opencorpora.cas.Word");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Word_Type)jcasType).casFeatCode_wordforms), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Word_Type)jcasType).casFeatCode_wordforms), i, jcasType.ll_cas.ll_getFSRef(v));}
   
    
  //*--------------*
  //* Feature: token

  /** getter for token - gets 
   * @generated */
  public Annotation getToken() {
    if (Word_Type.featOkTst && ((Word_Type)jcasType).casFeat_token == null)
      jcasType.jcas.throwFeatMissing("token", "org.opencorpora.cas.Word");
    return (Annotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Word_Type)jcasType).casFeatCode_token)));}
    
  /** setter for token - sets  
   * @generated */
  public void setToken(Annotation v) {
    if (Word_Type.featOkTst && ((Word_Type)jcasType).casFeat_token == null)
      jcasType.jcas.throwFeatMissing("token", "org.opencorpora.cas.Word");
    jcasType.ll_cas.ll_setRefValue(addr, ((Word_Type)jcasType).casFeatCode_token, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    