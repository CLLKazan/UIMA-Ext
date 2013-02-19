PACKAGE com.hp.hplabs.lim2.ie.text.typesystem;
TYPESYSTEM hp-text-ru-TypeSystem;

// variables for feature transfering
TYPE position;
TYPE company;
TYPE personName;
TYPE arg;
// for PC events
TYPE newPosition;
TYPE newCompany;
// null (or empty) string reference. MUST BE USED ONLY AS CONSTANT!
STRING null;

// EXPERIMENTS
// TODO remove after experiments
// "«"{->MARK(OpenQuoteMark)};
// "\""{->MARK(OpenQuoteMark)};
// "»"{->MARK(CloseQuoteMark)};
// "\""{->MARK(CloseQuoteMark)};

// OpenQuoteMark{NOT(PARTOF(QM))} ANY*? CloseQuoteMark{->MARK(QM,1,2,3)};

// ANY GPE{NOT(PARTOF(QM))->MARK(TargetGPE,1,2)};
// GPE{NOT(PARTOF(TargetGPE))->MARK(TargetGPE)};

// ------------------------------------------------------------------------------------------------
// --------------------------------------- AUXILIARY ----------------------------------------------
// ------------------------------------------------------------------------------------------------

TYPELIST commonWordTypes;

Document{->ADD(commonWordTypes, HL_GeneralPersonIndicator, HL_BusinessPersonIndicator, HL_PoliticalPersonIndicator,
HL_CompanyIndicator1, HL_CompanyIndicator2, HL_CompanyDepartment, HL_CountryAdj, HL_Position)};

// capitalized one character abbreviation
W{REGEXP("\\p{javaUpperCase}")} "." {->MARK(CAP1_ABBR,1,2)};

// first CW in sentence
W{AND(POSITION(Sentence,1),REGEXP("\\p{javaUpperCase}\\p{javaLowerCase}*"))->MARK(FCW)};

// Can be improved by prior common word detection, e.g., using common lexicon
FCW{IS(commonWordTypes)->MARK(NCW)};

// ------------------------------------------------------------------------------------------------
// --------------------------------------- NUMBERS ------------------------------------------------
// ------------------------------------------------------------------------------------------------
NUM+ COMMA NUM+ HL_WordNumber+? {->MARK(NUM,1,2,3,4)};
NUM+ HL_WordNumber+? {->MARK(NUM,1,2)};
HL_WordNumber+ {->MARK(NUM,1)};

// ------------------------------------------------------------------------------------------------
// --------------------------------------- TEMPORAL EXPRESSIONS -----------------------------------
// ------------------------------------------------------------------------------------------------
NUM{REGEXP("(1[6-9]|2[0-2])[0-9]{2}") -> MARK(TE_PossibleYear)};

NUM TE_Month{->MARK(TE,1,2)};

NUM "-" W{REGEXP("о?е")} TE_Month{->MARK(TE,1,2,3,4)};

NUM TE_Month COMMA? TE_PossibleYear {->MARK(TE,1,2,3,4)};

NUM TE_Month COMMA? TE_PossibleYear "года" {-> MARK(TE,1,2,3,4,5)};

TE_PossibleYear COMMA? NUM TE_Month {->MARK(TE,1,2,3,4)};

NUM "." NUM "." TE_PossibleYear {->MARK(TE,1,2,3,4,5)};

W{REGEXP("во?", true)} W?? TE_Day ","? TE? {->MARK(TE,1,2,3,4,5)};

"на" W?? "выходных" {-> MARK(TE,1,2,3)};

"в" NUM{REGEXP("[0-2]?[0-9]")} ANY{REGEXP("[-:.]")} NUM{REGEXP("[0-5][0-9]")->MARK(TE_Time,1,2,3,4)};

TE_Time "по" ANY[1,3]? "времени"{->MARK(TE_Time,1,2,3,4)};

TE TE_Time{->MARK(TE,1,2)};

// ------------------------------------------------------------------------------------------------
// --------------------------------------- COUNTRY ADJECTIVES -------------------------------------
// ------------------------------------------------------------------------------------------------
HL_CountryAdj "-" HL_CountryAdj "-"? HL_CountryAdj? "-"? HL_CountryAdj? {->MARK(HL_CountryAdj,1,2,3,4,5,6,7)};

// ------------------------------------------------------------------------------------------------
// --------------------------------------- PRICES -------------------------------------------------
// ------------------------------------------------------------------------------------------------
NUM HL_CurrencyCode {-> MARK(HL_PriceUnit,1,2)};

