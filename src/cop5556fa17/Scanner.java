/* *
 * Scanner for the class project in COP5556 Programming Language Principles 
 * at the University of Florida, Fall 2017.
 * 
 * This software is solely for the educational benefit of students 
 * enrolled in the course during the Fall 2017 semester.  
 * 
 * This software, and any software derived from it,  may not be shared with others or posted to public web sites,
 * either during the course or afterwards.
 * 
 *  @Beverly A. Sanders, 2017
  */

package cop5556fa17;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Scanner {
	
	@SuppressWarnings("serial")
	public static class LexicalException extends Exception {
		
		int pos;

		public LexicalException(String message, int pos) {
			super(message);
			this.pos = pos;
		}
		
		public int getPos() { return pos; }

	}

	public static void show(String s) {
		//System.out.println(s);
	}
	
	public static enum Kind {
		
		IDENTIFIER, INTEGER_LITERAL, BOOLEAN_LITERAL, STRING_LITERAL, 
		
		KW_x/* x */, KW_X/* X */, KW_y/* y */, KW_Y/* Y */, 
		KW_r/* r */, KW_R/* R */, KW_a/* a */, KW_A/* A */, 
		KW_Z/* Z */, KW_DEF_X/* DEF_X */, KW_DEF_Y/* DEF_Y */, KW_SCREEN/* SCREEN */, 
		KW_cart_x/* cart_x */, KW_cart_y/* cart_y */, KW_polar_a/* polar_a */, KW_polar_r/* polar_r */, 
		KW_abs/* abs */, KW_sin/* sin */, KW_cos/* cos */, KW_atan/* atan */, KW_log/* log */, 
		KW_image/* image */,  KW_int/* int */, KW_boolean/* boolean */, KW_url/* url */, KW_file/* file */, 
		
		OP_ASSIGN/* = */, OP_GT/* > */, OP_LT/* < */, 
		OP_EXCL/* ! */, OP_Q/* ? */, OP_COLON/* : */, OP_EQ/* == */, OP_NEQ/* != */, OP_GE/* >= */, OP_LE/* <= */, 
		OP_AND/* & */, OP_OR/* | */, OP_PLUS/* + */, OP_MINUS/* - */, OP_TIMES/* * */, OP_DIV/* / */, OP_MOD/* % */, 
		OP_POWER/* ** */, OP_AT/* @ */, OP_RARROW/* -> */, OP_LARROW/* <- */, 
		
		LPAREN/* ( */, RPAREN/* ) */, LSQUARE/* [ */, RSQUARE/* ] */, SEMI/* ; */, COMMA/* , */, EOF, 
	}

	public static enum State {
		START, 				//Ready to start reading new token
		IDENTIFIER, 		//Already reading a token which could be an Identifier / Keyword / Boolean Literal
		INTEGER_LITERAL, 	//Already reading an Integer Literal
		STRING_LITERAL, 	//Already reading a String Literal
		OPERATOR			//Already reading an Operator
	}
	
	Map<String, Kind> reserved;
	
	/** Class to represent Tokens. 
	 * 
	 * This is defined as a (non-static) inner class
	 * which means that each Token instance is associated with a specific 
	 * Scanner instance.  We use this when some token methods access the
	 * chars array in the associated Scanner.
	 * 
	 * 
	 * @author Beverly Sanders
	 *
	 */
	public class Token {
		public final Kind kind;
		public final int pos;
		public final int length;
		public final int line;
		public final int pos_in_line;

		public Token(Kind kind, int pos, int length, int line, int pos_in_line) {
			super();
			this.kind = kind;
			this.pos = pos;
			this.length = length;
			this.line = line;
			this.pos_in_line = pos_in_line;
		}

		public String getText() {
			if (kind == Kind.STRING_LITERAL) {
				return chars2String(chars, pos, length);
			}
			else return String.copyValueOf(chars, pos, length);
		}

		/**
		 * To get the text of a StringLiteral, we need to remove the
		 * enclosing " characters and convert escaped characters to
		 * the represented character.  For example the two characters \ t
		 * in the char array should be converted to a single tab character in
		 * the returned String
		 * 
		 * @param chars
		 * @param pos
		 * @param length
		 * @return
		 */
		private String chars2String(char[] chars, int pos, int length) {
			StringBuilder sb = new StringBuilder();
			for (int i = pos + 1; i < pos + length - 1; ++i) {// omit initial and final "
//			for (int i = pos; i < pos + length; ++i) {// omit initial and final "
				char ch = chars[i];
				if (ch == '\\') { // handle escape
					i++;
					ch = chars[i];
					switch (ch) {
						case 'b':
							sb.append('\b');
							break;
						case 't':
							sb.append('\t');
							break;
						case 'f':
							sb.append('\f');
							break;
						case 'r':
							sb.append('\r'); //for completeness, line termination chars not allowed in String literals
							break;
						case 'n':
							sb.append('\n'); //for completeness, line termination chars not allowed in String literals
							break;
						case '\"':
							sb.append('\"');
							break;
						case '\'':
							sb.append('\'');
							break;
						case '\\':
							sb.append('\\');
							break;
						default:
							assert false;
							break;
					}
				} else {
					sb.append(ch);
				}
			}
			return sb.toString();
		}

		/**
		 * precondition:  This Token is an INTEGER_LITERAL
		 * 
		 * @returns the integer value represented by the token
		 */
		public int intVal() {
			assert kind == Kind.INTEGER_LITERAL;
			return Integer.valueOf(String.copyValueOf(chars, pos, length));
		}

		public String toString() {
			return "[" + kind + "," + String.copyValueOf(chars, pos, length)  + "," + pos + "," + length + "," + line + ","
					+ pos_in_line + "]";
		}

		/** 
		 * Since we overrode equals, we need to override hashCode.
		 * https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html#equals-java.lang.Object-
		 * 
		 * Both the equals and hashCode method were generated by eclipse
		 * 
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((kind == null) ? 0 : kind.hashCode());
			result = prime * result + length;
			result = prime * result + line;
			result = prime * result + pos;
			result = prime * result + pos_in_line;
			return result;
		}

		/**
		 * Override equals method to return true if other object
		 * is the same class and all fields are equal.
		 * 
		 * Overriding this creates an obligation to override hashCode.
		 * 
		 * Both hashCode and equals were generated by eclipse.
		 * 
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Token other = (Token) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (kind != other.kind)
				return false;
			if (length != other.length)
				return false;
			if (line != other.line)
				return false;
			if (pos != other.pos)
				return false;
			if (pos_in_line != other.pos_in_line)
				return false;
			return true;
		}

		/**
		 * used in equals to get the Scanner object this Token is 
		 * associated with.
		 * @return
		 */
		private Scanner getOuterType() {
			return Scanner.this;
		}

	}

	/** 
	 * Extra character added to the end of the input characters to simplify the
	 * Scanner.  
	 */
	static final char EOFchar = 0;
	 
	
	/**
	 * The list of tokens created by the scan method.
	 */
	final ArrayList<Token> tokens;
	
	/**
	 * An array of characters representing the input.  These are the characters
	 * from the input string plus and additional EOFchar at the end.
	 */
	final char[] chars;  

	State state;

	
	/**
	 * position of the next token to be returned by a call to nextToken
	 */
	private int nextTokenPos = 0;

	Scanner(String inputString) {
		int numChars = inputString.length();
		this.chars = Arrays.copyOf(inputString.toCharArray(), numChars + 1); // input string terminated with null char
		chars[numChars] = EOFchar;
		this.state = State.START;
		reserved = new HashMap<>();
		buildMap(reserved);
		tokens = new ArrayList<Token>();
	}

	private void buildMap(Map<String, Kind> map) {
		/**
		 * Keywords
		 */
		map.put("x", Kind.KW_x);
		map.put("X", Kind.KW_X);
		map.put("y", Kind.KW_y);
		map.put("Y", Kind.KW_Y);
		map.put("a", Kind.KW_a);
		map.put("A", Kind.KW_A);
		map.put("r", Kind.KW_r);
		map.put("R", Kind.KW_R);
		map.put("Z", Kind.KW_Z);
		map.put("SCREEN", Kind.KW_SCREEN);
		map.put("cart_x", Kind.KW_cart_x);
		map.put("cart_y", Kind.KW_cart_y);
		map.put("DEF_X", Kind.KW_DEF_X);
		map.put("DEF_Y", Kind.KW_DEF_Y);
		map.put("polar_a", Kind.KW_polar_a);
		map.put("polar_r", Kind.KW_polar_r);
		map.put("abs", Kind.KW_abs);
		map.put("sin", Kind.KW_sin);
		map.put("cos", Kind.KW_cos);
		map.put("atan", Kind.KW_atan);
		map.put("log", Kind.KW_log);
		map.put("image", Kind.KW_image);
		map.put("url", Kind.KW_url);
		map.put("file", Kind.KW_file);
		map.put("int", Kind.KW_int);
		map.put("boolean", Kind.KW_boolean);
		
		/**
		 * Operators
		 */
		map.put("=", Kind.OP_ASSIGN);
		map.put(">", Kind.OP_GT);
		map.put("<", Kind.OP_LT);
		map.put("!", Kind.OP_EXCL);
		map.put("?", Kind.OP_Q);
		map.put(":", Kind.OP_COLON);
		map.put("==", Kind.OP_EQ);
		map.put("!=", Kind.OP_NEQ);
		map.put(">=", Kind.OP_GE);
		map.put("<=", Kind.OP_LE);
		map.put("&", Kind.OP_AND);
		map.put("|", Kind.OP_OR);
		map.put("+", Kind.OP_PLUS);
		map.put("-", Kind.OP_MINUS);
		map.put("*", Kind.OP_TIMES);
		map.put("/", Kind.OP_DIV);
		map.put("%", Kind.OP_MOD);
		map.put("**", Kind.OP_POWER);
		map.put("@", Kind.OP_AT);
		map.put("->", Kind.OP_RARROW);
		map.put("<-", Kind.OP_LARROW);
		
		/**
		 * Separators
		 */
		map.put("(", Kind.LPAREN);
		map.put(")", Kind.RPAREN);
		map.put("[", Kind.LSQUARE);
		map.put("]", Kind.RSQUARE);
		map.put(";", Kind.SEMI);
		map.put(",", Kind.COMMA);
		
		/**
		 * Boolean Literals
		 */
		map.put("true", Kind.BOOLEAN_LITERAL);
		map.put("false", Kind.BOOLEAN_LITERAL);
		
	}
	
	/**
	 * Method to scan the input and create a list of Tokens.
	 * 
	 * If an error is encountered during scanning, throw a LexicalException.
	 * 
	 * @return
	 * @throws LexicalException
	 */
	public Scanner scan() throws LexicalException {

		int pos = 0;
		int line = 1;
		int posInLine = 1;
		StringBuilder t = new StringBuilder("");
		int start = -1;
		int intValue = 0;
		int prevIntValue = 0;
		
		while (pos < chars.length) {
			
			char curr = chars[pos];
			
			if (state == State.START) {
				
				t = new StringBuilder("");
				
				// Line Terminator --- /n
				if ((int) curr == 10) {
				 	line++;
				 	posInLine = 1;
				 	pos++;
				}
				
				// Line Terminator --- /r
				else if ((int) curr == 13) {
					if ((int) chars[++pos] != 10) /* || pos == chars.length - 1 */ {
						line++;
		 				posInLine = 1;
					}
				}
				
				// White Space --- Space SP / Horizontal Tab HT / Form Feed FF
				else if (curr == 32 || curr == 9 || curr == 12) {
					pos++;
					posInLine++;
				}
				
				// Separators 
				else if (curr =='[' || curr ==']' || curr == '(' || curr == ')' || curr == ';' || curr == ',') {
					tokens.add(new Token(reserved.get(""+curr), pos, 1, line, posInLine));
					posInLine++;
					pos++;
				}
				
				// Identifier / Keyword / Boolean Literal
				else if (Character.isLetter(curr) || curr == '$' || curr == '_') {
					this.state = State.IDENTIFIER;
					t.append(curr);
					start = pos;
					posInLine++;
					pos++;
				}
				
				// Integer Literal
				else if (Character.isDigit(curr)) {
					if (curr == '0') {
						tokens.add(new Token(Kind.INTEGER_LITERAL, pos, 1, line, posInLine));
					}
					else {
						this.state = State.INTEGER_LITERAL;
						t.append(curr);
						start = pos;
						intValue = curr - '0';
						prevIntValue = 0;
					}
					posInLine++;
					pos++;
					
				}
				
				// String Literal
				else if (curr == '\"') {
					this.state = State.STRING_LITERAL;
					start = pos;
					posInLine++;
					pos++;
				}
				
				// Operator
				else if (curr == '=' || curr == '<' || curr == '>' || curr == '!' || curr == '?' ||
						 curr == ':' || curr == '&' || curr == '|' || curr == '+' || curr == '-' ||
						 curr == '*' || curr == '/' || curr == '%' || curr == '@') {
					
					t.append(curr);
					state = State.OPERATOR;
					start = pos;
					posInLine++;
					pos++;
				}
				
				else if ((int) curr == 0) { //EOF
					tokens.add(new Token(Kind.EOF, pos, 0, line, posInLine));
					pos++;
				}
				// Unknown Character
				else {
					if (curr == '\\') 
						throw new LexicalException("Error:: Unrecognized Character: Found Escape Character outside "
								+ "String Literal at line " + line + ", pos " + posInLine + ", char " + pos, pos);
					
					throw new LexicalException("Error:: Unrecognized Character: " + curr + " : ASCII Code: " 
							+ ((int) curr) + "at line " + line + ", pos " + posInLine + ", char " + pos, pos);
				}
			}
			
			else if (state == State.IDENTIFIER) {
				if (Character.isLetterOrDigit(curr) || curr == '$' || curr == '_') {
					t.append(curr);
					pos++;
					posInLine++;
				}
				else {
					tokens.add(new Token(reserved.getOrDefault(t.toString(), Kind.IDENTIFIER), start, pos - start, line, posInLine - (pos - start)));
					this.state = State.START;
					start = -1;
				}
			}
			
			else if (state == State.INTEGER_LITERAL) {
				if (Character.isDigit(curr)) {
					t.append(""+curr);
					prevIntValue = intValue;
					intValue = intValue * 10 + (curr - '0');
					if (intValue <= prevIntValue)
						throw new LexicalException("Error:: Integer Literal : Value too large at line " 
								+ line + ", pos " + posInLine + ", char " + pos, pos);
					pos++;
					posInLine++;
				}
				else {					
					tokens.add(new Token(Kind.INTEGER_LITERAL, start, pos-start, line, posInLine  - (pos - start)));
					state = State.START;
					start = -1;
				}

			}
			
			else if (state == State.STRING_LITERAL) {
				if ((int) curr == 10) {
					throw new LexicalException("Error:: String Literal: Line Terminator encountered "
							+ "at line " + line + ", pos " + posInLine + ", char " + pos, pos);
				}
				else if ((int) curr == 13) {
					throw new LexicalException("Error:: String Literal: Line Terminator encountered "
							+ "at line " + line + ", pos " + posInLine + ", char " + pos, pos);
				}
				else if (curr == '\"') {
					tokens.add(new Token(Kind.STRING_LITERAL, start, pos - start + 1, line, posInLine  - (pos - start)));
					state = State.START;
					start = -1;
				}
				else if (curr == '\\') {
					pos++;
					posInLine++;
					curr = chars[pos];
					
					switch(curr) {
						case 'b':
						case 't':
						case 'f':
						case '\"':
						case '\'':
						case '\\': 	
						case 'r': 	
						case 'n': break;
						default:  throw new LexicalException("Error:: String Literal: Invalid Escape Sequence "
								+ "at line " + line + ", pos " + posInLine + ", char " + pos, pos);
					}
				}
				else if (pos == chars.length - 1) {
						throw new LexicalException("Error:: String Literal: Ending quote missing "
								+ "at line " + line + ", pos " + posInLine + ", char " + pos, pos);
				}
				pos++;
				posInLine++;
			}
			
			else if (state == State.OPERATOR) {
				if (((curr == '=' || curr == '<' || curr == '>' || curr == '!' || curr == '?' ||
							 curr == ':' || curr == '&' || curr == '|' || curr == '+' || curr == '-' ||
							 curr == '*' || curr == '/' || curr == '%' || curr == '@')) && (reserved.containsKey(t.toString() + curr))) {
					t.append(curr);
					pos++;
					posInLine++;
				}
				else {
					if(t.toString().equals("/") && curr == '/') {
						pos++;
						posInLine++;
						boolean newLine = false;
						while (pos < chars.length-1 && !newLine) {
							if ((int) chars[pos] == 10) {
							 	line++;
							 	posInLine = 1;
							 	newLine = true;
							 	pos++;
							}
							else if ((int) chars[pos] == 13) {
								if ((int) chars[++pos] != 10) /* || pos == chars.length - 1*/ {
									line++;
									newLine = true;
					 				posInLine = 1;
								}
							}
							else 
								pos++;
						}
						this.state = State.START;
					}
					else {
						tokens.add(new Token(reserved.get(t.toString()), start, pos - start, line, posInLine - (pos - start)));
						state = State.START;
					}
				}
				
			}
		
		}
		return this;
	}


	/**
	 * Returns true if the internal interator has more Tokens
	 * 
	 * @return
	 */
	public boolean hasTokens() {
		return nextTokenPos < tokens.size();
	}

	/**
	 * Returns the next Token and updates the internal iterator so that
	 * the next call to nextToken will return the next token in the list.
	 * 
	 * It is the callers responsibility to ensure that there is another Token.
	 * 
	 * Precondition:  hasTokens()
	 * @return
	 */
	public Token nextToken() {
		return tokens.get(nextTokenPos++);
	}
	
	/**
	 * Returns the next Token, but does not update the internal iterator.
	 * This means that the next call to nextToken or peek will return the
	 * same Token as returned by this methods.
	 * 
	 * It is the callers responsibility to ensure that there is another Token.
	 * 
	 * Precondition:  hasTokens()
	 * 
	 * @return next Token.
	 */
	public Token peek() {
		return tokens.get(nextTokenPos);
	}
	
	
	/**
	 * Resets the internal iterator so that the next call to peek or nextToken
	 * will return the first Token.
	 */
	public void reset() {
		nextTokenPos = 0;
	}

	/**
	 * Returns a String representation of the list of Tokens 
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Tokens:\n");
		for (int i = 0; i < tokens.size(); i++) {
			sb.append(tokens.get(i).toString()).append('\n');
		}
		return sb.toString();
	}

	public int size() {
		return tokens.size();
	}
}
