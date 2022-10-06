package com.ofeitus.modelviewer.util;

public class Matrix4D {
    public static double[][] getLookAt(double[] eye, double[] target, double[] up) {
        double[] vz = Vector4D.normalize(Vector4D.sub(eye, target));
        double[] vx = Vector4D.normalize(Vector4D.crossProduct(up, vz));
        double[] vy = Vector4D.normalize(Vector4D.crossProduct(vz, vx));

        return Matrix4D.multiply(
            Matrix4D.getTranslation(-eye[0], -eye[1], -eye[2]),
            new double[][]{
                    {vx[0], vx[1], vx[2], 0},
                    {vy[0], vy[1], vy[2], 0},
                    {vz[0], vz[1], vz[2], 0},
                    {0, 0, 0, 1}
            }
        );
    }

    public static double[][] getPerspectiveProjection(double fov, double aspect, double zNear, double zFar) {
        double radians = Math.PI / 180 * fov;
        double sx = (1 / Math.tan(radians / 2)) / aspect;
        double sy = (1 / Math.tan(radians / 2));
        double sz = (zFar + zNear) / (zFar - zNear);
        double dz = (-2 * zFar * zNear) / (zFar - zNear);

        return new double[][]{
                {sx, 0, 0, 0},
                {0, sy, 0, 0},
                {0, 0, sz, dz},
                {0, 0, -1, 0}
        };
    }

    public static double[][] transpose(double[][] m) {
        double[][] tm = new double[][]{
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        };
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                tm[i][j] = m[j][i];
            }
        }
        return tm;
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
        return Matrix4D.multiply(Matrix4D.multiply(Matrix4D.getRotationY(y), Matrix4D.getRotationX(x)), Matrix4D.getRotationZ(z));
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

    public static double[] multiplyVector(double[][] m, double[] v) {
        return new double[]{
                m[0][0] * v[0] + m[0][1] * v[1] + m[0][2] * v[2] + m[0][3] * v[3],
                m[1][0] * v[0] + m[1][1] * v[1] + m[1][2] * v[2] + m[1][3] * v[3],
                m[2][0] * v[0] + m[2][1] * v[1] + m[2][2] * v[2] + m[2][3] * v[3],
                m[3][0] * v[0] + m[3][1] * v[1] + m[3][2] * v[2] + m[3][3] * v[3]
        };
    }

    public static double[][] getIdentity() {
        return new double[][]{
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        };
    }

    public static double[][] copy(double[][] m) {
        return new double[][]{
                {m[0][0], m[0][1], m[0][2], m[0][3]},
                {m[1][0], m[1][1], m[1][2], m[1][3]},
                {m[2][0], m[2][1], m[2][2], m[2][3]},
                {m[3][0], m[3][1], m[3][2], m[3][3]}
        };
    }
}
