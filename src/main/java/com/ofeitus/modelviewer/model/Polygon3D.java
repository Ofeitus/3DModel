package com.ofeitus.modelviewer.model;

import java.util.List;

public class Polygon3D {
    private final List<Vector3D> vertices;

    public Polygon3D(List<Vector3D> vertices) {
        this.vertices = vertices;
    }

    public List<Vector3D> getVertices() {
        return vertices;
    }
}
