package enums;

public enum ReservedWords {

    PROGRAM("program"), VAR("var"), BEGIN("begin"), END("end"),
            IF("if"), THEN("then"), ELSE("else"), FOR("for"), WHILE("while"), TO("to"), DO("do"), WRITELN("writeln");

    public String string;

    ReservedWords(String string) {
        this.string = string;
    }

}
