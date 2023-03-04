import java.lang.reflect.Array;
import java.util.Arrays;

public class GraphFactory {

    public static Graph createGraph(GraphType type) throws Exception {
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

    public static double[] createDistribution(GraphType type) throws Exception {
        switch (type) {
            case Clique:
                return createCliqueDistribution();
            case Chain:
                return createChainDistribution();
            case Candy:
                return createCandyDistribution();
            case CCLiques:
                return createCCliquesDistribution();
            default:
                throw new Exception("Graph not supported!");
        }
    }

    private static double[] createCliqueDistribution() throws Exception {
        double[] distribution = new double[(int) Math.pow(2, 14)];
        Arrays.fill(distribution, distribution.length);
        distribution = Utils.divide(distribution, Utils.sumVector(distribution));
        return distribution;
    }

    private static double[] createChainDistribution() throws Exception {
        double[] distribution = new double[(int) Math.pow(2, 14)];
        Arrays.fill(distribution, 3);
        distribution[0] = distribution[distribution.length - 1] = 2;
        distribution = Utils.divide(distribution, Utils.sumVector(distribution));
        return distribution;
    }

    private static double[] createCandyDistribution() throws Exception {
        double[] distribution = new double[(int) Math.pow(2, 14)];
        distribution[0] = 2;
        for (int i = 1; i < Math.pow(2, 13) - 1; i++) {
            distribution[i] = 3;
        }

        // the node idx (n/2 - 1) has the same degree as the clique nodes
        for (int i = (int)Math.pow(2, 13) - 1; i < Math.pow(2, 14); i++) {
            distribution[i] = Math.pow(2, 13) + 1;
        }

        distribution = Utils.divide(distribution, Utils.sumVector(distribution));
        return distribution;
    }

    private static double[] createCCliquesDistribution() throws Exception {
        double[] distribution = new double[(int) Math.pow(2, 14)];
        for (int i = 1; i < Math.pow(2, 13); i++) {
            distribution[i] = 3;
        }

        for (int i = (int)Math.pow(2, 13); i < Math.pow(2, 13) + Math.pow(2, 12); i++) {
            distribution[i] = Math.pow(2, 12);
        }

        for (int i = (int)Math.pow(2, 13) + (int)Math.pow(2, 12); i < Math.pow(2, 14); i++) {
            distribution[i] = Math.pow(2, 12);
        }

        distribution[distribution.length - 1]++; // connect to chain: end of graph to idx 0
        distribution[distribution.length / 2]++; // connect to chain: n/2 to idx n/2-1

        distribution = Utils.divide(distribution, Utils.sumVector(distribution));
        return distribution;
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
            chainedCliques.addEdge(i, i + 1); // includes connection from chain to clique n/2-1 + n/2
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
