package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import java.util.Observer;

public interface IModel {

    void assignObserver(Observer o);
    void generateMaze(int row, int col);
    Maze getMaze();
    int getPlayerRow();
    int getPlayerCol();
    void moveCharacter(int direction);
    void setMaze(Maze maze);
    Solution solveMaze();
    void close();
    void LoadMaze(Maze maze1);

}
