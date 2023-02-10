public class Main {
    public static void main(String[] args) {
//        Graph clique = cliqueGraph();
//        System.out.println("Clique graph:");
//        clique.coverTime();

//        Graph chain = chainGraph();
//        System.out.println("Chain graph:");
//        var time = chain.coverTime();

        System.out.println("candy graph:");
        Graph candy = candyGraph();
        var time = candy.coverTime();
    }

    private static Graph candyGraph() {
        Graph candy = new Graph((int) Math.pow(2, 14));
        System.out.println("Graph created");

        int i = 0;
        // first half is a chain
        for (; i < Math.pow(2, 13); i++) {
            candy.addEdge(i, i + 1,false);
        }

        // second half is a clique
        for (; i < Math.pow(2, 14); i++) {
            for (int j = (int) Math.pow(2, 13); j < Math.pow(2, 14); j++) {
                candy.addEdge(i, j, true);
            }
        }
        System.out.println("Graph filled");
        return candy;
    }

    private static Graph chainGraph() {
        Graph chain = new Graph((int) Math.pow(2, 14));
        System.out.println("Graph created");

        int i = 0;
        for (; i < Math.pow(2, 14) - 1; i++) {
            chain.addEdge(i, i + 1);
        }
        chain.addEdge(i, 0);
        System.out.println("Graph filled");

        return chain;
    }

    private static Graph cliqueGraph() {
        Graph graph = new Graph((int) Math.pow(2, 14));
        System.out.println("Graph created");

        for (int i = 0; i < Math.pow(2, 14); i++) {
            for (int j = 0; j < Math.pow(2, 14); j++) {
                graph.addEdge(i, j, true);
            }
        }
        System.out.println("Graph filled");

        return graph;
    }
}