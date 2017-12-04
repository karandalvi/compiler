package cop5556fa17;

import java.util.HashMap;
import java.util.Map;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeUtils.Type;
import cop5556fa17.AST.ASTNode;
import cop5556fa17.AST.ASTVisitor;
import cop5556fa17.AST.Declaration;
import cop5556fa17.AST.Declaration_Image;
import cop5556fa17.AST.Declaration_SourceSink;
import cop5556fa17.AST.Declaration_Variable;
import cop5556fa17.AST.Expression;
import cop5556fa17.AST.Expression_Binary;
import cop5556fa17.AST.Expression_BooleanLit;
import cop5556fa17.AST.Expression_Conditional;
import cop5556fa17.AST.Expression_FunctionAppWithExprArg;
import cop5556fa17.AST.Expression_FunctionAppWithIndexArg;
import cop5556fa17.AST.Expression_Ident;
import cop5556fa17.AST.Expression_IntLit;
import cop5556fa17.AST.Expression_PixelSelector;
import cop5556fa17.AST.Expression_PredefinedName;
import cop5556fa17.AST.Expression_Unary;
import cop5556fa17.AST.Index;
import cop5556fa17.AST.LHS;
import cop5556fa17.AST.Program;
import cop5556fa17.AST.Sink;
import cop5556fa17.AST.Sink_Ident;
import cop5556fa17.AST.Sink_SCREEN;
import cop5556fa17.AST.Source;
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_Ident;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Statement_Assign;
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;
import java.net.*;
public class TypeCheckVisitor implements ASTVisitor {
	
	private Map<String, Declaration> symbolTable;

	@SuppressWarnings("serial")
	public static class SemanticException extends Exception {
		Token t;

		public SemanticException(Token t, String message) {
			super("line " + t.line + " pos " + t.pos_in_line + ": "+  message);
			this.t = t;
		}

	}		
	
	public TypeCheckVisitor () {
		symbolTable = new HashMap<>();
	}
	
