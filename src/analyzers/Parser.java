package analyzers;

import enums.ReservedWords;
import enums.Symbols;
import enums.Types;

import java.util.ArrayList;

public class Parser {
    public Tree getNodes() {
        return nodes;
    }

    private Tree nodes = new Tree();
    private ArrayList<String> commands;
    private ArrayList<Types> types;
    private String current;
    private int pos = 0;
    private int temp = 0;
    private ArrayList<Integer> forSum = new ArrayList<>();
    private ArrayList<Integer> forMulti = new ArrayList<>();
    private int startInBracket = 0;
    private int forTest = 0;
    private String tab = "";

    public Parser(ArrayList<String> commands, ArrayList<Types> types) {
        this.commands = new ArrayList<>();
        this.types = new ArrayList<>();
        for (int i = 0; i < commands.size(); i++) {
            this.commands.add(commands.get(i));
//            System.out.println(i + commands.get(i));
        }
        for (int i = 0; i < commands.size(); i++) {
            this.types.add(types.get(i));
//            System.out.println(i + " " +  (types.get(i).toString()));
        }
    }

    public void parse() {
        current = commands.get(pos);
        if (current.equals(ReservedWords.VAR.string)) {
            addNode(-1);
            temp = pos;
            checkIds(pos - 1);
        }
        checkBody();

        ArrayList<Integer> root = new ArrayList<>();
        for (int i = 0; i < nodes.sizeOfTree(); i++) {
            if (nodes.getNode(i).getParentId() == -1) {
                root.add(i);
            }
        }
        System.out.println(root.get(1));
        for (int i = 0; i < root.size(); i++) {
            printChild(nodes.getNode(root.get(i)));
        }
    }

    public void checkBody() {
        if (current.equals(ReservedWords.BEGIN.string)) {
            addNode(-1);
            checkBodyStatements(pos - 1); //temp  checkStatement
            if (current.equals(ReservedWords.END.string)) {
                addNode(-1);
                if (current.equals(Symbols.DOT.string)) {
                    nodes.addNodeToTree(new Node(pos, current, -1));
                } else {
                    System.out.println("Очікувалося \"" + Symbols.DOT.string + "\"");
                    System.exit(-1);
                }
            } else {
                System.out.println("Очікувалося \"" + ReservedWords.END.string + "\"");
                System.exit(-1);
            }
        } else {
            System.out.println("Очікувався оператор \"" + ReservedWords.BEGIN.string + "\"");
            System.exit(-1);
        }
    }

    public void checkBodyStatements(int parent) {
        while (checkStatement(parent)) { //обработать ошибку
            forSum = new ArrayList<>();
        }
    }

    public void checkStatements(int parent) {
        if (checkStatement(parent)) {
            //
        } else if (current.equals(ReservedWords.BEGIN.string)) {
            addNode(parent);
            checkBodyStatements(pos - 1);
            if (current.equals(ReservedWords.END.string)) {
                addNode(parent);
                if (current.equals(Symbols.SEMICOLON.string)) {
                    addNode(parent);
                } else {
                    System.out.println("Очікувалося \"" + Symbols.SEMICOLON.string + "\"");
                    System.exit(-1);
                }
            } else {
                System.out.println("Очікувалося \"" + ReservedWords.END.string + "\"");
                System.exit(-1);
            }
        } else {
            System.out.println("Невірний вираз Statements");
            System.exit(-1);
        }
    }

