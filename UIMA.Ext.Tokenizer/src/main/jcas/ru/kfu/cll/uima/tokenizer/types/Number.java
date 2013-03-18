

/* First created by JCasGen Sat Mar 02 20:27:33 MSK 2013 */
package tokenization.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Sun Mar 10 20:12:40 MSK 2013
 * XML source: /home/marsel/Рабочий стол/desc/NLP@Cloud_Tokenizer_Descriptor.xml
 * @generated */
public class Number extends Token {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Number.class);
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
  protected Number() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Number(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Number(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Number(JCas jcas, int begin, int end) {
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
  //* Feature: Kind

  /** getter for Kind - gets 
   * @generated */
  public String getKind() {
    if (Number_Type.featOkTst && ((Number_Type)jcasType).casFeat_Kind == null)
      jcasType.jcas.throwFeatMissing("Kind", "tokenization.types.Number");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Number_Type)jcasType).casFeatCode_Kind);}
    
  /** setter for Kind - sets  
   * @generated */
  public void setKind(String v) {
    if (Number_Type.featOkTst && ((Number_Type)jcasType).casFeat_Kind == null)
      jcasType.jcas.throwFeatMissing("Kind", "tokenization.types.Number");
    jcasType.ll_cas.ll_setStringValue(addr, ((Number_Type)jcasType).casFeatCode_Kind, v);}    
   
    
  //*--------------*
  //* Feature: Sign

  /** getter for Sign - gets 
   * @generated */
  public String getSign() {
    if (Number_Type.featOkTst && ((Number_Type)jcasType).casFeat_Sign == null)
      jcasType.jcas.throwFeatMissing("Sign", "tokenization.types.Number");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Number_Type)jcasType).casFeatCode_Sign);}
    
  /** setter for Sign - sets  
   * @generated */
  public void setSign(String v) {
    if (Number_Type.featOkTst && ((Number_Type)jcasType).casFeat_Sign == null)
      jcasType.jcas.throwFeatMissing("Sign", "tokenization.types.Number");
    jcasType.ll_cas.ll_setStringValue(addr, ((Number_Type)jcasType).casFeatCode_Sign, v);}    
  }

    