package analyzers;

import enums.ReservedWords;
import enums.Symbols;
import enums.Types;
//import enums.TypesOfNumber;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LexicalAnalyser {

    public ReservedWords[] reservedWords = ReservedWords.values();
//    public TypesOfNumber[] typesOfNumbers = TypesOfNumber.values();
    public Symbols[] symbols = Symbols.values();

    private String string;
    private ArrayList<String> commands = new ArrayList<>();
    private ArrayList<Types> types = new ArrayList<>();

    public LexicalAnalyser(String string) {
        this.string = string;
    }

    public ArrayList<String> analyze() {

        string = string.toLowerCase();

        Pattern pattern = Pattern.compile("(:=)|(>=)|(<=)|(<>)|(end\\.)|[\\s\\*\\+\\-\\[\\]()=,;:<>/]");
        Matcher matcher = pattern.matcher(string);
        int index = 0, newIndex;
        String symbol, beforeSymbol;
        while (matcher.find()) {
            newIndex = matcher.start();
            symbol = matcher.group();
            if (index != newIndex) {
                beforeSymbol = string.substring(index, newIndex);
                commands.add(beforeSymbol);
                checkReservedWords(beforeSymbol);
            }
            if (symbol.equals("end.")) {
                beforeSymbol = "end";
                commands.add(beforeSymbol);
                checkReservedWords(beforeSymbol);
                symbol = ".";
            }
            commands.add(symbol);
            checkReservedWords(symbol);
            index = newIndex + symbol.length();
        }

        for (int i = 0; i < commands.size(); i++) {
            if (commands.get(i).equals(" ") || commands.get(i).equals("\n")) {
                commands.remove(i);
                i--;
            }
        }

        if (!commands.get(commands.size() - 1).equals(".")) {
            System.out.println("Очікувалося \".\" в кінці");
            System.exit(-1);
        }

        return commands;
    }

    private boolean checkReservedWords(String command) {

        Pattern pattern = Pattern.compile("[0-9]");
        Matcher matcher;

        Pattern patternForIllegalSymbols = Pattern.compile("[^a-z0-9_-]");
        Matcher matcherForIllegalSymbol;

        if (command.equals(" ") || command.equals("\n")) { return false; }

        for (ReservedWords reservedWord : reservedWords) {
            if (command.equals(reservedWord.string)) {
                types.add(Types.RESERVED_WORD);
                return true;
            }
        }

        if (command.equals("real") || command.equals("integer")){
            types.add(Types.NUMBER_TYPE);
            return true;
        }

//        for (TypesOfNumber type : typesOfNumbers) {
//            if (command.equals(type.string)) {
//                types.add(Types.NUMBER_TYPE);
//                return true;
//            }
//        }

        for (Symbols symbol : symbols) {
            if (command.equals(symbol.string)) {
                types.add(Types.SYMBOL);
                return true;
            }
        }

        matcher = pattern.matcher(command.substring(0, 1));
        if (matcher.find()){
            try {
                Double.parseDouble(command);
            } catch (NumberFormatException e) {
                System.out.println("Невірно введено змінна/число");
                System.exit(-1);
            }
            types.add(Types.NUMBER);
            return true;
        }

        matcherForIllegalSymbol = patternForIllegalSymbols.matcher(command);
        if (!matcherForIllegalSymbol.find()) {
            types.add(Types.ID);
            return true;
        } else {
            System.out.println("Невірний вираз");
            System.exit(-1);
        }

        return false;
    }

    public ArrayList<Types> getTypes() {
        return types;
    }
}
