import java.util.Arrays;
import java.util.Random;

public class Utils {

    public static double computeNorm(double[] vector) {
        double sum = 0;
        for (double cell : vector) {
            sum += cell * cell;
        }
        return Math.sqrt(sum);
    }

    public static double[] subtract(double[] v1, double[] v2) throws Exception {
        if (v1.length != v2.length) {
            throw new Exception("[Utils][subtract] failed to subtract vectors. length mismatch!");
        }

        double[] res = new double[v1.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = v1[i] - v2[i];
        }

        return res;
    }

    public static double[] generateRandomVector(int size) {
        double[] res;
        boolean foundZero;

        do {
            res = new double[size];
            for (int i = 0; i < size; i++) {
                res[i] = Math.random();
            }

            foundZero = Arrays.stream(res).anyMatch((i) -> i == 0);
            if (foundZero) {
                System.out.println("[Utils][generateRandomVector] found zero in random vector. generating again...");
            }

        } while(foundZero);

        return res;
    }

    public static double[] multiply(double[][] mat, double[] vec) throws Exception {
        if (mat[0].length != vec.length) {
            throw new Exception("[Utils][multiply] failed to multiply matrix and vector. length mismatch!");
        }

        double[] res = new double[vec.length];
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < vec.length; j++) {
                res[i] += mat[i][j] * vec[j];
            }
        }
        return res;
    }

    public static double multiply(double[] v1, double[] v2) throws Exception {
        if (v1.length != v2.length) {
            throw new Exception("[Utils][multiply] failed to multiply vectors. length mismatch!");
        }

        double res = 0;
        for (int i = 0; i < v1.length; i++) {
            res += v1[i] * v2[i];
        }

        return res;
    }

    public static double[] divide(double[] vec, double scalar) throws Exception {
        if (scalar == 0) {
            throw new Exception("[Utils][multiply] failed to divide vector with scalar. scalar is 0");
        }

        double[] res = new double[vec.length];
        for (int i = 0; i < vec.length; i++) {
            res[i] = vec[i] / scalar;
        }

        return res;
    }

    public static double[] clone(double[] vec) {
        double[] res = new double[vec.length];
        System.arraycopy(vec, 0, res, 0, vec.length);
        return res;
    }

    public static double computeEigenValue(double[][] A, double[] v) throws Exception {
        double[] Av = multiply(A, v);
        return computeNorm(Av) / computeNorm(v);
//        return multiply(Av, v); // https://www.cs.huji.ac.il/w~csip/tirgul2.pdf
    }

    // projection on simple space (span of 1 vector 'u', no need for more)
    public static double[] computeProj(double[] v, double[] u) throws Exception {
        double[] res = new double[v.length];
        double scalar = multiply(v, u) / multiply(u, u);
        for (int i = 0; i < v.length; i++) {
            res[i] = u[i] * scalar;
        }
        return res;
    }
}
