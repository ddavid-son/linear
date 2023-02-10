import java.lang.reflect.Array;
import java.util.Arrays;

public class Graph {
    private int[][] graph;


    public Graph(int size) {
        graph = new int[size][size];
    }

    public Graph() {
        graph = new int[0][0];
    }

    public void addVertex(int vertex) {
        int[][] newAdjacencyMatrix = new int[graph.length + 1][graph.length + 1];
        for (int i = 0; i < graph.length; i++) {
            System.arraycopy(graph[i], 0, newAdjacencyMatrix[i], 0, graph.length);
        }
        graph = newAdjacencyMatrix;
    }

    public void addEdge(int src, int dst, boolean isDirected) {
        if (src < 0 || dst < 0 || src >= graph.length || dst >= graph.length) {
            throw new IllegalArgumentException("Vertex not found");
        }
        graph[src][dst] = 1;
        if (!isDirected) {
            graph[dst][src] = 1;
        }
    }

    public void addEdge(int src, int dst) {
        addEdge(src, dst, false);
    }

    public long coverTime() {
        int current = (int) (Math.random() * graph.length);
        int count = 1, miss = 0, totalMiss = 0;

        boolean[] visited = new boolean[graph.length];
        long start = System.currentTimeMillis();
        while (count < graph.length) {
            visited[current] = true;
            current = getNeighbour(current);
            if (visited[current]) {
                miss++;
            }
            if (!visited[current]) {
                count++;
                totalMiss+= miss;
                System.out.println("misses: "+totalMiss+ " +" + miss);
                miss = 0;
                System.out.println("total cover: " + count);
                System.out.println("cover percentage: " + (count * 100 / graph.length) + "%");
            }
        }

        System.out.println("finished " + (System.currentTimeMillis() - start));
        return System.currentTimeMillis() - start;
    }

    private int getNeighbour(int current) {
        var idx = (int) (Math.random() * Arrays.stream(graph[current]).filter(i -> i == 1).count());

        for (int i = 0; i < graph[current].length; i++) {
            if (graph[current][i] == 1) {
                if (idx == 0) {
                    return i;
                }
                idx--;
            }
        }
        return Integer.MAX_VALUE;
    }
}
