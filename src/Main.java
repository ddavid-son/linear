public class Main {

    static long timeout = 1000 * 1;

    public static void runCoverAlgorithm(String graphType, int startingVertex) throws Exception {
        long graphAvg = 0, graphMax = 0, graphMin = Long.MAX_VALUE;
        Graph graph;
        if (graphType == "clique") {
            graph = cliqueGraph();
        } else if (graphType == "chain") {
            graph = chainGraph();
        } else if (graphType == "candy") {
            graph = candyGraph();
        } else if (graphType == "ccliques") {
            graph = chainedCliquesGraph();
        } else {
            throw new Exception("Not supported !!! (yet...)");
        }

        for (int i = 0; i < 5; i++) {
            long time = graph.coverTime(-1, timeout);
            graphAvg += time;
            graphMax = Math.max(graphMax, time);
            graphMin = Math.min(graphMin, time);
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
            runCoverAlgorithm("clique", 0);

            System.out.println("chain:");
            runCoverAlgorithm("chain", 0);

            System.out.println("candy:");
            runCoverAlgorithm("candy", 16000);

            System.out.println("ccliques:");
            runCoverAlgorithm("ccliques", 16000);

        } catch(Exception err) {
            System.out.println(err);
        }
    }

    public static void EX2() {

    }

    public static void main(String[] args) {
        EX1();
        EX2();
    }

    private static Graph candyGraph() {
        Graph candy = new Graph((int) Math.pow(2, 14));
        // System.out.println("Graph created");

        int i = 0;
        // first half is a chain
        for (; i < Math.pow(2, 13); i++) {
            candy.addEdge(i, i, true);
            candy.addEdge(i, i + 1,false);
        }

        // second half is a clique
        for (; i < Math.pow(2, 14); i++) {
            for (int j = (int) Math.pow(2, 13); j < Math.pow(2, 14); j++) {
                candy.addEdge(i, j, true);
            }
        }
        // System.out.println("Graph filled");
        return candy;
    }

    private static Graph chainGraph() {
        Graph chain = new Graph((int) Math.pow(2, 14));
        // System.out.println("Graph created");

        int i = 0;
        for (; i < Math.pow(2, 14) - 1; i++) {
            chain.addEdge(i, i, true);
            chain.addEdge(i, i + 1);
        }
        chain.addEdge(i, 0);
        // System.out.println("Graph filled");

        return chain;
    }

    private static Graph cliqueGraph() {
        Graph graph = new Graph((int) Math.pow(2, 14));
        // System.out.println("Graph created");

        for (int i = 0; i < Math.pow(2, 14); i++) {
            for (int j = 0; j < Math.pow(2, 14); j++) {
                graph.addEdge(i, j, true);
            }
        }
        // System.out.println("Graph filled");

        return graph;
    }

    private static Graph chainedCliquesGraph() {
        Graph chainedCliques = new Graph((int) Math.pow(2, 14));
        // System.out.println("Graph created");

        int i = 0;
        for (; i < Math.pow(2, 13); i++) {
            chainedCliques.addEdge(i, i, true);
            chainedCliques.addEdge(i, i + 1);
        }

        for (; i < Math.pow(2, 13) + Math.pow(2, 12); i++) {
            for (int j = (int)Math.pow(2, 13); j < Math.pow(2, 13) + Math.pow(2, 12); j++) {
                chainedCliques.addEdge(i, j, true);
            }
        }

        for (; i < Math.pow(2, 14); i++) {
            for (int j = (int)Math.pow(2, 13) + (int)Math.pow(2, 12); j < Math.pow(2, 14); j++) {
                chainedCliques.addEdge(i, j, true);
            }
        }

        chainedCliques.addEdge(0, (int)Math.pow(2, 14) - 1, false);

        // System.out.println("Graph filled");

        return chainedCliques;
    }
}