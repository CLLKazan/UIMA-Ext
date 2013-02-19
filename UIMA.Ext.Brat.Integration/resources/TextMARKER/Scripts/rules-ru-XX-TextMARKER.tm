PACKAGE rules;
TYPESYSTEM hp-text-TypeSystem;
TYPE company;
TYPE person;
TYPE position;
TYPE approvedBy;
TYPE datetime;
TYPE slot1;
TYPE slot2;

//-------------------------------------------- Numbers --------------------------------------------
// 3.5 billion
NUM+? PERIOD NUM+? HL_WordNumber*{-> MARKONCE(NUM,1,2,3,4)};
NUM+? HL_WordNumber+{-> MARKONCE(NUM,1,2)};
// twenty-two million
HL_WordNumber+{-> MARKONCE(NUM,1)};

//-------------------------------------------- Price --------------------------------------------
// 2.3 mln USD
NUM HL_CurrencyCode{-> MARKONCE(HL_PriceUnit,1,2)};
// 2.3 mln dollars
NUM HL_CurrencyUnit{-> MARKONCE(HL_PriceUnit,1,2)};
//2.3 mln USD dollars
NUM HL_CurrencyCode HL_CurrencyUnit{-> MARKONCE(HL_PriceUnit,1,2,3)};
//US$3.3 bn
HL_CurrencyUnit* NUM{-> MARKONCE(HL_PriceUnit,1,2)};

//-------------------------------------------- Companies --------------------------------------------
// first round
// total number : 1 265 822
// matched with following rules: 558535, unmatched: 707287
// ... , Inc. ...
//COMMA HL_CompanyIndicator+{-> MARKONCE(HL_CompanyIndicator, 1, 2)};
HL_CompanyIndicator CW+? HL_CompanyIndicator+{-> MARKONCE(HL_CompanyIndicator, 1, 2, 3)};
// ... Super Duper Inc. ... ; ... Super Duper, Inc. ...
CW+? HL_CompanyIndicator+{-> MARKONCE(HL_Company, 1, 2)};
CAP+? HL_CompanyIndicator+{-> MARKONCE(HL_Company, 1, 2)};
 // ... Super Duper USA Inc. ...
CW+? HL_Country HL_CompanyIndicator+?{-> MARKONCE(HL_Company, 1, 2, 3)};
HL_Company HL_Country HL_CompanyIndicator+?{-> MARKONCE(HL_Company, 1, 2, 3)};
// ... CORE Super Duper Corp. ... ; ABS Inc.
CAP+? HL_Company{-> MARKONCE(HL_Company, 1, 2)};
// ... Kent-Marsh Super Duper Inc ...
CW "-" CW+? HL_CompanyIndicator+?{-> MARKONCE(HL_Company, 1, 2, 3, 4)};
// second round
//total number : 707287
//matched with following rules: 360493 , unmatched: 346794
CAP+{-> MARKONCE(HL_Company, 1)};
CW+{-> MARKONCE(HL_Company, 1)};
HL_Company+{-> MARKONCE(HL_Company, 1)};
HL_Company+ "and" HL_Company{-> MARKONCE(HL_Company, 1, 2, 3)};
//
"hp" HL_AcquisitionIndicator HL_Company{->MARKONCE(HL_Acquisition,1,2,3)};
"хп" HL_AcquisitionIndicator HL_Company{->MARKONCE(HL_Acquisition,1,2,3)};
//
//third round
//total number : 346794
//matched with following rules: 109200 , unmatched: 237594
HL_Company+ "&" HL_Company{-> MARKONCE(HL_Company, 1, 2, 3)};
HL_Company+ "do" HL_Company{-> MARKONCE(HL_Company, 1, 2, 3)};
HL_Company+ "for" HL_Company{-> MARKONCE(HL_Company, 1, 2, 3)};
HL_Company+ "em" HL_Company{-> MARKONCE(HL_Company, 1, 2, 3)};
HL_Company+ "de" HL_Company{-> MARKONCE(HL_Company, 1, 2, 3)};
HL_Company+ "e" HL_Company{-> MARKONCE(HL_Company, 1, 2, 3)};
HL_Company+ "-" HL_Company{-> MARKONCE(HL_Company, 1, 2, 3)};
HL_Company+ "del" HL_Company{-> MARKONCE(HL_Company, 1, 2, 3)};
HL_Company+ "of" HL_Company{-> MARKONCE(HL_Company, 1, 2, 3)};
HL_Company+ "@" HL_Company{-> MARKONCE(HL_Company, 1, 2, 3)};
// finalizing
HL_Company HL_CompanyIndicator+{-> MARKONCE(HL_Company, 1, 2)};
// Cargill Inc [CARG.UL] said on Thursday it | Cargill Inc (CARG.UL) said on Thursday it
HL_Company "[" ANY*? "]"{-> MARKONCE(HL_Company, 1, 2, 3, 4)};
HL_Company "(" ANY*? ")"{-> MARKONCE(HL_Company, 1, 2, 3, 4)};
//
HL_Country ANY[0,10]? W{REGEXP("maker|company|automaker|processor|site|producer|startup|start-up")} HL_Company{-> MARKONCE(HL_Company, 1, 2, 3, 4)}; // Canadian mobile device maker Super Duper Corp
// total matched: 1 028 228 (81.2%)
HL_City "-" "based" HL_Company{-> MARKONCE(HL_Company, 1, 2, 3, 4)};    // Redmond-based Microsoft
HL_Country "'" "s" HL_Company{-> MARKONCE(HL_Company, 1, 2, 3, 4)};     // China's Lenovo
HL_Country "’" "s" HL_Company{-> MARKONCE(HL_Company, 1, 2, 3, 4)};     // China's Lenovo
                                                            // German computer and consumer-electronics company Medion AG
