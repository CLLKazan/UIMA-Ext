

/* First created by JCasGen Sun Jan 27 16:27:14 SAMT 2013 */
package ru.kfu.cll.uima.tokenizer.fstype;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Sun Jan 27 16:27:14 SAMT 2013
 * XML source: /home/vladimir/workspace-git/uima-ext/UIMA-Ext/UIMA.Ext.Tokenizer/src/main/resources/ru/kfu/cll/uima/tokenizer/tokenizer-TypeSystem.xml
 * @generated */
public class EXCLAMATION extends PM {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(EXCLAMATION.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected EXCLAMATION() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public EXCLAMATION(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public EXCLAMATION(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public EXCLAMATION(JCas jcas, int begin, int end) {
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
     
}

    