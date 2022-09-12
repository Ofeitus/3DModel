package com.ofeitus.modelviewer.model;

import java.util.ArrayList;
import java.util.List;

public class PolygonGroup {
    private final String name;
    private final List<Polygon3D> polygons;

    public PolygonGroup() {
        name = null;
        polygons = new ArrayList<>();
    }

    public PolygonGroup(String name) {
        this.name = name;
        polygons = new ArrayList<>();
    }

    public void addPolygon(Polygon3D polygon) {
        polygons.add(polygon);
    }

    public String getName() {
        return name;
    }

    public List<Polygon3D> getPolygons() {
        return polygons;
    }
}
