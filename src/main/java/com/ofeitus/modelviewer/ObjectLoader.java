package com.ofeitus.modelviewer;

import com.ofeitus.modelviewer.model.Object3D;
import com.ofeitus.modelviewer.model.Polygon3D;
import com.ofeitus.modelviewer.model.PolygonGroup;
import com.ofeitus.modelviewer.model.Vector3D;

import java.io.*;
import java.util.*;

public class ObjectLoader {
    protected List<Vector3D> vertices;
    protected ObjLineParser parser;
    private Object3D object;
    private PolygonGroup currentGroup;

    /**
     Creates a new ObjectLoader.
     */
    public ObjectLoader() {
        vertices = new ArrayList<>();
        parser = new ObjLineParser();
    }

    /**
     Loads an OBJ file as a PolygonGroup.
     */
    public Object3D loadObject(String filename) throws IOException
    {
        File file = new File(filename);
        object = new Object3D(file.getName());
        vertices.clear();
        currentGroup = null;
        parseFile(filename);
        return object;
    }

    /**
     Gets a Vector3D from the list of vectors in the file.
     Negative indices count from the end of the list, positive
     indices count from the beginning. 1 is the first index,
     -1 is the last. 0 is invalid and throws an exception.
     */
    protected Vector3D getVector(String indexStr) {
        int index = Integer.parseInt(indexStr);
        if (index < 0) {
            index = vertices.size() + index + 1;
        }
        return vertices.get(index-1);
    }

    /**
     Parses an OBJ (ends with ".obj") or MTL file (ends with
     ".mtl").
     */
    protected void parseFile(String filename) throws IOException
    {
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
        public void parseLine(String line) throws NumberFormatException, NoSuchElementException
        {
            StringTokenizer tokenizer = new StringTokenizer(line);
            String command = tokenizer.nextToken();
            switch (command) {
                case "v":
                    // create a new vertex
                    vertices.add(new Vector3D(
                            Double.parseDouble(tokenizer.nextToken()),
                            Double.parseDouble(tokenizer.nextToken()),
                            Double.parseDouble(tokenizer.nextToken())));
                    break;
                case "f":
                    // create a new face (flat, convex polygon)
                    List<Vector3D> currVertices = new ArrayList<>();
                    while (tokenizer.hasMoreTokens()) {
                        String indexStr = tokenizer.nextToken();
                        // ignore texture and normal coords
                        int endIndex = indexStr.indexOf('/');
                        if (endIndex != -1) {
                            indexStr = indexStr.substring(0, endIndex);
                        }
                        currVertices.add(getVector(indexStr));
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
                        currentGroup = new PolygonGroup(name);
                    } else {
                        currentGroup = new PolygonGroup();
                    }
                    object.addPolygonGroup(currentGroup);
                    break;
                case "mtllib":
                    // load materials from file
                    break;
                case "usemtl":
                    // define the current material
                    break;
                default:
                    // unknown command
                    break;
            }
        }
    }
}
