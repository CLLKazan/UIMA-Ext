

/* First created by JCasGen Sun Mar 03 15:23:57 MSK 2013 */
package tokenization.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Sun Mar 10 20:12:40 MSK 2013
 * XML source: /home/marsel/Рабочий стол/desc/NLP@Cloud_Tokenizer_Descriptor.xml
 * @generated */
public class Range extends Token {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Range.class);
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
  protected Range() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Range(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Range(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Range(JCas jcas, int begin, int end) {
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
    if (Range_Type.featOkTst && ((Range_Type)jcasType).casFeat_Left == null)
      jcasType.jcas.throwFeatMissing("Left", "tokenization.types.Range");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Range_Type)jcasType).casFeatCode_Left);}
    
  /** setter for Left - sets  
   * @generated */
  public void setLeft(String v) {
    if (Range_Type.featOkTst && ((Range_Type)jcasType).casFeat_Left == null)
      jcasType.jcas.throwFeatMissing("Left", "tokenization.types.Range");
    jcasType.ll_cas.ll_setStringValue(addr, ((Range_Type)jcasType).casFeatCode_Left, v);}    
   
    
  //*--------------*
  //* Feature: Right

  /** getter for Right - gets 
   * @generated */
  public String getRight() {
    if (Range_Type.featOkTst && ((Range_Type)jcasType).casFeat_Right == null)
      jcasType.jcas.throwFeatMissing("Right", "tokenization.types.Range");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Range_Type)jcasType).casFeatCode_Right);}
    
  /** setter for Right - sets  
   * @generated */
  public void setRight(String v) {
    if (Range_Type.featOkTst && ((Range_Type)jcasType).casFeat_Right == null)
      jcasType.jcas.throwFeatMissing("Right", "tokenization.types.Range");
    jcasType.ll_cas.ll_setStringValue(addr, ((Range_Type)jcasType).casFeatCode_Right, v);}    
  }

    