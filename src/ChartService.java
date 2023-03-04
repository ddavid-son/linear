import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.util.ExportUtils;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import java.awt.*;
import java.io.File;
import java.util.*;

public class ChartService {

    private static final Map<String, ArrayList<ChartItem>> charts = new HashMap<>();

    private static ArrayList<XYSeriesCollection> buildLines(ArrayList<ChartItem> items) {

        // convert our interface to jfree type- XYSeries
        Map<String, XYSeries> lines = new HashMap<>();
        items.forEach(item -> {
            String lineName = item.line;
            if (!lines.containsKey(lineName)) {
                lines.put(item.line, new XYSeries(item.line));
            }

            XYSeries curLine = lines.get(item.line);
            curLine.add(item.xVal, item.yVal);
            lines.put(item.line, curLine);
        });

        // build jfree collection/dataset
        ArrayList<XYSeriesCollection> datasets = new ArrayList<>();
        lines.forEach((key, value) -> {
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries(value);
            datasets.add(dataset);
        });

        return datasets;
    }

    public static void addChartItem(
            String chartId,
            String lineId,
            double xVal,
            double yVal
    ) {
        if (!charts.containsKey(chartId)) {
            charts.put(chartId, new ArrayList<>());
        }

        ArrayList<ChartItem> items = charts.get(chartId);
        items.add(new ChartItem(lineId, xVal, yVal));
        charts.put(chartId, items);
    }

    public static void saveChart(
            String chartId,
            String title,
            String xLabel,
            String yLabel,
            String filename
    ) {
        // get our items
        ArrayList<ChartItem> items = charts.get(chartId);

        // build jfree lines from our items
        ArrayList<XYSeriesCollection> lines = buildLines(items);

        // create new jfree chart
        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                xLabel,
                yLabel,
                null, // dataset will be added later
                PlotOrientation.VERTICAL,
                true,
                true,
                false);

        ArrayList<Color> colors = new ArrayList<>();
        colors.add(Color.BLUE);
        colors.add(Color.RED);
        colors.add(Color.CYAN);
        colors.add(Color.BLACK);
        colors.add(Color.GREEN);


        // add lines to jfree chart
        XYPlot plot = chart.getXYPlot();
        int serieNum = 0;
        for (XYSeriesCollection line : lines) {
            plot.setDataset(serieNum, line);
            XYLineAndShapeRenderer renderer1 = new XYLineAndShapeRenderer();
            renderer1.setSeriesPaint(serieNum, colors.get(serieNum));
            plot.setRenderer(serieNum, renderer1);
            serieNum++;
        }

        try {
            // save chart to file
            File file = new File(filename); // e.g "multiple-lines-chart.png"
            ExportUtils.writeAsPNG(chart, 800, 600, file);
        } catch (Exception err) {
            System.out.println("[ChartService][saveChart] failed to save chart");
        }
    }
}
