/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mazegen;

import java.awt.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

/**
 *
 * @author Thomas
 */
public class MazeGenerator extends JPanel {

    private static int displayOffX, displayOffY;

    private final Color BackgroundColor = new Color(63, 68, 71);
    private final Color BorderColor = new Color(37, 40, 41);

    private JButton generateButton = new JButton("Generate Maze");

    private JButton pauseButton = new JButton("Pause");

    private JButton continueButton = new JButton("Continue");

    private JButton stepButton = new JButton("Step");
    
    private JRadioButton primsButton = new JRadioButton("Prim's");
    private JRadioButton kruskalsButton = new JRadioButton("Kruskal's");

    private static final Dimension buttonDim = new Dimension(150, 32);
    private static final Dimension buttonDim2 = new Dimension(600, 100);

    private static final Dimension largeFrame = new Dimension(2480, 1370);
    private static final Dimension midFrame = new Dimension(1240, 685);
    private static final Dimension smallFrame = new Dimension(620, 342);

    private static final int MaxWidth = 120, MaxHeight = 80, BorderWidth = 8;
    private static int cellScale = 10;

    private Maze maze;

    private static int mazeHeight;
    private static int mazeWidth;

    private State state = State.Idle;

    private Timer timer;

    private static JSlider animationSlider = new JSlider(0, 100);

    private static JCheckBox checkBox = new JCheckBox();
    
    private int algorithm = 1;

    private enum State {

        Idle, Generating
    }

    public MazeGenerator() {
        setLayout(new FlowLayout());

        timer = new Timer(0, new TimerListener());
        timer.setInitialDelay(0);

        MouseListener listener = new MouseListener();

        generateButton.setLocation(displayOffX + 25, displayOffY + 40);
        generateButton.setSize(buttonDim2);
        generateButton.addMouseListener(listener);
        add(generateButton);

        pauseButton.setLocation(displayOffX + 25, displayOffY + 80);
        pauseButton.setSize(buttonDim2);
        pauseButton.addMouseListener(listener);
        add(pauseButton);

        continueButton.setLocation(displayOffX + 25, displayOffY + 120);
        continueButton.setSize(buttonDim2);
        continueButton.addMouseListener(listener);
        continueButton.setEnabled(false);
        add(continueButton);

        stepButton.setLocation(displayOffX + 25, displayOffY + 160);
        stepButton.setSize(buttonDim2);
        stepButton.addMouseListener(listener);
        stepButton.setEnabled(false);
        add(stepButton);

        animationSlider.setLocation(displayOffX, displayOffY + 200);
        animationSlider.setInverted(true);
        animationSlider.setValue(timer.getDelay());
        add(animationSlider);
        
        primsButton.setLocation(displayOffX + 25, displayOffY + 40);
        primsButton.setSize(buttonDim2);
        primsButton.addMouseListener(listener);
        add(primsButton);
        
        kruskalsButton.setLocation(displayOffX + 25, displayOffY + 40);
        kruskalsButton.setSize(buttonDim2);
        kruskalsButton.addMouseListener(listener);
        kruskalsButton.setSelected(true);
        add(kruskalsButton);

        add(checkBox);
    }

    private void createMaze() {
        maze = new Maze(2300, 1200, 100, algorithm);
        state = State.Generating;
    }

    public void buildMaze() {
        if (!checkBox.isSelected()) {
            if(algorithm == 0){
                while (!maze.primsAlgorithmStep());
            }else{
                while (!maze.kruskalsAlgorithmStep());
            }
            repaint();
            setIdle();
        } else {
            if(algorithm == 0){
                if (maze.primsAlgorithmStep()) {
                    setIdle();
                    timer.setDelay(animationSlider.getValue());
                }
            }else{
                if (maze.kruskalsAlgorithmStep()) {
                    setIdle();
                    timer.setDelay(animationSlider.getValue());
                }   
            }
            repaint();
        }
    }

    private void setIdle() {
        timer.stop();
        state = State.Idle;
        stepButton.setEnabled(true);
    }

    public class MouseListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getSource() == generateButton) {
                createMaze();
                pauseButton.setEnabled(true);
                timer.start();
            } else if (e.getSource() == pauseButton) {
                timer.stop();
                pauseButton.setEnabled(false);
                stepButton.setEnabled(true);
                continueButton.setEnabled(true);
            } else if (e.getSource() == continueButton) {
                timer.start();
                pauseButton.setEnabled(true);
                stepButton.setEnabled(false);
            } else if (e.getSource() == stepButton) {
                timer.stop();
                buildMaze();
            } else if(e.getSource() == primsButton){
                algorithm = 0;
                kruskalsButton.setSelected(false);
            } else if(e.getSource() == kruskalsButton){
                algorithm = 1;
                primsButton.setSelected(false);
            }
        }

    }

    public class TimerListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (state) {
                case Generating:
                    timer.setDelay(animationSlider.getValue());
                    stepButton.setEnabled(false);
                    continueButton.setEnabled(false);
                    buildMaze();
                    break;
                default:
                    return;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(BackgroundColor);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(BorderColor);
        g.fillRect(0, 0, getWidth(), BorderWidth);
        g.fillRect(getWidth() - BorderWidth, 0, BorderWidth, getHeight());

        /**g.fillRect(0, getHeight() - BorderWidth, getWidth(), BorderWidth);
        g.fillRect(0, 0, BorderWidth, getHeight());

        g.fillRect(displayOffX - BorderWidth, 0, BorderWidth, getHeight());**/

        // Paints the maze, offset by the border
        Graphics drawMaze = g.create();
        drawMaze.translate(BorderWidth - cellScale + 50, BorderWidth - cellScale + 50);
        if (maze != null) {
            maze.paint(drawMaze);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();

        frame.setSize(largeFrame);

        int offset = 250;

        displayOffX = frame.getWidth() - offset;
        displayOffY = 0;

        //mazeHeight = frame.getHeight() - BorderWidth;
        //mazeWidth = frame.getWidth() - displayOffX;
        MazeGenerator generator = new MazeGenerator();
        frame.add(generator);

        frame.setTitle("Maze Generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
        frame.setVisible(true);
    }

}
