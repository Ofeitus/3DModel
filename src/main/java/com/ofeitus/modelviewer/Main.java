package com.ofeitus.modelviewer;

import com.ofeitus.modelviewer.graphics.Bresenham;
import com.ofeitus.modelviewer.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends JFrame {
    private final List<Object3D> objects = new ArrayList<>();
    private final double[] eye = new double[]{0, 0, 1};
    private final double[] target = new double[]{0, 0, 0};
    private final double[] up = new double[]{0, 1, 0};
    private double translateX = 100;
    private double translateY = 100;
    private double translateZ = 0;
    private double rotateX = 0;
    private double rotateY = 0;
    private double rotateZ = 0;
    private double FOV = 45;
    private final double ASPECT = 800.0 / 600.0;
    private double VIEWPORT_WIDTH = 800.0;
    private double VIEWPORT_HEIGHT = 600.0;
    private final double Z_NEAR = 0.1;
    private final double Z_FAR = 100;

    double[][] viewport = {
            {VIEWPORT_WIDTH / 2, 0, 0, VIEWPORT_WIDTH / 2},
            {0, -VIEWPORT_HEIGHT / 2, 0, VIEWPORT_HEIGHT / 2},
            {0, 0, 1, 0},
            {0, 0, 0, 1}
    };
    double[][] projection = {
            {2.0 / 8, 0, 0, 0},
            {0, 2.0 / 6, 0, 0},
            {0, 0, 1 / (Z_NEAR - Z_FAR), Z_NEAR / (Z_NEAR - Z_FAR)},
            {0, 0, -1, 0},
    };
    double[][] perspective = {
            {1 / (ASPECT * Math.tan(FOV / 2)), 0, 0, 0},
            {0, 1 / Math.tan(FOV / 2), 0, 0},
            {0, 0, Z_FAR / (Z_NEAR - Z_FAR), Z_NEAR * Z_FAR / (Z_NEAR - Z_FAR)},
            {0, 0, -1, 0},
    };

    public Main() {
        ObjectLoader objectLoader = new ObjectLoader();

        try {
            objects.add(objectLoader.loadObject("C:\\Users\\ofeitus\\Desktop\\labs\\model\\source\\Guitar.obj"));
            System.out.println(objects.get(0));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyChar()) {
                    case 'q': translateX += 1; break;
                    case 'Q': translateX -= 1; break;
                    case 'w': translateY += 1; break;
                    case 'W': translateY -= 1; break;
                    case 'e': translateZ += 1; break;
                    case 'E': translateZ -= 1; break;
                    case 'a': rotateX += 1; break;
                    case 'A': rotateX -= 1; break;
                    case 's': rotateY += 1; break;
                    case 'S': rotateY -= 1; break;
                    case 'd': rotateZ += 1; break;
                    case 'D': rotateZ -= 1; break;
                    case 'f': FOV += 1; break;
                    case 'F': FOV -= 1; break;
                }
                revalidate();
                repaint();
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    @Override
    public void paint(Graphics g) {
        Image img = createImage();
        g.drawImage(img, 0,0,this);
    }

    private void drawObject(Object3D object, Graphics g) {
        for (PolygonGroup polygonGroup : object.getPolygonGroups()) {
            for (Polygon3D polygon : polygonGroup.getPolygons()) {
                List<Vector3D> vectors = polygon.getVertices();

                double[][] vertices = new double[3][2];
                for (int i = 0; i < 3; i++) {
                    Vector3D vectorModel = vectors.get(i).rotate(rotateX, rotateY, rotateZ).translate(translateX, translateY, translateZ).scale(2);
                    //Vector3D vectorModel = vectors.get(i).scale(0.01);
                    double[][] model = {
                            {vectorModel.getX(),0,0,0},
                            {0, vectorModel.getY(),0,0},
                            {0,0, vectorModel.getZ(),0},
                            {0,0,0,1}
                    };
                    //double[] ZAxis = Matrix.normalize(Matrix.minus(target, eye));
                    //double[] XAxis = Matrix.normalize(Matrix.multiply(ZAxis, up));
                    //double[] YAxis = Matrix.normalize(Matrix.multiply(XAxis, ZAxis));
                    //double[][] view = {
                    //        {XAxis[0], XAxis[1], XAxis[2], -Matrix.scalarMultiply(XAxis, eye)},
                    //        {YAxis[0], YAxis[1], YAxis[2], -Matrix.scalarMultiply(YAxis, eye)},
                    //        {-ZAxis[0], -ZAxis[1], -ZAxis[2], -Matrix.scalarMultiply(ZAxis, eye)},
                    //        {0, 0, 0, 1}
                    //};
                    vertices[i] = Matrix.toXY(
                            //Matrix.multiply(Matrix.multiply(Matrix.multiply(viewport, perspective), view), model)
                            Matrix.multiply(perspective, model)
                    );
                }
                Bresenham.drawLine(
                        (int) vertices[0][0],
                        (int) vertices[0][1],
                        (int) vertices[1][0],
                        (int) vertices[1][1],
                        g
                );
                Bresenham.drawLine(
                        (int) vertices[1][0],
                        (int) vertices[1][1],
                        (int) vertices[2][0],
                        (int) vertices[2][1],
                        g
                );
                Bresenham.drawLine(
                        (int) vertices[2][0],
                        (int) vertices[2][1],
                        (int) vertices[0][0],
                        (int) vertices[0][1],
                        g
                );
                g.drawString("translate " + translateX + " " + translateY + " " + translateZ, 10, 600-60);
                g.drawString("rotate " + rotateX + " " + rotateY + " " + rotateZ, 10, 600-40);
                g.drawString("FOV " + FOV, 10, 600-20);

            }
        }
    }

    private Image createImage() {
        BufferedImage bufferedImage = new BufferedImage(800,600, BufferedImage.TYPE_INT_RGB);
        Graphics g = bufferedImage.getGraphics();
        for (Object3D object : objects) {
            drawObject(object, g);
        }
        return bufferedImage;
    }

    public static void main(String[] args) {
        JFrame frame = new Main();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }
}
