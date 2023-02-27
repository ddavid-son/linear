
public class Main {

    static long timeout = 1000 * 60 * 5;

    public static void runCoverAlgorithm(String graphType, int startingVertex) throws Exception {
        long graphAvg = 0, graphMax = 0, graphMin = Long.MAX_VALUE;
        Graph graph;
        switch (graphType) {
            case "clique":
                graph = cliqueGraph();
                break;
            case "chain":
                graph = chainGraph();
                break;
            case "candy":
                graph = candyGraph();
                break;
            case "ccliques":
                graph = chainedCliquesGraph();
                break;
            default:
                throw new Exception("Not supported !!! (yet...)");
        }

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

    public static void printRatioV12(String graphType, double epsilon) throws Exception {
        Graph graph;
        System.out.println("building graph...");
        switch (graphType) {
            case "clique":
                graph = cliqueGraph();
                break;
            case "chain":
                graph = chainGraph();
                break;
            case "candy":
                graph = candyGraph();
                break;
            case "ccliques":
                graph = chainedCliquesGraph();
                break;
            default:
                throw new Exception("Not supported !!! (yet...)");
        }
        System.out.println("building graph completed");

        graph.normalize();
        double[][] eigenVecVal = graph.powerIteration(epsilon);
        double[] v1 = eigenVecVal[1]; // vpi
        double[] w0 = createRandomVector((int) Math.pow(2, 14));

        var ut = graph.subtract(w0, graph.projection(w0, v1));
        double[] wt;
        int iter = 0;
        while (true) {
            wt = graph.matrixVectorProduct(ut);
            double[] vt = graph.subtract(wt, graph.projection(wt, v1));
            double[] prevUt = ut;
            double vtNorm = graph.norm(vt);
            ut = graph.scalarDivision(vt, vtNorm);

            if (iter % 1 == 0) {
                System.out.printf("iter %d\n", iter);
                System.out.printf("epsilon %f\n", epsilon);
                System.out.printf("delta %f\n", graph.norm(graph.subtract(ut, prevUt)));
            }

            if (graph.norm(graph.subtract(ut, prevUt)) < epsilon) {
                break;
            }

            iter += 1;
        }

        System.out.printf("graphType %s\n", graphType);
        System.out.printf("epsilon %s\n", epsilon);
        System.out.printf("result: %f\n\n", graph.computeEigenvalue(v1) / graph.computeEigenvalue(ut));
    }

    public static void EX1() {
        try {
            // System.setOut(new PrintStream(new File("output-file.txt")));

            System.out.println("clique:");
            //runCoverAlgorithm("clique", 0);

            System.out.println("chain:");
            //runCoverAlgorithm("chain", 0);

            System.out.println("candy:");
            //runCoverAlgorithm("candy", 16000);

            System.out.println("ccliques:");
            runCoverAlgorithm("ccliques", 16000);

        } catch (Exception err) {
            System.out.println("err");
        }
    }

    public static void EX2() {
        runPageRank("clique", 0);
        runPageRank("chain", 0);
        runPageRank("candy", 16000);
        runPageRank("ccliques", 16000);
    }

    public static void EX3() {

        try {
            System.out.println("clique:");
            printRatioV12("clique", Math.pow(2, -2));
            // printRatioV12("clique", Math.pow(2, -3));
            // printRatioV12("clique", Math.pow(2, -4));
            // printRatioV12("clique", Math.pow(2, -5));
            // printRatioV12("clique", Math.pow(2, -6));

            System.out.println("chain:");
            printRatioV12("chain", Math.pow(2, -2));
//        printRatioV12("chain", Math.pow(2, -3));
//        printRatioV12("chain", Math.pow(2, -4));
//        printRatioV12("chain", Math.pow(2, -5));
//        printRatioV12("chain", Math.pow(2, -6));

            System.out.println("candy:");
            printRatioV12("candy", Math.pow(2, -2));
//        printRatioV12("candy", Math.pow(2, -3));
//        printRatioV12("candy", Math.pow(2, -4));
//        printRatioV12("candy", Math.pow(2, -5));
//        printRatioV12("candy", Math.pow(2, -6));

            System.out.println("ccliques:");
            printRatioV12("ccliques", Math.pow(2, -2));
//        printRatioV12("ccliques", Math.pow(2, -3));
//        printRatioV12("ccliques", Math.pow(2, -4));
//        printRatioV12("ccliques", Math.pow(2, -5));
//        printRatioV12("ccliques", Math.pow(2, -6));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // create reand vectorq
    public static double[] createRandomVector(int size) {
        double[] vector = new double[size];
        for (int i = 0; i < size; i++) {
            vector[i] = Math.random();
        }
        return vector;
    }

    private static void runPageRank(String graphType, int startVortex) {
        Graph graph;
        switch (graphType) {
            case "chain":
                graph = chainGraph();
                break;
            case "candy":
                graph = candyGraph();
                break;
            case "ccliques":
                graph = chainedCliquesGraph();
                break;
            default:
                graph = cliqueGraph();
                break;
        }

        System.out.println("\n================== " + graph.type + " ==================");
        graph.pageRankAlgo(Math.pow(2, -6), (int) Math.pow(2, 1), startVortex);
    }

    public static void main(String[] args) {
//        EX1();
//        EX2();
        EX3();
    }

    // ==================== Graphs ==================== //
    private static Graph candyGraph() {
        Graph candy = new Graph((int) Math.pow(2, 14), "candy");

        int i = 0;
        for (; i < Math.pow(2, 13); i++) {
            candy.addEdge(i, i, true);
            candy.addEdge(i, i + 1, false);
        }

        for (; i < Math.pow(2, 14); i++) {
            for (int j = (int) Math.pow(2, 13); j < Math.pow(2, 14); j++) {
                candy.addEdge(i, j, true);
            }
        }

        return candy;
    }

    private static Graph chainGraph() {
        Graph chain = new Graph((int) Math.pow(2, 14), "chain");

        int i = 0;
        for (; i < Math.pow(2, 14) - 1; i++) {
            chain.addEdge(i, i, true);
            chain.addEdge(i, i + 1);
        }

        chain.addEdge(i, 0);
        return chain;
    }

    private static Graph cliqueGraph() {
        Graph graph = new Graph((int) Math.pow(2, 14), "clique");

        for (int i = 0; i < Math.pow(2, 14); i++) {
            for (int j = 0; j < Math.pow(2, 14); j++) {
                graph.addEdge(i, j, true);
            }
        }

        return graph;
    }

    private static Graph chainedCliquesGraph() {
        Graph chainedCliques = new Graph((int) Math.pow(2, 14), "ccliques");

        int i = 0;
        for (; i < Math.pow(2, 13); i++) {
            chainedCliques.addEdge(i, i, true);
            chainedCliques.addEdge(i, i + 1);
        }

        for (; i < Math.pow(2, 13) + Math.pow(2, 12); i++) {
            for (int j = (int) Math.pow(2, 13); j < Math.pow(2, 13) + Math.pow(2, 12); j++) {
                chainedCliques.addEdge(i, j, true);
            }
        }

        for (; i < Math.pow(2, 14); i++) {
            for (int j = (int) Math.pow(2, 13) + (int) Math.pow(2, 12); j < Math.pow(2, 14); j++) {
                chainedCliques.addEdge(i, j, true);
            }
        }

        chainedCliques.addEdge(0, (int) Math.pow(2, 14) - 1);
        return chainedCliques;
    }
}