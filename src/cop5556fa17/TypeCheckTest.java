package cop5556fa17;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.*;

import cop5556fa17.AST.ASTNode;
import cop5556fa17.AST.ASTVisitor;
import cop5556fa17.AST.Declaration_Image;
import cop5556fa17.AST.Declaration_SourceSink;
import cop5556fa17.AST.Declaration_Variable;
import cop5556fa17.AST.Expression;
import cop5556fa17.AST.Expression_FunctionAppWithExprArg;
import cop5556fa17.AST.Expression_IntLit;
import cop5556fa17.AST.Expression_PixelSelector;
import cop5556fa17.AST.Expression_PredefinedName;
import cop5556fa17.AST.Expression_Unary;
import cop5556fa17.AST.Index;
import cop5556fa17.AST.LHS;
import cop5556fa17.AST.Program;
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Statement_Out;
import cop5556fa17.AST.Statement_Assign;
import cop5556fa17.Parser.SyntaxException;
import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.LexicalException;
import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeCheckVisitor.SemanticException;

import static cop5556fa17.Scanner.Kind.*;

public class TypeCheckTest {

	// set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	// To make it easy to print objects and turn this output on and off
	static final boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}
	
	
	/**
	 * Scans, parses, and type checks given input String.
	 * 
	 * Catches, prints, and then rethrows any exceptions that occur.
	 * 
	 * @param input
	 * @throws Exception
	 */
	void typeCheck(String input) throws Exception {
		show(input);
		try {
			Scanner scanner = new Scanner(input).scan();
			ASTNode ast = new Parser(scanner).parse();
			show(ast);
			ASTVisitor v = new TypeCheckVisitor();
			ast.visit(v, null);
		} catch (Exception e) {
			show(e);
			throw e;
		}
	}

	/**
	 * Simple test case with an almost empty program.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSmallest() throws Exception {
		String input = "n"; //Smallest legal program, only has a name
		show(input); // Display the input
		Scanner scanner = new Scanner(input).scan(); // Create a Scanner and
														// initialize it
		show(scanner); // Display the Scanner
		Parser parser = new Parser(scanner); // Create a parser
		ASTNode ast = parser.parse(); // Parse the program
		TypeCheckVisitor v = new TypeCheckVisitor();
		String name = (String) ast.visit(v, null);
		show("AST for program " + name);
		show(ast);
	}

	@Test
	 public void testImageDeclaration() throws Exception {
		 String[] input = new String[] 
				 {"prog image myVar;",
				  "prog image [100, 200] myVar;",
				  "prog image [0, 0] myVar;",
				  "prog image [10, 60] myVar <- \"/dir/file.txt\";",
				  "prog image [10, 50] myVar <- \"http://www.ufl.edu\";",
				  "prog file data = \"/dir/file.txt\"; image [10, 50] myVar <- data;",
				  "prog url data = \"http://www.ufl.edu\"; image [10, 50] myVar <- data;",
				  "prog file data = \"/dir/file.txt\"; image [10, 50] myVar <- data;",
				  "prog int data = 100; image [104, 0] myVar <- @ data;",
				  "prog image [104, 0] myVar <- @ 100;"
				 };
		 
		 for (String s: input)
			 typeCheck(s);
	 }
	 
	 @Test
	 public void testImageDeclaration01() throws Exception {
		 String input = "prog image [10, true] myVar;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void testImageDeclaration02() throws Exception {
		 String input = "prog image [false, true] myVar <- \"string\";";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void testImageDeclaration03() throws Exception {
		 String input = "prog int myVar = 10; image myVar <- \"string\";";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }

	 //-------------------------------------------------------------

	 @Test
	 public void testSourceSinkDeclaration() throws Exception {
		 String[] input = new String[] 
			{
				"prog url myVar = \"http://www.google.com\";",
				"prog file myVar = \"/dir/file.txt\";",
				"prog url var = \"http://www.google.com\"; url myVar = var;",
				"prog file var = \"homedir\"; file myVar = var;"
			};
		 for (String s: input)
			 typeCheck(s);
	 }
	 
	 @Test
	 public void testSourceSinkDeclaration01() throws Exception {
		 String input = "prog file myVar = @ 100 ;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void testSourceSinkDeclaration02() throws Exception {
		 String input = "prog file myVar = @ x;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void testSourceSinkDeclaration03() throws Exception {
		 String input = "prog file myVar = var;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void testSourceSinkDeclaration04() throws Exception {
		 String input = "prog int myVar; file myVar = \"file.txt\";";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }

	 
	 //--------------------------------------------------------------
	 @Test
	 public void testVariableDeclaration() throws Exception {
		 String[] input = new String[] 
			{
				 "prog int length = 100;",
				 "prog int length = 0;",
				 "prog boolean isEmpty = true;",
				 "prog boolean isEmpty = false;",
				 "prog int len = (100 / 20) + 5;",
				 "prog boolean gt = 100 > 80;",
				 "prog boolean aa = true; boolean b = aa;",
				 "prog int len = (100 / 20) + 5; int newlen = len / 2;",
				 "prog int aa; int bb = aa;",
				 "prog boolean TRUE = true; boolean empty = TRUE;"
			};
		 for (String s: input)
			 typeCheck(s);
	 }
	 
	 @Test
	 public void testVariableDeclaration01() throws Exception {
		 String input = "prog int length = true;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void testVariableDeclaration02() throws Exception {
		 String input = "prog boolean isEmpty = 100;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void testVariableDeclaration03() throws Exception {
		 String input = "prog boolean aa = true; int bb = aa;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void testVariableDeclaration04() throws Exception {
		 String input = "prog int aa = 1; boolean bb = aa;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void testVariableDeclaration05() throws Exception {
		 String input = "prog int bb = bb;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void testVariableDeclaration06() throws Exception {
		 String input = "prog boolean bb = TRUE;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void testVariableDeclaration07() throws Exception {
		 String input = "prog int bb = 1; int bb = 10;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 //---------------------------------------------------------------
	 
	 @Test
	 public void testExpressions() throws Exception {
		 String[] input = new String[] 
			{
				 "prog int v = x;",
				 "prog int v = DEF_X;",
				 "prog image [104, 0] v <- @ 100; int vv = v [1,1];",
				 "prog int v = ((DEF_X + DEF_Y) / 10);",
				 "prog int v = 1 + 2 - 3 / 4 * 5 % 6;",
				 "prog int v = 1 + 5 % 6; int w = 10; boolean b = v == w;",
				 "prog int var = 10; boolean isEmpty = (var > 15);",
				 "prog int v = sin(90) + cos(45);",
				 "prog boolean v = sin(90) <= cos(45);",
				 "prog boolean v = !(!(!(true))) | true & false;",
				 "prog boolean v = x != y;",
				 "prog int v = 1 > 2 ? 1 : 2;",
				 "prog boolean v = 1 > 2 ? true : false;",
				 "prog boolean v=1>2; v=1<2; v=1>=2; v=1<=2;",
				 "prog int v=0; v=+2; v=-5; boolean b = true|false;",
				 "prog int v0=0; int v1=1; int v3 = v0 | v1; v3 = v0 & v1;",
				 "prog int v = sin[r,a];"
				 
			};
		 for (String s: input)
			 typeCheck(s);
	 }
	 
	 @Test
	 public void testExpr01() throws Exception {
		 String input = "prog int v = 100; int vv = v [1,1];";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void testExpr02() throws Exception {
		 String input = "prog int v = 1 > 2 ? 1 : true;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void testExpr03() throws Exception {
		 String input = "prog int v = 100 ? 1 : 2;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void testExpr04() throws Exception {
		 String input = "prog boolean v = !(!(!(true))) | true & false + 5;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	
	 @Test
	 public void testExpr05() throws Exception {
		 String input = "prog int sum = 10 + true;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void testExpr06() throws Exception {
		 String input = "prog int var = 10; boolean isEmpty = true & false | true / var;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void testExpr07() throws Exception {
		 String input = "prog int var = sin(true);";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }

	 
	 @Test
	 public void testSource01() throws Exception {
		 String input = "prog file var = @ true;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void testSource02() throws Exception {
		 String input = "prog file var = @ 100;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 
	 @Test
	 public void testStatement() throws Exception {
		 String[] input = new String[] 
			{
				 "prog int aa; aa [[x,y]] = 10;",
				 "prog int aa; aa [[r,A]] = 10;",
				 "prog boolean aa; aa [[r,A]] = 100 == 100;",
				 "prog boolean aa; boolean bb = aa | true;",
				 "prog boolean aa; boolean bb; boolean cc; boolean dd = aa | bb | cc;",
				 "prog int aa = 10; int bb = 40; bb <- @aa;",
				 "prog int bb = 40; bb <- @10;",
				 "prog url var1 = \"http://www.google.com\"; url var2 = \"http://www.google.com\"; var2 <- var1;",
				 "prog int aa; aa -> SCREEN;",
				 "prog boolean aa; aa -> SCREEN;",
				 "prog image aa; aa -> SCREEN;",
				 "prog file bb = \"file.txt\"; image aa; aa -> bb;"
			};
		 for (String s: input)
			 typeCheck(s);
	 }
	 
	 @Test
	 public void testStatement01() throws Exception {
		 String input = "prog int aa; aa [[r,a]] = 10;";
		 thrown.expect(SyntaxException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void testStatement02() throws Exception {
		 String input = "prog boolean aa; boolean bb; boolean dd = aa | bb | cc;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void testStatement03() throws Exception {
		 String input = "prog file var1 = \"google.com\"; url var2 = \"http://www.google.com\"; var2 <- var1;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void testStatement04() throws Exception {
		 String input = "prog file bb = \"file.txt\"; int aa; aa -> bb;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void testStatement05() throws Exception {
		 String input = "prog file bb = \"file.txt\"; boolean aa; aa -> bb;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void testStatement06() throws Exception { 
		 String input = "prog int aa = 0; boolean b = true; aa = b;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void testStatement07() throws Exception { 
		 String input = "prog url bb = \"http://file.com\"; image aa; aa -> bb;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void testStatement08() throws Exception { 
		 String input = "prog image aa; aa -> bb;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void testStatement09() throws Exception { 
		 String input = "prog bb <- @10;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void testStatement10() throws Exception { 
		 String input = "prog aa -> SCREEN;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	/**
	 * This test should pass with a fully implemented assignment
	 * @throws Exception
	 */
	 @Test
	 public void testDec1() throws Exception {
	 String input = "prog int k = 42;";
	 typeCheck(input);
	 }
	 
	 /**
	  * This program does not declare k. The TypeCheckVisitor should
	  * throw a SemanticException in a fully implemented assignment.
	  * @throws Exception
	  */
	 @Test
	 public void testUndec() throws Exception {
	 String input = "prog k = 42;";
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }

	 
}	
