package com.ofeitus.modelviewer.model;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Matrix {
    public static double[][] getLookAt(Vector3D eye, Vector3D target, Vector3D up) {
        Vector3D vz = Vector3D.substruct(eye, target).normalize();
        Vector3D vx = Vector3D.crossProduct(up, vz).normalize();
        Vector3D vy = Vector3D.crossProduct(vz, vx).normalize();

        return Matrix.multiply(
            Matrix.getTranslation(-eye.x, -eye.y, -eye.z),
            new double[][]{
                    {vx.x, vx.y, vx.z, 0},
                    {vy.x, vy.y, vy.z, 0},
                    {vz.x, vz.y, vz.z, 0},
                    {0, 0, 0, 1}
            }
        );
    }

    public static double[][] getPerspectiveProjection(double fovy, double aspect, double n, double f) {
        double radians = Math.PI / 180 * fovy;
        double sx = (1 / Math.tan(radians / 2)) / aspect;
        double sy = (1 / Math.tan(radians / 2));
        double sz = (f + n) / (f - n);
        double dz = (-2 * f * n) / (f - n);

        return new double[][]{
                {sx, 0, 0, 0},
                {0, sy, 0, 0},
                {0, 0, sz, dz},
                {0, 0, -1, 0}
        };
    }

    public static double[][] multiply(double[][] a, double[][] b) {
        double[][] m = new double[][]{
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        };
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                m[i][j] = a[i][0] * b[0][j] +
                        a[i][1] * b[1][j] +
                        a[i][2] * b[2][j] +
                        a[i][3] * b[3][j];
            }
        }
        return m;
    }

    public static double[][] getRotationX(double angle) {
        double rad = Math.PI / 180 * angle;

        return new double[][]{
                {1, 0, 0, 0},
                {0, Math.cos(rad), -Math.sin(rad), 0},
                {0, Math.sin(rad), Math.cos(rad), 0},
                {0, 0, 0, 1}
        };
    }

    public static double[][] getRotationY(double angle) {
        double rad = Math.PI / 180 * angle;

        return new double[][]{
                {Math.cos(rad), 0, Math.sin(rad), 0},
                {0, 1, 0, 0},
                {-Math.sin(rad), 0, Math.cos(rad), 0},
                {0, 0, 0, 1}
        };
    }

    public static double[][] getRotationZ(double angle) {
        double rad = Math.PI / 180 * angle;

        return new double[][]{
                {Math.cos(rad), -Math.sin(rad), 0, 0},
                {Math.sin(rad), Math.cos(rad), 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        };
    }

    public static double[][] getRotation(double x, double y, double z) {
        //x = Math.PI / 180 * x;
        //y = Math.PI / 180 * y;
        //z = Math.PI / 180 * z;
        //return new double[][]{
        //        {(cos(y) * cos(z)), (cos(z) * sin(y) * sin(x) - cos(x) * sin(z)), (cos(x) * cos(z) * sin(y) + sin(x) * sin(z)), 0},
        //        {(cos(y) * sin(z)), (cos(x) * cos(z) + sin(y) * sin(x) * sin(z)), (cos(x) * sin(y) * sin(z) - cos(z) * sin(x)), 0},
        //        {(-sin(y)), (cos(y) * sin(x)), (cos(y) * cos(x)), 0},
        //        {0, 0, 0, 1}
        //};
        return Matrix.multiply(Matrix.multiply(Matrix.getRotationY(y), Matrix.getRotationX(x)), Matrix.getRotationZ(z));
    }

    public static double[][] getTranslation(double dx, double dy, double dz) {
        return new double[][]{
                {1, 0, 0, dx},
                {0, 1, 0, dy},
                {0, 0, 1, dz},
                {0, 0, 0, 1}
        };
    }

    public static double[][] getScale(double sx, double sy, double sz) {
        return new double[][]{
                {sx, 0, 0, 0},
                {0, sy, 0, 0},
                {0, 0, sz, 0},
                {0, 0, 0, 1}
        };
    }

    public static Vector3D multiplyVector(double[][] m, Vector3D v) {
        return new Vector3D(
                m[0][0] * v.x + m[0][1] * v.y + m[0][2] * v.z + m[0][3] * v.w,
                m[1][0] * v.x + m[1][1] * v.y + m[1][2] * v.z + m[1][3] * v.w,
                m[2][0] * v.x + m[2][1] * v.y + m[2][2] * v.z + m[2][3] * v.w,
                m[3][0] * v.x + m[3][1] * v.y + m[3][2] * v.z + m[3][3] * v.w
        );
    }
}
