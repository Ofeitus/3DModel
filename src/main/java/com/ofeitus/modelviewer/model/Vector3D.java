package com.ofeitus.modelviewer.model;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Vector3D {
    private final double x;
    private final double y;
    private final double z;
    private final double w;

    public Vector3D(double x, double y, double z) {
        this(x, y, z, 1);
    }

    public Vector3D(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getW() {
        return w;
    }

    public double getLength() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public Vector3D multiplyByMatrix(double[][] m) {
        return new Vector3D(
                m[0][0] * this.x + m[0][1] * this.y + m[0][2] * this.z + m[0][3] * this.w,
                m[1][0] * this.x + m[1][1] * this.y + m[1][2] * this.z + m[1][3] * this.w,
                m[2][0] * this.x + m[2][1] * this.y + m[2][2] * this.z + m[2][3] * this.w,
                m[3][0] * this.x + m[3][1] * this.y + m[3][2] * this.z + m[3][3] * this.w
        );
    }

    public Vector3D scale(double value) {
        return new Vector3D(
                value * this.x + 0 * this.y + 0 * this.z + 0 * this.w,
                0 * this.x + value * this.y + 0 * this.z + 0 * this.w,
                0 * this.x + 0 * this.y + value * this.z + 0 * this.w,
                0 * this.x + 0 * this.y + 0 * this.z + 1 * this.w
        );
    }

    public Vector3D translate(double x, double y, double z) {
        return new Vector3D(
                1 * this.x + 0 * this.y + 0 * this.z + x,
                0 * this.x + 1 * this.y + 0 * this.z + y,
                0 * this.x + 0 * this.y + 1 * this.z + z,
                0 * this.x + 0 * this.y + 0 * this.z + 1 * this.w
        );
    }

    public Vector3D rotateX(double angle) {
        double rad = Math.PI / 180 * angle;
        return new Vector3D(
                1 * this.x + 0 * this.y + 0 * this.z + 0 * this.w,
                0 * this.x + cos(rad) * this.y + -sin(rad) * this.z + 0 * this.w,
                0 * this.x + sin(rad) * this.y + cos(rad) * this.z + 0 * this.w,
                0 * this.x + 0 * this.y + 0 * this.z + 1 * this.w
        );
    }

    public Vector3D rotateY(double angle) {
        double rad = Math.PI / 180 * angle;
        return new Vector3D(
                cos(rad) * this.x + 0 * this.y + sin(rad) * this.z + 0 * this.w,
                0 * this.x + 1 * this.y + 0 * this.z + 0 * this.w,
                -sin(rad) * this.x + 0 * this.y + cos(rad) * this.z + 0 * this.w,
                0 * this.x + 0 * this.y + 0 * this.z + 1 * this.w
        );
    }

    public Vector3D rotateZ(double angle) {
        double rad = Math.PI / 180 * angle;
        return new Vector3D(
                cos(rad) * this.x + -sin(rad) * this.y + 0 * this.z + 0 * this.w,
                sin(rad) * this.x + cos(rad) * this.y + 0 * this.z + 0 * this.w,
                0 * this.x + 0 * this.y + 1 * this.z + 0 * this.w,
                0 * this.x + 0 * this.y + 0 * this.z + 1 * this.w
        );
    }

    public Vector3D rotate(double x, double y, double z) {
        x = Math.PI / 180 * x;
        y = Math.PI / 180 * y;
        z = Math.PI / 180 * z;
        return new Vector3D(
                (cos(y) * cos(z)) * this.x + (cos(z) * sin(y) * sin(x) - cos(x) * sin(z)) * this.y + (cos(x) * cos(z) * sin(y) + sin(x) * sin(z)) * this.z + 0 * this.w,
                (cos(y) * sin(z)) * this.x + (cos(x) * cos(z) + sin(y) * sin(x) * sin(z)) * this.y + (cos(x) * sin(y) * sin(z) - cos(z) * sin(x)) * this.z + 0 * this.w,
                (-sin(y)) * this.x + (cos(y) * sin(x)) * this.y + (cos(y) * cos(x)) * this.z + 0 * this.w,
                0 * this.x + 0 * this.y + 0 * this.z + 1 * this.w
        );
    }
}
