

/* First created by JCasGen Tue Mar 26 14:06:51 SAMT 2013 */
package ru.kfu.cll.uima.segmentation.fstype;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Tue Mar 26 14:06:51 SAMT 2013
 * XML source: /home/fsqcds/idea-projects/UIMA-Ext/UIMA.Ext.Tokenizer/src/main/resources/ru/kfu/cll/uima/tokenizer/tokenizer-TypeSystem.xml
 * @generated */
public class Token extends TokenBase {
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
  //* Feature: TypeName

  /** getter for TypeName - gets 
   * @generated */
  public String getTypeName() {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_TypeName == null)
      jcasType.jcas.throwFeatMissing("TypeName", "ru.kfu.cll.uima.segmentation.fstype.Token");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Token_Type)jcasType).casFeatCode_TypeName);}
    
  /** setter for TypeName - sets  
   * @generated */
  public void setTypeName(String v) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_TypeName == null)
      jcasType.jcas.throwFeatMissing("TypeName", "ru.kfu.cll.uima.segmentation.fstype.Token");
    jcasType.ll_cas.ll_setStringValue(addr, ((Token_Type)jcasType).casFeatCode_TypeName, v);}    
  }

    