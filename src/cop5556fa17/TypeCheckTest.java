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
	 public void testImageDeclaration01() throws Exception {
		 String input = "prog image myVar;";
		 typeCheck(input);
	 }

	 @Test
	 public void testImageDeclaration02() throws Exception {
		 String input = "prog image [10, 10] myVar;";
		 typeCheck(input);
	 }
	 
	 @Test
	 public void testImageDeclaration03() throws Exception {
		 String input = "prog image myVar <- \"string\";";
		 typeCheck(input);
		 
	 }
	 
	 @Test
	 public void testImageDeclaration04() throws Exception {
		 String input = "prog image [10, true] myVar <- \"string\";";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void testImageDeclaration05() throws Exception {
		 String input = "prog image [false, true] myVar <- \"string\";";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 //-------------------------------------------------------------
	 //TODO: Yet to check which ones should throw error
	 
	 @Test
	 public void testSourceSinkDeclaration01() throws Exception {
		 String input = "prog url myVar = \"hello\"";
		 typeCheck(input);
	 }

	 @Test
	 public void testSourceSinkDeclaration02() throws Exception {
		 String input = "prog file myVar = \"hello\"";
		 typeCheck(input);
	 }
	 
	 @Test
	 public void testSourceSinkDeclaration03() throws Exception {
		 String input = "prog url myVar = @ (10 + 10)";
		 typeCheck(input);
	 }
	 
	 @Test
	 public void testSourceSinkDeclaration04() throws Exception {
		 String input = "prog file myVar = @ (10 + 10)";
		 typeCheck(input);
	 }
	 
	 @Test
	 public void testSourceSinkDeclaration05() throws Exception {
		 String input = "prog int var = 10; file myVar = var";
		 typeCheck(input);
	 }

	 @Test
	 public void testSourceSinkDeclaration06() throws Exception {
		 String input = "prog file var = \"homedir\"; file myVar = var";
		 typeCheck(input);
	 }
	 
	 //--------------------------------------------------------------
	 
	 @Test
	 public void testVariableDeclaration01() throws Exception {
		 String input = "prog int length = 100;";
		 typeCheck(input);
	 }
	 
	 @Test
	 public void testVariableDeclaration02() throws Exception {
		 String input = "prog int length = 0;";
		 typeCheck(input);
	 }
	 
	 @Test
	 public void testVariableDeclaration03() throws Exception {
		 String input = "prog boolean isEmpty = true;";
		 typeCheck(input);
	 }
	 
	 @Test
	 public void testVariableDeclaration04() throws Exception {
		 String input = "prog boolean isEmpty = false;";
		 typeCheck(input);
	 }
	 
	 @Test
	 public void testVariableDeclaration05() throws Exception {
		 String input = "prog int length = true;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void testVariableDeclaration06() throws Exception {
		 String input = "prog boolean isEmpty = 100;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 //---------------------------------------------------------------
	 
//
//	/**
//	 * This test should pass with a fully implemented assignment
//	 * @throws Exception
//	 */
//	 @Test
//	 public void testDec1() throws Exception {
//	 String input = "prog int k = 42;";
//	 typeCheck(input);
//	 }
//	 
//	 /**
//	  * This program does not declare k. The TypeCheckVisitor should
//	  * throw a SemanticException in a fully implemented assignment.
//	  * @throws Exception
//	  */
//	 @Test
//	 public void testUndec() throws Exception {
//	 String input = "prog k = 42;";
//	 thrown.expect(SemanticException.class);
//	 typeCheck(input);
//	 }

	 
}	
