

/* First created by JCasGen Tue May 07 16:55:51 MSD 2013 */
package ru.kfu.cll.uima.segmentation.fstype;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** Annotate content between two line separators
 * Updated by JCasGen Tue May 07 16:55:51 MSD 2013
 * XML source: /home/rgareev/projects/uima-ext/UIMA.Ext.Tokenizer/src/main/resources/ru/kfu/cll/uima/segmentation/segmentation-TypeSystem.xml
 * @generated */
public class Paragraph extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Paragraph.class);
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
  protected Paragraph() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Paragraph(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Paragraph(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Paragraph(JCas jcas, int begin, int end) {
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

    