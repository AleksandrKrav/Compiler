package analyzers;

import java.util.ArrayList;

/**
 * Дерево для компилятора
 *
 * @author Alexandr
 * @version 1.0
 */
public class Tree {

    private Node[] nodes;

    public Tree() {
        nodes = new Node[0];
    }

    /**
     * Запись узлов в дерево
     *
     * @param nodes параметр узлов
     */
    public Tree(ArrayList nodes) {
        this.nodes = new Node[nodes.size()];
        for (int i = 0; i < nodes.size(); i++) {
            this.nodes[i] = (Node) nodes.get(i);
        }
    }

    /**
     * Вытаскиваем узел
     *
     * @param id номер узла
     * @return узел дерева
     */
    public Node getNode(int id) {
        return nodes[id];
    }

    /**
     * Добавление узла в дерево
     *
     * @param node узел
     */
    public void addNodeToTree(Node node) {
        Node[] temp = new Node[this.nodes.length + 1];
        for (int i = 0; i < this.nodes.length; i++) {
            temp[i] = this.nodes[i];
        }
        temp[this.nodes.length] = node;
        this.nodes = temp;
    }

    /**
     * Удаляем узел в дереве по его номеру
     *
     * @param treeId номер дерева
     * @return новое дерево с принадлежащими узлами
     */
    public void removeNodeFromTree(int treeId) {
        Node[] temp = new Node[nodes.length - 1];
        int k = 0;
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i].getId() != treeId) {
                temp[k] = nodes[i];
                k++;
            }
        }
        nodes = new Node[temp.length];
        nodes = temp;
    }

    /**
     * Ищем дерево по его номеру
     *
     * @param treeId номер дерева
     * @return новое дерево с принадлежащими узлами
     */
    public Tree findTreeById(int treeId) {
        ArrayList<Node> temp = new ArrayList<>();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i].getId() == treeId) {
                temp.add(nodes[i]);
            }
        }
        return new Tree(temp);
    }

    /**
     * Ищем дерево по номеру родителя
     *
     * @param parentId номер дерева
     * @return новое дерево с принадлежащими узлами
     */
    public Tree findByParent(int parentId) {
        ArrayList<Node> temp = new ArrayList<>();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i].getParentId() == parentId) {
                temp.add(nodes[i]);
            }
        }
        return new Tree(temp);
    }

    /**
     *
     * @return Размер дерева
     */
    public int sizeOfTree() {
        return nodes.length;
    }


    @Override
    public String toString() {
        if (nodes.length == 0) {
            return "Совпадений не найдено\n";
        }
        if (nodes.length == 1) {
            return nodes[0].toString() + "\n";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < nodes.length; i++) {
            if (i != (nodes.length - 1)) {
                sb.append(sb);
                sb.append(nodes[i].toString());
                sb.append(", ");
            } else {
                sb.append(sb);
                sb.append(nodes[i].toString());
            }
        }
        sb.append(sb);
        sb.append("}\n");
        return sb.toString();
    }
}
