

/* First created by JCasGen Tue Mar 12 00:30:02 MSK 2013 */
package ru.kfu.itis.issst.ner.typesystem;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Tue Mar 12 00:30:02 MSK 2013
 * XML source: /home/pathfinder/_WORK/Projects/BRATWorkspace/git/UIMA-Ext/UIMA.Ext.Brat.Integration/desc/UIMA2BratAnnotatorDescriptor.xml
 * @generated */
public class HL_CompanyReference extends HL_EntityReference {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(HL_CompanyReference.class);
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
  protected HL_CompanyReference() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public HL_CompanyReference(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public HL_CompanyReference(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public HL_CompanyReference(JCas jcas, int begin, int end) {
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
  //* Feature: company

  /** getter for company - gets 
   * @generated */
  public HL_Company getCompany() {
    if (HL_CompanyReference_Type.featOkTst && ((HL_CompanyReference_Type)jcasType).casFeat_company == null)
      jcasType.jcas.throwFeatMissing("company", "ru.kfu.itis.issst.ner.typesystem.HL_CompanyReference");
    return (HL_Company)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((HL_CompanyReference_Type)jcasType).casFeatCode_company)));}
    
  /** setter for company - sets  
   * @generated */
  public void setCompany(HL_Company v) {
    if (HL_CompanyReference_Type.featOkTst && ((HL_CompanyReference_Type)jcasType).casFeat_company == null)
      jcasType.jcas.throwFeatMissing("company", "ru.kfu.itis.issst.ner.typesystem.HL_CompanyReference");
    jcasType.ll_cas.ll_setRefValue(addr, ((HL_CompanyReference_Type)jcasType).casFeatCode_company, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    