	/**
	 * The program name is only used for naming the class.  It does not rule out
	 * variables with the same name.  It is returned for convenience.
	 * 
	 * @throws Exception 
	 */
	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		for (ASTNode node: program.decsAndStatements) {
			node.visit(this, arg);
		}
		return program.name;
	}

	@Override
	public Object visitDeclaration_Variable(Declaration_Variable declaration_Variable, Object arg) throws Exception {
		
		Declaration_Variable dv = declaration_Variable; 
		String name = dv.name;
		Expression expr = dv.e;
		Token t = dv.firstToken;
		
		visit(expr);
		
		if (symbolTable.containsKey(name))
			throw new SemanticException(t, "Identifier already declared before.");
		
		symbolTable.put(name, dv);
		dv.setType(TypeUtils.getType(t));
		
		if ((expr != null) && (expr.getType() != dv.getType()))
			throw new SemanticException(t, "Expression Type does not match the Identifier Type.");

		return arg;
	}

	
	@Override
	public Object visitDeclaration_Image(Declaration_Image declaration_Image, Object arg) throws Exception {
		
		Declaration_Image di = declaration_Image;
		String name = di.name;
		Token t = di.firstToken;
		Expression xExpr = di.xSize;
		Expression yExpr = di.ySize;
		Source src = di.source;
		
		visit(xExpr);
		visit(yExpr);
		visit(src);
		
		if (symbolTable.containsKey(name))
			throw new SemanticException(t, "Identifier already declared before.");
		
		symbolTable.put(name, di);
		di.setType(TypeUtils.Type.IMAGE);
		
		if (xExpr != null) {
			if (yExpr == null)
				throw new SemanticException(t, "Ysize not defined.");
			else {
				if (xExpr.Type != Type.INTEGER || yExpr.Type != Type.INTEGER) {
					throw new SemanticException(t, "Xsize and Ysize must have Integer Type.");
				}
			}
		}	
		return arg;
	}
	
	@Override
	public Object visitDeclaration_SourceSink(Declaration_SourceSink declaration_SourceSink, Object arg) throws Exception {
		
		Declaration_SourceSink ds = declaration_SourceSink;
		Token t = ds.firstToken;
		String name = ds.name;
		Source src = ds.source;
		
		visit(src);
		
		if (symbolTable.containsKey(name))
			throw new SemanticException(t, "Identifier already declared before.");
		
		symbolTable.put(name, ds);
		ds.setType(TypeUtils.getType(t));
		
		if (ds.Type != src.Type)
			throw new SemanticException(t, "Type of source and type specified do not match. " + src.Type.toString());
		return arg;
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public Object visitExpression_Binary(Expression_Binary expression_Binary,
			Object arg) throws Exception {
		
		Expression_Binary eb = expression_Binary;
		Kind op = eb.op;
		Expression e0 = eb.e0;
		Expression e1 = eb.e1;
		Token t = eb.firstToken;
		
		visit(e0);
		visit(e1);
		
		if (op == Kind.OP_EQ || op == Kind.OP_NEQ)
			eb.setType(Type.BOOLEAN);
		
		else if ((op == Kind.OP_GE|| op == Kind.OP_LE || op == Kind.OP_GT|| op == Kind.OP_LT) &&
				  e0.Type == Type.INTEGER)
			eb.setType(Type.BOOLEAN);
		
		else if ((op == Kind.OP_AND || op == Kind.OP_OR) &&
				  (e0.getType() == Type.INTEGER || e0.getType() == Type.BOOLEAN))
			eb.setType(e0.Type);
		
		else if ((op == Kind.OP_DIV || op == Kind.OP_MINUS || op == Kind.OP_MOD || op == Kind.OP_PLUS || op == Kind.OP_POWER || op == Kind.OP_TIMES) &&
				  (e0.getType() == Type.INTEGER))
			eb.setType(Type.INTEGER);
		else {
			expression_Binary.setType(null);
			throw new SemanticException(t, "Binary Expression has null Type.");
		}
		if (e0.Type != e1.Type)
			throw new SemanticException(t, "Binary Expressions do not have expressions of matching Type.");
		
		
		return arg;
	}

	@Override
	public Object visitExpression_Unary(Expression_Unary expression_Unary, Object arg) throws Exception {
		
		Expression_Unary eu = expression_Unary;
		Expression expr = eu.e;
		Kind op = eu.op;
		Token t = eu.firstToken;
		
		visit(expr);
		
		if ((op == Kind.OP_EXCL) && (expr.Type == Type.INTEGER || expr.Type == Type.BOOLEAN))
			eu.setType(expr.Type);
		
		else if ((op == Kind.OP_PLUS || op == Kind.OP_MINUS) && (expr.Type == Type.INTEGER))
			eu.setType(expr.Type);
		
		if (eu.Type == null)
			throw new SemanticException(t, "Expression has null type.");
		
		return arg;
	}

	@Override
	public Object visitIndex(Index index, Object arg) throws Exception {
		
		Expression e0 = index.e0;
		Expression e1 = index.e1;
		Token t = index.firstToken;
		
		visit(e0);
		visit(e1);
		
		if (e0.Type != e1.Type)
			throw new SemanticException(t, "Expressions in Index do not have same Type.");
		
		boolean cartesian;
		if (e0 instanceof Expression_PredefinedName && e1 instanceof Expression_PredefinedName) {
			Expression_PredefinedName ep0 = (Expression_PredefinedName) e0;
			Expression_PredefinedName ep1 = (Expression_PredefinedName) e1;
			cartesian = (!(ep0.kind == Kind.KW_r && ep1.kind == Kind.KW_a));
		}
		else {
			cartesian = true; //TODO: Verify this
		}
		index.setCartesian(cartesian);
		
		return arg;
	}

	
	@Override
	public Object visitExpression_PixelSelector(Expression_PixelSelector expression_PixelSelector, Object arg) throws Exception {
		
		Expression_PixelSelector ep = expression_PixelSelector;
		Token t = ep.firstToken;
		Index i = ep.index;
		String name = ep.name;
		
		visit(i);
		
		if (!symbolTable.containsKey(name))
			throw new SemanticException(t, "Identifier in PixelSelector Expression is not declared.");
		
		Type nameType = symbolTable.get(name).Type;
		ep.Type = (nameType == Type.IMAGE ? Type.INTEGER : (i == null? nameType : null));
		
		if (ep.Type == null) 
			throw new SemanticException(t, "PixelSelector Expression has null type.");
		
		return arg;
	}

	@Override
	public Object visitExpression_Conditional(Expression_Conditional expression_Conditional, Object arg) throws Exception {
		
		Expression_Conditional ec = expression_Conditional;
		Expression c = ec.condition;
		Expression t = ec.trueExpression;
		Expression f = ec.falseExpression;
		Token to = ec.firstToken;
		
		visit(c);
		visit(t);
		visit(f);
		
		if (c.Type != Type.BOOLEAN)
			throw new SemanticException(to, "Expression Conditional is not a boolean expression.");
		
		if (t.Type != f.Type)
			throw new SemanticException(to, "True & False expression do not have the same type.");
		
		ec.setType(t.Type);
		return arg;
	}

	@Override
	public Object visitSource_StringLiteral(Source_StringLiteral source_StringLiteral, Object arg) throws Exception {		
		Source_StringLiteral ss = source_StringLiteral;

		if (isValidURL(ss.fileOrUrl))
			ss.setType(Type.URL);
		else
			ss.setType(Type.FILE);
		
		return arg;
	}

	@Override
	public Object visitSource_CommandLineParam(Source_CommandLineParam source_CommandLineParam, Object arg) throws Exception {
		
		Source_CommandLineParam cp = source_CommandLineParam;
		Token t = cp.firstToken;
		Expression paramNum = cp.paramNum;
		
		visit(paramNum);
		
		cp.setType(paramNum.Type);
		if (cp.Type != Type.INTEGER)
			throw new SemanticException(t, "Command Line Param is not of Integer Type.");
		
		return arg;
	}

	@Override
	public Object visitSource_Ident(Source_Ident source_Ident, Object arg) throws Exception {

		Source_Ident si = source_Ident;
		Token t = si.firstToken;
		String name = si.name;
		
		if (!symbolTable.containsKey(name))
			throw new SemanticException(t, "Identifier not declared.");
		
		si.setType(symbolTable.get(name).Type);
		if (!(si.Type == Type.FILE || si.Type == Type.URL))
			throw new SemanticException(t, "Source Identifier is not of File Type or URL Type.");
		
		return arg;
	}


	@Override
	public Object visitExpression_IntLit(Expression_IntLit expression_IntLit, Object arg) throws Exception {
		expression_IntLit.setType(Type.INTEGER);
		return arg;
	}

	@Override
	public Object visitExpression_FunctionAppWithExprArg(Expression_FunctionAppWithExprArg expression_FunctionAppWithExprArg, Object arg) throws Exception {
		Expression_FunctionAppWithExprArg ef = expression_FunctionAppWithExprArg;
		Expression expr = ef.arg;
		Token t = ef.firstToken;
		
		visit(expr);
		
		if (expr.Type != Type.INTEGER)
			throw new SemanticException(t, "Expression does not have Integer Type.");
		
		ef.setType(Type.INTEGER);
		return arg;
	}

	@Override
	public Object visitExpression_FunctionAppWithIndexArg(Expression_FunctionAppWithIndexArg expression_FunctionAppWithIndexArg, Object arg) throws Exception {
		expression_FunctionAppWithIndexArg.setType(Type.INTEGER);
		return arg;
	}

	@Override
	public Object visitExpression_PredefinedName(
			Expression_PredefinedName expression_PredefinedName, Object arg)
			throws Exception {
		expression_PredefinedName.setType(Type.INTEGER);
		return arg;
	}

	@Override
	public Object visitStatement_Out(Statement_Out statement_Out, Object arg) throws Exception {
		
		Statement_Out so = statement_Out;
		Sink s = so.sink;
		Token t = so.firstToken;
		String name = so.name;
		
		visit(s);
		
		if (!symbolTable.containsKey(name))
			throw new SemanticException(t, "Identifier not declared.");
		
		so.Declaration = symbolTable.get(name);
		Type nameType = so.Declaration.Type;
		
		if (!(((nameType == Type.INTEGER || nameType == Type.BOOLEAN) && s.Type== Type.SCREEN) ||
			  ((nameType == Type.IMAGE) && (s.Type == Type.FILE || s.Type == Type.SCREEN))))
			throw new SemanticException(t, "Statement Out Type is incorrect.");
		
		return arg;
	}

	@Override
	public Object visitStatement_In(Statement_In statement_In, Object arg) throws Exception {
		
		Statement_In si = statement_In;
		Source src = si.source;
		Token t = si.firstToken;
		String name = si.name;
		
		visit(src);
//		Following lines commented for assignment 5		
//		if (!symbolTable.containsKey(name))
//			throw new SemanticException(t, "Identifier not declared.");

		si.Declaration = symbolTable.get(name);

//		if (si.Declaration.Type != src.Type)
//			throw new SemanticException(t, "Identifier and source type do not match.");
		
		return arg;
	}

	@Override
	public Object visitStatement_Assign(Statement_Assign statement_Assign, Object arg) throws Exception {
		
		Statement_Assign sa = statement_Assign;
		LHS l = sa.lhs;
		Expression e = sa.e;
		Token t = sa.firstToken;
		
		visit(l);
		visit(e);
		
		//Image added for assignment 6
		if (((l.Type != Type.IMAGE) && (l.Type != e.Type)) || ((l.Type == Type.IMAGE) && (e.Type != Type.INTEGER)))
			throw new SemanticException(t, "LHS and Expression type do not match.");
		
		sa.setCartesian(l.isCartesian);
		return arg;
	}

	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		Token t = lhs.firstToken;
		
		visit(lhs.index);
		
		if (!(symbolTable.containsKey(lhs.name)))
			throw new SemanticException(t, "Identifier is not declared.");
		
		lhs.Declaration = symbolTable.get(lhs.name);
		lhs.Type = lhs.Declaration.Type;
		
		//TODO:Verify this
		if (lhs.index!=null)
			lhs.setCartesian(lhs.index.isCartesian());
		
		return arg;
	}

	@Override
	public Object visitSink_SCREEN(Sink_SCREEN sink_SCREEN, Object arg)
			throws Exception {
		sink_SCREEN.setType(Type.SCREEN);
		return arg;
	}

	@Override
	public Object visitSink_Ident(Sink_Ident sink_Ident, Object arg) throws Exception {
		
		Sink_Ident si = sink_Ident;
		Token t = si.firstToken;
		String name = si.name;
		
		if (!symbolTable.containsKey(si.name))
			throw new SemanticException(t, "Identifier not declared.");
		
		si.Type = symbolTable.get(name).Type;
		if (si.Type != Type.FILE)
			throw new SemanticException(t, "Sink Identifier is not of File Type.");
		
		return arg;
	}

	@Override
	public Object visitExpression_BooleanLit(Expression_BooleanLit expression_BooleanLit, Object arg) throws Exception {
		expression_BooleanLit.setType(Type.BOOLEAN);
		return arg;
		
	}

	@Override
	public Object visitExpression_Ident(Expression_Ident expression_Ident, Object arg) throws Exception {
		Expression_Ident ei = expression_Ident;
		Token t = ei.firstToken;
		
		if (!symbolTable.containsKey(ei.name))
			throw new SemanticException(t, "Identifier not declared.");
		
		ei.Type = symbolTable.get(ei.name).Type;
		return arg;
	}
	
	public boolean isValidURL(String s) {
		try {
			new java.net.URL(s);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	public void visit(ASTNode a) throws Exception {
		if (a != null)
			a.visit(this, null);
	}
}
