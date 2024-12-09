public class Node {
    private Node left, right;
    private int frequency;
    private String huffmanCode;
    private byte byteValue;
    private byte huffmanLength;

    public Node() {

    }

    public Node(byte byteValue, int frequency) {
        this.frequency = frequency;
        this.byteValue = byteValue;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public byte getByteValue() {
        return byteValue;
    }

    public void setByteValue(byte byteValue) {
        this.byteValue = byteValue;
    }

    public String getHuffmanCode() {
        return huffmanCode;
    }

    public void setHuffmanCode(String huffmanCode) {
        this.huffmanCode = huffmanCode;
    }

    public byte getHuffmanLength() {
        return huffmanLength;
    }

    public void setHuffmanLength(byte huffmanLength) {
        this.huffmanLength = huffmanLength;
    }

    @Override
    public String toString() {
        return "Node{" +
                "frequency=" + frequency +
                ", huffmanCode='" + huffmanCode + '\'' +
                ", byteValue=" + byteValue +
                '}';
    }
}
