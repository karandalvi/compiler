package cop5556fa17;

//import java.util.Arrays;
import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
//import cop5556fa17.SimpleParser.SyntaxException;

import static cop5556fa17.Scanner.Kind.*;

public class SimpleParser {

	@SuppressWarnings("serial")
	public class SyntaxException extends Exception {
		Token t;

		public SyntaxException(Token t, String message) {
			super(message);
			this.t = t;
		}

	}

	Scanner scanner;
	Token t;

	SimpleParser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * Main method called by compiler to parser input.
	 * Checks for EOF
	 * 
	 * @throws SyntaxException
	 */
	
	public void parse() throws SyntaxException {
		program();
		matchEOF();
	}
	
	private boolean next(Kind type) {
		return t.kind == type;
	}
	
	private void nextToken() {
		t = scanner.nextToken();
	}
	
	public void throwException(String str) throws SyntaxException {
		throw new SyntaxException(t, errorMessage(t, str));
	}
	
	public void checkKind(Kind type) throws SyntaxException {
		if (!next(type)) 
			throwException(type.toString());
	}
	
	
	/**
	 * Program ::=  IDENTIFIER   ( Declaration SEMI | Statement SEMI )*   
	 * 
	 * Program is start symbol of our grammar.
	 * 
	 * @throws SyntaxException
	 */
	void program() throws SyntaxException {
		checkKind(Kind.IDENTIFIER);
		nextToken();
		
		while (!next(Kind.EOF)) {			
			switch(t.kind) {
				case KW_int:		//declaration();
				case KW_boolean:	//declaration();
				case KW_image:		//declaration();
				case KW_url:		//declaration();
				case KW_file: 		declaration();
							  		break;
				case IDENTIFIER: 	statement();
								 	break;
				default: 			throwException("Declaration OR Statement");
			}
			checkKind(Kind.SEMI);
			nextToken();
		}
	}

	//---------------------------------------------------------------------
	
	/**
	 * Declaration ::= VariableDeclaration | ImageDeclaration | SourceSinkDeclaration   
	 *
	 * @throws SyntaxException
	 */
	void declaration() throws SyntaxException {
		switch (t.kind) {
		case KW_int:		//variableDeclaration();
		case KW_boolean:	variableDeclaration();
							break;
		case KW_image:		imageDeclaration();
							break;
		case KW_url:		//sourceSinkDeclaration();
		case KW_file: 		sourceSinkDeclaration();
					  		break;
		default:			throwException("Declaration");
		}
	}	
	
	//---------------------------------------------------------------------
	
	/**
	 * VariableDeclaration  ::=  VarType IDENTIFIER ( OP_ASSIGN  Expression  | ε )   
	 *
	 * @throws SyntaxException
	 */
	void variableDeclaration() throws SyntaxException {
		varType();
		nextToken();
		checkKind(Kind.IDENTIFIER);
		nextToken();
		switch (t.kind) {
			case OP_ASSIGN:	nextToken();
							expression();
							break;
			case SEMI: 		break;
			default:		throwException("= OR ;");
		}
	}	
	
	/**
	 * VarType ::= KW_int | KW_boolean   
	 *
	 * @throws SyntaxException
	 */
	void varType() throws SyntaxException {
		if (!next(Kind.KW_int) && !next(Kind.KW_boolean)) 
			throw new SyntaxException(t, errorMessage(t, "KW_int or KW_boolean"));
	}	
	
	//---------------------------------------------------------------------
	
	/**
	 * ImageDeclaration ::=  KW_image  Image_Param  IDENTIFIER Image_Assign  
	 *
	 * @throws SyntaxException
	 */
	void imageDeclaration() throws SyntaxException {
		checkKind(Kind.KW_image);
		nextToken();
		imageParam();
		checkKind(Kind.IDENTIFIER);
		nextToken();
		imageAssign();
	}
	
	/**
	 * Image_Param ::= LSQUARE Expression COMMA Expression RSQUARE
	 * Image_Param ::= ε
	 * 
	 * @throws SyntaxException
	 */
	void imageParam() throws SyntaxException {
		switch(t.kind) {
			case LSQUARE:		nextToken();
								expression();
								checkKind(Kind.COMMA);
								nextToken();
								expression();
								checkKind(Kind.RSQUARE);
								nextToken();
								break;
			case IDENTIFIER:	break;
			default:			throwException("[ OR IDENTIFIER");
								break;
		}
	}
	