NUM HL_CurrencyUnit {-> MARK(HL_PriceUnit,1,2)};

NUM HL_CountryAdj HL_CurrencyUnit {-> MARK(HL_PriceUnit,1,2,3)};

HL_CurrencyUnit NUM {-> MARK(HL_PriceUnit,1,2)};

// ------------------------------------------------------------------------------------------------
// --------------------------------------- POSSIBLE PERSON ----------------------------------------
// ------------------------------------------------------------------------------------------------
CAP1_ABBR CAP1_ABBR? CW{REGEXP("..+") -> MARK(PossiblePerson,1,2,3)};

CW{-IS(NCW)} CAP1_ABBR CAP1_ABBR? {->MARK(PossiblePerson,1,2,3)};

CW{-IS(NCW)} CAP1_ABBR CW {REGEXP("..+") ->MARK(PossiblePerson,1,2,3)};

CW{-IS(NCW)} W{REGEXP ("Мак|де|ди|дю|дез|ле|да|ван|фон|цу|", true)} CW {->MARK(PossiblePerson,1,2,3)}; 

CW{-IS(NCW)} "О" "’" CW{->MARK(PossiblePerson,1,2,3,4)};

CW{-IS(NCW)} "д" "’" CW{->MARK(PossiblePerson,1,2,3,4)};

CW{-IS(NCW)} "Тер" "-" CW{->MARK(PossiblePerson,1,2,3,4)};

// this rule for nickname catching is too ambiguous because it seems to catch company names more often
//CW "\"" W "\"" CW{-> MARK(HL_Person,1,2,3,4,5)};

// fill person 'name' feature
// this rule doesn't work because of SETFEATURE doesn't handle TYPE expressions at all
// PossiblePerson{FEATURE("name",null)->MATCHEDTEXT(personName), SETFEATURE("name",personName)};

// ------------------------------------------------------------------------------------------------
// --------------------------------------- POSSIBLE COMPANY ---------------------------------------
// ------------------------------------------------------------------------------------------------

"\""{NOT(PARTOF(QM))} W{REGEXP("\\p{javaUpperCase}.*")} ANY*? "\"" 
{->MARK(PossibleCompany,1,2,3,4),MARK(QM,1,2,3,4)};

"«" W{REGEXP("\\p{javaUpperCase}.*")} ANY*? "»" {->MARK(PossibleCompany,1,2,3,4)};

// ------------------------------------------------------------------------------------------------
// --------------------------------------- POSSIBLE NAMED ENTITY ----------------------------------
// ------------------------------------------------------------------------------------------------

// PNE1
CW{AND(-IS(NCW), -PARTOF({PossibleNE, PossiblePerson, PossibleCompany}))} W*{REGEXP("\\p{javaUpperCase}.*") ->MARK(PossibleNE,1,2)};
// PNE2
CAP {NOT(PARTOF({PossibleNE, PossiblePerson, PossibleCompany, HL_CompanyIndicator2}))} W*{REGEXP("\\p{javaUpperCase}.*") ->MARK(PossibleNE,1,2)};
// PNE3
PossibleNE "[" ANY*? "]" {->MARK(PossibleNE,1,2,3,4)};
// PNE4
PossibleNE "(" ANY*? ")" {->MARK(PossibleNE,1,2,3,4)};

// PossibleNE "," ANY+? "," {->MARK(PossibleNE,1,2,3,4)};

// ------------------------------------------------------------------------------------------------
// --------------------------------------- COMPANY INDICATORS -------------------------------------
// ------------------------------------------------------------------------------------------------
HL_CountryAdj ANY[0,4]? HL_CompanyIndicator1 {-> MARK(HL_CompanyIndicator1,1,2,3)};

HL_CompanyIndicator1 HL_Country {-> MARK(HL_CompanyIndicator1,1,2)};

HL_CompanyIndicator1 HL_CompanyIndicator2? {-> MARK(HL_CompanyIndicator,1,2)};

HL_CompanyIndicator2 {NOT(PARTOF(HL_CompanyIndicator))-> MARK(HL_CompanyIndicator,1,2)};

HL_CompanyIndicator PossibleNE {->MARK(PossibleCompany,1,2)};

// ------------------------------------------------------------------------------------------------
// --------------------------------------- PERSON INDICATORS --------------------------------------
// ------------------------------------------------------------------------------------------------
HL_CountryAdj HL_BusinessPersonIndicator {->MARK (HL_BusinessPersonIndicator,1,2)};

