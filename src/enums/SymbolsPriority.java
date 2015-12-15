package enums;

/**
 * Created by PetroOV on 12/12/2015.
 */
public enum SymbolsPriority {

    PLUS("2"), MINUS("1"), MULTI("4"), DIV("3"), RBRA("5"), LBRA("5");

    public String string;

    SymbolsPriority(String string) {
        this.string = string;
    }
}
