package cop5556fa17;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cop5556fa17.Parser.SyntaxException;

import static cop5556fa17.Scanner.Kind.*;
import cop5556fa17.AST.*;
public class Parser {

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

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * Main method called by compiler to parser input.
	 * Checks for EOF
	 *
	 * @throws SyntaxException
	 */

	public Program parse() throws SyntaxException {
		Program p = program();
		matchEOF();
		return p;
	}

	/**
	 * Only for check at end of program. Does not "consume" EOF so no attempt
	 * to get nonexistent next Token.
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

	void matchEOF_() throws SyntaxException {
		if (t.kind == EOF) {
			return;
		}
		String message =  "Expected EOL at " + t.line + ":" + t.pos_in_line;
		throw new SyntaxException(t, message);
	}
	
	private void nextToken() {
		t = scanner.nextToken();
	}

	private boolean next(Kind type) {
		return t.kind == type;
	}

	public void checkKind(Kind type, String errorMsg) throws SyntaxException {
		if (!next(type))
			raiseException(errorMsg + "Expected Token: " + type.toString() + "\n");
		nextToken();
	}

	private String errorLocation() {
		return "Found " + t.kind.toString() + " ==> " + t.getText() +
			" at " + t.line + ":" + t.pos_in_line;
	}

	public void raiseException(String errorMsg) throws SyntaxException {
		throw new SyntaxException(t, "SyntaxException\nParsing Error: " +
			errorMsg + errorLocation());
	}
	
	//-------------------------------------------------------------------------

	/**
	 * Program ::=  IDENTIFIER   ( Declaration SEMI | Statement SEMI )*
	 * Program is start symbol of our grammar.
	 *
	 * @throws SyntaxException
	 */
	Program program() throws SyntaxException {
		Token firstToken = t;
		checkKind(Kind.IDENTIFIER, "program()\nExpected: Start of program\n");
		ArrayList<ASTNode> decOrStat = new ArrayList<>();
		
		while (!next(Kind.EOF)) {
			switch(t.kind) {
				case KW_int:
				case KW_boolean:
				case KW_image:
				case KW_url:
				case KW_file: decOrStat.add(declaration());
							  break;
				case IDENTIFIER: decOrStat.add(statement());
								 break;
				default: raiseException("program()\nExpected: Start of a declaration" +
				" / statement\nExpected Token: KW_int / KW_boolean / KW_image /" +
				" KW_url / KW_file / IDENTIFIER\n");
			}
			checkKind(Kind.SEMI, "program()\nExpected: End of a declaration / " +
			"statement\n");
		}
		return new Program(firstToken, firstToken, decOrStat);
	}

	//-------------------------------------------------------------------------
	
	/**
	 * Declaration ::= VariableDeclaration | ImageDeclaration | SourceSinkDeclaration
	 *
	 * @throws SyntaxException
	 */
	Declaration declaration() throws SyntaxException {
		switch (t.kind) {
			case KW_int:
			case KW_boolean: return variableDeclaration();
			case KW_image: return imageDeclaration();
			case KW_url:
			case KW_file: return sourceSinkDeclaration();
			default: raiseException("declaration()\nExpected: Start of declaration" +
			"\nExpected Token: KW_int / KW_boolean / KW_image / KW_url / KW_file\n");
		}
		return null;
	}

	//-------------------------------------------------------------------------

	/**
	 * VariableDeclaration  ::=  VarType IDENTIFIER ( OP_ASSIGN  Expression  | ε )
	 *
	 * @throws SyntaxException
	 */
	Declaration variableDeclaration() throws SyntaxException {
		Token firstToken = t;
		Token type = varType();
		Token identifier = t;
		checkKind(Kind.IDENTIFIER, "variableDeclaration()\nExpected: Identifier " +
		"being declared\n");
		
		Expression e = null;
		if (next(Kind.OP_ASSIGN)) {
			nextToken();
			e = expression();
		}
		return new Declaration_Variable(firstToken, type, identifier, e);
	}

