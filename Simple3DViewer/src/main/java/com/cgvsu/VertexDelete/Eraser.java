package com.cgvsu.VertexDelete;

import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;

import java.util.*;

public class Eraser {
    public static Model vertexDelete(Model model, List<Integer> index, boolean new_file, boolean hanging_NormalIndices, boolean hanging_TexturelIndices, boolean hanging_polygons) {
        Model modelrez = new Model();
        ArrayList<Polygon> polygons = new ArrayList<>();


        Map<Integer, Integer> connectionVertexIndices = new HashMap<>();
        Map<Integer, Integer> connectionNormalIndices = new HashMap<>();
        Map<Integer, Integer> connectiontextureVertexIndices = new HashMap<>();

        Set<Integer> deletenormals = new HashSet<Integer>();
        Set<Integer> deletetextureVertices = new HashSet<Integer>();

        for (int i = 0; i < model.polygons.size(); i++) {
            Polygon polygon = model.polygons.get(i);
            Polygon polygonrez = new Polygon();

            List<Integer> textureVertexIndices = new ArrayList<>(polygon.getTextureVertexIndices().size());
            List<Integer> normalIndices = new ArrayList<>(polygon.getNormalIndices().size());
            List<Integer> vertexIndices = new ArrayList<>(polygon.getVertexIndices().size());


            int k = 0;
            for (int j = 0; j < polygon.getVertexIndices().size(); j++) {
                if (!index.contains(polygon.getVertexIndices().get(j))) k++;
            }
            if (k < 3) continue;


            for (int j = 0; j < polygon.getVertexIndices().size(); j++) {

                if (index.contains(polygon.getVertexIndices().get(j))) {
                    deletenormals.add(polygon.getNormalIndices().get(j));
                    deletetextureVertices.add(polygon.getTextureVertexIndices().get(j));

                } else {

                    if (!connectionVertexIndices.containsKey(polygon.getVertexIndices().get(j))) {
                        vertexIndices.add(modelrez.vertices.size());
                        connectionVertexIndices.put(polygon.getVertexIndices().get(j), modelrez.vertices.size());
                        modelrez.vertices.add(new_file ? model.vertices.get(polygon.getVertexIndices().get(j)).clone() : model.vertices.get(polygon.getVertexIndices().get(j)));
                    } else {
                        vertexIndices.add(connectionVertexIndices.get(polygon.getVertexIndices().get(j)));
                    }

                    if (!hanging_TexturelIndices) {
                        if (j < polygon.getTextureVertexIndices().size() && !connectiontextureVertexIndices.containsKey(polygon.getTextureVertexIndices().get(j))) {
                            textureVertexIndices.add(modelrez.textureVertices.size());
                            connectiontextureVertexIndices.put(polygon.getTextureVertexIndices().get(j), modelrez.textureVertices.size());
                            modelrez.textureVertices.add(new_file ? model.textureVertices.get(polygon.getTextureVertexIndices().get(j)).clone() : model.textureVertices.get(polygon.getTextureVertexIndices().get(j)));
                        } else {
                            textureVertexIndices.add(connectiontextureVertexIndices.get(polygon.getTextureVertexIndices().get(j)));
                        }
                    }

                    if (!hanging_NormalIndices) {
                        if (j < polygon.getNormalIndices().size() && !connectionNormalIndices.containsKey(polygon.getNormalIndices().get(j))) {
                            normalIndices.add(modelrez.normals.size());
                            connectionNormalIndices.put(polygon.getNormalIndices().get(j), modelrez.normals.size());
                            modelrez.normals.add(new_file ? model.normals.get(polygon.getNormalIndices().get(j)).clone() : model.normals.get(polygon.getNormalIndices().get(j)));
                        } else {
                            normalIndices.add(connectionNormalIndices.get(polygon.getNormalIndices().get(j)));
                        }
                    }

                }
            }

            if (hanging_polygons || k == polygon.getVertexIndices().size()) {
                polygonrez.setNormalIndices(hanging_NormalIndices ? polygon.getNormalIndices() : new ArrayList<>(normalIndices));
                polygonrez.setTextureVertexIndices(hanging_TexturelIndices ? polygon.getTextureVertexIndices() : new ArrayList<>(textureVertexIndices));
                polygonrez.setVertexIndices(new ArrayList<>(vertexIndices));
                polygons.add(polygonrez);
            }

        }

        if (hanging_TexturelIndices)
            modelrez.textureVertices = new_file ? model.cloneTextureVertices() : model.textureVertices;
        else {
            for (int i = 0; i < model.textureVertices.size(); i++) {
                if (!connectiontextureVertexIndices.containsKey(i) && !deletetextureVertices.contains(i))
                    modelrez.textureVertices.add(model.textureVertices.get(i));
            }
        }

        if (hanging_NormalIndices)
            modelrez.normals = new_file ? model.cloneNormals() : model.normals;
        else {
            for (int i = 0; i < model.normals.size(); i++) {
                if (!connectionNormalIndices.containsKey(i) && !deletenormals.contains(i))
                    modelrez.normals.add(model.normals.get(i));
            }
        }
        modelrez.polygons = polygons;

        if (new_file) {
            return modelrez;
        }


        model.vertices = new ArrayList<>(modelrez.vertices);
        model.normals = new ArrayList<>(modelrez.normals);
        model.textureVertices = new ArrayList<>(modelrez.textureVertices);
        model.polygons = new ArrayList<>(modelrez.polygons);

        return model; // Возврат уже модифицированного объекта

    }
}
