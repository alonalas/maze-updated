package View;

import Model.IModel;
import Model.MyModel;
import ViewModel.MyViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import Server.Configurations;
import javafx.stage.StageStyle;

import javafx.scene.image.ImageView;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * this controller represents the main scene of the game where user is asked to
 * choose solving algorithm generating method
 */
public class FirstController implements Initializable {

    @FXML
    ComboBox combo_boxAlgo;
    @FXML
    ComboBox combo_boxGenerate;
    private String generateMethod;
    private String algorithm;
    @FXML
    private ImageView imageView;
    private static Stage primaryStage;
    static boolean isAbout=false;
    public FirstController() {
        this.generateMethod = "MyMazeGenerator";
        this.algorithm = "BestFirstSearch";
    }



    /**
     * switching scenes to maze-screen scene
     * @param actionEvent
     * @throws IOException
     */
    public void startPlay(ActionEvent actionEvent) throws IOException {

        isAbout=false;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MyView.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        primaryStage.setTitle("Quiddich Maze");
        Scene secondScene = new Scene(root, 1200, 700);
        String css = this.getClass().getResource("myView.css").toExternalForm();
        secondScene.getStylesheets().add(css);
        primaryStage.setScene(secondScene);
        Configurations.initializeProp(this.algorithm,this.generateMethod);
        primaryStage.show();

        IModel model = new MyModel();
        MyViewModel myViewModel = new MyViewModel(model);
        MyViewController controller=fxmlLoader.getController();

        controller.setResizeEvent(secondScene);
        controller.setViewModel(myViewModel);
        controller.setStage(primaryStage);
        myViewModel.addObserver(controller);
        model.assignObserver(myViewModel);

    }

    /**
     * initializing the solving algorithm of the maze by the user's request
     * @param actionEvent
     */
    public void clickAlgo(ActionEvent actionEvent) {
        String algo = (String) combo_boxAlgo.getValue();
        this.algorithm = algo;
    }

    /**
     * initializing the maze generating method of the maze by the user's request
     * @param actionEvent
     */
    public void clickGenerate(ActionEvent actionEvent) {
        String method = (String) combo_boxGenerate.getValue();
        this.generateMethod = method;
    }


    /**
     * shows a window which contains details about the maze writers
     * @param actionEvent
     * @throws IOException
     */
    public void onClickAbout(ActionEvent actionEvent) throws IOException {
        isAbout=true;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("About.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.initModality(Modality.NONE);
        stage.initStyle(StageStyle.DECORATED);
        stage.setTitle("About us");
        Scene fifth = new Scene(root, 500, 300);
        String css = this.getClass().getResource("about.css").toExternalForm();
        fifth.getStylesheets().add(css);
        stage.setScene(fifth);
        stage.show();
        Label label_about=new Label("This game was designed and created by ISE\nstudents named: Alona Lasry\n and Niv Meir. The Maze\n generator engine was based on\n prim's algorithm. Solving way\n is by BFS algorithm.\n Find the Snitch and\n HAVE FUN! ©");

        label_about.setPadding(new Insets(5,5,5,5));
        AnchorPane anchor = new AnchorPane();
        anchor.getChildren().addAll(label_about);
        ((AnchorPane) root).getChildren().add(anchor);
        stage.setScene(fifth);
    }

    /**
     * closes the program properly when the user press the exit button
     * @param actionEvent
     */
    public void onClickExit(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to exit?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            alert.close();
            Stage stage=(Stage) this.combo_boxAlgo.getScene().getWindow();
            stage.close();
        } else {
            alert.close();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(!isAbout) {
            Image im = new Image("/images/frame3.png");
            imageView.setImage(im);
            imageView.setStyle(" background-color: transparent;");
        }
    }


}
