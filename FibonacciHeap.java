
/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */
public class FibonacciHeap {
    public HeapNode first;
    public HeapNode min;
    public int roots;
    public int size;
    public int numMarks;
    public static int links;
    public static int cuts;


    public FibonacciHeap() {
        this.first = null;
        this.min = null;
        this.roots = 0;
        this.size = 0;
        this.numMarks = 0;
    }

    public HeapNode getFirst() {
        return this.first;
    }

    /**
     * public boolean isEmpty()
     * <p>
     * Returns true if and only if the heap is empty.
     */
    public boolean isEmpty() {
        return this.size == 0;
    }

    /**
     * public HeapNode insert(int key)
     * <p>
     * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
     * The added key is assumed not to already belong to the heap.
     * <p>
     * Returns the newly created node.
     */
    public HeapNode insert(int key) {
        HeapNode newNode = new HeapNode(key);
        if (this.isEmpty()) {
            this.first = newNode;
            this.min = newNode;
            this.roots = 1;
            this.size = 1;
            newNode.setPrev(newNode);
            newNode.setNext(newNode);
        } else {
            HeapNode temp = this.first;
            temp = temp.getPrev();
            temp.setNext(newNode);
            newNode.setPrev(temp);
            newNode.setNext(this.first);
            this.first.setPrev(newNode);
            this.roots++;
            this.size++;
            if (min.getKey() > key) {
                this.min = newNode;
            }
        }
        return newNode;
    }

    /**
     * public void deleteMin()
     * <p>
     * Deletes the node containing the minimum key.
     */
    public void deleteMin() {
        if (this.min.getRank() == 0) {
            if (this.min == this.first) {
                this.first = min.getNext();
             }
            this.min.getPrev().setNext(min.getNext());
            this.min.getNext().setPrev(min.getPrev());
            this.size--;
            this.roots--;
            this.SucLink();
        } else {
            HeapNode temp = min.getChild();
            temp.setNext(min.getNext());
            min.getNext().setPrev(temp);
            for (int i = 0; i < min.getRank(); i++) {
                temp.setParent(null);
                if(temp.getMarked()) {
                    temp.setMarked(false);
                    numMarks--;
                }
                if(i != min.getRank()-1) {
                    temp = temp.getPrev();
                }
            }
            if(this.min==this.first){
                this.first = temp;
            }
            temp.setPrev(min.getPrev());
            min.getPrev().setNext(temp);
            this.size--;
            this.roots--;
            this.SucLink();
        }
    }

    public void SucLink() {
        if (this.roots != 1 && !this.isEmpty()) {
            HeapNode[] basket = new HeapNode[(int) (Math.log(this.size) / Math.log(2)) + 1];
            HeapNode temp = this.first;
            for (int i = 0; i < roots-1; i++) {
                int rank = temp.getRank();
                if (basket[rank] == null) {
                    basket[rank] = temp;
                } else {
                    HeapNode linked = this.Link(temp, basket[rank]);
                    basket[rank] = null;
                    while (basket[linked.getRank()] != null) {
                        HeapNode curLink = basket[linked.getRank()];
                        basket[linked.getRank()] = null;
                        linked = this.Link(linked, curLink);
                    }
                    basket[linked.getRank()] = linked;
                }
                temp = temp.getNext();
            }
            HeapNode cur = null;
            this.roots = 0;
            int last = 0;
            for (int i = 0; i < basket.length; i++) {
                if (basket[i] != null) {
                    if (cur == null) {
                        cur = basket[i];
                        last = i;
                        this.min = cur;
                        this.roots++;
                    } else {
                        if (min.getKey() > basket[i].getKey()) {
                            this.min = basket[i];
                        }
                        cur.setPrev(basket[i]);
                        basket[i].setNext(cur);
                        cur = cur.getPrev();
                        this.roots++;
                    }
                }
            }
            basket[last].setNext(cur);
            cur.setPrev(basket[last]);
            this.first = cur;
        }
        else{
            if(this.isEmpty()){
                this.first=null;
                this.roots=0;
                this.numMarks=0;
                this.size=0;
                this.min=null;
            }
            else{
                this.first.setNext(this.first);
                this.first.setPrev(this.first);
                this.min=this.first;
            }
        }
    }

