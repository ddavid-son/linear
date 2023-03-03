public class EX3 {
    public static void run() {
        try {
            System.out.println("clique:");
            computeRatioV12(GraphType.Clique, Math.pow(2, -2));
            computeRatioV12(GraphType.Clique, Math.pow(2, -3));
            computeRatioV12(GraphType.Clique, Math.pow(2, -4));
            computeRatioV12(GraphType.Clique, Math.pow(2, -5));
            computeRatioV12(GraphType.Clique, Math.pow(2, -6));

            System.out.println("chain:");
            computeRatioV12(GraphType.Chain, Math.pow(2, -2));
            computeRatioV12(GraphType.Chain, Math.pow(2, -3));
            computeRatioV12(GraphType.Chain, Math.pow(2, -4));
            computeRatioV12(GraphType.Chain, Math.pow(2, -5));
            computeRatioV12(GraphType.Chain, Math.pow(2, -6));

            System.out.println("candy:");
            computeRatioV12(GraphType.Candy, Math.pow(2, -2));
            computeRatioV12(GraphType.Candy, Math.pow(2, -3));
            computeRatioV12(GraphType.Candy, Math.pow(2, -4));
            computeRatioV12(GraphType.Candy, Math.pow(2, -5));
            computeRatioV12(GraphType.Candy, Math.pow(2, -6));

            System.out.println("ccliques:");
            computeRatioV12(GraphType.CCLiques, Math.pow(2, -2));
            computeRatioV12(GraphType.CCLiques, Math.pow(2, -3));
            computeRatioV12(GraphType.CCLiques, Math.pow(2, -4));
            computeRatioV12(GraphType.CCLiques, Math.pow(2, -5));
            computeRatioV12(GraphType.CCLiques, Math.pow(2, -6));


            // save charts
            for (GraphType type : GraphType.values()) {
                ChartService.saveChart(String.format("EX3-%s", type.name()), type.name(), "Epsilon", "Eigenvalue", String.format("dist/EX3/%s.png", type.name()));
                for (int i = 2; i <= 6; i++) {
                    ChartService.saveChart(String.format("EX3-%s-v1", type.name()), String.format("%s-v1", type.name()), "Iteration", "Eigenvalue", String.format("dist/EX3/%s-v1.png", type.name()));
                    ChartService.saveChart(String.format("EX3-%s-v2", type.name()), String.format("%s-v2", type.name()), "Iteration", "Eigenvalue", String.format("dist/EX3/%s-v2.png", type.name()));
                }
            }
            ChartService.saveChart("EX3 ratios", "EX3 ratios", "Epsilon", "Ratio v1 and v2", "dist/EX3/Ratios.png");

        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    private static double computeRatioV12(
            GraphType graphType,
            double epsilon
    ) throws Exception {
        System.out.printf("start %s with %f\n", graphType.toString(), epsilon);
        Graph graph = GraphFactory.create(graphType);
        graph.normalize();

        double[] v1 = graph.powerIteration(epsilon);
        double v1Val = Utils.computeEigenValue(graph.getGraph(), v1);

        double[] v2 = graph.generalizedPowerIteration(v1, epsilon, 100);
        double v2Val = Utils.computeEigenValue(graph.getGraph(), v2);

        double ratio = v1Val / v2Val;

        ChartService.addChartItem(
                String.format("EX3-%s",graphType.name()), // chart of graph: v1 and v2 per epsilon
                "v1",
                epsilon,
                v1Val
        );

        ChartService.addChartItem(
                String.format("EX3-%s",graphType.name()), // chart of graph: v1 and v2 per epsilon
                "v2",
                epsilon,
                v2Val
        );

        ChartService.addChartItem(
                "EX3 ratios", // chart of EX3: graphs ratios per epsilon
                graphType.toString(),
                epsilon,
                ratio
        );

        System.out.printf("finish %s with %f\n", graphType.toString(), epsilon);
        return ratio;
    }
}