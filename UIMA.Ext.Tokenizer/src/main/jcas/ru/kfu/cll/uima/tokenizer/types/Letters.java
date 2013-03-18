

/* First created by JCasGen Sat Mar 02 18:51:31 MSK 2013 */
package tokenization.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Sun Mar 10 20:12:40 MSK 2013
 * XML source: /home/marsel/Рабочий стол/desc/NLP@Cloud_Tokenizer_Descriptor.xml
 * @generated */
public class Letters extends Token {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Letters.class);
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
  protected Letters() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Letters(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Letters(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Letters(JCas jcas, int begin, int end) {
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
  //* Feature: LetterCase

  /** getter for LetterCase - gets 
   * @generated */
  public String getLetterCase() {
    if (Letters_Type.featOkTst && ((Letters_Type)jcasType).casFeat_LetterCase == null)
      jcasType.jcas.throwFeatMissing("LetterCase", "tokenization.types.Letters");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Letters_Type)jcasType).casFeatCode_LetterCase);}
    
  /** setter for LetterCase - sets  
   * @generated */
  public void setLetterCase(String v) {
    if (Letters_Type.featOkTst && ((Letters_Type)jcasType).casFeat_LetterCase == null)
      jcasType.jcas.throwFeatMissing("LetterCase", "tokenization.types.Letters");
    jcasType.ll_cas.ll_setStringValue(addr, ((Letters_Type)jcasType).casFeatCode_LetterCase, v);}    
   
    
  //*--------------*
  //* Feature: Language

  /** getter for Language - gets 
   * @generated */
  public String getLanguage() {
    if (Letters_Type.featOkTst && ((Letters_Type)jcasType).casFeat_Language == null)
      jcasType.jcas.throwFeatMissing("Language", "tokenization.types.Letters");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Letters_Type)jcasType).casFeatCode_Language);}
    
  /** setter for Language - sets  
   * @generated */
  public void setLanguage(String v) {
    if (Letters_Type.featOkTst && ((Letters_Type)jcasType).casFeat_Language == null)
      jcasType.jcas.throwFeatMissing("Language", "tokenization.types.Letters");
    jcasType.ll_cas.ll_setStringValue(addr, ((Letters_Type)jcasType).casFeatCode_Language, v);}    
  }

    