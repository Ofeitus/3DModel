package com.ofeitus.modelviewer.graphics;

public class Light {
    double[] color;
    double[] position;
    double ka;
    double kd;
    double ks;
    int shininess;

    public Light(double[] color, double[] position, double ka, double kd, double ks, int shininess) {
        this.color = color;
        this.position = position;
        this.ka = ka;
        this.kd = kd;
        this.ks = ks;
        this.shininess = shininess;
    }
}
