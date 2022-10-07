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
    private BufferedImage glowMap;

    public PolygonGroup(String name) {
        this.name = name;
        this.texture = null;
        this.normalMap = null;
        this.reflectionMap = null;
        this.glowMap = null;
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

    public BufferedImage getGlowMap() {
        return glowMap;
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

    public void setGlowMap(BufferedImage glowMap) {
        this.glowMap = glowMap;
    }

    public List<Polygon3D> getPolygons() {
        return polygons;
    }
}