    public boolean checkStatement(int parent) {
        if (current.equals(ReservedWords.IF.string)) {
            addNode(parent);
            checkCondition(pos - 1);
            if (current.equals(ReservedWords.THEN.string)) {
                addNode(parent); //подумати про parent
                checkStatements(pos - 1);
            } else {
                System.out.println("Очікувалося \"" + ReservedWords.THEN.string + "\"");
                System.exit(-1);
            }
            if (current.equals(ReservedWords.ELSE.string)) {
                addNode(parent);
                checkStatements(pos - 1);
            }
            return true;
        } else if (current.equals(ReservedWords.WHILE.string)) {
            addNode(parent);
            checkCondition(pos - 1);
            if (current.equals(ReservedWords.DO.string)) {
                addNode(parent); //подумати про parent
                checkStatements(pos - 1);
            } else {
                System.out.println("Очікувалося \"" + ReservedWords.DO.string + "\"");
                System.exit(-1);
            }
            return true;
        } else if (current.equals(ReservedWords.FOR.string)) {
            addNode(parent);
            checkExpression(pos - 1);
            if (current.equals(ReservedWords.TO.string)) {
                addNode(parent);
                checkSum(pos - 1);
                if (current.equals(ReservedWords.DO.string)) {
                    addNode(parent);
                    checkStatements(pos - 1);
                } else {
                    System.out.println("Очікувалося \"" + ReservedWords.DO.string + "\"");
                    System.exit(-1);
                }
            } else {
                System.out.println("Очікувалося \"" + ReservedWords.TO.string + "\"");
                System.exit(-1);
            }
            return true;
        } else if (types.get(pos) == Types.ID) {
            checkExpression(parent);
            if (!current.equals(Symbols.SEMICOLON.string)) { //наоборот
                System.out.println("Очікувалося \"" + Symbols.SEMICOLON.string + "\"");
                System.exit(-1);
            } else {
                addNode(parent);
            }
            return true;
        } else if (current.equals(ReservedWords.WRITELN.string)) {
            addNode(parent);
            checkBracketExpr(pos - 1);
            if (current.equals(Symbols.SEMICOLON.string)) {
                commands.remove(pos);  // new
                types.remove(pos);  //
                current = commands.get(pos); //
            } else {
                System.out.println("Очікувалося \"" + Symbols.SEMICOLON.string + "\"");
                System.exit(-1);
            }
            return true;
        } else {
            return false;
        }
    }

    public void checkCondition(int parent) { //temp name
        if (current.equals(Symbols.LBRA.string)) {
            commands.remove(pos);
            types.remove(pos);
            current = commands.get(pos);
            checkTest(parent);
        } else {
            System.out.println("\"Очікувалося \"" + Symbols.LBRA.string + "\"");
            System.exit(-1);
        }

        if (current.equals(Symbols.RBRA.string)) {
            commands.remove(pos);
            types.remove(pos);
            current = commands.get(pos);
        } else {
            System.out.println("\"Очікувалося \"" + Symbols.RBRA.string + "\"");
            System.exit(-1);
        }
    }

    public void checkTest(int parent) {
        if (checkSum(parent)) {
            if (current.equals(Symbols.LESS.string) || current.equals(Symbols.MORE.string) ||
                    current.equals(Symbols.LESS_EQUAL.string) || current.equals(Symbols.MORE_EQUAL.string) ||
                    current.equals(Symbols.EQUAL.string) || current.equals(Symbols.NOT_EQUAL.string)) {

                if (forSum.size() != 0) {
                    nodes.getNode(forSum.get(0)).setParent(pos); //think
                } else {
                    nodes.getNode(pos - 1).setParent(pos);
                }

                forSum = new ArrayList<>(); //

                addNode(parent);
                if (!checkSum(pos - 1)) {
                    System.out.println("Невірний вираз логічних операцій");
                    System.exit(-1);
                }
            } else {
                System.out.println("Невірний знак порівняння");
                System.exit(-1);
            }
        } else {
            System.out.println("Невірний вираз логічної операції");
            System.exit(-1);
        }
    }

