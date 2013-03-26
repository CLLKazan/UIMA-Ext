

/* First created by JCasGen Tue Mar 26 13:55:53 SAMT 2013 */
package ru.kfu.cll.uima.tokenizer.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Tue Mar 26 13:55:53 SAMT 2013
 * XML source: /home/fsqcds/idea-projects/UIMA-Ext/UIMA.Ext.Tokenizer/src/main/resources/ru/kfu/cll/uima/tokenizer/jflex-tokenizer-ts.xml
 * @generated */
public class Separator extends Token {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Separator.class);
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
  protected Separator() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Separator(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Separator(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Separator(JCas jcas, int begin, int end) {
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
    if (Separator_Type.featOkTst && ((Separator_Type)jcasType).casFeat_Kind == null)
      jcasType.jcas.throwFeatMissing("Kind", "ru.kfu.cll.uima.tokenizer.types.Separator");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Separator_Type)jcasType).casFeatCode_Kind);}
    
  /** setter for Kind - sets  
   * @generated */
  public void setKind(String v) {
    if (Separator_Type.featOkTst && ((Separator_Type)jcasType).casFeat_Kind == null)
      jcasType.jcas.throwFeatMissing("Kind", "ru.kfu.cll.uima.tokenizer.types.Separator");
    jcasType.ll_cas.ll_setStringValue(addr, ((Separator_Type)jcasType).casFeatCode_Kind, v);}    
  }

    