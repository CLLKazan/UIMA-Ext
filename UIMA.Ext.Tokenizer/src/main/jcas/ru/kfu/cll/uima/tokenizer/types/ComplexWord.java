

/* First created by JCasGen Sat Mar 09 23:24:41 MSK 2013 */
package tokenization.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Sun Mar 10 20:12:40 MSK 2013
 * XML source: /home/marsel/Рабочий стол/desc/NLP@Cloud_Tokenizer_Descriptor.xml
 * @generated */
public class ComplexWord extends Token {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(ComplexWord.class);
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
  protected ComplexWord() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public ComplexWord(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public ComplexWord(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public ComplexWord(JCas jcas, int begin, int end) {
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
  //* Feature: Left

  /** getter for Left - gets 
   * @generated */
  public String getLeft() {
    if (ComplexWord_Type.featOkTst && ((ComplexWord_Type)jcasType).casFeat_Left == null)
      jcasType.jcas.throwFeatMissing("Left", "tokenization.types.ComplexWord");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ComplexWord_Type)jcasType).casFeatCode_Left);}
    
  /** setter for Left - sets  
   * @generated */
  public void setLeft(String v) {
    if (ComplexWord_Type.featOkTst && ((ComplexWord_Type)jcasType).casFeat_Left == null)
      jcasType.jcas.throwFeatMissing("Left", "tokenization.types.ComplexWord");
    jcasType.ll_cas.ll_setStringValue(addr, ((ComplexWord_Type)jcasType).casFeatCode_Left, v);}    
   
    
  //*--------------*
  //* Feature: Right

  /** getter for Right - gets 
   * @generated */
  public String getRight() {
    if (ComplexWord_Type.featOkTst && ((ComplexWord_Type)jcasType).casFeat_Right == null)
      jcasType.jcas.throwFeatMissing("Right", "tokenization.types.ComplexWord");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ComplexWord_Type)jcasType).casFeatCode_Right);}
    
  /** setter for Right - sets  
   * @generated */
  public void setRight(String v) {
    if (ComplexWord_Type.featOkTst && ((ComplexWord_Type)jcasType).casFeat_Right == null)
      jcasType.jcas.throwFeatMissing("Right", "tokenization.types.ComplexWord");
    jcasType.ll_cas.ll_setStringValue(addr, ((ComplexWord_Type)jcasType).casFeatCode_Right, v);}    
  }

    