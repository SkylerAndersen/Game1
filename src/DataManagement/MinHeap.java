package DataManagement;

public class MinHeap <T> {
    private Node<T> head;
    private int size;
    private boolean printingOutput;
    public MinHeap () {
        printingOutput = false;
    }

    MinHeap (Node<T> head, int size) {
        this.head = head;
        this.size = size;
        printingOutput = false;
    }

    public int size () {
        return size;
    }

    public Pair<Integer,T> peek () {
        if (head == null)
            return null;

        return new Pair<>(head.getPriority(),head.getData());
    }

    public Pair<Integer,T> pop () {
        // simple cases
        if (size < 4)
            return simplePop();

        // get output
        Pair<Integer,T> output = new Pair<>(head.getPriority(),head.getData());

        // detach last element
        Node<T> lastElement = getLastNode();
        if (lastElement.getParent().getLeftChild() == lastElement)
            lastElement.getParent().setLeftChild(null);
        else
            lastElement.getParent().setRightChild(null);
        lastElement.setParent(null);

        // replace head with last element
        head.getLeftChild().setParent(lastElement);
        head.getRightChild().setParent(lastElement);
        lastElement.setLeftChild(head.getLeftChild());
        lastElement.setRightChild(head.getRightChild());
        head = lastElement;

        // filter down and do swap logic
        while ((lastElement.getLeftChild() != null) || (lastElement.getRightChild() != null)) {
            // get the smaller node
            boolean leftIsSmaller = lastElement.getLeftChild() != null &&
                    (lastElement.getRightChild() == null ||
                    lastElement.getRightChild().getPriority() >= lastElement.getLeftChild().getPriority());
            Node<T> smallerNode = leftIsSmaller ? lastElement.getLeftChild() : lastElement.getRightChild();

            // swap if necessary
            if (smallerNode.getPriority() < lastElement.getPriority()) {
                swapWithParent(smallerNode);
            } else {
                break;
            }
        }

        // decrement size and return output
        size--;
        return output;
    }

    private Pair<Integer,T> simplePop () {
        // case for 0
        if (size == 0)
            return null;

        // case for 3 or 2
        Pair<Integer,T> output = new Pair<>(head.getPriority(),head.getData());
        switch (size) {
            case 3:
                if (head.getLeftChild().getPriority() < head.getRightChild().getPriority()) {
                    head.getLeftChild().setParent(null);
                    head.getLeftChild().setLeftChild(head.getRightChild());
                    head.getRightChild().setParent(head.getLeftChild());
                    head = head.getLeftChild();
                    size--;
                    return output;
                } else {
                    head.getRightChild().setParent(null);
                    head.getRightChild().setLeftChild(head.getLeftChild());
                    head.getLeftChild().setParent(head.getRightChild());
                    head = head.getRightChild();
                    size--;
                    return output;
                }
            case 2:
                head.getLeftChild().setParent(null);
                head = head.getLeftChild();
                size--;
                return output;
        }

        // case for 1
        size--;
        head = null;
        return output;
    }

    public void push (int priority, T data) {
        Node<T> newNode = new Node<>(priority,data);
        if (head == null) {
            head = newNode;
            size++;
            return;
        }

        // add to the end
        Node<T> parent = (head.getLeftChild() == null || head.getRightChild() == null) ?
                head : getParentOfNextNode();
        if (parent.getLeftChild() == null)
            parent.setLeftChild(newNode);
        else if (parent.getRightChild() == null)
            parent.setRightChild(newNode);
        newNode.setParent(parent);

        // swap logic
        while (newNode.getParent() != null && newNode.getPriority() < newNode.getParent().getPriority())
            swapWithParent(newNode);
        size++;
    }

    private void swapWithParent (Node<T> node) {
        if (node.getParent() == null)
            return;

        // children's parent pointers updated
        if (node.getLeftChild() != null)
            node.getLeftChild().setParent(node.getParent());
        if (node.getRightChild() != null)
            node.getRightChild().setParent(node.getParent());

        // parent's parent's correct child pointer is updated
        if (node.getParent().getParent() != null) {
            if (node.getParent().getParent().getLeftChild() == node.getParent()) {
                node.getParent().getParent().setLeftChild(node);
            } else {
                node.getParent().getParent().setRightChild(node);
            }
        }

        // parents other child's parent pointer is updated
        if (node.getParent().getLeftChild() == node) {
            if (node.getParent().getRightChild() != null)
                node.getParent().getRightChild().setParent(node);
        } else {
            if (node.getParent().getLeftChild() != null)
                node.getParent().getLeftChild().setParent(node);
        }

        // store parent, left+right child, parent's other child, and if node is left or right child
        boolean isLeftChild = node.getParent().getLeftChild() == node;
        Node<T> parent = node.getParent();
        Node<T> leftChild = node.getLeftChild();
        Node<T> rightChild = node.getRightChild();
        Node<T> sibling = isLeftChild ? parent.getRightChild() : parent.getLeftChild();

        // update node's pointers child pointers
        if (isLeftChild) {
            node.setLeftChild(parent);
            node.setRightChild(sibling);
        } else {
            node.setRightChild(parent);
            node.setLeftChild(sibling);
        }

        // update node's parent pointer
        node.setParent(parent.getParent());

        // update parent's pointers
        parent.setParent(node);
        parent.setLeftChild(leftChild);
        parent.setRightChild(rightChild);

        // update head
        if (parent == head)
            head = node;
    }

