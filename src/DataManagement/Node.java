package DataManagement;

class Node <T> {
    private T data;
    private int priority;
    private Node<T> leftChild;
    private Node<T> rightChild;
    private Node<T> parent;
    public Node (int priority, T data) {
        this.priority = priority;
        this.data = data;
    }

    public void setLeftChild (Node<T> leftChild) {
        this.leftChild = leftChild;
    }

    public void setRightChild (Node<T> rightChild) {
        this.rightChild = rightChild;
    }

    public void setParent (Node<T> parent) {
        this.parent = parent;
    }

    public Node<T> getLeftChild () {
        return leftChild;
    }

    public Node<T> getRightChild () {
        return rightChild;
    }

    public Node<T> getParent () {
        return parent;
    }

    public int getPriority () {
        return priority;
    }

    public T getData () {
        return data;
    }
}
