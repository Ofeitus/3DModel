package com.ofeitus.modelviewer.model;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Object3D {
    private final String name;
    private final List<PolygonGroup> polygonGroups;
    private final BufferedImage texture;
    private final BufferedImage normalMap;

    private final BufferedImage reflectionMap;

    public Object3D(String name, BufferedImage texture, BufferedImage normalMap, BufferedImage reflectionMap) {
        this.name = name;
        this.texture = texture;
        this.normalMap = normalMap;
        this.reflectionMap = reflectionMap;
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

    public BufferedImage getTexture() {
        return texture;
    }

    public BufferedImage getNormalMap() {
        return normalMap;
    }

    public BufferedImage getReflectionMap() {
        return reflectionMap;
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