    public boolean checkSum(int parent) {
        if (checkTerm(parent)) {
            if (current.equals(Symbols.PLUS.string) || current.equals(Symbols.MINUS.string) ||
                    current.equals(Symbols.MULTI.string) || current.equals(Symbols.DIV.string)|| current.equals(Symbols.MOD.string)) {

                if (startInBracket != 0) { //kostil'
                    nodes.getNode(startInBracket).setParent(pos);
                    startInBracket = 0;
                } else if (forSum.size() != 0) {
                    nodes.getNode(forSum.get(forSum.size() - 1)).setParent(pos);
                    forSum.remove(forSum.size() - 1);
                } else { //
                    nodes.getNode(pos - 1).setParent(pos);
                }

                forSum.add(pos);
                forTest = pos;
//                nodes.getNode(pos - 1).setParent(pos);
                addNode(parent);
                if (!checkTerm(pos - 1)) {
                    System.out.println("Невірний вираз Sum1");
                    System.exit(-1);
                } else {
                    if (current.equals(Symbols.MULTI.string) || current.equals(Symbols.DIV.string)|| current.equals(Symbols.MOD.string)) {
                        nodes.getNode(pos - 1).setParent(pos);
                        checkMulti(pos - 2);
                    }
                    test(parent);
                }
            } else {
                return true;
            }
        } else {
            System.out.println("Невірний вираз Sum2");
            System.exit(-1); //
        }
        if (forSum.size() > 1) {
            if (forMulti.size() != 0) {
                nodes.getNode(forSum.get(forSum.size() - 1)).setParent(forMulti.get(forMulti.size() - 1));
            } else {
                nodes.getNode(forSum.get(forSum.size() - 1)).setParent(forSum.get(forSum.size() - 2));
            }
            forSum.remove(forSum.size() - 1);
        }
        return true;
    }

    public boolean test(int parent) {
        if (current.equals(Symbols.PLUS.string) || current.equals(Symbols.MINUS.string) ||
                current.equals(Symbols.MULTI.string) || current.equals(Symbols.DIV.string)|| current.equals(Symbols.MOD.string)) {

//            if (current.equals(Symbols.PLUS.string) || current.equals(Symbols.MINUS.string)) {
            nodes.getNode(forSum.get(forSum.size() - 1)).setParent(pos); //
            forSum.set(forSum.size() - 1, pos);
//            }

            addNode(parent);

//            if (commands.get(pos - 1).equals(Symbols.MULTI.string) || commands.get(pos - 1).equals(Symbols.DIV.string)) {
//                nodes.getNode(pos - 2).setParent(pos - 1);
//                nodes.getNode(pos - 1).setParent(forSum.get(forSum.sizeOfTree() - 1)); //
//            }

            if (!checkTerm(pos - 1)) {
                System.out.println("Невірний вираз test");
                System.exit(-1);
            } else {
                if (current.equals(Symbols.MULTI.string) || current.equals(Symbols.DIV.string)|| current.equals(Symbols.MOD.string)) {
                    nodes.getNode(pos - 1).setParent(pos);
                    checkMulti(pos - 2);
                }
                test(parent);
            }
        } else {
            return true;
        }
        return false;
    }

    public boolean checkMulti(int parent) {
        if (current.equals(Symbols.MULTI.string) || current.equals(Symbols.DIV.string)|| current.equals(Symbols.MOD.string)) {

//            if (forMulti.sizeOfTree() != 0) {
//                nodes.getNode(forMulti.get(forMulti.sizeOfTree() - 1)).setParent(pos);
//                forMulti.remove(forMulti.sizeOfTree() - 1);
//            }

            forMulti.add(pos);
            addNode(parent);

            if (!checkTerm(pos - 1)) {
                System.out.println("Невірний вираз Multi");
                System.exit(-1);
            } else {
                if (current.equals(Symbols.MULTI.string) || current.equals(Symbols.DIV.string)) {
                    checkMulti(parent);
                }
            }
        }
        if (forMulti.size() > 0) {
            forMulti.remove(forMulti.size() - 1);
        }
        return true;
    }

