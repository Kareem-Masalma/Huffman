public class MinHeap {
    private Node[] heap;
    private int size;
    private int maxSize;

    public MinHeap(int maxSize) {
        this.maxSize = maxSize;
        heap = new Node[maxSize];
        size = 0;
    }

    public void insert(Node element) {
        if(size >= maxSize)
            return;

        heap[++size] = element;
        int i = size;
        while (i > 1 && heap[i].getFrequency() < heap[parent(i)].getFrequency()) {
            swap(i, i / 2);
            i = parent(i);
        }
    }

    public void heapify(int pos) {
        if(!isLeaf(pos)) {
            int left = leftChild(pos);
            int right = rightChild(pos);
            Node parent = heap[pos];

            int min;
            if(heap[left].getFrequency() < parent.getFrequency())
                min = left;
            else
                min = pos;

            if(heap[min].getFrequency() > heap[right].getFrequency())
                min = right;

            if(min != pos) {
                swap(min, pos);
                heapify(min);
            }
        }
    }

    public Node deleteMin() {
        Node popped = heap[1];
        heap[1] = heap[size--];
        heapify(1);
        return popped;
    }

    public void swap(int i, int j) {
        Node temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    private int leftChild(int pos) {
        return pos * 2;
    }

    private int rightChild(int pos) {
        return pos * 2 + 1;
    }

    private int parent(int pos) {
        return pos / 2;
    }

    private boolean isLeaf(int pos) {
        return pos > size / 2;
    }
    public void print() {
        for(int i = 1; i <= size; i++) {
            System.out.println(heap[i]);
        }
    }

    public int getSize() {
        return size;
    }

    public Node get(int index) {
        return heap[index];
    }

    public Node getRoot() {
        return heap[1];
    }

    public int getMaxSize() {
        return maxSize;
    }

    public Node[] getHeap() {
        return heap;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}