

/* First created by JCasGen Mon Feb 11 15:45:21 MSK 2013 */
package tokenization.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Thu Feb 14 01:21:33 MSK 2013
 * XML source: /home/marsel/workspace/NLP@Cloud/desc/NLP@Cloud_TokenizerDecs.xml
 * @generated */
public class Abbreviation extends Letters {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Abbreviation.class);
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
  protected Abbreviation() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Abbreviation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Abbreviation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Abbreviation(JCas jcas, int begin, int end) {
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

    