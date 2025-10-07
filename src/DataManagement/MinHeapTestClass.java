package DataManagement;

import java.util.Random;

public class MinHeapTestClass {
    private static boolean logSearchPaths;
    public static boolean test () {
        boolean status = testGetParentOfNextNode(logSearchPaths);
        status = status && testGetLastNode(logSearchPaths);
        status = status && testPush();
        status = status && testPop();

        return status;
    }

    public static boolean testPop () {
        if (logSearchPaths)
            System.out.print("""
                    ====================================================
                      Testing MinHeap.pop()
                       Requ.: Assumes that MinHeap.push() works.
                       Desc.: For 100 iterations, generates
                              random unbounded values from a fixed
                              seed and pushes or pops them to a MinHeap.
                              Further tests 100 successive pushes
                              followed by 100 successive pops.
                              Checks if the heap property is
                              maintained for each node after each
                              push or pop. Further checks that references
                              to parents and children remain intact.
                    ====================================================
                    """);

        Random rand = new Random();
        rand.setSeed(12045231);
        MinHeap<String> minHeap = new MinHeap<>();
        boolean status = true;

        // random pushes then pops
        for (int i = 0; i < 100; i++) {
            int val = Math.abs(rand.nextInt());
            if (val % 2 == 0) {
                minHeap.push(val, null);
                if (logSearchPaths) {
                    System.out.printf("Pushed value: %d, Printing in order: ", val);
                    printInOrder(minHeap.getHead());
                }
            } else {
                val = (minHeap.size() == 0) ? -1 : minHeap.pop().getVal1();
                if (logSearchPaths) {
                    System.out.printf("Popped value: %d, Printing in order: ", val);
                    printInOrder(minHeap.getHead());
                }
            }
            if (!passesHeapProperty(minHeap)) {
                if (logSearchPaths)
                    System.out.println("ERROR: ^ Above Failed. Not passing heap property ^");
                status = false;
            }
        }

        // 100 successive
        for (int i = 0; i < 100; i++) {
            minHeap.push(Math.abs(rand.nextInt()), null);
        }
        if (logSearchPaths)
            System.out.println("Pushed 100 vals successively!");
        if (!passesHeapProperty(minHeap)) {
            if (logSearchPaths)
                System.out.println("ERROR: ^ Above Failed. Not passing heap property ^");
            status = false;
        }
        for (int i = 0; i < 100; i++) {
            int val = minHeap.pop().getVal1();
            char size = (val >= 1000000000) ? 'B' : (val >= 1000000) ? 'M' : (val >= 1000) ? 'K' : 'd';
            double outVal = (val >= 1000000000) ? val/1000000000.0 : (val >= 1000000) ? val/1000000.0 :
                    (val >= 1000) ? val/1000.0 : val;
            String stringVal = (size == 'd') ? String.format("%.2f",outVal) :
                    String.format("%.2f%c",outVal,size);

            if (logSearchPaths) {
                System.out.printf("Popped value: %s, Printing in order: ", stringVal);
                printInOrder(minHeap.getHead());
            }

            if (!passesHeapProperty(minHeap)) {
                if (logSearchPaths)
                    System.out.println("ERROR: ^ Above Failed. Not passing heap property ^");
                status = false;
            }
        }



        if (logSearchPaths)
            System.out.printf("""
                    ====================================================
                      Testing MinHeap.pop()
                       Requ.: Assumes that MinHeap.push() works.
                       Desc.: For 100 iterations, generates
                              random unbounded values from a fixed
                              seed and pushes or pops them to a MinHeap.
                              Further tests 100 successive pushes
                              followed by 100 successive pops.
                              Checks if the heap property is
                              maintained for each node after each
                              push or pop. Further checks that references
                              to parents and children remain intact.
                       Stat.: %b
                    ====================================================
                    """,status);

        return status;
    }

