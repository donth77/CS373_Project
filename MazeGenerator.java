/**import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Font;**/
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

class Cell{
    private int x;
    private int y;
    
    public Cell(int x, int y){
        this.x = x;
        this.y = y;
    }
    
    public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public void set(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public String toString(){
		return "(" + x + ", " + y + ")";
	}
}

class Maze extends JPanel {
    
    private int height;
    private int width;
    private int scale;
    
    private int rows;
    private int cols;
    
    //2d array for maze
    int maze[][];
    
    //use this to debug
    private boolean DEBUG = false;
    
    //this wallList contains the so-called frontier cells
    List<Cell> wallList = new ArrayList<>();
    
    public Maze(int height, int width, int scale){
        this.height = height;
        this.width = width;
        this.scale = scale;
        rows = height / scale;
        cols = width / scale;
        maze = new int[rows][cols];
        
        //Start with a grid full of walls
        //Our maze is like this: the top left corner is (0, 0) where x = 0 and y = 0
        //x increases as you move to the right of the maze
        //y increases as you move down the maze
        initialize();
        
        primsAlgorithm();
    }

	//enum for state
    public enum State {
        PASSAGE(0), FRONTIER(1), WALL(2), START(3), END(4);
        private final int value;

        private State(int value) {
            this.value = value;
        }
    }
    
    //similar to state enum, used for direction
    public enum Direction {
		UP(0), RIGHT(1), DOWN(2), LEFT(3);
        private final int value;

        private Direction(int value) {
            this.value = value;
        }
    }
   
    @Override
    //paintComponent is called last
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Font font = new Font("Arial", Font.PLAIN, 12);
        Graphics2D g2d = (Graphics2D) g;    
		g2d.setFont(font);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
       
