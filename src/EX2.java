public class EX2 {

    public static void run() {
        try {
            for (int t = 2; t <= 6; t++) {
                runPageRank(
                        GraphType.Clique,
                        Utils.randInt(0, (int)Math.pow(2, 14)), // startingVertex
                        (int)Math.pow(2, t) // max iterations
                );

                runPageRank(
                        GraphType.Chain,
                        Utils.randInt(0, (int)Math.pow(2, 14)), // startingVertex
                        (int)Math.pow(2, t) // max iterations
                );

                runPageRank(
                        GraphType.Candy,
                        Utils.randInt((int)Math.pow(2, 13), (int)Math.pow(2, 14)), // startingVertex
                        (int)Math.pow(2, t) // max iterations
                );

                runPageRank(
                        GraphType.CCLiques,
                        Utils.randInt((int)Math.pow(2, 13), (int)Math.pow(2, 14)), // startingVertex
                        (int)Math.pow(2, t) // max iterations
                );
            }

            // start with union distribution
            for (GraphType graphType : GraphType.values()) {
                runPageRank(
                        graphType,
                        -1,
                        32
                );
            }

        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    private static void runPageRank(
            GraphType graphType,
            int startingVertex,
            int maxIterations
    ) throws Exception {
        System.out.println("\n================== " + graphType.name() + " ==================");
        Graph graph = GraphFactory.createGraph(graphType);
        graph.normalize();
        double[] result = graph.pageRankAlgoV2(Math.pow(2, -6), maxIterations, startingVertex);


        // create chartId
        String chartId;
        if (startingVertex == -1) {
            chartId = String.format("%s- Union Distribution- N=%d", graphType.name(), maxIterations);
        } else {
            chartId = String.format("%s- Starting Vertex=%d, N=%d", graphType.name(), startingVertex, maxIterations);
        }

        // create histogram
        for (int i=0; i < result.length; i++) {
            ChartService.addChartItem(
                    chartId,
                    "probability",
                    i,
                    result[i]
            );
        }

        ChartService.saveChart(
                chartId,
                chartId,
                "Vertex",
                "Probability",
                "dist/EX2/" + chartId + ".png"
        );
    }
}
