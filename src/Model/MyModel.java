package Model;

import Client.Client;
import Client.IClientStrategy;
import IO.MyDecompressorInputStream;
import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * holds the logic layer which represents a maze
 */
public class MyModel extends Observable implements IModel {

    public Server myGeneratingServer;
    public Server mySolvingServer;
    private Maze maze;
    private Integer playerRow;
    private Integer playerCol;
    private Solution solution;
    private static boolean inTheAir = false;

    private ExecutorService threadPool = Executors.newCachedThreadPool();


    public MyModel() {
        if (!inTheAir) {
            this.myGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
            this.mySolvingServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
            mySolvingServer.start();
            myGeneratingServer.start();
            inTheAir = true;
        }
    }

    @Override
    public void assignObserver(Observer o) {
        this.addObserver(o);
    }

    public void setMaze(Maze maze) {
        this.maze= maze;
        Position curr=new Position(playerRow.intValue(),playerCol.intValue());
        this.maze.setStartPosition(curr);
    }

    public void generateMaze(int row, int column) {
        CommunicateWithServer_MazeGenerating(row, column);
        playerRow = maze.getStartPosition().getRowIndex();
        playerCol = maze.getStartPosition().getColumnIndex();
        setChanged();
        notifyObservers();
    }

    @Override
    public Maze getMaze() {
        return maze;
    }

    /**
     * checks if a cell with the given indexes is in the maze's boundry
     * @param row
     * @param col
     * @return
     */
    private boolean cellInBoundry(int row, int col) {
        return row >= 0 && col >= 0 && this.maze.getMaze().length > row && this.maze.getMaze()[0].length > col;
    }

    @Override
    /**
     * 1- down left
     * 2- down
     * 3- down right
     * 4- left
     * 6- right
     * 7- left up
     * 8- up
     * 9- right up
     */
    public void moveCharacter(int direction) {

        switch (direction){
            case 8://UP
                if (cellInBoundry(playerRow - 1,playerCol)) {
                    if(maze.getMaze()[playerRow-1][playerCol].getValue()!=1){
                        setPlayerRow(playerRow-1);
                        setPlayerCol(playerCol);
                        setMaze(maze);
                    }
                }
                break;
            case 2://DOWN
                if (cellInBoundry(playerRow + 1,playerCol)) {
                    if(maze.getMaze()[playerRow+1][playerCol].getValue()!=1){
                        setPlayerRow(playerRow+1);
                        setPlayerCol(playerCol);
                        setMaze(maze);
                    }
                }

                break;
            case 6://RIGHT
                if (cellInBoundry(playerRow ,playerCol + 1)) {
                    if(maze.getMaze()[playerRow][playerCol+1].getValue()!=1){
                        setPlayerRow(playerRow);
                        setPlayerCol(playerCol+1);
                        setMaze(maze);
                    }
                }
                break;
            case 4://LEFT
                if (cellInBoundry( playerRow,playerCol -1)) {
                    if(maze.getMaze()[playerRow][playerCol-1].getValue()!=1){
                        setPlayerRow(playerRow);
                        setPlayerCol(playerCol-1);
                        setMaze(maze);
                    }
                }
                break;
            case 9://UP-RIGHT
                if (cellInBoundry( playerRow-1, playerCol + 1)) {
                    if (maze.getMaze()[playerRow - 1][playerCol + 1].getValue() != 1) {
                        setPlayerRow(playerRow-1);
                        setPlayerCol(playerCol+1);
                        setMaze(maze);
                    }
                }

                break;
            case 7://UP-LEFT
                if (cellInBoundry(playerRow-1, playerCol - 1)) {
                    if (maze.getMaze()[playerRow - 1][playerCol -1].getValue() != 1) {
                        setPlayerRow(playerRow-1);
                        setPlayerCol(playerCol-1);
                        setMaze(maze);
                    }
                }

                break;
            case 3://DOWN-RIGHT
                if (cellInBoundry(playerRow+1, playerCol + 1)) {
                    if (maze.getMaze()[playerRow + 1][playerCol + 1].getValue() != 1) {
                        setPlayerRow(playerRow+1);
                        setPlayerCol(playerCol+1);
                        setMaze(maze);
                    }
                }

                break;
            case 1://DOWN-LEFT
                if (cellInBoundry(playerRow+1, playerCol - 1)) {
                    if (maze.getMaze()[playerRow + 1][playerCol - 1].getValue() != 1) {
                        setPlayerRow(playerRow+1);
                        setPlayerCol(playerCol-1);
                        setMaze(maze);
                    }
                }

                break;
            default:
                setPlayerRow(playerRow);
                setPlayerCol(playerCol);
                setMaze(maze);
        }

        setChanged();
        notifyObservers();

    }

    @Override
    public Solution solveMaze() {
        CommunicateWithServer_SolveSearchProblem();
        setChanged();
        notifyObservers();
        return solution;
    }

    /**
     * creates a connection between the client and the server and solving the maze
     */
    private void CommunicateWithServer_SolveSearchProblem() {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        toServer.writeObject(maze); //send maze to server
                        toServer.flush();
                        solution = (Solution) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    /**
     * creates a connection between the client and the server and solving the maze
     * @param row
     * @param column
     */
    private void CommunicateWithServer_MazeGenerating(int row, int column) {

        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{row, column};
                        toServer.writeObject(mazeDimensions); //send maze dimensions to server
                        toServer.flush();
                        byte[] compressedMaze = (byte[]) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[row*column+12]; //allocating byte[] for the decompressed maze -
                        is.read(decompressedMaze); //Fill decompressedMaze with bytes
                        maze = new Maze(decompressedMaze);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void setPlayerRow(int playerRow) {
        this.playerRow = playerRow;
    }

    public void setPlayerCol(int playerCol) {
        this.playerCol = playerCol;
    }

    public int getPlayerRow() {
        return playerRow.intValue();
    }

    public int getPlayerCol() {
        return playerCol.intValue();
    }

    public void close() {
        try {
            threadPool.shutdown();
            threadPool.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }
    }

    /**
     * loads a requested maze from the disk memory
     * @param maze1
     */
    @Override
    public void LoadMaze(Maze maze1) {
        maze = new Maze(maze1);
        playerRow = maze.getStartPosition().getRowIndex();
        playerCol = maze.getStartPosition().getColumnIndex();
        setChanged();
        notifyObservers();
    }

}