//-------------------------------------------- Businesses -------------------------------------------
HL_Company W[0,2]? HL_Business{->MARKONCE(HL_Business,1,2,3)};                // ... HP's printing business ...
HL_Company "'" "s" W[0,2]? HL_Business{->MARKONCE(HL_Business,1,2,3,4,5)};           // ... HP's printing business ...
HL_Business "of" "the"? HL_Company{->MARKONCE(HL_Business,1,2,3,4)};          // ... business of HP ... 
HL_Business "in" "the"? HL_Company{->MARKONCE(HL_Business,1,2,3,4)};          // ... HP's business in Palm ...
"assets" "of"? HL_Business{->MARKONCE(HL_Business,1,2,3)};                   // ... assests of HP's business
//-------------------------------------------- Persons ----------------------------------------------

//Jimmy "Jimbo" Wales
HL_FirstName "\"" W "\"" CW{->MARK(HL_LastName), MARK(HL_Person,1,2,3,4,5)};
// Jimmy Wales
HL_FirstName CW{->MARK(HL_LastName), MARK(HL_Person,1,2)};
HL_FirstName COMMA{->MARK(HL_Person,1)};
//William McLaughlin
HL_FirstName "Mc" CW{->MARK(HL_LastName,2,3), MARK(HL_Person,1,2,3)};
// John C. Williams
HL_FirstName "C" "." CW{->MARK(HL_Person,1,2,3,4)};
//James Flaherty, who was named
HL_Person COMMA "who"{->CREATE(HL_PersonReference, "person" = 1)} HL_PositionChangeIndicator;
// ------------ aggregators

HL_Company HL_Position HL_Person{->MARK(HL_Person,1, 2, 3)};                                      // HP CEO Leo Apotheker
HL_Company HL_Position {->MARK(HL_Person,1, 2)};                                                  // HP CEO 
HL_Position "of" "the"? HL_Company HL_Person{->MARK(HL_Person,1, 2, 3, 4, 5)};                    // president of the Google John White
HL_Position "of" "the"? HL_Company{->MARK(HL_Person,1, 2, 3, 4)};                                 // president of the Google
HL_Person "'" HL_Company{->MARK(HL_Company,1, 2, 3)};                                             // Bill Gross' UberMedia
//--------------------------------------- Temporal Expressions --------------------------------------

