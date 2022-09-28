package com.ofeitus.modelviewer;

import com.ofeitus.modelviewer.constant.Constant;
import com.ofeitus.modelviewer.graphics.DrawMode;
import com.ofeitus.modelviewer.graphics.Drawer;
import com.ofeitus.modelviewer.graphics.Light;
import com.ofeitus.modelviewer.graphics.Scene;
import com.ofeitus.modelviewer.model.*;
import com.ofeitus.modelviewer.util.Matrix4D;
import com.ofeitus.modelviewer.util.Vector4D;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Game implements Runnable {
    private final List<Object3D> objects = new ArrayList<>();
    private Scene scene = new Scene(
            new Camera(
                new double[]{0, 100, 150, 1},
                new double[]{0, 0, -1, 1},
                new double[]{0, 1, 0, 1}
            ),
            new Light(
                new double[]{1, 1, 1, 1},
                new double[]{100, 50, 100, 1},
                0.1, 0.4, 0.8, 4
            ),
            new double[Constant.SCREEN_HEIGHT][Constant.SCREEN_WIDTH]
    );
    JFrame frame;
    BufferedImage background;
    boolean[] keys = new boolean[1024];
    private boolean running = false;
    private FpsCounter fpsCounter = new FpsCounter();
    private double rotationX = 0;
    private double rotationY = 0;
    private boolean useTexture = false;
    private boolean useNormalMap = false;
    private boolean useReflectionMap = false;

    public Game() throws AWTException {
        // Invisible cursor
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");


        // Load objects
        ObjectLoader objectLoader = new ObjectLoader();
        try {
            background = ImageIO.read(new File("C:\\Users\\ofeitus\\Desktop\\labs\\models\\background.jpg"));
            // Grid
            Object3D grid = new Object3D("Grid");
            PolygonGroup polygonGroup = new PolygonGroup("grid", null, null, null);
            for (int i = -10; i <= 10; i++) {
                List<Vertex3D> vertices = new ArrayList<>();
                vertices.add(new Vertex3D(i * 10.0, 0, -100));
                vertices.add(new Vertex3D(i * 10.0, 0, 100));
                vertices.add(new Vertex3D(i * 10.0, 0, 100));
                Polygon3D polygon = new Polygon3D(vertices);
                polygon.setNormal(new double[]{0, 1, 0, 0});
                polygonGroup.addPolygon(polygon);
                vertices = new ArrayList<>();
                vertices.add(new Vertex3D(-100, 0, i * 10.0));
                vertices.add(new Vertex3D( 100, 0, i * 10.0));
                vertices.add(new Vertex3D( 100, 0, i * 10.0));
                polygon = new Polygon3D(vertices);
                polygon.setNormal(new double[]{0, 1, 0, 0});
                polygonGroup.addPolygon(polygon);
            }
            grid.addPolygonGroup(polygonGroup);
            objects.add(grid);

            objects.add(objectLoader.loadObject("C:\\Users\\ofeitus\\Desktop\\labs\\models\\skull\\skull.obj"));
            objects.add(objectLoader.loadObject("C:\\Users\\ofeitus\\Desktop\\labs\\models\\cube\\cube.obj"));
            for (Object3D object : objects) {
                System.out.println(object);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        frame = new JFrame("3D"){

            @Override
            public void paint(Graphics g) {
                try {
                    Game.this.drawImage();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                g.drawImage(scene.image, 0,0,this);
            }
        };

        frame.setCursor(blankCursor);

        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                // Do nothing
            }

            @Override
            public void keyPressed(KeyEvent e) {
                keys[e.getKeyCode()] = true;
                if (e.getKeyChar() == 't') {
                    useTexture = !useTexture;
                }
                if (e.getKeyChar() == 'n') {
                    useNormalMap = !useNormalMap;
                }
                if (e.getKeyChar() == 'r') {
                    useReflectionMap = !useReflectionMap;
                }
                if (e.getKeyChar() == 'i') {
                    Constant.INTERPOLATION = !Constant.INTERPOLATION;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                keys[e.getKeyCode()] = false;
            }
        });

        frame.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // Do nothing
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                scene.camera.setViewByMouse(frame.getLocationOnScreen(), e.getX(), e.getY());
            }
        });

        frame.addMouseWheelListener(e -> {
            if (scene.camera.fov >= 1.0 && scene.camera.fov <= 90.0)
                scene.camera.fov += e.getPreciseWheelRotation() * 2;
            if (scene.camera.fov <= 1.0)
                scene.camera.fov = 1.0;
            if (scene.camera.fov >= 90.0)
                scene.camera.fov = 90.0;
        });

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(Constant.SCREEN_WIDTH, Constant.SCREEN_HEIGHT);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void drawImage() throws InterruptedException {
        for (int i = 0; i < Constant.SCREEN_HEIGHT; i++) {
            for (int j = 0; j < Constant.SCREEN_WIDTH; j++) {
                scene.zBuffer[i][j] = 0;
            }
        }
        scene.image = new BufferedImage(Constant.SCREEN_WIDTH, Constant.SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics g = scene.image.getGraphics();
        g.drawImage(background, 0, 0, Constant.SCREEN_WIDTH, Constant.SCREEN_HEIGHT, null, null);

        // Draw grid
        drawObject(objects.get(0), new DrawMode(
                false,
                1,
                true,
                false,
                false,
                false,
                true,
                0, 0, 0, 0, 0, 0, 1));

        // Draw objects
        drawObject(objects.get(1), new DrawMode(
                false,
                1,
                true,
                useTexture,
                useNormalMap,
                useReflectionMap,
                false,
                0, 80, 0, rotationX, rotationY, 0, 50));
        //drawObject(objects.get(2), new DrawMode(
        //        false,
        //        1,
        //        true,
        //        useTexture,
        //        useNormalMap,
        //        useReflectionMap,
        //        false,
        //        60, 80, 0, rotationX, rotationY, 0, 50));

        // Draw fps count
        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        g.setColor(useTexture ? Color.GREEN : Color.RED);
        g.drawString("textures: " + (useTexture ? "on" : "off"), 10, Constant.SCREEN_HEIGHT - 90);
        g.setColor(useNormalMap ? Color.GREEN : Color.RED);
        g.drawString("normal map: " + (useNormalMap ? "on" : "off"), 10, Constant.SCREEN_HEIGHT - 70);
        g.setColor(useReflectionMap ? Color.GREEN : Color.RED);
        g.drawString("reflection map: " + (useReflectionMap ? "on" : "off"), 10, Constant.SCREEN_HEIGHT - 50);
        g.setColor(Constant.INTERPOLATION ? Color.GREEN : Color.RED);
        g.drawString("interpolation: " + (Constant.INTERPOLATION ? "perspective" : "affine"), 10, Constant.SCREEN_HEIGHT - 30);
        g.setColor(Color.WHITE);
        g.drawString("fps: " + fpsCounter.count(), 10, Constant.SCREEN_HEIGHT - 10);
    }

    private void drawObject(Object3D object, DrawMode drawMode) {
        double[][] matrix = Matrix4D.getIdentity();
        final double[][] viewProjectionMatrix;
        matrix = Matrix4D.multiply(
                Matrix4D.getRotation(drawMode.rotateX, drawMode.rotateY, drawMode.rotateZ),
                matrix
        );
        drawMode.rotationMatrix = Matrix4D.copy(matrix);
        matrix = Matrix4D.multiply(
                Matrix4D.getScale(drawMode.scale, drawMode.scale, drawMode.scale),
                matrix
        );
        matrix = Matrix4D.multiply(
                Matrix4D.getTranslation(drawMode.translateX, drawMode.translateY, drawMode.translateZ),
                matrix
        );
        drawMode.transformMatrix = Matrix4D.copy(matrix);
        matrix = Matrix4D.multiply(
                Matrix4D.getTranslation(-scene.camera.eye[0], -scene.camera.eye[1], -scene.camera.eye[2]),
                matrix
        );
        matrix = Matrix4D.multiply(
                Matrix4D.getLookAt(
                        new double[]{0, 0, 0, 1},
                        scene.camera.target,
                        scene.camera.up
                ),
                matrix
        );

        matrix = Matrix4D.multiply(
                Matrix4D.getPerspectiveProjection(
                        scene.camera.fov,
                        (double)Constant.SCREEN_WIDTH / Constant.SCREEN_HEIGHT,
                        scene.camera.zNear,
                        scene.camera.zFar),
                matrix
        );
        viewProjectionMatrix = matrix;

        for (PolygonGroup polygonGroup : object.getPolygonGroups()) {
            //for (Polygon3D polygon : polygonGroup.getPolygons()) {
            polygonGroup.getPolygons().parallelStream().forEach(polygon -> {
                List<Vertex3D> vectors = polygon.getVertices();

                Vertex3D[] vertices = new Vertex3D[3];
                for (int i = 0; i < 3; i++) {
                    vertices[i] = new Vertex3D(
                            Matrix4D.multiplyVector(
                                    viewProjectionMatrix,
                                    vectors.get(i).position
                            ),
                            vectors.get(i).texture,
                            Matrix4D.multiplyVector(
                                    drawMode.transformMatrix,
                                    drawMode.light == 0 ? polygon.getNormal() : vectors.get(i).normal
                            )
                    );
                }

                if (vertices[0].position[2] < 0 && vertices[1].position[2] < 0 && vertices[2].position[2] < 0) {
                    // Draw polygon
                    Drawer.drawTriangle(
                            scene,
                            polygonGroup,
                            polygon,
                            drawMode,
                            vertices[0],
                            vertices[1],
                            vertices[2]
                    );
                }
            });
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
            scene.camera.eye = Vector4D.add(scene.camera.eye, Vector4D.multiplyByScalar(scene.camera.target, cameraSpeed));
        if (keys[83]) // S
            scene.camera.eye = Vector4D.sub(scene.camera.eye, Vector4D.multiplyByScalar(scene.camera.target, cameraSpeed));
        if (keys[65]) // A
            scene.camera.eye = Vector4D.add(scene.camera.eye, Vector4D.multiplyByScalar(Vector4D.normalize(Vector4D.crossProduct(scene.camera.up, scene.camera.target)), cameraSpeed));
        if (keys[68]) // D
            scene.camera.eye = Vector4D.sub(scene.camera.eye, Vector4D.multiplyByScalar(Vector4D.normalize(Vector4D.crossProduct(scene.camera.up, scene.camera.target)), cameraSpeed));
        if (keys[38]) // UP
            rotationX = (rotationX + 1) % 356;
        if (keys[40]) // DOWN
            rotationX = (rotationX - 1) % 356;
        if (keys[37]) // LEFT
            rotationY = (rotationY + 1) % 356;
        if (keys[39]) // RIGHT
            rotationY = (rotationY - 1) % 356;
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

            long timeout = (lastLoopTime-System.nanoTime() + optimalTime)/1000000;
            if (timeout >= 0) {
                try {
                    Thread.sleep(timeout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void render() {
        frame.repaint();
    }

    public static void main(String[] args) throws AWTException {
        Game game = new Game();
        game.start();
    }
}
