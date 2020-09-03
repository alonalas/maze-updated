package ViewModel;

import Model.IModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.input.KeyEvent;
import java.util.Observable;
import java.util.Observer;


public class MyViewModel extends Observable implements Observer {

    IModel myModel;


    public MyViewModel(IModel myModel) {
        this.myModel = myModel;
    }

    @Override
    public void update(Observable observable, Object o) {
        if (observable == myModel) {
            setChanged();
            notifyObservers();
        }
    }

    public void generateMaze(int rows, int cols) {
        this.myModel.generateMaze(rows, cols);
    }

    public IModel getModel() {
        return this.myModel;
    }

    public void moveCharacter(KeyEvent keyEvent) {
        int direction = -1;

        switch (keyEvent.getCode()) {
            case UP:
            case NUMPAD8:
                direction = 8;
                break;
            case DOWN:
            case NUMPAD2:
                direction = 2;
                break;
            case LEFT:
            case NUMPAD4:
                direction = 4;
                break;
            case RIGHT:
            case NUMPAD6:
                direction = 6;
                break;
            case NUMPAD1:///LEFT-DOWN
            case END:
                direction = 1;
                break;
            case PAGE_DOWN:
            case NUMPAD3:
                direction = 3;
                break;
            case HOME:
            case NUMPAD7:
                direction = 7;
                break;
            case PAGE_UP:
            case NUMPAD9:
                direction = 9;
                break;
        }

        myModel.moveCharacter(direction);
    }

    public Solution solveMaze() {
        return myModel.solveMaze();
    }

    public void LoadMaze(Maze maze1) {
        this.myModel.LoadMaze(maze1);
    }
}