	/**
	 * VarType ::= KW_int | KW_boolean
	 *
	 * @throws SyntaxException
	 */
	Token varType() throws SyntaxException {
		Token type = t;
		if (!next(Kind.KW_int) && !next(Kind.KW_boolean))
			raiseException("varType()\nExpected: Start of variable declaration\n" +
			"Expected Token: KW_int / KW_boolean\n");
		nextToken();
		return type;
	}


	//-------------------------------------------------------------------------

	/**
	 * ImageDeclaration ::=  KW_image  Image_Param  IDENTIFIER Image_Assign
	 *
	 * @throws SyntaxException
	 */
	Declaration imageDeclaration() throws SyntaxException {
		Token firstToken = t;
		checkKind(Kind.KW_image, "checkKind()\nExpected: Start of image declaration\n");
		List<Expression> param = imageParam();
		Token ident = t;
		checkKind(Kind.IDENTIFIER, "checkKind()\nExpected: Identifier in image declaration\n");
		Source src = imageAssign(); //TODO: could be null
		return new Declaration_Image(firstToken, param.get(0), param.get(1), ident, src);
	}

	/**
	 * Image_Param ::= LSQUARE Expression COMMA Expression RSQUARE
	 * Image_Param ::= ε
	 *
	 * @throws SyntaxException
	 */
	List<Expression> imageParam() throws SyntaxException {
		List<Expression> param = new ArrayList<>();
		if (next(Kind.LSQUARE)) {
			nextToken();
			param.add(expression());
			checkKind(Kind.COMMA, "imageParam()\nExpected: Comma between two expression\n");
			param.add(expression());
			checkKind(Kind.RSQUARE, "imageParam()\nExpected: Closing bracket for image parameters\n");
		}
		else {
			param.add(null);
			param.add(null);
		}	
		return param;
	}

	/**
	 * Image_Assign ::= OP_LARROW Source
	 * Image_Assign ::= ε
	 *
	 * @throws SyntaxException
	 */
	Source imageAssign() throws SyntaxException {
		if (next(Kind.OP_LARROW)) {
			nextToken();
			return source();
		}
		return null;
	}

	/**
	 * Source ::= STRING_LITERAL
	 * Source ::= OP_AT Expression
	 * Source ::= IDENTIFIER
	 *
	 * @throws SyntaxException
	 */
	Source source() throws SyntaxException {
		Token firstToken = t;
		Source s = null;
		switch(t.kind) {
			case IDENTIFIER: s = new Source_Ident(firstToken, t);
							 nextToken();
							 break;
							 
			case STRING_LITERAL: s = new Source_StringLiteral(firstToken, t.getText());
								 nextToken();
				 				 break;
				 				 
			case OP_AT: nextToken();
						s = new Source_CommandLineParam(firstToken, expression());
						break;
						
			default: raiseException("source()\nExpected: A valid source entity\n" +
			"Expected Token: IDENTIFIER / STRING_LITERAL / OP_AT\n");
		}
		return s;
	}

	//-------------------------------------------------------------------------

	/**
	 * SourceSinkDeclaration ::= SourceSinkType IDENTIFIER  OP_ASSIGN  Source
	 *
	 * @throws SyntaxException
	 */
	Declaration sourceSinkDeclaration() throws SyntaxException {
		Token firstToken = t;
		Token type = sourceSinkType();
		Token ident = t;
		checkKind(Kind.IDENTIFIER, "sourceSinkDeclaration()\nExpected: Identifier in source sink declaration \n");
		checkKind(Kind.OP_ASSIGN, "sourceSinkDeclaration()\nExpected: Assignment operator\n");
		Source src = source();
		return new Declaration_SourceSink(firstToken, type, ident, src);
	}

