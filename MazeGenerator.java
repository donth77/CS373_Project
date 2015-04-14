package prim;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
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
}

class Maze extends JPanel {
    
    private int height;
    private int width;
    private int scale;
    
    private int rows;
    private int cols;
    
    int maze[][];
    
    List<Cell> wallList = new ArrayList<>();
    
    public Maze(int height, int width, int scale){
        this.height = height;
        this.width = width;
        this.scale = scale;
        rows = height / scale;
        cols = width / scale;
        maze = new int[rows][cols];
    }

    public enum State {

        PASSAGE(0), FRONTIER(1), WALL(2);
        private final int value;

        private State(int value) {
            this.value = value;
        }

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //Start with a grid full of walls
        initialize();
        
        //Pick starting cell
        pickStart();
       
        //draw grid
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                switch (maze[i][j]) {
                    case 0:
                        g.setColor(Color.WHITE);
                        break;
                    case 1:
                        g.setColor(Color.RED);
                        break;
                    case 2:
                        g.setColor(Color.BLACK);
                        break;

                }
                g.drawRect(i * scale, j * scale, scale, scale);
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

    private void pickStart() {
        int startX = 1;
        int startY = 1;
        maze[startY][startX] = State.PASSAGE.value;
    }
    
    private void addWalls(int x, int y){
        if(x > 0){
            
        }
    }

    private void printMaze() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(maze[i][j] + " ");
            }
            System.out.println();
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
                int scale = 30;
                
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
