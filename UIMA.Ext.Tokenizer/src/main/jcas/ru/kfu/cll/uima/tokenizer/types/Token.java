

/* First created by JCasGen Sat Mar 02 18:51:31 MSK 2013 */
package tokenization.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Sun Mar 10 20:12:40 MSK 2013
 * XML source: /home/marsel/Рабочий стол/desc/NLP@Cloud_Tokenizer_Descriptor.xml
 * @generated */
public class Token extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Token.class);
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
  protected Token() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Token(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Token(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Token(JCas jcas, int begin, int end) {
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
  //* Feature: Text

  /** getter for Text - gets Text of token.
   * @generated */
  public String getText() {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_Text == null)
      jcasType.jcas.throwFeatMissing("Text", "tokenization.types.Token");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Token_Type)jcasType).casFeatCode_Text);}
    
  /** setter for Text - sets Text of token. 
   * @generated */
  public void setText(String v) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_Text == null)
      jcasType.jcas.throwFeatMissing("Text", "tokenization.types.Token");
    jcasType.ll_cas.ll_setStringValue(addr, ((Token_Type)jcasType).casFeatCode_Text, v);}    
   
    
  //*--------------*
  //* Feature: Norm

  /** getter for Norm - gets 
   * @generated */
  public String getNorm() {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_Norm == null)
      jcasType.jcas.throwFeatMissing("Norm", "tokenization.types.Token");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Token_Type)jcasType).casFeatCode_Norm);}
    
  /** setter for Norm - sets  
   * @generated */
  public void setNorm(String v) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_Norm == null)
      jcasType.jcas.throwFeatMissing("Norm", "tokenization.types.Token");
    jcasType.ll_cas.ll_setStringValue(addr, ((Token_Type)jcasType).casFeatCode_Norm, v);}    
  }

    