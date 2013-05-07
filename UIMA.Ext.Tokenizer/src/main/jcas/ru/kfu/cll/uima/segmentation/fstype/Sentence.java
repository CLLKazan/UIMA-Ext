

/* First created by JCasGen Tue May 07 16:55:51 MSD 2013 */
package ru.kfu.cll.uima.segmentation.fstype;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;
import ru.kfu.cll.uima.tokenizer.fstype.TokenBase;


/** 
 * Updated by JCasGen Tue May 07 16:55:51 MSD 2013
 * XML source: /home/rgareev/projects/uima-ext/UIMA.Ext.Tokenizer/src/main/resources/ru/kfu/cll/uima/segmentation/segmentation-TypeSystem.xml
 * @generated */
public class Sentence extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Sentence.class);
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
  protected Sentence() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Sentence(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Sentence(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Sentence(JCas jcas, int begin, int end) {
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
  //* Feature: firstToken

  /** getter for firstToken - gets the first token of a sentence
   * @generated */
  public TokenBase getFirstToken() {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_firstToken == null)
      jcasType.jcas.throwFeatMissing("firstToken", "ru.kfu.cll.uima.segmentation.fstype.Sentence");
    return (TokenBase)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_firstToken)));}
    
  /** setter for firstToken - sets the first token of a sentence 
   * @generated */
  public void setFirstToken(TokenBase v) {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_firstToken == null)
      jcasType.jcas.throwFeatMissing("firstToken", "ru.kfu.cll.uima.segmentation.fstype.Sentence");
    jcasType.ll_cas.ll_setRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_firstToken, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: lastToken

  /** getter for lastToken - gets the last token of a sentence
   * @generated */
  public TokenBase getLastToken() {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_lastToken == null)
      jcasType.jcas.throwFeatMissing("lastToken", "ru.kfu.cll.uima.segmentation.fstype.Sentence");
    return (TokenBase)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_lastToken)));}
    
  /** setter for lastToken - sets the last token of a sentence 
   * @generated */
  public void setLastToken(TokenBase v) {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_lastToken == null)
      jcasType.jcas.throwFeatMissing("lastToken", "ru.kfu.cll.uima.segmentation.fstype.Sentence");
    jcasType.ll_cas.ll_setRefValue(addr, ((Sentence_Type)jcasType).casFeatCode_lastToken, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    