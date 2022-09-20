package com.ofeitus.modelviewer.util;

public class Vector4D {
    public static double[] invert(double[] v) {
        return new double[]{
                -v[0],
                -v[1],
                -v[2],
                -v[3]
        };
    }

    public static double[] sub(double[] v1, double[] v2) {
        return new double[]{
                v1[0] - v2[0],
                v1[1] - v2[1],
                v1[2] - v2[2],
                v1[3] - v2[3]
        };
    }

    public static double[] crossProduct(double[] a, double[] b) {
        return new double[]{
                a[1] * b[2] - a[2] * b[1],
                a[2] * b[0] - a[0] * b[2],
                a[0] * b[1] - a[1] * b[0],
                0
        };
    }

    public static double scalarProduct(double[] a, double[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }

    public static double[] add(double[] v1, double[] v2) {
        return new double[]{
                v1[0] + v2[0],
                v1[1] + v2[1],
                v1[2] + v2[2],
                v1[3] + v2[3]
        };
    }

    public static double[] multiplyByScalar(double[] v, double s) {
        return new double[]{
                v[0] * s,
                v[1] * s,
                v[2] * s,
                v[3] * s
        };
    }

    public static double getLength(double[] v) {
        return Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
    }

    public static double[] normalize(double[] v) {
        double length = Vector4D.getLength(v);
        return new double[]{
                v[0] / length,
                v[1] / length,
                v[2] / length,
                v[3]
        };
    }
}
