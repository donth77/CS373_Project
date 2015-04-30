/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mazegen;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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

        public boolean inMaze() {
            return inMaze;
        }

        public void setCoord(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void putInMaze() {
            inMaze = true;
        }

        public void setState(State state) {
            this.state = state;
        }

        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }

    //edge structure for kruskal's algorithm
    class Edge {

        private int x;
        private int y;

        private Cell node1;
        private Cell node2;

        public Edge(int x, int y, Cell cell1, Cell cell2) {
            this.x = x;
            this.y = y;
            node1 = cell1;
            node2 = cell2;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public Cell getFirst() {
            return node1;
        }

        public Cell getSecond() {
            return node2;
        }

        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }

    //tree data structure for Kruskal's algorithm
    class Tree {

        private String treeID = "null";

        private Tree parent = null;
        //cells in this tree
        private ArrayList<Cell> cells;

        public Tree(Cell firstCell) {
            cells = new ArrayList<Cell>();
            cells.add(firstCell);
            treeID = UUID.randomUUID().toString();
        }

        public String getID() {
            return treeID;
        }

        public ArrayList<Cell> getCells() {
            return cells;
        }

        public boolean hasCell(Cell cell) {
            for (int i = 0; i < cells.size(); i++) {
                if (cells.get(i).getX() == cell.getX() && cells.get(i).getY() == cell.getY()) {
                    return true;
                }
            }
            return false;
        }

        public Tree root() {
            if (parent != null) {
                return parent.root();
            } else {
                return this;
            }
        }

        public boolean connected(Tree tree) {
            return this.root() == tree.root();
        }

        public void connect(Tree tree) {
            tree.root().setParent(this);
            cells.addAll(tree.getCells());
            treeID = tree.getID();
        }

        public void setParent(Tree parent) {
            this.parent = parent;
        }
    }

    private int height;
    private int width;
    private int scale;

    private int rows;
    private int cols;

    private int whichAlgorithm = 0;

    private int numConnectedComponents = 0;

    //2d array for maze
    Cell maze[][];

    Cell startCell = null;
    Cell finalCell = null;

    //this wallList contains the so-called frontier cells
    List<Cell> primWallList = new ArrayList<Cell>();

    //list of edges that connects nodes
    List<Edge> KList = new ArrayList<Edge>();

    //disjoint set
    private List<Tree> cellSets = new ArrayList<Tree>();

    public enum State {

        PASSAGE, WALL, FRONTIER, START, END, TEST, TEST2, TEST3, TEST4
    }

    public Maze(int width, int height, int scale, int alg) {
        this.width = width;
        this.height = height;
        this.scale = scale;
        rows = width / scale;
        cols = height / scale;
        if ((cols % 2 == 0)) {
            cols--;
        }
        if ((rows % 2 == 0)) {
            rows--;
        }
        maze = new Cell[rows][cols];
        whichAlgorithm = alg;

        //Start with a grid full of walls
        //Our maze is like this: the top left corner is (0, 0) where x = 0 and y = 0
        //x increases to value rows as you move to the right of the maze
        //y increases to value cols as you move down the maze
        initMaze();

        startCell = new Cell(1, 1, State.START, true);
        setStart(startCell);
        if (whichAlgorithm == 0) {
            addWalls(startCell);
        }

    }

    private void initMaze() {
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                //create cells in maze
                maze[i][j] = new Cell(i, j, State.WALL, false);
            }
        }
        if (whichAlgorithm == 1) {
            for (int i = 0; i < maze.length; i++) {
                for (int j = 0; j < maze[i].length; j++) {
                    if (isInRange(i, j) && i % 2 != 0 && j % 2 != 0) {
                        //initialize starting nodes for kruskal's
                        maze[i][j].setState(State.TEST2);
                        numConnectedComponents++;
                        cellSets.add(new Tree(maze[i][j]));
                    } else if (isInRange(i, j)) {
                        if ((i % 2 == 0) && (j % 2 == 0)) {
                            maze[i][j].setState(State.WALL);
                        } else {
                            if (i % 2 == 0) {
                                //connect horizontal nodes
                                Cell rightNode = maze[i + 1][j];
                                Cell leftNode = maze[i - 1][j];
                                KList.add(new Edge(i, j, leftNode, rightNode));
                                //maze[i][j].setState(State.TEST3);
                            } else {
                                //connect vertical nodes
                                Cell upNode = maze[i][j - 1];
                                Cell downNode = maze[i][j + 1];
                                //System.out.println(downNode);
                                KList.add(new Edge(i, j, upNode, downNode));
                                //maze[i][j].setState(State.TEST4);
                            }
                        }
                    }
                }
            }
        }

    }

    private boolean inRange(Cell cell) {
        if (cell.getX() > 0 && cell.getX() < rows - 1 && cell.getY() > 0 && cell.getY() < cols - 1) {
            return true;
        }
        return false;
    }

    private boolean isInRange(int x, int y) {
        if (x > 0 && x < rows - 1 && y > 0 && y < cols - 1) {
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
            if (cell.getState() != State.START) {
                maze[cell.getX()][cell.getY()].setState(State.PASSAGE);
            }
        }
    }

    public boolean isPass(Cell cell) {
        if (cell.getState() == State.PASSAGE || cell.getState() == State.START) {
            return true;
        }
        return false;
    }

    public boolean isWall(Cell cell) {
        if (cell.getState() == State.WALL) {
            return true;
        }
        return false;
    }

    public Cell pickRandWall() {
        int randPos = (int) (Math.random() * primWallList.size());
        return primWallList.get(randPos);
    }

    private void addWalls(Cell cell) {
        if (isInRange(cell.getX(), cell.getY() - 1)) {
            Cell upCell = maze[cell.getX()][cell.getY() - 1];
            if (!upCell.inMaze() && !isPass(upCell)) {
                upCell.setState(State.FRONTIER);
            }
            primWallList.add(upCell);
        }
        if (isInRange(cell.getX() + 1, cell.getY())) {
            Cell rightCell = maze[cell.getX() + 1][cell.getY()];
            if (!rightCell.inMaze() && !isPass(rightCell)) {
                rightCell.setState(State.FRONTIER);
            }
            primWallList.add(rightCell);
        }
        if (isInRange(cell.getX(), cell.getY() + 1)) {
            Cell downCell = maze[cell.getX()][cell.getY() + 1];
            if (!downCell.inMaze() && !isPass(downCell)) {
                downCell.setState(State.FRONTIER);
            }
            primWallList.add(downCell);
        }
        if (isInRange(cell.getX() - 1, cell.getY())) {
            Cell leftCell = maze[cell.getX() - 1][cell.getY()];
            if (!leftCell.inMaze() && !isPass(leftCell)) {
                leftCell.setState(State.FRONTIER);
            }
            primWallList.add(leftCell);
        }
    }

    public Cell getOppositeCell(Cell cell) {
        if (isInRange(cell.getX(), cell.getY() - 1)) {
            Cell upCell = maze[cell.getX()][cell.getY() - 1];
            if (isPass(upCell) && isInRange(cell.getX(), cell.getY() + 1)) {
                return maze[cell.getX()][cell.getY() + 1];
            }
        }
        if (isInRange(cell.getX() + 1, cell.getY())) {
            Cell rightCell = maze[cell.getX() + 1][cell.getY()];
            if (isPass(rightCell) && isInRange(cell.getX() - 1, cell.getY())) {
                return maze[cell.getX() - 1][cell.getY()];
            }
        }
        if (isInRange(cell.getX(), cell.getY() + 1)) {
            Cell downCell = maze[cell.getX()][cell.getY() + 1];
            if (isPass(downCell) && isInRange(cell.getX(), cell.getY() - 1)) {
                return maze[cell.getX()][cell.getY() - 1];
            }
        }
        if (isInRange(cell.getX() - 1, cell.getY())) {
            Cell leftCell = maze[cell.getX() - 1][cell.getY()];
            if (isPass(leftCell) && isInRange(cell.getX() + 1, cell.getY())) {
                return maze[cell.getX() + 1][cell.getY()];
            }
        }

        return cell;
    }

    public int passageNeighbors(Cell cell) {
        int num = 0;
        if (isInRange(cell.getX(), cell.getY() - 1)) {
            Cell upCell = maze[cell.getX()][cell.getY() - 1];
            if (isPass(upCell)) {
                num++;
            }
        }
        if (isInRange(cell.getX() + 1, cell.getY())) {
            Cell rightCell = maze[cell.getX() + 1][cell.getY()];
            if (isPass(rightCell)) {
                num++;
            }
        }
        if (isInRange(cell.getX(), cell.getY() + 1)) {
            Cell downCell = maze[cell.getX()][cell.getY() + 1];
            if (isPass(downCell)) {
                num++;
            }
        }
        if (isInRange(cell.getX() - 1, cell.getY())) {
            Cell leftCell = maze[cell.getX() - 1][cell.getY()];
            if (isPass(leftCell)) {
                num++;
            }
        }

        return num;
    }

    public int wallNeighbors(Cell cell) {
        int num = 0;
        if (isInRange(cell.getX(), cell.getY() - 1)) {
            Cell upCell = maze[cell.getX()][cell.getY() - 1];
            if (isWall(upCell)) {
                num++;
            }
        }
        if (isInRange(cell.getX() + 1, cell.getY())) {
            Cell rightCell = maze[cell.getX() + 1][cell.getY()];
            if (isWall(rightCell)) {
                num++;
            }
        }
        if (isInRange(cell.getX(), cell.getY() + 1)) {
            Cell downCell = maze[cell.getX()][cell.getY() + 1];
            if (isWall(downCell)) {
                num++;
            }
        }
        if (isInRange(cell.getX() - 1, cell.getY())) {
            Cell leftCell = maze[cell.getX() - 1][cell.getY()];
            if (isWall(leftCell)) {
                num++;
            }
        }

        return num;
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
                        g2d.setColor(Color.BLACK);
                        break;
                    case TEST2:
                        g2d.setColor(Color.WHITE);
                        break;
                    case TEST3:
                        g2d.setColor(Color.PINK);
                        break;
                    case TEST4:
                        g2d.setColor(Color.CYAN);
                        break;

                }
                g2d.fillRect(i * scale, j * scale, scale, scale);
            }
        }
        /**
         * g2d.setColor(Color.BLACK); for (int i = 0; i <= rows; i++) {
         * g2d.fillRect(i * scale, cols * scale, scale, scale); } for (int i =
         * 0; i <= cols; i++) { g2d.fillRect(rows * scale, i * scale, scale,
         * scale);
        }*
         */

    }

    /**
     * Create a list of all walls, and create a set for each cell, each
     * containing just that one cell. For each wall, in some random order: If
     * the cells divided by this wall belong to distinct sets: Remove the
     * current wall. Join the sets of the formerly divided cells.
     */
    public boolean kruskalsAlgorithmStep() {
        int randPos = (int) (Math.random() * KList.size());
        Edge randEdge = KList.get(randPos);

        KList.remove(randEdge);
        //maze[randEdge.getX()][randEdge.getY()].setState(State.TEST4);

        Cell node1 = randEdge.getFirst();
        Cell node2 = randEdge.getSecond();

        Tree tree1 = getTree(node1);
        Tree tree2 = getTree(node2);

        if (tree2 != null && !tree1.connected(tree2)) {
            numConnectedComponents--;
            tree1.connect(tree2);
            maze[randEdge.getX()][randEdge.getY()].setState(State.PASSAGE);
        }

        if (false) {
            System.out.println("numConnectedComponents: " + numConnectedComponents);
            System.out.println("rand edge: " + randEdge);
            System.out.println("node 1: " + node1);
            System.out.println("node 2: " + node2);
            System.out.println("tree 1: " + tree1.getID());
            System.out.println("tree 2: " + tree2.getID());
            System.out.println();
        }
        if (passageNeighbors(node1) == 1) {
            finalCell = node1;
        } else if (passageNeighbors(node2) == 1) {
            finalCell = node2;
        }
        if (numConnectedComponents == 1) {
            markEnd(finalCell);
        }
        return numConnectedComponents == 1;
        //return true;
    }

    public Tree getTree(Cell cell) {
        for (Tree t : cellSets) {
            if (t.hasCell(cell)) {
                return t;
            }
        }
        return null;
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

        if (!oppCell.inMaze()) {
            makePassage(randWall);
            makePassage(oppCell);
            oppCell.putInMaze();
            addWalls(oppCell);
        } else {
            primWallList.remove(randWall);
        }
        if (passageNeighbors(oppCell) == 1) {
            finalCell = oppCell;
        }
        if (primWallList.isEmpty()) {
            markEnd(finalCell);
        }
        if (maze[2][2].getState() == State.WALL && maze[2][1].getState()== State.PASSAGE && 
                maze[1][2].getState() == State.PASSAGE && maze[3][2].getState() == State.PASSAGE &&
                maze[2][3].getState() == State.PASSAGE) {
            maze[2][1].setState(State.WALL);
        }
        //System.out.println(primWallList.size());
        return primWallList.isEmpty();
    }
}