        //draw grid
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
				g2d.setColor(Color.BLACK);
				g2d.drawRect(i * scale, j * scale, scale, scale);
                switch (maze[i][j]) {
                    case 0:
                        g2d.setColor(Color.WHITE);
                        break;
                    case 1:
                        g2d.setColor(Color.RED);
                        break;
                    case 2:
                        g2d.setColor(Color.BLACK);
                        break;
                    case 3:
						g2d.setColor(Color.GREEN);
						break;
					case 4:
						g2d.setColor(Color.RED);
						break;
                }
                String val = String.valueOf(maze[i][j]);
				//g2d.drawString(val, i * scale, j * scale);
                g.fillRect(i * scale, j * scale, scale, scale);
            }
        }
        

    }

    private void initialize() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                maze[i][j] = State.WALL.value;
            }
        }
    }
    
    /**
		Start with a grid full of walls.
		Pick a cell, mark it as part of the maze. Add the walls of the cell to the wall list.
		While there are walls in the list:
		Pick a random wall from the list. If the cell on the opposite side isn't in the maze yet:
		Make the wall a passage and mark the cell on the opposite side as part of the maze.
		Add the neighboring walls of the cell to the wall list.
		Remove the wall from the list.
     */
    private void primsAlgorithm(){
		//starting cell
		int startX = 1;
		int startY = 1;
		Cell currCell = new Cell(startX, startY);
		Cell lastCell = null;
		System.out.println("start at " + currCell);
		//marking starting cell as part of the maze
		pickStart(currCell);
		
		//adding the walls of the cell to the wall list
		addWalls(currCell);
		
		while(!wallList.isEmpty()){
		//for(int i = 0; i < 200; i++){
			if(DEBUG) printWallList();
			int randWallPos = (int) (Math.random() * wallList.size());
			Cell randCell = wallList.get(randWallPos);
			lastCell = randCell;
			
			maze[randCell.getX()][randCell.getY()] = State.PASSAGE.value;
			
			//currCell.set(randCell.getX(), randCell.getY());
			
			//the cell on the opposite side needs to be checked; we need this value so we can know which cell to check
			//direction can be 0 - up, 1 - right, 2 - down, 3 - left in a clockwise fashion, because clocks are cool
			/**int direction = getDirection(currCell, randCell);
			String dir;
			if(direction == 0){
				dir = "UP";
			}else if(direction == 1){
				dir = "RIGHT";
			}else if(direction == 2){
				dir = "DOWN";
			}else if(direction == 3){
				dir = "LEFT";
			}else{
				dir = "NULL";
			}
			System.out.println("Direction: " + dir);**/
			
			/**switch(direction){
				case 0: //UP
					if(maze[randCell.getX()][randCell.getY() - 1] == State.WALL.value){
						maze[randCell.getX()][randCell.getY() - 1] = State.FRONTIER.value;
					}
					break;
				case 1: // RIGHT
					if(maze[randCell.getX() + 1][randCell.getY()] == State.WALL.value){
						maze[randCell.getX() + 1][randCell.getY()] = State.FRONTIER.value;
					}
					break;
				case 2: //DOWN
					if(maze[randCell.getX()][randCell.getY() + 1] == State.WALL.value){
						maze[randCell.getX()][randCell.getY() + 1] = State.FRONTIER.value;
					}
					break;
				case 3: // LEFT
					if(maze[randCell.getX() - 1][randCell.getY()] == State.WALL.value){
						maze[randCell.getX() - 1][randCell.getY()] = State.FRONTIER.value;
					}
					break;
			}**/
			makePassage(randCell);
			addWalls(randCell);
			removeFromList(randCell);
			if(DEBUG){
				printWallList();        
				printMaze();
			}
		//}
		}
		
		markEnd(lastCell);
		
		
	}
        
    private void pickStart(Cell startCell) {
        maze[startCell.getX()][startCell.getY()] = State.START.value;
    }
    
    private void markEnd(Cell endCell) {
        maze[endCell.getX()][endCell.getY()] = State.END.value;
    }
    
    private void removeFromList(Cell cell){
		for(int i = 0; i < wallList.size(); i++){
			if(wallList.get(i).getX() == cell.getX() && wallList.get(i).getY() == cell.getY()){
				wallList.remove(wallList.get(i));
			}
		}
	}
    
    private void makePassage(Cell randCell){
		maze[randCell.getX()][randCell.getY()] = State.PASSAGE.value;
	}
    
    private int getDirection (Cell currCell, Cell randCell) {
		System.out.println("current cell's X: " + currCell.getX());
		System.out.println("current cell's Y: " + currCell.getY());
		System.out.println("random cell's X: " + randCell.getX());
		System.out.println("random cell's Y: " + randCell.getY());
		if(currCell.getX() == randCell.getX() && currCell.getY() + 1 == randCell.getY()){
			//up
			return Direction.UP.value;
		}else if(currCell.getX() + 1 == randCell.getX() && currCell.getY() == randCell.getY()){
			//right
			return Direction.RIGHT.value;
		}else if(currCell.getX() == randCell.getX() && currCell.getY() - 1 == randCell.getY()){
			//down
			return Direction.DOWN.value;
		}else if(currCell.getX() - 1 == randCell.getX() && currCell.getY() == randCell.getY()){
			//left
			return Direction.LEFT.value;
		}
		return -1; //this cannot happen; invalid
	}
    
    private void addWalls(Cell cell){
		System.out.print("add walls to (" + cell.getX());
		System.out.print(", " + cell.getY() + ")\n");
		System.out.println("rows: " + (width / scale));
		System.out.println("cols: " + (height / scale));
		//bounds checking, we need to leave a border around the maze
		if(cell.getY() + 1 > 0 && cell.getY() + 1 < cols){
			//check up wall
			if(maze[cell.getX()][cell.getY() + 1] == State.WALL.value) {
				Cell upCell = new Cell(cell.getX(), cell.getY() + 1);
				if(isValidFrontier(upCell)){
					wallList.add(upCell);
					maze[cell.getX()][cell.getY() + 1] = State.FRONTIER.value;
				}
			}
		}
		if(cell.getX() + 1 > 0 && cell.getX() + 1 < rows){
			//check right wall
			if(maze[cell.getX() + 1][cell.getY()] == State.WALL.value) {
				Cell rightCell = new Cell(cell.getX() + 1, cell.getY());
				if(isValidFrontier(rightCell)){
					wallList.add(rightCell);
					maze[cell.getX() + 1][cell.getY()] = State.FRONTIER.value;
				}
			}
		}
		if(cell.getY() - 1 > 0 && cell.getY() - 1 < cols){
			//check down wall
			if(maze[cell.getX()][cell.getY() - 1] == State.WALL.value) {
				Cell downCell = new Cell(cell.getX(), cell.getY() - 1);
				if(isValidFrontier(downCell)){
					wallList.add(downCell);
					maze[cell.getX()][cell.getY() - 1] = State.FRONTIER.value;
				}
			}
		}
		if(cell.getX() - 1 > 0 && cell.getX() - 1 < rows){
			//check left wall
			if(maze[cell.getX() - 1][cell.getY()] == State.WALL.value) {
				Cell leftCell = new Cell(cell.getX() - 1, cell.getY());
				if(isValidFrontier(leftCell)){
					wallList.add(leftCell);
					maze[cell.getX() - 1][cell.getY()] = State.FRONTIER.value;
				}
			}
		}

    }
    
    private boolean isValidFrontier(Cell cell){
		int walls = 0;
		//check up wall
		if(cell.getY() < cols - 1){
			if(maze[cell.getX()][cell.getY() + 1] == State.WALL.value) {
				walls++;
			}
		}
		//check right wall
		if(cell.getX() < rows - 1){
			if(maze[cell.getX() + 1][cell.getY()] == State.WALL.value) {
				walls++;
			}
		}
		//check down wall
		if(cell.getY() > 0){
			if(maze[cell.getX()][cell.getY() - 1] == State.WALL.value) {
				walls++;
			}
		}
		//check left wall
		if(cell.getX() > 0){
			if(maze[cell.getX() - 1][cell.getY()] == State.WALL.value) {
				walls++;
			}
		}
		
		if(walls == 3){
			return true;
		}
		return false;
	}
    
    //prints maze for debugging
    private void printMaze() {
        for (int i = 0; i < rows; i++) {
			System.out.println();
            for (int j = 0; j < cols; j++) {
                System.out.print(maze[j][i] + " ");
            }
        }
    }
    
    //prints wall list for debugging
    private void printWallList(){
		System.out.println("Wall List: " );
		for(int i = 0; i < wallList.size(); i++){
			System.out.println("Wall list at " + i);
			System.out.println(wallList.get(i));
			//System.out.println("X: " + wallList.get(i).getX());
			//System.out.println("Y: " + wallList.get(i).getY());
		}
	}

}

public class MazeGenerator {

    static int HEIGHT = 337;
    static int WIDTH = 315;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                }
                
                int height = 300;
                int width = 300;
                int scale = 10;
                
                Maze maze = new Maze(height, width, scale);

                JFrame window = new JFrame();
                window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                window.setSize(WIDTH, HEIGHT);
                window.add(maze);
                //window.setResizable(false);
                window.setVisible(true);
            }
        });
    }

}
