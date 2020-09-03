package View;

import algorithms.mazeGenerators.Maze;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * This class represents the maze board
 */
public class MazeDisplayer extends Canvas {



    private Maze maze;
    private int row_player;
    private int col_player;
    private int row_maze;
    private int col_maze;
    private double cellHeight;
    private double cellWidth;
    private double canvasWidth;
    private double canvasHeight;
    GraphicsContext graphicsContext;
    StringProperty imageFileNameWall = new SimpleStringProperty();
    StringProperty imageFileNamePlayer = new SimpleStringProperty();
    StringProperty imageFileNameEnd=new SimpleStringProperty();

    public String getImageFileNamePlayer() { return imageFileNamePlayer.get(); }
    public String getImageFileNameEnd() { return imageFileNameEnd.get(); }
    public String getImageFileNameWall() { return imageFileNameWall.get(); }
    public int getRow_player() { return row_player; }
    public int getCol_player() { return col_player; }
    public Maze getMaze() { return maze; }
    public void set_player_position(int row, int col){
        int prevRow = row_player;
        int prevCol = col_player;
        this.row_player = row;
        this.col_player = col;
        drawCurrPlayer(prevRow,prevCol);
    }

    private void initializeParameters(){
        canvasHeight = getHeight();
        canvasWidth = getWidth();
        row_maze = maze.getMaze().length;
        col_maze = maze.getMaze()[0].length;
        cellHeight = canvasHeight/row_maze;
        cellWidth = canvasWidth/col_maze;
        graphicsContext = getGraphicsContext2D();
        graphicsContext.clearRect(0,0,canvasWidth,canvasHeight);
        graphicsContext.setFill(Color.TRANSPARENT);
    }

    /**
     * draws the charecter's current position of the maze board
     * @param prevRow
     * @param prevCol
     */
    private void drawCurrPlayer(int prevRow, int prevCol) {
        double h_player = getRow_player() * cellHeight;
        double w_player = getCol_player() * cellWidth;
        //DeletePos();
        Image playerImage = null;
        try {
            playerImage = new Image(new FileInputStream(getImageFileNamePlayer()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no Image player....");
        }

        graphicsContext.clearRect(prevCol*cellWidth,prevRow*cellHeight,cellWidth,cellHeight);
        graphicsContext.setFill(Color.TRANSPARENT);
        //graphicsContext.fillRect(prevCol*cellWidth,prevRow*cellHeight,cellWidth,cellHeight);
        //graphicsContext.setFill(Color.TRANSPARENT);
        graphicsContext.drawImage(playerImage,w_player,h_player,cellWidth,cellHeight);

    }

    public void drawMaze(Maze newMaze) {
        this.maze = newMaze;
        this.row_player = this.maze.getStartPosition().getRowIndex();
        this.col_player = this.maze.getStartPosition().getColumnIndex();
        initializeParameters();
        draw();

    }

    /**
     * draws the maze and displaying it on the user screen
     */
    public void draw() {
        if( maze!=null) {
            double w,h;
            Image wallImage = null;
            try {
                wallImage =  new Image(new FileInputStream(getImageFileNameWall()));
            } catch (FileNotFoundException e) {
                System.out.println("There is no file....");
            }
            for(int i=0;i<row_maze;i++) {
                for(int j=0;j<col_maze;j++) {
                    if(maze.getMaze()[i][j].getValue() == 1) {// Wall
                        h = i * cellHeight;
                        w = j * cellWidth;
                        if (wallImage == null){
                            graphicsContext.fillRect(w,h,cellWidth,cellHeight);
                        }else{
                            graphicsContext.drawImage(wallImage,w,h,cellWidth,cellHeight);
                        }
                    }
                    else {
                        h = i * cellHeight;
                        w = j * cellWidth;
                        graphicsContext.setFill(Color.TRANSPARENT);
                        graphicsContext.fillRect(w,h,cellWidth,cellHeight);
                    }
                }
            }
            double h_end = maze.getGoalPosition().getRowIndex() * cellHeight;
            double w_end = maze.getGoalPosition().getColumnIndex() * cellWidth;
            Image endImage=null;

            try {
                endImage = new Image(new FileInputStream(getImageFileNameEnd()));
            } catch (FileNotFoundException e) {
                System.out.println("There is no Image player....");
            }
            graphicsContext.drawImage(endImage,w_end,h_end,cellWidth,cellHeight);
        }
        drawCurrPlayer(getRow_player(),getCol_player());
    }

    /**
     * draws a path between the current charecter position and the goal position which represents
     * the solution path
     * @param solution
     */
    public void drawSol(Solution solution) {
        Image solPos = new Image("/images/trace.png");
        ArrayList<AState> solpath = solution.getSolutionPath();
        for (int i = 1; i < solpath.size()-1; i++) {
            MazeState tmp = (MazeState) solpath.get(i);
            graphicsContext.drawImage(solPos,tmp.getCurrPosition().getColumnIndex()*cellWidth,tmp.getCurrPos().getRowIndex()*cellHeight,cellWidth,cellHeight);
        }
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.imageFileNameWall.set(imageFileNameWall);
    }
    public void setImageFileNamePlayer(String imageFileNamePlayer) {
        this.imageFileNamePlayer.set(imageFileNamePlayer);
    }
    public void setImageFileNameEnd(String imageFileNameEnd) {
        this.imageFileNameEnd.set(imageFileNameEnd);
    }
}
