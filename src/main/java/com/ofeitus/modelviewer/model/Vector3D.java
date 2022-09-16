package com.ofeitus.modelviewer.model;

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

    public static double scalarProduct(Vector3D a, Vector3D b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    public static Vector3D add(Vector3D v1, Vector3D v2) {
        return new Vector3D(
                v1.x + v2.x,
                v1.y + v2.y,
                v1.z + v2.z
        );
    }

    public Vector3D multiplyByScalar(double s) {
        return new Vector3D(
                this.x * s,
                this.y * s,
                this.z * s
        );
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
}
