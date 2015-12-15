package enums;

public enum Logic {
    MORE(">"), LESS("<"), MORE_EQUAL(">="), LESS_EQUAL("<="), NOT_EQUAL("<>"),  EQUAL("=") ;

    public String string;

    Logic(String string) {
        this.string = string;
    }
}
