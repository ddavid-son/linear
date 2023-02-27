import java.util.Arrays;
import java.util.Random;

public class Graph {

    private int[][] graph;
    int[] outDegree;
    String type = "";
    Random random = new Random();


    public Graph(int size) {
        graph = new int[size][size];
        outDegree = new int[size];
        Arrays.fill(outDegree, 0);
    }

    public Graph(int size, String type) {
        this(size);
        this.type = type;
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
            current = random.nextInt((int) Math.pow(2, 12)) + (int) Math.pow(2, 13) + (int) Math.pow(2, 12);
        }
        long count = 1, miss = 0, totalMiss = 0;
        long initial = current;
        long bla = 0;

        boolean[] visited = new boolean[graph.length];
        long start = System.currentTimeMillis();
        while (count < graph.length) {
            bla++;
            visited[current] = true;
            current = getNeighbour(current);

            if (bla % 1000 == 0)
                System.out.println("bla: " + bla);

            if (System.currentTimeMillis() - start > timeout) {
                System.out.println("finished due to timeout" + "; cover percentage: " + (count * 100 / graph.length) + "%" + "; total cover: " + count + "; totalMiss: " + (totalMiss + miss + count));
                return timeout;
            }

            if (visited[current]) {
                miss++;
            }

            if (!visited[current]) {
                count++;
                totalMiss += miss;
                System.out.println("misses: " + totalMiss + " +" + miss + " bla: " + bla);
                miss = 0;
                System.out.println("total cover: " + count);
                System.out.println("cover percentage: " + (count * 100 / graph.length) + "%" + " started at: " + initial);
            }
        }

