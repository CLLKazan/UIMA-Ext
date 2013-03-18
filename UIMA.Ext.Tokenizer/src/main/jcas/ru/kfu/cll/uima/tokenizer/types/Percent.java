

/* First created by JCasGen Sun Mar 10 20:09:10 MSK 2013 */
package tokenization.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Sun Mar 10 20:12:40 MSK 2013
 * XML source: /home/marsel/Рабочий стол/desc/NLP@Cloud_Tokenizer_Descriptor.xml
 * @generated */
public class Percent extends Token {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Percent.class);
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
  protected Percent() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Percent(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Percent(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Percent(JCas jcas, int begin, int end) {
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
    if (Percent_Type.featOkTst && ((Percent_Type)jcasType).casFeat_Value == null)
      jcasType.jcas.throwFeatMissing("Value", "tokenization.types.Percent");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Percent_Type)jcasType).casFeatCode_Value);}
    
  /** setter for Value - sets  
   * @generated */
  public void setValue(String v) {
    if (Percent_Type.featOkTst && ((Percent_Type)jcasType).casFeat_Value == null)
      jcasType.jcas.throwFeatMissing("Value", "tokenization.types.Percent");
    jcasType.ll_cas.ll_setStringValue(addr, ((Percent_Type)jcasType).casFeatCode_Value, v);}    
  }

    