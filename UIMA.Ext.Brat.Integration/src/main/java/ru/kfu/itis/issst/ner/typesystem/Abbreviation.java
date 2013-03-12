

/* First created by JCasGen Tue Mar 12 00:30:02 MSK 2013 */
package ru.kfu.itis.issst.ner.typesystem;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** An abbreviation is a letter or group of letters, taken from a word or words. For example, the word "abbreviation" can be abbreviated as "abbr." or "abbrev."
 * Updated by JCasGen Tue Mar 12 00:30:02 MSK 2013
 * XML source: /home/pathfinder/_WORK/Projects/BRATWorkspace/git/UIMA-Ext/UIMA.Ext.Brat.Integration/desc/UIMA2BratAnnotatorDescriptor.xml
 * @generated */
public class Abbreviation extends Annotation {
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
     
 
    
  //*--------------*
  //* Feature: expan

  /** getter for expan - gets The full form of an abbreviation. 
          For example he fullform, for example for HLA (human leukocyte antigen)
   * @generated */
  public String getExpan() {
    if (Abbreviation_Type.featOkTst && ((Abbreviation_Type)jcasType).casFeat_expan == null)
      jcasType.jcas.throwFeatMissing("expan", "ru.kfu.itis.issst.ner.typesystem.Abbreviation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Abbreviation_Type)jcasType).casFeatCode_expan);}
    
  /** setter for expan - sets The full form of an abbreviation. 
          For example he fullform, for example for HLA (human leukocyte antigen) 
   * @generated */
  public void setExpan(String v) {
    if (Abbreviation_Type.featOkTst && ((Abbreviation_Type)jcasType).casFeat_expan == null)
      jcasType.jcas.throwFeatMissing("expan", "ru.kfu.itis.issst.ner.typesystem.Abbreviation");
    jcasType.ll_cas.ll_setStringValue(addr, ((Abbreviation_Type)jcasType).casFeatCode_expan, v);}    
   
    
  //*--------------*
  //* Feature: textReference

  /** getter for textReference - gets Reference to the text span that contains the full form of the abbreviation/acronym
   * @generated */
  public Annotation getTextReference() {
    if (Abbreviation_Type.featOkTst && ((Abbreviation_Type)jcasType).casFeat_textReference == null)
      jcasType.jcas.throwFeatMissing("textReference", "ru.kfu.itis.issst.ner.typesystem.Abbreviation");
    return (Annotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Abbreviation_Type)jcasType).casFeatCode_textReference)));}
    
  /** setter for textReference - sets Reference to the text span that contains the full form of the abbreviation/acronym 
   * @generated */
  public void setTextReference(Annotation v) {
    if (Abbreviation_Type.featOkTst && ((Abbreviation_Type)jcasType).casFeat_textReference == null)
      jcasType.jcas.throwFeatMissing("textReference", "ru.kfu.itis.issst.ner.typesystem.Abbreviation");
    jcasType.ll_cas.ll_setRefValue(addr, ((Abbreviation_Type)jcasType).casFeatCode_textReference, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: definedHere

  /** getter for definedHere - gets The definedHere is true if the abbreviation/acronym is defined for the first time in the text, e.g. in "interleukin 2 (Il-2) receptor", it can be true only for locally introduced abbreviations/acronyms.
   * @generated */
  public boolean getDefinedHere() {
    if (Abbreviation_Type.featOkTst && ((Abbreviation_Type)jcasType).casFeat_definedHere == null)
      jcasType.jcas.throwFeatMissing("definedHere", "ru.kfu.itis.issst.ner.typesystem.Abbreviation");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((Abbreviation_Type)jcasType).casFeatCode_definedHere);}
    
  /** setter for definedHere - sets The definedHere is true if the abbreviation/acronym is defined for the first time in the text, e.g. in "interleukin 2 (Il-2) receptor", it can be true only for locally introduced abbreviations/acronyms. 
   * @generated */
  public void setDefinedHere(boolean v) {
    if (Abbreviation_Type.featOkTst && ((Abbreviation_Type)jcasType).casFeat_definedHere == null)
      jcasType.jcas.throwFeatMissing("definedHere", "ru.kfu.itis.issst.ner.typesystem.Abbreviation");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((Abbreviation_Type)jcasType).casFeatCode_definedHere, v);}    
  }

    