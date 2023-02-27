
public class Main {

    static long timeout = 1000 * 60 * 5;

    public static void runCoverAlgorithm(String graphType, int startingVertex) throws Exception {
        long graphAvg = 0, graphMax = 0, graphMin = Long.MAX_VALUE;
        Graph graph = switch (graphType) {
            case "clique" -> cliqueGraph();
            case "chain" -> chainGraph();
            case "candy" -> candyGraph();
            case "ccliques" -> chainedCliquesGraph();
            default -> throw new Exception("Not supported !!! (yet...)");
        };

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
//        Graph graph = cliqueGraph();
//        // Perform power iteration on the original matrix and get the largest eigenvalue and eigenvector
//        double[][] result1 = graph.powerIteration(0.15);
//        double lambda1 = result1[0][0]; // The largest eigenvalue
//        double[] x1 = result1[1]; // The corresponding eigenvector
//
//        // Perform deflation on the original matrix using lambda1 and x1
//        graph.deflate(lambda1, x1);
//
//        // Perform power iteration on the deflated matrix and get the second largest eigenvalue
//        double[][] result2 = graph.powerIteration(0.15);
//        double lambda2 = result2[0][0]; // The second largest eigenvalue
//        System.out.println("first eigenvalue: " + lambda1);
//        System.out.println("second eigenvalue: " + lambda2);
//        System.out.println("ratio: " + lambda2 / lambda1);

    }

    public static void ex3() {
        Graph graph = cliqueGraph();
        double[][] eigenVecVal = graph.powerIteration(0.15);
        double[] v1 = eigenVecVal[1]; // vpi
        double[] randVec = createRandomVector((int) Math.pow(2, 14));
        double[] projectionV1 = graph.projection(randVec, v1); // vpos

        var ut = graph.subtract(randVec, projectionV1); // u_t
        double[] w_t; // w_t
        while (true) {
            w_t = graph.matrixVectorProduct(ut);
            double[] v_t = graph.subtract(w_t, graph.projection(w_t, v1)); // v_t
            double[] prevUT = ut; // u_t_minus
            double vtNorm = graph.norm(v_t);
            ut = graph.scalarDivision(v_t, vtNorm);

            if (graph.norm(graph.subtract(ut, prevUT)) < Math.pow(2, -6)) {
                break;
            }
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
        Graph graph = switch (graphType) {
            case "chain" -> chainGraph();
            case "candy" -> candyGraph();
            case "ccliques" -> chainedCliquesGraph();
            default -> cliqueGraph();
        };

        System.out.println("\n================== " + graph.type + " ==================");
        graph.pageRankAlgo(Math.pow(2, -6), (int) Math.pow(2, 1), startVortex);
    }

    public static void main(String[] args) {
        EX1();
//        EX2();
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