// 1999
NUM{REGEXP("(1[6-9]|2[0-2])[0-9]{2}") -> MARKONCE(TE_PossibleYear)};
// December 31, December 31st 
//TE_Month TE_PossibleDayNumber {->MARKONCE(TE,1,2)};
TE_Month NUM {->MARKONCE(TE,1,2)};
// December 31-st
//TE_Month TE_PossibleDayNumber "-" W{REGEXP("th|st|nd|rd")->MARKONCE(TE,1,2,3,4)};
TE_Month NUM "-" W[1,3]{->MARKONCE(TE,1,2,3,4)};
// второго Января
NUM TE_Month{->MARKONCE(TE,1,2)};
//второго Января 2010
NUM TE_Month NUM{->MARKONCE(TE,1,2,3)};
// May-99
TE_Month "-" NUM{REGEXP("[0-9]{2}")->MARKONCE(TE,1,2,3)};
//10-Feb
NUM "-" TE_Month{->MARKONCE(TE,1,2,3)};
// June 1995
TE_Month TE_PossibleYear{->MARKONCE(TE,1,2)};
// 14-Feb-99
NUM "-" TE_Month "-" NUM{REGEXP("^([0-9]{2})$")->MARKONCE(TE,1,2,3,4,5)};
// 20/06/87
NUM "/" NUM "/" NUM{REGEXP("^(1[6-9]|2[0-2])?[0-9]{2}$")->MARKONCE(TE,1,2,3,4,5)};
// 12.4.2009
NUM "." NUM "." NUM{REGEXP("^(1[6-9]|2[0-2])?[0-9]{2}$")->MARKONCE(TE,1,2,3,4,5)};
// 19-10-2005
NUM "-" NUM "-" NUM{REGEXP("^(1[6-9]|2[0-2])?[0-9]{2}$")->MARKONCE(TE,1,2,3,4,5)};
// 2010-10-19
NUM{REGEXP("^(1[6-9]|2[0-2])?[0-9]{2}$")} "-" NUM "-" NUM{->MARKONCE(TE,1,2,3,4,5)};
// December 30th, 2010
TE COMMA TE_PossibleYear{->MARKONCE(TE,1,2,3)};
// Monday, June 5th, 2011
TE_Day COMMA TE{->MARKONCE(TE,1,2,3)};
// on the next Friday, at the end of 2011, at the end of the year
W*?{REGEXP("on|On|in|In|at|At")} W*?{REGEXP("the|The")} TE{ ->MARKONCE(TE, 1, 2, 3)};
W*?{REGEXP("on|On|in|In|at|At")} W*?{REGEXP("the|The")} TE_Prefix*? "of"? "the"? TE_Base{ ->MARKONCE(TE, 1, 2, 3, 4, 5, 6)};
//--------------------------------------- Company's Announcements --------------------------------------
//BLOCK(Announcements) Sentence {CONTAINS(HL_AnnouncementIndicator)}
//{
	// some rules for Russian language
	TE Token[0,4]? HL_Company HL_AnnouncementIndicator{-> GATHER(HL_CompanyAnnouncement, 1, 2, 3, 4, "date" = 1, "company" = 3)};
	HL_Company Token HL_Company HL_AnnouncementIndicator{->MARKONCE(HL_CompanyAnnouncement,1,2,3,4)};
	//HL_Company COMMA Token[0,20]? COMMA HL_AnnouncementIndicator{->MARKONCE(HL_CompanyAnnouncement,1,2,3,4,5)};
	//HL_Company COMMA ANY[0,20]? COMMA HL_AnnouncementIndicator{->MARKONCE(HL_CompanyAnnouncement,1,2,3,4,5)};
	
	//

	HL_Company HL_AnnouncementIndicator{-> GATHER(HL_CompanyAnnouncement, 1, 2, "company" = 1)};                            // ... HP announced ... (HP announces it will discontinue webOS development.)
	TE HL_Company HL_AnnouncementIndicator{-> GATHER(HL_CompanyAnnouncement, 1, 2, 3, "date" = 1, "company" = 2)};          // ... yesterday HP said ... (HP yesterday announced plans to keep its PC division.)
	HL_Company TE HL_AnnouncementIndicator{-> GATHER(HL_CompanyAnnouncement, 1, 2, 3, "date" = 2, "company" = 1)};          // ... HP on Friday said ... (HP yesterday announced plans to keep its PC division.)
	HL_Company HL_AnnouncementIndicator TE{-> GATHER(HL_CompanyAnnouncement, 1, 2, 3, "date" = 3, "company" = 1)};          // ... HP has announced today ... (HP said yesterday it may shed the unit.)
	// Mavenir Systems, the leading innovator of mobile infrastructure solutions for LTE operators, today announced
	HL_Company COMMA W{REGEXP("the|which")} ANY*? COMMA TE HL_AnnouncementIndicator{-> GATHER(HL_CompanyAnnouncement, 1, 2, 3, 4, 5, 6, 7, "date" = 6, "company" = 1)};
	HL_Company COMMA W{REGEXP("the|which")} ANY*? COMMA HL_AnnouncementIndicator TE{-> GATHER(HL_CompanyAnnouncement, 1, 2, 3, 4, 5, 6, 7, "date" = 7, "company" = 1)};
	// HP has today announced that it will discontinue its operations for webOS devices including the newly released TouchPad.
	HL_Company W{REGEXP("has|have|had")} TE HL_AnnouncementIndicator{-> GATHER(HL_CompanyAnnouncement, 1, 2, 3, 4, "company" = 1, "date" = 3)};
	// HP has just announced that it plans to spin off its PC business.
	// HP have finally announced the long rumoured HP TouchPad and first impressions are that it looks pretty good.
	HL_Company W{REGEXP("has|have|had")} W HL_AnnouncementIndicator{-> GATHER(HL_CompanyAnnouncement, 1, 2, 3, 4, "company" = 1)};
	// HP also announced that customer adoption of its cloud-enabled, web-connected print technologies has reached the mainstream.
	HL_Company W HL_AnnouncementIndicator{-> GATHER(HL_CompanyAnnouncement, 1, 2, 3, "company" = 1)};
	HL_Company "'" "s" HL_AnnouncementIndicator{-> GATHER(HL_CompanyAnnouncement, 1, 2, 3, 4, "company" = 1)};             	 // As GigaOM noted of HP's announcement.
	// Here is Hewlett-Packard's press release announcing that it will discontinue webOS development.
	HL_Company "'" "s" "press" "release" HL_AnnouncementIndicator{-> GATHER(HL_CompanyAnnouncement, 1, 2, 3, 4, 5, 6, "company" = 1)};
	HL_Company "'" "s" "press" SPECIAL "release" HL_AnnouncementIndicator{-> GATHER(HL_CompanyAnnouncement, 1, 2, 3, 4, 5, 6, 7, "company" = 1)};
