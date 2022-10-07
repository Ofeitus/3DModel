package com.ofeitus.modelviewer.graphics;

import com.ofeitus.modelviewer.model.Camera;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Scene {
    public Camera camera;
    public Light light;
    public SkyBox skyBox;
    public final double[] zBuffer;
    public final int[] glowBuffer;
    public final BufferedImage image;
    public final int[] imageBuffer;

    public Scene(Camera camera, Light light, SkyBox skyBox, double[] zBuffer, int[] glowBuffer, BufferedImage image) {
        this.camera = camera;
        this.light = light;
        this.skyBox = skyBox;
        this.zBuffer = zBuffer;
        this.glowBuffer = glowBuffer;
        this.image = image;
        this.imageBuffer = ((DataBufferInt) this.image.getRaster().getDataBuffer()).getData();
    }
}
