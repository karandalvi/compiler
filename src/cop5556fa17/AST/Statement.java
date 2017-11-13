package cop5556fa17.AST;

import cop5556fa17.Scanner.Token;

public abstract class Statement extends ASTNode {
	
	public boolean isCartesian;
	public Declaration Declaration;
	
	public Statement(Token firstToken) {
		super(firstToken);
	}

	public boolean isCartesian() {
		return isCartesian;
	}

	public void setCartesian(boolean isCartesian) {
		this.isCartesian = isCartesian;
	}

	public Declaration getDeclaration() {
		return Declaration;
	}

	public void setDeclaration(Declaration declaration) {
		Declaration = declaration;
	}

}
