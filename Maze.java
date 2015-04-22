package mazegen;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

class Maze extends JPanel {

    class Cell {

        private int x;
        private int y;
        private boolean inMaze;
        private State state;

        public Cell(int x, int y, State state, boolean inMaze) {
            this.x = x;
            this.y = y;
            this.state = state;
            this.inMaze = inMaze;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public State getState() {
            return state;
        }
        
        public boolean inMaze(){
            return inMaze;
        }

        public void setCoord(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public void putInMaze(){
            inMaze = true;
        }

        public void setState(State state) {
            this.state = state;
        }

        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }

    private int height;
    private int width;
    private int scale;

    private int rows;
    private int cols;

    //2d array for maze
    Cell maze[][];
    
    Cell finalCell = null;

    //this wallList contains the so-called frontier cells
    List<Cell> wallList = new ArrayList<Cell>();

    public enum State {

        PASSAGE, WALL, FRONTIER, START, END, TEST
    }

    public enum Dir {

        UP, RIGHT, DOWN, LEFT
    }

    public Maze(int width, int height, int scale) {
        this.width = width;
        this.height = height;
        this.scale = scale;
        rows = width / scale;
        cols = height / scale;
        maze = new Cell[rows][cols];

        //Start with a grid full of walls
        //Our maze is like this: the top left corner is (0, 0) where x = 0 and y = 0
        //x increases to value rows as you move to the right of the maze
        //y increases to value cols as you move down the maze
        initMaze();

        Cell startCell = new Cell(5, 5, State.START, true);
        setStart(startCell);
        addWalls(startCell);

    }

    private void initMaze() {
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                maze[i][j] = new Cell(i, j, State.WALL, false);
            }
        }
    }    

    private boolean inRange(Cell cell) {
        if (cell.getX() > 0 && cell.getX() < rows && cell.getY() > 0 && cell.getY() < cols) {
            return true;
        }
        return false;
    }

    private boolean isInRange(int x, int y) {
        if (x > 0 && x < rows && y > 0 && y < cols) {
            return true;
        }
        return false;
    }

    private void setStart(Cell startCell) {
        if (inRange(startCell)) {
            maze[startCell.getX()][startCell.getY()].setState(State.START);
        }
    }

    private void markEnd(Cell endCell) {
        if (inRange(endCell)) {
            maze[endCell.getX()][endCell.getY()].setState(State.END);
        }
    }

    private void makePassage(Cell cell) {
        if (inRange(cell)) {
            if(cell.getState() != State.START) maze[cell.getX()][cell.getY()].setState(State.PASSAGE);
        }
    }

