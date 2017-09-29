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

	/**
	 * Helper functions
	 *
	 */

	private void nextToken() {
		t = scanner.nextToken();
	}

	private boolean next(Kind type) {
		return t.kind == type;
	}

	public void checkKind(Kind type) throws SyntaxException {
		if (!next(type))
			throwException(type.toString());
		nextToken();
	}

	private String errorMessage(Token tt, String s) {
		return "Expected " + s + " at "  + tt.line + ":" + tt.pos_in_line;
	}

	private String errorMessage(Token tt, String s, String msg) {
		return msg + " Expected " + s + " at "  + tt.line + ":" + tt.pos_in_line;
	}

	public void throwException(String str) throws SyntaxException {
		throw new SyntaxException(t, errorMessage(t, str));
	}

	public void throwException(String str, String msg) throws SyntaxException {
		throw new SyntaxException(t, errorMessage(t, str, msg));
	}

	//-----------------------------------------------------------------------

	/**
	 * Program ::=  IDENTIFIER   ( Declaration SEMI | Statement SEMI )*
	 *
	 * Program is start symbol of our grammar.
	 *
	 * @throws SyntaxException
	 */
	void program() throws SyntaxException {
		checkKind(Kind.IDENTIFIER);
		while (!next(Kind.EOF)) {
			switch(t.kind) {
				case KW_int:
				case KW_boolean:
				case KW_image:
				case KW_url:
				case KW_file: declaration();
				break;
				case IDENTIFIER: statement();
				break;
				default: throwException("Expected Tokens: KW_int, KW_boolean, KW_image, KW_url, KW_file, IDENTIFIER", "Error parsing program(). Expected start of a declaration / statement.");
			}
			checkKind(Kind.SEMI);
		}
	}

	//-----------------------------------------------------------------------

	/**
	 * Declaration ::= VariableDeclaration | ImageDeclaration | SourceSinkDeclaration
	 *
	 * @throws SyntaxException
	 */
	void declaration() throws SyntaxException {
		switch (t.kind) {
			case KW_int:
			case KW_boolean: variableDeclaration();
			break;
			case KW_image:  imageDeclaration();
			break;
			case KW_url:
			case KW_file:   sourceSinkDeclaration();
			break;
			default:   throwException("Expected Tokens: KW_int, KW_boolean, KW_image, KW_url, KW_file",
			"Error parsing declaration(). Expected start of a variable / image / source sink declaration.");
		}
	}

	//-----------------------------------------------------------------------

	/**
	 * VariableDeclaration  ::=  VarType IDENTIFIER ( OP_ASSIGN  Expression  | ε )
	 *
	 * @throws SyntaxException
	 */
	void variableDeclaration() throws SyntaxException {
		varType();
		checkKind(Kind.IDENTIFIER);
		if (next(Kind.OP_ASSIGN)) {
			nextToken();
			expression();
		}
	}

	/**
	 * VarType ::= KW_int | KW_boolean
	 *
	 * @throws SyntaxException
	 */
	void varType() throws SyntaxException {
		if (!next(Kind.KW_int) && !next(Kind.KW_boolean))
			throwException("Expected Tokens: KW_int, KW_boolean", "Error parsing variableDeclaration(). Expected int or boolean at the beginning of declaration.");
		nextToken();
	}

	//-----------------------------------------------------------------------

	/**
	 * ImageDeclaration ::=  KW_image  Image_Param  IDENTIFIER Image_Assign
	 *
	 * @throws SyntaxException
	 */
	void imageDeclaration() throws SyntaxException {
		checkKind(Kind.KW_image);
		imageParam();
		checkKind(Kind.IDENTIFIER);
		imageAssign();
	}

	/**
	 * Image_Param ::= LSQUARE Expression COMMA Expression RSQUARE
	 * Image_Param ::= ε
	 *
	 * @throws SyntaxException
	 */
	void imageParam() throws SyntaxException {
		if (next(Kind.LSQUARE)) {
			nextToken();
			expression();
			checkKind(Kind.COMMA);
			expression();
			checkKind(Kind.RSQUARE);
		}
	}

	/**
	 * Image_Assign ::= OP_LARROW Source
	 * Image_Assign ::= ε
	 *
	 * @throws SyntaxException
	 */
	void imageAssign() throws SyntaxException {
		if (next(Kind.OP_LARROW)) {
			nextToken();
			source();
		}
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
			case IDENTIFIER: //break;
			case STRING_LITERAL: nextToken();
			break;
			case OP_AT: nextToken();
						expression();
			break;
			default:    throwException("Expected Tokens: IDENTIFIER, STRING_LITERAL, OP_AT", "Error parsing source(). Expected source to be a string literal / identifier / @ operator.");
		}
	}

	//-----------------------------------------------------------------------

	/**
	 * SourceSinkDeclaration ::= SourceSinkType IDENTIFIER  OP_ASSIGN  Source
	 *
	 * @throws SyntaxException
	 */
	void sourceSinkDeclaration() throws SyntaxException {
		sourceSinkType();
		checkKind(Kind.IDENTIFIER);
		checkKind(Kind.OP_ASSIGN);
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
		nextToken();
	}

	//-----------------------------------------------------------------------

	/**
	 * Statement ::= IDENTIFIER STATEMENT_TAIL
	 *
	 * @throws SyntaxException
	 */
	void statement() throws SyntaxException {
		checkKind(Kind.IDENTIFIER);
		statementTail();
	}

	/**
	 * Statement_Tail ::= (LSQUARE​ ​ LhsSelector​ ​ RSQUARE​ |ε) OP_ASSIGN​ ​ Expression |
	 *      OP_RARROW Sink |
	 *					OP_LARROW Source​
	*
	* @throws SyntaxException
	*/
	void statementTail() throws SyntaxException {
		switch(t.kind) {
			case OP_ASSIGN:
			case LSQUARE: assignmentTail();
			break;
			case OP_RARROW: imageOutTail();
			break;
			case OP_LARROW: imageInTail();
			break;
			default: throwException("LSQUARE, OP_ASSIGN, OP_RARROW, OP_LARROW");
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
		expression();
	}

	/**
	 * Lhs_Tail :: = LSQUARE LhsSelector RSQUARE
	 * Lhs_Tail :: = ε
	 *
	 * @throws SyntaxException
	 */
	void lhsTail() throws SyntaxException {
		if (next(Kind.LSQUARE)) {
			lhsSelector();
			checkKind(Kind.RSQUARE);
		}
	}

	/**
	 * SelectorBody ::= LSQUARE  Selector_Body   RSQUARE
	 *
	 * @throws SyntaxException
	 */
	void lhsSelector() throws SyntaxException {
		checkKind(Kind.LSQUARE);
		selectorBody();
		checkKind(Kind.RSQUARE);
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
		case KW_r: raSelector();
		break;
		default: throwException("KW_x, KW_r");
		}
	}

	/**
	 * XySelector ::= KW_x COMMA KW_y
	 *
	 * @throws SyntaxException
	 */
	void xySelector() throws SyntaxException {
		checkKind(Kind.KW_x);
		checkKind(Kind.COMMA);
		checkKind(Kind.KW_y);
	}

	/**
	 * RaSelector ::= KW_r COMMA KW_A
	 *
	 * @throws SyntaxException
	 */
	void raSelector() throws SyntaxException {
		checkKind(Kind.KW_r);
		checkKind(Kind.COMMA);
		checkKind(Kind.KW_A);
	}

	//-----------------------------------------------------------------------

	/**
	 * ImageInTail ::=  OP_LARROW Source
	 *
	 * @throws SyntaxException
	 */
	void imageInTail() throws SyntaxException {
		checkKind(Kind.OP_LARROW);
		source();
	}

	/**
	 * ImageOutTail ::=  OP_RARROW Sink
	 *
	 * @throws SyntaxException
	 */
	void imageOutTail() throws SyntaxException {
		checkKind(Kind.OP_RARROW);
		sink();
	}

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
			case OP_PLUS:
			case OP_MINUS:  nextToken();
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
			case BOOLEAN_LITERAL:
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
			case KW_DEF_Y: unaryExpressionNotPlusOrMinus();
									break;
			default: throwException("Expected Tokens: OP_PLUS, OP_MINUS, OP_EXCL, INTEGER_LITERAL, LPAREN, KW_sin, KW_cos, KW_atan, KW_abs, KW_cart_x, KW_cart_y, KW_polar_a, KW_polar_r, IDENTIFIER, KW_x, KW_y, KW_r, KW_a, KW_X, KW_Y, KW_Z, KW_A, KW_R, KW_DEF_X, KW_DEF_Y, BOOLEAN_LITERAL", "Error parsing unaryExpression(). ");
		}
	}

	/**
	 * UnaryExpressionNotPlusMinus ::=  OP_EXCL  UnaryExpression  | Primary |
	 *	  IdentOrPixelSelectorExpression | KW_x | KW_y | KW_r | KW_a | KW_X |
	*		KW_Y | KW_Z | KW_A | KW_R | KW_DEF_X | KW_DEF_Y
	*
	* @throws SyntaxException
	*/
	void unaryExpressionNotPlusOrMinus() throws SyntaxException {
		switch(t.kind) {
			case OP_EXCL:   nextToken();
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
			case KW_polar_r:
			case BOOLEAN_LITERAL: 	primary();
									break;
			case IDENTIFIER:    identOrPixelSelectorExpression();
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
			case KW_DEF_Y:  nextToken();
							break;
			default: throwException("Excepted Tokens: OP_EXCL, KW_sin, KW_cos, KW_atan, KW_abs, KW_cart_x, KW_cart_y, KW_polar_a, KW_polar_r, IDENTIFIER, KW_x, KW_y, KW_r, KW_a, KW_X, KW_Y, KW_Z, KW_A, KW_R, KW_DEF_X, KW_DEF_Y");
		}
	}

	/**
	 * Primary ::= INTEGER_LITERAL | LPAREN Expression RPAREN | FunctionApplication
	 *
	 * @throws SyntaxException
	 */
	void primary() throws SyntaxException {
		switch(t.kind) {
			case INTEGER_LITERAL:
			case BOOLEAN_LITERAL:	nextToken();
														break;
			case LPAREN:    nextToken();
							expression();
							checkKind(Kind.RPAREN);
							break;
			case KW_sin:
			case KW_cos:
			case KW_atan:
			case KW_abs:
			case KW_cart_x:
			case KW_cart_y:
			case KW_polar_a:
			case KW_polar_r:  functionApplication();
								break;
			default: throwException("Excepted Tokens: INTEGER_LITERAL, LPAREN, KW_sin, KW_cos, KW_atan, KW_abs, KW_cart_x, KW_cart_y, KW_polar_a, KW_polar_r, BOOLEAN_LITERAL");
		}
	}

	/**
	 * IdentOrPixelSelectorExpression::=  IDENTIFIER LSQUARE Selector RSQUARE   | IDENTIFIER
	 *
	 * @throws SyntaxException
	 */
	void identOrPixelSelectorExpression() throws SyntaxException {
		checkKind(Kind.IDENTIFIER);
		pixelTail();
	}

	/**
	 * Pixel_Tail ::= LSQUARE Selector RSQUARE
	 * Pixel_Tail ::= ε
	 *
	 * @throws SyntaxException
	 */
	void pixelTail() throws SyntaxException {
		if (next(Kind.LSQUARE)) {
			nextToken();
			selector();
			checkKind(Kind.RSQUARE);
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
			case KW_polar_r: nextToken();
							break;
			default: throwException("Expected Tokens: KW_sin, KW_cos, KW_atan, KW_abs, KW_cart_x, KW_cart_y, KW_polar_a, KW_polar_r", "Error parsing functionName(). ");
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
			case LPAREN: nextToken();
							expression();
							checkKind(Kind.RPAREN);
							break;
			case LSQUARE: nextToken();
							selector();
							checkKind(Kind.RSQUARE);
							break;
			default: throwException("Expected Token: LPAREN, LSQUARE", "Error parsing functionTail().");
		}
	}

	/**
	 * Selector ::=  Expression COMMA Expression
	 *
	 * @throws SyntaxException
	 */
	void selector() throws SyntaxException {
		expression();
		checkKind(Kind.COMMA);
		expression();
	}

}
