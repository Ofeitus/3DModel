package com.ofeitus.modelviewer.model;

import com.ofeitus.modelviewer.util.Vector4D;

import java.util.List;

public class Polygon3D {
    private final List<Vertex3D> vertices;
    private double[] normal;
    private final double[] center;

    public Polygon3D(List<Vertex3D> vertices) {
        this.vertices = vertices;
        double[] v1 = vertices.get(0).position;
        double[] v2 = vertices.get(1).position;
        double[] v3 = vertices.get(2).position;
        double[] vec21 = Vector4D.sub(v2, v1);
        double[] vec31 = Vector4D.sub(v3, v1);
        normal = Vector4D.normalize(Vector4D.crossProduct(vec21, vec31));
        center = Vector4D.multiplyByScalar(Vector4D.add(Vector4D.add(v1, v2), v3), 1.0 / 3.0);
    }

    public List<Vertex3D> getVertices() {
        return vertices;
    }

    public double[] getNormal() {
        return normal;
    }

    public void setNormal(double[] normal) {
        this.normal = normal;
    }

    public double[] getCenter() {
        return center;
    }
}
