package cop5556fa17.AST;

import cop5556fa17.TypeUtils;
import cop5556fa17.Scanner.Token;

public abstract class Expression extends ASTNode {
	
	public TypeUtils.Type Type;
	
	public TypeUtils.Type getType() {
		return Type;
	}

	public void setType(TypeUtils.Type type) {
		Type = type;
	}

	public Expression(Token firstToken) {
		super(firstToken);
	}

}
