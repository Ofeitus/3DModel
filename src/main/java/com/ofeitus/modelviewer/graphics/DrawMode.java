package com.ofeitus.modelviewer.graphics;

public class DrawMode {
    public boolean wireframe;
    public int light;
    public boolean faceCulling;
    public boolean useTexture;
    public boolean useNormalMap;
    public boolean useReflectionMap;
    public boolean line;
    public double translateX;
    public double translateY;
    public double translateZ;
    public double rotateX;
    public double rotateY;
    public double rotateZ;
    public double scale;
    public double[][] rotationMatrix;
    public double[][] transformMatrix;

    public DrawMode(boolean wireframe, int light, boolean faceCulling, boolean useTexture, boolean useNormalMap, boolean useReflectionMap, boolean line, double translateX, double translateY, double translateZ, double rotateX, double rotateY, double rotateZ, double scale) {
        this.wireframe = wireframe;
        this.light = light;
        this.faceCulling = faceCulling;
        this.useTexture = useTexture;
        this.useNormalMap = useNormalMap;
        this.useReflectionMap = useReflectionMap;
        this.line = line;
        this.translateX = translateX;
        this.translateY = translateY;
        this.translateZ = translateZ;
        this.rotateX = rotateX;
        this.rotateY = rotateY;
        this.rotateZ = rotateZ;
        this.scale = scale;
        rotationMatrix = null;
        transformMatrix = null;
    }
}
