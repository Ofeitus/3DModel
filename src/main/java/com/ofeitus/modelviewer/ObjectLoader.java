package com.ofeitus.modelviewer;

import com.ofeitus.modelviewer.model.Object3D;
import com.ofeitus.modelviewer.model.Polygon3D;
import com.ofeitus.modelviewer.model.PolygonGroup;
import com.ofeitus.modelviewer.model.Vertex3D;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.*;

public class ObjectLoader {
    protected List<double[]> vertices;
    protected List<double[]> textures;
    protected List<double[]> normals;
    protected ObjLineParser parser;
    protected File root;
    private Object3D object;
    private PolygonGroup currentGroup;

    public ObjectLoader() {
        vertices = new ArrayList<>();
        textures = new ArrayList<>();
        normals = new ArrayList<>();
        parser = new ObjLineParser();
    }

    public Object3D loadObject(String model) throws IOException {
        File modelFile = new File(model);
        root = modelFile.getParentFile();
        object = new Object3D(modelFile.getName());
        vertices.clear();
        textures.clear();
        normals.clear();
        currentGroup = new PolygonGroup("Default group", null, null, null);
        object.addPolygonGroup(currentGroup);
        parseFile(model);
        return object;
    }

    protected double[] getVector(String indexStr) {
        int index = Integer.parseInt(indexStr);
        if (index < 0) {
            index = vertices.size() + index + 1;
        }
        return vertices.get(index-1);
    }

    protected double[] getTexture(String indexStr) {
        int index = Integer.parseInt(indexStr);
        if (index < 0) {
            index = textures.size() + index + 1;
        }
        return textures.get(index-1);
    }

    protected double[] getNormal(String indexStr) {
        int index = Integer.parseInt(indexStr);
        if (index < 0) {
            index = normals.size() + index + 1;
        }
        return normals.get(index-1);
    }

    protected void parseFile(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            // parse every line in the file
            String line = reader.readLine();
            while (line != null) {
                line = line.trim();
                // ignore blank lines and comments
                if (line.length() > 0 && !line.startsWith("#")) {
                    // interpret the line
                    parser.parseLine(line);
                }
                line = reader.readLine();
            }
        } catch (NumberFormatException | NoSuchElementException ex) {
            throw new IOException(ex.getMessage());
        }
    }

    protected class ObjLineParser {
        public void parseLine(String line) throws NumberFormatException, NoSuchElementException, IOException {
            StringTokenizer tokenizer = new StringTokenizer(line);
            String command = tokenizer.nextToken();
            switch (command) {
                case "v":
                    // create a new vertex
                    vertices.add(new double[]{
                            Double.parseDouble(tokenizer.nextToken()),
                            Double.parseDouble(tokenizer.nextToken()),
                            Double.parseDouble(tokenizer.nextToken()),
                            1.0});
                    break;
                case "vt":
                    // create a new vertex
                    textures.add(new double[]{
                            Double.parseDouble(tokenizer.nextToken()),
                            Double.parseDouble(tokenizer.nextToken()),
                            0.0,
                            0.0});
                    break;
                case "vn":
                    // create a new vertex
                    normals.add(new double[]{
                            Double.parseDouble(tokenizer.nextToken()),
                            Double.parseDouble(tokenizer.nextToken()),
                            Double.parseDouble(tokenizer.nextToken()),
                            0.0});
                    break;
                case "f":
                    // create a new face (flat, convex polygon)
                    List<Vertex3D> currVertices = new ArrayList<>();
                    while (tokenizer.hasMoreTokens()) {
                        String indexStr = tokenizer.nextToken();
                        // ignore texture and normal coords
                        String[] indices = indexStr.split("/");
                        Vertex3D vertex;
                        if (indices.length == 2) {
                            vertex = new Vertex3D(
                                    getVector(indices[0]),
                                    getVector(indices[0]),
                                    getTexture(indices[1]),
                                    getVector(indices[0])
                            );
                        } else {
                            vertex = new Vertex3D(
                                    getVector(indices[0]),
                                    getVector(indices[0]),
                                    getTexture(indices[1]),
                                    getNormal(indices[2])
                            );
                        }
                        currVertices.add(vertex);
                    }
                    // create textured polygon
                    Polygon3D poly = new Polygon3D(currVertices);
                    // add the polygon to the current group
                    currentGroup.addPolygon(poly);
                    break;
                case "g":
                    // define the current group
                    if (tokenizer.hasMoreTokens()) {
                        String name = tokenizer.nextToken();
                        currentGroup = new PolygonGroup(name, null, null, null);
                    } else {
                        currentGroup = new PolygonGroup("", null, null, null);
                    }
                    object.addPolygonGroup(currentGroup);
                    break;
                case "usemtl":
                    String materialName = tokenizer.nextToken();
                    File[] textureFiles = root.listFiles((dir, name) -> name.startsWith(materialName + "_albedo"));
                    if (textureFiles != null && textureFiles.length > 0)
                        currentGroup.setTexture(ImageIO.read(textureFiles[0]));
                    File[] normalFiles = root.listFiles((dir, name) -> name.startsWith(materialName + "_normal"));
                    if (normalFiles != null && normalFiles.length > 0)
                        currentGroup.setNormalMap(ImageIO.read(normalFiles[0]));
                    File[] reflectionFiles = root.listFiles((dir, name) -> name.startsWith(materialName + "_reflection"));
                    if (reflectionFiles != null && reflectionFiles.length > 0)
                        currentGroup.setReflectionMap(ImageIO.read(reflectionFiles[0]));
                    break;
                default:
                    // unknown command
                    break;
            }
        }
    }
}
