public class EX2 {

    public static void run() {
        try {
            runPageRank(GraphType.Clique, 0);
            runPageRank(GraphType.Chain, 0);
            runPageRank(GraphType.Candy, 16000);
            runPageRank(GraphType.CCLiques, 16000);
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    private static void runPageRank(GraphType graphType, int startVortex) throws Exception {
        Graph graph = GraphFactory.create(graphType);
        System.out.println("\n================== " + graph.graphType + " ==================");
        graph.pageRankAlgo(Math.pow(2, -6), (int) Math.pow(2, 1), startVortex);
    }
}
