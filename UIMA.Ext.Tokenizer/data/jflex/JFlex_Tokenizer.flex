// Sidikov Marsel

package ru.kfu.cll.uima.tokenizer;
import ru.kfu.cll.uima.tokenizer.types.Token;
import ru.kfu.cll.uima.tokenizer.types.Letters;
import ru.kfu.cll.uima.tokenizer.types.Number;
import ru.kfu.cll.uima.tokenizer.types.Separator;
import ru.kfu.cll.uima.tokenizer.types.PM;
import ru.kfu.cll.uima.tokenizer.types.Bracket;
import ru.kfu.cll.uima.tokenizer.types.Symbol;
import ru.kfu.cll.uima.tokenizer.types.Range;
import ru.kfu.cll.uima.tokenizer.types.Abbrevation;
import ru.kfu.cll.uima.tokenizer.types.Currensy;
import ru.kfu.cll.uima.tokenizer.types.Measurement;
import ru.kfu.cll.uima.tokenizer.types.ComplexWord;
import ru.kfu.cll.uima.tokenizer.types.Date;
import ru.kfu.cll.uima.tokenizer.types.Email;
import ru.kfu.cll.uima.tokenizer.types.Abbrevation;
import ru.kfu.cll.uima.tokenizer.types.RussianWord;
import org.apache.uima.jcas.JCas;
%%
%public
%class JFlex_Tokenizer
%standalone
%type Token
%unicode

%eofval{
return null;
%eofval}

