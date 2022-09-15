package com.ofeitus.modelviewer;

import com.ofeitus.modelviewer.graphics.Bresenham;
import com.ofeitus.modelviewer.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Game implements Runnable {
    private final List<Object3D> objects = new ArrayList<>();
    private Vector3D eye = new Vector3D(0, 0, 300);
    private Vector3D target = new Vector3D(0, 0, -1);
    private final Vector3D up = new Vector3D(0, 1, 0);
    private final double SCREEN_WIDTH = 1280.0;
    private final double SCREEN_HEIGHT = 720.0;
    JFrame frame;
    boolean[] keys = new boolean[1024];
    public boolean running = false;
    boolean firstMouse = true;
    double lastX = SCREEN_WIDTH / 2;
    double lastY = SCREEN_HEIGHT / 2;
    double yaw = -90.0;
    double pitch = 0.0;
    double fov = 90.0;

    public Game() {
        ObjectLoader objectLoader = new ObjectLoader();

        try {
            objects.add(objectLoader.loadObject("C:\\Users\\ofeitus\\Desktop\\labs\\models\\guitar\\source\\Guitar.obj"));
            objects.add(objectLoader.loadObject("C:\\Users\\ofeitus\\Desktop\\labs\\models\\amp\\source\\amp.obj"));
            Object3D grid = new Object3D("Grid");
            PolygonGroup polygonGroup = new PolygonGroup("grid");
            for (int i = -10; i <= 10; i++) {
                List<Vector3D> vertices = new ArrayList<>();
                vertices.add(new Vector3D(i * 10.0, 0, -100));
                vertices.add(new Vector3D(i * 10.0, 0,  100));
                vertices.add(new Vector3D(i * 10.0, 0,  100));
                polygonGroup.addPolygon(new Polygon3D(vertices));
                vertices = new ArrayList<>();
                vertices.add(new Vector3D(-100, 0, i * 10.0));
                vertices.add(new Vector3D( 100, 0, i * 10.0));
                vertices.add(new Vector3D( 100, 0, i * 10.0));
                polygonGroup.addPolygon(new Polygon3D(vertices));
            }
            grid.addPolygonGroup(polygonGroup);
            objects.add(grid);
            for (Object3D object : objects) {
                System.out.println(object);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        frame = new JFrame("3D"){
            @Override
            public void paint(Graphics g) {
                Image img = Game.this.createImage();
                g.drawImage(img, 0,0,this);
            }
        };

        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                keys[e.getKeyCode()] = true;
            }

            @Override
            public void keyReleased(KeyEvent e) {
                keys[e.getKeyCode()] = false;
            }
        });

        frame.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                double xpos = e.getX();
                double ypos = e.getY();

                double xoffset = xpos - lastX;
                double yoffset = lastY - ypos;
                lastX = xpos;
                lastY = ypos;

                double sensitivity = 0.4;
                xoffset *= sensitivity;
                yoffset *= sensitivity;

                yaw   += xoffset;
                pitch += yoffset;

                if (pitch > 89.0)
                    pitch = 89.0;
                if (pitch < -89.0)
                    pitch = -89.0;

                target = new Vector3D(
                        cos(Math.toRadians(yaw)) * cos(Math.toRadians(pitch)),
                        sin(Math.toRadians(pitch)),
                        sin(Math.toRadians(yaw)) * cos(Math.toRadians(pitch))
                ).normalize();
            }
        });

        frame.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (fov >= 1.0 && fov <= 90.0)
                    fov += e.getPreciseWheelRotation() * 2;
                if (fov <= 1.0)
                    fov = 1.0;
                if (fov >= 90.0)
                    fov = 90.0;
            }
        });

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize((int)SCREEN_WIDTH, (int)SCREEN_HEIGHT);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private Image createImage() {
        BufferedImage bufferedImage = new BufferedImage((int)SCREEN_WIDTH, (int)SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics g = bufferedImage.getGraphics();
        drawObject(g, objects.get(0), -90, 120, 0, 45, -90, 60, 1);
        drawObject(g, objects.get(1), 0, 60, -10, 0, 0, 0, 3);
        drawObject(g, objects.get(2), 0, 0, 0, 0, 0, 0, 2);

        return bufferedImage;
    }

    private void drawObject(Graphics g, Object3D object, double translateX, double translateY, double translateZ, double rotateX, double rotateY, double rotateZ, double scale) {
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
                                eye,
                                Vector3D.add(eye, target),
                                up
                        ),
                        matrix
                );

                matrix = Matrix.multiply(
                        Matrix.getPerspectiveProjection(
                                fov, SCREEN_WIDTH / SCREEN_HEIGHT,
                                -1.0, -1000.0),
                        matrix
                );
                double[][] vertices = new double[3][2];
                for (int i = 0; i < 3; i++) {
                    Vector3D vertex = Matrix.multiplyVector(
                            matrix,
                            vectors.get(i)
                    );
                    vertices[i] = new double[]{
                            vertex.x / vertex.w * SCREEN_WIDTH / 2 + SCREEN_WIDTH / 2,
                            SCREEN_HEIGHT - (vertex.y / vertex.w * SCREEN_HEIGHT / 2 + SCREEN_HEIGHT / 2)
                    };
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
            }
        }
    }

    public synchronized void start() {
        new Thread(this).start();
        running = true;
    }

    public synchronized void stop() {
        running = false;
    }

    void moveCamera(double delta) {
        double cameraSpeed = 2.0 * delta;
        if (keys[87]) // W
            eye = Vector3D.add(eye, target.multiplyByScalar(cameraSpeed));
        if (keys[83]) // S
            eye = Vector3D.substruct(eye, target.multiplyByScalar(cameraSpeed));
        if (keys[65]) // A
            eye = Vector3D.add(eye, Vector3D.crossProduct(up, target).normalize().multiplyByScalar(cameraSpeed));
        if (keys[68]) // D
            eye = Vector3D.substruct(eye, Vector3D.crossProduct(up, target).normalize().multiplyByScalar(cameraSpeed));
    }

    @Override
    public void run() {
        long lastLoopTime = System.nanoTime();
        double delta;
        int targetFps = 60;
        long optimalTime = 1000000000 / targetFps;

        while (running) {
            long now = System.nanoTime();
            long updateLength = now - lastLoopTime;
            lastLoopTime = now;
            delta = updateLength / ((double)optimalTime);

            // draw
            moveCamera(delta);
            render();

            try{
                Thread.sleep( (lastLoopTime-System.nanoTime() + optimalTime)/1000000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void render() {
        frame.repaint();
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }
}
