public class ProductionRhs {

	Character terminal;
	String variables;

	ProductionRhs(Character t, String v){
		this.terminal = t;
		this.variables = v;
	}

	public Character getTerminal() {
		return terminal;
	}

	public String getVariables() {
		return variables;
	}
}