//}
	
//--------------------------------------- Company's References --------------------------------------
// ... Company today said that it ...
HL_CompanyAnnouncement{->GETFEATURE("company",company)} "that"? HL_EntityReference{REGEXP("it|It")->CREATE(HL_CompanyReference, "company" = company)};
HL_CompanyAnnouncement{->GETFEATURE("company",company)} HL_EntityReference{REGEXP("it|It")->CREATE(HL_CompanyReference, "company" = company)};
// --------------------------------------- Acquisitions ---------------------------------------------

// Specific grammars for Russian language
// HL_AcquisitionIndicator Token Token HL_Company Token HL_Company{-> GATHER(HL_Acquisition,1,2,3,4,5,"slot1" = 3, "slot2" = 5)};
HL_AcquisitionIndicator Token[0,6]? HL_Company Token[0,4]? HL_Company{->MARKONCE(HL_Acquisition,1,2,3,4,5)};
HL_Company HL_AnnouncementIndicator Token[0,4]? HL_AcquisitionIndicator HL_Company{->MARKONCE(HL_Acquisition,1,2,3,4,5)};
HL_Company Token[0,5]? HL_AcquisitionIndicator Token[0,4]? HL_Company{->MARKONCE(HL_Acquisition,1,2,3,4,5)};
HL_Company Token HL_Company HL_AnnouncementIndicator Token[0,4]? HL_AcquisitionIndicator{->MARKONCE(HL_Acquisition,1,2,3,4,5,6)};
HL_Company Token[0,4]? HL_AcquisitionIndicator Token[0,4]? HL_Company Token[0,4]? HL_AcquisitionIndicator Token[0,4]? HL_Company{->MARKONCE(HL_Acquisition,1,2,3,4,5,6,7,8,9)};
HL_Company Token[0,4]? HL_PriceUnit Token[0,4]? HL_AcquisitionIndicator Token[0,4]? HL_Company{->MARKONCE(HL_Acquisition,1,2,3,4,5,6,7)};
HL_Company COMMA Token[0,20]? COMMA HL_AnnouncementIndicator Token[0,4]? HL_AcquisitionIndicator Token[0,4]? HL_Company{->MARKONCE(HL_Acquisition,1,2,3,4,5,6,7,8,9)};


