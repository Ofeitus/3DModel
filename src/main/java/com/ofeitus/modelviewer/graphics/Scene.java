package com.ofeitus.modelviewer.graphics;

import com.ofeitus.modelviewer.model.Camera;

public class Scene {
    public Camera camera;
    public Light light;
    public final double[][] zBuffer;

    public Scene(Camera camera, Light light, double[][] zBuffer) {
        this.camera = camera;
        this.light = light;
        this.zBuffer = zBuffer;
    }
}
