

/* First created by JCasGen Sat Mar 02 21:16:11 MSK 2013 */
package tokenization.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Sun Mar 10 20:12:40 MSK 2013
 * XML source: /home/marsel/Рабочий стол/desc/NLP@Cloud_Tokenizer_Descriptor.xml
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
      jcasType.jcas.throwFeatMissing("Kind", "tokenization.types.Separator");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Separator_Type)jcasType).casFeatCode_Kind);}
    
  /** setter for Kind - sets  
   * @generated */
  public void setKind(String v) {
    if (Separator_Type.featOkTst && ((Separator_Type)jcasType).casFeat_Kind == null)
      jcasType.jcas.throwFeatMissing("Kind", "tokenization.types.Separator");
    jcasType.ll_cas.ll_setStringValue(addr, ((Separator_Type)jcasType).casFeatCode_Kind, v);}    
  }

    