    public static boolean testPush () {
        if (logSearchPaths)
            System.out.print("""
                    ====================================================
                      Testing MinHeap.push()
                       Desc.: For 100 iterations, generates
                              random unbounded values from a fixed
                              seed and pushes them to a MinHeap.
                              Checks if the heap property is
                              maintained for each node after each
                              push. Further checks that references
                              to parents and children remain intact.
                    ====================================================
                    """);

        Random rand = new Random();
        rand.setSeed(12045231);
        MinHeap<String> minHeap = new MinHeap<>();

        boolean status = true;
        for (int i = 0; i < 100; i++) {
            int val = Math.abs(rand.nextInt());
            minHeap.push(val,null);
            if (logSearchPaths) {
                System.out.printf("Pushed value: %d, Printing in order: ",val);
                printInOrder(minHeap.getHead());
            }
            if (!passesHeapProperty(minHeap)) {
                if (logSearchPaths)
                    System.out.println("ERROR: ^ Above Failed. Not passing heap property ^");
                status = false;
            }
        }

        if (logSearchPaths)
            System.out.printf("""
                    ====================================================
                      END: <Testing MinHeap.push()>
                       Desc.: For 100 iterations, generates
                              random unbounded values from a fixed
                              seed and pushes them to a MinHeap.
                              Checks if the heap property is
                              maintained for each node after each
                              push. Further checks that references
                              to parents and children remain intact.
                       Stat.: %b
                    ====================================================
                    """,status);

        return status;
    }

    private static boolean passesHeapProperty (MinHeap<String> minHeap) {
        if (minHeap == null)
            return false;

        if (minHeap.getHead() == null) {
            if (minHeap.size() == 0)
                return true;
            else
                return false;
        }

        int[] nodesSeen = {0};
        boolean status = recursivePassesHeapProperty(minHeap.getHead(),nodesSeen);
        return (nodesSeen[0] == minHeap.size()) && status;
    }

    private static boolean recursivePassesHeapProperty (Node<String> tree, int[] nodesSeen) {
        nodesSeen[0]++;

        // does this pass the heap property
        boolean leftPasses = (tree.getLeftChild() == null ||
                (tree.getPriority() <= tree.getLeftChild().getPriority()));
        boolean rightPasses = (tree.getRightChild() == null ||
                (tree.getPriority() <= tree.getRightChild().getPriority()));
        boolean parentPasses = (tree.getParent() == null ||
                (tree.getPriority() >= tree.getParent().getPriority()));
        boolean status = leftPasses && rightPasses && parentPasses;

        // recursive movement
        if (tree.getLeftChild() != null)
            status = status && recursivePassesHeapProperty(tree.getLeftChild(),nodesSeen);
        if (tree.getRightChild() != null)
            status = status && recursivePassesHeapProperty(tree.getRightChild(),nodesSeen);

        return status;
    }

    private static boolean testGetLastNode (boolean debugMode) {
        if (logSearchPaths)
            System.out.print("""
                    =============================================
                      Testing MinHeap.getLastNode()
                       Desc.: For sizes 2-100 on pre-generated
                              trees, checks if MinHeap can
                              accurately find the last node
                              when searching top to bottom,
                              left to right (i.e. as one
                              reads).
                    =============================================
                    """);

        boolean status = true;
        for (int i = 2; i < 100; i++) {
            Node<String> tree = createCompleteTree(i);
            MinHeap<String> minHeap = new MinHeap<>(tree,i);
            minHeap.setDebugMode(debugMode);
            Node<String> lastNodeNode = minHeap.getLastNode();
            int lastVal = (lastNodeNode == null) ? -1 : lastNodeNode.getPriority();
            if (lastVal != i) {
                System.out.printf("ERROR: ^ For above Size: %d, Last Val: %d, Correct Val: %d ^\n",
                        i, lastVal, i);
                status = false;
            }
        }

        if (logSearchPaths)
            System.out.printf("""
                    =============================================
                      END: <Testing MinHeap.getLastNode()>
                       Desc.: For sizes 2-100 on pre-generated
                              trees, checks if MinHeap can
                              accurately find the last node
                              when searching top to bottom,
                              left to right (i.e. as one
                              reads).
                       Stat.: %b
                    =============================================
                    """,status);

        return status;
    }

