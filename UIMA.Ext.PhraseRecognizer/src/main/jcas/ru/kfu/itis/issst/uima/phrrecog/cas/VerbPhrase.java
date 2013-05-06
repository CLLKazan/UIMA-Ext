

/* First created by JCasGen Fri Mar 01 17:08:28 MSK 2013 */
package ru.kfu.itis.issst.uima.phrrecog.cas;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** Represents Verb Phrase
 * Updated by JCasGen Fri Mar 01 17:08:28 MSK 2013
 * XML source: src/main/resources/ru/kfu/itis/issst/uima/phrrecog/ts-phrase-recognizer.xml
 * @generated */
public class VerbPhrase extends Phrase {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(VerbPhrase.class);
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
  protected VerbPhrase() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public VerbPhrase(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public VerbPhrase(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public VerbPhrase(JCas jcas, int begin, int end) {
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

    