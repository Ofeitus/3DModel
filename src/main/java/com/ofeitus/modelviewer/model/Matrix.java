package com.ofeitus.modelviewer.model;

public class Matrix {
    public static double[][] multiply(double[][] m1, double[][] m2) {
        double[][] m = {
                {0,0,0,0},
                {0,0,0,0},
                {0,0,0,0},
                {0,0,0,0}
        };

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                m[i][j] = m1[i][0] * m2[0][j] +
                        m1[i][1] * m2[1][j] +
                        m1[i][2] * m2[2][j] +
                        m1[i][3] * m2[3][j];
            }
        }

        return m;
    }

    public static double[] normalize(double[] vector) {
        double length = Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2]);
        return new double[]{
                vector[0] / length,
                vector[1] / length,
                vector[2] / length
        };
    }

    public static double[] minus(double[] v1, double[] v2) {
        return new double[]{
                v1[0] - v2[0],
                v1[1] - v2[1],
                v1[2] - v2[2]
        };
    }

    public static double[] multiply(double[] v1, double[] v2) {
        return new double[]{
                v1[1] * v2[2] - v1[2] * v2[1],
                v1[2] * v2[0] - v1[0] * v2[2],
                v1[0] * v2[1] - v1[1] * v2[0]
        };
    }

    public static double scalarMultiply(double[] v1, double[] v2) {
        return v1[0] * v2[0] + v1[1] * v2[1] + v1[2] * v2[2];
    }

    public static double[] toXY(double[][] m) {
        return new double[]{m[0][0], m[1][1]};
    }
}
