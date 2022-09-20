package com.ofeitus.modelviewer;

public class FpsCounter {
    long lastLoopTime;
    long lastFpsTime;
    int fps;
    int currentFps;

    public FpsCounter() {
        lastFpsTime = System.nanoTime();
        lastFpsTime = 0;
        fps = 0;
        currentFps = 0;
    }

    public int count() {
        long now = System.nanoTime();
        long updateLength = now - lastLoopTime;
        lastLoopTime = now;

        lastFpsTime += updateLength;
        fps++;

        if (lastFpsTime >= 1000000000) {
            lastFpsTime = 0;
            currentFps = fps;
            fps = 0;
        }

        return currentFps;
    }
}
