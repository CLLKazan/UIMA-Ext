

/* First created by JCasGen Sun May 05 02:07:47 MSK 2013 */
package ru.kfu.cll.uima.stemmer.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Sun May 05 02:07:47 MSK 2013
 * XML source: /home/marsel/UIMA-Ext/UIMA.Ext.Stemmer/src/main/resources/ru/kfu/cll/uima/stemmer/stemmer-ts.xml
 * @generated */
public class StemID extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(StemID.class);
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
  protected StemID() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public StemID(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public StemID(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public StemID(JCas jcas, int begin, int end) {
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
  //* Feature: Index

  /** getter for Index - gets Text of token.
   * @generated */
  public String getIndex() {
    if (StemID_Type.featOkTst && ((StemID_Type)jcasType).casFeat_Index == null)
      jcasType.jcas.throwFeatMissing("Index", "ru.kfu.cll.uima.stemmer.types.StemID");
    return jcasType.ll_cas.ll_getStringValue(addr, ((StemID_Type)jcasType).casFeatCode_Index);}
    
  /** setter for Index - sets Text of token. 
   * @generated */
  public void setIndex(String v) {
    if (StemID_Type.featOkTst && ((StemID_Type)jcasType).casFeat_Index == null)
      jcasType.jcas.throwFeatMissing("Index", "ru.kfu.cll.uima.stemmer.types.StemID");
    jcasType.ll_cas.ll_setStringValue(addr, ((StemID_Type)jcasType).casFeatCode_Index, v);}    
  }

    