	/**
	 * SourceSinkType := KW_url | KW_file
	 *
	 * @throws SyntaxException
	 */
	Token sourceSinkType() throws SyntaxException {
		Token type = t;
		if (!next(Kind.KW_url) && !next(Kind.KW_file))
			raiseException("sourceSinkType()\nExpected: Valid source sink\nExpected Token: KW_url / KW_file \n");
		nextToken();
		return type;
	}

	//-------------------------------------------------------------------------

	/**
	 * Statement ::= IDENTIFIER STATEMENT_TAIL
	 *
	 * @throws SyntaxException
	 */
	Statement statement() throws SyntaxException {
		Token ident = t;
		checkKind(Kind.IDENTIFIER, "statement()\nExpected: Identifier at the start of statement\n");
		return statementTail(ident);
	}
 
	/**
	 * Statement_Tail ::= AssignmentTail | ImageInTail | ImageOutTail
	 *
	 * @throws SyntaxException
	 */
	Statement statementTail(Token ident) throws SyntaxException {
		switch(t.kind) {
			case OP_ASSIGN:
			case LSQUARE: return assignmentTail(ident);
			case OP_RARROW: Sink sink = imageOutTail();
							return new Statement_Out(ident, ident, sink);
			case OP_LARROW: Source source = imageInTail();
							return new Statement_In(ident, ident, source);
			default: raiseException("statementTail()\nExpected: Start of assignment" +
			" tail / image in tail / image out tail\nExpected Token: OP_ASSIGN / " +
			"LSQUARE / OP_RARROW / OP_LARROW\n");
		}
		return null;
	}

	/**
	 * AssignmentTail::= LhsTail OP_ASSIGN Expression
	 *
	 * @throws SyntaxException
	 */
	Statement assignmentTail(Token ident) throws SyntaxException {
		LHS lhs = lhsTail(ident);
		checkKind(Kind.OP_ASSIGN, "assingmentTail()\nExpected: Assignment Operator\n");
		Expression e = expression();
		return new Statement_Assign(ident, lhs, e);
	}

	/**
	 * Lhs_Tail :: = LSQUARE LhsSelector RSQUARE
	 * Lhs_Tail :: = ε
	 *
	 * @throws SyntaxException
	 */
	LHS lhsTail(Token ident) throws SyntaxException {
		Index i = null;
		if (next(Kind.LSQUARE)) {
			nextToken();
			i = lhsSelector();
			checkKind(Kind.RSQUARE, "lhsTail()\nExpected: Closing brackets for selector\n");
		}
		return new LHS(ident, ident, i);
	}

	/**
	 * LhsSelector ::= LSQUARE  Selector_Body   RSQUARE
	 *
	 * @throws SyntaxException
	 */
	Index lhsSelector() throws SyntaxException {
		checkKind(Kind.LSQUARE, "lhsSelector()\nExpected: Opening brackets\n");
		Index i = selectorBody();
		checkKind(Kind.RSQUARE, "lhsSelector()\nExpected: Closing brackets\n");
		return i;
	}

	/**
	 * Selector_Body :== XySelector
	 * Selector_Body :== RaSelector
	 *
	 * @throws SyntaxException
	 */
	Index selectorBody() throws SyntaxException {
		switch(t.kind) {
		case KW_x:  return xySelector();
		case KW_r:  return raSelector();
		default:    raiseException("selectorBody()\nExpected: Start of xy / ra" +
					" selector\nExpected Token: KW_x / KW_r\n");
		}
		return null;
	}

	/**
	 * XySelector ::= KW_x COMMA KW_y
	 *
	 * @throws SyntaxException
	 */
	Index xySelector() throws SyntaxException {
		//TODO:Changed here
		Token firstToken = t;
		Expression e1 = null, e2 = null;
		if (!next(Kind.KW_x))
			raiseException("xySelector()\nExpected: Start of xy selector\n");
		else
			e1 = new Expression_PredefinedName(t, t.kind);
		nextToken();
		
		checkKind(Kind.COMMA, "xySelector()\nExpected: Comma inside xy selector\n");
		
		if (!next(Kind.KW_y))
			raiseException("xySelector()\nExpected: End of xy selector\n");
		else
			e2 = new Expression_PredefinedName(t, t.kind);
		nextToken();
		return new Index(firstToken, e1, e2);
	}