HL_BusinessPersonIndicator PossibleNE {->MARK(PossiblePerson,1,2)};

HL_PoliticalPersonIndicator ANY[0,4]? HL_Country PossibleNE {->MARK (PossiblePerson,1,2,3,4)};

HL_PoliticalPersonIndicator{NOT(PARTOF(PossiblePerson))} HL_CountryAdj? ANY[0,5]? PossibleNE {->MARK (PossiblePerson,1,2,3,4)};

HL_GeneralPersonIndicator PossibleNE {->MARK(PossiblePerson,1,2)};

// ------------------------------------------------------------------------------------------------
// --------------------------------------- PERSON POSITIONS ---------------------------------------
// ------------------------------------------------------------------------------------------------
HL_Position W[0,3]? HL_CompanyDepartment{->MARK(HL_Position,1,2,3)};

HL_Position COMMA HL_Position {->MARK(HL_Position,1,2,3)};

HL_Position ANY[0,4]? PossibleNE PossibleNE
{->GATHER(PossiblePerson,1,2,3,4, "position"=1, "company"=3, "name"=4)};

// softer version of previous rule
HL_Position{NOT(PARTOF(PossiblePerson))} ANY[0,4]? PossibleNE 
{->GATHER(PossiblePerson,1,2,3, "position"=1, "company"=3)};

// ------------------------------------------------------------------------------------------------
// --------------------------------------- NAMED ENTITY CONJUNCTION -------------------------------
// ------------------------------------------------------------------------------------------------
PossibleNE "," PossibleNE {->MARK(PossibleNE,1,2,3)};

PossibleNE "и" PossibleNE {->MARK(PossibleNE,1,2,3)};

// ------------------------------------------------------------------------------------------------
// --------------------------------------- ANNOUNCEMENTS ----------------------------------
// ------------------------------------------------------------------------------------------------
// following (commented) rule is expanded to three simple because of
// 1. starting anchor search strategy of TextMarker
// 2. correct assignment of "date" feature 
// TE? Token [0,4]? HL_Company Token [0,4]? TE? HL_AnnouncementIndicator TE? {-> GATHER(HL_CompanyAnnouncement,1,2,3,4,5,6,7, "company" = 3, "date" = 1, "date" = 5, "date" = 7)};
TE ANY[0,4]? PossibleNE ANY[0,4]? HL_AnnouncementIndicator 
{-> GATHER(Announcement,1,2,3,4,5, "subject"=3, "date"=1)};

PossibleNE ANY[0,4]? TE HL_AnnouncementIndicator 
{-> GATHER(Announcement,1,2,3,4, "subject"=1, "date"=3)};

PossibleNE ANY[0,4]? HL_AnnouncementIndicator TE
{-> GATHER(Announcement,1,2,3,4, "subject"=1, "date"=4)};

HL_AnnouncementIndicator PossibleNE TE{-> GATHER(Announcement,1,2,3, "subject" = 2, "date" = 3)};

HL_AnnouncementIndicator PossibleNE {NOT(PARTOF(Announcement)) -> GATHER(Announcement,1,2, "subject" = 2)};

PossibleNE HL_AnnouncementIndicator "пресс" "-" "релиз" {-> GATHER(Announcement,1,2,3,4,5, "subject" = 1)};

HL_AnnouncementIndicator "в"? "пресс" "-" W{REGEXP("релизе?")} PossibleNE 
{-> GATHER(Announcement,1,2,3,4,5,6, "subject" = 6)};

// ------------------------------------------------------------------------------------------------
// --------------------------------------- RESIGNATION --------------------------------------------
// ------------------------------------------------------------------------------------------------
// ... заявил, что уходит ...
// ... заявил о своем уходе ...
PossibleNE HL_AnnouncementIndicator ","? W[0,2]? HL_ResignationIndicator 
W{REGEXP ("с|от")} W?? W{REGEXP("(должност|пост).*")} HL_Position PossibleNE 
{-> GATHER(HL_Resignation,1,2,3,4,5,6,7,8,9,10, "person" = 1, "position" = 9, "company" = 10)};

// same but without company
PossibleNE HL_AnnouncementIndicator ","? W[0,2]? HL_ResignationIndicator {NOT(PARTOF(HL_Resignation))} 
W{REGEXP ("с|от")} W?? W{REGEXP("(должност|пост).*")} HL_Position 
{-> GATHER(HL_Resignation,1,2,3,4,5,6,7,8,9, "person" = 1, "position" = 9)};

