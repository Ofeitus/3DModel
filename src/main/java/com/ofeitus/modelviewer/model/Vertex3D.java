package com.ofeitus.modelviewer.model;

public class Vertex3D {
    public double[] position;
    public double[] model_pos;
    public double[] texture;
    public double[] normal;
    public double oneOverZ;

    public Vertex3D(double x, double y, double z, double w) {
        position = new double[4];
        model_pos = new double[4];
        texture = new double[4];
        normal = new double[]{0, 1, 0, 0};
        position[0] = x;
        position[1] = y;
        position[2] = z;
        position[3] = w;
        model_pos[0] = x;
        model_pos[1] = y;
        model_pos[2] = z;
        model_pos[3] = w;
    }

    public Vertex3D(double x, double y, double z) {
        this(x, y, z, 1);
    }

    public Vertex3D(double[] position, double[] model_pos, double[] texture, double[] normal) {
        this.position = position;
        this.model_pos = model_pos;
        this.texture = texture;
        this.normal = normal;
    }
}
