package tokenization;

%%

%public
%class JF_Tokenizer
%standalone
%type TokenInfo
%unicode

%eofval{
return null;
%eofval}

%{
	public boolean isEof() {
		return yy_atEOF;
	}
%}



WHITESPACE_SEPARATOR = [ \t\r\n\f]+
PUNCTUATION_SEPARATOR = [\.,:;!\?…——-]
PAREN_SEPARATOR = [\[\]\{\}\(\)<>]
QUOTE_SEPARATOR = [`\"'«»„“’”«]
SPECIAL_SYMBOL =  [$%\^&*_+|~=/#@№\\©]
DEC_SEPARATOR = \.|,
SIGN = "+"|"-"



SEPARATOR = {PUNCTUATION_SEPARATOR} | {QUOTE_SEPARATOR} | {WHITESPACE_SEPARATOR} | {PAREN_SEPARATOR}

EMAIL = ([a-zA-Z0-9!#$%*+'/=\?\^_\x2D`{|}~.\x26]+)@([a-zA-Z0-9\._-]+[a-zA-Z]{2,4})

LETTERS_RU = [А-Яа-яёЁ]+
LETTERS_EN = [A-Za-z]+

LETTERS_RU_CAP = [А-ЯЁ]+
LETTERS_EN_CAP = [A-Z]+

LETTERS_CAP = {LETTERS_RU_CAP} | {LETTERS_EN_CAP}

LETTERS_RU_S = [а-яё]+
LETTERS_EN_S = [a-z]+
LETTERS_S = {LETTERS_RU_S} | {LETTERS_EN_S}

LETTERS = {LETTERS_S} | {LETTERS_CAP} | {LETTERS_RU} | {LETTERS_EN} 

DIGITS = [0-9]+

NUMBER = {SIGN}?{DIGITS}({DEC_SEPARATOR}{DIGITS})?

UNIT_S = см|мм|кг|гр|км|м|т
UNIT = {UNIT_S}"."

MAGN_S = млн|тыс|млрд|трлн
MAGN = {MAGN_S}"."

FACTOR_S = кв|куб
FACTOR = {FACTOR_S}"."

MEASUREMENT = {NUMBER}{WHITESPACE_SEPARATOR}?{MAGN}?({WHITESPACE_SEPARATOR}?{UNIT})?({WHITESPACE_SEPARATOR}?{FACTOR})?({WHITESPACE_SEPARATOR}?{UNIT})

DATE = {DIGITS}{1,2}"."{DIGITS}{1,2}"."{DIGITS}{2,4}
TIME = {DIGITS}{2}":"{DIGITS}{2}":"{DIGITS}{2}

PERCENTS = {NUMBER}" "?"%"

COMPLEXWORDSuffix = "-"{WHITESPACE_SEPARATOR}?{TOKEN}
COMPLEXWORD = {TOKEN}{WHITESPACE_SEPARATOR}?{COMPLEXWORDSuffix}+ 

// wierd URL!!!
//URL = (https?://)?[0-9a-z\.-]+\.[a-z\.]{2,6}[A-Za-z0-9_\./]*/?
//URL = ((https?|ftp|gopher|telnet|file|notes|ms-help):((//)|(\\\\))+[a-z0-9:#@%/;$()~_?\+=\\\.&-]*)

// wierd IP!!!
IP = {DIGITS}{1,3}"."{DIGITS}{1,3}"."{DIGITS}{1,3}"."{DIGITS}{1,3}

TOKEN = [^ \t\r\n\f`\"'«»„“’”«\.,:;!\?…\[\]\{\}\(\)<>——$%\^&*_+|~=/#@№\\©-]+


//SPECIAL = [-$%\^&*()_+|~=`{}\[\]\"'<>/#—«»@„“’”№#\\–©]

//SPECIAL = [^A-Za-z0-9А-Яа-я \.,:;!\?…]

%%
{EMAIL}
	{return (new TokenInfo("EMAIL",yy_currentPos,yy_currentPos+yylength()));}

{COMPLEXWORD}
	{return (new TokenInfo("COMPLEXWORD",yy_currentPos,yy_currentPos+yylength()));}

{MEASUREMENT}
	{return (new TokenInfo("MEASUREMENT",yy_currentPos,yy_currentPos+yylength()));}

{DATE}
	{return (new TokenInfo("DATE",yy_currentPos,yy_currentPos+yylength()));}
	
{TIME}
	{return (new TokenInfo("TIME",yy_currentPos,yy_currentPos+yylength()));}

{PERCENTS}
	{return (new TokenInfo("PERCENTS",yy_currentPos,yy_currentPos+yylength()));}

{NUMBER}
	{return (new TokenInfo("NUMBER",yy_currentPos,yy_currentPos+yylength()));}

{IP}
	{return (new TokenInfo("IP",yy_currentPos,yy_currentPos+yylength()));}


{PUNCTUATION_SEPARATOR}
	{return (new TokenInfo("PM",yy_currentPos,yy_currentPos+yylength()));}

{QUOTE_SEPARATOR}
	{return (new TokenInfo("QM",yy_currentPos,yy_currentPos+yylength()));}
	
{PAREN_SEPARATOR}
	{return (new TokenInfo("PR",yy_currentPos,yy_currentPos+yylength()));}

{SPECIAL_SYMBOL}
	{return (new TokenInfo("SPECIAL_SYMBOL",yy_currentPos,yy_currentPos+yylength()));}
	
{WHITESPACE_SEPARATOR}
	{return (new TokenInfo("WHITESPACE",yy_currentPos,yy_currentPos+yylength()));}
	
{LETTERS}
	{return (new TokenInfo("LETTERS",yy_currentPos,yy_currentPos+yylength()));}

{TOKEN}
	{return (new TokenInfo("TOKEN",yy_currentPos,yy_currentPos+yylength()));}
	