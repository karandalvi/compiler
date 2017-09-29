package cop5556fa17;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556fa17.Scanner.LexicalException;
import cop5556fa17.Scanner.Token;
import cop5556fa17.SimpleParser.SyntaxException;

import static cop5556fa17.Scanner.Kind.*;

public class SimpleParserTest {

	//set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	
	//To make it easy to print objects and turn this output on and off
	static final boolean doPrint = false;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}

	/**
	 * Simple test case with an empty program.  This test 
	 * expects an SyntaxException because all legal programs must
	 * have at least an identifier
	 *   
	 * @throws LexicalException
	 * @throws SyntaxException 
	 */
	@Test
	public void testEmpty() throws LexicalException, SyntaxException {
		String input = "";  //The input is the empty string.  This is not legal
		show(input);        //Display the input 
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		SimpleParser parser = new SimpleParser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
		parser.parse();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}

	@Test
	public void program01() throws LexicalException, SyntaxException {
		String input = "myProgam";  //The input is the empty string.  This is not legal
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		SimpleParser parser = new SimpleParser(scanner);  //Create a parser
		try {
			parser.parse();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void program02() throws LexicalException, SyntaxException {
		String input = "$1__myProg__;";  //The input is the empty string.  This is not legal
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		SimpleParser parser = new SimpleParser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.parse();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void program03() throws LexicalException, SyntaxException {
		String input = "; $1__myProg__";  //The input is the empty string.  This is not legal
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		SimpleParser parser = new SimpleParser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.parse();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void variableDeclaration01() throws LexicalException, SyntaxException {
		//TODO Insert int ident = (valid expressions)
		String[] valid = new String[] {"int ident1", "boolean b1", "int indent1 = 100", "int ident1 = 2 + 2"};
		for (String input : valid) {
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			SimpleParser parser = new SimpleParser(scanner);  //Create a parser
			try {
				parser.variableDeclaration();  //Parse the program
			}
			catch (SyntaxException e) {
				show(e);
				throw e;
			}
		}
	}
	
	@Test
	public void variableDeclaration02() throws LexicalException, SyntaxException {
		String input = "int int";  //The input is the empty string.  This is not legal
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		SimpleParser parser = new SimpleParser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.variableDeclaration();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void variableDeclaration03() throws LexicalException, SyntaxException {
		String input = "boolean x";  //The input is the empty string.  This is not legal
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		SimpleParser parser = new SimpleParser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.variableDeclaration();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void variableDeclaration04() throws LexicalException, SyntaxException {
		String input = "int ident1=;";  //The input is the empty string.  This is not legal
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		SimpleParser parser = new SimpleParser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.variableDeclaration();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void imageDeclaration01() throws LexicalException, SyntaxException {
		String input = "image [1,5] ident1 <- \"stringliteral\"";  //The input is the empty string.  This is not legal
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		SimpleParser parser = new SimpleParser(scanner);  //Create a parser
		try {
			parser.imageDeclaration();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}

	@Test
	public void imageDeclaration02() throws LexicalException, SyntaxException {
		String input = "image [1,5] ident1 <- @100";  //The input is the empty string.  This is not legal
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		SimpleParser parser = new SimpleParser(scanner);  //Create a parser
		try {
			parser.imageDeclaration();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void imageDeclaration03() throws LexicalException, SyntaxException {
		String input = "image [1,5] ident1 <- ident2";  //The input is the empty string.  This is not legal
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		SimpleParser parser = new SimpleParser(scanner);  //Create a parser
		try {
			parser.imageDeclaration();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void imageDeclaration04() throws LexicalException, SyntaxException {
		String input = "image [1,5] ident1";  //The input is the empty string.  This is not legal
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		SimpleParser parser = new SimpleParser(scanner);  //Create a parser
		try {
			parser.imageDeclaration();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void imageDeclaration05() throws LexicalException, SyntaxException {
		String input = "image ident1 <- \"stringliteral\"";  //The input is the empty string.  This is not legal
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		SimpleParser parser = new SimpleParser(scanner);  //Create a parser
		try {
			parser.imageDeclaration();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void imageDeclaration06() throws LexicalException, SyntaxException {
		String input = "image ident1 <- @100";  //The input is the empty string.  This is not legal
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		SimpleParser parser = new SimpleParser(scanner);  //Create a parser
		try {
			parser.imageDeclaration();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}

	@Test
	public void imageDeclaration07() throws LexicalException, SyntaxException {
		String input = "image ident1 <- ident2";  //The input is the empty string.  This is not legal
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		SimpleParser parser = new SimpleParser(scanner);  //Create a parser
		try {
			parser.imageDeclaration();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void imageDeclaration08() throws LexicalException, SyntaxException {
		String input = "image ident1";  //The input is the empty string.  This is not legal
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		SimpleParser parser = new SimpleParser(scanner);  //Create a parser
		try {
			parser.imageDeclaration();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void imageDeclaration09() throws LexicalException, SyntaxException {
		String input = "image [1] ident1";  //The input is the empty string.  This is not legal
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		SimpleParser parser = new SimpleParser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.imageDeclaration();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void imageDeclaration10() throws LexicalException, SyntaxException {
		String input = "image 1,2 ident1";  //The input is the empty string.  This is not legal
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		SimpleParser parser = new SimpleParser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.imageDeclaration();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void imageDeclaration11() throws LexicalException, SyntaxException {
		String input = "image ident1 <-";  //The input is the empty string.  This is not legal
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		SimpleParser parser = new SimpleParser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.imageDeclaration();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	
	@Test
	public void imageDeclaration12() throws LexicalException, SyntaxException {
		String input = "image ident1 <- x";  //The input is the empty string.  This is not legal
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		SimpleParser parser = new SimpleParser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.imageDeclaration();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void sourceSinkDeclaration01() throws LexicalException, SyntaxException {
		String[] valid = new String[] {"url ident1 = ident2", "file ident1 = ident2", 
		"url indent1 = @ 100", "url ident1 = \"hello\""};
		for (String input : valid) {
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			SimpleParser parser = new SimpleParser(scanner);  //Create a parser
			try {
				parser.sourceSinkDeclaration();  //Parse the program
			}
			catch (SyntaxException e) {
				show(e);
				throw e;
			}
		}
	}
	
	@Test
	public void sourceSinkDeclaration02() throws LexicalException, SyntaxException {
		String input = "keyword url";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		SimpleParser parser = new SimpleParser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.sourceSinkDeclaration();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}

	@Test
	public void sourceSinkDeclaration03() throws LexicalException, SyntaxException {
		String input = "url ident = ;";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		SimpleParser parser = new SimpleParser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.sourceSinkDeclaration();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void sourceSinkDeclaration04() throws LexicalException, SyntaxException {
		String input = "file helloWorld";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		SimpleParser parser = new SimpleParser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.sourceSinkDeclaration();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void sourceSinkDeclaration05() throws LexicalException, SyntaxException {
		String input = "file ";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		SimpleParser parser = new SimpleParser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.sourceSinkDeclaration();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void expression01() throws LexicalException, SyntaxException {
		String[] valid = new String[] {
				"25", "(0)", "sin(90)", "cos(45)", "atan(0)", "abs(45)",
				"cart_x(100)", "cart_y(100)", "polar_a(10)", "polar_r(10)",
				"(sin(90))", "(cos(45))", "(atan(0))", "(abs(45))",
				"(cart_x(100))", "(cart_y(100))", "(polar_a(10))", "(polar_r(10))",
				"sin[10, 20]", "cos[sin(90), 20]", "atan[(10), (abs(45))]",
				"ident [10,20]", "ident [sin(90), cos(90)]", "ident", "x", "y", "r", "a",
				"X", "Y", "Z", "A", "R", "DEF_X", "DEF_Y",	
				"!25", "!(0)", "!sin(90)", "!cos(45)", "!atan(0)", "!abs(45)",
				"!cart_x(100)", "!cart_y(100)", "!polar_a(10)", "!polar_r(10)",
				"!(sin(90))", "!(cos(45))", "!(atan(0))", "!(abs(45))",
				"!(cart_x(100))", "!(cart_y(100))", "!(polar_a(10))", "!(polar_r(10))",
				"!sin[10, 20]", "!cos[sin(90), 20]", "!atan[(10), (abs(45))]",
				"!ident [10,20]", "!ident [sin(90), cos(90)]", "!ident", "!x", "!y", "!r", "!a",
				"!X", "!Y", "!Z", "!A", "!R", "!DEF_X", "!DEF_Y",
				"+25", "+(0)", "+sin(90)", "+cos(45)", "+atan(0)", "+abs(45)",
				"+cart_x(100)", "+cart_y(100)", "+polar_a(10)", "+polar_r(10)",
				"+(sin(90))", "+(cos(45))", "+(atan(0))", "+(abs(45))",
				"+(cart_x(100))", "+(cart_y(100))", "+(polar_a(10))", "+(polar_r(10))",
				"+sin[10, 20]", "+cos[sin(90), 20]", "+atan[(10), (abs(45))]",
				"+ident [10,20]", "+ident [sin(90), cos(90)]", "+ident", "+x", "+y", "+r", "+a",
				"+X", "+Y", "+Z", "+A", "+R", "+DEF_X", "+DEF_Y",
				"-25", "-(0)", "-sin(90)", "-cos(45)", "-atan(0)", "-abs(45)",
				"-cart_x(100)", "-cart_y(100)", "-polar_a(10)", "-polar_r(10)",
				"-(sin(90))", "-(cos(45))", "-(atan(0))", "-(abs(45))",
				"-(cart_x(100))", "-(cart_y(100))", "-(polar_a(10))", "-(polar_r(10))",
				"-sin[10, 20]", "-cos[sin(90), 20]", "-atan[(10), (abs(45))]",
				"-ident [10,20]", "-ident [sin(90), cos(90)]", "-ident", "-x", "-y", "-r", "-a",
				"-X", "-Y", "-Z", "-A", "-R", "-DEF_X", "-DEF_Y",
				"+++---+25", "+++---+(0)", "+++---+sin(90)", "+++---+cos(45)", "+++---+atan(0)", "+++---+abs(45)",
				"+++---+cart_x(100)", "+++---+cart_y(100)", "+++---+polar_a(10)", "+++---+polar_r(10)",
				"+++---+(sin(90))", "+++---+(cos(45))", "+++---+(atan(0))", "+++---+(abs(45))",
				"+++---+(cart_x(100))", "+++---+(cart_y(100))", "+++---+(polar_a(10))", "+++---+(polar_r(10))",
				"+++---+sin[10, 20]", "+++---+cos[sin(90), 20]", "+++---+atan[(10), (abs(45))]",
				"+++---+ident [10,20]", "+++---+ident [sin(90), cos(90)]", "+++---+ident", "+++---+x", "+++---+y", "+++---+r", "+++---+a",
				"+++---+X", "+++---+Y", "+++---+Z", "+++---+A", "+++---+R", "+++---+DEF_X", "+++---+DEF_Y",
				"sin(90)*++25", "sin(90)*++(0)", "sin(90)*++sin(90)", "--sin(90)*++cos(45)", "--sin(90)*++atan(0)", "--sin(90)*++abs(45)",
				"10*++cart_x(100)", "10*++cart_y(100)", "10*++polar_a(10)", "10*++polar_r(10)",
				"(50)*(sin(90))", "+++---+(cos(45))", "+++---+(atan(0))", "+++---+(abs(45))",
				"+++---+(cart_x(100))", "-x*(cart_y(100))", "+++---+(polar_a(10))", "+++---+(polar_r(10))",
				"+++---+sin[10, 20]", "+++---+cos[sin(90), 20]", "+++---+atan[(10), (abs(45))]",
				"+++---+ident [10,20]", "+++---+ident [sin(90), cos(90)]", "ident [sin(90), cos(90)]%ident", "ident [sin(90), cos(90)]%x", "DEF_Y%y", "DEF_Y%r", "DEF_Y%a",
				"identif%X", "--identif%Y", "--identif%Z", "((((ident))))%A", "((((+ident))))%R", "-((((+ident))))%DEF_X", "-((((+10))))%DEF_Y",
				"++sin(90)++sin(90)","+x-+y", "+x-+y > sin(10)", "true", "false", "+true", "-false", "true == false",
				"100 != 90", "5 * 2 + 4 / 4 % 3 > 40", "++-+-+-+-sin(cos(atan(abs(++true)))) & abs[true>x, y!=false]",
				"true != false"};
		for (String input : valid) {
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			SimpleParser parser = new SimpleParser(scanner);  //Create a parser
			try {
				parser.expression();  
				parser.matchEOFs();
			}
			catch (SyntaxException e) {
				System.out.println("Error parsing " + input);
				show(e);
				throw e;
			}
		}
	}
	
	@Test
	public void selector01() throws LexicalException, SyntaxException {
		String[] valid = new String[] {"x,y", "r,a", "true, false", "-4,100",
				"sin (true),cos(2+2)", "atan ((300)),abs(sin(true))",
				"sin [(true),20] ,cos[(2+2), 2+2]", "atan[((300)), (1)], abs[(sin(true)), x]",
				"cart_x[(true), ((A))] , cart_y[(100), polar_a(true)]", "polar_a[(true&true), -0],polar_r[(((2)+2)+2),---0]"
				};
		for (String input : valid) {
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			SimpleParser parser = new SimpleParser(scanner);  //Create a parser
			try {
				parser.selector(); 
				parser.matchEOFs();
			}
			catch (SyntaxException e) {
				System.out.println("Error parsing " + input);
				show(e);
				throw e;
			}
		}
	}
	
	@Test
	public void xySelector01() throws LexicalException, SyntaxException {
		String[] valid = new String[] {"x,y"};
		for (String input : valid) {
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			SimpleParser parser = new SimpleParser(scanner);  //Create a parser
			try {
				parser.xySelector();  
			}
			catch (SyntaxException e) {
				System.out.println("Error parsing " + input);
				show(e);
				throw e;
			}
		}
	}
	
	@Test
	public void primary01() throws LexicalException, SyntaxException {
		String[] valid = new String[] {"true", "false", "100", "0", "(true)","(100)","((x>y))",
				"sin (true)", "cos(2+2)", "atan ((300))", "abs(sin(true))",
				"cart_x(true)", "cart_y(100)", "polar_a(true&true)", "polar_r(((2)+2)+2)",
				"sin [(true),20]", "cos[(2+2), 2+2]", "atan[((300)), (1)]", "abs[(sin(true)), x]",
				"cart_x[(true), ((A))]", "cart_y[(100), polar_a(true)]", "polar_a[(true&true), -0]", "polar_r[(((2)+2)+2),---0]"};
		for (String input : valid) {
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			SimpleParser parser = new SimpleParser(scanner);  //Create a parser
			try {
				parser.primary();
				parser.matchEOFs();
			}
			catch (SyntaxException e) {
				System.out.println("Error parsing " + input);
				show(e);
				throw e;
			}
		}
	}
	
	@Test
	public void identOrPixelSelector01() throws LexicalException, SyntaxException {
		String[] valid = new String[] {"identifier", "identifier [true, false]", 
				"abc[sin (true),cos(2+2)]", "abc[atan ((300)),abs(sin(true))]",
				"abc[sin [(true),20] ,cos[(2+2), 2+2]]", "abc[atan[((300)), (1)], abs[(sin(true)), x]]",
				"abc[cart_x[(true), ((A))] , cart_y[(100), polar_a(true)]]", "abc[polar_a[(true&true), -0],polar_r[(((2)+2)+2),---0]]"};
		for (String input : valid) {
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			SimpleParser parser = new SimpleParser(scanner);  //Create a parser
			try {
				parser.identOrPixelSelectorExpression();
				parser.matchEOFs();
			}
			catch (SyntaxException e) {
				System.out.println("Error parsing " + input);
				show(e);
				throw e;
			}
		}
	}
	
	@Test
	public void function01() throws LexicalException, SyntaxException {
		String[] valid = new String[] {"sin (true)", "cos(2+2)", "atan ((300))", "abs(sin(true))",
										"cart_x(true)", "cart_y(100)", "polar_a(true&true)", "polar_r(((2)+2)+2)",
										"sin [(true),20]", "cos[(2+2), 2+2]", "atan[((300)), (1)]", "abs[(sin(true)), x]",
										"cart_x[(true), ((A))]", "cart_y[(100), polar_a(true)]", "polar_a[(true&true), -0]", "polar_r[(((2)+2)+2),---0]"};
		
		for (String input : valid) {
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			SimpleParser parser = new SimpleParser(scanner);  //Create a parser
			try {
				parser.functionApplication();
				parser.matchEOFs();
			}
			catch (SyntaxException e) {
				System.out.println("Error parsing " + input);
				show(e);
				throw e;
			}
		}
	}
	
	@Test
	public void raSelector01() throws LexicalException, SyntaxException {
		String[] valid = new String[] {"r,A"};
		for (String input : valid) {
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			SimpleParser parser = new SimpleParser(scanner);  //Create a parser
			try {
				parser.raSelector();  
			}
			catch (SyntaxException e) {
				System.out.println("Error parsing " + input);
				show(e);
				throw e;
			}
		}
	}
	//---------------------------------------------------------------------
	
	/** Another example.  This is a legal program and should pass when 
	 * your parser is implemented.
	 * 
	 * @throws LexicalException
	 * @throws SyntaxException
	 */

	@Test
	public void testDec1() throws LexicalException, SyntaxException {
		String input = "prog int k;";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		SimpleParser parser = new SimpleParser(scanner);  //
		parser.parse();
	}
	

	/**
	 * This example invokes the method for expression directly. 
	 * Effectively, we are viewing Expression as the start
	 * symbol of a sub-language.
	 *  
	 * Although a compiler will always call the parse() method,
	 * invoking others is useful to support incremental development.  
	 * We will only invoke expression directly, but 
	 * following this example with others is recommended.  
	 * 
	 * @throws SyntaxException
	 * @throws LexicalException
	 */
	@Test
	public void expression1() throws SyntaxException, LexicalException {
		String input = "2";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		SimpleParser parser = new SimpleParser(scanner);  
		parser.expression();  //Call expression directly.  
	}


	
	}

