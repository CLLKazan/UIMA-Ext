

/* First created by JCasGen Wed Feb 27 15:40:57 SAMT 2013 */
package ru.kfu.cll.uima.tokenizer.fstype;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Wed Feb 27 15:40:57 SAMT 2013
 * XML source: /home/vladimir/workspace-git/uima-ext/UIMA-Ext/UIMA.Ext.Term/desc/tokenizer-TypeSystem.xml
 * @generated */
public class SEMICOLON extends PM {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(SEMICOLON.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected SEMICOLON() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public SEMICOLON(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public SEMICOLON(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public SEMICOLON(JCas jcas, int begin, int end) {
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

    