	/**
	 * Image_Assign ::= OP_LARROW Source
	 * Image_Assign ::= ε
	 *
	 * @throws SyntaxException
	 */
	void imageAssign() throws SyntaxException {
		switch(t.kind) {
			case OP_LARROW:		nextToken();
								source();
								break;
			case SEMI:			break;
			default:			throwException("<- OR ;");
								break;
		}
	}
	
	//---------------------------------------------------------------------
	
	/**
	 * SourceSinkDeclaration ::= SourceSinkType IDENTIFIER  OP_ASSIGN  Source  
	 *
	 * @throws SyntaxException
	 */
	void sourceSinkDeclaration() throws SyntaxException {
		sourceSinkType();
		nextToken();
		checkKind(Kind.IDENTIFIER);
		nextToken();
		checkKind(Kind.OP_ASSIGN);
		nextToken();
		source();
	}
	
	/**
	 * SourceSinkType := KW_url | KW_file   
	 *
	 * @throws SyntaxException
	 */
	void sourceSinkType() throws SyntaxException {
		if (!next(Kind.KW_url) && !next(Kind.KW_file)) 
			throwException("KW_url or KW_file");
	}
	
	/**
	 * Source ::= STRING_LITERAL  
	 * Source ::= OP_AT Expression 
	 * Source ::= IDENTIFIER   
	 *
	 * @throws SyntaxException
	 */
	void source() throws SyntaxException {
		switch(t.kind) {
			case IDENTIFIER:		//break;
			case STRING_LITERAL:	nextToken();
									break;
			case OP_AT:				nextToken();
									expression();
									break;
			default:				throwException("IDENTIFIER OR STRING_LITERAL OR @");
		}
	}
	
	//---------------------------------------------------------------------
	
	
	
	
	
	/**
	 * Sink ::= IDENTIFIER | KW_SCREEN  //ident must be file
	 *
	 * @throws SyntaxException
	 */
	void sink() throws SyntaxException {
		if (!next(Kind.KW_SCREEN) && !next(Kind.IDENTIFIER)) 
			throwException("KW_SCREEN or IDENTIFIER");
	}
	
	
	
	//---------------------------------------------------------------------
	
	/**
	 * Statement ::= IDENTIFIER STATEMENT_TAIL   
	 *
	 * @throws SyntaxException
	 */
	void statement() throws SyntaxException {
		checkKind(Kind.IDENTIFIER);
		nextToken();
		statementTail();
	}
	
	/**
	 * Statement_Tail ::= (LSQUARE​ ​ LhsSelector​ ​ RSQUARE​ |ε) OP_ASSIGN​ ​ Expression | 
	 * 					OP_RARROW Sink |
	 *					OP_LARROW Source​
	 *
	 * @throws SyntaxException
	 */
	void statementTail() throws SyntaxException {
		switch(t.kind) {
			case LSQUARE:    assignmentTail();
							 break;
			case OP_ASSIGN:  break;
			case OP_RARROW:  imageOutTail();
							 break;
			case OP_LARROW:	 imageInTail();
							 break;
			default:		 throwException("LSQUARE, OP_ASSIGN, OP_RARROW, OP_LARROW");
		}
	}	
	
	/**
	 * AssignmentTail::= LhsTail OP_ASSIGN Expression
	 *
	 * @throws SyntaxException
	 */
	void assignmentTail() throws SyntaxException {
		lhsTail();
		checkKind(Kind.OP_ASSIGN);
		nextToken();
		expression();
	}
	
	/**
	 * ImageInTail ::=  OP_LARROW Source   
	 *
	 * @throws SyntaxException
	 */
	void imageInTail() throws SyntaxException {
		checkKind(Kind.OP_LARROW);
		nextToken();
		source();
	}
	
	/**
	 * ImageOutTail ::=  OP_RARROW Sink    
	 *
	 * @throws SyntaxException
	 */
	void imageOutTail() throws SyntaxException {
		checkKind(Kind.OP_RARROW);
		nextToken();
		sink();
	}
	
	//---------------------------------------------------------------------
	
