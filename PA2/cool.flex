/*
 *  The scanner definition for COOL.
 */

/*
 *  Stuff enclosed in %{ %} in the first section is copied verbatim to the
 *  output, so headers and global definitions are placed here to be visible
 * to the code in the file.  Don't remove anything that was here initially
 */
%{
#include <cool-parse.h>
#include <stringtab.h>
#include <utilities.h>

/* The compiler assumes these identifiers. */
#define yylval cool_yylval
#define yylex  cool_yylex

/* Max size of string constants */
#define MAX_STR_CONST 1025
#define YY_NO_UNPUT   /* keep g++ happy */
#define MAX_STACK_SIZE 1025

extern FILE *fin; /* we read from this file */

/* define YY_INPUT so we read from the FILE fin:
 * This change makes it possible to use this scanner in
 * the Cool compiler.
 */
#undef YY_INPUT
#define YY_INPUT(buf,result,max_size) \
	if ( (result = fread( (char*)buf, sizeof(char), max_size, fin)) < 0) \
		YY_FATAL_ERROR( "read() in flex scanner failed");

char string_buf[MAX_STR_CONST]; /* to assemble string constants */
char *string_buf_ptr;

char state_stack[MAX_STACK_SIZE];
int stack_top=0;

int yy_push_state(int state) {
	state_stack[stack_top++]=state;
}

void yy_pop_state() {
	int state = state_stack[--stack_top];
	BEGIN(state);
}


extern int curr_lineno;
extern int verbose_flag;

extern YYSTYPE cool_yylval;

/*
 *  Add Your own definitions here
 */

%}

%x MULTILINECOMMENT
%x STRING
/*
 * Define names for regular expressions here.
 */

DARROW          =>

LE             <=
ASSIGN         <-
DIGIT          [0-9]
LETTER         [a-zA-Z:_]
SPACE          [ \t\f\r\013]
NEWLINE        [\n]
LETTERDIGIT    [a-zA-Z0-9_]

%%

 /*
  *  Nested comments
  */

 /*
  *  The multiple-character operators.
  */
{DARROW}		{ return (DARROW); }

 /*
  * Keywords are case-insensitive except for the values true and false,
  * which must begin with a lower-case letter.
  */


t[Rr][Uu][Ee]        { yylval.boolean=1; return BOOL_CONST; }
f[Aa][Ll][Ss][Ee]    { yylval.boolean=0; return BOOL_CONST; }
[Ff][Ii]	     { return FI; }
[iI][fF]	     { return IF; }
[iI][nN]	     { return IN; }
[Ii][Nn][Hh][Ee][Rr][Ii][Tt][Ss]  { return INHERITS;}
[Ll][Ee][Tt]         { return LET; }
[Ll][Oo][Oo][Pp]     { return LOOP; }
[Pp][Oo][Oo][Ll]     { return POOL; }
[Tt][Hh][Ee][Nn]     { return THEN; }
[Ww][Hh][Ii][Ll][Ee] { return WHILE; }
[Cc][Aa][Ss][Ee]     { return CASE; }
[Ee][Ss][Aa][Cc]     { return ESAC; }
[Oo][Ff]             { return OF; }
[Nn][Ee][Ww]         { return NEW; }
[Ii][Ss][Vv][Oo][Ii][Dd] {return ISVOID;}
\<-	             { return ASSIGN; }
[Nn][Oo][Tt]         { return NOT; }
{LE}		     { return LE;  }
[Ee][Ll][Ss][Ee]     { return ELSE; }


[Cc][Ll][Aa][Ss][Ss] { return CLASS; }

{SPACE}

"."                  { return '.'; }
"@"                  { return '@'; } 
"~"                  { return '~'; }
"+"                  { return '+'; }
"-"                  { return '-'; }
"*"                  { return '*'; }
"/"                  { return '/'; }
";"		     { return ';'; }
"("                  { return '('; }
")"                  { return ')'; }
"="                  { return '='; }
"<"                  { return '<'; }
"~"                  { return '~'; }
","                  { return ','; }
":"                  { return ':'; }
"{"                  { return '{'; }
"}"                  { return '}'; }


{DIGIT}+             { 
			cool_yylval.symbol=inttable.add_string(yytext);
			return INT_CONST; 
		     }
self                 { 
			cool_yylval.symbol=idtable.add_string("self");
			return OBJECTID; }
SELF_TYPE            { 
			cool_yylval.symbol=idtable.add_string("SELF_TYPE");
		        return	TYPEID; }