// end of special Russian grammars
HL_CompanyReference HL_AcquisitionIndicator "by" HL_Company{-> GATHER(HL_Acquisition,1,2,3,4,"slot1" = 4, "slot2" = 1)};
HL_Company HL_AcquisitionIndicator "by" HL_Company{-> GATHER(HL_Acquisition,1,2,3,4,"slot1" = 4, "slot2" = 1)};
HL_Company "(" HL_AcquisitionIndicator "by" HL_Company ")" {-> GATHER(HL_Acquisition,1,2,3,4,5,6, "slot1" = 5, "slot2" = 1)};

HL_CompanyReference HL_AcquisitionIndicator "the"? HL_Company{-> GATHER(HL_Acquisition,1,2,3,4,"slot1" = 1,"slot2" = 4)};
HL_CompanyReference HL_AcquisitionIndicator "the"? HL_Business{-> GATHER(HL_Acquisition,1,2,3,4,"slot1" = 1,"slot2" = 4)};
HL_CompanyReference HL_AcquisitionIndicator "the"? HL_Company "and" HL_Company{-> GATHER(HL_Acquisition,1,2,3,4,5,6,"slot1" = 1,"slot2" = 6)};
HL_Company HL_AcquisitionIndicator "the"? HL_CityPlace{-> GATHER(HL_Acquisition,1,2,3,4,"slot1" = 1, "slot2" = 4)};
HL_CompanyReference HL_AcquisitionIndicator "the"? HL_CityPlace{-> GATHER(HL_Acquisition,1,2,3,4,"slot1" = 1, "slot2" = 4)};
HL_Company HL_AcquisitionIndicator "the"? HL_Company{-> GATHER(HL_Acquisition,1,2,3,4,"slot1" = 1, "slot2" = 4)};
HL_Company HL_AcquisitionIndicator "the"? HL_Business{-> GATHER(HL_Acquisition,1,2,3,4,"slot1" = 1, "slot2" = 4)};

HL_Company HL_AcquisitionIndicator "the"? "privately" "held" HL_Company{-> GATHER(HL_Acquisition,1,2,3,4,5,6,"slot1" = 1, "slot2" = 6)};
HL_Company HL_AcquisitionIndicator "the"? "privately" "-" "held" HL_Company{-> GATHER(HL_Acquisition,1,2,3,4,5,6,7,"slot1" = 1, "slot2" = 7)};
HL_CompanyReference HL_AcquisitionIndicator "the"? "privately" "held" HL_Company{-> GATHER(HL_Acquisition,1,2,3,4,5,6,"slot1" = 1, "slot2" = 6)};
HL_CompanyReference HL_AcquisitionIndicator "the"? "privately" "-" "held" HL_Company{-> GATHER(HL_Acquisition,1,2,3,4,5,6,7,"slot1" = 1, "slot2" = 7)};

