PACKAGE rules;
TYPESYSTEM hp-text-TypeSystem;
TYPE company;
TYPE person;
TYPE position;
TYPE approvedBy;
TYPE datetime;
TYPE slot1;
TYPE slot2;
//
STRING coveredTextA;
STRING coveredTextB;
//-------------------------------------------- Numbers --------------------------------------------
// 2.3
NUM+ PERIOD NUM+ {->CREATE(Number,1,2,3,"exact"=true)}; 
//1,200,000
NUM+ COMMA NUM+ COMMA NUM+{->CREATE(Number,1,2,3,4,5,"exact"=true)};
//1,200
NUM+{-PARTOF(Number)} COMMA NUM+{->CREATE(Number,1,2,3,"exact"=true)};
//344 234
NUM+{-PARTOF(Number)->CREATE(Number,1,"exact"=true)};
//3.4 trillion
Number[2,] {->CREATE(Number,1,"exact"=true)};
// 5.22-billion
Number "-" Number {->CREATE(Number,1,2,3,"exact"=true)};
//from 1,200,000 to 1,500,000 mln
W{REGEXP("from|between")} Number{->MATCHEDTEXT(coveredTextA)} W{REGEXP("to|and|up")} Number{->MATCHEDTEXT(coveredTextB),CREATE(Number,1,2,3,4,"exact"=false,"range"=true,"value1"=coveredTextA,"value2"=coveredTextB)};
Number{->MATCHEDTEXT(coveredTextA)} "to"{-PARTOF(Number)} Number{->MATCHEDTEXT(coveredTextB),CREATE(Number,1,2,3,"exact"=false,"range"=true,"value1"=coveredTextA,"value2"=coveredTextB)};
//two hundred and fifty thousand
Number "and"{-PARTOF(Number)} Number {->CREATE(Number,1,2,3,"exact"=true)};
// around 3 millions or less
FuzzyQualifier+ Number{->MATCHEDTEXT(coveredTextA)} FuzzyQualifier* {->CREATE(Number,1,2,3,"exact"=false,"value1"=coveredTextA)};
Number{->MATCHEDTEXT(coveredTextA)} FuzzyQualifier+ {-PARTOF(Number)->CREATE(Number,1,2,"exact"=false,"value1"=coveredTextA)};
//-------------------------------------------- Price Units--------------------------------------------
// 2.3 mln USD
NUM HL_CurrencyCode {->MARKONCE(HL_PriceUnit,1,2)};
// 2.3 mln dollars
NUM HL_CurrencyUnit {->MARKONCE(HL_PriceUnit,1,2)};
//2.3 mln USD dollars
NUM HL_CurrencyCode HL_CurrencyUnit {->MARKONCE(HL_PriceUnit,1,2,3)};
//US$3.3 bn
HL_CurrencyUnit* NUM {->MARKONCE(HL_PriceUnit,1,2)};