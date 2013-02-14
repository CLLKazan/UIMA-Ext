// Sidikov Marsel

package tokenization;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import tokenization.types.Letters;
import tokenization.types.Number;
import tokenization.types.Range;
import tokenization.types.TokenSeparator;
import tokenization.types.Percents;
import tokenization.types.Brackets;
%%

%public
%class JFlex_Tokenizer
%standalone
%type Annotation
%unicode
%eofval{
return null;
%eofval}

// Java-code ------------------------------------- 
%{
	JCas UIMA_JCas;
	
	public JFlex_Tokenizer(java.io.Reader in, JCas UIMA_JCas) {
		this.UIMA_JCas = UIMA_JCas;
    		this.yy_reader = in;
    	}
    
	public boolean isEof() {
		return yy_atEOF;
	}
	
	Letters createLettersToken(String language, String letterCase) {
		Letters token = new Letters(UIMA_JCas);
		token.setLanguage(language);
		token.setLetterCase(letterCase);
		token.setBegin(yy_currentPos);
		token.setEnd(yy_currentPos+yylength());
		return token;
	}
	
	Number createNumberToken(String sign, String kind) {
		Number token = new Number(UIMA_JCas);
		token.setSign(sign);
		token.setKind(kind);
		token.setBegin(yy_currentPos);
		token.setEnd(yy_currentPos+yylength());
		return token;
	}
	
	Range createRangeToken() {
		Range token = new Range(UIMA_JCas);
		token.setBegin(yy_currentPos);
		token.setEnd(yy_currentPos+yylength());
		return token;
	}

	TokenSeparator createSeparatorToken(String typeValue) {
		TokenSeparator token = new TokenSeparator(UIMA_JCas);
		token.setBegin(yy_currentPos);
		token.setEnd(yy_currentPos+yylength());
		token.setTypeOfSeparator(typeValue);
		return token;
	}

	Percents createPercentsToken(Number value) {
		Percents token = new Percents(UIMA_JCas);
		token.setBegin(yy_currentPos);
		token.setEnd(yy_currentPos+yylength());
		token.setValue(value);
		return token;
	}

	Brackets createBracketToken() {
		Brackets token = new Brackets(UIMA_JCas);
		token.setBegin(yy_currentPos);
		token.setEnd(yy_currentPos+yylength());
		return token;
	}
		 
%}
// -----------------------------------------------

// REGULARS --------------------------------------
	NEW_LINE = "\n" // final
	CAR_RET = "\r" // final
	TAB = "\t" // final
	SPACE = " " // final
	FORM_FEED = "\f" // final


	DIGITS = [0-9]+// final
	DEC_SEPARATOR = "."|"," // final

	PM = [\.,:;!\?…——-]

	CURRENCY_SYMBOL = [$£¥₣€] // final

	BRACKETS = [\[\]\(\)\{\}]

	NUMBER_REAL_POSITIVE = "+"?{DIGITS}{DEC_SEPARATOR}{DIGITS} // final
	NUMBER_REAL_NEGATIVE = "-"|"−"{DIGITS}{DEC_SEPARATOR}{DIGITS} // final
	NUMBER_INTEGER_POSITIVE = "+"?{DIGITS} // final
	NUMBER_INTEGER_NEGATIVE = "-"|"−"{DIGITS} // final

	NUMBER = {NUMBER_REAL_POSITIVE}|{NUMBER_REAL_NEGATIVE}|{NUMBER_INTEGER_POSITIVE}|{NUMBER_INTEGER_NEGATIVE} // final

	LETTERS_RU_CAP = [A-Я][а-яё]+ // final
	LETTERS_RU_LOW = [а-яё]+ // final
	LETTERS_RU_UPP = [А-Я]+ // final
	LETTERS_RU_MIX = [А-Яа-яёЁ]+ // final
	LETTERS_EN_CAP = [A-Z][a-z]+ // final
	LETTERS_EN_LOW = [a-z]+ // final
	LETTERS_EN_UPP = [A-Z]+ // final
	LETTERS_EN_MIX = [A-Za-z]+ //final
	


	RANGE = {NUMBER}("-"|"—"){NUMBER} // final
	
	


