

/* First created by JCasGen Sat Mar 09 01:46:51 MSK 2013 */
package tokenization.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Sun Mar 10 20:12:40 MSK 2013
 * XML source: /home/marsel/Рабочий стол/desc/NLP@Cloud_Tokenizer_Descriptor.xml
 * @generated */
public class Currensy extends Token {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Currensy.class);
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
  protected Currensy() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Currensy(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Currensy(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Currensy(JCas jcas, int begin, int end) {
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
  //* Feature: Value

  /** getter for Value - gets 
   * @generated */
  public String getValue() {
    if (Currensy_Type.featOkTst && ((Currensy_Type)jcasType).casFeat_Value == null)
      jcasType.jcas.throwFeatMissing("Value", "tokenization.types.Currensy");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Currensy_Type)jcasType).casFeatCode_Value);}
    
  /** setter for Value - sets  
   * @generated */
  public void setValue(String v) {
    if (Currensy_Type.featOkTst && ((Currensy_Type)jcasType).casFeat_Value == null)
      jcasType.jcas.throwFeatMissing("Value", "tokenization.types.Currensy");
    jcasType.ll_cas.ll_setStringValue(addr, ((Currensy_Type)jcasType).casFeatCode_Value, v);}    
   
    
  //*--------------*
  //* Feature: CurrensySymbol

  /** getter for CurrensySymbol - gets 
   * @generated */
  public String getCurrensySymbol() {
    if (Currensy_Type.featOkTst && ((Currensy_Type)jcasType).casFeat_CurrensySymbol == null)
      jcasType.jcas.throwFeatMissing("CurrensySymbol", "tokenization.types.Currensy");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Currensy_Type)jcasType).casFeatCode_CurrensySymbol);}
    
  /** setter for CurrensySymbol - sets  
   * @generated */
  public void setCurrensySymbol(String v) {
    if (Currensy_Type.featOkTst && ((Currensy_Type)jcasType).casFeat_CurrensySymbol == null)
      jcasType.jcas.throwFeatMissing("CurrensySymbol", "tokenization.types.Currensy");
    jcasType.ll_cas.ll_setStringValue(addr, ((Currensy_Type)jcasType).casFeatCode_CurrensySymbol, v);}    
  }

    