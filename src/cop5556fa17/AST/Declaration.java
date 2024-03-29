package cop5556fa17.AST;

import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeUtils;
public abstract class Declaration extends ASTNode {
	
	public TypeUtils.Type Type;
	
	public Declaration(Token firstToken) {
		super(firstToken);
	}

	public TypeUtils.Type getType() {
		return Type;
	}

	public void setType(TypeUtils.Type type) {
		Type = type;
	}

}
