package enums;

public enum Cycle {
    IF("if"), THEN("then"), ELSE("else"), FOR("for"), WHILE("while"), TO("to"), DO("do");

    public String string;

   Cycle(String string) {
        this.string = string;
    }
}
