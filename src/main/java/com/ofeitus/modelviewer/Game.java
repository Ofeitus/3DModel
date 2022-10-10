package com.ofeitus.modelviewer;

import com.ofeitus.modelviewer.constant.Constant;
import com.ofeitus.modelviewer.graphics.*;
import com.ofeitus.modelviewer.model.*;
import com.ofeitus.modelviewer.util.Matrix4D;
import com.ofeitus.modelviewer.util.Vector4D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Game implements Runnable {
    private final String rootDirectory = "C:\\Users\\ofeitus\\Desktop\\labs\\";
    private final List<Object3D> objects = new ArrayList<>();
    private int displayedObject = 0;
    private final Scene scene = new Scene(
            new Camera(
                new double[]{0, 150, 200, 1},
                new double[]{0, 0, -1, 1},
                new double[]{0, 1, 0, 1}
            ),
            new Light(
                new double[]{1, 1, 1, 1},
                new double[]{-100, 200, 100, 1},
                0.1, 0.4, 0.8, 4
            ),
            new SkyBox(rootDirectory + "models\\skybox\\skybox.jpg"),
            new double[Constant.SCREEN_HEIGHT * Constant.SCREEN_WIDTH],
            new int[Constant.SCREEN_HEIGHT * Constant.SCREEN_WIDTH],
            new BufferedImage(Constant.SCREEN_WIDTH, Constant.SCREEN_HEIGHT, BufferedImage.TYPE_INT_ARGB)
    );
    JFrame frame;
    boolean[] keys = new boolean[1024];
    private boolean running = false;
    private final FpsCounter fpsCounter = new FpsCounter();
    private double rotationX = 0;
    private double rotationY = 0;
    private boolean useTexture = false;
    private boolean useNormalMap = false;
    private boolean useReflectionMap = false;
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    public Game() throws AWTException {
        // Invisible cursor
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");

        // Load objects
        ObjectLoader objectLoader = new ObjectLoader();
        try {
            // Grid
            Object3D grid = new Object3D("Grid");
            PolygonGroup polygonGroup = new PolygonGroup("grid");
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

            objects.add(objectLoader.loadObject(rootDirectory + "models\\skull\\skull.obj"));
            objects.add(objectLoader.loadObject(rootDirectory + "models\\impala\\impala.obj"));
            objects.add(objectLoader.loadObject(rootDirectory + "models\\cube\\cube.obj"));

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
                if (e.getKeyCode() >= 49 && e.getKeyCode() <= 57) {
                    displayedObject = e.getKeyCode() - 48;
                }
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
                if (e.getKeyChar() == 'e') {
                    Constant.GLOW = !Constant.GLOW;
                }
                if (e.getKeyChar() == ',') {
                    scene.skyBox = new SkyBox(rootDirectory + "models\\skybox\\skybox.jpg");
                    scene.skyBox.createRayVectors(scene.camera.fov);
                }
                if (e.getKeyChar() == '.') {
                    scene.skyBox = new SkyBox(rootDirectory + "models\\skybox\\skybox_night.jpg");
                    scene.skyBox.createRayVectors(scene.camera.fov);
                }
                if (e.getKeyChar() == ']') {
                    scene.light.position[0] += 10;
                }
                if (e.getKeyChar() == '[') {
                    scene.light.position[0] -= 10;
                }
                if (e.getKeyChar() == 'b') {
                    scene.skyBox.createRayVectors(scene.camera.fov);
                    Constant.SKYBOX = !Constant.SKYBOX;
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
            if (Constant.SKYBOX) {
                scene.skyBox.createRayVectors(scene.camera.fov);
            }
        });

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(Constant.SCREEN_WIDTH, Constant.SCREEN_HEIGHT);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);
        frame.setVisible(true);
    }


    private void drawImage() throws InterruptedException {
        Arrays.fill(scene.zBuffer, 0);
        Arrays.fill(scene.glowBuffer, 0xff000000);
        Graphics g = scene.image.getGraphics();
        if (Constant.SKYBOX) {
            scene.skyBox.draw(scene);
        } else {
            Arrays.fill(scene.imageBuffer, 0xff000000);
        }

        // Draw grid
        if (displayedObject == 0) {
            renderObject(objects.get(0), new DrawMode(
                    false,
                    1,
                    true,
                    false,
                    false,
                    false,
                    false,
                    true,
                    0, 0, 0, 0, 0, 0, 1));
        }

        // Draw objects
        if (displayedObject == 1) {
            renderObject(objects.get(1), new DrawMode(
                    false,
                    1,
                    true,
                    true,
                    useTexture,
                    useNormalMap,
                    useReflectionMap,
                    false,
                    0, 0, 0, rotationX, rotationY, 0, 50));
        }
        if (displayedObject == 2) {
            renderObject(objects.get(2), new DrawMode(
                    false,
                    1,
                    true,
                    true,
                    useTexture,
                    useNormalMap,
                    useReflectionMap,
                    false,
                    0, 0, 0, rotationX, rotationY, 0, 100));
        }
        if (displayedObject == 3) {
            renderObject(objects.get(3), new DrawMode(
                    false,
                    1,
                    true,
                    true,
                    useTexture,
                    useNormalMap,
                    useReflectionMap,
                    false,
                    0, 0, 0, rotationX, rotationY, 0, 50));
        }

        // Glow effect
        if (Constant.GLOW) {
            for (int y = 0; y < Constant.SCREEN_HEIGHT; y++) {
                for (int x = 0; x < Constant.SCREEN_WIDTH; x++) {
                    if (scene.glowBuffer[y * Constant.SCREEN_WIDTH + x] != 0xff000000 && (x + y) % 2 == 0) {
                        Drawer.drawGlow(g, x, y,
                                20,
                                scene.glowBuffer[y * Constant.SCREEN_WIDTH + x]);
                    }
                }
            }
        }

        // Draw axes
        Drawer.drawAxes(scene);

        // Draw info
        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        g.setColor(useTexture ? Color.GREEN : Color.RED);
        g.drawString("textures: " + (useTexture ? "on" : "off"), 10, Constant.SCREEN_HEIGHT - 115);
        g.setColor(useNormalMap ? Color.GREEN : Color.RED);
        g.drawString("normal map: " + (useNormalMap ? "on" : "off"), 10, Constant.SCREEN_HEIGHT - 95);
        g.setColor(useReflectionMap ? Color.GREEN : Color.RED);
        g.drawString("reflection map: " + (useReflectionMap ? "on" : "off"), 10, Constant.SCREEN_HEIGHT - 75);
        g.setColor(Constant.GLOW ? Color.GREEN : Color.RED);
        g.drawString("emissive map: " + (Constant.GLOW ? "on" : "off"), 10, Constant.SCREEN_HEIGHT - 55);
        g.setColor(Constant.INTERPOLATION ? Color.GREEN : Color.RED);
        g.drawString("interpolation: " + (Constant.INTERPOLATION ? "perspective" : "affine"), 10, Constant.SCREEN_HEIGHT - 35);
        g.setColor(Color.WHITE);
        g.drawString("fps: " + fpsCounter.count(), 10, Constant.SCREEN_HEIGHT - 15);
        g.drawString("target: " +
                String.format("%5.2f ", scene.camera.target[0]) +
                String.format("%5.2f ", scene.camera.target[1]) +
                String.format("%5.2f", scene.camera.target[2]), Constant.SCREEN_WIDTH - 213, Constant.SCREEN_HEIGHT - 35);
        g.drawString("fov: " + scene.camera.fov, Constant.SCREEN_WIDTH - 85, Constant.SCREEN_HEIGHT - 15);
    }

    private void renderObject(Object3D object, DrawMode drawMode) throws InterruptedException {
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

        List<Callable<Void>> tasks = new ArrayList<>();
        for (PolygonGroup polygonGroup : object.getPolygonGroups()) {
            for (Polygon3D polygon : polygonGroup.getPolygons()) {
                tasks.add(() -> {
                    renderPolygon(polygonGroup, polygon, drawMode, viewProjectionMatrix);
                    return null;
                });
            }
        }
        executor.invokeAll(tasks);
    }

    private void renderPolygon(PolygonGroup polygonGroup, Polygon3D polygon, DrawMode drawMode, double[][] matrix) {
        List<Vertex3D> vectors = polygon.getVertices();

        Vertex3D[] vertices = new Vertex3D[3];
        for (int i = 0; i < 3; i++) {
            vertices[i] = new Vertex3D(
                    Matrix4D.multiplyVector(
                            matrix,
                            vectors.get(i).position
                    ),
                    Matrix4D.multiplyVector(
                            drawMode.transformMatrix,
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
    }

    public synchronized void start() {
        new Thread(this).start();
        running = true;
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