    public HeapNode Link(HeapNode root1, HeapNode root2) {
        if (root1.getKey() > root2.getKey()) { // We want root1 to point to the smaller key
            HeapNode temp = root1;
            root1 = root2;
            root2 = temp;
        }
        HeapNode child = root1.getChild();
        root1.setChild(root2);
        root2.setParent(root1);
        root2.getNext().setPrev(root2.getPrev());
        root2.getPrev().setNext(root2.getNext());
        if(child !=null) {
            root2.setNext(child.getNext());
            child.getNext().setPrev(root2);
            root2.setPrev(child);
            child.setNext(root2);
        }
        else {
            root2.setPrev(root2);
            root2.setNext(root2);
        }
        this.roots--;
        root1.setRank(root1.getRank() + 1);
        links++;
        return root1;
    }

    /**
     * public HeapNode findMin()
     * <p>
     * Returns the node of the heap whose key is minimal, or null if the heap is empty.
     */
    public HeapNode findMin() {
        return this.min;
    }

    /**
     * public void meld (FibonacciHeap heap2)
     * <p>
     * Melds heap2 with the current heap.
     */
    public void meld(FibonacciHeap heap2) {
        if (!this.isEmpty() && !heap2.isEmpty()) {
            HeapNode temp = this.first;
            while (temp.getNext() != this.first) {
                temp = temp.getNext();
            }
            this.first.setPrev(heap2.first.getPrev());
            heap2.first.getPrev().setNext(this.first);
            temp.setNext(heap2.first);
            heap2.first.setPrev(temp);
            if (this.min.getKey() > heap2.min.getKey()) {
                this.min = heap2.min;
            }
        } else {
            if (this.isEmpty()) {
                this.min = heap2.min;
            }
        }
        this.roots = this.roots + heap2.roots;
        this.size = this.size + heap2.size();
        this.numMarks = this.numMarks + heap2.numMarks;
        heap2.first = this.first;
        heap2.min = this.min;
        heap2.roots = this.roots;
        heap2.size = this.size;
        heap2.numMarks = this.numMarks;
    }

    /**
     * public int size()
     * <p>
     * Returns the number of elements in the heap.
     */
    public int size() {
        return this.size;
    }

    /**
     * public int[] countersRep()
     * <p>
     * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
     * (Note: The size of of the array depends on the maximum order of a tree.)
     */
    public int[] countersRep() {
        if(this.isEmpty()){
            return new int [0];
        }
        int[] arr = new int[this.size];
        int bigI = 0;
        HeapNode temp = this.first;
        arr[temp.getRank()]++;
        bigI = temp.getRank();
        temp = temp.getNext();
        while (temp != this.first) {
            arr[temp.getRank()]++;
            bigI = temp.getRank();
            temp = temp.getNext();
        }
        bigI++;
        int[] res = new int[bigI];
        System.arraycopy(arr, 0, res, 0, bigI);
        return res;
    }

    /**
     * public void delete(HeapNode x)
     * <p>
     * Deletes the node x from the heap.
     * It is assumed that x indeed belongs to the heap.
     */
    public void delete(HeapNode x) {
        decreaseKey(x, Integer.MAX_VALUE);
        deleteMin();
    }

    /**
     * public void decreaseKey(HeapNode x, int delta)
     * <p>
     * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
     * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
     */
    public void decreaseKey(HeapNode x, int delta) {
        if (delta == Integer.MAX_VALUE) {
            this.min = x;
            x.setKey(Integer.MIN_VALUE);
        } else {
            x.setKey(x.getKey() - delta);
            if (x.getKey() < min.getKey()) {
                this.min = x;
            }
        }
        HeapNode parent = x.getParent();
        if (parent != null && parent.getKey() > x.getKey()) {
            casCut(x, parent);
        }
    }