	/**
	 * RaSelector ::= KW_r COMMA KW_A
	 *
	 * @throws SyntaxException
	 */
	Index raSelector() throws SyntaxException {
		//TODO: Changed here
		Token firstToken = t;
		Expression e1 = null, e2 = null;
		if (!next(Kind.KW_r))
			raiseException("raSelector()\nExpected: Start of ra selector\n");
		else
			e1 = new Expression_PredefinedName(t, t.kind);
		nextToken();
		
		checkKind(Kind.COMMA, "raSelector()\nExpected: Comma inside ra selector\n");
		
		//Following is a fix for Assignment 6
		if (!next(Kind.KW_a))
			raiseException("raSelector()\nExpected: End of ra selector\n");
		else
			e2 = new Expression_PredefinedName(t, t.kind);
		nextToken();
		return new Index(firstToken, e1, e2);
	}

	//-------------------------------------------------------------------------

	/**
	 * ImageInTail ::=  OP_LARROW Source
	 *
	 * @throws SyntaxException
	 */
	Source imageInTail() throws SyntaxException {
		checkKind(Kind.OP_LARROW, "imageInTail()\nExpected: Start of image in tail\n");
		return source();
	}

	/**
	 * ImageOutTail ::=  OP_RARROW Sink
	 *
	 * @throws SyntaxException
	 */
	Sink imageOutTail() throws SyntaxException {
		checkKind(Kind.OP_RARROW, "imageOutTail()\nExpected: Start of image out tail\n");
		return sink();
	}
	
	/**
	 * Sink ::= IDENTIFIER | KW_SCREEN
	 *
	 * @throws SyntaxException
	 */
	Sink sink() throws SyntaxException {
		Sink s = null;
		if (next(Kind.KW_SCREEN))
			s = new Sink_SCREEN(t);
		else if (next(Kind.IDENTIFIER))
			s = new Sink_Ident(t, t);
		else
			raiseException("sink()\nExpected: Valid sink\nExpected Token: KW_SCREEN / IDENTIFIER\n");
		nextToken();
		return s;
	}

	//-------------------------------------------------------------------------

	/**
	 * Expression ::=  OrExpression  OP_Q  Expression OP_COLON Expression    | OrExpression
	 *
	 * Our test cases may invoke this routine directly to support incremental development.
	 *
	 * @throws SyntaxException
	 */
	//TODO
	Expression expression() throws SyntaxException {
		Token firstToken = t;
		Expression base = orExpression();
		if (next(Kind.OP_Q)) {
			Expression[] e = expressionTail();
			return new Expression_Conditional(firstToken, base, e[0], e[1]);
		}
		return base;
		
	}

	/**
	 * OrExpression ::= AndExpression   (  OP_OR  AndExpression)*
	 *
	 * @throws SyntaxException
	 */
	Expression[] expressionTail() throws SyntaxException {
		checkKind(Kind.OP_Q, "expressionTail()\nExpected: Ternary operator\n");
		Expression e1 = expression();
		checkKind(Kind.OP_COLON, "expressionTail()\nExpected: Separator inside ternary conditional\n");
		Expression e2 = expression();
		return new Expression[] {e1, e2};
	}

	/**
	 * OrExpression ::= AndExpression   (  OP_OR  AndExpression)*
	 *
	 * @throws SyntaxException
	 */
	Expression orExpression() throws SyntaxException {
		Token firstToken = t;
		Expression o1 = andExpression();
		while (next(Kind.OP_OR)) {
			Token op = t;
			nextToken();
			Expression o2 = andExpression();
			Expression binary = new Expression_Binary(firstToken, o1, op, o2);
			o1 = binary;
		}
		return o1;
	}

