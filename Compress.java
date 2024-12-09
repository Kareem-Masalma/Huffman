import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;

public class Compress {
    private int[] freq;
    private File file;
    private Scene scene;
    private MinHeap heap;
    private Node[] nodes;
    private int headerSize;
    private String header;
    private Node root;
    private long newFileSize;

    public Compress(Scene scene, File file) {
        this.file = file;
        this.scene = scene;
        this.freq = new int[256];
        this.nodes = new Node[256];
        readFile();
        createHeap();
        compress();
        createScene();
    }

    /*
        This function reads data from the selected file 8 bytes at a time. It calculates the frequency of each byte
        stored in freq array. The file can be any extension but not .huff
    */
    public void readFile() {
        // The 8 bytes buffer to read the file
        byte[] buffer = new byte[8];
        try (FileInputStream fis = new FileInputStream(file)) {
            /* This variable will store the number of bytes read in each iteration
             because the last iteration may not read 8 bytes */
            int bytesRead;
            // Read the file 8 bytes at a time from the file
            while ((bytesRead = fis.read(buffer)) != -1) {
                // Loop through the bytes read and increment the frequency of each byte
                for (int i = 0; i < bytesRead; i++) {
                    // To handle negative byte values and convert them to positive
                    freq[buffer[i] & 0xFF]++;
                }
            }
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error with file");
            alert.setContentText("File is corrupted");
            alert.showAndWait();
        }
    }

    // This function creates a MinHeap from the frequency array to get the minimum frequency node
    public void createHeap() {
        // The heap is created with a maximum size of 256
        heap = new MinHeap(257);
        for (int i = 0; i < 256; i++) {
            // If frequency of a byte is greater than 0, insert it into the heap
            if (freq[i] > 0) {
                heap.insert(new Node((byte) i, freq[i]));
            }
        }
    }

    /* This function to start compressing the file, first it creates the huffman tree, second it read the tree
        in pre-order to get the huffman code for each byte, third it creates the header,
        and finally it read the file again and compress it and write it to the new file
    * */
    public void compress() {
        // Loop to go through all the nodes in the heap
        if (heap.getSize() != 1) {
            while (heap.getSize() > 1) {
                // New node to store the frequency of the two nodes with minimum frequency
                // The root of the heap will be the Huffman tree
                Node z = new Node();
                Node x = heap.deleteMin();
                Node y = heap.deleteMin();
                z.setLeft(x);
                z.setRight(y);
                z.setFrequency(x.getFrequency() + y.getFrequency());
                heap.insert(z);
            }
            // The root of the heap is the Huffman tree
            this.root = heap.deleteMin();
        } else {
            this.root = new Node();
            root.setRight(heap.deleteMin());
        }

        // Traverse the Huffman tree in pre-order to get the Huffman code for each byte
        preOrder(root, "");
        // Create the header function
        createHeader();
        // Write the header and the compressed data to the compressed file
        writeOnFile();
    }

    /*
        This function creates the header for the compressed file. The header contains the extension length,
        the extension, the tree, and the size of the tree
    * */
    public void createHeader() {
        StringBuilder header = new StringBuilder();
        // The original file extension
        String fileExtension = (file.getName()).split("\\.")[1];
        byte extensionLength = (byte) fileExtension.length();
        header.append(byteToBinary(extensionLength));
        for (int i = 0; i < extensionLength; i++) {
            header.append(byteToBinary((byte) fileExtension.charAt(i)));
        }

        // Construct the header from the tree
        StringBuilder tree = constructTree();

        headerSize = tree.length();

        // If the header length is not a multiple of 8, add padding to make it a multiple of 8
        if (tree.length() % 8 != 0) {
            int padding = 8 - header.length() % 8;
            for (int i = 0; i < padding; i++) {
                tree.append("0");
            }
        }

        // Append the extension length and the extension to the header
        header.append(byteToBinary((byte) (headerSize >> 24)));
        header.append(byteToBinary((byte) (headerSize >> 16)));
        header.append(byteToBinary((byte) (headerSize >> 8)));
        header.append(byteToBinary((byte) headerSize));

        // Append the tree to the header
        header.append(tree);

        this.header = header.toString();
    }

    // This function constructs the Huffman tree in pre
    private StringBuilder constructTree() {
        StringBuilder tree = new StringBuilder();
        preOrder(root, tree);
        return tree;
    }