	/**
	 * Expression ::=  OrExpression  OP_Q  Expression OP_COLON Expression    | OrExpression
	 * 
	 * Our test cases may invoke this routine directly to support incremental development.
	 * 
	 * @throws SyntaxException
	 */
	void expression() throws SyntaxException {
		orExpression();
		if (next(Kind.OP_Q)) {
			expressionTail();
		}
	}

	/**
	 * OrExpression ::= AndExpression   (  OP_OR  AndExpression)*
	 *
	 * @throws SyntaxException
	 */
	void expressionTail() throws SyntaxException {
		checkKind(Kind.OP_Q);
		nextToken();
		expression();
		checkKind(Kind.OP_COLON);
		expression();
	}
	
	/**
	 * OrExpression ::= AndExpression   (  OP_OR  AndExpression)*
	 *
	 * @throws SyntaxException
	 */
	void orExpression() throws SyntaxException {
		andExpression();
		while (next(Kind.OP_OR)) {
			nextToken();
			andExpression();
		}
	}
	
	/**
	 * AndExpression ::= EqExpression ( OP_AND  EqExpression )*
	 *
	 * @throws SyntaxException
	 */
	void andExpression() throws SyntaxException {
		eqExpression();
		while (next(Kind.OP_AND)) {
			nextToken();
			eqExpression();
		}
	}
	
	/**
	 * EqExpression ::= RelExpression  (  (OP_EQ | OP_NEQ )  RelExpression )*
	 *
	 * @throws SyntaxException
	 */
	void eqExpression() throws SyntaxException {
		relExpression();
		while (next(Kind.OP_EQ) || next(Kind.OP_NEQ)) {
			nextToken();
			relExpression();
		}
	}
	
	/**
	 * RelExpression ::= AddExpression (  ( OP_LT  | OP_GT |  OP_LE  | OP_GE )  AddExpression)*
	 *
	 * @throws SyntaxException
	 */
	void relExpression() throws SyntaxException {
		addExpression();
		while (next(Kind.OP_GT) || next(Kind.OP_LT) || next(Kind.OP_GE) || next(Kind.OP_LE)) {
			nextToken();
			addExpression();
		}
	}
	
	/**
	 * AddExpression ::= MultExpression   (  (OP_PLUS | OP_MINUS ) MultExpression )*
	 *
	 * @throws SyntaxException
	 */
	void addExpression() throws SyntaxException {
		mulExpression();
		while (next(Kind.OP_PLUS) || next(Kind.OP_MINUS)) {
			nextToken();
			mulExpression();
		}
	}
	
	/**
	 * MultExpression := UnaryExpression ( ( OP_TIMES | OP_DIV  | OP_MOD ) UnaryExpression )*
	 *
	 * @throws SyntaxException
	 */
	void mulExpression() throws SyntaxException {
		unaryExpression();
		while (next(Kind.OP_TIMES) || next(Kind.OP_DIV) || next(Kind.OP_MOD)) {
			nextToken();
			unaryExpression();
		}
	}	
	
	//---------------------------------------------------------------------
	
	/**
	 * UnaryExpression ::= OP_PLUS UnaryExpression 
     *                   | OP_MINUS UnaryExpression 
     *                   | UnaryExpressionNotPlusMinus
	 *
	 * @throws SyntaxException
	 */
	void unaryExpression() throws SyntaxException {
		
		switch(t.kind) {
			case OP_PLUS: 		//unaryExpression();
			case OP_MINUS:		nextToken();
								unaryExpression();
								break;
			case OP_EXCL:
			case INTEGER_LITERAL:
			case LPAREN:
			case KW_sin:
			case KW_cos:
			case KW_atan:
			case KW_abs:
			case KW_cart_x:
			case KW_cart_y:
			case KW_polar_a:
			case KW_polar_r:
			case IDENTIFIER:
			case KW_x:
			case KW_y:
			case KW_r:
			case KW_a: 
			case KW_X:
			case KW_Y:
			case KW_Z:
			case KW_A:
			case KW_R:
			case KW_DEF_X:
			case KW_DEF_Y:			unaryExpressionNotPlusOrMinus();
									break;
			default: 				throwException("OP_PLUS, OP_MINUS, OP_EXCL, KW_sin, KW_cos, KW_atan, "
									+ "KW_abs, KW_cart_x, KW_cart_y, KW_polar_a, KW_polar_r, IDENTIFIER, "
									+ "KW_x, KW_y, KW_r, KW_a, KW_X, KW_Y, KW_Z, KW_A, KW_R, KW_DEF_X, KW_DEF_Y");
		}
		
	}	
	