%state IN_RANGE
%state IN_CURRENSY
%state IN_MEAS
%state IN_COMPLEX
%state IN_DATE_YMD
%state IN_DATE_DMY
%state IN_PERCENTS
// Java-code ------------------------------------- 
%{
	JCas UIMA_JCas;

	Range currentRange;
	Currensy currentCurrensy;
	ComplexWord currentComplexWord;	
	Date currentDate;		
	
	boolean inRange;
	boolean currensyEnd;
	boolean inComplex;
	boolean inDate;

	int beginPosition;
	

	public JFlex_Tokenizer(java.io.Reader in, JCas UIMA_JCas) {
		this.UIMA_JCas = UIMA_JCas;
    	this.zzReader = in;
    	allBooleansToFalse();
    }
    
    public void allBooleansToFalse() {
    	this.inRange = false; 
    	this.currensyEnd = false; 
    	this.inComplex = false;
    	this.inDate = false; 		
    }
	public boolean isEof() {
		return zzAtEOF;
	}

	private void back() {
		yypushback(yylength());
	}	  
	
	public Letters getLettersToken(String language, String letterCase) {
		Letters token = new Letters(UIMA_JCas);
		token.setNorm(null);
		token.setText(yytext().toString());
		token.setLanguage(language);
		token.setLetterCase(letterCase);
		token.setBegin(zzCurrentPos);
		token.setEnd(zzCurrentPos + yylength());
		return token;
	}
	
	public Number getNumberToken(String kind, String sign) {
		Number token = new Number(UIMA_JCas);
		token.setNorm(null);
		token.setText(yytext().toString());
		token.setKind(kind);
		token.setSign(sign);
		token.setBegin(zzCurrentPos);
		token.setEnd(zzCurrentPos + yylength());
		return token;
	}
	
	public Separator getSeparatorToken(String kind) {
		Separator token = new Separator(UIMA_JCas);
		token.setNorm(null);
		token.setText(yytext().toString());
		token.setKind(kind);		
		token.setBegin(zzCurrentPos);
		token.setEnd(zzCurrentPos + yylength());
		return token;
	}
	
	public PM getPmToken() {
		PM token = new PM(UIMA_JCas);
		token.setNorm(null);
		token.setText(yytext().toString());		
		token.setBegin(zzCurrentPos);
		token.setEnd(zzCurrentPos + yylength());
		return token;
	}
	
	public Bracket getBracketToken() {
		Bracket token = new Bracket(UIMA_JCas);
		token.setNorm(null);
		token.setText(yytext().toString());		
		token.setBegin(zzCurrentPos);
		token.setEnd(zzCurrentPos + yylength());
		return token;
	}
	
	public Symbol getSymbolToken() {
		Symbol token = new Symbol(UIMA_JCas);
		token.setNorm(null);
		token.setText(yytext().toString());		
		token.setBegin(zzCurrentPos);
		token.setEnd(zzCurrentPos + yylength());
		return token;
	}
	
	public Abbrevation getAbbrevationToken(String lang) {
		Abbrevation token = new Abbrevation(UIMA_JCas);
		token.setNorm(null);
		token.setText(yytext().toString());
		token.setBegin(zzCurrentPos);
		token.setEnd(zzCurrentPos + yylength());
		token.setLanguage(lang);
		return token;
	}
	public RussianWord getRussianWordToken() {
    		RussianWord token = new RussianWOrd(UIMA_JCas);
    		token.setNorm(null);
    		token.setText(yytext().toString());
    		token.setBegin(zzCurrentPos);
    		token.setEnd(zzCurrentPos + yylength());
    		return token;
    	}

	public Range getRangeToken(String left, String right) {
		Range token = new Range(UIMA_JCas);
		token.setNorm(null);
		token.setText(yytext().toString());
		token.setBegin(zzCurrentPos);
		token.setEnd(zzCurrentPos + yylength());
		token.setLeft(left);
		token.setRight(right);
		return token;
	}	

	public Currensy getCurrensyToken(String value, String kindOfCurrensy) {
		Currensy token = new Currensy(UIMA_JCas);
		token.setNorm(null);
		token.setText(yytext().toString());
		token.setBegin(zzCurrentPos);
		token.setEnd(zzCurrentPos + yylength());
		token.setValue(value);
		token.setCurrensySymbol(kindOfCurrensy);
		return token;
	}

/*	public Measurement getMeasurementToken(Number value, String unitName) {
		Measurement token = new Measurement(UIMA_JCas);
		token.setNorm(null);
		token.setText(yytext().toString());

	}*/

	public ComplexWord getComplexWordToken(String left, String right) {
		ComplexWord token = new ComplexWord(UIMA_JCas);
		token.setNorm(null);
		token.setText(yytext().toString());
		token.setBegin(zzCurrentPos);
		token.setEnd(zzCurrentPos + yylength());
		token.setLeft(left);
		token.setRight(right);
		return token;
	}

	public Date getDateToken(String year, String mounth, String day) {
		Date token = new Date(UIMA_JCas);
		token.setNorm(null);
		token.setText(yytext().toString());
		token.setBegin(zzCurrentPos);
		token.setEnd(zzCurrentPos + yylength());
		token.setYear(year);
		token.setMounth(mounth);
		token.setDay(day);
		return token;
	}

	public Email getEmailToken() {
		Email token = new Email(UIMA_JCas);
		token.setNorm(null);
		token.setText(yytext().toString());
		token.setBegin(zzCurrentPos);
		token.setEnd(zzCurrentPos + yylength());
		return token;
	}
	
%}
// -----------------------------------------------

