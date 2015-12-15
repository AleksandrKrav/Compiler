package enums;

public enum Symbols {

    PLUS("+"), MINUS("-"), MULTI("*"), DIV("/"), ASSIGN(":="), EQUAL("="), MORE(">"),
    LESS("<"), MORE_EQUAL(">="), LESS_EQUAL("<="), NOT_EQUAL("<>"), RBRA(")"), LBRA("("),
    SEMICOLON(";"), COLON(":"), DOT("."), COMMA(","),MOD("mod");

    public String string;

    Symbols(String string) {
        this.string = string;
    }
}