    // This function to construct the Huffman tree in pre-order to add it to the header.
    private void preOrder(Node root, StringBuilder tree) {
        if (root == null) {
            return;
        }

        // If the node is a leaf node, add 1 followed by the byte value
        if (root.getLeft() == null && root.getRight() == null) {
            tree.append("1");
            tree.append(byteToBinary(root.getByteValue()));
        } else {
            tree.append("0");
        }
        preOrder(root.getLeft(), tree);
        preOrder(root.getRight(), tree);
    }

    // This function traverses the Huffman tree in pre-order and stores the Huffman code for each byte
    public void preOrder(Node root, String code) {
        if (root == null) {
            return;
        }

        // If the node is a leaf node, store the Huffman code and length
        if (root.getLeft() == null && root.getRight() == null) {
            root.setHuffmanCode(code);
            int length = code.length();
            root.setHuffmanLength((byte) length);
            nodes[root.getByteValue() & 0xFF] = root;
        }
        // Traverse the left child with code 0
        preOrder(root.getLeft(), code + "0");
        // Traverse the right child with code 1
        preOrder(root.getRight(), code + "1");
    }

    // This function writes the header and the compressed data to the compressed file
    public void writeOnFile() {
        String path = file.getPath();
        File compressedFile = new File(path.split("\\.")[0] + ".huff");
        try (FileOutputStream fos = new FileOutputStream(compressedFile)) {
            byte[] bufferIn = new byte[8]; // Buffer to read the original file
            byte[] bufferOut = new byte[8]; // Buffer to write compressed data
            byte[] headerBytes = new byte[8]; // Buffer to store header information

            int byteNumber = 0;
            // Convert header string bits to bytes and write them to the output file
            for (byteNumber = 0; byteNumber < header.length() / 8; byteNumber++) {
                String byteString = header.substring(8 * byteNumber, 8 * byteNumber + 8);
                headerBytes[byteNumber % 8] = (byte) Integer.parseInt(byteString, 2);

                // Write every 8 bytes to the file
                if ((byteNumber + 1) % 8 == 0)
                    fos.write(headerBytes);
            }

            // Write any remaining bytes in the header buffer
            if (byteNumber % 8 != 0)
                fos.write(headerBytes, 0, byteNumber % 8);

            // Read the original file again to compress it
            try (FileInputStream fis = new FileInputStream(file)) {
                int bytesRead;
                StringBuilder compressed = new StringBuilder();
                while ((bytesRead = fis.read(bufferIn)) != -1) {
                    for (int i = 0; i < bytesRead; i++) {
                        compressed.append(nodes[bufferIn[i] & 0xFF].getHuffmanCode());
                        if (compressed.length() >= 64) {
                            for (int j = 0; j < 8; j++) {
                                bufferOut[j] = (byte) Integer.parseInt(compressed.substring(0, 8), 2);
                                compressed.delete(0, 8);
                            }
                            fos.write(bufferOut);
                        }
                    }
                }

                // Write any remaining bits to the output file
                byte length = (byte) compressed.length();
                for (int i = 0; i < length / 8; i++) {
                    bufferOut[i] = (byte) Integer.parseInt(compressed.substring(0, 8), 2);
                    compressed.delete(0, 8);
                }
                fos.write(bufferOut, 0, length / 8);

                try {
                    byte padding = (byte) compressed.length();
                    byte[] rem = new byte[2];
                    if (padding != 0) {
                        for (int i = 0; i < 8 - padding; i++) {
                            compressed.append("0");
                        }
                        rem[0] = (byte) Integer.parseInt(compressed.substring(0, 8), 2);
                        rem[1] = (byte) (8 - padding);
                    } else
                        rem[0] = (byte) Integer.parseInt(compressed.substring(0, 8), 2);
                    fos.write(rem, 0, 2);

                    this.newFileSize = compressedFile.length();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error with file");
            alert.setContentText("File is corrupted");
            alert.showAndWait();
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

    /* This method creates the scene after the compression is done, with three buttons
     to display the Huffman table, header, and statistics
     */
    public void createScene() {
        Button btCompress = new Button("Compress");
        btCompress.setPrefSize(125, 75);
        Button btDecompress = new Button("Decompress");
        btDecompress.setPrefSize(125, 75);
        HBox hbCompression = new HBox(10);
        hbCompression.setAlignment(Pos.CENTER);
        hbCompression.getChildren().addAll(btCompress, btDecompress);


        // Creating buttons for Huffman Table, Header, and Statistics
        Button btHuffmanTable = new Button("Huff Table");
        btHuffmanTable.setPrefSize(105, 55);
        Button btHeader = new Button("Header");
        btHeader.setPrefSize(115, 55);
        Button btStat = new Button("Statistics");
        btStat.setPrefSize(115, 55);

        // Creating VBox to contains the buttons
        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(btHuffmanTable, btHeader, btStat);
        hBox.setAlignment(Pos.CENTER);

        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(10);
        pane.setVgap(30);
        pane.add(hBox, 0, 0);
        pane.add(hbCompression, 0, 1);

        // Image for background
        Image background = new Image("background.png");
        // BackgroundImage to set the background image on grid pane
        BackgroundImage backgroundImage = new BackgroundImage(background,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                null,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, true, true));
        pane.setBackground(new Background(backgroundImage));
        scene.setRoot(pane);

        // Setting action for the huffman button which will display the huffman table
        btHuffmanTable.setOnAction(e -> {
            createHuffmanTable();
        });

        // Setting action for the compress button which will open a dialog to select a new file to compress
        btCompress.setOnAction(e -> {
            GUI.compress((Stage) scene.getWindow(), scene);
        });

        btDecompress.setOnAction(e -> {
            GUI.decompress((Stage) scene.getWindow(), scene);
        });

        // Setting action for the header button which will display the header in a textArea
        btHeader.setOnAction(e -> {
            headerScene();
        });

        // Setting action for the statistics button which will display the statistics of the compressed file
        btStat.setOnAction(e -> {
            double percent = (1 - (double) newFileSize / file.length()) * 100;
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Statistics");
            alert.setHeaderText("Statistics of the compressed file");
            alert.setContentText("Original file size: " + file.length() + " bytes\n" +
                    "Compressed file size: " + newFileSize + " bytes\n" +
                    "Header size: " + headerSize + " bits\n" +
                    "Compress percentage: " + String.format("%.2f", percent) + "%");
            alert.showAndWait();
        });
    }

    // This method creates the Huffman Table scene
    private void createHuffmanTable() {
        // TableView to display the Huffman Table, it contains four columns
        // Byte, Frequency, Huffman Code, and Huffman Length
        TableView<Node> table = new TableView<>();
        // Create columns for the table
        TableColumn<Node, Integer> byteColumn = new TableColumn<>("Byte");
        byteColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getByteValue() & 0xFF));
        TableColumn<Node, Integer> freqColumn = new TableColumn<>("Frequency");
        freqColumn.setCellValueFactory(new PropertyValueFactory<Node, Integer>("frequency"));
        TableColumn<Node, String> codeColumn = new TableColumn<>("Huffman Code");
        codeColumn.setCellValueFactory(new PropertyValueFactory<Node, String>("huffmanCode"));
        TableColumn<Node, Byte> lengthColumn = new TableColumn<>("Huffman Length");
        lengthColumn.setCellValueFactory(new PropertyValueFactory<Node, Byte>("huffmanLength"));

        // Add the columns to the table
        table.getColumns().addAll(byteColumn, freqColumn, codeColumn, lengthColumn);

        // Add the nodes to the table
        for (int i = 0; i < 256; i++) {
            if (nodes[i] != null) {
                table.getItems().add(nodes[i]);
            }
        }

        Scene tableScene = new Scene(table);
        tableScene.getStylesheets().add("style.css");
        Stage stage = new Stage();
        stage.setTitle("Huffman Table");
        stage.setScene(tableScene);
        stage.show();
    }

    // This method creates the Header scene
    private void headerScene() {
        // TextArea to display the header
        TextArea textArea = new TextArea();
        textArea.setText("");
        for (int i = 0; i < header.length() - 8; i += 8) {
            textArea.appendText((char) ((byte) (Integer.parseInt(header.substring(i, i + 8), 2))) + "");
        }
        textArea.appendText("\n" + header);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        Scene headerScene = new Scene(textArea);
        headerScene.getStylesheets().add("style.css");
        Stage stage = new Stage();
        stage.setTitle("Header");
        stage.setScene(headerScene);
        stage.show();
    }

}
