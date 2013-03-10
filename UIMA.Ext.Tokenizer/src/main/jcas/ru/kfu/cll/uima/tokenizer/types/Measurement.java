

/* First created by JCasGen Sat Mar 09 22:06:31 MSK 2013 */
package tokenization.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Sun Mar 10 20:12:40 MSK 2013
 * XML source: /home/marsel/Рабочий стол/desc/NLP@Cloud_Tokenizer_Descriptor.xml
 * @generated */
public class Measurement extends Token {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Measurement.class);
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
  protected Measurement() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Measurement(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Measurement(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Measurement(JCas jcas, int begin, int end) {
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
  //* Feature: UnitName

  /** getter for UnitName - gets 
   * @generated */
  public String getUnitName() {
    if (Measurement_Type.featOkTst && ((Measurement_Type)jcasType).casFeat_UnitName == null)
      jcasType.jcas.throwFeatMissing("UnitName", "tokenization.types.Measurement");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Measurement_Type)jcasType).casFeatCode_UnitName);}
    
  /** setter for UnitName - sets  
   * @generated */
  public void setUnitName(String v) {
    if (Measurement_Type.featOkTst && ((Measurement_Type)jcasType).casFeat_UnitName == null)
      jcasType.jcas.throwFeatMissing("UnitName", "tokenization.types.Measurement");
    jcasType.ll_cas.ll_setStringValue(addr, ((Measurement_Type)jcasType).casFeatCode_UnitName, v);}    
   
    
  //*--------------*
  //* Feature: Value

  /** getter for Value - gets 
   * @generated */
  public String getValue() {
    if (Measurement_Type.featOkTst && ((Measurement_Type)jcasType).casFeat_Value == null)
      jcasType.jcas.throwFeatMissing("Value", "tokenization.types.Measurement");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Measurement_Type)jcasType).casFeatCode_Value);}
    
  /** setter for Value - sets  
   * @generated */
  public void setValue(String v) {
    if (Measurement_Type.featOkTst && ((Measurement_Type)jcasType).casFeat_Value == null)
      jcasType.jcas.throwFeatMissing("Value", "tokenization.types.Measurement");
    jcasType.ll_cas.ll_setStringValue(addr, ((Measurement_Type)jcasType).casFeatCode_Value, v);}    
  }

    