	/**
	 * AndExpression ::= EqExpression ( OP_AND  EqExpression )*
	 *
	 * @throws SyntaxException
	 */
	Expression andExpression() throws SyntaxException {
		Token firstToken = t;
		Expression o1 = eqExpression();
		while (next(Kind.OP_AND)) {
			Token op = t;
			nextToken();
			Expression o2 = eqExpression();
			Expression binary = new Expression_Binary(firstToken, o1, op, o2);
			o1 = binary;
		}
		return o1;
	}

	/**
	 * EqExpression ::= RelExpression  (  (OP_EQ | OP_NEQ )  RelExpression )*
	 *
	 * @throws SyntaxException
	 */
	Expression eqExpression() throws SyntaxException {
		Token firstToken = t;
		Expression o1 = relExpression();
		while (next(Kind.OP_EQ) || next(Kind.OP_NEQ)) {
			Token op = t;
			nextToken();
			Expression o2 = relExpression();
			Expression binary = new Expression_Binary(firstToken, o1, op, o2);
			o1 = binary;
		}
		return o1;
	}

	/**
	 * RelExpression ::= AddExpression (  ( OP_LT  | OP_GT |  OP_LE  | OP_GE )  AddExpression)*
	 *
	 * @throws SyntaxException
	 */
	Expression relExpression() throws SyntaxException {
		Token firstToken = t;
		Expression o1 = addExpression();
		while (next(Kind.OP_GT) || next(Kind.OP_LT) || next(Kind.OP_GE) || next(Kind.OP_LE)) {
			Token op = t;
			nextToken();
			Expression o2 = addExpression();
			Expression binary = new Expression_Binary(firstToken, o1, op, o2);
			o1 = binary;
		}
		return o1;
	}

	/**
	 * AddExpression ::= MultExpression   (  (OP_PLUS | OP_MINUS ) MultExpression )*
	 *
	 * @throws SyntaxException
	 */
	Expression addExpression() throws SyntaxException {
		Token firstToken = t;
		Expression o1 = mulExpression();
		while (next(Kind.OP_PLUS) || next(Kind.OP_MINUS)) {
			Token op = t;
			nextToken();
			Expression o2 = mulExpression();
			Expression binary = new Expression_Binary(firstToken, o1, op, o2);
			o1 = binary;
		}
		return o1;
	}

	/**
	 * MultExpression := UnaryExpression ( ( OP_TIMES | OP_DIV  | OP_MOD ) UnaryExpression )*
	 *
	 * @throws SyntaxException
	 */
	Expression mulExpression() throws SyntaxException {
		Token firstToken = t;
		Expression o1 = unaryExpression();
		while (next(Kind.OP_TIMES) || next(Kind.OP_DIV) || next(Kind.OP_MOD)) {
			Token op = t;
			nextToken();
			Expression o2 = unaryExpression();
			Expression binary = new Expression_Binary(firstToken, o1, op, o2);
			o1 = binary;
		}
		return o1;
	}

	//-------------------------------------------------------------------------

	/**
	 * UnaryExpression ::= OP_PLUS UnaryExpression | OP_MINUS UnaryExpression | UnaryExpressionNotPlusMinus
	 *
	 * @throws SyntaxException
	 */
	Expression unaryExpression() throws SyntaxException {
		Token firstToken = t;
		switch(t.kind) {
			case OP_PLUS:
			case OP_MINUS:  Token op = t;
							nextToken();
							Expression ue = unaryExpression();
							return new Expression_Unary(firstToken, op, ue);
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
			case KW_DEF_Y: return unaryExpressionNotPlusOrMinus();
			default: raiseException("unaryExpression()\nExpected: Start of unary expression" +
			"\nExpected Token: OP_PLUS / OP_MINUS / OP_EXCL / INTEGER_LITERAL /" +
			" LPAREN / KW_sin / KW_cos / KW_atan / KW_abs / KW_cart_x / KW_cart_y /" +
			" KW_polar_a / KW_polar_r / IDENTIFIER / KW_x / KW_y / KW_r / KW_a / " +
			"KW_X / KW_Y / KW_Z / KW_A / KW_R / KW_DEF_X / KW_DEF_Y / BOOLEAN_LITERAL\n");
		}
		return null;
	}

