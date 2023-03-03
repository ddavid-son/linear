public class GraphFactory {

    public static Graph create(GraphType type) throws Exception {
        switch (type) {
            case Clique:
                return createCliqueGraph();
            case Chain:
                return createChainGraph();
            case Candy:
                return createCandyGraph();
            case CCLiques:
                return createChainedCliquesGraph();
            default:
                throw new Exception("Graph not supported!");
        }
    }

    private static Graph createCandyGraph() {
        Graph candy = new Graph((int) Math.pow(2, 14), GraphType.Candy);

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

    private static Graph createChainGraph() {
        Graph chain = new Graph((int) Math.pow(2, 14), GraphType.Chain);

        int i = 0;
        for (; i < Math.pow(2, 14) - 1; i++) {
            chain.addEdge(i, i, true);
            chain.addEdge(i, i + 1);
        }

        chain.addEdge(i, 0);
        return chain;
    }

    private static Graph createCliqueGraph() {
        Graph graph = new Graph((int) Math.pow(2, 14), GraphType.Clique);

        for (int i = 0; i < Math.pow(2, 14); i++) {
            for (int j = 0; j < Math.pow(2, 14); j++) {
                graph.addEdge(i, j, true);
            }
        }

        return graph;
    }

    private static Graph createChainedCliquesGraph() {
        Graph chainedCliques = new Graph((int) Math.pow(2, 14), GraphType.CCLiques);

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