HL_Company COMMA "a" ANY*? COMMA HL_AcquisitionIndicator "the"? HL_Company{-> GATHER(HL_Acquisition,1,2,3,4,5,6,7,8, "slot1" = 1, "slot2" = 8)};
//Google's acquisition of AdMob
HL_Company SPECIAL "s" HL_AcquisitionIndicator "of" HL_Company{-> GATHER(HL_Acquisition,1,2,3,4,5,6, "slot1" = 1, "slot2" = 6)};
HL_Company SPECIAL "s" HL_AcquisitionIndicator HL_Company{-> GATHER(HL_Acquisition,1,2,3,4,5, "slot1" = 1, "slot2" = 5)};
//
HL_Company W{REGEXP("agreed|about")} TE? HL_AcquisitionIndicator "the"? HL_Company{->GATHER(HL_Acquisition, 1, 2, 3, 4, 5, 6, "slot1" = 1, "slot2" = 6)};
HL_Company W{REGEXP("agreed|about")} TE? HL_AcquisitionIndicator "the"? HL_Business{->GATHER(HL_Acquisition, 1, 2, 3, 4, 5, 6, "slot1" = 1, "slot2" = 6)};
HL_Company HL_AcquisitionIndicator "the"? HL_Business "from" HL_Company{->GATHER(HL_Acquisition, 1, 2, 3, 4, 5, 6, "slot1" = 1, "slot2" = 4)};
//
HL_Acquisition{->GETFEATURE("slot1",slot1), GETFEATURE("slot2",slot2)}  W HL_PriceUnit{->CREATE(HL_Acquisition, 1, 2, 3, "slot1"=slot1, "slot2"=slot2)};
HL_Acquisition{->GETFEATURE("slot1",slot1), GETFEATURE("slot2",slot2)} HL_PriceUnit{->CREATE(HL_Acquisition, 1, 2, "slot1"=slot1, "slot2"=slot2)};
HL_Acquisition{->GETFEATURE("slot1",slot1), GETFEATURE("slot2",slot2)} TE{->CREATE(HL_Acquisition, 1, 2, "slot1"=slot1, "slot2"=slot2)};
//
// Persons Announcements
HL_Person HL_AnnouncementIndicator{->GATHER(HL_PersonAnnouncement,1,2,"person"=1)};

// Management Position Change ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
HL_Person HL_PositionChangeIndicator W*? HL_Position{-> GATHER(HL_PositionChange,1,2,3,4,"person" = 1, "newPosition" = 4)};
HL_Person HL_PositionChangeIndicator W*? HL_Position W? HL_Company TE?{-> GATHER(HL_PositionChange,1,2,3,4,5,7,"person" = 1, "newPosition" = 4, "newCompany" = 6)};
HL_Person HL_PositionChangeIndicator HL_Position HL_Company TE?{-> GATHER(HL_PositionChange,1,2,3,4,5,"person" = 1, "newPosition" = 3, "newCompany" = 4)};
//

