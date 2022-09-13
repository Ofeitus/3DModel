package com.ofeitus.modelviewer.model;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Vector3D {
    public double x;
    public double y;
    public double z;
    public double w;

    public Vector3D(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector3D(double x, double y, double z) {
        this(x, y, z, 1);
    }

    public static Vector3D substruct(Vector3D v1, Vector3D v2) {
        return new Vector3D(
                v1.x - v2.x,
                v1.y - v2.y,
                v1.z - v2.z,
                v1.w - v2.w
        );
    }

    public static Vector3D crossProduct(Vector3D a, Vector3D b) {
        return new Vector3D(
                a.y * b.z - a.z * b.y,
                a.z * b.x - a.x * b.z,
                a.x * b.y - a.y * b.x
        );
    }

    public static Vector3D add(Vector3D v1, Vector3D v2) {
        return new Vector3D(
                v1.x + v2.x,
                v1.y + v2.y,
                v1.z + v2.z
        );
    }

    public Vector3D multiplyByScalar(double s) {
        this.x *= s;
        this.y *= s;
        this.z *= s;
        return this;
    }

    public double getLength() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public Vector3D normalize() {
        double length = this.getLength();
        this.x /= length;
        this.y /= length;
        this.z /= length;
        return this;
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
