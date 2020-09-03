package View;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import sample.Main;
import IO.MyCompressorOutputStream;
import IO.MyDecompressorInputStream;
import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static javafx.geometry.Pos.CENTER;

/**
 * This class controlls the second scene of the program which displays the maze on the screen
 */
public class MyViewController implements Observer, IView, Initializable {

    public MyViewModel myViewModel;
    public TextField text_save_maze;
    public Button saveButton;
    public BorderPane myBoarderPane;
    public Button backButton;
    @FXML
    private MazeDisplayer mazeDisplayer;
    public Button generate;
    public Button solve;
    @FXML
    Menu file_menu;
    public TextField textfield_rows;
    public TextField textfield_cols;
    public Label player_rows;
    public Label player_cols;
    private static boolean enableInitialize = true;
    Stage stage;

    private boolean isFinished=false;
    public static Maze maze;
    StringProperty update_player_position_row = new SimpleStringProperty();
    StringProperty update_player_position_col = new SimpleStringProperty();



    public void setUpdate_player_position_row(String update_player_position_row) {
        this.update_player_position_row.set(update_player_position_row);
    }

    public void setUpdate_player_position_col(String update_player_position_col) {
        this.update_player_position_col.set(update_player_position_col);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (enableInitialize) {
            player_rows.textProperty().bind(update_player_position_row);
            player_cols.textProperty().bind(update_player_position_col);

        }
    }

    public void setViewModel(MyViewModel v) {
        this.myViewModel = v;
    }

    /**
     * generates a maze with the given dimensions after user clicks on the generate button
     */
    public void generateMaze() {
        if ( validateParameters() ) {
            if(isFinished){
                isFinished=false;
                switchMusic();
            }
            solve.setDisable(false);
            int rows = Integer.valueOf(textfield_rows.getText());
            int cols = Integer.valueOf(textfield_cols.getText());
            myViewModel.generateMaze(rows, cols);
            mazeDisplayer.drawMaze(maze);
            mazeDisplayer.set_player_position(maze.getStartPosition().getRowIndex(), maze.getStartPosition().getColumnIndex());
            file_menu.getItems().get(0).setDisable(false);
        }
    }

