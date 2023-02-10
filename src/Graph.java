import java.util.Arrays;
import java.util.Random;

public class Graph {
    private int[][] graph;
    int[] outDegree;


    public Graph(int size) {
        graph = new int[size][size];
        outDegree = new int[size];
    }

    public Graph() {
        graph = new int[0][0];
        outDegree = new int[0];
    }

    public void addVertex(int vertex) {
        int[][] newAdjacencyMatrix = new int[graph.length + 1][graph.length + 1];
        for (int i = 0; i < graph.length; i++) {
            System.arraycopy(graph[i], 0, newAdjacencyMatrix[i], 0, graph.length);
        }
        graph = newAdjacencyMatrix;
    }

    public void pageRankAlgo(double epsilon, int maxIteration) {
        double[] rank = new double[graph.length];
        double[] base = new double[graph.length];
        Arrays.fill(base, 1.0 / graph.length);
        Arrays.fill(rank, 0);
        rank[(int) (Math.random() * rank.length)] = 1;// random start
        double[] newRank = new double[graph.length];
        double diff = 1;
        int iteration = 0;
        while (diff > epsilon && iteration < maxIteration) {
            // build the new stationary distribution vector
            for (int i = 0; i < graph.length; i++) {
                double sum = 0;
                for (int j = 0; j < graph.length; j++) {
                    if (graph[j][i] == 1 && rank[j] != 0) {
                        sum += rank[j] / getOutDegree(j);
                    }
                }

                if (i % 1200 == 0) System.out.println("i is " + i);

                newRank[i] = sum;
            }

            diff = 0;
            for (int i = 0; i < graph.length; i++) {
                diff += Math.pow(base[i] - newRank[i], 2);
            }
            diff = Math.sqrt(diff);
            rank = newRank;
            iteration++;
            var count = Arrays.stream(rank).filter(x -> x > 0).count();
            System.out.println("iteration: " + iteration + " diff: " + diff + " count: " + count);
        }
        System.out.println("PageRank: ");
    }

    public int getOutDegree(int vertex) {
        return outDegree[vertex];
    }

    public void addEdge(int src, int dst, boolean isDirected) {
        if (src < 0 || dst < 0 || src >= graph.length || dst >= graph.length) {
            throw new IllegalArgumentException("Vertex not found");
        }
        graph[src][dst] = 1;
        outDegree[src]++;
        if (!isDirected) {
            graph[dst][src] = 1;
            outDegree[dst]++;
        }
    }

    public void addEdge(int src, int dst) {
        addEdge(src, dst, false);
    }

    public long coverTime(int current, long timeout) {
        if (current == -1) {
            current = (int) (Math.random() * graph.length);
        }
        int count = 1, miss = 0, totalMiss = 0;
        int initial = current;

        boolean[] visited = new boolean[graph.length];
        long start = System.currentTimeMillis();
        while (count < graph.length) {
            visited[current] = true;
            current = getNeighbour(current);
            if ((count + miss) % 10000 == 0) {
                String graphPart = "";
                if (current < Math.pow(2, 13)) {
                    graphPart = "chain";
                } else if (current < Math.pow(2, 13) + Math.pow(2, 12)) {
                    graphPart = "clique 1";
                } else {
                    graphPart = "clique 2";
                }

//                System.out.println("current " + current + ". iteration: " + (count + miss) + ". start at: " + initial + ". part: " + graphPart);
            }

            if (System.currentTimeMillis() - start > timeout) {
                System.out.println("finished due to timeout" + "; cover percentage: " + (count * 100 / graph.length) + "%" + "; total cover: " + count + "; totalMiss: " + totalMiss);
                return timeout;
            }

            if (visited[current]) {
                miss++;
            }
            if (!visited[current]) {
                count++;
                totalMiss += miss;
//                System.out.println("misses: "+totalMiss+ " +" + miss);
                miss = 0;
//                System.out.println("total cover: " + count);
//                System.out.println("cover percentage: " + (count * 100 / graph.length) + "%");
            }
        }

        System.out.println("finished after: " + (System.currentTimeMillis() - start) + "; cover percentage: " + (count * 100 / graph.length) + "%" + "; total cover: " + count + "; totalMiss: " + totalMiss);
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
