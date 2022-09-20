package com.ofeitus.modelviewer.model;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class PolygonGroup {
    private final String name;
    private final List<Polygon3D> polygons;
    private BufferedImage texture;
    private BufferedImage normalMap;
    private BufferedImage reflectionMap;

    public PolygonGroup(String name, BufferedImage texture, BufferedImage normalMap, BufferedImage reflectionMap) {
        this.name = name;
        this.texture = texture;
        this.normalMap = normalMap;
        this.reflectionMap = reflectionMap;
        polygons = new ArrayList<>();
    }

    public void addPolygon(Polygon3D polygon) {
        polygons.add(polygon);
    }

    public String getName() {
        return name;
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

    public void setTexture(BufferedImage texture) {
        this.texture = texture;
    }

    public void setNormalMap(BufferedImage normalMap) {
        this.normalMap = normalMap;
    }

    public void setReflectionMap(BufferedImage reflectionMap) {
        this.reflectionMap = reflectionMap;
    }

    public List<Polygon3D> getPolygons() {
        return polygons;
    }
}
