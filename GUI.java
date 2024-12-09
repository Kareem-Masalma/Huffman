import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/*
    This Class to provide an action when the user clicks on the compress or decompress buttons, it opens a dialog to
    select a file and return it to the compress or decompress class to start the process.
* */
public class GUI {
    public GUI() {

    }

    /*
        This function opens a filechooser to choose a file to compress and return it to the Compress class. It first
        checks if the file is valid, any file is valid except the .huff files. If the user chose invalid file, it will
        open an alert to the user
     */
    public static void compress(Stage stage, Scene scene) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a file to compress");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
        File file = fileChooser.showOpenDialog(stage);
        if (file == null || file.getName().endsWith(".huff")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid File Selection");
            alert.setContentText("Select a valid file to compress");
            alert.showAndWait();
        } else {
            Compress compress = new Compress(scene, file);
        }
    }

    /*
        This function opens a filechooser to choose a file to decompress and return it to the Decompress class. It first
        checks if the file is valid, only .huff files are valid. If the user chose invalid file, it will open an alert to
        the user
    */
    public static void decompress(Stage stage, Scene scene) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a file to decompress");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Huffman Files", "*.huff"));
        File file = fileChooser.showOpenDialog(stage);
        if (file == null || !file.getName().endsWith(".huff")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid File Selection");
            alert.setContentText("Select a valid file to decompress");
            alert.showAndWait();
        } else {
            Decompress decompress = new Decompress(scene, file);
        }
    }
}