	/**
	 * UnaryExpressionNotPlusMinus ::=  OP_EXCL  UnaryExpression  | Primary | 
	 *	 	IdentOrPixelSelectorExpression | KW_x | KW_y | KW_r | KW_a | KW_X | 
	 *		KW_Y | KW_Z | KW_A | KW_R | KW_DEF_X | KW_DEF_Y
	 *
	 * @throws SyntaxException
	 */
	void unaryExpressionNotPlusOrMinus() throws SyntaxException {
		switch(t.kind) {
		case OP_EXCL: 		nextToken();
							unaryExpression();
							break;
		case INTEGER_LITERAL:
		case LPAREN:
		case KW_sin:
		case KW_cos:
		case KW_atan:
		case KW_abs:
		case KW_cart_x:
		case KW_cart_y:
		case KW_polar_a:
		case KW_polar_r: 	primary();
							break;
		case IDENTIFIER:	identOrPixelSelectorExpression();
							break;
		case KW_x:
		case KW_y:
		case KW_r:
		case KW_a:
		case KW_X: 
		case KW_Y: 
		case KW_Z:
		case KW_A: 
		case KW_R:
		case KW_DEF_X: 
		case KW_DEF_Y:		nextToken();
							break;
		default: 			throwException("OP_EXCL, KW_sin, KW_cos, KW_atan, KW_abs, KW_cart_x, "
							+ "KW_cart_y, KW_polar_a, KW_polar_r, IDENTIFIER, KW_x, KW_y, KW_r, "
							+ "KW_a, KW_X, KW_Y, KW_Z, KW_A, KW_R, KW_DEF_X, KW_DEF_Y");
		}
	}
	
	/**
	 * Primary ::= INTEGER_LITERAL | LPAREN Expression RPAREN | FunctionApplication  
	 *
	 * @throws SyntaxException
	 */
	void primary() throws SyntaxException {
		switch(t.kind) {
		case INTEGER_LITERAL:	nextToken();
								break;
		case LPAREN:			nextToken();
								expression();
								checkKind(Kind.RPAREN);
								nextToken();
								break;
		case KW_sin:
		case KW_cos:
		case KW_atan:
		case KW_abs:
		case KW_cart_x:
		case KW_cart_y:
		case KW_polar_a:
		case KW_polar_r:		nextToken();
								functionApplication();
								break;
		default:				throwException("INTEGER_LITERAL, LPAREN, KW_sin, KW_cos, KW_atan,"
								+ " KW_abs, KW_cart_x, KW_cart_y, KW_polar_a, KW_polar_r");

		}
	}
	
	/**
	 * IdentOrPixelSelectorExpression::=  IDENTIFIER LSQUARE Selector RSQUARE   | IDENTIFIER  
	 *
	 * @throws SyntaxException
	 */
	void identOrPixelSelectorExpression() throws SyntaxException {
		checkKind(Kind.IDENTIFIER);
		nextToken();
		pixelTail();
	}
	
	
	/**
	 * Pixel_Tail ::= LSQUARE Selector RSQUARE
	 * Pixel_Tail ::= ε
	 *
	 * @throws SyntaxException
	 */
	void pixelTail() throws SyntaxException {
		switch(t.kind) {
		case LSQUARE:		nextToken();
							selector();
							checkKind(Kind.RSQUARE);
							nextToken();
							break;
		case OP_TIMES:
		case OP_DIV:
		case OP_MOD:
		case OP_PLUS:
		case OP_MINUS:
		case OP_LT:
		case OP_GT:
		case OP_LE:
		case OP_GE:
		case OP_EQ:
		case OP_NEQ:
		case OP_AND:
		case OP_OR:
		case OP_Q:			nextToken();
							break;
		default:			throwException("LSQUARE, OP_TIMES, OP_DIV, OP_MOD, OP_PLUS, OP_MINUS,"
							+ " OP_LT, OP_GT, OP_LE, OP_GE, OP_EQ, OP_NEQ, OP_AND, OP_OR, OP_Q");

		}
	}
	