[a-z]{LETTERDIGIT}*       { cool_yylval.symbol=idtable.add_string(yytext); 
			return OBJECTID;
		     }
[A-Z]{LETTERDIGIT}*       { cool_yylval.symbol=idtable.add_string(yytext);
			return TYPEID;
		     }

{NEWLINE}            { curr_lineno++; }

\(\*                 {	
			//printf("begin-->%s",yytext);
			yy_push_state(INITIAL);
			BEGIN(MULTILINECOMMENT); 
		     }

<MULTILINECOMMENT>"(*" {
				//printf("nested begin-->%s",yytext);
				yy_push_state(MULTILINECOMMENT);
				BEGIN(MULTILINECOMMENT);
			}
<MULTILINECOMMENT>"*)"  {	

				//printf("end-->%s",yytext);
				yy_pop_state(); 
			}

<MULTILINECOMMENT>[^\n\*\)\(]* {
				//printf("-->%s<---\n",yytext);
			}
<MULTILINECOMMENT>"(" {}
<MULTILINECOMMENT>")" {}
<MULTILINECOMMENT>"*" {}

<MULTILINECOMMENT>{NEWLINE} {curr_lineno++;}
"*)"                 { 
			cool_yylval.error_msg="Unmatched *)";
			yy_pop_state();	
			return ERROR;
		     }
<MULTILINECOMMENT><<EOF>> { 
				
			cool_yylval.error_msg="EOF in comment";
			yy_pop_state();	
			return ERROR;
		}



"--"[^\n]*          {}




 /*
  *  String constants (C syntax)
  *  Escape sequence \c is accepted for all characters c. Except for 
  *  \n \t \b \f, the result is c.
  *
  */

\"        		{    BEGIN(STRING); string_buf_ptr=string_buf; }
<STRING>"\"" 		{
				BEGIN(INITIAL);
				int length = string_buf_ptr-string_buf; 
				char* str = new char[length+1];
				string_buf[length]='\0';
				strncpy(str, string_buf,length);
				if(length>strlen(str)){
					BEGIN(INITIAL);
					cool_yylval.error_msg="null in string.";
					return ERROR;
				}
				
                                if(length>=MAX_STR_CONST) {
					BEGIN(INITIAL);
					cool_yylval.error_msg="String constant too long";
					return ERROR;
				}
				cool_yylval.symbol = stringtable.add_string(str);
				return STR_CONST;
				}
<STRING>\0           {
			    cool_yylval.error_msg="String contains null character.";
		            BEGIN(INITIAL);
			    return ERROR;	
		       } 
<STRING>\\n {
		*string_buf_ptr='\n';
		*string_buf_ptr++;
	    }
<STRING>[^\"\\]$ {
		//printf("reach end of string with line return\n");
		cool_yylval.error_msg="Unterminated string constant";
		curr_lineno++;
                BEGIN(INITIAL);
		return ERROR;
		}
<STRING>"\\\\"   { 
			 *string_buf_ptr='\\';
                          string_buf_ptr++;
			}
<STRING>\\\n    { 
			*string_buf_ptr='\n';
			 string_buf_ptr++;
			curr_lineno++;
	
		}

<STRING>\\[^\\ntbf]   {   
			//	printf("string with escape:%s\n",yytext);
				*string_buf_ptr=(char)(*(yytext+1)); 
				string_buf_ptr++; 
			} 

<STRING>\\b  {
		*string_buf_ptr='\b';
                string_buf_ptr++;
	    }
<STRING>\\f  {
		*string_buf_ptr='\f';
                string_buf_ptr++;
	    }
<STRING>\\t  {
		*string_buf_ptr='\t';
                string_buf_ptr++;
	    }
<STRING>\t { 
	*string_buf_ptr='\t';
	string_buf_ptr++;
}

<STRING>\f {
	*string_buf_ptr='\f';
	string_buf_ptr++;
}


<STRING>[^"\\\n\t\b\f]* {
			    //printf("string no escape:%s\n",yytext);
			     
			     strncpy(string_buf_ptr,yytext,yyleng);
			     string_buf_ptr+=yyleng;   
			}
<STRING>\n    {
		//printf("end of string with error\n");
		cool_yylval.error_msg="Unterminated string constant";
		curr_lineno++;
                BEGIN(INITIAL);
		return ERROR;
		}

<STRING><<EOF>>  {
		cool_yylval.error_msg="EOF in string constant";
		BEGIN(INITIAL);
		return ERROR;
		}


.  		    {
			cool_yylval.error_msg=yytext;
			return ERROR;
		    }



%%