    public boolean checkTerm(int parent) {
        if (types.get(pos) == Types.ID) {
            addNode(parent);
            return true;
        } else if (types.get(pos) == Types.NUMBER) {
            addNode(parent);
            return true;
        } else if (current.equals(Symbols.LBRA.string)) {
            checkBracketExpr(parent);
            return true;
        } else {
            System.out.println("Невірний вираз Term");
            System.exit(-1);
        }

        return false;
    }

    public void checkBracketExpr(int parent) { //temp name
        if (current.equals(Symbols.LBRA.string)) {
            commands.remove(pos);
            types.remove(pos);
            current = commands.get(pos);
            if (!current.equals(Symbols.LBRA.string)) {
                startInBracket = pos;
            }
            checkSum(parent);
        } else {
            System.out.println("\"Очікувалося \"" + Symbols.LBRA.string + "\"");
            System.exit(-1);
        }

        if (current.equals(Symbols.RBRA.string)) {
            commands.remove(pos);
            types.remove(pos);

            current = commands.get(pos);

//            if (forSum.sizeOfTree() == 2) { //maybe kostil' sizeOfTree == 2
//                nodes.getNode(forSum.get(forSum.sizeOfTree() - 1)).setParent(forSum.get(forSum.sizeOfTree() - 2));
//                forSum.remove(forSum.sizeOfTree() - 1);
//            }

        } else {
            System.out.println("\"Очікувалося \"" + Symbols.RBRA.string + "\"");
            System.exit(-1);
        }
    }


    public void checkVar(int parent) {
        for (int i = temp; i < pos; i++) {
            nodes.getNode(i).setParent(pos);
        }

        addNode(parent);
        if (types.get(pos) == Types.NUMBER_TYPE) { //перевірка на числовий тип
            addNode(pos - 1);
        } else {
            System.out.println("Очікувався числовий тип");
            System.exit(-1);
        }

        if (current.equals(Symbols.SEMICOLON.string)) { //перевірка на крапку з комою
            addNode(parent);
        } else {
            System.out.println("Очікувалося \"" + Symbols.SEMICOLON.string + "\"");
            System.exit(-1);
        }

        if (types.get(pos) == Types.ID) {
            temp = pos;
            checkIds(parent);
        }
//        } else {
//            checkStatements();
//        }

    }

    public void checkIds(int parent) {
        if ((types.get(pos) == Types.ID)) { //перевірка на ідентифікатор
            addNode(pos + 1);
            if (current.equals(Symbols.COLON.string)) { //перевірка на двокрапку
                checkVar(parent);
            } else if ((current.equals(Symbols.COMMA.string))) { //перевірка на кому і видаляєм її
                commands.remove(pos);
                types.remove(pos);
                current = commands.get(pos);
                checkIds(parent);
            }
        } else {
            System.out.println("Очікувався правильний ідентифікатор");
            System.exit(-1);
        }
    }

    public void checkExpression(int parent) {
        if ((types.get(pos) == Types.ID)) {
            addNode(pos + 1);
            if (current.equals(Symbols.ASSIGN.string)) {
                addNode(parent);
                if (!checkSum(pos - 1)) {
                    System.out.println("Очікувалась правильна права частина");
                    System.exit(-1);
                }
            } else {
                System.out.println("Очікувався знак \"" + Symbols.ASSIGN.string + "\"");
                System.exit(-1);
            }
        } else {
            System.out.println("Очікувався правильний ідентифікатор");
            System.exit(-1);
        }
    }

    public void addNode(int parent) {
        nodes.addNodeToTree(new Node(pos, current, parent));
        pos++;
        current = commands.get(pos);
    }

    public void printChild(Node row) {
        Tree temp = nodes.findByParent(row.getId());
        System.out.println(tab + row);
        tab += "\t";
        for (int i = 0; i < temp.sizeOfTree(); i++) {
            printChild(temp.getNode(i));
        }
        tab = tab.substring(1);
    }

}
