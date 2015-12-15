package analyzers;

import enums.Symbols;
//import enums.TypesOfNumber;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Simantic {
    int funcGlobalBegin;
    int funcBegin;
    Map<String, Double> real = new HashMap<>();
    Map<String, Integer> integer = new HashMap<>();
    Map<String, Integer> local = new HashMap<>();
    Tree nodes;
    Tree nodes2;
    ArrayList<Integer> root = new ArrayList<>();
    final ArrayList<Node> tree = new ArrayList<>();
    ArrayList<Integer> idVarTypes = new ArrayList<>();
    ArrayList<Integer> idBeginTypes = new ArrayList<>();
    int blockEnd;

    public int getFuncBegin() {
        return funcBegin;
    }

    //    int parentId;
    Pattern pattern = Pattern.compile("[a-zA-Z]+");
    Matcher matcher;

    public Simantic(Tree nodes, Tree nodes2) {
        this.nodes = nodes;
        this.nodes2 = nodes2;
    }

    public void intialization() {
        for (int i = 0; i < nodes.sizeOfTree(); i++) {
            if (nodes.getNode(i).getParentId() == -1) {
                root.add(i);
            }
            Node rot = new Node(nodes.getNode(i).getId(), nodes.getNode(i).getValue(), nodes.getNode(i).getParentId() );
            tree.add(rot);
        }

        funcBegin = root.get(1);
        for (int i = 0; i < nodes.sizeOfTree(); i++) {
            if (nodes.getNode(i).getParentId() == 0) {
                idVarTypes.add(i);
            }
            if (nodes.getNode(i).getParentId() == funcBegin) {
                idBeginTypes.add(i);
            }
        }
        for (int i = 0; i < idVarTypes.size(); i += 2) {
            Tree temp = nodes.findByParent(idVarTypes.get(i));
            initilize(temp);
        }
    }

    public void simanticParse(int subParentId) {

        idBeginTypes = new ArrayList<>();
        for (int i = 0; i < nodes.sizeOfTree(); i++) {
            if (nodes.getNode(i).getParentId() == subParentId) {
                idBeginTypes.add(i);
            }
        }
        final ArrayList<Node> r = new ArrayList<>();
        for (int i = 0; i < nodes2.sizeOfTree(); i++) {
            r.add(new Node(nodes2.getNode(i).getId(), nodes2.getNode(i).getValue(), nodes2.getNode(i).getParentId()));
        }
        final Tree t2 = new Tree(r);
//        t2 = nodes;
        for (int i = 0; i < idBeginTypes.size(); i += 2) {
            int d = subParentId;
            String k = "";
            boolean b = false;

            if (nodes.findTreeById(idBeginTypes.get(i)).getNode(0).getValue().equals("while")) {
                int whileId = idBeginTypes.get(i);
                d = idBeginTypes.get(i + 1);
                while (b = parseContition(nodes.findByParent(whileId).getNode(0))) {
                    final ArrayList<Node> f = new ArrayList<>();
                    for (int l = 0; l < nodes2.sizeOfTree(); l++) {
                        f.add(new Node(t2.getNode(l).getId(), t2.getNode(l).getValue(), t2.getNode(l).getParentId()));
                    }

                    nodes = new Tree(f);
//                    System.out.println(b);

                    if (nodes.findTreeById(d + 1).getNode(0).getValue().equals("begin")) {
//                        for (int j = 0; j < nodes.findByParent(idBeginTypes.get(d) + 1).sizeOfTree(); j += 2) {
                        simanticParse(d + 1);
//                            k = parseTable2(nodes.findByParent(idBeginTypes.get(d) + 1).getNode(j), idBeginTypes.get(d) + 1);
//                        }
                    } else {
                        parseTable2(nodes.findByParent(d).getNode(0), d);
                    }
//                    for (Map.Entry<String, Integer> m : integer.entrySet()) {
//                        System.out.println(m.getValue() + " " + m.getValue());
//                    }

                }
            } else if (nodes.findTreeById(idBeginTypes.get(i)).getNode(0).getValue().equals("if")) {
                b = parseContition(nodes.findByParent(idBeginTypes.get(i)).getNode(0));
                System.out.println(b);
                d = i + 1;
                if (b) {
                    if (nodes.findTreeById(idBeginTypes.get(d) + 1).getNode(0).getValue().equals("begin")) {
//                        for (int j = 0; j < nodes.findByParent(idBeginTypes.get(d) + 1).sizeOfTree(); j += 2) {
                        simanticParse(d + 1);
//                            k = parseTable2(nodes.findByParent(idBeginTypes.get(d) + 1).getNode(j), idBeginTypes.get(d) + 1);
//                        }
                    } else {
                        parseTable2(nodes.findByParent(idBeginTypes.get(d)).getNode(0), idBeginTypes.get(d));
                    }
                }
                if (idBeginTypes.size() > (i + 2)) {
                    if (nodes.findTreeById(idBeginTypes.get(i + 2)).getNode(0).getValue().equals("else")) {
                        d = i + 2;
                        i += 1;
                        if (!b) {
                            if (nodes.findTreeById(idBeginTypes.get(d) + 1).getNode(0).getValue().equals("begin")) {
                                for (int j = 0; j < nodes.findByParent(idBeginTypes.get(d) + 1).sizeOfTree(); j += 2) {
                                    simanticParse(d + 1);
//                                    k = parseTable2(nodes.findByParent(idBeginTypes.get(d) + 1).getNode(j), idBeginTypes.get(d) + 1);
                                }
                            } else {
                                parseTable2(nodes.findByParent(idBeginTypes.get(d)).getNode(0), idBeginTypes.get(d));
                            }
                        }
                    }
                }
            } else if (nodes.findTreeById(idBeginTypes.get(i)).getNode(0).getValue().equals("for")) {
                Tree tFor = nodes.findByParent(idBeginTypes.get(i));
                String[] cond = parseFor(tFor.getNode(0));
                String start = cond[1];
                local.put(cond[0], Integer.valueOf(cond[1]));
                int idTo = idBeginTypes.get(i + 1);
                Node row = nodes.findByParent(idTo).getNode(0);
                String finish = parseTo(row);
                int idDO = idBeginTypes.get(i + 2);
                i += 2;
                for (int j = Integer.parseInt(start); j < Integer.parseInt(finish) + 1; j++) {
                    local.put(cond[0], j);
                    final ArrayList<Node> f = new ArrayList<>();
                    for (int l = 0; l < nodes2.sizeOfTree(); l++) {
                        f.add(new Node(t2.getNode(l).getId(), t2.getNode(l).getValue(), t2.getNode(l).getParentId()));
                    }
                    nodes = new Tree(f);
                    d = idDO;
                    if (nodes.findTreeById(d + 1).getNode(0).getValue().equals("begin")) {
//                        for (int l = 0; l < nodes.findByParent(d + 1).sizeOfTree(); l += 2) {
                            simanticParse(d + 1);
//                            k = parseTable2(nodes.findByParent(d + 1).getNode(l), d + 1);
//                        }
                    } else {
                        parseTable2(nodes.findByParent(d).getNode(0), d);
                    }
                }
                local.remove(cond[0]);
            } else if (nodes.findTreeById(idBeginTypes.get(i)).getNode(0).getValue().equals(":=")) {
                k = parseTable2(nodes.findTreeById(idBeginTypes.get(i)).getNode(0), subParentId);
            } else if (nodes.findTreeById(idBeginTypes.get(i)).getNode(0).getValue().equals("writeln")) {
                System.out.println(parseValue(nodes.findByParent(idBeginTypes.get(i)).getNode(0).getValue()));
                i -= 1;
            } else {
                return;
            }

//                System.out.println(k);
        }
        for (Map.Entry<String, Integer> m : integer.entrySet()) {
            System.out.println(m.getKey() + " " + m.getValue());
        }
        for (Map.Entry<String, Double> m : real.entrySet()) {
            System.out.println(m.getKey() + " " + m.getValue());
        }
    }

    void initilize(Tree t) {
        String type = t.getNode(t.sizeOfTree() - 1).getValue();
        for (int i = 0; i < t.sizeOfTree() - 1; i++) {
            String name = t.getNode(i).getValue();
            if (type.equals("real"))
                real.put(name, 0.0);
            else
                integer.put(name, 0);
        }
    }

    void logic(Node table, Node parent) {

    }

    String[] parseFor(Node rowFor) {
        String[] result = new String[2];
        Tree tFor = nodes.findByParent(rowFor.getId());
        result[0] = tFor.getNode(0).getValue();
        result[1] = parseValue(tFor.getNode(1).getValue());
        return result;
    }

    String parseTo(Node rowFor) {
//        Tree tFor = nodes.findByParent(rowFor.getId());
        return parseValue(rowFor.getValue());
    }

    boolean parseContition(Node conditionRow) {
        boolean result = false;
        String condition = conditionRow.getValue();
        Tree t = nodes.findByParent(conditionRow.getId());
        String left = parseTable2(t.getNode(0), conditionRow.getId());
        String right = parseTable2(t.getNode(1), conditionRow.getId());
        Double leftValue = Double.parseDouble(parseValue(left));
        Double rightValue = Double.parseDouble(parseValue(right));
        if (condition.equals(Symbols.LESS.string)) {
            result = leftValue < rightValue;
        } else if (condition.equals(Symbols.MORE.string)) {
            result = leftValue > rightValue;
        } else if (condition.equals(Symbols.MORE_EQUAL.string)) {
            result = leftValue >= rightValue;
        } else if (condition.equals(Symbols.LESS_EQUAL.string)) {
            result = leftValue <= rightValue;
        } else if (condition.equals((Symbols.NOT_EQUAL))) {
            result = leftValue != rightValue;
        }
        return result;
    }

    String parseValue(String value) {
        String leftName;
        Number leftValue = null;
        matcher = pattern.matcher(value);
        if (matcher.matches()) {
            if (integer.containsKey(value)) { // есть в мапе интежеров
                leftName = value;
                leftValue = integer.get(leftName);
            } else if (local.containsKey(value)) {
                leftName = value;
                leftValue = local.get(leftName);
            } else if (real.containsKey(value)) { // есть в мапе real
                leftName = value;
                leftValue = real.get(leftName);
            } else {
                System.out.println("невідома змінна " + value);
                System.exit(-1);
            }
        } else {
            if (value.contains(".")) {
                leftValue = Double.parseDouble(value);
            } else {
                leftValue = Integer.parseInt(value);
            }
        }
        return leftValue.toString();
    }

    String parseTable2(Node table, int finish) {
        Tree t = nodes.findByParent(table.getId());
        Node parent;
        if (t.sizeOfTree() > 0) {
            if (nodes.findByParent(t.getNode(0).getId()).sizeOfTree() > 0) {
                parseTable2(t.getNode(0), finish);
            } else if (nodes.findByParent(t.getNode(1).getId()).sizeOfTree() > 0) {
                parseTable2(t.getNode(1), finish);
            } else parseTable2(t.getNode(0), finish);
        } else if (table.getParentId() == finish) {
            return table.getValue();
        } else {
            Node childTable = nodes.findTreeById(table.getParentId()).getNode(0);
            Tree childList = nodes.findByParent(childTable.getId());
            String s;
            if (childList.getNode(0) == table) {
                if (nodes.findByParent(childList.getNode(1).getId()).sizeOfTree() > 0) {
                    parseTable2(childTable, finish);
                    return childTable.getValue();
                }
                s = mathOperation(table, nodes.findByParent(table.getParentId()).getNode(1), nodes.findTreeById(table.getParentId()).getNode(0).getValue());
            } else {
                s = mathOperation(nodes.findByParent(table.getParentId()).getNode(0), table, nodes.findTreeById(table.getParentId()).getNode(0).getValue());
            }
            parent = nodes.findTreeById(table.getParentId()).getNode(0);
            parent.setValue(s);
            int childParent = table.getParentId();
            nodes.removeNodeFromTree(nodes.findByParent(childParent).getNode(0).getId());
            nodes.removeNodeFromTree(nodes.findByParent(childParent).getNode(0).getId());

            parseTable2(parent, finish);

        }
        return table.getValue();
    }

    //функция для проверки ли это листья
    String parseTable(Node table) {
        Tree t = nodes.findByParent(table.getId());
        Node parent;
        if (t.sizeOfTree() > 0) {
            if (nodes.findByParent(t.getNode(0).getId()).sizeOfTree() > 0) {
                parseTable(t.getNode(0));
            } else if (nodes.findByParent(t.getNode(1).getId()).sizeOfTree() > 0) {
                parseTable(t.getNode(1));
            } else parseTable(t.getNode(0));
        } else if (table.getParentId() == funcBegin) {
            return table.getValue();
        } else {
            Node childTable = nodes.findTreeById(table.getParentId()).getNode(0);
            Tree childList = nodes.findByParent(childTable.getId());
            String s;
            if (childList.getNode(0) == table) {
                if (nodes.findByParent(childList.getNode(1).getId()).sizeOfTree() > 0) {
                    parseTable(childTable);
                    return childTable.getValue();
                }
                s = mathOperation(table, nodes.findByParent(table.getParentId()).getNode(1), nodes.findTreeById(table.getParentId()).getNode(0).getValue());
            } else {
                s = mathOperation(nodes.findByParent(table.getParentId()).getNode(0), table, nodes.findTreeById(table.getParentId()).getNode(0).getValue());
            }
            parent = nodes.findTreeById(table.getParentId()).getNode(0);
            parent.setValue(s);
            int childParent = table.getParentId();
            nodes.removeNodeFromTree(nodes.findByParent(childParent).getNode(0).getId());
            nodes.removeNodeFromTree(nodes.findByParent(childParent).getNode(0).getId());

            parseTable(parent);

        }
        return table.getValue();
    }

    String mathOperation(Node left, Node right, String key) {
        String result = null;
        Number leftValue = 0;
        Number rightValue = 0;
        String leftName = "";
        String rightName;

        matcher = pattern.matcher(left.getValue());
        if (matcher.matches()) {
            if (integer.containsKey(left.getValue())) { // есть в мапе интежеров
                leftName = left.getValue();
                leftValue = integer.get(leftName);
            } else if (local.containsKey(left.getValue())) {
                leftName = left.getValue();
                leftValue = local.get(leftName);
            } else if (real.containsKey(left.getValue())) { // есть в мапе real
                leftName = left.getValue();
                leftValue = real.get(leftName);
            } else {
                leftName = left.getValue();
                System.out.println("Невідома змінна \"" + leftName + "\"");
            }
        } else {
            if (left.getValue().contains(".")) {
                leftValue = Double.parseDouble(left.getValue());
            } else {
                leftValue = Integer.parseInt(left.getValue());
            }
        }
        matcher = pattern.matcher(right.getValue());
        if (matcher.matches()) {
            if (integer.containsKey(right.getValue())) { // есть в мапе интежеров
                rightName = right.getValue();
                rightValue = integer.get(rightName);
            } else if (local.containsKey(right.getValue())) {
                rightName = right.getValue();
                rightValue = local.get(rightName);
            } else if (real.containsKey(right.getValue())) { // есть в мапе real
                rightName = right.getValue();
                rightValue = real.get(rightName);
            } else {
                rightName = right.getValue();
                System.out.println("Невідома змінна - \"" + rightName + "\"");
            }
        } else {
            if (right.getValue().contains(".")) {
                rightValue = Double.parseDouble(right.getValue());
            } else {
                rightValue = Integer.parseInt(right.getValue());
            }
        }

        if (key.equals(":=") && (!leftName.equals(""))) {
            if ((leftValue instanceof Integer) && (rightValue instanceof Integer)) {
                leftValue = rightValue;
                integer.put(leftName, (Integer) leftValue);
                result = leftValue.toString();
                return result;
            } else if ((leftValue instanceof Double) && (rightValue instanceof Double)) {
                leftValue = rightValue;
                real.put(leftName, (Double) leftValue);
                result = leftValue.toString();
                return result;
            } else {
                System.out.println("Невірне приведення типів ");
                System.exit(-1);
            }
        } else if (key.equals("+")) {
            if ((leftValue instanceof Integer) && (rightValue instanceof Integer)) {
                leftValue = (Integer) leftValue + (Integer) rightValue;
                result = leftValue.toString();
                return result;
            } else if ((leftValue instanceof Double) && (rightValue instanceof Double)) {
                leftValue = (Double) leftValue + (Double) rightValue;
                result = leftValue.toString();
                return result;
            } else {
                leftValue = Double.parseDouble(leftValue.toString()) + Double.parseDouble(rightValue.toString());
                result = leftValue.toString();
                return result;
            }
        } else if (key.equals("*")) {
            if ((leftValue instanceof Integer) && (rightValue instanceof Integer)) {
                leftValue = (Integer) leftValue * (Integer) rightValue;
                result = leftValue.toString();
                return result;
            } else {
                leftValue = Double.parseDouble(leftValue.toString()) * Double.parseDouble(rightValue.toString());
                result = leftValue.toString();
                return result;
            }
        } else if (key.equals("/")) {
            leftValue = Double.valueOf(leftValue.toString()) / Double.valueOf(rightValue.toString());
            result = leftValue.toString();
            return result;
        }else if (key.equals("mod")) {
            if ((leftValue instanceof Integer) && (rightValue instanceof Integer)) {
                leftValue = (Integer) leftValue % (Integer) rightValue;
                result = leftValue.toString();
                return result;
            }else{
                System.out.println("Невірний тип даних для виконная операції mod");
                System.exit(-1);
            }
        }
        return result;
    }


    public void printResult(String fileName) {
        File file = new File(fileName);
        StringBuilder sb = new StringBuilder();
        String s;
        for (int i = 0; i < tree.size(); i++) {
            s = tree.get(i).getValue();
            if (s.equals(":")) {
                sb.append("тип " + " ");
            } else if (s.equals("integer")) {
                sb.append("integer ");
            } else if (s.equals(";")) {
                sb.append("\n");
            } else if (s.equals("real")) {
                sb.append("real ");
            } else if (s.equals("begin")) {
                if (tree.get(i).getParentId() == -1) {
                    sb.append("Початок програми" + "\n");
                } else sb.append("Початок блоку" + "\n");
            } else if (s.equals("end")) {
                if (tree.get(i).getParentId() == -1) {
                    sb.append("Кінець програми" + "\n");
                } else sb.append("Кінець блоку");
            } else if (s.equals("writeln")){
                sb.append("Вивід на екран: " + parseValue(tree.get(i+1).getValue()) + "\n");
                i++;
            } else if (s.equals(Symbols.ASSIGN.string)){
                sb.append("присвоїти ");
            } else if (s.equals("+")){
                sb.append("додати ");
            } else if (s.equals("-")){
                sb.append("відняти ");
            } else if (s.equals("/")){
                sb.append("поділити ");
            } else if (s.equals("*")){
                sb.append("помножити ");
            } else if (s.equals("=")){
                sb.append("дорівнює ");
            } else if (s.equals(">")){
                sb.append("більше ");
            }  else if (s.equals("<")){
                sb.append("менше ");
            } else if (s.equals(">=")){
                sb.append("більше або дорівнює ");
            } else if (s.equals("<=")){
                sb.append("менше або дорівнює ");
            } else if (s.equals("<>")){
                sb.append("не дорівнює ");
            } else if (s.equals("if")){
                sb.append("Якщо ");
            } else if (s.equals("then")){
                sb.append("то \n\t");
            } else if (s.equals("else")){
                sb.append("інакше \n");
            } else if (s.equals("for")){
                sb.append("від ");
            } else if (s.equals("to")){
                sb.append("до ");
            } else if (s.equals("do")){
                sb.append("виконати \n");
            } else if (s.equals("while")){
                sb.append("доки ");
            } else if (s.equals("readln")){
                sb.append("зчитати з консолі змінну : ");
            } else if(s.equals(".")) {
                continue;
            } else if(s.equals("var")) {
                sb.append("Змінні : \n");
            }else{
             sb.append(s + " ");}
        }


        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            PrintWriter out = new PrintWriter(file.getAbsoluteFile());

            try {
                out.print(sb.toString());
            } finally {
                out.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}