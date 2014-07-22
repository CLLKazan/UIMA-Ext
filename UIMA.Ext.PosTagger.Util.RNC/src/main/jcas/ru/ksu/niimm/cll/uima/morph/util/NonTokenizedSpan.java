

/* First created by JCasGen Thu May 30 19:00:05 MSK 2013 */
package ru.ksu.niimm.cll.uima.morph.util;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** Marks a span between two tokens that are explicitly defined in corpus markup.
 * Updated by JCasGen Thu May 30 19:00:05 MSK 2013
 * XML source: C:/rinat/projects/uima-ext/UIMA.Ext.Morph.OpenCorpora/src/main/resources/ru/ksu/niimm/cll/uima/morph/util/ts-util.xml
 * @generated */
public class NonTokenizedSpan extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(NonTokenizedSpan.class);
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
  protected NonTokenizedSpan() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public NonTokenizedSpan(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public NonTokenizedSpan(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public NonTokenizedSpan(JCas jcas, int begin, int end) {
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
     
}

    