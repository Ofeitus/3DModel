package com.ofeitus.modelviewer.graphics;

import com.ofeitus.modelviewer.constant.Constant;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;

import static java.lang.Math.asin;
import static java.lang.Math.atan2;

public class SkyBox {
    private BufferedImage skyboxImage;
    private int[] skyboxBuffer;
    private static final double FOV = Math.toRadians(90);
    private final double cameraPlaneDistance;
    private double[][][] rayVectors;
    private static final double ACCURACY_FACTOR = 2048;
    private static final int REQUIRED_SIZE = (int) (2 * ACCURACY_FACTOR);
    private double[] asinTable;
    private double[] atan2Table;
    private static final double INV_PI = 1 / Math.PI;
    private static final double INV_2PI = 1 / (2 * Math.PI);

    public SkyBox(String path) {
        try {
            BufferedImage sphereTmpImage = ImageIO.read(new File(path));
            skyboxImage = new BufferedImage(sphereTmpImage.getWidth(), sphereTmpImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            skyboxImage.getGraphics().drawImage(sphereTmpImage, 0, 0, null);
            skyboxBuffer = ((DataBufferInt) skyboxImage.getRaster().getDataBuffer()).getData();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
        cameraPlaneDistance = ((double) Constant.SCREEN_WIDTH / 2) / Math.tan(FOV / 2);
        createRayVecs();
        precalculateAsinAtan2();
    }

    private void createRayVecs() {
        rayVectors = new double[Constant.SCREEN_WIDTH][Constant.SCREEN_HEIGHT][3]; // x, y, z
        for (int y = 0; y < Constant.SCREEN_HEIGHT; y++) {
            for (int x = 0; x < Constant.SCREEN_WIDTH; x++) {
                double vecX = x - (double) Constant.SCREEN_WIDTH / 2;
                double vecY = y - (double) Constant.SCREEN_HEIGHT / 2;
                double vecZ = cameraPlaneDistance;
                double invVecLength = 1 / Math.sqrt(vecX * vecX + vecY * vecY + vecZ * vecZ);
                rayVectors[x][y][0] = vecX * invVecLength;
                rayVectors[x][y][1] = vecY * invVecLength;
                rayVectors[x][y][2] = vecZ * invVecLength;
            }
        }
    }

    private void precalculateAsinAtan2() {
        asinTable = new double[REQUIRED_SIZE];
        atan2Table = new double[REQUIRED_SIZE * REQUIRED_SIZE];
        for (int i = 0; i < 2 * ACCURACY_FACTOR; i++) {
            asinTable[i] = asin((i - ACCURACY_FACTOR) * 1 / ACCURACY_FACTOR);
            for (int j = 0; j < 2 * ACCURACY_FACTOR; j++) {
                double y = (i - ACCURACY_FACTOR) / ACCURACY_FACTOR;
                double x = (j - ACCURACY_FACTOR) / ACCURACY_FACTOR;
                atan2Table[i + j * REQUIRED_SIZE] = atan2(y, x);
            }
        }
    }

    public int calculateReflection(double[] target) {
        double currentRotationX = asin(-target[1]);
        double currentRotationY = -atan2(target[0], target[2]);
        double sinRotationX = Math.sin(currentRotationX);
        double cosRotationX = Math.cos(currentRotationX);
        double sinRotationY = Math.sin(currentRotationY);
        double cosRotationY = Math.cos(currentRotationY);
        double tmpVecX;
        double tmpVecY;
        double tmpVecZ;
        double vecX = rayVectors[Constant.SCREEN_WIDTH / 2][Constant.SCREEN_HEIGHT / 2][0];
        double vecY = rayVectors[Constant.SCREEN_WIDTH / 2][Constant.SCREEN_HEIGHT / 2][1];
        double vecZ = rayVectors[Constant.SCREEN_WIDTH / 2][Constant.SCREEN_HEIGHT / 2][2];
        // rotate x
        tmpVecZ = vecZ * cosRotationX - vecY * sinRotationX;
        tmpVecY = vecZ * sinRotationX + vecY * cosRotationX;
        vecZ = tmpVecZ;
        vecY = tmpVecY;
        // rotate y
        tmpVecZ = vecZ * cosRotationY - vecX * sinRotationY;
        tmpVecX = vecZ * sinRotationY + vecX * cosRotationY;
        vecZ = tmpVecZ;
        vecX = tmpVecX;
        int iX = (int) ((vecX + 1) * ACCURACY_FACTOR);
        int iY = (int) ((vecY + 1) * ACCURACY_FACTOR);
        int iZ = (int) ((vecZ + 1) * ACCURACY_FACTOR);
        // https://en.wikipedia.org/wiki/UV_mapping
        double u = 0.5 + (atan2Table[iZ + iX * REQUIRED_SIZE] * INV_2PI);
        double v = 0.5 - (asinTable[iY] * INV_PI);
        int tx = (int) (skyboxImage.getWidth() * u);
        int ty = (int) (skyboxImage.getHeight() * (1 - v));
        return skyboxBuffer[ty * skyboxImage.getWidth() + tx];
    }

    public void draw(Scene scene) {
        double currentRotationX = asin(-scene.camera.target[1]);
        double currentRotationY = -atan2(scene.camera.target[0], scene.camera.target[2]);
        double sinRotationX = Math.sin(currentRotationX);
        double cosRotationX = Math.cos(currentRotationX);
        double sinRotationY = Math.sin(currentRotationY);
        double cosRotationY = Math.cos(currentRotationY);
        double tmpVecX;
        double tmpVecY;
        double tmpVecZ;
        for (int y = 0; y < Constant.SCREEN_HEIGHT; y++) {
            for (int x = 0; x < Constant.SCREEN_WIDTH; x++) {
                double vecX = rayVectors[x][y][0];
                double vecY = rayVectors[x][y][1];
                double vecZ = rayVectors[x][y][2];
                // rotate x
                tmpVecZ = vecZ * cosRotationX - vecY * sinRotationX;
                tmpVecY = vecZ * sinRotationX + vecY * cosRotationX;
                vecZ = tmpVecZ;
                vecY = tmpVecY;
                // rotate y
                tmpVecZ = vecZ * cosRotationY - vecX * sinRotationY;
                tmpVecX = vecZ * sinRotationY + vecX * cosRotationY;
                vecZ = tmpVecZ;
                vecX = tmpVecX;
                int iX = (int) ((vecX + 1) * ACCURACY_FACTOR);
                int iY = (int) ((vecY + 1) * ACCURACY_FACTOR);
                int iZ = (int) ((vecZ + 1) * ACCURACY_FACTOR);
                // https://en.wikipedia.org/wiki/UV_mapping
                double u = 0.5 + (atan2Table[iZ + iX * REQUIRED_SIZE] * INV_2PI);
                double v = 0.5 - (asinTable[iY] * INV_PI);
                int tx = (int) (skyboxImage.getWidth() * u);
                int ty = (int) (skyboxImage.getHeight() * (1 - v));
                int color = skyboxBuffer[ty * skyboxImage.getWidth() + tx];
                scene.imageBuffer[y * Constant.SCREEN_WIDTH + x] = color;
            }
        }
    }
}