	/**
	 * SelectorBody ::= LSQUARE  Selector_Body   RSQUARE  
	 *
	 * @throws SyntaxException
	 */
	void lhsSelector() throws SyntaxException {
		checkKind(Kind.LSQUARE);
		nextToken();
		selectorBody();
		checkKind(Kind.RSQUARE);
		nextToken();
	}
	
	/**
	 * Selector_Body :== XySelector
	 * Selector_Body :== RaSelector
	 *
	 * @throws SyntaxException
	 */
	void selectorBody() throws SyntaxException {
		switch(t.kind) {
			case KW_x:  xySelector();
						break;
			case KW_r:	raSelector();
						break;
			default:	throwException("KW_x, KW_r");
		}
	}

	/**
	 * FunctionApplication ::= FunctionName LPAREN Expression RPAREN  
	 *						 | FunctionName  LSQUARE Selector RSQUARE    
	 *
	 * @throws SyntaxException
	 */
	void functionApplication() throws SyntaxException {
		functionName();
		functionTail();
	}
	
	/**
	 *   FunctionName ::= KW_sin | KW_cos | KW_atan | KW_abs | KW_cart_x | KW_cart_y | KW_polar_a | KW_polar_r
	 *
	 * @throws SyntaxException
	 */
	void functionName() throws SyntaxException {
		switch(t.kind) {
		case KW_sin:
		case KW_cos:
		case KW_atan:
		case KW_abs:
		case KW_cart_x:
		case KW_cart_y:
		case KW_polar_a:
		case KW_polar_r:	nextToken();
							break;
		default:			throwException("KW_sin, KW_cos, KW_atan, KW_abs, KW_cart_x, KW_cart_y, KW_polar_a, KW_polar_r");
		}
	}
	
	/**
	 * FunctionTail ::= LPAREN Expression RPAREN  
	 * FunctionTail ::= LSQUARE Selector RSQUARE
	 *  
	 * @throws SyntaxException
	 */
	void functionTail() throws SyntaxException {
		switch(t.kind) {
		case LPAREN:	nextToken();
						expression();
						checkKind(Kind.RPAREN);
						nextToken();
						break;
		case LSQUARE:	nextToken();
						selector();
						checkKind(Kind.RSQUARE);
						nextToken();
						break;
		default:		throwException("LPAREN, LSQUARE");
		}
	}	
	

	/**
	 * XySelector ::= KW_x COMMA KW_y   
	 *
	 * @throws SyntaxException
	 */
	void xySelector() throws SyntaxException {
		checkKind(Kind.KW_x);
		nextToken();
		checkKind(Kind.COMMA);
		nextToken();
		checkKind(Kind.KW_y);
		nextToken();
	}
	
	/**
	 * RaSelector ::= KW_r COMMA KW_A 
	 *
	 * @throws SyntaxException
	 */
	void raSelector() throws SyntaxException {
		checkKind(Kind.KW_r);
		nextToken();
		checkKind(Kind.COMMA);
		nextToken();
		checkKind(Kind.KW_A);
		nextToken();
	}
	
	/**
	 * Selector ::=  Expression COMMA Expression    
	 *
	 * @throws SyntaxException
	 */
	void selector() throws SyntaxException {
		expression();
		checkKind(Kind.COMMA);
		nextToken();
		expression();
	}

	/**
	 * Lhs_Tail :: = LSQUARE LhsSelector RSQUARE
	 * Lhs_Tail :: = ε 
	 * 
	 * @throws SyntaxException
	 */
	void lhsTail() throws SyntaxException {
		checkKind(Kind.LSQUARE);
		nextToken();
		lhsSelector();
		checkKind(Kind.RSQUARE);
		nextToken();
	}
	//---------------------------------------------------------------------
	
	/**
	 * Only for check at end of program. Does not "consume" EOF so no attempt to get
	 * nonexistent next Token.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (t.kind == EOF) {
			return t;
		}
		String message =  "Expected EOL at " + t.line + ":" + t.pos_in_line;
		throw new SyntaxException(t, message);
	}
	
	private String errorMessage(Token tt, String s) {
		return "Expected " + s + " at "  + tt.line + ":" + tt.pos_in_line;
	}
}