        System.out.println("finished after: " + (System.currentTimeMillis() - start) + "; cover percentage: " + (count * 100 / graph.length) + "%" + "; total cover: " + count + "; totalMiss: " + totalMiss);
        return System.currentTimeMillis() - start;
    }

    private int getNeighbour(int current) {
        return switch (type) {
            case "chain" -> getChainNeighbour(current);
            case "clique" -> getCliqueNeighbour();
            case "candy" -> getCandyNeighbour(current);
            case "ccliques" -> getcCliequeNeighbour(current);
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
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


    // projection of vector
    public double[] projection(double[] v, double[] u) {
        double[] result = new double[v.length];
        double scalar = dot(v, u) / dot(u, u);
        for (int i = 0; i < v.length; i++) {
            result[i] = scalar * u[i];
        }
        return result;
    }

    public double[][] powerIteration(double tol) {

        // Initialize a random vector b with the same size as matrix
        double[] b = new double[graph.length];
        double[] result = new double[]{};
        for (int i = 0; i < b.length; i++) {
            b[i] = Math.random();
        }

        // Set a tolerance for convergence
        // double tol = 1e-6;

        // Set a maximum number of iterations
        int maxIter = 100;

        // Initialize an iteration counter
        int iter = 0;

        // Initialize a variable to store the eigenvalue estimate
        double lambda = 0;

        // Loop until convergence or maximum iterations reached
        double[] y;
        while (true) {
            // Multiply matrix by b and store the result in a new vector y
            y = new double[b.length];
            for (int i = 0; i < y.length; i++) {
                for (int j = 0; j < b.length; j++) {
                    y[i] += graph[j][i] * b[j] / outDegree[j];
                }
            }

            // Normalize y and store the result in a new vector z
            double[] z = new double[y.length];
            double normY = norm(y); // A method to calculate the Euclidean norm of a vector
            for (int i = 0; i < z.length; i++) {
                z[i] = y[i] / normY;
            }

            // Update the eigenvalue estimate by taking the dot product of y and b
            lambda = dot(y, b); // A method to calculate the dot product of two vectors

            // Check if the relative change in b is less than the tolerance
            if (norm(subtract(b, z)) < tol) { // A method to subtract two vectors element-wise
                result = z;
                break;
            }

            // Update b with z and increment the iteration counter
            b = z;
            iter++;

            // Check if the maximum number of iterations is reached
            if (iter == maxIter) {
                System.out.println("Maximum iterations reached");
                break;
            }
        }


        return new double[][]{new double[]{lambda}, result}; // eigenvalue and eigenvector
//        return lambda;
    }

    public double norm(double[] vec) {
        double sum = 0;
        for (double v : vec) {
            sum += v * v;
        }
        return Math.sqrt(sum);
    }

    // vec * scalar func
    public double[] scalarProduct(double[] vec, double scalar) {
        double[] result = new double[vec.length];
        for (int i = 0; i < vec.length; i++) {
            result[i] = vec[i] * scalar;
        }
        return result;
    }

    public double[] matrixVectorProduct(double[] vec) {
        double[] result = new double[vec.length];
        for (int i = 0; i < vec.length; i++) {
            for (int j = 0; j < vec.length; j++) {
                result[i] += graph[j][i] * vec[j] / outDegree[j];
            }
        }
        return result;
    }
    public double[] scalarDivision(double[] vec, double scalar) {
        double[] result = new double[vec.length];
        for (int i = 0; i < vec.length; i++) {
            result[i] = vec[i] / scalar;
        }
        return result;
    }

    // get eigenvalue from vector
    public double getEigenValue(double[] vec) {
        double[] result = matrixVectorProduct(vec);

        return dot(result, vec);
    }
    // find the eigenvalues of the matrix


    public double dot(double[] x, double[] y) {
        double sum = 0;
        for (int i = 0; i < x.length; i++) {
            sum += x[i] * y[i];
        }
        return sum;
    }

    public double[] subtract(double[] x, double[] y) {
        double[] z = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            z[i] = x[i] - y[i];
        }
        return z;
    }

    // Define a method to perform deflation on a given matrix and an eigenvalue-eigenvector pair
    // This method returns a new matrix that has one less dimension and one less eigenvalue than the original matrix
    public void deflate(double lambda, double[] x) {

        // Calculate x * x^T, where x^T is the transpose of x
        double[][] xxT = new double[graph.length][graph[0].length];
        for (int i = 0; i < xxT.length; i++) {
            for (int j = 0; j < xxT[0].length; j++) {
                xxT[i][j] = x[i] * x[j];
            }
        }

        // Subtract lambda * x * x^T from matrix and store the result in a new matrix B
        double[][] B = new double[graph.length][graph[0].length];
        for (int i = 0; i < B.length; i++) {
            for (int j = 0; j < B[0].length; j++) {
                B[i][j] = graph[i][j] - lambda * xxT[i][j];
            }
        }

        // Find an index k such that |x[k]| is maximal
        int k = 0;
        for (int i = 1; i < x.length; i++) {
            if (Math.abs(x[i]) > Math.abs(x[k])) {
                k = i;
            }
        }

        // Create a new matrix C that has one less row and one less column than B by removing row k and column k
        double[][] C = new double[B.length - 1][B[0].length - 1];
        for (int i = 0; i < C.length; i++) {
            for (int j = 0; j < C[0].length; j++) {
                if (i < k && j < k) {
                    C[i][j] = B[i][j];
                } else if (i < k) {
                    C[i][j] = B[i][j + 1];
                } else if (j < k) {
                    C[i][j] = B[i + 1][j];
                } else {
                    C[i][j] = B[i + 1][j + 1];
                }
            }
        }
    }


    // gets all the job done in a single method - need to see if legit
    public double calcRatioOfEigenVal(double tol) {
        int n = graph.length;
        double[] x = new double[n];
        double[] y = new double[n];
        double[] z = new double[n];
        double lambda1 = 0;
        double lambda2 = 0;
        //double tol = 1e-8; // tolerance value for convergence

        // Initialize x to a random vector
        Random rand = new Random();
        for (int i = 0; i < n; i++) {
            x[i] = rand.nextDouble();
        }

        // Perform power iteration to find the largest eigenvalue and corresponding eigenvector
        while (true) {
            // Multiply matrix by x
            for (int i = 0; i < n; i++) {
                y[i] = 0;
                for (int j = 0; j < n; j++) {
                    y[i] += graph[i][j] * x[j];
                }
            }

            // Find the largest element in y and store its index in z
            int idx = 0;
            for (int i = 1; i < n; i++) {
                if (Math.abs(y[i]) > Math.abs(y[idx])) {
                    idx = i;
                }
            }

            // Normalize y and check for convergence
            double norm = y[idx];
            for (int i = 0; i < n; i++) {
                x[i] = y[i] / norm;
            }
            if (Math.abs(lambda1 - norm) < tol) {
                break;
            }
            lambda1 = norm;
        }

        // Perform power iteration to find the second-largest eigenvalue
        while (true) {
            // Multiply matrix by x
            for (int i = 0; i < n; i++) {
                y[i] = 0;
                for (int j = 0; j < n; j++) {
                    y[i] += graph[i][j] * x[j];
                }
            }

            // Subtract the contribution of the largest eigenvalue
            for (int i = 0; i < n; i++) {
                y[i] -= lambda1 * x[i];
            }

            // Find the largest element in y and store its index in z
            int idx = 0;
            for (int i = 1; i < n; i++) {
                if (Math.abs(y[i]) > Math.abs(y[idx])) {
                    idx = i;
                }
            }

            // Normalize y and check for convergence
            double norm = y[idx];
            for (int i = 0; i < n; i++) {
                x[i] = y[i] / norm;
            }
            if (Math.abs(lambda2 - norm) < tol) {
                break;
            }
            lambda2 = norm;
        }

        // Compute and return the ratio between the largest and second-largest eigenvalues
        return lambda1 / lambda2;
    }
}





