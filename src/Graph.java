import java.util.Arrays;
import java.util.Random;

public class Graph {

    private double[][] graph;
    int[] outDegree;
    GraphType graphType;
    Random random = new Random();


    public Graph(int size) {
        graph = new double[size][size];
        outDegree = new int[size];
        Arrays.fill(outDegree, 0);
    }

    public Graph(int size, GraphType type) {
        this(size);
        this.graphType = type;
    }

    public Graph() {
        graph = new double[0][0];
        outDegree = new int[0];
    }

    public void addVertex(int vertex) {
        double[][] newAdjacencyMatrix = new double[graph.length + 1][graph.length + 1];
        for (int i = 0; i < graph.length; i++) {
            System.arraycopy(graph[i], 0, newAdjacencyMatrix[i], 0, graph.length);
        }
        graph = newAdjacencyMatrix;
    }

    public void pageRankAlgo(double epsilon, int maxIteration, int start) {
        double[] rank = new double[graph.length];
        double[] base = new double[graph.length];
        Arrays.fill(base, 1.0 / graph.length);
        Arrays.fill(rank, 0);
        rank[start] = 1;// random start
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

                newRank[i] = sum;
            }

            diff = 0;
            for (int i = 0; i < graph.length; i++) {
                diff += Math.pow(base[i] - newRank[i], 2);
            }
            diff = Math.sqrt(diff);
            //System.arraycopy(newRank, 0, rank, 0, graph.length);
            iteration++;
            //var count = Arrays.stream(rank).filter(x -> x > 0).count();
            //System.out.println("iteration: " + iteration + " diff: " + diff + " epsilon: " + epsilon + " count: " + count);
        }
        System.out.println("PageRank: ");
    }

    public double[] pageRankAlgoV2(
            double epsilon,
            int maxIterations,
            int startingIdx
    ) throws Exception {
        boolean stop;
        double delta;
        int iteration = 0;

        // build vector
        double [] distribution = GraphFactory.createDistribution(graphType);

        // set initial vector
        double[] cur_vector = new double[graph.length];
        if (startingIdx == -1) {
            Arrays.fill(cur_vector, 1 / Math.pow(2, 14));
        } else {
            Arrays.fill(cur_vector, 0);
            cur_vector[startingIdx] = 1;
        }

        do {
            // calc next vector
            cur_vector = Utils.multiply(this.graph, cur_vector);

            // compute delta
            delta = Utils.computeNorm(Utils.subtract(cur_vector, distribution));

            // check stop condition
            stop = delta < epsilon;

            // break on max iterations
            System.out.printf("iteration: %d; delta: %f\n", iteration, delta);
            if (iteration == maxIterations) {
                System.out.println("[generalizedPowerIteration] max iterations reached. skipping search...");
                break;
            }
            iteration++;

        } while(!stop);

        return cur_vector;
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

    public double[][] getGraph() {
        return graph;
    }


    public long coverTime(
            int startingIdx,
            long timeout,
            String chartId
    ) {
        if (startingIdx == -1) {
            // generate starting idx from the 3/4 graph part (always clique)
            startingIdx = (int) Math.pow(2, 13) + (int) Math.pow(2, 12) + random.nextInt((int) Math.pow(2, 12));
        }

        long count = 1, miss = 0, totalMiss = 0, iteration = 0;
        long initial = startingIdx;
        boolean[] visited = new boolean[graph.length];
        long start = System.currentTimeMillis();

        while (count < graph.length) {
            iteration++;
            visited[startingIdx] = true;
            startingIdx = getNeighbour(startingIdx);

            if (System.currentTimeMillis() - start > timeout) {
                System.out.println("finished due to timeout" + "; cover percentage: " + (count * 100 / graph.length) + "%" + "; total cover: " + count + "; totalMiss: " + (totalMiss + miss + count));
                return timeout;
            }

            if (visited[startingIdx]) {
                miss++;
            }

            if (!visited[startingIdx]) {
                count++;
                totalMiss += miss;
                miss = 0;
//                System.out.println("misses: " + totalMiss + " + " + miss + " iteration: " + iteration);
//                System.out.println("total cover: " + count);
//                System.out.println("cover percentage: " + (count * 100 / graph.length) + "%" + " started at: " + initial);

                ChartService.addChartItem(
                        String.format("%s-misses", chartId),
                        "misses",
                        count,
                        totalMiss
                );
            }

            if (iteration % 1000 == 0) {
                // graph of iteration (x) to count (y)
                ChartService.addChartItem(
                        chartId,
                        "count",
                        iteration,
                        count
                );
            }
        }

        System.out.println("finished after: " + (System.currentTimeMillis() - start) + "; cover percentage: " + (count * 100 / graph.length) + "%" + "; total cover: " + count + "; totalMiss: " + totalMiss);
        return System.currentTimeMillis() - start;
    }

    private int getNeighbour(int current) {
        switch (graphType) {
            case Chain:
                return getChainNeighbour(current);
            case Clique:
                return getCliqueNeighbour();
            case Candy:
                return getCandyNeighbour(current);
            case CCLiques:
                return getcCliequeNeighbour(current);
            default:
                throw new IllegalStateException("Unexpected value: " + graphType);
        }
    }

    private int getChainNeighbour(int current) {
        var val = (random.nextInt(3) + current - 1) % graph.length;
        return val == -1 ? graph.length - 1 : val;
    }

    private int getCliqueNeighbour() {
        return random.nextInt(graph.length);
    }

    private int getCandyNeighbour(int current) {
        if (current < 8192) { // chain
            if (current == 0) {
                return random.nextInt(2);
            }
            return (random.nextInt(3) + current - 1) % 8192;
        } else { // clique
            if (current == 8192) {
                return random.nextInt((int) Math.pow(2, 13) + 1) + (int) Math.pow(2, 13) - 1;
            }
            return random.nextInt((int) Math.pow(2, 13)) + (int) Math.pow(2, 13);
        }
    }

    private int getcCliequeNeighbour(int current) {
        if (current == 0) {
            return (random.nextInt((int) Math.pow(2, 12)) + (int) Math.pow(2, 12) + (int) Math.pow(2, 13) + 2) % graph.length;
        } else if (current == 8192) {
            return random.nextInt((int) Math.pow(2, 12)) + (int) Math.pow(2, 13) - 1;
        } else if (current == 16383) {
            return (random.nextInt((int) Math.pow(2, 12)) + (int) Math.pow(2, 12) + (int) Math.pow(2, 13)) % graph.length;
        } else if (current < 8192) { // chain
            return (random.nextInt(3) + current - 1);
        } else if (current < 12288) { // clique 1
            return random.nextInt((int) Math.pow(2, 12)) + (int) Math.pow(2, 13);
        } else { // clique 2
            return (random.nextInt((int) Math.pow(2, 12)) + (int) Math.pow(2, 13) + (int) Math.pow(2, 12) + 1) % graph.length;
        }
    }

    public void normalize(){
        for (int i = 0; i < graph.length; i++) {
            for (int j = 0; j < graph.length; j++) {
                graph[i][j] /= outDegree[i];
            }
        }
        transpose();
    }

    public void transpose(){
        double temp;
        for (int i = 0; i < graph.length; i++) {
            for (int j = i; j < graph.length; j++) {
                temp = graph[i][j];
                graph[i][j] = graph[j][i];
                graph[j][i] = temp;
            }
        }
    }

    // this method assumes that 'this' is already normalized
    public double[] powerIteration(
            double epsilon,
            String chartId,
            String lineId,
            int maxIterations
    ) throws Exception {
        boolean stop;
        double delta, normVt;
        double[] vt, ut;
        double[] utm1 = Utils.generateRandomVector(graph.length); // u0. i.e "ut minus 1"
        int iteration = 0;

        do {
            // compute vt
            vt = Utils.multiply(this.graph, utm1);

            // compute ut
            normVt = Utils.computeNorm(vt);
            ut = Utils.divide(vt, normVt);

            // compute delta
            delta = Utils.computeNorm(Utils.subtract(ut, utm1));

            // add to chart
            ChartService.addChartItem(
                    chartId, // chart of eigenvector: eigenvalue per iteration
                    lineId,
                    iteration,
                    Utils.computeEigenValue(this.graph, ut)
            );

            // check condition
            stop = delta < epsilon;

            // update utm1
            utm1 = Utils.clone(ut);

//            System.out.printf("iteration: %d\n", iteration);
            // break on max iterations
            if (iteration == maxIterations) {
                System.out.println("[generalizedPowerIteration] max iterations reached. skipping search...");
                break;
            }
            iteration++;
        } while(!stop);

        return ut;
    }

    // not in use: doesnt work
    // this method assumes that 'this' is already normalized
    public double[] generalizedPowerIteration(
            double[] v1,
            double epsilon,
            int maxIterations
    ) throws Exception {

        boolean stop;
        double delta, normVt;
        double[] wt, ut, vt, projWtOnV1;
        double[] utm1; // i.e "ut minus 1"
        int iteration = 0;

        double[] w0 = Utils.generateRandomVector(graph.length);
        utm1 = Utils.computeProj(w0, v1); // compute u0

        do {
            // compute wt
            wt = Utils.multiply(this.graph, utm1);

            // compute vt
            projWtOnV1 = Utils.computeProj(wt, v1);
            vt = Utils.subtract(wt, projWtOnV1);

            // compute ut
            normVt = Utils.computeNorm(vt);
            ut = Utils.divide(vt, normVt);

            // compute delta
            delta = Utils.computeNorm(Utils.subtract(ut, utm1));

            // add to chart
            ChartService.addChartItem(
                    String.format("EX3-%s-v2", graphType.name()), // chart of v2: eigenvalue per iteration
                    "v2",
                    iteration,
                    Utils.computeEigenValue(this.graph, ut)
            );

            // check condition
            stop = delta < epsilon;

            // update utm1
            utm1 = Utils.clone(ut);

            // break on max iterations
            if (iteration == maxIterations) {
                System.out.println("[generalizedPowerIteration] max iterations reached. skipping search...");
                break;
            }
            iteration++;

        } while(!stop);

        return ut;
    }

    // the attempts to cancel the most dominant component using projection didnt work.
    // this algorithm is used to cancel the most dominant component according to numpy docs (python library)
    public void cancelVectorComponent(double[] vector) throws Exception {
        double lambda = Utils.computeEigenValue(this.graph, vector);
        for (int i = 0; i < this.graph.length; i++) {
            for (int j = 0; j < this.graph.length; j++) {
                this.graph[i][j] -= vector[i] * vector[j]; // subtract the vector outer product
                this.graph[i][j] *= lambda;
            }
        }
    }
}
