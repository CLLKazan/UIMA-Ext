package tokenization;

%%

%public
%class JF_Tokenizer
%standalone
%type TokenInfo
%unicode

%{
	public boolean isEof() {
		return yy_atEOF;
	}
%}
LETTERS = [:letter:]
DIGITS = [:digit:]
NUMBERS = {DIGITS}+(("."|","){DIGITS}+)?

PERCENTS = {NUMBERS}" "?"%"
%%
{LETTERS}+ 
	{return (new TokenInfo("LETTERS",yy_currentPos,yy_currentPos+yylength()));}
{NUMBERS}
	{return (new TokenInfo("NUMBERS",yy_currentPos,yy_currentPos+yylength()));}
[ \t\n]
	{return (new TokenInfo("SEPARATORS",yy_currentPos,yy_currentPos+yylength()));}
(([0,1][0-9])|(2[0-3]))":"[0-5][0-9] // hh.mm
	{return (new TokenInfo("TIME",yy_currentPos,yy_currentPos+yylength()));}
{PERCENTS}
	{return (new TokenInfo("PERSENTS",yy_currentPos,yy_currentPos+yylength()));}
	