// fallback
PossiblePerson{NOT(FEATURE("position",null))->GETFEATURE("position",position),GETFEATURE("company",company),GETFEATURE("name",personName)} 
HL_AnnouncementIndicator ","? W[0,2]? HL_ResignationIndicator 
{NOT(PARTOF(HL_Resignation)) -> CREATE(HL_Resignation,1,2,3,4,5, "person" = personName, "position"=position, "company"=company)};

// fallback for NE
PossibleNE HL_AnnouncementIndicator ","? 
W[0,2]? HL_ResignationIndicator {NOT(PARTOF(HL_Resignation)) -> GATHER(HL_Resignation,1,2,3,4,5, "person" = 1)};

// один уволил другого
PossibleNE HL_ResignationIndicator PossibleNE 
W{REGEXP("с|от")} W?? W{REGEXP("(должност|пост).*")} HL_Position PossibleNE 
{-> GATHER (HL_Resignation,1,2,3,4,5,6,7,8, "person" = 3, "position" = 7, "company" = 8, "approvedBy" = 1)};

// same but without company
PossibleNE HL_ResignationIndicator{NOT(PARTOF(HL_Resignation))} 
PossibleNE W{REGEXP("с|от")} W?? W{REGEXP("(должност|пост).*")} HL_Position 
{-> GATHER (HL_Resignation,1,2,3,4,5,6,7, "person" = 3, "position" = 7, "approvedBy" = 1)};

// fallback
PossibleNE{->MATCHEDTEXT(arg)} HL_ResignationIndicator{NOT(PARTOF(HL_Resignation))}
PossiblePerson{NOT(FEATURE("position",null))->GETFEATURE("name",personName),GETFEATURE("position",position),GETFEATURE("company",company), 
CREATE (HL_Resignation,1,2,3, "person" = personName, "approvedBy" = arg, "position"=position, "company"=company)};

// fallback for NE
PossibleNE HL_ResignationIndicator{NOT(PARTOF(HL_Resignation))} PossibleNE 
{-> GATHER (HL_Resignation,1,2,3, "person" = 3, "approvedBy" = 1)};

// кто-то уволился с должности, ушел с поста и т.п.
PossibleNE W?{REGEXP("был.")} W?{REGEXP("решил.")} HL_ResignationIndicator{NOT(PARTOF(HL_Resignation))}
W?{REGEXP ("с|от")}
W??
W{REGEXP("(должност|пост).*")} HL_Position PossibleNE 
{-> GATHER(HL_Resignation,1,2,3,4,5,6,7,8,9, "person" = 1, "position" = 8, "company" = 9)};

// same without company
PossibleNE W?{REGEXP("был.")} W?{REGEXP("решил.")} HL_ResignationIndicator{NOT(PARTOF(HL_Resignation))}
W?{REGEXP ("с|от")} W??
W{REGEXP("(должност|пост).*")} HL_Position 
{-> GATHER(HL_Resignation,1,2,3,4,5,6,7,8, "person" = 1, "position" = 8)};

// fallback
PossiblePerson{NOT(FEATURE("position",null))->GETFEATURE("name",personName),GETFEATURE("position",position),GETFEATURE("company",company)}
"был"? "решил"? HL_ResignationIndicator{NOT(PARTOF(HL_Resignation)) 
// W?{REGEXP ("с|от")} W?? 
// W{REGEXP("(должност|пост).*") 
-> CREATE(HL_Resignation,1,2,3,4, "person"=personName, "position"=position, "company"=company)};

// fallback for NE
PossibleNE W?{REGEXP("был.")} W?{REGEXP("решил.")} HL_ResignationIndicator{NOT(PARTOF(HL_Resignation)) 
-> GATHER(HL_Resignation,1,2,3,4, "person" = 1)};

// ------------------------------------------------------------------------------------------------
// --------------------------------------- ACQUISITIONS -------------------------------------------
// ------------------------------------------------------------------------------------------------
// TODO: WRONG - transfer features
// TE? HL_Acquisition TE? {-> MARK(HL_Acquisition,1,2,3)};

// TODO: WRONG - transfer features
// "за"? HL_PriceUnit? HL_Acquisition "за"? HL_PriceUnit?  {-> MARK(HL_Acquisition,1,2,3,4,5)};

