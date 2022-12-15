
package rs.ac.bg.etf.pp1;

import java_cup.runtime.Symbol;

%%

%{

	// ukljucivanje informacije o poziciji tokena
	private Symbol new_symbol(int type) {
		return new Symbol(type, yyline+1, yycolumn);
	}
	
	// ukljucivanje informacije o poziciji tokena
	private Symbol new_symbol(int type, Object value) {
		return new Symbol(type, yyline+1, yycolumn, value);
	}

%}

%cup
%line
%column

%xstate COMMENT

%eofval{
	return new_symbol(sym.EOF);
%eofval}

%%

" " 	{ }
"\b" 	{ }
"\t" 	{ }
"\r\n" 	{ }
"\f" 	{ }


//FALE enum, this, super
"program" { return new_symbol(sym.PROG, yytext());}
"break"		{ return new_symbol(sym.BREAK, yytext());}
"class" 	{ return new_symbol(sym.CLASS, yytext());}
"else" 		{ return new_symbol(sym.ELSE, yytext());}
"const"		{ return new_symbol(sym.CONST, yytext());}
"if" 		{ return new_symbol(sym.IF, yytext());}
"do" 		{ return new_symbol(sym.DO, yytext());}
"while" 	{ return new_symbol(sym.WHILE, yytext());}
"new" 		{ return new_symbol(sym.NEW, yytext());}
"print" 	{ return new_symbol(sym.PRINT, yytext());}
"read" 		{ return new_symbol(sym.READ, yytext());}
"return" 	{ return new_symbol(sym.RETURN, yytext());}
"void" 		{ return new_symbol(sym.VOID, yytext());}
"extends" 	{ return new_symbol(sym.EXTENDS, yytext());}
"continue" 	{ return new_symbol(sym.CONTINUE, yytext());}


"+" 		{ return new_symbol(sym.PLUS, yytext());}
"-"		{ return new_symbol(sym.MIN, yytext());}
"*"		{ return new_symbol(sym.MUL, yytext());}
"/"		{ return new_symbol(sym.DIV, yytext());}
"%"		{ return new_symbol(sym.MOD, yytext());}
"=="		{ return new_symbol(sym.SAME, yytext());}
"!="		{ return new_symbol(sym.DIFFER, yytext());}
">"		{ return new_symbol(sym.GRE, yytext());}
">="		{ return new_symbol(sym.GRE_EQ, yytext());}
"<"		{ return new_symbol(sym.LESS, yytext());}
"<="		{ return new_symbol(sym.LESS_EQ, yytext());}
"&&"		{ return new_symbol(sym.AND, yytext());}
"||"		{ return new_symbol(sym.OR, yytext());}
"=" 		{ return new_symbol(sym.EQUAL, yytext());}
"++"		{ return new_symbol(sym.INC, yytext());}
"--"		{ return new_symbol(sym.DEC, yytext());}
";" 		{ return new_symbol(sym.SEMI, yytext());}
":" 		{ return new_symbol(sym.COLON, yytext());}
"," 		{ return new_symbol(sym.COMMA, yytext());}
"."		{ return new_symbol(sym.DOT, yytext());}
"(" 		{ return new_symbol(sym.LPAREN, yytext());}
")" 		{ return new_symbol(sym.RPAREN, yytext());}
"{" 		{ return new_symbol(sym.LBRACE, yytext());}
"}"		{ return new_symbol(sym.RBRACE, yytext());}
"["		{ return new_symbol(sym.LBRACKET, yytext());}
"]"		{ return new_symbol(sym.RBRACKET, yytext());}
"??"	{ return new_symbol(sym.NULLISH, yytext());}


<YYINITIAL> "//" 		     { yybegin(COMMENT); }
<COMMENT> .      { yybegin(COMMENT); }
<COMMENT> "\r\n" { yybegin(YYINITIAL); }

"true"					{return new_symbol(sym.BOOLEAN, new Boolean(yytext())); }
"false"					{return new_symbol(sym.BOOLEAN, new Boolean(yytext())); }
[0-9]+  				{ return new_symbol(sym.NUMBER, new Integer (yytext())); }
([a-z]|[A-Z])[a-z|A-Z|0-9|_]* 		{return new_symbol (sym.IDENT, yytext()); }
"'"[\040-\176]"'" 			{return new_symbol (sym.CHARACTER,new Character (yytext().charAt(1))); }
. { System.err.println("Leksicka greska ("+yytext()+") u liniji "+(yyline+1)+" i koloni "+(yycolumn+1)); }
