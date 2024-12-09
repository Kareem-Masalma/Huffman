import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Driver extends Application {

    @Override
    public void start(Stage stage) {
        // Image for background
        Image background = new Image("background.png");
        // BackgroundImage to set the background image on grid pane
        BackgroundImage backgroundImage = new BackgroundImage(background,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                null,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, true, true));

        // Grid pane to add the buttons
        GridPane gridPane = new GridPane();
        gridPane.setBackground(new Background(backgroundImage));
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        Button btCompress = new Button("Compress");
        btCompress.setPrefSize(125, 75);

        Button btDecompress = new Button("Decompress");
        btDecompress.setPrefSize(125, 75);

        HBox hbCompression = new HBox(10);
        hbCompression.getChildren().addAll(btCompress, btDecompress);
        hbCompression.setAlignment(Pos.CENTER);
        gridPane.add(hbCompression, 0, 0);


        Scene scene = new Scene(gridPane);
        scene.getStylesheets().add("style.css");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setTitle("Huffman Coding");
        stage.show();

        btCompress.setOnAction(e -> {
            GUI.compress(stage, scene);
        });

        btDecompress.setOnAction(e -> {
            GUI.decompress(stage, scene);
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