    public void cut(HeapNode x, HeapNode parent) {
        x.setParent(null);
        if(x.getMarked()) {
            x.setMarked(false);
            this.numMarks--;
        }
        this.roots++;
        parent.setRank(parent.getRank() - 1);
        if (parent.getChild() == x) {
            if (parent.getRank() > 0) {
                parent.setChild(x.getPrev());
            }
            else {
                parent.setChild(null);
            }
        }
        if(parent.getRank() >0) {
            x.getPrev().setNext(x.getNext());
            x.getNext().setPrev(x.getPrev());
        }
        x.setNext(parent.getNext());
        parent.getNext().setPrev(x);
        x.setPrev(parent);
        parent.setNext(x);
        cuts++;
    }

    public void casCut(HeapNode x, HeapNode parent) {
        cut(x, parent);
        if (parent.getParent() != null) {
            if (!parent.getMarked()) {
                parent.setMarked(true);
                this.numMarks++;
            } else {
                casCut(parent, parent.getParent());
            }
        }
    }


    /**
     * public int nonMarked()
     * <p>
     * This function returns the current number of non-marked items in the heap
     */
    public int nonMarked() {
        return (this.size - this.numMarks);
    }

    /**
     * public int potential()
     * <p>
     * This function returns the current potential of the heap, which is:
     * Potential = #trees + 2*#marked
     * <p>
     * In words: The potential equals to the number of trees in the heap
     * plus twice the number of marked nodes in the heap.
     */
    public int potential() {
        return (this.roots + 2 * this.numMarks);
    }

    /**
     * public static int totalLinks()
     * <p>
     * This static function returns the total number of link operations made during the
     * run-time of the program. A link operation is the operation which gets as input two
     * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
     * tree which has larger value in its root under the other tree.
     */
    public static int totalLinks() {
        return links;
    }

    /**
     * public static int totalCuts()
     * <p>
     * This static function returns the total number of cut operations made during the
     * run-time of the program. A cut operation is the operation which disconnects a subtree
     * from its parent (during decreaseKey/delete methods).
     */
    public static int totalCuts() {
        return cuts;
    }

    /**
     * public static int[] kMin(FibonacciHeap H, int k)
     * <p>
     * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
     * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
     * <p>
     * ###CRITICAL### : you are NOT allowed to change H.
     */
    public static int[] kMin(FibonacciHeap H, int k) {
        if(H.isEmpty()){
            return new int[0];
        }
        FibonacciHeap minHeap = new FibonacciHeap();
        int[] arr = new int[k];
        arr[0] = H.getFirst().getKey();
        HeapNode minNode = H.getFirst();
        for(int i=0; i<k -1; i++){
            HeapNode temp = minNode.getChild();
            while(temp.getPrev() != temp){
                minHeap.insert(temp.getKey());
                temp = temp.getPrev();
            }
            arr[i] = minHeap.min.getKey();
            minHeap.deleteMin();
        }
        return arr;
    }


    /**
     * public class HeapNode
     * <p>
     * If you wish to implement classes other than FibonacciHeap
     * (for example HeapNode), do it in this file, not in another file.
     */
    public static class HeapNode {

        public int key;
        public boolean marked;
        public HeapNode child;
        public HeapNode parent;
        public HeapNode next;
        public HeapNode prev;
        public int rank;

        public HeapNode(int key) {
            this.key = key;
            this.marked = false;
            this.child = null;
            this.parent = null;
            this.next = null;
            this.prev = null;
            this.rank = 0;
        }

        public int getKey() {
            return this.key;
        }

        public boolean getMarked() {
            return this.marked;
        }

        public HeapNode getChild() {
            return this.child;
        }

        public HeapNode getParent() {
            return this.parent;
        }

        public HeapNode getNext() {
            return this.next;
        }

        public HeapNode getPrev() {
            return this.prev;
        }

        public int getRank() {
            return this.rank;
        }

        public void setKey(int key) {
            this.key = key;
        }

        public void setMarked(boolean marked) {
            this.marked = marked;
        }

        public void setChild(HeapNode child) {
            this.child = child;
        }

        public void setParent(HeapNode parent) {
            this.parent = parent;
        }

        public void setNext(HeapNode next) {
            this.next = next;
        }

        public void setPrev(HeapNode prev) {
            this.prev = prev;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

    }
}
