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
		XYSeries series = new XYSeries("Prim's Algorithm");
		
		for (int i = 1000; i <= 30000; i+=100) {
			long startTime = System.currentTimeMillis();
			Maze m = new Maze(i, i, 10);
			long endTime = System.currentTimeMillis();
			series.add(i, (int)(endTime-startTime));
		}
		
		result.addSeries(series);
		JFreeChart chart = ChartFactory.createScatterPlot("Test Data", "Maze Size", "Time (ms)", result);
		ChartFrame frame = new ChartFrame("Test Data", chart);
		frame.pack();
		frame.setVisible(true);
	}

}
