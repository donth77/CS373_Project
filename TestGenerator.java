package mazegen;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class TestGenerator {
	
	public TestGenerator() {
		runTests();
	}
	
	public void runTests() {
		
		XYSeriesCollection result = new XYSeriesCollection();
                int cellSize = 10;
                int min = 100;
                int max = 1000;
                int increment = 100;
                String title = "Minimum Spanning Tree Algorithms for Random Maze Generation";
                
              XYSeries seriesP = new XYSeries("Prim's Algorithm");
              XYSeries seriesK = new XYSeries("Kruskal's Algorithm");
              
		for (int i = min; i <= max; i+= increment) {
			Maze mp = new Maze(i, i, cellSize, 0);
                        long startTimeP = System.currentTimeMillis();
                        while (!mp.primsAlgorithmStep());
			long endTimeP = System.currentTimeMillis();
                        long durationP = endTimeP - startTimeP;
                        System.out.println("prim's: " + durationP  + " ms");
			seriesP.add(i, durationP);
                        
                        Maze mk = new Maze(i, i, cellSize, 1);
                        long startTimeK = System.currentTimeMillis();
                        while (!mk.kruskalsAlgorithmStep());
			long endTimeK = System.currentTimeMillis();
                        long durationK = endTimeK - startTimeK;
                        System.out.println("kruskal's: " + durationK + " ms");
			seriesK.add(i, durationK);
		}
                
                result.addSeries(seriesP);
                result.addSeries(seriesK);
		JFreeChart chart = ChartFactory.createScatterPlot(title, "Maze Size", "Time (ms)", result);
		ChartFrame frame = new ChartFrame(title, chart);
		frame.pack();
		frame.setVisible(true);
	}
        
        public static void main(String[] args){
            TestGenerator tests = new TestGenerator();
        }

}