    private void addWalls(Cell cell) {
        if (isInRange(cell.getX(), cell.getY() - 1)) {
            Cell upCell = maze[cell.getX()][cell.getY() - 1];
            if(!upCell.inMaze() && !isPass(upCell)) upCell.setState(State.FRONTIER);
            wallList.add(upCell);
        }
        if (isInRange(cell.getX() + 1, cell.getY())) {
            Cell rightCell = maze[cell.getX() + 1][cell.getY()];
            if(!rightCell.inMaze() && !isPass(rightCell)) rightCell.setState(State.FRONTIER);
            wallList.add(rightCell);
        }
        if (isInRange(cell.getX(), cell.getY() + 1)) {
            Cell downCell = maze[cell.getX()][cell.getY() + 1];
            if(!downCell.inMaze() && !isPass(downCell)) downCell.setState(State.FRONTIER);
            wallList.add(downCell);
        }
        if (isInRange(cell.getX() - 1, cell.getY())) {
            Cell leftCell = maze[cell.getX() - 1][cell.getY()];
            if(!leftCell.inMaze() && !isPass(leftCell)) leftCell.setState(State.FRONTIER);
            wallList.add(leftCell);
        }
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        //draw grid
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                g2d.setColor(Color.BLACK);
                g2d.drawRect(i * scale, j * scale, scale, scale);
                switch (maze[i][j].getState()) {
                    case PASSAGE:
                        g2d.setColor(Color.WHITE);
                        break;
                    case WALL:
                        g2d.setColor(Color.BLACK);
                        break;
                    case FRONTIER:
                        g2d.setColor(Color.BLACK);
                        break;
                    case START:
                        g2d.setColor(Color.GREEN);
                        break;
                    case END:
                        g2d.setColor(Color.ORANGE);
                        break;
                    case TEST:
                        g2d.setColor(Color.BLUE);
                        break;

                }
                g2d.fillRect(i * scale, j * scale, scale, scale);
            }
        }
        g2d.setColor(Color.BLACK);
        for(int i = 0; i <= rows; i++){
            g2d.fillRect(i * scale, cols * scale, scale, scale);
        }
        for(int i = 0; i <= cols; i++){
            g2d.fillRect(rows * scale, i * scale, scale, scale);
        }
       

    }

    /**
     * Start with a grid full of walls. Pick a cell, mark it as part of the
     * maze. Add the walls of the cell to the wall list. While there are walls
     * in the list: Pick a random wall from the list. If the cell on the
     * opposite side isn't in the maze yet: Make the wall a passage and mark the
     * cell on the opposite side as part of the maze. Add the neighboring walls
     * of the cell to the wall list. Remove the wall from the list.
     */
    public boolean primsAlgorithmStep() {

        Cell randWall = pickRandWall();
        
        Cell oppCell = getOppositeCell(randWall); 
        
        if(!oppCell.inMaze()){
          makePassage(randWall); 
          makePassage(oppCell);
          oppCell.putInMaze();
          addWalls(oppCell); 
        }
        wallList.remove(randWall);

        if(wallList.isEmpty()){
            //markEnd(oppCell)'
        }
        return wallList.isEmpty();
    }

    public Cell pickRandWall() {
        int randPos = (int) (Math.random() * wallList.size());
        return wallList.get(randPos);
    }

    public Cell getOppositeCell(Cell cell) {
        if (isInRange(cell.getX(), cell.getY() - 1)) {
            Cell upCell = maze[cell.getX()][cell.getY() - 1];
            if (isPass(upCell) && isInRange(cell.getX(), cell.getY() + 1)) {
                //System.out.println("1");
                //maze[cell.getX()][cell.getY() + 2].setState(State.TEST);
                return maze[cell.getX()][cell.getY() + 1];
            }
        }
        if (isInRange(cell.getX() + 1, cell.getY())) {
            Cell rightCell = maze[cell.getX() + 1][cell.getY()];
            if (isPass(rightCell) && isInRange(cell.getX() - 1, cell.getY())) {
                //System.out.println("2");
                //maze[cell.getX() - 2][cell.getY()].setState(State.TEST);
                return maze[cell.getX() - 1][cell.getY()];
            }
        }
        if (isInRange(cell.getX(), cell.getY() + 1)) {
            Cell downCell = maze[cell.getX()][cell.getY() + 1];
            if (isPass(downCell) && isInRange(cell.getX(), cell.getY() - 1)) {
                //System.out.println("3");
                //maze[cell.getX()][cell.getY() - 2].setState(State.TEST);
                return maze[cell.getX()][cell.getY() - 1];
            }
        }
        if (isInRange(cell.getX() - 1, cell.getY())) {
            Cell leftCell = maze[cell.getX() - 1][cell.getY()];
            if (isPass(leftCell) && isInRange(cell.getX() + 1, cell.getY())) {
                //System.out.println("4");
                //maze[cell.getX() + 2][cell.getY()].setState(State.TEST);
                return maze[cell.getX() + 1][cell.getY()];
            }
        }

        return cell;
    }

    public boolean isPass(Cell cell) {
        if (cell.getState() == State.PASSAGE || cell.getState() == State.START) {
            return true;
        }
        return false;
    }

}
