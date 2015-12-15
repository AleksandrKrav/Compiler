package analyzers;

/**
 * Определяеться как узел в дереве
 * @author Alexandr
 * @version 1.0
 */
public class Node {
    private int id;
    private String value;
    private int parentId;

    /**
     * @param id номер узла
     * @param value значение узла
     * @param parentId родитель узла
     */
    public Node(int id, String value, int parentId) {
        this.id = id;
        this.value = value;
        this.parentId = parentId;
    }

    public int getId() {
        return id;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParent(int parentId) {
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return ("{номер:" + id + " узел:\"" + value + "\" номер родителя:" + parentId + "}");
    }
}