//DATES_DD_MM_YY = 
//DATES_DD_MM_YYYY = 
//COMPLEX_WORD_RU = {LETTERS}"-"{LETTERS}

	EMAIL = ([a-zA-Z0-9!#$%*+'/=\?\^_\x2D`{|}~.\x26]+)@([a-zA-Z0-9\._-]+[a-zA-Z]{2,4})

	PERCENTS_1 = {NUMBER_REAL_POSITIVE}" ""%" // without type-system
	PERCENTS_2 = {NUMBER}"%" // without type-system

	CURRENSY_1 = {NUMBER}{CURRENCY_SIMBOL}
	CURRENSY_2 = {NUMBER}" "{CURRENCY_SYMBOL}
	CURRENSY_3 = {CURRENSY_SYMBOL}{NUMBER}
	CURRENSY_4 = {CURRENSY_SYMBOL}" "{NUMBER}	
//------------------------------------------------

%%
// LETTERS----------------------------------------

{LETTERS_RU_CAP} 
	{
		return createLettersToken("RU","Capitalized");
	}
{LETTERS_RU_LOW}
	{
		return createLettersToken("RU","Lower");
	}
{LETTERS_RU_UPP}
	{
		return createLettersToken("RU","Upper");
	}
{LETTERS_RU_MIX}
	{
		return createLettersToken("RU","Mixed");
	}
{LETTERS_EN_CAP} 
	{
		return createLettersToken("EN","Capitalized");
	}
{LETTERS_EN_LOW}
	{
		return createLettersToken("EN","Lower");
	}
{LETTERS_EN_UPP}
	{
		return createLettersToken("EN","Upper");
	}
{LETTERS_EN_MIX}
	{
		return createLettersToken("EN","Mixed");
	}		
//--------------------------------------------------------------------

// NUMBERS -----------------------------------------------------------

{NUMBER_INTEGER_POSITIVE}
	{
		return createNumberToken("Positive", "Integer");
	}
{NUMBER_INTEGER_NEGATIVE}
	{
		return createNumberToken("Negative", "Integer");
	}
{NUMBER_REAL_POSITIVE}
	{
		return createNumberToken("Positive", "Real");
	}
{NUMBER_REAL_NEGATIVE}
	{
		return createNumberToken("Negative", "Real");
	}

//--------------------------------------------------------------------

// SEPARATORS --------------------------------------------------------
{NEW_LINE}
	{
		return createSeparatorToken("New Line");
	}
{CAR_RET}
	{
		return createSeparatorToken("Carriage return");
	}
{TAB}
	{
		return createSeparatorToken("Tabulation");
	}
{SPACE}
	{
		return createSeparatorToken("Space");
	}
{FORM_FEED}
	{
		return createSeparatorToken("Formfeed");
	}
// -------------------------------------------------------------------

// ELSE --------------------------------------------------------------

{BRACKETS}
	{
		return createBracketToken();
	}
{RANGE}
	{
		return createRangeToken();
	}
{PERCENTS_1}
	{
		int separatorPosition = yytext().indexOf(' ');
		String value = yytext().substring(0, separatorPosition);
		Number tokenNum = new Number(UIMA_JCas);
		tokenNum.setBegin(yy_currentPos);
		tokenNum.setEnd(yy_currentPos+separatorPosition);
		tokenNum.setKind("Real");
		tokenNum.setSign("Positive");
		return createPercentsToken(tokenNum);
		
	}
{PERCENTS_2}
	{
		/*int percentsSymbolPosition = yytext().indexOf('%');
		String value = yytext().substring(0, percentsSymbolPosition);
		return createPercentsToken(value);*/
	}

//--------------------------------------------------------------------



		