	/**
	 * UnaryExpressionNotPlusMinus ::=  OP_EXCL  UnaryExpression  | Primary |
	 *	  IdentOrPixelSelectorExpression | KW_x | KW_y | KW_r | KW_a | KW_X |
	*		KW_Y | KW_Z | KW_A | KW_R | KW_DEF_X | KW_DEF_Y
	*
	* @throws SyntaxException
	*/
	Expression unaryExpressionNotPlusOrMinus() throws SyntaxException {
		Token firstToken = t;
		switch(t.kind) {
			case OP_EXCL:   Token op = t;
							nextToken();
							Expression e1 = unaryExpression();
							return new Expression_Unary(firstToken, op, e1);
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
			case BOOLEAN_LITERAL: return primary();
			case IDENTIFIER: return identOrPixelSelectorExpression();
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
			case KW_DEF_Y: Expression e2 = new Expression_PredefinedName(firstToken, t.kind);
						   nextToken();
						   return e2;
			default: raiseException("unaryExpressionNotPlusOrMinus()\nExpected: Start of" +
			" unaryExpressionNotPlusOrMinus\nExpected Token: OP_EXCL / " +
			"INTEGER_LITERAL / LPAREN / KW_sin / KW_cos / KW_atan / KW_abs / " +
			"KW_cart_x / KW_cart_y / KW_polar_a / KW_polar_r / IDENTIFIER / KW_x /" +
			" KW_y / KW_r / KW_a / KW_X / KW_Y / KW_Z / KW_A / KW_R / KW_DEF_X / " +
			"KW_DEF_Y / BOOLEAN_LITERAL\n");
		}
		return null;
	}

	/**
	 * Primary ::= INTEGER_LITERAL | LPAREN Expression RPAREN | FunctionApplication
	 *
	 * @throws SyntaxException
	 */
	Expression primary() throws SyntaxException {
		Token firstToken = t;
		Expression e = null;
		switch(t.kind) {
			case INTEGER_LITERAL:	e = new Expression_IntLit(firstToken, t.intVal()); //TODO: Verify Int Value
									nextToken();
									break;
			case BOOLEAN_LITERAL:	e = new Expression_BooleanLit(firstToken, t.getText().equals("true")); //TODO: Verify Boolean Value
									nextToken();
									break;
			case LPAREN:    nextToken();
							e = expression(); //TODO -- first token should be LPAREN or expressions  token?
							checkKind(Kind.RPAREN, "primary()\nExpected: Closing brackets\n");
							break;
			case KW_sin:
			case KW_cos:
			case KW_atan:
			case KW_abs:
			case KW_cart_x:
			case KW_cart_y:
			case KW_polar_a:
			case KW_polar_r:  e = functionApplication();
							  break;
			default: raiseException("primary()\nExpected: Start of primary\nExpected"+
			" Token: INTEGER_LITERAL / LPAREN / KW_sin / KW_cos / KW_atan / KW_abs /"+
			" KW_cart_x / KW_cart_y / KW_polar_a / KW_polar_r / BOOLEAN_LITERAL\n");
		}
		return e;
	}

	//-------------------------------------------------------------------------
	
	/**
	 * IdentOrPixelSelectorExpression::=  IDENTIFIER LSQUARE Selector RSQUARE   | IDENTIFIER
	 *
	 * @throws SyntaxException
	 */
	Expression identOrPixelSelectorExpression() throws SyntaxException {
		Token firstToken = t;
		Token ident = t;
		checkKind(Kind.IDENTIFIER, "identOrPixelSelectorExpression()\nExpected: Start of identifier expression\n");
		Index index = pixelTail();
		if (index == null)
			return new Expression_Ident(firstToken, ident);
		else
			return new Expression_PixelSelector(firstToken, ident, index);
	}

