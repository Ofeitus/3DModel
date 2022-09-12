package com.ofeitus.modelviewer.model;

import java.util.ArrayList;
import java.util.List;

public class Object3D {
    private final String name;

    private final List<PolygonGroup> polygonGroups;

    public Object3D(String name) {
        this.name = name;
        polygonGroups = new ArrayList<>();
    }

    public void addPolygonGroup(PolygonGroup polygonGroup) {
        this.polygonGroups.add(polygonGroup);
    }

    public String getName() {
        return name;
    }

    public List<PolygonGroup> getPolygonGroups() {
        return polygonGroups;
    }

    @Override
    public String toString() {
        String result = "Object '" + name + "'\n" + polygonGroups.size() + " polygon groups:" + "\n";
        int totalVertices = 0;
        for (PolygonGroup polygonGroup : polygonGroups) {
            result += polygonGroup.getName() + " - " + polygonGroup.getPolygons().size() + " vertices\n";
            totalVertices += polygonGroup.getPolygons().size();
        }
        result += totalVertices + " vertices total";
        return result;
    }
}