// REGULARS --------------------------------------	
	
	// SEPATARORS
	NEW_LINE = "\n" 
	CAR_RET = "\r" 
	TAB = "\t" 
	SPACE = " "
	FORM_FEED = "\f" 
	DEC_SEPARATOR = ","|"."
	
	

	// NUMBERS	
	DIGITS = [0-9]+
	NUMBER_REAL_POSITIVE = "+"?{DIGITS}{DEC_SEPARATOR}{DIGITS} 
	NUMBER_REAL_NEGATIVE = ("-"|"−"){DIGITS}{DEC_SEPARATOR}{DIGITS} 
	NUMBER_INTEGER_POSITIVE = ("+")?{DIGITS} 
	NUMBER_INTEGER_NEGATIVE = ("-"|"−"){DIGITS}
	NUMBER = {NUMBER_REAL_POSITIVE}|{NUMBER_REAL_NEGATIVE}|{NUMBER_INTEGER_POSITIVE}|{NUMBER_INTEGER_NEGATIVE} 	

	
	PERCENT_SYMBOL = "%"
	PERCENTS_IP_1 = {NUMBER_INTEGER_POSITIVE}{SPACE}{PERCENT_SYMBOL} 
	PERCENTS_IN_1 = {NUMBER_INTEGER_NEGATIVE}{SPACE}{PERCENT_SYMBOL}
	PERCENTS_IP_2 = {NUMBER_INTEGER_POSITIVE}{PERCENT_SYMBOL} 
	PERCENTS_IN_2 = {NUMBER_INTEGER_NEGATIVE}{PERCENT_SYMBOL}	
	PERCENTS_RP_1 = {NUMBER_REAL_POSITIVE}{SPACE}{PERCENT_SYMBOL}
	PERCENTS_RN_1 = {NUMBER_REAL_NEGATIVE}{SPACE}{PERCENT_SYMBOL}	
	PERCENTS_RP_2 = {NUMBER_REAL_POSITIVE}{PERCENT_SYMBOL}
	PERCENTS_RN_2 = {NUMBER_REAL_NEGATIVE}{PERCENT_SYMBOL}
	PERCENTS = {PERCENTS_IP_1}|{PERCENTS_IN_1}|{PERCENTS_IP_2}|{PERCENTS_IN_2}|{PERCENTS_RP_1}|{PERCENTS_RN_1}|{PERCENTS_RP_2}|{PERCENTS_RN_2}
	
	CURRENSY_SYMBOL = [$£¥₣€]
	CURRENSY_IP_1 = {NUMBER_INTEGER_POSITIVE}{CURRENSY_SYMBOL}
	CURRENSY_IP_2 = {NUMBER_INTEGER_POSITIVE}{SPACE}{CURRENSY_SYMBOL}
	CURRENSY_IP_3 = {CURRENSY_SYMBOL}{NUMBER_INTEGER_POSITIVE}
	CURRENSY_IP_4 = {CURRENSY_SYMBOL}{SPACE}{NUMBER_INTEGER_POSITIVE}
	CURRENSY_IN_1 = {NUMBER_INTEGER_NEGATIVE}{CURRENSY_SYMBOL}
	CURRENSY_IN_2 = {NUMBER_INTEGER_NEGATIVE}{SPACE}{CURRENSY_SYMBOL}
	CURRENSY_IN_3 = {CURRENSY_SYMBOL}{NUMBER_INTEGER_NEGATIVE}
	CURRENSY_IN_4 = {CURRENSY_SYMBOL}{SPACE}{NUMBER_INTEGER_NEGATIVE}
	CURRENSY_RP_1 = {NUMBER_REAL_POSITIVE}{CURRENSY_SYMBOL}
	CURRENSY_RP_2 = {NUMBER_REAL_POSITIVE}{SPACE}{CURRENSY_SYMBOL}
	CURRENSY_RP_3 = {CURRENSY_SYMBOL}{NUMBER_INTEGER_POSITIVE}
	CURRENSY_RP_4 = {CURRENSY_SYMBOL}{SPACE}{NUMBER_REAL_POSITIVE}
	CURRENSY_RN_1 = {NUMBER_REAL_NEGATIVE}{CURRENSY_SYMBOL}
	CURRENSY_RN_2 = {NUMBER_REAL_NEGATIVE}{SPACE}{CURRENSY_SYMBOL}
	CURRENSY_RN_3 = {CURRENSY_SYMBOL}{NUMBER_REAL_NEGATIVE}
	CURRENSY_RN_4 = {CURRENSY_SYMBOL}{SPACE}{NUMBER_REAL_NEGATIVE}
	CURRENSY = {CURRENSY_IP_1}|CURRENSY_IP_2}|{CURRENSY_IP_3}|CURRENSY_IP_4}|{CURRENSY_IN_1}|{CURRENSY_IN_2}|{CURRENSY_IN_3}|{CURRENSY_IN_4}|{CURRENSY_RP_1}|{CURRENSY_RP_2}|{CURRENSY_RP_3}|{CURRENSY_RP_4}|{CURRENSY_RN_1}|{CURRENSY_RN_2}|{CURRENSY_RN_3}|{CURRENSY_RN_4}
	
	// TEMPERATURE	
	TEMPERATURE_SYMBOL = "°С"|"°F" 
	TEMPERATURE_IP_1 = {NUMBER_INTEGER_POSITIVE}{SPACE}{TEMPERATURE_SYMBOL}
	TEMPERATURE_IN_1 = {NUMBER_INTEGER_NEGATIVE}{SPACE}{TEMPERATURE_SYMBOL}
	TEMPERATURE_RP_1 = {NUMBER_REAL_POSITIVE}{SPACE}{TEMPERATURE_SYMBOL}
	TEMPERATURE_RN_1 = {NUMBER_REAL_NEGATIVE}{SPACE}{TEMPERATURE_SYMBOL}
	TEMPERATURE_IP_2 = {NUMBER_INTEGER_POSITIVE}{TEMPERATURE_SYMBOL}
	TEMPERATURE_IN_2 = {NUMBER_INTEGER_NEGATIVE}{TEMPERATURE_SYMBOL}
	TEMPERATURE_RP_2 = {NUMBER_REAL_POSITIVE}{TEMPERATURE_SYMBOL}
	TEMPERATURE_RN_2 = {NUMBER_REAL_NEGATIVE}{TEMPERATURE_SYMBOL}
	TEMPERATURE = {TEMPERATURE_IP_1}|{TEMPERATURE_IN_1}|{TEMPERATURE_RP_1}|{TEMPERATURE_RN_1}|{TEMPERATURE_IP_2}|{TEMPERATURE_IN_2}|{TEMPERATURE_RP_2}|{TEMPERATURE_RN_2}
	// LETTERS
	LETTERS_RU_CAP = [A-Я][а-яё]+ 
	LETTERS_RU_LOW = [а-яё]+ 
	LETTERS_RU_UPP = [А-Я]+ 
	LETTERS_RU_MIX = [А-Яа-яёЁ]+
	LETTERS_EN_CAP = [A-Z][a-z]+ 
	LETTERS_EN_LOW = [a-z]+ 
	LETTERS_EN_UPP = [A-Z]+ 
	LETTERS_EN_MIX = [A-Za-z]+	
	LETTERS = {LETTERS_EN_MIX}|{LETTERS_EN_UPP}|{LETTERS_EN_LOW}|{LETTERS_EN_CAP}|{LETTERS_RU_MIX}|{LETTERS_RU_UPP}|{LETTERS_RU_LOW}|{LETTERS_RU_CAP}
	//DATE
	YYYY = [1-9][0-9][0-9][0-9]
	MM = ([0][0-9])|([1][0-2])
	DD = ([0-2][0-9])|([3][0-1])
	M = ([1-9])|([1][0-2])	
	D = ([1-9])|([1-2][0-9])|([3][0-1])
	DATE_YYYY_MM_DD_DOT_SEP = {YYYY}"."{SPACE}{MM}"."{SPACE}{DD}
	DATE_YYYY_MM_DD_DASH = {YYYY}[-−]{MM}[-−]{DD}
	DATE_YYYY_MM_DD_SLASH = {YYYY}"/"{MM}"/"{DD}
	DATE_YYYY_M_D_DASH = {YYYY}[-−]{M}[-−]{D}
	DATE_YYYY_M_D_SLASH = {YYYY}"/"{M}"/"{D}
	DATE_D_M_YYYY_DOT = {D}"."{M}"."{YYYY}
	DATE_D_M_YYYY_DASH = {D}[-−]{M}[-−]{YYYY}
	DATE_D_M_YYYY_SLASH = {D}"/"{M}"/"{YYYY}
	DATE_DD_MM_YYYY_DOT = {DD}"."{MM}"."{YYYY}
	DATE_DD_MM_YYYY_DASH = {DD}[-−]{MM}[-−]{YYYY}]
	DATE_DD_MM_YYYY_SLASH = {DD}"/"{MM}"/"{YYYY}
	DATE_M_D_YYYY_SLASH = {M}"/"{D}"/"{YYYY}
	DATE_YMD = {DATE_YYYY_MM_DD_DOT_SEP}|{DATE_YYYY_MM_DD_DASH}|{DATE_YYYY_MM_DD_SLASH}|{DATE_YYYY_M_D_DASH}|{DATE_YYYY_M_D_SLASH}
	DATE_DMY = {DATE_D_M_YYYY_DOT}|{DATE_D_M_YYYY_DASH}|{DATE_D_M_YYYY_SLASH}|{DATE_DD_MM_YYYY_DOT}|{DATE_DD_MM_YYYY_DASH}|{DATE_DD_MM_YYYY_SLASH}
	



	//TIME
	AM_PM = "a.m."|"p.m"|"AM"|"PM"
	//TIME_12 = ([1-9])|([1][0-2])":"([0-5][0-9])|("60")){SPACE}{AM_PM}
	TIME_24 = (([0][0-9])|([1][0-9])|([2][0-3]))":"(([0-5][0-9])|("60"))
	
	// MAIL
	LD_R = [a-z0-9_-]
	E_MAIL = ({LD_R}".")*{LD_R}"@"{LD_R}("."{LD_R}+)*"."{LETTERS_EN_LOW}
	E_MAIL2 = ([a-zA-Z0-9!#$%*+/=\?\^_\x2D\{\|\}~\.\x26]+)"@"([a-zA-Z0-9\._-]+[a-zA-Z]{2,4})
	
	RANGE = {NUMBER}("-"|"—"){NUMBER} 
	BRACKET = [\[\]\(\)\{\}]
	PM = [\.,:;!\?…——-]
	SYMBOLS = [«»$£¥₣€°@#\%\^&\*№<>\+=]|("\"")
	COMPLEX_WORD = {LETTERS}("-"|"—"){LETTERS}
	ABBREVATION_RUS = "ж/д"|"т.д."|"т. д."|"т.п."|"т. п."|"пр."|"см."|"др."|"гг."|"б/y"|"и.о."|"и. о."
	ABBREVATION_ENG = "anon."|"etc."|"a.m."|"p.m"|"AM"|"PM"
	//MEASUREMET = "кг"|"м"|"м/c"|"см"|"км/ч"
	//UINIT_OF_MEASUREMENT = {NUMBER}{SPACE}{MEASUREMENT}
//------------------------------------------------
%%
<YYINITIAL> 
	{
		{NUMBER_REAL_POSITIVE}		{ yybegin(YYINITIAL); return getNumberToken("Real","Positive"); } 
		{NUMBER_REAL_NEGATIVE}		{ yybegin(YYINITIAL); return getNumberToken("Real","Negative"); } 
		{NUMBER_INTEGER_POSITIVE}	{ yybegin(YYINITIAL); return getNumberToken("Integer","Positive"); } 
		{NUMBER_INTEGER_NEGATIVE}	{ yybegin(YYINITIAL); return getNumberToken("Integer","Negative"); }
		
		{LETTERS_RU_CAP}			{ yybegin(YYINITIAL); return getLettersToken("Russian","Capital"); }
		{LETTERS_RU_LOW}			{ yybegin(YYINITIAL); return getLettersToken("Russian","Lower"); }		
		{LETTERS_RU_UPP}			{ yybegin(YYINITIAL); return getLettersToken("Russian","Upper"); }
		{LETTERS_RU_MIX}			{ yybegin(YYINITIAL); return getLettersToken("Russian","Mixed"); }
		{LETTERS_EN_CAP}			{ yybegin(YYINITIAL); return getLettersToken("English","Capital"); }
		{LETTERS_EN_LOW}			{ yybegin(YYINITIAL); return getLettersToken("English","Lower"); }
		{LETTERS_EN_UPP}			{ yybegin(YYINITIAL); return getLettersToken("English","Upper"); }
		{LETTERS_EN_MIX}			{ yybegin(YYINITIAL); return getLettersToken("English","Mixed"); }		
		
		{NEW_LINE}					{ yybegin(YYINITIAL); return getSeparatorToken("New line"); }
		{CAR_RET}					{ yybegin(YYINITIAL); return getSeparatorToken("Carrige return"); }
		{TAB}						{ yybegin(YYINITIAL); return getSeparatorToken("Tabulation"); }
		{SPACE} 					{ yybegin(YYINITIAL); return getSeparatorToken("Space"); }
		{FORM_FEED}					{ yybegin(YYINITIAL); return getSeparatorToken("Form feed"); }
		
		{BRACKET}					{ yybegin(YYINITIAL); return getBracketToken(); }
		
		{SYMBOLS}					{ yybegin(YYINITIAL); return getSymbolToken(); }
		
		{PM}						{ yybegin(YYINITIAL); return getPmToken(); }
		
		{ABBREVATION_RUS}   		{ yybegin(YYINITIAL); return getAbbrevationToken("Russian"); }
		{ABBREVATION_ENG}   		{ yybegin(YYINITIAL); return getAbbrevationToken("English"); }

		{RANGE} {			
			currentRange = getRangeToken(null, null);
			yybegin(IN_RANGE);
			back();
			return currentRange;
		}	
		{COMPLEX_WORD} {
			currentComplexWord = getComplexWordToken(null,null);
			yybegin(IN_COMPLEX);
			back();
			return currentComplexWord;
		}
		{CURRENSY} {
			currentCurrensy = getCurrensyToken(null, null);
			yybegin(IN_CURRENSY);
			back();			
			return currentCurrensy;
		}
		{DATE_YMD} {
			currentDate = getDateToken(null,null,null);
			yybegin(IN_DATE_YMD);
			back();
			return currentDate;
		}
		{DATE_DMY} {
			currentDate = getDateToken(null,null,null);
			yybegin(IN_DATE_DMY);
			back();
			return currentDate;
		}

		{E_MAIL2} {
			return getEmailToken();
		}
	}	
<IN_RANGE>
	{
		{NUMBER_REAL_POSITIVE}	{ 
			if (inRange == true) {
				yybegin(YYINITIAL); 
				Number numberRight = getNumberToken("Real","Positive");
				currentRange.setRight(numberRight.getText());
				inRange = false;
				return numberRight;
			}
			else {
				yybegin(IN_RANGE);
				Number numberLeft = getNumberToken("Real","Positive");
				currentRange.setLeft(numberLeft.getText());
				inRange = true;
				return numberLeft;
			}			
		}		
		{NUMBER_INTEGER_POSITIVE} {
			if (inRange == true) {
				yybegin(YYINITIAL); 
				Number numberRight = getNumberToken("Integer","Positive");
				currentRange.setRight(numberRight.getText());
				inRange = false;
				return numberRight;
			}
			else {
				yybegin(IN_RANGE);
				Number numberLeft = getNumberToken("Integer","Positive");
				currentRange.setLeft(numberLeft.getText());
				inRange = true;
				return numberLeft;
			}			
		}
	}	
<IN_CURRENSY>	
{
		{NUMBER_REAL_POSITIVE} {			 
			Number numberValue = getNumberToken("Real","Positive");
			currentCurrensy.setValue(numberValue.getText());
			if (currensyEnd == true) {
				yybegin(YYINITIAL);
				currensyEnd = false;
			}
			else {
				yybegin(IN_CURRENSY);				
				currensyEnd = true;
			}
			return numberValue;
		} 
		{NUMBER_REAL_NEGATIVE} {
			Number numberValue = getNumberToken("Real","Negative");
			currentCurrensy.setValue(numberValue.getText());
			if (currensyEnd == true) {
				yybegin(YYINITIAL);
				currensyEnd = false;
			}
			else {
				yybegin(IN_CURRENSY);
				currensyEnd = true;
			}
			return numberValue;
		}
		{NUMBER_INTEGER_POSITIVE} {
			Number numberValue = getNumberToken("Integer","Positive");
			currentCurrensy.setValue(numberValue.getText());
			if (currensyEnd == true) {
				yybegin(YYINITIAL);
				currensyEnd = false;
			}
			else {
				yybegin(IN_CURRENSY);			
				currensyEnd = true;
			}
			return numberValue;
		}
		{NUMBER_INTEGER_NEGATIVE} {
			Number numberValue = getNumberToken("Integer","Negative");
			currentCurrensy.setValue(numberValue.getText());
			if (currensyEnd == true) {
				yybegin(YYINITIAL);
				currensyEnd = false;
			}
			else {
				yybegin(IN_CURRENSY);			
				currensyEnd = true;
			}
			return numberValue;
		}

		{CURRENSY_SYMBOL} {
			Symbol currensySymbol = getSymbolToken();
			currentCurrensy.setCurrensySymbol(currensySymbol.getText());
			if (currensyEnd == true) {
				yybegin(YYINITIAL);
				currensyEnd = false;
			}
			else {
				yybegin(IN_CURRENSY);				
				currensyEnd = true;
			}
			return currensySymbol;
		}	
}	
<IN_COMPLEX> 
{
	    {LETTERS_RU_CAP} {
	    	if (inComplex == true) {
	    		yybegin(YYINITIAL);
	    		Letters lettresRight = getLettersToken("Russian","Capital");
	    		currentComplexWord.setRight(lettresRight.getText());
	    		inComplex = false;
	    		return lettresRight;
	    	}
	    	else {
	    		yybegin(IN_COMPLEX);
	    		Letters lettresLeft = getLettersToken("Russian","Capital");
	    		currentComplexWord.setLeft(lettresLeft.getText());
	    		inComplex = true;
	    		return lettresLeft;
	    	}
	    }
	   	{LETTERS_RU_LOW} {
	   		if (inComplex == true) {
	    		yybegin(YYINITIAL);
	    		Letters lettresRight = getLettersToken("Russian","Lower");
	    		currentComplexWord.setRight(lettresRight.getText());
	    		inComplex = false;
	    		return lettresRight;
	    	}
	    	else {
	    		yybegin(IN_COMPLEX);
	    		Letters lettresLeft = getLettersToken("Russian","Lower");
	    		currentComplexWord.setLeft(lettresLeft.getText());
	    		inComplex = true;
	    		return lettresLeft;
	    	}
	   	}
	   	{LETTERS_RU_UPP} {
	    	if (inComplex == true) {
	    		yybegin(YYINITIAL);
	    		Letters lettresRight = getLettersToken("Russian","Upper");
	    		currentComplexWord.setRight(lettresRight.getText());
	    		inComplex = false;
	    		return lettresRight;
	    	}
	    	else {
	    		yybegin(IN_COMPLEX);
	    		Letters lettresLeft = getLettersToken("Russian","Upper");
	    		currentComplexWord.setLeft(lettresLeft.getText());
	    		inComplex = true;
	    		return lettresLeft;
	    	}
	    }
	    {LETTERS_RU_MIX} {
	    	if (inComplex == true) {
	    		yybegin(YYINITIAL);
	    		Letters lettresRight = getLettersToken("Russian","Mixed");
	    		currentComplexWord.setRight(lettresRight.getText());
	    		inComplex = false;
	    		return lettresRight;
	    	}
	    	else {
	    		yybegin(IN_COMPLEX);
	    		Letters lettresLeft = getLettersToken("Russian","Mixed");
	    		currentComplexWord.setLeft(lettresLeft.getText());
	    		inComplex = true;
	    		return lettresLeft;
	    	}
	    }
	    {LETTERS_EN_CAP} {
	    	if (inComplex == true) {
	    		yybegin(YYINITIAL);
	    		Letters lettresRight = getLettersToken("English","Capital");
	    		currentComplexWord.setRight(lettresRight.getText());
	    		inComplex = false;
	    		return lettresRight;
	    	}
	    	else {
	    		yybegin(IN_COMPLEX);
	    		Letters lettresLeft = getLettersToken("English","Capital");
	    		currentComplexWord.setLeft(lettresLeft.getText());
	    		inComplex = true;
	    		return lettresLeft;
	    	}
	    }
	   	{LETTERS_EN_LOW} {
	   		if (inComplex == true) {
	    		yybegin(YYINITIAL);
	    		Letters lettresRight = getLettersToken("English","Lower");
	    		currentComplexWord.setRight(lettresRight.getText());
	    		inComplex = false;
	    		return lettresRight;
	    	}
	    	else {
	    		yybegin(IN_COMPLEX);
	    		Letters lettresLeft = getLettersToken("English","Lower");
	    		currentComplexWord.setLeft(lettresLeft.getText());
	    		inComplex = true;
	    		return lettresLeft;
	    	}
	   	}
	   	{LETTERS_EN_UPP} {
	    	if (inComplex == true) {
	    		yybegin(YYINITIAL);
	    		Letters lettresRight = getLettersToken("English","Upper");
	    		currentComplexWord.setRight(lettresRight.getText());
	    		inComplex = false;
	    		return lettresRight;
	    	}
	    	else {
	    		yybegin(IN_COMPLEX);
	    		Letters lettresLeft = getLettersToken("English","Upper");
	    		currentComplexWord.setLeft(lettresLeft.getText());
	    		inComplex = true;
	    		return lettresLeft;
	    	}
	    }
	    {LETTERS_EN_MIX} {
	    	if (inComplex == true) {
	    		yybegin(YYINITIAL);
	    		Letters lettresRight = getLettersToken("English","Mixed");
	    		currentComplexWord.setRight(lettresRight.getText());
	    		inComplex = false;
	    		return lettresRight;
	    	}
	    	else {
	    		yybegin(IN_COMPLEX);
	    		Letters lettresLeft = getLettersToken("English","Mixed");
	    		currentComplexWord.setLeft(lettresLeft.getText());
	    		inComplex = true;
	    		return lettresLeft;
	    	}
	    }		
}	
<IN_DATE_YMD> 
{
	{YYYY} {
		Number number = getNumberToken("Integer","Positive");
		currentDate.setYear(number.getText());
		return number;
	}

	{M}|{D} {
		Number number = getNumberToken("Integer","Positive");
		if (inDate == true) {
			currentDate.setDay(number.getText());
			yybegin(YYINITIAL);
			inDate = false;
		}
		else {
			currentDate.setMounth(number.getText());
			yybegin(IN_DATE_YMD);
			inDate = true;
		}
		return number;
	}

	{MM}|{DD} {
		Number number = getNumberToken("Integer","Positive");
		if (inDate == true) {
			currentDate.setDay(number.getText());
			yybegin(YYINITIAL);
			inDate = false;
		}
		else {
			currentDate.setMounth(number.getText());
			yybegin(IN_DATE_YMD);
			inDate = true;
		}
		return number;
	}
}
<IN_DATE_DMY> {
	{YYYY} {
		Number number = getNumberToken("Integer","Positive");
		currentDate.setYear(number.getText());
		yybegin(YYINITIAL);
		return number;
	}
	{M}|{D} {
		Number number = getNumberToken("Integer","Positive");
		if (inDate == true) {
			currentDate.setMounth(number.getText());
			inDate = false;
		}
		else {
			currentDate.setDay(number.getText());			
			inDate = true;
		}
		yybegin(IN_DATE_DMY);
		return number;
	}
	{MM}|{DD} {
		Number number = getNumberToken("Integer","Positive");
		if (inDate == true) {
			currentDate.setMounth(number.getText());			
			inDate = false;
		}
		else {
			currentDate.setDay(number.getText());			
			inDate = true;
		}
		yybegin(IN_DATE_DMY);
		return number;
	}

}


