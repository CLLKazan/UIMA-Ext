

/* First created by JCasGen Sun Feb 10 23:27:25 MSK 2013 */
package ru.kfu.itis.issst.uima.chunker.cas;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSArray;
import org.opencorpora.cas.Word;
import org.apache.uima.jcas.tcas.Annotation;


/** Represents typed 'chunk', i.e. a head word with its dependents.
				Chunk annotation should have the same borders with its head word.
 * Updated by JCasGen Sun Feb 10 23:27:25 MSK 2013
 * XML source: src/main/resources/ru/kfu/itis/issst/uima/chunker/ts-chunking.xml
 * @generated */
public class Chunk extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Chunk.class);
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
  protected Chunk() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Chunk(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Chunk(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Chunk(JCas jcas, int begin, int end) {
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
  //* Feature: chunkType

  /** getter for chunkType - gets 
   * @generated */
  public String getChunkType() {
    if (Chunk_Type.featOkTst && ((Chunk_Type)jcasType).casFeat_chunkType == null)
      jcasType.jcas.throwFeatMissing("chunkType", "ru.kfu.itis.issst.uima.chunker.cas.Chunk");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Chunk_Type)jcasType).casFeatCode_chunkType);}
    
  /** setter for chunkType - sets  
   * @generated */
  public void setChunkType(String v) {
    if (Chunk_Type.featOkTst && ((Chunk_Type)jcasType).casFeat_chunkType == null)
      jcasType.jcas.throwFeatMissing("chunkType", "ru.kfu.itis.issst.uima.chunker.cas.Chunk");
    jcasType.ll_cas.ll_setStringValue(addr, ((Chunk_Type)jcasType).casFeatCode_chunkType, v);}    
   
    
  //*--------------*
  //* Feature: head

  /** getter for head - gets 
   * @generated */
  public Word getHead() {
    if (Chunk_Type.featOkTst && ((Chunk_Type)jcasType).casFeat_head == null)
      jcasType.jcas.throwFeatMissing("head", "ru.kfu.itis.issst.uima.chunker.cas.Chunk");
    return (Word)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Chunk_Type)jcasType).casFeatCode_head)));}
    
  /** setter for head - sets  
   * @generated */
  public void setHead(Word v) {
    if (Chunk_Type.featOkTst && ((Chunk_Type)jcasType).casFeat_head == null)
      jcasType.jcas.throwFeatMissing("head", "ru.kfu.itis.issst.uima.chunker.cas.Chunk");
    jcasType.ll_cas.ll_setRefValue(addr, ((Chunk_Type)jcasType).casFeatCode_head, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: dependents

  /** getter for dependents - gets 
   * @generated */
  public FSArray getDependents() {
    if (Chunk_Type.featOkTst && ((Chunk_Type)jcasType).casFeat_dependents == null)
      jcasType.jcas.throwFeatMissing("dependents", "ru.kfu.itis.issst.uima.chunker.cas.Chunk");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Chunk_Type)jcasType).casFeatCode_dependents)));}
    
  /** setter for dependents - sets  
   * @generated */
  public void setDependents(FSArray v) {
    if (Chunk_Type.featOkTst && ((Chunk_Type)jcasType).casFeat_dependents == null)
      jcasType.jcas.throwFeatMissing("dependents", "ru.kfu.itis.issst.uima.chunker.cas.Chunk");
    jcasType.ll_cas.ll_setRefValue(addr, ((Chunk_Type)jcasType).casFeatCode_dependents, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for dependents - gets an indexed value - 
   * @generated */
  public Word getDependents(int i) {
    if (Chunk_Type.featOkTst && ((Chunk_Type)jcasType).casFeat_dependents == null)
      jcasType.jcas.throwFeatMissing("dependents", "ru.kfu.itis.issst.uima.chunker.cas.Chunk");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Chunk_Type)jcasType).casFeatCode_dependents), i);
    return (Word)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Chunk_Type)jcasType).casFeatCode_dependents), i)));}

  /** indexed setter for dependents - sets an indexed value - 
   * @generated */
  public void setDependents(int i, Word v) { 
    if (Chunk_Type.featOkTst && ((Chunk_Type)jcasType).casFeat_dependents == null)
      jcasType.jcas.throwFeatMissing("dependents", "ru.kfu.itis.issst.uima.chunker.cas.Chunk");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Chunk_Type)jcasType).casFeatCode_dependents), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Chunk_Type)jcasType).casFeatCode_dependents), i, jcasType.ll_cas.ll_getFSRef(v));}
  }

    