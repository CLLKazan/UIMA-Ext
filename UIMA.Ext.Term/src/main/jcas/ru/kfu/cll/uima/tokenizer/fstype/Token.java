

/* First created by JCasGen Wed Feb 27 15:40:57 SAMT 2013 */
package ru.kfu.cll.uima.tokenizer.fstype;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Wed Feb 27 15:40:57 SAMT 2013
 * XML source: /home/vladimir/workspace-git/uima-ext/UIMA-Ext/UIMA.Ext.Term/desc/tokenizer-TypeSystem.xml
 * @generated */
public class Token extends TokenBase {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(Token.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Token() {}
    
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
  private void readObject() {}
     
 
    
  //*--------------*
  //* Feature: TypeName

  /** getter for TypeName - gets 
   * @generated */
  public String getTypeName() {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_TypeName == null)
      jcasType.jcas.throwFeatMissing("TypeName", "ru.kfu.cll.uima.tokenizer.fstype.Token");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Token_Type)jcasType).casFeatCode_TypeName);}
    
  /** setter for TypeName - sets  
   * @generated */
  public void setTypeName(String v) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_TypeName == null)
      jcasType.jcas.throwFeatMissing("TypeName", "ru.kfu.cll.uima.tokenizer.fstype.Token");
    jcasType.ll_cas.ll_setStringValue(addr, ((Token_Type)jcasType).casFeatCode_TypeName, v);}    
  }

    