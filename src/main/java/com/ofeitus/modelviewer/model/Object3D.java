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

    public int countPolygons() {
        int totalPolygons = 0;
        for (PolygonGroup polygonGroup : polygonGroups) {
            totalPolygons += polygonGroup.getPolygons().size();
        }
        return totalPolygons;
    }

    @Override
    public String toString() {
        String result = "Object '" + name + "'\n" + polygonGroups.size() + " polygon groups:" + "\n";
        int totalPolygons = 0;
        for (PolygonGroup polygonGroup : polygonGroups) {
            result += polygonGroup.getName() + " - " + polygonGroup.getPolygons().size() + " polygons\n";
            totalPolygons += polygonGroup.getPolygons().size();
        }
        result += totalPolygons + " polygons total\n";
        return result;
    }
}