//HL_Company TE? "за"? HL_PriceUnit? TE? HL_AcquisitionIndicator TE?  "за"? HL_PriceUnit? 
//TE? ANY*? HL_Company {-> MARK(HL_Acquisition,1,2,3,4,5,6,7,8,9,10,11,12)};

// TODO add HL_PriceUnit and TE
PossibleNE HL_AnnouncementIndicator? "что"? HL_AcquisitionIndicator ANY*? PossibleNE 
{-> GATHER(HL_Acquisition,1,2,3,4,5,6, "slot1"=1,"slot2"=6)};

PossibleNE "," ANY+? "," HL_AnnouncementIndicator? "что"? HL_AcquisitionIndicator ANY*?
PossibleNE {-> GATHER(HL_Acquisition,1,2,3,4,5,6,7,8,9, "slot1"=1,"slot2"=9)};

// TODO: compare with previous
// HL_Company TE? "за"? HL_PriceUnit? TE? HL_AcquisitionIndicator TE?  "за"? HL_PriceUnit? 
// TE? ANY*? "бизнес" ANY*? HL_Company? 
//{-> GATHER (HL_Acquisition,1,2,3,4,5,6,7,8,9,10,11,12,13,14, "slot1" = 1, "slot2" = 12)};

PossibleNE HL_AcquisitionIndicator "c" ANY*? PossibleNE 
{-> GATHER(HL_Acquisition,1,2,3,4,5, "slot1"=1, "slot2"=5)};

HL_AcquisitionIndicatorS ANY*? PossibleNE ANY*? PossibleNE
{-> GATHER(HL_Acquisition,1,2,3,4,5, "slot1"=3, "slot2"=5)};

// will always fail because of PNE4
// PossibleNE "(" HL_AcquisitionIndicator PossibleNE ")"
// {-> GATHER(HL_Acquisition,1,2,3,4,5, "slot1" = 4, "slot2" = 1)};

PossibleNE W{REGEXP("(потратил|потрачен).*")} "на" HL_AcquisitionIndicatorS ANY*? PossibleNE 
HL_PriceUnit? {-> GATHER (HL_Acquisition,1,2,3,4,5,6,7, "slot1" = 1, "slot2" = 6)};

PossibleNE "на" HL_AcquisitionIndicatorS ANY*? PossibleNE W?? W{REGEXP("(потратил|потрачен).*")}  
HL_PriceUnit? {-> GATHER(HL_Acquisition,1,2,3,4,5,6,7,8, "slot1" = 1, "slot2" = 5)};

"на" HL_AcquisitionIndicatorS ANY*? PossibleNE ANY*? PossibleNE W{REGEXP("(потратил|потрачен).*")} 
HL_PriceUnit? {-> GATHER(HL_Acquisition,1,2,3,4,5,6,7,8, "slot1" = 6, "slot2" = 4)};

// ------------------------------------------------------------------------------------------------
// --------------------------------------- POSITION CHANGES ---------------------------------------
// ------------------------------------------------------------------------------------------------

// PC1
PossibleNE COMMA W{REGEXP("котор.+")} HL_PositionChangeIndicator PossibleNE 
{-> GATHER(HL_PositionChange,1,2,3,4,5, "person" = 5, "newCompany" = 1)};

// PC5
"на" W{REGEXP("должность|пост")} HL_Position PossibleNE? ANY*? HL_PositionChangeIndicator 
PossibleNE COMMA HL_Position PossibleNE?
{-> GATHER(HL_PositionChange,1,2,3,4,5,6,7,8,9,10, "person" = 7, "newPosition" = 3, "oldPosition" = 9,
"newCompany"=4, "oldCompany"=10)};

// same without leading 'на'
W{REGEXP("должность|пост")} HL_Position PossibleNE? ANY*? HL_PositionChangeIndicator{NOT(PARTOF(HL_PositionChange))} 
PossibleNE COMMA HL_Position PossibleNE?
{-> GATHER(HL_PositionChange,1,2,3,4,5,6,7,8,9, "person" = 6, "newPosition" = 2, "oldPosition" = 8,
"newCompany"=2, "oldCompany"=9)};

// same without leading 'должност|пост'
HL_Position PossibleNE? ANY*? HL_PositionChangeIndicator{NOT(PARTOF(HL_PositionChange))} PossibleNE
COMMA HL_Position PossibleNE?
{-> GATHER(HL_PositionChange,1,2,3,4,5,6,7,8, "person" = 5, "newPosition" = 1, "oldPosition" = 7,
"newCompany"=2, "oldCompany"=8)};

