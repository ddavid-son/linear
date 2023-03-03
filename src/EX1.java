public class EX1 {

    static long timeout = 1000 * 60 * 5;

    public static void run() {
        try {
            System.out.println("clique:");
            runCoverAlgorithm(GraphType.Clique, 0);

            System.out.println("chain:");
            runCoverAlgorithm(GraphType.Chain, 0);

            System.out.println("candy:");
            runCoverAlgorithm(GraphType.Candy, 16000);

            System.out.println("ccliques:");
            runCoverAlgorithm(GraphType.CCLiques, 16000);

        } catch (Exception err) {
            System.out.println("err");
        }
    }


    public static void runCoverAlgorithm(GraphType graphType, int startingVertex) throws Exception {
        long graphAvg = 0, graphMax = 0, graphMin = Long.MAX_VALUE;
        Graph graph = GraphFactory.create(graphType);
        System.out.println("graph created");

        for (int i = 0; i < 5; i++) {
            long time = graph.coverTime(-1, timeout);
            graphAvg += time;
            graphMax = Math.max(graphMax, time);
            graphMin = Math.min(graphMin, time);
            System.out.println("run number: " + i + " time: " + time);
        }

        System.out.println();
        System.out.println("Random statistics:");
        System.out.println("graphAvg: " + graphAvg / 5);
        System.out.println("graphMax: " + graphMax);
        System.out.println("graphMin: " + graphMin);
        System.out.println();

        graphAvg = graphMax = 0;
        graphMin = Long.MAX_VALUE;

        for (int i = 0; i < 5; i++) {
            long time = graph.coverTime(startingVertex, timeout);
            graphAvg += time;
            graphMax = Math.max(graphMax, time);
            graphMin = Math.min(graphMin, time);
        }

        System.out.println();
        System.out.println("Not Random statistics:");
        System.out.println("graphAvg: " + graphAvg / 5);
        System.out.println("graphMax: " + graphMax);
        System.out.println("graphMin: " + graphMin);
        System.out.println();
        System.out.println("===================================");
        System.out.println();
    }

}
