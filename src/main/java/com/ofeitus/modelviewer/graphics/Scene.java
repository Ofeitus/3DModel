package com.ofeitus.modelviewer.graphics;

import com.ofeitus.modelviewer.model.Camera;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Scene {
    public Camera camera;
    public Light light;
    public SkyBox skyBox;
    public final double[] zBuffer;
    public BufferedImage image;
    public int[] imageBuffer;

    public Scene(Camera camera, Light light, SkyBox skyBox, double[] zBuffer, BufferedImage image) {
        this.camera = camera;
        this.light = light;
        this.skyBox = skyBox;
        this.zBuffer = zBuffer;
        this.image = image;
        this.imageBuffer = ((DataBufferInt) this.image.getRaster().getDataBuffer()).getData();
    }
}
