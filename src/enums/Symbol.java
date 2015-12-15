package enums;

public enum Symbol {

    ASSIGN(":="), LBRA("("), RBRA(")"),
    SEMICOLON(";"), COLON(":"), DOT("."), COMMA(","),;

    public String string;

    Symbol(String string) {
        this.string = string;
    }
}
