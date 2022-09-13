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
    private final double[] eye = new double[]{0, 0, 0};
    private final double[] target = new double[]{0, 0, -1};
    private final double[] up = new double[]{0, 1, 0};
    private double translateX = 0;
    private double translateY = 0;
    private double translateZ = -500;
    private double scale = 3;
    private double rotateX = 0;
    private double rotateY = -90;
    private double rotateZ = 0;

    private final double SCREEN_WIDTH = 800.0;
    private final double SCREEN_HEIGHT = 600.0;

    public Main() {
        ObjectLoader objectLoader = new ObjectLoader();

        try {
            objects.add(objectLoader.loadObject("C:\\Users\\ofeitus\\Desktop\\labs\\model\\source\\Guitar.obj"));
            //ObjectLoader.scaleObject(objects.get(0));
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
                    case 'q': translateX += 2; break;
                    case 'Q': translateX -= 2; break;
                    case 'w': translateY += 2; break;
                    case 'W': translateY -= 2; break;
                    case 'e': translateZ += 2; break;
                    case 'E': translateZ -= 2; break;
                    case 'a': rotateX += 2; break;
                    case 'A': rotateX -= 2; break;
                    case 's': rotateY += 2; break;
                    case 'S': rotateY -= 2; break;
                    case 'd': rotateZ += 2; break;
                    case 'D': rotateZ -= 2; break;
                    case 'z': scale *= 2; break;
                    case 'Z': scale /= 2; break;

                    //case 'q': eye[0] += 2; break;
                    //case 'Q': eye[0] -= 2; break;
                    //case 'w': eye[1] += 2; break;
                    //case 'W': eye[1] -= 2; break;
                    //case 'e': eye[2] += 2; break;
                    //case 'E': eye[2] -= 2; break;
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

                double[][] matrix = Matrix.getRotationX(0);
                matrix = Matrix.multiply(
                        Matrix.getRotation(rotateX, rotateY, rotateZ),
                        matrix
                );
                matrix = Matrix.multiply(
                        Matrix.getScale(scale, scale, scale),
                        matrix
                );
                matrix = Matrix.multiply(
                        Matrix.getTranslation(translateX, translateY, translateZ),
                        matrix
                );
                matrix = Matrix.multiply(
                        Matrix.getLookAt(
                                new Vector3D(0, 0, 0),
                                new Vector3D(0, 0, -1),
                                new Vector3D(0, 1, 0)
                        ),
                        matrix
                );

                matrix = Matrix.multiply(
                        Matrix.getPerspectiveProjection(
                                90, 800.0 / 600.0,
                                -1.0, -1000.0),
                        matrix
                );
                double[][] vertices = new double[3][2];
                for (int i = 0; i < 3; i++) {
                    Vector3D vertex = Matrix.multiplyVector(
                            matrix,
                            vectors.get(i)
                    );

                    vertex.x = vertex.x / vertex.w * 400 + 400;
                    vertex.y = vertex.y / vertex.w * 300 + 300;

                    vertices[i] = new double[]{
                            vertex.x,
                            vertex.y
                    };
                }
                //System.out.println(vertices[0][0]);
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
