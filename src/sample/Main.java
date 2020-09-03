package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.util.Optional;

public class Main extends Application {

    public static MediaPlayer media;

    public static MediaPlayer getMedia() {
        return media;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void stopMusic() {
        media.stop();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader();
        BorderPane root = fxmlLoader.load(getClass().getResource("/View/firstWindow.fxml").openStream());
        primaryStage.setTitle("Quiddich Maze");
        Scene FirstScene = new Scene(root, 1200, 700);
        String css = this.getClass().getResource("/View/FirstStyle.css").toExternalForm();
        FirstScene.getStylesheets().add(css);
        primaryStage.setScene(FirstScene);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/smallPic.png")));
        primaryStage.show();
        media = new MediaPlayer(new Media(getClass().getResource("/Song.mp3").toURI().toString()));
        media.setCycleCount(MediaPlayer.INDEFINITE);
        media.play();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                onClickExit(event, primaryStage);
                event.consume();
            }
        });
        root.prefHeightProperty().bind(FirstScene.heightProperty());
        root.prefWidthProperty().bind(FirstScene.widthProperty());



    }
    private static void onClickExit(WindowEvent event, Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to exit?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            Platform.exit();
            System.exit(0);
        } else {
            // ... user chose CANCEL or closed the dialog
            alert.close();
        }
    }


}
