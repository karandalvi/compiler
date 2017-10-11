package cop5556fa17;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556fa17.Scanner.LexicalException;
import cop5556fa17.Scanner.Token;
import cop5556fa17.Parser.SyntaxException;

import static cop5556fa17.Scanner.Kind.*;

public class ParserTest {

	//set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	
	//To make it easy to print objects and turn this output on and off
	static final boolean doPrint = true;
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
		Parser parser = new Parser(scanner);  //Create a parser
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
		Parser parser = new Parser(scanner);  //Create a parser
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
		Parser parser = new Parser(scanner);  //Create a parser
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
		Parser parser = new Parser(scanner);  //Create a parser
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
			Parser parser = new Parser(scanner);  //Create a parser
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
		Parser parser = new Parser(scanner);  //Create a parser
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
		Parser parser = new Parser(scanner);  //Create a parser
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
		Parser parser = new Parser(scanner);  //Create a parser
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
		Parser parser = new Parser(scanner);  //Create a parser
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
		Parser parser = new Parser(scanner);  //Create a parser
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
		Parser parser = new Parser(scanner);  //Create a parser
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
		Parser parser = new Parser(scanner);  //Create a parser
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
		Parser parser = new Parser(scanner);  //Create a parser
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
		Parser parser = new Parser(scanner);  //Create a parser
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
		Parser parser = new Parser(scanner);  //Create a parser
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
		Parser parser = new Parser(scanner);  //Create a parser
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
		Parser parser = new Parser(scanner);  //Create a parser
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
		Parser parser = new Parser(scanner);  //Create a parser
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
		Parser parser = new Parser(scanner);  //Create a parser
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
		Parser parser = new Parser(scanner);  //Create a parser
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
			Parser parser = new Parser(scanner);  //Create a parser
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
		Parser parser = new Parser(scanner);  //Create a parser
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
		Parser parser = new Parser(scanner);  //Create a parser
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
		Parser parser = new Parser(scanner);  //Create a parser
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
		Parser parser = new Parser(scanner);  //Create a parser
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
			Parser parser = new Parser(scanner);  //Create a parser
			try {
				parser.expression();  
				parser.matchEOF_();
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
			Parser parser = new Parser(scanner);  //Create a parser
			try {
				parser.selector(); 
				parser.matchEOF_();
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
			Parser parser = new Parser(scanner);  //Create a parser
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
			Parser parser = new Parser(scanner);  //Create a parser
			try {
				parser.primary();
				parser.matchEOF_();
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
			Parser parser = new Parser(scanner);  //Create a parser
			try {
				parser.identOrPixelSelectorExpression();
				parser.matchEOF_();
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
			Parser parser = new Parser(scanner);  //Create a parser
			try {
				parser.functionApplication();
				parser.matchEOF_();
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
			Parser parser = new Parser(scanner);  //Create a parser
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
		Parser parser = new Parser(scanner);  //
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
		Parser parser = new Parser(scanner);  
		parser.expression();  //Call expression directly.  
	}

	//-------------------------------------------------------------------------------
	
	//Valid Cases
	@Test
	public void validImage() throws SyntaxException, LexicalException {
		String[] valid = new String[] {
				"image ident",
				"image [0,0] ident",
				"image [sin(90),1+cos(90)] ident",
				"image ident <- \"source\"",
				"image ident <- @ sin(100/10/1)",
				"image ident1 <- ident2",
				"image [sin(90),1+cos(90)] ident <- ident2",
				"image [sin(90),1+cos(90)] ident <- \"source\"",
				"image [sin(90),1+cos(90)] ident <- @ sin(100/10/1)",
				"image [sin(90),1+cos(90)] ident <- @+true",
				"image [(20)*(10),1|2&3==4!=5<6<=7>8>=9+10-11*12/13%14+++15---(16)] ident <- @+true"
		};
		for (String input : valid) {
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			Parser parser = new Parser(scanner);  //Create a parser
			try {
				parser.imageDeclaration();
				parser.matchEOF_();
			}
			catch (SyntaxException e) {
				System.out.println("Error parsing " + input);
				show(e);
				throw e;
			}
		} 
	}

	@Test
	public void validVariable() throws SyntaxException, LexicalException {
		String[] valid = new String[] {
				"int ident = +-x? 23:sin(A)",
				"boolean ident = true",
				"int ident",
				"boolean b1",
				"int ident1 = 100",
				"boolean b1 = true",
				"int ident = +++1 * ---2",
				"boolean Mumbai = - + !(Y) ? true : polar_a [x,r]"
		};
		for (String input : valid) {
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			Parser parser = new Parser(scanner);  //Create a parser
			try {
				parser.variableDeclaration();
				parser.matchEOF_();
			}
			catch (SyntaxException e) {
				System.out.println("Error parsing " + input);
				show(e);
				throw e;
			}
		} 
	}
	
	@Test
	public void validSourceSink() throws SyntaxException, LexicalException {
		String[] valid = new String[] {
				"url ident = ident2",
				"file ident = ident2",
				"url ident = \"filepath\"",
				"file ident = \"filepath\"",
				"url ident = @ 10",
				"file ident = @ 10",
				"url ident = @true",
				"file ident = @false",
				"file ident = @true|2&0==false!=4<5>sin(100)<=7>=8+9-10*+11/x%13",
		};
		for (String input : valid) {
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			Parser parser = new Parser(scanner);  //Create a parser
			try {
				parser.sourceSinkDeclaration();
				parser.matchEOF_();
			}
			catch (SyntaxException e) {
				System.out.println("Error parsing " + input);
				show(e);
				throw e;
			}
		} 
	}
	
	@Test
	public void validAssignment() throws SyntaxException, LexicalException {
		String[] valid = new String[] {
				"ident = 100",
				"ident [[x,y]] = 100",
				"ident [[r,A]] = 100",
				"ident = true",
				"ident [[x,y]] = true",
				"ident [[r,A]] = true",
				"ident = false",
				"ident [[x,y]] = false",
				"ident [[r,A]] = false",
				"ident = x",
				"ident = +-10|-+10",
				"ident =true|2&0==false!=4<5>sin(100)<=7>=8+9-10*+11/x%13"
		};
		for (String input : valid) {
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			Parser parser = new Parser(scanner);  //Create a parser
			try {
				parser.assignment();
				parser.matchEOF_();
			}
			catch (SyntaxException e) {
				System.out.println("Error parsing " + input);
				show(e);
				throw e;
			}
		} 
	}

	@Test
	public void validImageOut() throws SyntaxException, LexicalException {
		String[] valid = new String[] {
			"glades221->bachelors",
			"glades221->screen"
		};
		for (String input : valid) {
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			Parser parser = new Parser(scanner);  //Create a parser
			try {
				parser.imageOut();
				parser.matchEOF_();
			}
			catch (SyntaxException e) {
				System.out.println("Error parsing " + input);
				show(e);
				throw e;
			}
		} 
	}
	
	@Test
	public void validImageIn() throws SyntaxException, LexicalException {
		String[] valid = new String[] {
			"glades221<-\"hello\"",
			"glades221<-@Z",
			"glades221<-UF"
		};
		for (String input : valid) {
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			Parser parser = new Parser(scanner);  //Create a parser
			try {
				parser.imageIn();
				parser.matchEOF_();
			}
			catch (SyntaxException e) {
				System.out.println("Error parsing " + input);
				show(e);
				throw e;
			}
		} 
	}
	@Test
	public void validExpression() throws SyntaxException, LexicalException {
		String[] valid = new String[] {
				"x",
				"y",
				"r",
				"a",
				"X",
				"Y",
				"Z",
				"A",
				"R",
				"DEF_X",
				"DEF_Y",
				"+++x",
				"---x",
				"!!!x",
				"!!+x",
				"!!-x",
				"!+!x",
				"10",
				"(x)",
				"(10)",
				"(+++X)",
				"(---10)",
				"(!!Z)",
				"(!+!y)",
				"(((10)))",
				"++(10)",
				"(!(10))",
				"sin(10)",
				"sin[10,10]",
				"sin((10))",
				"sin[(1),(1)]",
				"sin(x)",
				"sin[x,y]",
				"sin(++x)",
				"sin[++DEF_X, --DEF_Y]",
				"sin(--x)",
				"sin[--a,--r]",
				"sin(!!x)",
				"sin[!!x, !!y]",
				"sin(!+!x)",
				"sin[!+!x, !+!y]",
				"cos(10)",
				"cos[10,10]",
				"cos((10))",
				"cos[(1),(1)]",
				"cos(x)",
				"cos[x,y]",
				"cos(++x)",
				"cos[++DEF_X, --DEF_Y]",
				"cos(--x)",
				"cos[--a,--r]",
				"cos(!!x)",
				"cos[!!x, !!y]",
				"cos(!+!x)",
				"cos[!+!x, !+!y]",
				"atan(10)",
				"atan[10,10]",
				"atan((10))",
				"atan[(1),(1)]",
				"atan(x)",
				"atan[x,y]",
				"atan(++x)",
				"atan[++DEF_X, --DEF_Y]",
				"atan(--x)",
				"atan[--a,--r]",
				"atan(!!x)",
				"atan[!!x, !!y]",
				"atan(!+!x)",
				"atan[!+!x, !+!y]",
				"abs(10)",
				"abs[10,10]",
				"abs((10))",
				"abs[(1),(1)]",
				"abs(x)",
				"abs[x,y]",
				"abs(++x)",
				"abs[++DEF_X, --DEF_Y]",
				"abs(--x)",
				"abs[--a,--r]",
				"abs(!!x)",
				"abs[!!x, !!y]",
				"abs(!+!x)",
				"abs[!+!x, !+!y]",
				"cart_x(10)",
				"cart_x[10,10]",
				"cart_x((10))",
				"cart_x[(1),(1)]",
				"cart_x(x)",
				"cart_x[x,y]",
				"cart_x(++x)",
				"cart_x[++DEF_X, --DEF_Y]",
				"cart_x(--x)",
				"cart_x[--a,--r]",
				"cart_x(!!x)",
				"cart_x[!!x, !!y]",
				"cart_x(!+!x)",
				"cart_x[!+!x, !+!y]",
				"cart_y(10)",
				"cart_y[10,10]",
				"cart_y((10))",
				"cart_y[(1),(1)]",
				"cart_y(x)",
				"cart_y[x,y]",
				"cart_y(++x)",
				"cart_y[++DEF_X, --DEF_Y]",
				"cart_y(--x)",
				"cart_y[--a,--r]",
				"cart_y(!!x)",
				"cart_y[!!x, !!y]",
				"cart_y(!+!x)",
				"cart_y[!+!x, !+!y]",
				"polar_a(10)",
				"polar_a[10,10]",
				"polar_a((10))",
				"polar_a[(1),(1)]",
				"polar_a(x)",
				"polar_a[x,y]",
				"polar_a(++x)",
				"polar_a[++DEF_X, --DEF_Y]",
				"polar_a(--x)",
				"polar_a[--a,--r]",
				"polar_a(!!x)",
				"polar_a[!!x, !!y]",
				"polar_a(!+!x)",
				"polar_a[!+!x, !+!y]",
				"polar_r(10)",
				"polar_r[10,10]",
				"polar_r((10))",
				"polar_r[(1),(1)]",
				"polar_r(x)",
				"polar_r[x,y]",
				"polar_r(++x)",
				"polar_r[++DEF_X, --DEF_Y]",
				"polar_r(--x)",
				"polar_r[--a,--r]",
				"polar_r(!!x)",
				"polar_r[!!x, !!y]",
				"polar_r(!+!x)",
				"polar_r[!+!x, !+!y]",
				"true",
				"false",
				"sin(true)",
				"sin[true,false]",
				"cos(true)",
				"cos[true,false]",
				"atan(true)",
				"atan[true,false]",
				"abs(true)",
				"abs[true,false]",
				"cart_x(true)",
				"cart_x[true,false]",
				"cart_y(true)",
				"cart_y[true,false]",
				"polar_a(true)",
				"polar_a[true,false]",
				"polar_r(true)",
				"polar_r[true,false]",
				"ident",
				"ident [true, false]",
				"ident[10,10]",
				"ident[(1),(1)]",
				"ident[x,y]",
				"ident[++DEF_X, --DEF_Y]",
				"ident[--a,--r]",
				"ident[!!x, !!y]",
				"ident[!+!x, !+!y]",
				"1|2&0==3!=4<5>6<=7>=8+9-10*11/12%13",
				"true|2&0==false!=4<5>sin(100)<=7>=8+9-10*+11/x%13",
				"++10*--10",
				"+-+10%+-+10",
				"+-10|-+10",
				"++true&--false",
				"true>-(-false)",
				"sin(x)!=cos(true + (DEF_Y))"
		};
		for (String input : valid) {
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			Parser parser = new Parser(scanner);  //Create a parser
			try {
				parser.expression();
				parser.matchEOF_();
			}
			catch (SyntaxException e) {
				System.out.println("Error parsing " + input);
				show(e);
				throw e;
			}
		} 
	}
	
	@Test
	public void invalid01() throws SyntaxException, LexicalException {
		String input = "image [0] ident";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.imageDeclaration();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	} 
	
	@Test
	public void invalid02() throws SyntaxException, LexicalException {
		String input = "image (0,0) ident";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.imageDeclaration();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	} 
	
	@Test
	public void invalid03() throws SyntaxException, LexicalException {
		String input = "image";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.imageDeclaration();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	} 
	
	@Test
	public void invalid04() throws SyntaxException, LexicalException {
		String input = "ident";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.imageDeclaration();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	} 
	
	@Test
	public void invalid05() throws SyntaxException, LexicalException {
		String input = "image sin(10)";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.imageDeclaration();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	} 
	
	@Test
	public void invalid06() throws SyntaxException, LexicalException {
		String input = "image 10";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.imageDeclaration();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	} 
	
	@Test
	public void invalid07() throws SyntaxException, LexicalException {
		String input = "image [] ident";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.imageDeclaration();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	} 
	
	@Test
	public void invalid08() throws SyntaxException, LexicalException {
		String input = "ident = ident2";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.sourceSinkDeclaration();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	} 
	
	@Test
	public void invalid09() throws SyntaxException, LexicalException {
		String input = "url ident = @";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.sourceSinkDeclaration();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	} 
	
	@Test
	public void invalid10() throws SyntaxException, LexicalException {
		String input = "url ident = true";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.sourceSinkDeclaration();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	} 
	
	@Test
	public void invalid11() throws SyntaxException, LexicalException {
		String input = "file ident @10";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.sourceSinkDeclaration();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	} 
	
	@Test
	public void invalid12() throws SyntaxException, LexicalException {
		String input = "url ident == false";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.sourceSinkDeclaration();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	} 
	
	@Test
	public void invalid13() throws SyntaxException, LexicalException {
		String input = "url ident =";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.sourceSinkDeclaration();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	} 
	
	@Test
	public void invalid14() throws SyntaxException, LexicalException {
		String input = "file ident = x";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.sourceSinkDeclaration();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	} 
	
	@Test
	public void invalid15() throws SyntaxException, LexicalException {
		String input = "file ident = ()";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.sourceSinkDeclaration();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	} 
	
	//-----------------------------------------------------
	
	@Test
	public void invalid16() throws SyntaxException, LexicalException {
		String input = "ident";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.assignment();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	} 
	
	@Test
	public void invalid17() throws SyntaxException, LexicalException {
		String input = "ident [x,y] = 100";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.assignment();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	} 
	
	@Test
	public void invalid18() throws SyntaxException, LexicalException {
		String input = "ident [[x,y]] =";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.assignment();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	} 
	@Test
	public void invalid19() throws SyntaxException, LexicalException {
		String input = "ident [[x,y]]";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.assignment();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	} 
	@Test
	public void invalid20() throws SyntaxException, LexicalException {
		String input = "ident = \"string\"";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.assignment();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	} 
	
	@Test
	public void invalid21() throws SyntaxException, LexicalException {
		String input = "ident [[r,a]] = 100";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.assignment();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	} 
	
	@Test
	public void invalid22() throws SyntaxException, LexicalException {
		String input = "ident [[1,1]] = 20";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.assignment();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	} 
	
	@Test
	public void invalid23() throws SyntaxException, LexicalException {
		String input = "ident [1,1] = 20";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.assignment();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	} 
	
	@Test
	public void invalid24() throws SyntaxException, LexicalException {
		String input = "ident (1,1) = 20";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.assignment();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void invalid25() throws SyntaxException, LexicalException {
		String input = "ident = @true";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.assignment();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	//-------------------------------------------------
	
	@Test
	public void invalid26() throws SyntaxException, LexicalException {
		String input = "5**6";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.expression();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	
	@Test
	public void invalid27() throws SyntaxException, LexicalException {
		String input = "4+/5";	
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.expression();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void invalid28() throws SyntaxException, LexicalException {
		String input = "true/%false";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.expression();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void invalid29() throws SyntaxException, LexicalException {
		String input = "x||y";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.expression();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void invalid30() throws SyntaxException, LexicalException {
		String input = "A&|Y";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.expression();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void invalid31() throws SyntaxException, LexicalException {
		String input = "sin";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.expression();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void invalid32() throws SyntaxException, LexicalException {
		String input = "@100";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.expression();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void invalid33() throws SyntaxException, LexicalException {
		String input = "@true";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.expression();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void invalid34() throws SyntaxException, LexicalException {
		String input = "polar_a[x]";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.expression();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void invalid35() throws SyntaxException, LexicalException {
		String input = "abs(10,20)";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.expression();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void invalid36() throws SyntaxException, LexicalException {
		String input = "sin(\"hi\")";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.expression();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void invalid37() throws SyntaxException, LexicalException {
		String input = "\"stringLiteral\"";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.expression();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void invalid38() throws SyntaxException, LexicalException {
		String input = "[1]";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.expression();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	@Test
	public void invalid39() throws SyntaxException, LexicalException {
		String input = "[true]";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.expression();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void invalid40() throws SyntaxException, LexicalException {
		String input = "*10";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.expression();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void invalid41() throws SyntaxException, LexicalException {
		String input = "!";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.expression();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void invalid42() throws SyntaxException, LexicalException {
		String input = "x y";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.expression();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void invalid43() throws SyntaxException, LexicalException {
		String input = "cos()";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.expression();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void invalid44() throws SyntaxException, LexicalException {
		String input = "cos[]";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.expression();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void invalid45() throws SyntaxException, LexicalException {
		String input = "(([1]))";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.expression();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void invalid46() throws SyntaxException, LexicalException {
		String input = "(1+2)+[3+4]";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.expression();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	
	//----------------------------------------------
	
	@Test
	public void invalid47() throws SyntaxException, LexicalException {
		String input = "glades221->polar_a";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.imageOut();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void invalid48() throws SyntaxException, LexicalException {
		String input = "glades221->[]";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.imageOut();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void invalid49() throws SyntaxException, LexicalException {
		String input = "glades221<-screen";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.imageOut();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void invalid50() throws SyntaxException, LexicalException {
		String input = "x<-screen";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.imageOut();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	//-----------------------------------------
	
	
	@Test
	public void invalid51() throws SyntaxException, LexicalException {
		String input = "Z<-glades221";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.imageIn();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void invalid52() throws SyntaxException, LexicalException {
		String input = "glades221->UF";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.imageIn();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void invalid53() throws SyntaxException, LexicalException {
		String input = "glades221<-!x";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.imageIn();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	//--------------------------------------------
	
	
	@Test
	public void invalid54() throws SyntaxException, LexicalException {
		String input = "boolean Mumbai = - + !(Y) ? true : polar_a [x,r)";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.variableDeclaration();
			parser.matchEOF_();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	

	@Test
	public void invalid55() throws SyntaxException, LexicalException {
		String input = "myProgram\n"
				+ "int apple = 1;\n"
				+ "int mango = 2 + 2;\n"
				+ "boolean isEmpty = true;\n"
				+ "int sum;\n"
				+ "image [0,100] dp;\n"
				+ "image [0,100] copy <- dp;\n"
				+ "file data = \"helloworld\";\n"
				+ "sum =(apple + mango) * 100;\n"
				+ "dp -> screen;\n"
				+ "copy <- @ sum % (dp[0,50]);\n";
	
				
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		try {
			parser.parse();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void invalid56() throws SyntaxException, LexicalException {
		String input = "myProgram\n"
				+ "int apple = 1;\n"
				+ "int mango = 2 @ 2;\n"
				+ "boolean isEmpty = true;\n"
				+ "int sum;\n"
				+ "image [0,100] dp;\n"
				+ "image [0,100] copy <- dp;\n"
				+ "file data = \"helloworld\";\n"
				+ "sum =(apple + mango) * 100;\n"
				+ "dp -> screen;\n"
				+ "copy <- @ sum % (dp[0,50]);\n";
	
				
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.parse();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void invalid57() throws SyntaxException, LexicalException {
		String input = "myProgram\n"
				+ "int apple == 1;\n";
	
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			parser.parse();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void invalid58() throws SyntaxException, LexicalException {
		String input = "myProgram\n"
				+ "int apple = !!!1;\n";
	
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		try {
			parser.parse();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void invalid59() throws SyntaxException, LexicalException {
		String input = "myProgram\n";
	
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //Create a parser
		try {
			parser.parse();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
}

