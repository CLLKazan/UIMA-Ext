

/* First created by JCasGen Tue Mar 12 00:30:02 MSK 2013 */
package ru.kfu.itis.issst.ner.typesystem;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import ru.kfu.itis.issst.ner.typesystem.temporal.TE;


/** 
 * Updated by JCasGen Tue Mar 12 00:30:02 MSK 2013
 * XML source: /home/pathfinder/_WORK/Projects/BRATWorkspace/git/UIMA-Ext/UIMA.Ext.Brat.Integration/desc/UIMA2BratAnnotatorDescriptor.xml
 * @generated */
public class HL_AnnouncementIndicator extends EventIndicator {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(HL_AnnouncementIndicator.class);
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
  protected HL_AnnouncementIndicator() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public HL_AnnouncementIndicator(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public HL_AnnouncementIndicator(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public HL_AnnouncementIndicator(JCas jcas, int begin, int end) {
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
  //* Feature: date

  /** getter for date - gets 
   * @generated */
  public TE getDate() {
    if (HL_AnnouncementIndicator_Type.featOkTst && ((HL_AnnouncementIndicator_Type)jcasType).casFeat_date == null)
      jcasType.jcas.throwFeatMissing("date", "ru.kfu.itis.issst.ner.typesystem.HL_AnnouncementIndicator");
    return (TE)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((HL_AnnouncementIndicator_Type)jcasType).casFeatCode_date)));}
    
  /** setter for date - sets  
   * @generated */
  public void setDate(TE v) {
    if (HL_AnnouncementIndicator_Type.featOkTst && ((HL_AnnouncementIndicator_Type)jcasType).casFeat_date == null)
      jcasType.jcas.throwFeatMissing("date", "ru.kfu.itis.issst.ner.typesystem.HL_AnnouncementIndicator");
    jcasType.ll_cas.ll_setRefValue(addr, ((HL_AnnouncementIndicator_Type)jcasType).casFeatCode_date, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    