    /**
     * shows alert on the user screen with the given string
     * @param alertMessage
     */
    private void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.show();
    }

    /**
     * shows alert on the user screen with the given string
     */
    private void showConfirmationAlert() throws IOException, InterruptedException {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to try again?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
            backToStage1(new ActionEvent());
        } else {
            alert.close();
        }
    }

    /**
     * validates the parameters that was given in the rows and cols text areas
     * @return
     */
    private boolean validateParameters() {
        int rows=0;
        int cols=0;
        try {
            rows = Integer.valueOf(this.textfield_rows.getText());
            cols = Integer.valueOf(this.textfield_cols.getText());
        }
        catch (NumberFormatException e) { // not string
            showAlert("Please enter only NUMBERS!");
            return false;
        }
        if ( rows < 2 || rows > 500 || cols < 2 || cols > 500 ) { // not in range
            showAlert("Please enter numbers between 2-500");
            return false;
        }
        return true;
    }

    /**
     * moves the charecter on the maze's board whenever the user press on one of the allowed keys
     * @param keyEvent
     */
    public void keyPressed(KeyEvent keyEvent) {
        enableInitialize =true;
        if(mazeDisplayer.getMaze()!=null)
            myViewModel.moveCharacter(keyEvent);
        keyEvent.consume();
    }



    @Override
    public void update(Observable o, Object arg) {

        if (!isFinished) {
            if (maze == null) {
                this.maze = myViewModel.getModel().getMaze();
                setUpdate_player_position_row(String.valueOf(myViewModel.getModel().getPlayerRow()));
                setUpdate_player_position_col(String.valueOf(myViewModel.getModel().getPlayerCol()));
                mazeDisplayer.drawMaze(maze);
            } else {
                if (maze == myViewModel.getModel().getMaze()) {
                    if (myViewModel.getModel().getPlayerRow() == maze.getGoalPosition().getRowIndex()
                            && myViewModel.getModel().getPlayerCol() == maze.getGoalPosition().getColumnIndex()) {
                        solutionFound();

                    } else {
                        setUpdate_player_position_row(String.valueOf(myViewModel.getModel().getPlayerRow()));
                        setUpdate_player_position_col(String.valueOf(myViewModel.getModel().getPlayerCol()));
                        this.mazeDisplayer.set_player_position(myViewModel.getModel().getPlayerRow(), myViewModel.getModel().getPlayerCol());

                    }
                } else {
                    this.maze = myViewModel.getModel().getMaze();
                    mazeDisplayer.drawMaze(maze);
                }
            }
            if(mazeDisplayer.getMaze()!=null)
                this.zoom(mazeDisplayer);
        }
    }

    /**
     * This function creates a stage of win situation and suggest to come back to first window
     */
    private void solutionFound(){
        solve.setDisable(true);
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("YOU WIN!!!");
        VBox layout = new VBox();
        HBox H = new HBox(5);
        H.setAlignment(CENTER);
        layout.setAlignment(CENTER);
        isFinished=true;
        Button close = new Button();
        close.setText("Brilliant!");

        H.getChildren().add(close);
        layout.spacingProperty().setValue(30);
        Image im = new Image("/images/findsnitch.gif");
        ImageView image = new ImageView(im);

        layout.getChildren().add(image);
        layout.getChildren().add(H);
        close.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.close();
                try {
                    showConfirmationAlert();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Scene scene = new Scene(layout, 500, 350);

        String css = this.getClass().getResource("win.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
        stage.show();
        switchMusic();

    }

    private void switchMusic(){
        if(isFinished)
            defineMusic("/Win.mp3");
        else
            defineMusic("/Song.mp3");
    }

    private void defineMusic(String song) {
        Main.stopMusic();
        try {
            Main.media  = new MediaPlayer(new Media(getClass().getResource(song).toURI().toString()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Main.media.setCycleCount(MediaPlayer.INDEFINITE);
        Main.media.play();
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        mazeDisplayer.requestFocus();
    }

    /**
     * solves the maze when the user clicks on the "solve" button
     * @param actionEvent
     */
    public void solveMaze(ActionEvent actionEvent) throws IOException, InterruptedException {
        Solution solution=myViewModel.solveMaze();
        mazeDisplayer.drawSol(solution);
    }

    /**
     * displays on the screen a new window when the user clicks on the "save" button in the menuBar
     * @param actionEvent
     * @throws IOException
     */
    public void onClickSaveMazeMenuBar(ActionEvent actionEvent) throws IOException {

        isFinished=false;
        enableInitialize = false;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("saveMaze.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage1 = new Stage();
        stage1.initModality(Modality.NONE);
        stage1.initStyle(StageStyle.DECORATED);
        stage1.setTitle("Save Maze");
        Scene third = new Scene(root, 500, 300);
        String css = this.getClass().getResource("save.css").toExternalForm();
        third.getStylesheets().add(css);
        stage1.setScene(third);
        stage1.show();

    }


    /**
     * saves the current maze on the user's disk when he clicks on the "save" button
     * @param actionEvent
     */
    public void saveInDisk(ActionEvent actionEvent) {

        if (text_save_maze.getText() == null || text_save_maze.getText().trim().isEmpty()||text_save_maze.getText().contains(" "))
            showAlert("Please enter a name without spaces");
        else{
            String name = this.text_save_maze.getText() + ".maze";
            // a directory which contains solutions for mazes and mazes
            String tempDirectoryPath = System.getProperty("java.io.tmpdir");
            File folder = new File(tempDirectoryPath);
            File[] listOfFiles = folder.listFiles();
            boolean nameFound = false;

            for (File f : listOfFiles) {
                if (f.getName().equals(name)) {
                    nameFound = true;
                }
            }
            if (!nameFound) {
                OutputStream output = null;
                try {
                    output = new FileOutputStream(new File(tempDirectoryPath + name));
                    MyCompressorOutputStream compressorOutputStream = new MyCompressorOutputStream(output);
                    compressorOutputStream.write(this.maze.toByteArray());
                    showAlert("successfully saved!");
                    output.close();
                    compressorOutputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                stage.close();
            } else {
                showAlert("Chosen file already exists, please choose another name");
            }
        }
    }


    /**
     * displays on the screen a new window when the user clicks on the "Load" button in the menuBar,
     * searches the chosen maze by the user, and loading it to the user's screen
     * @param actionEvent
     * @throws IOException
     */
    public void onClickLoadMazeMenuBar(ActionEvent actionEvent) throws IOException {

        //initialize new scene
        isFinished=false;
        enableInitialize = false;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("loadMaze.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage1 = new Stage();
        stage1.initModality(Modality.NONE);
        stage1.initStyle(StageStyle.DECORATED);
        stage1.setTitle("Load Maze");
        Scene fourth = new Scene(root, 500, 300);
        String css = this.getClass().getResource("load.css").toExternalForm();
        fourth.getStylesheets().add(css);
        //add controls
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(50,60,20,20));
        hBox.setSpacing(15);
        ComboBox<String> comboBoxLoad = new ComboBox<>();
        comboBoxLoad.setTranslateX(140);
        comboBoxLoad.setTranslateY(150);
        comboBoxLoad.setPromptText("Choose a Maze:");

        Button Load = new Button("Load file");
        Load.setDisable(true);
        Load.setTranslateX(10);
        Load.setTranslateY(180);

        comboBoxLoad.setOnAction(event -> {
            Load.setDisable(false);
        });

        // load the maze the client asked for
        Load.setOnAction(click -> {
            InputStream inputStream = null;
            String name = comboBoxLoad.getValue();

            String tempDirectoryPath = System.getProperty("java.io.tmpdir");
            try{
                inputStream = new MyDecompressorInputStream(new FileInputStream(tempDirectoryPath + name));

                byte[] loadedMazeByteArray = new byte[500*500+12]; // maximum size of any maze
                int size = inputStream.read(loadedMazeByteArray);
                loadedMazeByteArray = cleanEmptyCells(loadedMazeByteArray, size);

                Maze maze1 = new Maze(loadedMazeByteArray);
                generateMaze(maze1);

                inputStream.close();
                stage1.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        hBox.getChildren().addAll(comboBoxLoad,Load);
        ((AnchorPane) root).getChildren().add(hBox);
        stage1.setScene(fourth);

        String tempDirectoryPath = System.getProperty("java.io.tmpdir");
        File folder = new File(tempDirectoryPath);
        File[] listOfFiles = folder.listFiles();
        int i=0;

        for (File f : listOfFiles) {
            if (f.getName().endsWith(".maze")) {
                comboBoxLoad.getItems().add(i,f.getName());
                i++;
            }
        }
        stage1.show();
    }

    /**
     * generates a loaded maze and shows it on the user's screen
     * @param maze1
     */
    private void generateMaze(Maze maze1) {
        myViewModel.LoadMaze(maze1);
        mazeDisplayer.drawMaze(maze);
        mazeDisplayer.set_player_position(maze.getStartPosition().getRowIndex(),maze.getStartPosition().getColumnIndex());
        file_menu.getItems().get(0).setDisable(false);
        this.solve.setDisable(false);
    }

    /**
     * a help finction which copies only the used cells of the byte array to a new byte array with the currect size of a maze
     * @param byteArray
     * @param size
     * @return
     */
    private byte[] cleanEmptyCells(byte[] byteArray, int size) {
        byte[] newByteArray = new byte[size];
        for ( int i = 0 ; i < size ; i ++ ) {
            newByteArray[i] = byteArray[i];
        }
        return newByteArray;
    }

    /**
     * opens a new window with helping information on the user's screen when the user press "help" button in the menuBar
     * @param actionEvent
     * @throws IOException
     */
    public void onClickHelp(ActionEvent actionEvent) throws IOException {

        enableInitialize = false;
        isFinished=false;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Help.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.initModality(Modality.NONE);
        stage.initStyle(StageStyle.DECORATED);
        stage.setTitle("Help");
        VBox layout = new VBox();

        Image image1 = new Image(new FileInputStream("resources/images/keyboard.jpeg"));
        ImageView helpImage=new ImageView(image1);
        helpImage.setTranslateX(50);
        helpImage.setTranslateY(50);
        layout.getChildren().add(helpImage);
        ((AnchorPane) root).getChildren().add(layout);

        Button close = new Button();
        close.setTranslateX(415);
        close.setTranslateY(80);
        close.setText("Got it !");
        layout.getChildren().add(close);

        close.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.close();
            }
        });
        Scene fifth = new Scene(root, 1000, 500);
        String css = this.getClass().getResource("help.css").toExternalForm();
        fifth.getStylesheets().add(css);
        stage.setScene(fifth);
        setResizeEvent(fifth);
        stage.show();

    }

    /**
     * streches the scene board
     * @param scene
     */
    public void setResizeEvent(Scene scene) {

        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                mazeDisplayer.setWidth((scene.getWidth() * 0.80));
                mazeDisplayer.drawMaze(maze);
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                mazeDisplayer.setHeight((scene.getHeight()));
                mazeDisplayer.drawMaze(maze);
            }
        });
    }

    /**
     * closes the program properly when the user press the "exit" button
     * @param actionEvent
     */
    public void onClickExit(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to exit?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            Platform.exit();
            System.exit(0);
        } else {
            alert.close();
        }
    }

    /**
     * zoom in and out when user press Ctrl+scroll the mouse
     * @param pane
     */
    public void zoom(MazeDisplayer pane) {
        if(mazeDisplayer.getMaze()!=null) {
            pane.setOnScroll(
                    new EventHandler<ScrollEvent>() {
                        @Override
                        public void handle(ScrollEvent event) {
                            if (event.isControlDown()) {
                                double zoomFactor = 1.05;
                                double deltaY = event.getDeltaY();

                                if (deltaY < 0) {
                                    zoomFactor = 0.95;
                                }
                                pane.setScaleX(pane.getScaleX() * zoomFactor);
                                pane.setScaleY(pane.getScaleY() * zoomFactor);
                                event.consume();
                            }
                        }
                    });
        }
    }

    public void setStage(Stage primaryStage) {
        this.stage = primaryStage;
    }

    public void backToStage1(ActionEvent actionEvent) throws IOException {
        if(isFinished){
            isFinished=false;
            switchMusic();
        }
        Parent root;
        Stage stage;
        stage = (Stage) myBoarderPane.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("firstWindow.fxml"));
        Scene scene = new Scene(root, 1200, 700);
        String css = this.getClass().getResource("/View/FirstStyle.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }
}