	/**
	 * Pixel_Tail ::= LSQUARE Selector RSQUARE
	 * Pixel_Tail ::= ε
	 *
	 * @throws SyntaxException
	 */
	Index pixelTail() throws SyntaxException {
		if (next(Kind.LSQUARE)) {
			nextToken();
			Index i = selector();
			checkKind(Kind.RSQUARE, "pixelTail()\nExpected: Ending brackets \n");
			return i;
		}
		return null;
	}

	/**
	 * FunctionApplication ::= FunctionName LPAREN Expression RPAREN
	 *						 | FunctionName  LSQUARE Selector RSQUARE
	*
	* @throws SyntaxException
	*/
	Expression functionApplication() throws SyntaxException {
		Token firstToken = functionName();
		return functionTail(firstToken);
	}

	/**
	 *   FunctionName ::= KW_sin | KW_cos | KW_atan | KW_abs | KW_cart_x | KW_cart_y | KW_polar_a | KW_polar_r
	 *
`	 * @throws SyntaxException
	 */
	Token functionName() throws SyntaxException {
		switch(t.kind) {
			case KW_sin:
			case KW_cos:
			case KW_atan:
			case KW_abs:
			case KW_cart_x:
			case KW_cart_y:
			case KW_polar_a:
			case KW_polar_r: Token firstToken = t;
							nextToken();
							return firstToken;
			default: raiseException("functionName()\nExpected: Start of function " +
			"call\nExpected Token: KW_sin / KW_cos / KW_atan / KW_abs / KW_cart_x /" +
			" KW_cart_y / KW_polar_a / KW_polar_r\n");
		}
		return null;
	}

	/**
	 * FunctionTail ::= LPAREN Expression RPAREN
	 * FunctionTail ::= LSQUARE Selector RSQUARE
	 *
	 * @throws SyntaxException
	 */
	Expression functionTail(Token firstToken) throws SyntaxException {
		switch(t.kind) {
			case LPAREN: nextToken();
						 Expression e = expression();
						 checkKind(Kind.RPAREN, "functionTail()\nExpected: Ending brackets\n");
						 return new Expression_FunctionAppWithExprArg(firstToken, firstToken.kind, e);
			case LSQUARE: nextToken();
						  Index i = selector();
						  checkKind(Kind.RSQUARE, "functionTail()\nExpected: Ending brackets\n");
						  return new Expression_FunctionAppWithIndexArg(firstToken, firstToken.kind, i);
			default: raiseException("functionTail()\nExpected: Starting brackets\nExpected Token: LPAREN / LSQUARE\n");
		}
		return null;
	}

	/**
	 * Selector ::=  Expression COMMA Expression
	 *
	 * @throws SyntaxException
	 */
	Index selector() throws SyntaxException {
		Token firstToken = t;
		Expression e1 = expression();
		checkKind(Kind.COMMA, "selector()\nExpected: Comma inside selector\n");
		Expression e2 = expression();
		return new Index(firstToken, e1, e2);
	}
	
	//Functions to facilitate testing
	void assignment() throws SyntaxException {
		Token ident = t;
		checkKind(Kind.IDENTIFIER, "assignment()\nExpected: Identifier for assignment\n");
		assignmentTail(ident);
	}
	
	void imageIn() throws SyntaxException {
		checkKind(Kind.IDENTIFIER, "imageIn()\nExpected: Identifier for image in\n");
		imageInTail();
	}
	
	void imageOut() throws SyntaxException {
		checkKind(Kind.IDENTIFIER, "imageOut()\nExpected: Identifier for image out\n");
		imageOutTail();
	}

}
