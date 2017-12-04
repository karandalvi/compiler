package cop5556fa17;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import cop5556fa17.Scanner.Kind;
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
import cop5556fa17.AST.Sink_Ident;
import cop5556fa17.AST.Sink_SCREEN;
import cop5556fa17.AST.Source;
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_Ident;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;
import cop5556fa17.AST.Statement_Assign;
//import cop5556fa17.image.ImageFrame;
//import cop5556fa17.image.ImageSupport;
import cop5556fa17.ImageFrame;
import cop5556fa17.ImageSupport;

public class CodeGenVisitor implements ASTVisitor, Opcodes {

	/**
	 * All methods and variable static.
	 */


	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 */
	public CodeGenVisitor(boolean DEVEL, boolean GRADE, String sourceFileName) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
	}

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;

	MethodVisitor mv; // visitor of method currently under construction

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;
	
	final int x_slot = 1;
	final int y_slot = 2;
	final int X_slot = 3;
	final int Y_slot = 4;
	final int r_slot = 5;
	final int a_slot = 6;
	final int R_slot = 7;
	final int A_slot = 8;
	final int DEF_X_slot = 9;
	final int DEF_Y_slot = 10;
	final int Z_slot = 11;
	
	public void loadPredefinedVariables() {
		mv.visitLdcInsn(256);
		mv.visitVarInsn(ISTORE, DEF_X_slot);
		mv.visitLdcInsn(256);
		mv.visitVarInsn(ISTORE, DEF_Y_slot);
		mv.visitLdcInsn(16777215);
		mv.visitVarInsn(ISTORE, Z_slot);
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
//		cw = new ClassWriter(0);
		className = program.name;  
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object", null);
		cw.visitSource(sourceFileName, null);
		// create main method
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
		// initialize
		mv.visitCode();		
		//add label before first instruction
		Label mainStart = new Label();
		mv.visitLabel(mainStart);		
		// if GRADE, generates code to add string to log
		//CodeGenUtils.genLog(GRADE, mv, "entering main");

		loadPredefinedVariables();
		// visit decs and statements to add field to class
		//  and instructions to main method, respectivley
		ArrayList<ASTNode> decsAndStatements = program.decsAndStatements;
		for (ASTNode node : decsAndStatements) {
			node.visit(this, arg);
		}

		//generates code to add string to log
		//CodeGenUtils.genLog(GRADE, mv, "leaving main");
		
		//adds the required (by the JVM) return statement to main
		mv.visitInsn(RETURN);
		
		//adds label at end of code
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		
		//handles parameters and local variables of main. Right now, only args
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);
		mv.visitLocalVariable("x", "I", null, mainStart, mainEnd, 1);
		mv.visitLocalVariable("y", "I", null, mainStart, mainEnd, 2);
		mv.visitLocalVariable("X", "I", null, mainStart, mainEnd, 3);
		mv.visitLocalVariable("Y", "I", null, mainStart, mainEnd, 4);
		mv.visitLocalVariable("r", "I", null, mainStart, mainEnd, 5);
		mv.visitLocalVariable("a", "I", null, mainStart, mainEnd, 6);
		mv.visitLocalVariable("R", "I", null, mainStart, mainEnd, 7);
		mv.visitLocalVariable("A", "I", null, mainStart, mainEnd, 8);
		mv.visitLocalVariable("DEF_X", "I", null, mainStart, mainEnd, 9);
		mv.visitLocalVariable("DEF_Y", "I", null, mainStart, mainEnd, 10);
		mv.visitLocalVariable("Z", "I", null, mainStart, mainEnd, 11);

		//Sets max stack size and number of local vars.
		//Because we use ClassWriter.COMPUTE_FRAMES as a parameter in the constructor,
		//asm will calculate this itself and the parameters are ignored.
		//If you have trouble with failures in this routine, it may be useful
		//to temporarily set the parameter in the ClassWriter constructor to 0.
		//The generated classfile will not be correct, but you will at least be
		//able to see what is in it.
		mv.visitMaxs(0, 0);
		
		//terminate construction of main method
		mv.visitEnd();
		
		//terminate class construction
		cw.visitEnd();

		//generate classfile as byte array and return
		return cw.toByteArray();
	}

	@Override
	public Object visitDeclaration_Variable(Declaration_Variable declaration_Variable, Object arg) throws Exception {
		
		Declaration_Variable dv = declaration_Variable;
		String fieldName = dv.name;
		String fieldType = dv.type.kind == Kind.KW_int? "I" : "Z";
		Object initValue = new Integer(0);
		FieldVisitor fv = cw.visitField(ACC_STATIC, fieldName, fieldType, null, initValue);
		fv.visitEnd();
		
		if (dv.e != null) {
			dv.e.visit(this, arg);
			mv.visitFieldInsn(PUTSTATIC, className, fieldName, fieldType);
		}
		
		return null;
	}

	@Override
	public Object visitDeclaration_SourceSink(Declaration_SourceSink declaration_SourceSink, Object arg)
			throws Exception {
		String fieldName = declaration_SourceSink.name;
		String fieldType = "Ljava/lang/String;";
		Object initValue = new String("");
		FieldVisitor fv = cw.visitField(ACC_STATIC, fieldName, fieldType, null, initValue);
		fv.visitEnd();
		
		if (declaration_SourceSink.source != null) {
			declaration_SourceSink.source.visit(this, arg);
			mv.visitFieldInsn(PUTSTATIC, className, fieldName, fieldType);
		}
		return null;
	}
	
	@Override
	public Object visitDeclaration_Image(Declaration_Image declaration_Image, Object arg) throws Exception {
		String fieldName = declaration_Image.name;
		String fieldType = "Ljava/awt/image/BufferedImage;";
		Object initValue = null;
		
		FieldVisitor fv = cw.visitField(ACC_STATIC, fieldName, fieldType, null, initValue);
		fv.visitEnd();
		
		if (declaration_Image.source != null) {
			declaration_Image.source.visit(this, arg);
			if (declaration_Image.xSize != null) {
				declaration_Image.xSize.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
				declaration_Image.ySize.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
			}
			else {
				mv.visitInsn(ACONST_NULL);
				mv.visitInsn(ACONST_NULL);
			}
			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/ImageSupport", "readImage", "(Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/Integer;)Ljava/awt/image/BufferedImage;", false);
		}
		else {
			if (declaration_Image.xSize != null) {
				declaration_Image.xSize.visit(this, arg);
//				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
				declaration_Image.ySize.visit(this, arg);
//				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
			}
			else {
				mv.visitVarInsn(ILOAD, DEF_X_slot);
				mv.visitVarInsn(ILOAD, DEF_Y_slot);
			}
			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/ImageSupport", "makeImage", "(II)Ljava/awt/image/BufferedImage;", false);
		}
		mv.visitFieldInsn(PUTSTATIC, className, fieldName, fieldType);
		return null;
	}

	/** For Integers and booleans, the only "sink"is the screen, so generate code to print to console.
	 * For Images, load the Image onto the stack and visit the Sink which will generate the code to handle the image.
	 */
	@Override
	public Object visitStatement_Out(Statement_Out statement_Out, Object arg) throws Exception {
		// TODO in HW5:  only INTEGER and BOOLEAN	
		if (statement_Out.Declaration.Type == Type.INTEGER || statement_Out.Declaration.Type == Type.BOOLEAN) {
			String fieldType = statement_Out.Declaration.Type == Type.INTEGER? "I" : "Z";
			mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			mv.visitFieldInsn(GETSTATIC, className, statement_Out.name, fieldType);
			CodeGenUtils.genLogTOS(GRADE, mv, statement_Out.Declaration.Type); //TODO: Verify
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "("+fieldType+")V", false);
		}
		else if (statement_Out.Declaration.Type == Type.IMAGE) {
			String fieldName = statement_Out.name;
			String fieldType = "Ljava/awt/image/BufferedImage;";
			mv.visitFieldInsn(GETSTATIC, className, fieldName, fieldType);
			CodeGenUtils.genLogTOS(GRADE, mv, statement_Out.Declaration.Type); //TODO: Verify
			statement_Out.sink.visit(this, arg);
		}
		return null;
	}

	/**
	 * Visit source to load rhs, which will be a String, onto the stack
	 * 
	 *  In HW5, you only need to handle INTEGER and BOOLEAN
	 *  Use java.lang.Integer.parseInt or java.lang.Boolean.parseBoolean 
	 *  to convert String to actual type. 
	 *  
	 *  TODO HW6 remaining types
	 */
	@Override
	public Object visitStatement_In(Statement_In statement_In, Object arg) throws Exception {
		// TODO (see comment )
		Statement_In s = statement_In;
		if (s.source.Type == Type.INTEGER) {
			s.source.visit(this, arg); //command line param is now on top of stack
			String desc = "";
			if (s.Declaration.Type == Type.INTEGER) {
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
				desc = "I";
			}
			else if (s.Declaration.Type == Type.BOOLEAN) {
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z", false);
				desc = "Z";
			}
			else if (s.Declaration.Type == Type.IMAGE) {
				if (((Declaration_Image) s.Declaration).xSize != null) {
					mv.visitFieldInsn(GETSTATIC, className, s.name, "Ljava/awt/image/BufferedImage;");
					mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/ImageSupport", "getX", "(Ljava/awt/image/BufferedImage;)I", false);
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
					mv.visitFieldInsn(GETSTATIC, className, s.name, "Ljava/awt/image/BufferedImage;");
					mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/ImageSupport", "getY", "(Ljava/awt/image/BufferedImage;)I", false);
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
				}
				else {
					mv.visitInsn(ACONST_NULL);
					mv.visitInsn(ACONST_NULL);
				}
				
				mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/ImageSupport", "readImage", "(Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/Integer;)Ljava/awt/image/BufferedImage;", false);
				desc = "Ljava/awt/image/BufferedImage;";
			}
			mv.visitFieldInsn(PUTSTATIC, className, s.name, desc);
		}
		
		else if (s.source.Type == Type.IMAGE) {
			s.source.visit(this, arg); 
			if (((Declaration_Image) s.Declaration).xSize != null) {
				mv.visitFieldInsn(GETSTATIC, className, s.name, "Ljava/awt/image/BufferedImage;");
				mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/ImageSupport", "getX", "(Ljava/awt/image/BufferedImage;)I", false);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);

				mv.visitFieldInsn(GETSTATIC, className, s.name, "Ljava/awt/image/BufferedImage;");
				mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/ImageSupport", "getY", "(Ljava/awt/image/BufferedImage;)I", false);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);

			}
			else {
				mv.visitInsn(ACONST_NULL);
				mv.visitInsn(ACONST_NULL);
			}
						
			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/ImageSupport", "readImage", "(Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/Integer;)Ljava/awt/image/BufferedImage;", false);
			mv.visitFieldInsn(PUTSTATIC, className, s.name, "Ljava/awt/image/BufferedImage;");
		}
		return null;
		
	}

	
	/**
	 * In HW5, only handle INTEGER and BOOLEAN types.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public Object visitStatement_Assign(Statement_Assign statement_Assign, Object arg) throws Exception {
		//TODO  (see comment)
		if (statement_Assign.lhs.Type == Type.INTEGER || statement_Assign.lhs.Type == Type.BOOLEAN) {
			statement_Assign.e.visit(this, arg);
			statement_Assign.lhs.visit(this, arg);
		}
		else if (statement_Assign.lhs.Type == Type.IMAGE) {
			mv.visitInsn(ICONST_0);
			mv.visitVarInsn(ISTORE, x_slot);
			mv.visitFieldInsn(GETSTATIC, className, statement_Assign.lhs.name, "Ljava/awt/image/BufferedImage;");
			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/ImageSupport", "getX", "(Ljava/awt/image/BufferedImage;)I", false);
			mv.visitVarInsn(ISTORE, X_slot);
			mv.visitFieldInsn(GETSTATIC, className, statement_Assign.lhs.name, "Ljava/awt/image/BufferedImage;");
			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/ImageSupport", "getY", "(Ljava/awt/image/BufferedImage;)I", false);
			mv.visitVarInsn(ISTORE, Y_slot);
			
			
			Label l0 = new Label();
			Label l1 = new Label();
			Label l2 = new Label();
			Label l3 = new Label();
			Label l00 = new Label();
			Label l11 = new Label();
			Label l22 = new Label();
			Label l33 = new Label();
			
			mv.visitLabel(l0);
			mv.visitJumpInsn(GOTO, l1);
			mv.visitLabel(l2);
			//---code
				mv.visitInsn(ICONST_0);
				mv.visitVarInsn(ISTORE, y_slot);
				mv.visitLabel(l00);
				mv.visitJumpInsn(GOTO, l11);
				mv.visitLabel(l22);
//				if (!statement_Assign.isCartesian()) {
//					mv.visitVarInsn(ILOAD, x_slot);
//					mv.visitVarInsn(ILOAD, y_slot);
//					mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "polar_r", "(II)I", false);
//					mv.visitVarInsn(ISTORE, r_slot);
//					mv.visitVarInsn(ILOAD, x_slot);
//					mv.visitVarInsn(ILOAD, y_slot);
//					mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "polar_a", "(II)I", false);
//					mv.visitVarInsn(ISTORE, a_slot);
//				}
				//---code
				statement_Assign.e.visit(this, arg);
				mv.visitFieldInsn(GETSTATIC, className, statement_Assign.lhs.name, "Ljava/awt/image/BufferedImage;");
				mv.visitVarInsn(ILOAD, x_slot);
				mv.visitVarInsn(ILOAD, y_slot);
				statement_Assign.lhs.visit(this, arg);
				mv.visitIincInsn(y_slot, 1);
				mv.visitLabel(l11);
				mv.visitVarInsn(ILOAD, y_slot);
				mv.visitVarInsn(ILOAD, Y_slot);
				mv.visitJumpInsn(IF_ICMPLT, l22);
				mv.visitLabel(l33);
			mv.visitIincInsn(x_slot, 1);
			mv.visitLabel(l1);
			mv.visitVarInsn(ILOAD, x_slot);
			mv.visitVarInsn(ILOAD, X_slot);
			mv.visitJumpInsn(IF_ICMPLT, l2);
			mv.visitLabel(l3);
			
		}
		return null;
	}
	
	@Override
	public Object visitExpression_Binary(Expression_Binary expression_Binary, Object arg) throws Exception {
		Expression_Binary eb = expression_Binary;
		
		switch(eb.op) {
		case OP_PLUS:
			eb.e0.visit(this, arg);
			eb.e1.visit(this, arg);
			mv.visitInsn(IADD); 
			break;
		case OP_MINUS: 
			eb.e0.visit(this, arg);
			eb.e1.visit(this, arg);
			mv.visitInsn(ISUB); 
			break;
		case OP_TIMES:
			eb.e0.visit(this, arg);
			eb.e1.visit(this, arg);
			mv.visitInsn(IMUL); 
			break;
		case OP_DIV: 
			eb.e0.visit(this, arg);
			eb.e1.visit(this, arg);
			mv.visitInsn(IDIV); 
			break;
		case OP_MOD: 
			eb.e0.visit(this, arg);
			eb.e1.visit(this, arg);
			mv.visitInsn(IREM); 
			break;
		case OP_POWER:
			eb.e0.visit(this, arg);
			mv.visitInsn(I2D);
			eb.e1.visit(this, arg);
			mv.visitInsn(I2D);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "pow", "(DD)D", false);
			mv.visitInsn(D2I);
			break;
		case OP_NEQ:
			eb.e0.visit(this, arg);
			eb.e1.visit(this, arg);
			Label l1 = new Label();
			mv.visitJumpInsn(IF_ICMPNE, l1);
			mv.visitLdcInsn(new Boolean(false));
			Label l2 = new Label();
			mv.visitJumpInsn(GOTO, l2);
			mv.visitLabel(l1);
			mv.visitLdcInsn(new Boolean(true));
			mv.visitLabel(l2);
			break;
		case OP_EQ:
			eb.e0.visit(this, arg);
			eb.e1.visit(this, arg);
			Label l3 = new Label();
			mv.visitJumpInsn(IF_ICMPEQ, l3);
			mv.visitLdcInsn(new Boolean(false));
			Label l4 = new Label();
			mv.visitJumpInsn(GOTO, l4);
			mv.visitLabel(l3);
			mv.visitLdcInsn(new Boolean(true));
			mv.visitLabel(l4);
			break;
			
		case OP_AND:
			eb.e0.visit(this, arg);
			eb.e1.visit(this, arg);
			mv.visitInsn(IAND); 
			break;
		case OP_OR:
			eb.e0.visit(this, arg);
			eb.e1.visit(this, arg);
			mv.visitInsn(IOR); 
			break;
		case OP_LT:
			eb.e0.visit(this, arg);
			eb.e1.visit(this, arg);
			Label l5 = new Label();
			mv.visitJumpInsn(IF_ICMPLT, l5);
			mv.visitLdcInsn(new Boolean(false));
			Label l6 = new Label();
			mv.visitJumpInsn(GOTO, l6);
			mv.visitLabel(l5);
			mv.visitLdcInsn(new Boolean(true));
			mv.visitLabel(l6);
			break;
		case OP_LE:
			eb.e0.visit(this, arg);
			eb.e1.visit(this, arg);
			Label l7 = new Label();
			mv.visitJumpInsn(IF_ICMPLE, l7);
			mv.visitLdcInsn(new Boolean(false));
			Label l8 = new Label();
			mv.visitJumpInsn(GOTO, l8);
			mv.visitLabel(l7);
			mv.visitLdcInsn(new Boolean(true));
			mv.visitLabel(l8);
			break;	
		case OP_GT:
			eb.e0.visit(this, arg);
			eb.e1.visit(this, arg);
			Label l9 = new Label();
			mv.visitJumpInsn(IF_ICMPGT, l9);
			mv.visitLdcInsn(new Boolean(false));
			Label l10 = new Label();
			mv.visitJumpInsn(GOTO, l10);
			mv.visitLabel(l9);
			mv.visitLdcInsn(new Boolean(true));
			mv.visitLabel(l10);
			break;
		case OP_GE:
			eb.e0.visit(this, arg);
			eb.e1.visit(this, arg);
			Label l11 = new Label();
			mv.visitJumpInsn(IF_ICMPGE, l11);
			mv.visitLdcInsn(new Boolean(false));
			Label l12 = new Label();
			mv.visitJumpInsn(GOTO, l12);
			mv.visitLabel(l11);
			mv.visitLdcInsn(new Boolean(true));
			mv.visitLabel(l12);
			break;	
		}
		//CodeGenUtils.genLogTOS(GRADE, mv, expression_Binary.getType());
		return null;
	}

	@Override
	public Object visitExpression_Unary(Expression_Unary expression_Unary, Object arg) throws Exception {
		Expression_Unary eu = expression_Unary;
		if (eu.op == Kind.OP_MINUS) {
			eu.e.visit(this, null);
			mv.visitLdcInsn(new Integer(-1));
			mv.visitInsn(IMUL);
		}
		else if (eu.op == Kind.OP_EXCL) {
			eu.e.visit(this, null);
			if (eu.e.Type == Type.INTEGER)
				mv.visitInsn(INEG);
			else {
				mv.visitLdcInsn(new Boolean(true));
				Label l1 = new Label();
				mv.visitJumpInsn(IF_ICMPEQ, l1);
				mv.visitLdcInsn(new Boolean(true));
				Label l2 = new Label();
				mv.visitJumpInsn(GOTO, l2);
				mv.visitLabel(l1);
				mv.visitLdcInsn(new Boolean(false));
				mv.visitLabel(l2);
			}
		}
		else {
			eu.e.visit(this,null);
		}
		//CodeGenUtils.genLogTOS(GRADE, mv, expression_Unary.getType());
		return null;
	}

	// generate code to leave the two values on the stack
	@Override
	public Object visitIndex(Index index, Object arg) throws Exception {
		
		index.e0.visit(this, arg);
		index.e1.visit(this, arg);
		if (!index.isCartesian()) {
			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "cart_x", RuntimeFunctions.cart_xSig, false);
			index.e0.visit(this, arg);
			index.e1.visit(this, arg);
			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "cart_y", RuntimeFunctions.cart_ySig, false);
		}
		return null;
	}

	@Override
	public Object visitExpression_PixelSelector(Expression_PixelSelector expression_PixelSelector, Object arg)
			throws Exception {
		mv.visitFieldInsn(GETSTATIC, className, expression_PixelSelector.name, "Ljava/awt/image/BufferedImage;");
		expression_PixelSelector.index.visit(this, arg);
		mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/ImageSupport", "getPixel", "(Ljava/awt/image/BufferedImage;II)I", false);
		return null;
	}

	@Override
	public Object visitExpression_Conditional(Expression_Conditional expression_Conditional, Object arg)
			throws Exception {
		// TODO 
		Expression_Conditional ec = expression_Conditional;
		ec.condition.visit(this, arg);
		mv.visitLdcInsn(new Boolean(true));
		Label l1 = new Label();
		mv.visitJumpInsn(IF_ICMPEQ, l1);
		ec.falseExpression.visit(this, arg);
		Label l2 = new Label();
		mv.visitJumpInsn(GOTO, l2);
		mv.visitLabel(l1);
		ec.trueExpression.visit(this, arg);
		mv.visitLabel(l2);
//		TODO: Commented because trueexpression is logged when visited
//		CodeGenUtils.genLogTOS(GRADE, mv, expression_Conditional.trueExpression.getType());
		return null;
	}

  
	@Override
	public Object visitSource_StringLiteral(Source_StringLiteral source_StringLiteral, Object arg) throws Exception {
		mv.visitLdcInsn(new String(source_StringLiteral.fileOrUrl));
		return null;
	}

	

	@Override
	public Object visitSource_CommandLineParam(Source_CommandLineParam source_CommandLineParam, Object arg)
			throws Exception {
		mv.visitVarInsn(ALOAD, 0);
		source_CommandLineParam.paramNum.visit(this, arg);
		mv.visitInsn(AALOAD);
		return null;
	}

	@Override
	public Object visitSource_Ident(Source_Ident source_Ident, Object arg) throws Exception {
		mv.visitFieldInsn(GETSTATIC, className, source_Ident.name, "Ljava/lang/String;");
		return null;
	}	



	@Override
	public Object visitExpression_IntLit(Expression_IntLit expression_IntLit, Object arg) throws Exception {	
		mv.visitLdcInsn(new Integer(expression_IntLit.value));
		//CodeGenUtils.genLogTOS(GRADE, mv, Type.INTEGER);
		return null;
	}

	@Override
	public Object visitExpression_FunctionAppWithExprArg(
			Expression_FunctionAppWithExprArg expression_FunctionAppWithExprArg, Object arg) throws Exception {
		expression_FunctionAppWithExprArg.arg.visit(this, arg);
		switch(expression_FunctionAppWithExprArg.function) {
			case KW_abs: mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "abs", "(I)I", false);
			break;
			case KW_log: mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "log", "(I)I", false);
			break;
		}
		return null;
	}

	@Override
	public Object visitExpression_FunctionAppWithIndexArg(
			Expression_FunctionAppWithIndexArg expression_FunctionAppWithIndexArg, Object arg) throws Exception {
		expression_FunctionAppWithIndexArg.arg.e0.visit(this, arg);
		expression_FunctionAppWithIndexArg.arg.e1.visit(this, arg);
		switch(expression_FunctionAppWithIndexArg.function) {
			case KW_cart_x: mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "cart_x", "(II)I", false);
			break;
			case KW_cart_y: mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "cart_y", "(II)I", false);
			break;
			case KW_polar_r: mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "polar_r", "(II)I", false);
			break;
			case KW_polar_a: mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "polar_a", "(II)I", false);
			break;
		}
		return null;
	}

	@Override
	public Object visitExpression_PredefinedName(Expression_PredefinedName expression_PredefinedName, Object arg)
			throws Exception {
		switch (expression_PredefinedName.kind) {
			case KW_x: 	mv.visitVarInsn(ILOAD, x_slot);
			break;
			case KW_y: 	mv.visitVarInsn(ILOAD, y_slot);
			break;
			case KW_X: 	mv.visitVarInsn(ILOAD, X_slot);
			break;
			case KW_Y: 	mv.visitVarInsn(ILOAD, Y_slot);
			break;
			case KW_r:  mv.visitVarInsn(ILOAD, x_slot);
						mv.visitVarInsn(ILOAD, y_slot);
						mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "polar_r", "(II)I", false);
						mv.visitVarInsn(ISTORE, r_slot); 	
						mv.visitVarInsn(ILOAD, r_slot);
			break;
			case KW_a: 	mv.visitVarInsn(ILOAD, x_slot);
						mv.visitVarInsn(ILOAD, y_slot);
						mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "polar_a", "(II)I", false);
						mv.visitVarInsn(ISTORE, a_slot);
						mv.visitVarInsn(ILOAD, a_slot);
			break;
			case KW_R: 	mv.visitVarInsn(ILOAD, R_slot);
			break;
			case KW_A: 	mv.visitVarInsn(ILOAD, A_slot);
			break;
			case KW_DEF_X: 	mv.visitVarInsn(ILOAD, DEF_X_slot);
			break;
			case KW_DEF_Y: 	mv.visitVarInsn(ILOAD, DEF_Y_slot);
			break;
			case KW_Z: 	mv.visitVarInsn(ILOAD, Z_slot);
			break;
		}
		return null;
	}

	

	/**
	 * In HW5, only handle INTEGER and BOOLEAN types.
	 */
	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		if (lhs.Declaration.Type == Type.INTEGER) {
			mv.visitFieldInsn(PUTSTATIC, className, lhs.name, "I");
		}
		else if (lhs.Declaration.Type == Type.BOOLEAN) {
			mv.visitFieldInsn(PUTSTATIC, className, lhs.name, "Z");
		}
		else if (lhs.Declaration.Type == Type.IMAGE) {
			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/ImageSupport", "setPixel", "(ILjava/awt/image/BufferedImage;II)V", false);
		}
		return null;
	}
	

	@Override
	public Object visitSink_SCREEN(Sink_SCREEN sink_SCREEN, Object arg) throws Exception {
		mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/ImageFrame", "makeFrame", "(Ljava/awt/image/BufferedImage;)Ljavax/swing/JFrame;", false);
		mv.visitInsn(POP);
		return null;
	}

	@Override
	public Object visitSink_Ident(Sink_Ident sink_Ident, Object arg) throws Exception {
		mv.visitFieldInsn(GETSTATIC, className, sink_Ident.name, "Ljava/lang/String;");
		mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/ImageSupport", "write", "(Ljava/awt/image/BufferedImage;Ljava/lang/String;)V", false);
		return null;
	}

	@Override
	public Object visitExpression_BooleanLit(Expression_BooleanLit expression_BooleanLit, Object arg) throws Exception {
		mv.visitLdcInsn(new Boolean(expression_BooleanLit.value));
		//CodeGenUtils.genLogTOS(GRADE, mv, Type.BOOLEAN);
		return null;
	}

	@Override
	public Object visitExpression_Ident(Expression_Ident expression_Ident,
			Object arg) throws Exception {
		Expression_Ident ei = expression_Ident;
		//TODO: Works only for Int and Boolean for Assignment 5
		String desc = (ei.Type == Type.INTEGER)? "I" : "Z";
		mv.visitFieldInsn(GETSTATIC, className, ei.name, desc);
		//CodeGenUtils.genLogTOS(GRADE, mv, expression_Ident.getType());
		return null;
	}

}
