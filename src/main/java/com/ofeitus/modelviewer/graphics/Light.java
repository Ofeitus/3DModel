package com.ofeitus.modelviewer.graphics;

public class Light {
    public double[] color;
    public double[] position;
    public double ka;
    public double kd;
    public double ks;
    public int shininess;

    public Light(double[] color, double[] position, double ka, double kd, double ks, int shininess) {
        this.color = color;
        this.position = position;
        this.ka = ka;
        this.kd = kd;
        this.ks = ks;
        this.shininess = shininess;
    }
}
