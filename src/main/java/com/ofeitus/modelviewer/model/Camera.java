package com.ofeitus.modelviewer.model;

import com.ofeitus.modelviewer.util.Quaternion;
import com.ofeitus.modelviewer.util.Vector4D;

import java.awt.*;

import com.ofeitus.modelviewer.constant.Constant;

public class Camera {
    private final Robot robot = new Robot();
    public double[] eye;
    public double[] target;
    public double[] up;
    public double fov;
    public double zNear;
    public double zFar;

    public Camera(double[] eye, double[] target, double[] up) throws AWTException {
        this.eye = eye;
        this.target = target;
        this.up = up;
        fov = 90.0;
        zNear = -1.0;
        zFar = -1000.0;
    }

    public void setViewByMouse(Point locationOnScreen, int mouseX, int mouseY) {
        int middleX = Constant.SCREEN_WIDTH / 2;
        int middleY = Constant.SCREEN_HEIGHT / 2;

        double[] mouseDirection = new double[]{0, 0, 0};

        robot.mouseMove(locationOnScreen.x + middleX, locationOnScreen.y + middleY);
        mouseDirection[0] = (middleX - mouseX) * Constant.MOUSE_SENSITIVITY;
        mouseDirection[1] = (middleY - mouseY) * Constant.MOUSE_SENSITIVITY;

        double[] axis = Vector4D.normalize(Vector4D.crossProduct(target, up));
        rotateCamera(mouseDirection[1], axis[0], axis[1], axis[2]);
        rotateCamera(mouseDirection[0], 0, 1, 0);
    }

    private void rotateCamera(double angle, double x, double y, double z) {
        Quaternion temp = new Quaternion();
        Quaternion quaternionView = new Quaternion();
        Quaternion result;
        temp.x = x * Math.sin(angle/2);
        temp.y = y * Math.sin(angle/2);
        temp.z = z * Math.sin(angle/2);
        temp.w = Math.cos(angle/2);
        quaternionView.x = target[0];
        quaternionView.y = target[1];
        quaternionView.z = target[2];
        quaternionView.w = 0;
        result = Quaternion.multiply(Quaternion.multiply(temp, quaternionView), Quaternion.conjugate(temp));
        target[0] = result.x;
        target[1] = result.y;
        target[2] = result.z;
    }
}
