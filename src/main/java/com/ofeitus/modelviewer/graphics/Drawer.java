package com.ofeitus.modelviewer.graphics;

import com.ofeitus.modelviewer.constant.Constant;
import com.ofeitus.modelviewer.model.Camera;
import com.ofeitus.modelviewer.model.Object3D;
import com.ofeitus.modelviewer.model.Vertex3D;
import com.ofeitus.modelviewer.util.Vector4D;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Drawer {
    private static final double EPSILON = 0.000001d;

    private Drawer() {

    }

    private static int sign (int x) {
        return Integer.compare(x, 0);
    }

    public static void drawLineBresenham(BufferedImage image, int xStart, int yStart, int xEnd, int yEnd) {
        int x;
        int y;
        int dx;
        int dy;
        int incx;
        int incy;
        int pdx;
        int pdy;
        int es;
        int el;
        int err;

        dx = xEnd - xStart;
        dy = yEnd - yStart;

        incx = sign(dx);
        incy = sign(dy);

        if (dx < 0) {
            dx = -dx;
        }

        if (dy < 0) {
            dy = -dy;
        }

        if (dx > dy) {
            pdx = incx;
            pdy = 0;
            es = dy;
            el = dx;
        } else {
            pdx = 0;
            pdy = incy;
            es = dx;
            el = dy;
        }

        x = xStart;
        y = yStart;
        err = el/2;
        if (x > 0 && x < Constant.SCREEN_WIDTH && y > 0 && y < Constant.SCREEN_HEIGHT) {
            image.setRGB(x, y, 0xffffff);
        }

        for (int t = 0; t < el; t++) {
            err -= es;
            if (err < 0) {
                err += el;
                x += incx;
                y += incy;
            } else {
                x += pdx;
                y += pdy;
            }
            if (x > 0 && x < Constant.SCREEN_WIDTH && y > 0 && y < Constant.SCREEN_HEIGHT) {
                image.setRGB(x, y, 0xffffff);
            }
        }
    }

    private static double cosBetweenVectors(double[] v1, double[] v2) {
        double l1 = Vector4D.getLength(v1);
        double l2 = Vector4D.getLength(v2);
        return Vector4D.scalarProduct(v1, v2) / l1 / l2;
    }

    private static void viewPort(Vertex3D v) {
        v.position[0] = v.position[0] / v.position[3] * Constant.SCREEN_WIDTH / 2 + (double)Constant.SCREEN_WIDTH / 2;
        v.position[1] = Constant.SCREEN_HEIGHT - (v.position[1] / v.position[3] * Constant.SCREEN_HEIGHT / 2 + (double)Constant.SCREEN_HEIGHT / 2);
    }

    private static void setOneOverZ(Vertex3D v) {
        v.oneOverZ = 1 / v.position[3];
    }

    private static Vertex3D[] sortVerticesByY(Vertex3D v1, Vertex3D v2, Vertex3D v3) {
        Vertex3D[] vertices = new Vertex3D[3];
        if (v1.position[1] > v2.position[1]) {
            if (v1.position[1] > v3.position[1]) {
                vertices[0] = v1;
                vertices[1] = v2;
                vertices[2] = v3;
            }
            else {
                vertices[0] = v3;
                vertices[1] = v1;
                vertices[2] = v2;
            }
        }
        else {
            if (v2.position[1] > v3.position[1]) {
                vertices[0] = v2;
                vertices[1] = v1;
                vertices[2] = v3;
            }
            else {
                vertices[0] = v3;
                vertices[1] = v1;
                vertices[2] = v2;
            }
        }

        if (vertices[1].position[1] < vertices[2].position[1]) {
            Vertex3D t = vertices[1];
            vertices[1] = vertices[2];
            vertices[2] = t;
        }

        return vertices;
    }

    private static double interpolation(double x1, double x2, double t) {
        return x1 + (x2 - x1) * t;
    }

    private static void vectorInterpolation(double[] out, double[] a, double[] b, double t) {
        out[0] = interpolation(a[0], b[0], t);
        out[1] = interpolation(a[1], b[1], t);
        out[2] = interpolation(a[2], b[2], t);
    }

    private static void vertexInterpolation(Vertex3D out, Vertex3D v1, Vertex3D v2, double t) {
        vectorInterpolation(out.position, v1.position, v2.position, t);
        vectorInterpolation(out.texture, v1.texture, v2.texture, t);
        vectorInterpolation(out.normal, v1.normal, v2.normal, t);
        out.oneOverZ = interpolation(v1.oneOverZ, v2.oneOverZ, t);
    }

    private static void applyLambertianLighting(Scene scene, double[] normal, double[] color) {
        Light light = scene.light;

        double[] lightDirection = Vector4D.normalize(light.position);

        double diffuse = Vector4D.scalarProduct(
                Vector4D.normalize(normal),
                lightDirection
        );
        diffuse = light.kd * Math.max(diffuse, 0);

        color[0] *= (diffuse * light.color[0]);
        color[1] *= (diffuse * light.color[1]);
        color[2] *= (diffuse * light.color[2]);
    }

    private static void applyPhongLighting(Scene scene, double[] normal, double[] color) {
        Light light = scene.light;

        double[] lightDirection = Vector4D.normalize(light.position);

        double diffuse = Vector4D.scalarProduct(
                Vector4D.normalize(normal),
                lightDirection
        );
        diffuse = light.kd * Math.max(diffuse, 0);

        double[] eye = Vector4D.normalize(scene.camera.eye);

        double[] specularDirection = Vector4D.normalize(Vector4D.add(lightDirection, eye));

        double specular = Vector4D.scalarProduct(
                Vector4D.normalize(normal),
                specularDirection
        );
        specular = light.ks * Math.pow(Math.max(specular, 0), light.shininess);

        color[0] *= ((light.ka + diffuse + specular) * light.color[0]);
        color[1] *= ((light.ka + diffuse + specular) * light.color[1]);
        color[2] *= ((light.ka + diffuse + specular) * light.color[2]);
    }

    private static void drawHorizontalLine(BufferedImage image, Scene scene, Object3D object, DrawMode drawMode, int x1, int x2, int y, Vertex3D v, Vertex3D step) {
        double z;
        double[] normal = new double[4];
        while (x1 < x2) {
            if (x1 >= 0 && x1 < Constant.SCREEN_WIDTH && y >= 0 && y < Constant.SCREEN_HEIGHT && scene.zBuffer[y][x1] <= v.oneOverZ) {
                z = 1 / v.oneOverZ;
                normal[0] = v.normal[0] * z;
                normal[1] = v.normal[1] * z;
                normal[2] = v.normal[2] * z;
                normal[3] = 0;

                double[] color = new double[] {1, 1, 1, 1};
                if (drawMode.light == 0) {
                    applyLambertianLighting(scene, normal, color);
                } else {
                    applyPhongLighting(scene, normal, color);
                }

                if (object.getTexture() != null && drawMode.texture) {
                    int textureSize = object.getTexture().getHeight();
                    int textureColor = object.getTexture().getRGB((int)(textureSize * v.texture[0]), (int)(textureSize * (1 - v.texture[1])));
                    color[0] *= ((textureColor >> 16) & 255) / 255.0;
                    color[1] *= ((textureColor >> 8) & 255) / 255.0;
                    color[2] *= (textureColor & 255) / 255.0;
                }

                color[0] = Math.min(color[0], 1);
                color[1] = Math.min(color[1], 1);
                color[2] = Math.min(color[2], 1);

                Color rgb = new Color((float)color[0], (float)color[1], (float)color[2]);

                image.setRGB(x1, y, rgb.getRGB());

                scene.zBuffer[y][x1] = v.oneOverZ;
            }

            v.position = Vector4D.add(v.position, step.position);
            v.texture = Vector4D.add(v.texture, step.texture);
            v.normal = Vector4D.add(v.normal, step.normal);
            v.oneOverZ += step.oneOverZ;
            x1++;
        }
    }

    private static void rasterizeBottomTriangle(BufferedImage image, Scene scene, Object3D object, DrawMode drawMode, Vertex3D v1, Vertex3D v2, Vertex3D v3) {
        double top = Math.min(v1.position[1], Constant.SCREEN_HEIGHT);
        double bottom = Math.max(v3.position[1], 0);

        Vertex3D left = new Vertex3D(0, 0, 0);
        Vertex3D right = new Vertex3D(0, 0, 0);

        double height = v1.position[1] - v2.position[1];

        for (int y = (int)top; y >= bottom; y--) {
            double t = (v1.position[1] - y) / height;
            if (t < 0)
                t = 0;
            vertexInterpolation(left, v1, v2, t);
            vertexInterpolation(right, v1, v3, t);

            if (left.position[0] > right.position[0]) {
                Vertex3D tmp = left;
                left = right;
                right = tmp;
            }

            int x1 = (int)(left.position[0] + 0.5);
            int x2 = (int)(right.position[0] + 0.5);
            double oneOverWidth = 1 / (right.position[0] - left.position[0]);

            Vertex3D step = new Vertex3D(
                    Vector4D.multiplyByScalar(Vector4D.sub(right.position, left.position), oneOverWidth),
                    Vector4D.multiplyByScalar(Vector4D.sub(right.texture, left.texture), oneOverWidth),
                    Vector4D.multiplyByScalar(Vector4D.sub(right.normal, left.normal), oneOverWidth)
            );
            step.oneOverZ = (right.oneOverZ - left.oneOverZ) * oneOverWidth;

            if (drawMode.line && x1 == x2) {
                x2++;
            }

            drawHorizontalLine(image, scene, object, drawMode, x1, x2, y, left, step);
        }
    }

    private static void rasterizeTopTriangle(BufferedImage image, Scene scene, Object3D object, DrawMode drawMode, Vertex3D v1, Vertex3D v2, Vertex3D v3) {
        double top = Math.min(v2.position[1], Constant.SCREEN_HEIGHT);
        double bottom = Math.max(v3.position[1], 0);

        Vertex3D left = new Vertex3D(0, 0, 0);
        Vertex3D right = new Vertex3D(0, 0, 0);

        double height = v2.position[1] - v3.position[1];

        for (int y = (int)bottom; y < top; y++) {
            double t = (y - v3.position[1]) / height;
            if (t < 0)
                t = 0;
            vertexInterpolation(left, v3, v1, t);
            vertexInterpolation(right, v3, v2, t);

            if (left.position[0] > right.position[0]) {
                Vertex3D tmp = left;
                left = right;
                right = tmp;
            }

            int x1 = (int)(left.position[0] + 0.5);
            int x2 = (int)(right.position[0] + 0.5);
            double oneOverWidth = 1 / (right.position[0] - left.position[0]);

            Vertex3D step = new Vertex3D(
                    Vector4D.multiplyByScalar(Vector4D.sub(right.position, left.position), oneOverWidth),
                    Vector4D.multiplyByScalar(Vector4D.sub(right.texture, left.texture), oneOverWidth),
                    Vector4D.multiplyByScalar(Vector4D.sub(right.normal, left.normal), oneOverWidth)
            );
            step.oneOverZ = (right.oneOverZ - left.oneOverZ) * oneOverWidth;

            if (drawMode.line && x1 == x2) {
                x2++;
            }

            drawHorizontalLine(image, scene, object, drawMode, x1, x2, y, left, step);
        }
    }

    public static void drawTriangle(BufferedImage image, Scene scene, Object3D object, DrawMode drawMode, double[] center, double[] normal, Vertex3D v1, Vertex3D v2, Vertex3D v3) {
        if (drawMode.faceCulling && cosBetweenVectors(Vector4D.sub(scene.camera.eye, center), normal) < 0) {
            return;
        }

        setOneOverZ(v1);
        setOneOverZ(v2);
        setOneOverZ(v3);

        viewPort(v1);
        viewPort(v2);
        viewPort(v3);

        if (!drawMode.wireframe) {
            Vertex3D[] vertices = sortVerticesByY(v1, v2, v3);
            Vertex3D top = vertices[0];
            Vertex3D middle = vertices[1];
            Vertex3D bottom = vertices[2];

            if (Math.abs(middle.position[1] - bottom.position[1]) < EPSILON) {
                rasterizeBottomTriangle(image, scene, object, drawMode, top, middle, bottom);
            }
            else if (Math.abs(middle.position[1] - top.position[1]) < EPSILON) {
                rasterizeTopTriangle(image, scene, object, drawMode, top, middle, bottom);
            }
            else {
                Vertex3D v4 = new Vertex3D(0, 0, 0);
                vertexInterpolation(v4, top, bottom, (top.position[1] - middle.position[1]) / (top.position[1] - bottom.position[1]));
                rasterizeBottomTriangle(image, scene, object, drawMode, top, middle, v4);
                rasterizeTopTriangle(image, scene, object, drawMode, middle, v4, bottom);
            }
        } else {
            drawLineBresenham(image, (int)v1.position[0], (int)v1.position[1], (int)v2.position[0], (int)v2.position[1]);
            drawLineBresenham(image, (int)v2.position[0], (int)v2.position[1], (int)v3.position[0], (int)v3.position[1]);
            drawLineBresenham(image, (int)v3.position[0], (int)v3.position[1], (int)v1.position[0], (int)v1.position[1]);
        }
    }

    public static void drawPoint(Graphics g, Camera camera, double[] center, double[] normal, Vertex3D point) {
        if (cosBetweenVectors(Vector4D.sub(camera.eye, center), normal) < 0) {
            return;
        }

        viewPort(point);
        g.drawOval((int)point.position[0], (int)point.position[1], 3, 3);
    }
}
