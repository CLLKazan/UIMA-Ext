

/* First created by JCasGen Thu Feb 14 01:21:33 MSK 2013 */
package tokenization.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Thu Feb 14 01:21:33 MSK 2013
 * XML source: /home/marsel/workspace/NLP@Cloud/desc/NLP@Cloud_TokenizerDecs.xml
 * @generated */
public class TokenSeparator extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(TokenSeparator.class);
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
  protected TokenSeparator() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public TokenSeparator(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public TokenSeparator(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public TokenSeparator(JCas jcas, int begin, int end) {
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
  //* Feature: TypeOfSeparator

  /** getter for TypeOfSeparator - gets 
   * @generated */
  public String getTypeOfSeparator() {
    if (TokenSeparator_Type.featOkTst && ((TokenSeparator_Type)jcasType).casFeat_TypeOfSeparator == null)
      jcasType.jcas.throwFeatMissing("TypeOfSeparator", "tokenization.types.TokenSeparator");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TokenSeparator_Type)jcasType).casFeatCode_TypeOfSeparator);}
    
  /** setter for TypeOfSeparator - sets  
   * @generated */
  public void setTypeOfSeparator(String v) {
    if (TokenSeparator_Type.featOkTst && ((TokenSeparator_Type)jcasType).casFeat_TypeOfSeparator == null)
      jcasType.jcas.throwFeatMissing("TypeOfSeparator", "tokenization.types.TokenSeparator");
    jcasType.ll_cas.ll_setStringValue(addr, ((TokenSeparator_Type)jcasType).casFeatCode_TypeOfSeparator, v);}    
  }

    