HL_PersonReference HL_PositionChangeIndicator W*? HL_Position{-> GATHER(HL_PositionChange,1,2,3,4,"person" = 1, "newPosition" = 4)};
HL_Person COMMA HL_Position COMMA HL_PositionChangeIndicator W*? HL_Position{-> GATHER(HL_PositionChange,1,2,3,4,5,6,7,"person" = 1, "oldPosition" = 3, "newPosition" = 7)};
HL_CompanyReference HL_PositionChangeIndicator HL_Person "as" HL_Position{-> GATHER(HL_PositionChange,1,2,3,4,5, "person" = 3, "newPosition" = 5)};
HL_CompanyReference HL_PositionChangeIndicator HL_Person HL_Position{-> GATHER(HL_PositionChange,1,2,3,4, "person" = 3, "newPosition" = 4)};
HL_Company HL_PositionChangeIndicator HL_Company HL_Position HL_Person "as" HL_Position{-> GATHER(HL_PositionChange,1,2,3,4,5,6,7, "person" = 5, "oldPosition" = 4, "newPosition" = 7)};
HL_Person COMMA ANY*? COMMA HL_PositionChangeIndicator CW HL_Position{-> GATHER(HL_PositionChange,1,2,3,4,5,6,7, "person" = 1, "newPosition" = 7)};
HL_Person COMMA "the"? HL_Position ANY*? COMMA TE? HL_PositionChangeIndicator HL_Position{-> GATHER(HL_PositionChange,1,2,3,4,5,6,7,8,9, "person" = 1, "oldPosition" = 4, "newPosition" = 9)};
// Resignation ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
HL_Person "accepted" "the"? HL_ResignationIndicator W*? HL_Person{->GATHER(HL_Resignation, 4, 5, 6, "person" = 6, "approvedBy" = 1)};
HL_Position "accepted" "the"? HL_ResignationIndicator W*? HL_Person{->GATHER(HL_Resignation, 4, 5, 6, "person" = 6, "approvedBy" = 1)};
// Some Person resigned as Position of the Company
HL_Person HL_ResignationIndicator{->GATHER(HL_Resignation, 1, 2, "person" = 1)};
HL_Company "'"? "s"? HL_Position HL_Person HL_ResignationIndicator{->GATHER(HL_Resignation, 1, 2, 3, 4, "person" = 5, "company" = 1, "position" = 4)};
HL_Position HL_Person HL_ResignationIndicator{->GATHER(HL_Resignation, 1, 2, 3, "person" = 2, "position" = 1)};
HL_Person HL_ResignationIndicator "as" HL_Position{->GATHER(HL_Resignation, 1, 2, 3, 4, "person" = 1, "position" = 4)};
HL_Person HL_ResignationIndicator W?{REGEXP("his|her")} "post"? "as" HL_Position "of" "the"? HL_Company{->GATHER(HL_Resignation, 1, 2, 3, 4, 5, 6, 7, "person" = 1, "position" = 6, "company" = 9)};
HL_Person HL_ResignationIndicator W{REGEXP("as|from")} "the"? HL_Company HL_Position{->GATHER(HL_Resignation, 1, 2, 3, 4, 5, 6, "person" = 1, "position" = 6, "company" = 5)};
//
HL_Position HL_ResignationIndicator{->GATHER(HL_Resignation, 1, 2, "person" = 1)};
HL_Person HL_AnnouncementIndicator "that"? HL_EntityReference HL_ResignationIndicator{->GATHER(HL_Resignation, 1, 2, 3, 4, 5, "person" = 1)};
HL_Person "has"? "decided" "to"? HL_ResignationIndicator{->GATHER(HL_Resignation, 1, 2, 3, 4, 5, "person" = 1)};
HL_Position "of" CW*? HL_ResignationIndicator{->GATHER(HL_Resignation, 1, 2, 3, 4, "person" = 1)};
//Some Person resigned as Position of the Company
HL_EntityReference HL_ResignationIndicator{->GATHER(HL_Resignation, 1, 2, "person" = 1)};
HL_EntityReference HL_ResignationIndicator "as" HL_Position{->GATHER(HL_Resignation, 1, 2, 3, 4, "person" = 1, "position" = 4)};
HL_EntityReference HL_ResignationIndicator "as" HL_Position "of" "the"? HL_Company{->GATHER(HL_Resignation, 1, 2, 3, 4, 5, 6, 7, "person" = 1, "position" = 4, "company" = 7)};
HL_EntityReference HL_ResignationIndicator "as" HL_Company HL_Position{->GATHER(HL_Resignation, 1, 2, 3, 4, 5, "person" = 1, "position" = 5, "company" = 4)};
//
HL_Person "has"? "confirmed" "that"? HL_EntityReference HL_ResignationIndicator{->GATHER(HL_Resignation, 1, 2, 3, 4, 5, 6, "person" = 1)};
HL_Person COMMA ANY*? COMMA HL_ResignationIndicator{->GATHER(HL_Resignation, 1, 2, 3, 4, 5, "person" = 1)};
HL_Person COMMA ANY*? COMMA HL_ResignationIndicator "as" HL_Position "of" "the"? HL_Company{->GATHER(HL_Resignation, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, "person" = 1, "company" = 10, "position" = 7)};
HL_ResignationIndicator "of" ANY*? HL_Position HL_Person{->GATHER(HL_Resignation, 1, 2, 3, 4, 5, "person" = 5, "position" = 4)};
//
HL_Resignation{->GETFEATURE("person",person), GETFEATURE("position",position), GETFEATURE("company",company), GETFEATURE("approvedBy",approvedBy)} TE{->CREATE(HL_Resignation, 1, 2, "person"=person, "position"=position, "company"=company, "approvedBy"=approvedBy)};