// PC2 - same as PC5 but without comma-surrounded 'refinement' in the end
"на" W{REGEXP("должность|пост")} HL_Position{->MATCHEDTEXT(newPosition)} 
PossibleNE?{->MATCHEDTEXT(newCompany)} ANY*? HL_PositionChangeIndicator{NOT(PARTOF(HL_PositionChange))}
PossiblePerson{NOT(FEATURE("position",null))->GETFEATURE("name",personName),GETFEATURE("position",position),GETFEATURE("company",company),
CREATE(HL_PositionChange,1,2,3,4,5,6,7, "person"=personName, "newPosition"=newPosition, 
"newCompany"=newCompany, "oldPosition"=position, "oldCompany"=company)};

W{REGEXP("должность|пост")} HL_Position{->MATCHEDTEXT(newPosition)} PossibleNE?{->MATCHEDTEXT(newCompany)}
ANY*? HL_PositionChangeIndicator{NOT(PARTOF(HL_PositionChange))} 
PossiblePerson{NOT(FEATURE("position",null))->GETFEATURE("name",personName),GETFEATURE("position",position),GETFEATURE("company",company),
CREATE(HL_PositionChange,1,2,3,4,5,6, "person" = personName, "newPosition" = newPosition, "newCompany"=newCompany, "oldPosition"=position, "oldCompany"=company)};

HL_Position{->MATCHEDTEXT(newPosition)} PossibleNE?{->MATCHEDTEXT(newCompany)} 
ANY*? HL_PositionChangeIndicator{NOT(PARTOF(HL_PositionChange))} 
PossiblePerson{NOT(FEATURE("position",null))->GETFEATURE("name",personName),GETFEATURE("position",position),GETFEATURE("company",company),
CREATE(HL_PositionChange,1,2,3,4,5, "person" = personName, "newPosition" = newPosition, "newCompany"=newCompany, "oldPosition"=position, "oldCompany"=company)};

// PC2.1 - same as PC2 but for NE instead of Person
"на" W{REGEXP("должность|пост")} HL_Position PossibleNE? ANY*? HL_PositionChangeIndicator{NOT(PARTOF(HL_PositionChange))} PossibleNE 
{-> GATHER(HL_PositionChange,1,2,3,4,5,6,7, "person" = 7, "newPosition" = 3, "newCompany"=4)};

W{REGEXP("должность|пост")} HL_Position PossibleNE? ANY*? HL_PositionChangeIndicator{NOT(PARTOF(HL_PositionChange))} PossibleNE 
{-> GATHER(HL_PositionChange,1,2,3,4,5,6, "person" = 6, "newPosition" = 2, "newCompany"=3)};

HL_Position PossibleNE? ANY*? HL_PositionChangeIndicator{NOT(PARTOF(HL_PositionChange))} PossibleNE 
{-> GATHER(HL_PositionChange,1,2,3,4,5, "person" = 5, "newPosition" = 1, "newCompany"=2)}; 

// PC3
PossiblePerson{NOT(FEATURE("position",null))->GETFEATURE("name",personName),GETFEATURE("position",position),GETFEATURE("company",company)}
ANY*? HL_PositionChangeIndicator "на"? W?{REGEXP("должность|пост")} HL_Position{->MATCHEDTEXT(newPosition)} 
PossibleNE?{->MATCHEDTEXT(newCompany), CREATE(HL_PositionChange,1,2,3,4,5,6,7, "person" = personName, "newPosition" = newPosition,
"newCompany"=newCompany, "oldPosition"=position, "oldCompany"=company)};

// PC3.1 Same as PC3 but with NE instead of Person
PossibleNE ANY*? HL_PositionChangeIndicator{NOT(PARTOF(HL_PositionChange))} "на"? W?{REGEXP("должность|пост")} 
HL_Position PossibleNE? {-> GATHER (HL_PositionChange,1,2,3,4,5,6,7, "person" = 1, "newPosition" = 6, "newCompany"=7)};

// PC4
PossibleNE COMMA HL_Position PossibleNE COMMA HL_PositionChangeIndicator 
"на"? W?{REGEXP("должность|пост")} HL_Position PossibleNE? 
{-> GATHER (HL_PositionChange,1,2,3,4,5,6,7,8,9,10, "person" = 1, "newPosition" = 9, "oldPosition" = 3, "newCompany"=10, "oldCompany"=4)};
