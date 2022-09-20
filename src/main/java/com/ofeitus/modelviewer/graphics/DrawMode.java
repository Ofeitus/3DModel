package com.ofeitus.modelviewer.graphics;

public class DrawMode {
    public boolean wireframe;
    public int light;
    public boolean faceCulling;
    public boolean texture;
    public boolean line;

    public DrawMode(boolean wireframe, int light, boolean faceCulling, boolean texture, boolean line) {
        this.wireframe = wireframe;
        this.light = light;
        this.faceCulling = faceCulling;
        this.texture = texture;
        this.line = line;
    }
}
