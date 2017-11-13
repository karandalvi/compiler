package cop5556fa17;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.LexicalException;
import cop5556fa17.AST.*;

import cop5556fa17.Parser.SyntaxException;

import static cop5556fa17.Scanner.Kind.*;

public class ParserTest {

	// set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	// To make it easy to print objects and turn this output on and off
	static final boolean doPrint = false; //TODO: Toggle
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}

	/**
	 * Simple test case with an empty program. This test expects an exception
	 * because all legal programs must have at least an identifier
	 * 
	 * @throws LexicalException
	 * @throws SyntaxException
	 */
	@Test
	public void testEmpty() throws LexicalException, SyntaxException {
		String input = ""; // The input is the empty string. Parsing should fail
		show(input); // Display the input
		Scanner scanner = new Scanner(input).scan(); // Create a Scanner and
														// initialize it
		show(scanner); // Display the tokens
		Parser parser = new Parser(scanner); //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			ASTNode ast = parser.parse();; //Parse the program, which should throw an exception
		} catch (SyntaxException e) {
			show(e);  //catch the exception and show it
			throw e;  //rethrow for Junit
		}
	}


	@Test
	public void testNameOnly() throws LexicalException, SyntaxException {
		String input = "prog";  //Legal program with only a name
		show(input);            //display input
		Scanner scanner = new Scanner(input).scan();   //Create scanner and create token list
		show(scanner);    //display the tokens
		Parser parser = new Parser(scanner);   //create parser
		Program ast = parser.parse();          //parse program and get AST
		show(ast);                             //Display the AST
		assertEquals(ast.name, "prog");        //Check the name field in the Program object
		assertTrue(ast.decsAndStatements.isEmpty());   //Check the decsAndStatements list in the Program object.  It should be empty.
	}

	@Test
	public void testDec1() throws LexicalException, SyntaxException {
		String input = "prog int k;";
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Parser parser = new Parser(scanner);
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "prog"); 
		//This should have one Declaration_Variable object, which is at position 0 in the decsAndStatements list
		Declaration_Variable dec = (Declaration_Variable) ast.decsAndStatements
				.get(0);  
		assertEquals(KW_int, dec.type.kind);
		assertEquals("k", dec.name);
		assertNull(dec.e);
	}
	
	@Test
	public void testVariableDeclaration01() throws LexicalException, SyntaxException {
		String input = "int maxSize = 100";
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		Declaration dec = parser.variableDeclaration();
		Declaration_Variable decVar = (Declaration_Variable) dec;
		assertEquals(decVar.firstToken, scanner.tokens.get(0));
		assertTrue(decVar.type.equals(scanner.tokens.get(0)));
		assertEquals(decVar.name, "maxSize");
		assertEquals(decVar.e, new Expression_IntLit(scanner.tokens.get(3), 100));
		assertEquals(decVar.e.firstToken, scanner.tokens.get(3));
	}	
	
	@Test
	public void testVariableDeclaration02() throws LexicalException, SyntaxException {
		String input = "boolean maxSize = true";
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		Declaration dec = parser.variableDeclaration();
		Declaration_Variable decVar = (Declaration_Variable) dec;
		assertEquals(decVar.firstToken, scanner.tokens.get(0));
		assertTrue(decVar.type.equals(scanner.tokens.get(0)));
		assertEquals(decVar.name, "maxSize");
		assertEquals(decVar.e, new Expression_BooleanLit(scanner.tokens.get(3), true));
		assertEquals(decVar.e.firstToken, scanner.tokens.get(3));
	}
	
	@Test
	public void testVariableDeclaration03() throws LexicalException, SyntaxException {
		String input = "boolean maxSize";
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		Declaration dec = parser.variableDeclaration();
		Declaration_Variable decVar = (Declaration_Variable) dec;
		assertEquals(decVar.firstToken, scanner.tokens.get(0));
		assertTrue(decVar.type.equals(scanner.tokens.get(0)));
		assertEquals(decVar.name, "maxSize");
		assertEquals(decVar.e, null);
	}
	
	@Test
	public void testVariableDeclaration04() throws LexicalException, SyntaxException {
		String input = "int maxSize";
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		Declaration dec = parser.variableDeclaration();
		Declaration_Variable decVar = (Declaration_Variable) dec;
		assertEquals(decVar.firstToken, scanner.tokens.get(0));
		assertTrue(decVar.type.equals(scanner.tokens.get(0)));
		assertEquals(decVar.name, "maxSize");
		assertEquals(decVar.e, null);
	}

	// --------------------------
	
	@Test
	public void testImageDeclaration01() throws LexicalException, SyntaxException {
		String input = "image wallpaper";
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		Declaration decImg = parser.imageDeclaration();
		Declaration_Image dec = (Declaration_Image) decImg;
		assertEquals(dec.firstToken, scanner.tokens.get(0));
		assertEquals(dec.xSize, null);
		assertEquals(dec.ySize, null);
		assertEquals(dec.name, "wallpaper");
		assertEquals(dec.source, null);
	}
	
	@Test
	public void testImageDeclaration02() throws LexicalException, SyntaxException {
		String input = "image wallpaper <- \"string\"";
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		Declaration decImg = parser.imageDeclaration();
		Declaration_Image dec = (Declaration_Image) decImg;
		assertEquals(dec.firstToken, scanner.tokens.get(0));
		assertEquals(dec.xSize, null);
		assertEquals(dec.ySize, null);
		assertEquals(dec.name, "wallpaper");
		Source_StringLiteral ss = (Source_StringLiteral) dec.source;
		assertEquals(ss.fileOrUrl, "string");
	}
	
	@Test
	public void testImageDeclaration03() throws LexicalException, SyntaxException {
		String input = "image [10,20] florida";
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		Declaration decImg = parser.imageDeclaration();
		Declaration_Image dec = (Declaration_Image) decImg;
		assertEquals(dec.firstToken, scanner.tokens.get(0));
		assertEquals(dec.xSize, new Expression_IntLit(scanner.tokens.get(2), 10));
		assertEquals(dec.ySize, new Expression_IntLit(scanner.tokens.get(4), 20));
		assertEquals(dec.name, "florida");
		assertEquals(dec.source, null);
	}
	
	@Test
	public void testImageDeclaration04() throws LexicalException, SyntaxException {
		String input = "image [true, false] gainesville <- miami";
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		Declaration decImg = parser.imageDeclaration();
		Declaration_Image dec = (Declaration_Image) decImg;
		assertEquals(dec.firstToken, scanner.tokens.get(0));
		assertEquals(dec.xSize, new Expression_BooleanLit(scanner.tokens.get(2), true));
		assertEquals(dec.ySize, new Expression_BooleanLit(scanner.tokens.get(4), false));
		assertEquals(dec.name, "gainesville");
		Source_Ident ss = (Source_Ident) dec.source;
		assertEquals(ss.name, "miami");
	}
	
	@Test
	public void testImageDeclaration05() throws LexicalException, SyntaxException {
		String input = "image [] gainesville <- miami";
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			Declaration decImg = parser.imageDeclaration();
		}		
		catch(Exception e) {
//			e.printStackTrace();
			throw e;
		}
	}
	
	@Test
	public void testImageDeclaration06() throws LexicalException, SyntaxException {
		String input = "image gainesville <- ;";
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			Declaration decImg = parser.imageDeclaration();
		}		
		catch(Exception e) {
//			e.printStackTrace();
			throw e;
		}
	}
	
	// -----------------------------------
	
	@Test
	public void testSourceSinkDeclaration01() throws LexicalException, SyntaxException {
		String input = "url webpath = \"facebook.com\"";
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		Declaration decSourceSink = parser.sourceSinkDeclaration();
		Declaration_SourceSink dec = (Declaration_SourceSink) decSourceSink;
		assertEquals(dec.firstToken, scanner.tokens.get(0));
		assertEquals(dec.type, Kind.KW_url);
		assertEquals(dec.name, "webpath");
		Source_StringLiteral ss = (Source_StringLiteral) dec.source;
		assertEquals(ss.fileOrUrl, "facebook.com");
	}
	
	@Test
	public void testSourceSinkDeclaration02() throws LexicalException, SyntaxException {
		String input = "file webpath = \"facebook.com\"";
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		Declaration decSourceSink = parser.sourceSinkDeclaration();
		Declaration_SourceSink dec = (Declaration_SourceSink) decSourceSink;
		assertEquals(dec.firstToken, scanner.tokens.get(0));
		assertEquals(dec.type, Kind.KW_file);
		assertEquals(dec.name, "webpath");
		Source_StringLiteral ss = (Source_StringLiteral) dec.source;
		assertEquals(ss.fileOrUrl, "facebook.com");
	}
	
	// ------------------------------------
	
	@Test
	public void testSource01() throws LexicalException, SyntaxException {
		String input = "\"stringLiteral\"";
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		Source s = parser.source();
		Source t = new Source_StringLiteral(scanner.tokens.get(0), "stringLiteral");
		assertEquals(s, t);
		assertEquals(s.firstToken, scanner.tokens.get(0));
	}
	
	@Test
	public void testSource02() throws LexicalException, SyntaxException {
		String input = "iAmAnIdentifier";
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		Source s = parser.source();
		Source t = new Source_Ident(scanner.tokens.get(0), scanner.tokens.get(0));
		assertEquals(s.firstToken, scanner.tokens.get(0));
		assertEquals(s, t);
	}
	
	@Test
	public void testSource03() throws LexicalException, SyntaxException {
		String input = "@ true";
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		Source s = parser.source();
		Source_CommandLineParam ss = (Source_CommandLineParam) s;
		Source t = new Source_CommandLineParam(scanner.tokens.get(0), new Expression_BooleanLit(scanner.tokens.get(1), true));
		assertEquals(s.firstToken, scanner.tokens.get(0));
		assertEquals(ss, t);
	}
	
	//---------------------------------------
	
	@Test
	public void testSink01() throws LexicalException, SyntaxException {
		String input = "anotherIdentifier";
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		Sink s = parser.sink();
		Sink t = new Sink_Ident(scanner.tokens.get(0), scanner.tokens.get(0));
		assertEquals(s, t);
	}
	
	@Test
	public void testSink02() throws LexicalException, SyntaxException {
		String input = "SCREEN";
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		Sink s = parser.sink();
		Sink t = new Sink_SCREEN(scanner.tokens.get(0));
		assertEquals(s, t);
	}
	
	// ---------------------------------
	
	@Test
	public void testIndex01() throws LexicalException, SyntaxException {
		String input = "sin(90), abs(5)";
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		Index s = parser.selector();
		assertTrue(s.e0 instanceof Expression_FunctionAppWithExprArg); 
		assertTrue(s.e1 instanceof Expression_FunctionAppWithExprArg);
		Expression_FunctionAppWithExprArg e = (Expression_FunctionAppWithExprArg) s.e0;
		assertEquals(Kind.KW_sin, e.function);
		Expression_IntLit eI = (Expression_IntLit) e.arg;
		assertEquals(90, eI.value);
		e = (Expression_FunctionAppWithExprArg) s.e1;
		assertEquals(Kind.KW_abs, e.function);
		eI = (Expression_IntLit) e.arg;
		assertEquals(5, eI.value);
	}
	
	@Test
	public void testIndex02() throws LexicalException, SyntaxException {
		String input = "x,y";
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		Index s = parser.xySelector(); 
		assertTrue(s.e0 instanceof Expression_PredefinedName);
		assertTrue(s.e1 instanceof Expression_PredefinedName);
		Expression_PredefinedName e = (Expression_PredefinedName) s.e0;
		assertEquals(Kind.KW_x, e.kind);
		e = (Expression_PredefinedName) s.e1;
		assertEquals(Kind.KW_y, e.kind);
	}
	
	@Test
	public void testIndex03() throws LexicalException, SyntaxException {
		String input = "r,A";
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		Index s = parser.raSelector(); 
		assertTrue(s.e0 instanceof Expression_PredefinedName);
		assertTrue(s.e1 instanceof Expression_PredefinedName);
		Expression_PredefinedName e = (Expression_PredefinedName) s.e0;
		assertEquals(Kind.KW_r, e.kind);
		e = (Expression_PredefinedName) s.e1;
		assertEquals(Kind.KW_A, e.kind);
	}
	
	@Test
	public void testIndex04() throws LexicalException, SyntaxException {
		String input = "r,a";
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			Index s = parser.raSelector();
		}
		catch (Exception e) {
//			e.printStackTrace();
			throw e;
		}
	}
	
	@Test
	public void testIndex05() throws LexicalException, SyntaxException {
		String input = "x,";
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			Index s = parser.raSelector();
		}
		catch (Exception e) {
//			e.printStackTrace();
			throw e;
		}
	}
	
	// -------------------------------
	
	@Test
	public void testImgInStatement01() throws LexicalException, SyntaxException {
		String input = "myImage <- \"stringLiteral\"";
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		Statement s = parser.statement();
		Statement_In sin = (Statement_In) s;
		assertEquals("myImage", sin.name);
		Source_StringLiteral ss = (Source_StringLiteral) sin.source;
		assertEquals("stringLiteral", ss.fileOrUrl);
	}
	
	@Test
	public void testImgOutStatement01() throws LexicalException, SyntaxException {
		String input = "myImage -> SCREEN";
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		Statement s = parser.statement();
		Statement_Out sout = (Statement_Out) s;
		assertEquals("myImage", sout.name);
		Sink_SCREEN ss = (Sink_SCREEN) sout.sink;
		assertEquals(Kind.KW_SCREEN, ss.kind);
	}
	
	//---------------------------------------
	
	@Test
	public void testLHS01() throws LexicalException, SyntaxException {
		String input = "iAmIdentifier =";
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		LHS lhs = parser.lhsTail(scanner.tokens.get(0));
		assertEquals("iAmIdentifier", lhs.name);
		assertTrue(lhs.index == null);
	}
	
	@Test
	public void testLHS02() throws LexicalException, SyntaxException {
		String input = "identifier [[x,y]]";
		Scanner scanner = new Scanner(input).scan();
		scanner.nextToken();
		Parser parser = new Parser(scanner);
		LHS lhs = parser.lhsTail(scanner.tokens.get(0));
		assertEquals("identifier", lhs.name);
		Expression_PredefinedName ep = (Expression_PredefinedName) lhs.index.e0;
		assertEquals(Kind.KW_x, ep.kind);
		ep = (Expression_PredefinedName) lhs.index.e1;
		assertEquals(Kind.KW_y, ep.kind);
	}
	
	// --------------------------------------
	
	@Test
	public void testAssignmentStatement01() throws LexicalException, SyntaxException {
		String input = "ident = 400";
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		Statement s = parser.statement();
		Statement_Assign sa = (Statement_Assign) s;
		LHS lhs = sa.lhs;
		Expression_IntLit e = (Expression_IntLit) sa.e;
		assertEquals("ident", lhs.name);
		assertEquals(null, lhs.index);
		assertEquals(400, e.value);
	}
	
	@Test
	public void testAssignmentStatement02() throws LexicalException, SyntaxException {
		String input = "ident [[x,y]] = false + false + true";
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		Statement s = parser.statement();
		Statement_Assign sa = (Statement_Assign) s;
		LHS lhs = sa.lhs;
		assertEquals("ident", lhs.name);
		Expression_PredefinedName ep = (Expression_PredefinedName) lhs.index.e0;
		assertEquals(Kind.KW_x, ep.kind);
		ep = (Expression_PredefinedName) lhs.index.e1;
		assertEquals(Kind.KW_y, ep.kind);
		Expression_Binary e = (Expression_Binary) sa.e;
		Expression_Binary e0 = (Expression_Binary) e.e0;
		Expression_BooleanLit e00 = (Expression_BooleanLit) e0.e0;
		Expression_BooleanLit e01 = (Expression_BooleanLit) e0.e1;
		assertEquals(false, e00.value);
		assertEquals(false, e01.value);
		Expression_BooleanLit e1 = (Expression_BooleanLit) e.e1;
		assertEquals(true, e1.value);
		assertEquals(Kind.OP_PLUS, e.op);
	}
	
	// -----------------------------------
	@Test
	public void testExpressionInt01() throws LexicalException, SyntaxException {
		String input = "94737";
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		Expression e = parser.expression();
		Expression_IntLit i = (Expression_IntLit) e;
		assertEquals(94737, i.value);
	}
	
	@Test
	public void testExpressionInt02() throws LexicalException, SyntaxException {
		String input = "0";
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		Expression e = parser.expression();
		Expression_IntLit i = (Expression_IntLit) e;
		assertEquals(0, i.value);
	}
	
	@Test
	public void testExpressionBool01() throws LexicalException, SyntaxException {
		String input = "true";
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		Expression e = parser.expression();
		Expression_BooleanLit i = (Expression_BooleanLit) e;
		assertEquals(true, i.value);
	}
	
	@Test
	public void testExpressionBool02() throws LexicalException, SyntaxException {
		String input = "false";
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		Expression e = parser.expression();
		Expression_BooleanLit i = (Expression_BooleanLit) e;
		assertEquals(false, i.value);
	}
	
	// ----------------------------------------
	
	@Test
	public void testExpressionPredefined01() throws LexicalException, SyntaxException {
		String[] input = new String[] {"x", "y", "r", "a", "X", "Y", "R", "A", "Z", "DEF_X", "DEF_Y"};
		Kind[] types = new Kind[] {KW_x, KW_y, KW_r, KW_a, KW_X, KW_Y, KW_R, KW_A, KW_Z, KW_DEF_X, KW_DEF_Y};
		Scanner scanner;
		Parser parser;
		Expression_PredefinedName e;
		for (int i=0; i<input.length; i++) {
			scanner = new Scanner(input[i]).scan();
			parser = new Parser(scanner);
			e = (Expression_PredefinedName) parser.expression();
			assertEquals(types[i], e.kind);
		}
	}
	
	@Test
	public void testExpressionIdent01() throws LexicalException, SyntaxException {
		String[] input = new String[] {"hello" , "$var", "$_123", "_hello", "$$$$$", "_____"};
		Scanner scanner;
		Parser parser;
		Expression_Ident e;
		for (int i=0; i<input.length; i++) {
			scanner = new Scanner(input[i]).scan();
			parser = new Parser(scanner);
			e = (Expression_Ident) parser.expression();
			assertEquals(input[i], e.name);
		}
	}
	
	@Test
	public void testExpressionIdentPixel01() throws LexicalException, SyntaxException {
		String[] input = new String[] {"hello [1,true]" , "$var [1, true]", "$_123 [1, true]", "_hello [1, true]", "$$$$$ [1, true]"};
		Scanner scanner;
		Parser parser;
		Expression_PixelSelector e;
		for (int i=0; i<input.length; i++) {
			scanner = new Scanner(input[i]).scan();
			parser = new Parser(scanner);
			e = (Expression_PixelSelector) parser.expression();
			assertEquals(input[i].split(" ")[0], e.name);
			assertTrue(e.index!=null);
			assertTrue(e.index.e0 instanceof Expression_IntLit);
			assertEquals(1, ((Expression_IntLit)e.index.e0).value);
			assertTrue(e.index.e1 instanceof Expression_BooleanLit);
			assertEquals(true, ((Expression_BooleanLit)e.index.e1).value);
		}
	}
	
	@Test
	public void testExpressionFunctionWithExprArg01() throws LexicalException, SyntaxException {
		String[] input = new String[] {"sin (90)" ,"cos(90)", "atan(90)", "abs(90)", "cart_x(90)", "cart_y(90)", "polar_a(90)", "polar_r(90)"};
		Kind[] function = new Kind[] {KW_sin, KW_cos, KW_atan, KW_abs, KW_cart_x, KW_cart_y, KW_polar_a, KW_polar_r};
		Scanner scanner;
		Parser parser;
		Expression_FunctionAppWithExprArg e;
		for (int i=0; i<input.length; i++) {
			scanner = new Scanner(input[i]).scan();
			parser = new Parser(scanner);
			e = (Expression_FunctionAppWithExprArg) parser.expression();
			assertEquals(function[i], e.function);
			assertEquals(90, ((Expression_IntLit) e.arg).value);
		}
	}

	@Test
	public void testExpressionFunctionWithIndexArg01() throws LexicalException, SyntaxException {
		String[] input = new String[] {"sin [1,4]" ,"cos [1,4]", "atan [1,4]", "abs [1,4]", "cart_x [1,4]", "cart_y [1,4]", "polar_a[1,4]", "polar_r[1,4]"};
		Kind[] function = new Kind[] {KW_sin, KW_cos, KW_atan, KW_abs, KW_cart_x, KW_cart_y, KW_polar_a, KW_polar_r};
		Scanner scanner;
		Parser parser;
		Expression_FunctionAppWithIndexArg e;
		for (int i=0; i<input.length; i++) {
			scanner = new Scanner(input[i]).scan();
			parser = new Parser(scanner);
			e = (Expression_FunctionAppWithIndexArg) parser.expression();
			assertEquals(function[i], e.function);
			assertEquals(1, ((Expression_IntLit) e.arg.e0).value);
			assertEquals(4, ((Expression_IntLit) e.arg.e1).value);
		}
	}
	
	@Test
	public void testExpression01() throws LexicalException, SyntaxException {
		String[] input = new String[] {"1+2", "1-2", "1*2", "1%2", "1/2", "1<2", "1>2", "1<=2", "1>=2", "1==2", "1!=2", "1|2", "1&2"};
		Kind[] types = new Kind[] {Kind.OP_PLUS, Kind.OP_MINUS, Kind.OP_TIMES, Kind.OP_MOD, Kind.OP_DIV, Kind.OP_LT, 
				Kind.OP_GT, Kind.OP_LE, Kind.OP_GE, Kind.OP_EQ, Kind.OP_NEQ, Kind.OP_OR, Kind.OP_AND};
		Scanner scanner;
		Parser parser;
		Expression_Binary e;
		for (int i=0; i<input.length; i++) {
			scanner = new Scanner(input[i]).scan();
			parser = new Parser(scanner);
			e = (Expression_Binary) parser.expression();
			assertEquals(1, ((Expression_IntLit) e.e0).value);
			assertEquals(types[i], e.op);
			assertEquals(2, ((Expression_IntLit) e.e1).value);
		}
	}
		
	@Test
	public void testUnaryExpression01() throws LexicalException, SyntaxException {
		String[] input = new String[] {"+2", "!2", "-2"};
		Kind[] types = new Kind[] {Kind.OP_PLUS, Kind.OP_EXCL, Kind.OP_MINUS};
		Scanner scanner;
		Parser parser;
		Expression_Unary e;
		for (int i=0; i<input.length; i++) {
			scanner = new Scanner(input[i]).scan();
			parser = new Parser(scanner);
			e = (Expression_Unary) parser.expression();
			assertEquals(types[i], e.op);
			assertEquals(2, ((Expression_IntLit) e.e).value);
		}
	}
	
	@Test
	public void testProgram() throws LexicalException, SyntaxException {
		String input = "programName var1 = 200; var2 = 300; int k = 100;";
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		Program e = parser.parse();
		assertEquals("programName", e.name);
		assertEquals(3, e.decsAndStatements.size());
		assertEquals("var1", ((Statement_Assign)e.decsAndStatements.get(0)).lhs.name);
		assertEquals("var2", ((Statement_Assign)e.decsAndStatements.get(1)).lhs.name);
		assertEquals("k", ((Declaration_Variable)e.decsAndStatements.get(2)).name);
	}
}