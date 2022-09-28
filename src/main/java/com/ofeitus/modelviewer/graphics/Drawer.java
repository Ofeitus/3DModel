package com.ofeitus.modelviewer.graphics;

import com.ofeitus.modelviewer.constant.Constant;
import com.ofeitus.modelviewer.model.*;
import com.ofeitus.modelviewer.util.Matrix4D;
import com.ofeitus.modelviewer.util.Vector4D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Drawer {
    private static final double EPSILON = 0.000001d;

    private Drawer() {

    }

    private static int sign (int x) {
        return Integer.compare(x, 0);
    }

    public static void drawLineBresenham(Scene scene, int xStart, int yStart, int xEnd, int yEnd, int color) {
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
            scene.image.setRGB(x, y, color);
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
                scene.image.setRGB(x, y, color);
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
        return (1 - t) * x1 + t * x2;
    }

    private static double perspectiveInterpolation(double x1, double x1z, double x2, double x2z, double t) {
        return ((1 - t) * x1 / x1z + t * x2 / x2z) / ((1 - t) / x1z + t / x2z);
    }

    private static void vectorInterpolation(double[] out, double[] a, double[] b, double t) {
        out[0] = interpolation(a[0], b[0], t);
        out[1] = interpolation(a[1], b[1], t);
        out[2] = interpolation(a[2], b[2], t);
    }

    private static void interpolateTexture(double[] out, double[] a, double az, double[] b, double bz, double t) {
        out[0] = perspectiveInterpolation(a[0], az, b[0], bz, t);
        out[1] = perspectiveInterpolation(a[1], az, b[1], bz, t);
    }

    private static void vertexInterpolation(Vertex3D out, Vertex3D v1, Vertex3D v2, double t) {
        vectorInterpolation(out.position, v1.position, v2.position, t);
        vectorInterpolation(out.texture, v1.texture, v2.texture, t);
        if (Constant.INTERPOLATION) {
            interpolateTexture(out.texture, v1.texture, v1.position[2], v2.texture, v2.position[2], t);
        }
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

    private static void applyPhongLighting(Scene scene, double[] normal, double[] color, double reflection) {
        Light light = scene.light;

        double[] lightDirection = Vector4D.normalize(light.position);

        double diffuse = Vector4D.scalarProduct(
                Vector4D.normalize(normal),
                lightDirection
        );
        diffuse = light.kd * Math.max(diffuse, 0);

        double[] eye = Vector4D.normalize(scene.camera.eye);

        // Модель Блина-Фонга
        //double[] specularDirection = Vector4D.normalize(Vector4D.add(lightDirection, eye));
        //double specular = Vector4D.scalarProduct(
        //        Vector4D.normalize(normal),
        //        specularDirection
        //`);

        double[] lightReflection = Vector4D.sub(
                lightDirection,
                Vector4D.multiplyByScalar(
                        Vector4D.crossProduct(
                                Vector4D.crossProduct(
                                        Vector4D.invert(lightDirection),
                                        Vector4D.normalize(normal)
                                ),
                                Vector4D.normalize(normal)
                        ),
                        2
                )
        );

        double specular = Vector4D.scalarProduct(
                lightReflection,
                eye
        );

        specular = (reflection) * Math.pow(Math.max(specular, 0), light.shininess);

        color[0] *= ((light.ka + diffuse + specular) * light.color[0]);
        color[1] *= ((light.ka + diffuse + specular) * light.color[1]);
        color[2] *= ((light.ka + diffuse + specular) * light.color[2]);
    }

    private static int getTextureValue(BufferedImage texture, double[] textureCoordinates) {
        int textureSize = texture.getHeight();
        int xCoordinate = (int)(textureSize * textureCoordinates[0]);
        int yCoordinate = (int)(textureSize * (1 - textureCoordinates[1]));
        return texture.getRGB(xCoordinate % textureSize, yCoordinate % textureSize);
    }

    private static void drawHorizontalLine(Scene scene, PolygonGroup polygonGroup, DrawMode drawMode, int x1, int x2, int y, Vertex3D left, Vertex3D right) {
        int width = x2 - x1;
        while (x1 < x2) {
            double t = (double) (x2 - x1) / width;
            Vertex3D v = new Vertex3D(0, 0, 0);
            vertexInterpolation(v, left, right, 1 - t);
            if (x1 >= 0 && x1 < Constant.SCREEN_WIDTH && y >= 0 && y < Constant.SCREEN_HEIGHT && scene.zBuffer[y][x1] <= v.oneOverZ) {
                double[] color = new double[]{1, 1, 1, 1};
                // Base color
                if (polygonGroup.getTexture() != null && drawMode.useTexture) {
                    int textureColor = getTextureValue(polygonGroup.getTexture(), v.texture);
                    color[0] *= ((textureColor >> 16) & 255) / 255.0;
                    color[1] *= ((textureColor >> 8) & 255) / 255.0;
                    color[2] *= (textureColor & 255) / 255.0;
                }

                double[] normal;
                // Normal map
                if (polygonGroup.getNormalMap() != null && drawMode.useNormalMap) {
                    int normalValue = getTextureValue(polygonGroup.getNormalMap(), v.texture);
                    normal = Matrix4D.multiplyVector(
                            drawMode.rotationMatrix,
                            new double[]{
                                    (((normalValue >> 16) & 255) / 255.0) * 2 - 1,
                                    (((normalValue >> 8) & 255) / 255.0) * 2 - 1,
                                    ((normalValue & 255) / 255.0) * 2 - 1,
                                    1
                            }
                    );
                } else {
                    normal = v.normal;
                }

                double reflection = scene.light.ks;
                // Reflection map
                if (polygonGroup.getReflectionMap() != null && drawMode.useReflectionMap) {
                    int reflectionValue = getTextureValue(polygonGroup.getReflectionMap(), v.texture);
                    reflection = (reflectionValue & 255) / 255.0 * 5;
                }

                // Lighting
                if (drawMode.light == 0) {
                    applyLambertianLighting(scene, normal, color);
                } else {
                    applyPhongLighting(scene, normal, color, reflection);
                }

                color[0] = Math.min(color[0], 1);
                color[1] = Math.min(color[1], 1);
                color[2] = Math.min(color[2], 1);

                Color rgb = new Color((float)color[0], (float)color[1], (float)color[2]);

                scene.image.setRGB(x1, y, rgb.getRGB());
                scene.zBuffer[y][x1] = v.oneOverZ;
            }

            x1++;
        }
    }

    private static void rasterizeBottomTriangle(Scene scene, PolygonGroup polygonGroup, DrawMode drawMode, Vertex3D v1, Vertex3D v2, Vertex3D v3, double[] center) {
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

            if (drawMode.line && x1 == x2) {
                x2++;
            }

            drawHorizontalLine(scene, polygonGroup, drawMode, x1, x2, y, left, right);
        }
    }

    private static void rasterizeTopTriangle(Scene scene, PolygonGroup polygonGroup, DrawMode drawMode, Vertex3D v1, Vertex3D v2, Vertex3D v3, double[] center) {
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

            if (drawMode.line && x1 == x2) {
                x2++;
            }

            drawHorizontalLine(scene, polygonGroup, drawMode, x1, x2, y, left, right);
        }
    }

    public static void drawTriangle(Scene scene, PolygonGroup polygonGroup, Polygon3D polygon, DrawMode drawMode, Vertex3D v1, Vertex3D v2, Vertex3D v3) {
        double[] center = Matrix4D.multiplyVector(drawMode.transformMatrix, polygon.getCenter());
        double[] normal = Matrix4D.multiplyVector(drawMode.transformMatrix, polygon.getNormal());
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
                rasterizeBottomTriangle(scene, polygonGroup, drawMode, top, middle, bottom, center);
            }
            else if (Math.abs(middle.position[1] - top.position[1]) < EPSILON) {
                rasterizeTopTriangle(scene, polygonGroup, drawMode, top, middle, bottom, center);
            }
            else {
                Vertex3D v4 = new Vertex3D(0, 0, 0);
                vertexInterpolation(v4, top, bottom, (top.position[1] - middle.position[1]) / (top.position[1] - bottom.position[1]));
                rasterizeBottomTriangle(scene, polygonGroup, drawMode, top, middle, v4, center);
                rasterizeTopTriangle(scene, polygonGroup, drawMode, middle, v4, bottom, center);
            }
        } else {
            drawLineBresenham(scene, (int)v1.position[0], (int)v1.position[1], (int)v2.position[0], (int)v2.position[1], 0xffffff);
            drawLineBresenham(scene, (int)v2.position[0], (int)v2.position[1], (int)v3.position[0], (int)v3.position[1], 0xffffff);
            drawLineBresenham(scene, (int)v3.position[0], (int)v3.position[1], (int)v1.position[0], (int)v1.position[1], 0xffffff);
        }
    }

    public static void drawPoint(Scene scene, int x, int y, int color) {
        Graphics g = scene.image.getGraphics();
        g.setColor(new Color(color));
        g.drawOval(x, y, 4, 4);
    }
}
