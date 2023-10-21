/*
  Created By:
  Drew Mainprize
  Christian Foote
  File Name: cminus.flex
  To Build: jflex cminus.flex
  and then after the parser is created
    javac Lexer.java
*/


   
import java_cup.runtime.*;
      
%%

%class Lexer

%eofval{
    return null;
%eofval};

%line
%column
%cup



/*
  Declarations
   
  Code between %{ and %}, both of which must be at the beginning of a
  line, will be copied letter to letter into the lexer class source.
  Here you declare member variables and functions that are used inside
  scanner actions.  
*/

%{
    private Symbol symbol(int type){
        return new Symbol(type, yyline, yycolumn);
    }

    private Symbol symbol(int type, Object value){
        return new Symbol(type, yyline, yycolumn, value);
    }
%}

/* Declare Macros */

LineTerminator = \r|\n|\r\n
whitespace     = {LineTerminator} | [ \t\f]


digit = [0-9]
number = {digit}+


letter = [a-zA-Z]
identifier = {letter}+

truth = "true"|"false"
comment = \/\*[^*]*[*]+([^/*][^*]*[*]+)*\/
%%


/* Lexical Rules */
/* Based off of the terminals declared in cminus.cup */


/* Keywords */
"bool"      { return symbol(sym.BOOL); }
"else"			{ return symbol(sym.ELSE); }
"if"			  { return symbol(sym.IF);}
"int"			  { return symbol(sym.INT); }
"return"		{ return symbol(sym.RETURN); }
"void"			{ return symbol(sym.VOID); }
"VOID"			{ return symbol(sym.VOID); }
"while"			{ return symbol(sym.WHILE); }


/* Special Symbols */
"+"				  { return symbol(sym.PLUS); }
"-"				  { return symbol(sym.MINUS); }
"*"				  { return symbol(sym.TIMES); }
"/"			    { return symbol(sym.DIV); }

"<"				  { return symbol(sym.LT); }
"<="			  { return symbol(sym.LE); }
">"				  { return symbol(sym.GT); }
">="			  { return symbol(sym.GE); }
"=="			  { return symbol(sym.EQEQ); }
"!="        { return symbol(sym.NE); }

"~"         { return symbol(sym.TILDE); }
"||"        { return symbol(sym.OR); }
"&&"        { return symbol(sym.AND); }

"="				  { return symbol(sym.EQ); }
";"				  { return symbol(sym.SEMI); }
","         { return symbol(sym.COMMA); }
"("				  { return symbol(sym.LB); }
")"				  { return symbol(sym.RB); }
"["				  { return symbol(sym.LSB); }
"]"				  { return symbol(sym.RSB); }
"{"			  	{ return symbol(sym.LCB); }
"}"				  { return symbol(sym.RCB); }


{truth}       { return symbol(sym.TRUTH, yytext()); }
{identifier}	{ return symbol(sym.ID, yytext()); }
{number}		  { return symbol(sym.NUM, yytext()); }


{whitespace}	{ /* skip whitespace */ }
{comment}		  { /* skip comments */ }
.             { System.err.println("ERROR:Line:" + yyline + " Col:" + yycolumn + ", unrecognized: \'" + yytext() + "\'"); } /* Off by one from yyline starting at 0 */