    Node<T> getParentOfNextNode () {
        if (printingOutput)
            System.out.print("Size: "+size);
        if (size < 1) {
            if (printingOutput)
                System.out.println(" > null input");
            return null;
        }
        if (size == 1) {
            if (printingOutput)
                System.out.println(" > returning head!");
            return head;
        }

        // special case for if tree is currently perfect (size+1 is power of 2)
        if (( (size+1) & size ) == 0) {
            if (printingOutput)
                System.out.println(" > perfect tree > found on left!");
            return parentOfNextNodePerfectCase();
        }

        // get max depth (d=floor(log_2(n)))
        int maxDepth = (int)(Math.floor(Math.log(size)/Math.log(2)));

        // get ideal number in a perfect tree (solving sum of finite geometric series with r>1 => i=2^(d+1)-1)
        int idealSize = (2 << maxDepth)-1;

        // get ideal number of leaves (num leaves in a perfect binary tree)
        int idealNumLeaves = (maxDepth == 0) ? 1 : (2 << maxDepth-1);

        // get number of leaves we currently have in the max depth
        int missingLeaves = idealSize-size;
        int numLeavesInMaxDepth = idealNumLeaves-missingLeaves;

        // find the one node such that adding a child node will maintain the tree's completeness
        return parentOfNextRecursiveSearch(head,numLeavesInMaxDepth,idealNumLeaves);
    }

    private Node<T> parentOfNextNodePerfectCase () {
        Node<T> currNode = head;
        while (currNode.getLeftChild() != null) {
            currNode = currNode.getLeftChild();
        }
        return currNode;
    }

    private Node<T> parentOfNextRecursiveSearch (Node<T> currNode, int numLeavesInMaxDepth,
                                                 int idealNumLeaves) {
        if (currNode.getLeftChild() == null || currNode.getRightChild() == null) {
            if (printingOutput)
                System.out.println(" > found!");
            return currNode;
        }

        idealNumLeaves /= 2;
        if (numLeavesInMaxDepth >= idealNumLeaves) {
            if (printingOutput)
                System.out.print(" > right");
            return parentOfNextRecursiveSearch(currNode.getRightChild(),
                    numLeavesInMaxDepth - idealNumLeaves, idealNumLeaves);
        }
        if (printingOutput)
            System.out.print(" > left");
        return parentOfNextRecursiveSearch(currNode.getLeftChild(),numLeavesInMaxDepth,idealNumLeaves);
    }

    Node<T> getLastNode () {
        if (printingOutput)
            System.out.print("Size: "+size);
        if (size < 1) {
            if (printingOutput)
                System.out.println(" > null input");
            return null;
        }
        if (size == 1) {
            if (printingOutput)
                System.out.println(" > returning head!");
            return head;
        }

        // special case for if tree is currently perfect (size+1 is power of 2)
        if (( (size+1) & size ) == 0) {
            if (printingOutput)
                System.out.println(" > perfect tree > found on right!");
            return lastNodePerfectCase();
        }

        // get max depth (d=floor(log_2(n)))
        int maxDepth = (int)(Math.floor(Math.log(size)/Math.log(2)));

        // get ideal number in a perfect tree (solving sum of finite geometric series with r>1 => i=2^(d+1)-1)
        int idealSize = (2 << maxDepth)-1;

        // get ideal number of leaves (num leaves in a perfect binary tree)
        int idealNumLeaves = (maxDepth == 0) ? 1 : (2 << maxDepth-1);

        // get number of leaves we currently have in the max depth
        int missingLeaves = idealSize-size;
        int numLeavesInMaxDepth = idealNumLeaves-missingLeaves;

        // find the one node such that adding a child node will maintain the tree's completeness
        return lastNodeRecursiveSearch(head,numLeavesInMaxDepth,idealNumLeaves);
    }

    private Node<T> lastNodePerfectCase () {
        Node<T> currNode = head;
        while (currNode.getRightChild() != null) {
            currNode = currNode.getRightChild();
        }
        return currNode;
    }

    private Node<T> lastNodeRecursiveSearch (Node<T> currNode, int numLeavesInMaxDepth,
                                                 int idealNumLeaves) {
        if (currNode.getLeftChild() == null && currNode.getRightChild() == null) {
            if (printingOutput)
                System.out.println(" > found!");
            return currNode;
        }

        idealNumLeaves /= 2;
        if (numLeavesInMaxDepth > idealNumLeaves) {
            if (printingOutput)
                System.out.print(" > right");
            return lastNodeRecursiveSearch(currNode.getRightChild(),
                    numLeavesInMaxDepth - idealNumLeaves, idealNumLeaves);
        }
        if (printingOutput)
            System.out.print(" > left");
        return lastNodeRecursiveSearch(currNode.getLeftChild(),numLeavesInMaxDepth,idealNumLeaves);
    }

    void setDebugMode (boolean status) {
        printingOutput = status;
    }

    Node<T> getHead () {
        return head;
    }
}
