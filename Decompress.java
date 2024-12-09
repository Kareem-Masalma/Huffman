import javafx.scene.Scene;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Decompress {
    private int[] freq;
    private File file;
    private Scene scene;
    private String exe;
    private String header;

    public Decompress(Scene scene, File file) {
        this.freq = new int[256];
        this.file = file;
        this.scene = scene;
        decompress();
    }

    public void decompress() {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte exeLen = (byte) fis.read();
            byte[] exeBytes = new byte[exeLen];
            fis.read(exeBytes);
            exe = new String(exeBytes);

            byte[] buffer = new byte[8];
            fis.read(buffer, 0, 4);
            int headerLen = buffer[3] & 0xFF | (buffer[2] & 0xFF) << 8 | (buffer[1] & 0xFF) << 16
                    | (buffer[0] & 0xFF) << 24;

            int read = 0;
            int i = 0;
            StringBuilder header = new StringBuilder();
            StringBuilder content = new StringBuilder();
            while ((read = fis.read(buffer)) != -1) {
                for (int j = 0; j < read; j++) {
                    if (i < headerLen) {
                        header.append(byteToBinary(buffer[j]));
                        i += read;
                    } else
                        content.append(byteToBinary(buffer[j]));
                }
            }
            this.header = header.toString();
            this.header = header.substring(0, headerLen);
            int startIndex = content.length() - 8;
            int addedBits = Integer.parseInt(content.substring(startIndex), 2);
            content.delete(startIndex - addedBits, content.length());

            Node root = buildTree(this.header, new int[]{0});
            StringBuilder decompressed = new StringBuilder();
            decompressedContent(content.toString(), root, decompressed);

            File compressedFile = new File(file.getPath().split("\\.")[0] + "." + exe);
            try (FileOutputStream fos = new FileOutputStream(compressedFile)) {
                byte[] out = new byte[8];
                for (i = 0; i < decompressed.length(); i++) {
                    out[i % 8] = (byte) decompressed.charAt(i);
                    if (i % 8 == 7) {
                        fos.write(out);
                    }
                }
                if (i % 8 != 0) {
                    fos.write(out, 0, i % 8);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void decompressedContent(String content, Node root, StringBuilder decompressed) {
        int i = 0;
        while (i < content.length()) {
            Node current = root;
            while (current != null) {
                if (current.getLeft() == null && current.getRight() == null) {
                    decompressed.append((char) current.getByteValue());
                    break;
                }
                if (content.charAt(i) == '0') {
                    current = current.getLeft();
                } else {
                    current = current.getRight();
                }
                i++;
            }
        }
    }

    public Node buildTree(String header, int[] i) {
        if (i[0] >= header.length())
            return null;

        if (header.charAt(i[0]++) == '1') {
            byte b = (byte) Integer.parseInt(header.substring(i[0], i[0] + 8), 2);
            i[0] += 8;
            return new Node(b, 0);
        } else {
            Node node = new Node();
            node.setLeft(buildTree(header, i));
            node.setRight(buildTree(header, i));
            return node;
        }
    }

    // This method converts a byte to a binary string
    public String byteToBinary(byte b) {
        // StringBuilder to store the binary string, because it is faster than String
        // and we can use append method to concatenate the binary digits
        StringBuilder binary = new StringBuilder();
        for (int i = 7; i >= 0; i--) {
            // Append the binary digit at the i-th position to the StringBuilder
            binary.append(b >> i & 1);
        }
        return binary.toString();
    }

    private void levelByLevel(Node root) {
        if (root == null)
            return;
        Queue queue = new Queue();
        queue.enQueue(root);
        while (!queue.isEmpty()) {
            Node curr = (Node) queue.front();
            System.out.println(curr);
            if (curr.getLeft() != null)
                queue.enQueue(curr.getLeft());
            if (curr.getRight() != null)
                queue.enQueue(curr.getRight());
            queue.deQueue();
        }
    }
}