    private static boolean testGetParentOfNextNode (boolean debugMode) {
        if (logSearchPaths)
            System.out.print("""
                    =============================================
                      Testing MinHeap.getParentOfNextNode()
                       Desc.: For sizes 2-100 on pre-generated
                              trees, checks if MinHeap can
                              accurately find the parent for
                              which an inserted child node will
                              not disrupt completeness.
                    =============================================
                    """);

        boolean status = true;
        for (int i = 2; i < 100; i++) {
            Node<String> tree = createCompleteTree(i);
            MinHeap<String> minHeap = new MinHeap<>(tree,i);
            minHeap.setDebugMode(debugMode);
            Node<String> parentOfNextNodeNode = minHeap.getParentOfNextNode();
            int parentOfNext = (parentOfNextNodeNode == null) ? -1 : parentOfNextNodeNode.getPriority();
            int correctVal = (i+1)/2;
            if (parentOfNext != correctVal) {
                System.out.printf("ERROR: ^ For above Size: %d, Parent of Next: %d, Correct Val: %d ^\n",
                        i, parentOfNext, correctVal);
                status = false;
            }
        }

        if (logSearchPaths)
            System.out.printf("""
                    ==================================================
                      END: <Testing MinHeap.getParentOfNextNode()>
                       Desc.: For sizes 2-100 on pre-generated
                              trees, checks if MinHeap can
                              accurately find the parent for
                              which an inserted child node will
                              not disrupt completeness.
                       Stat.: %b
                    ==================================================
                    """,status);

        return status;
    }

    public static void setLogSearchPaths (boolean logSearchPaths) {
        MinHeapTestClass.logSearchPaths = logSearchPaths;
    }

    public static void demoCreationMethod () {
        Node<String> testTree0 = createCompleteTree(0);
        Node<String> testTree1 = createCompleteTree(1);
        Node<String> testTree2 = createCompleteTree(2);
        Node<String> testTree3 = createCompleteTree(3);
        Node<String> testTree4 = createCompleteTree(4);
        Node<String> testTree5 = createCompleteTree(5);
        Node<String> testTree6 = createCompleteTree(6);
        Node<String> testTree7 = createCompleteTree(7);
        Node<String> testTree8 = createCompleteTree(8);
        Node<String> testTree9 = createCompleteTree(9);
        Node<String> testTree10 = createCompleteTree(10);

        printInOrder(testTree0);
        printInOrder(testTree1);
        printInOrder(testTree2);
        printInOrder(testTree3);
        printInOrder(testTree4);
        printInOrder(testTree5);
        printInOrder(testTree6);
        printInOrder(testTree7);
        printInOrder(testTree8);
        printInOrder(testTree9);
        printInOrder(testTree10);
    }

    public static void printInOrder (Node<String> tree) {
        if (tree == null) {
            System.out.println("null");
            return;
        }

        recursivePrintInOrder(tree);
        System.out.println();
    }

    private static void recursivePrintInOrder (Node<String> tree) {
        if (tree == null)
            return;

        recursivePrintInOrder(tree.getLeftChild());
        int val = tree.getPriority();
        char size = (val >= 1000000000) ? 'B' : (val >= 1000000) ? 'M' : (val >= 1000) ? 'K' : 'd';
        double outVal = (val >= 1000000000) ? val/1000000000.0 : (val >= 1000000) ? val/1000000.0 :
                (val >= 1000) ? val/1000.0 : val;
        String stringVal = (size == 'd') ? String.format("%.2f",outVal) :
                String.format("%.2f%c, ",outVal,size);
        System.out.print(stringVal);
        recursivePrintInOrder(tree.getRightChild());
    }

    private static Node<String> createCompleteTree (int size) {
        if (size == 0)
            return null;
        if (size == 1)
            return new Node<>(1,null);

        int maxDepth = (int)(Math.floor(Math.log(size)/Math.log(2)));
        int[] currOffsetByDepth = new int[maxDepth+1];
        return recursiveCreateCompleteTree(-1,currOffsetByDepth,maxDepth,size);
    }

    private static Node<String> recursiveCreateCompleteTree (int currDepth, int[] currOffsetByDepth,
                                                             int maxDepth, int size) {
        // update depth and count nodes above this one
        int numNodesAbove = (currDepth == -1) ? 0 : (2 << currDepth)-1;
        currDepth++;

        // check if this node is within expected depth/size
        if (currDepth > maxDepth)
            return null;
        int nodeNumber = numNodesAbove + (currOffsetByDepth[currDepth]++)+1;
        if (nodeNumber > size)
            return null;

        // create children
        Node<String> leftChild = recursiveCreateCompleteTree(currDepth,currOffsetByDepth,maxDepth,size);
        Node<String> rightChild = recursiveCreateCompleteTree(currDepth,currOffsetByDepth,maxDepth,size);

        // create subtree
        Node<String> output = new Node<>(nodeNumber,null);
        output.setLeftChild(leftChild);
        output.setRightChild(rightChild);
        if (leftChild != null)
            leftChild.setParent(output);
        if (rightChild != null)
            rightChild.setParent(output);

        return output;
    }
}
