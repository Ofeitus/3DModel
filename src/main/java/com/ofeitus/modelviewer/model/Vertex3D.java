package com.ofeitus.modelviewer.model;

public class Vertex3D {
    public double[] position;
    public double[] texture;
    public double[] normal;
    public double oneOverZ;

    public Vertex3D(double x, double y, double z, double w) {
        position = new double[4];
        texture = new double[4];
        normal = new double[]{0, 1, 0, 0};
        position[0] = x;
        position[1] = y;
        position[2] = z;
        position[3] = w;
    }

    public Vertex3D(double x, double y, double z) {
        this(x, y, z, 1);
    }

    public Vertex3D(double[] position, double[] texture, double[] normal) {
        this.position = position;
        this.texture = texture;
        this.normal = normal;
    }
}
