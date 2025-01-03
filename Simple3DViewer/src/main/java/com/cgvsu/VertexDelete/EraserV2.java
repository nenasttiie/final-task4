package com.cgvsu.VertexDelete;

import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;

import java.util.*;

public class EraserV2 {
    public static Model vertexDelete(Model model, List<Integer> index, boolean new_file, boolean hanging_NormalIndices, boolean hanging_TexturelIndices, boolean hanging_polygons) {
        Model modelrez = new Model();
        Map<Integer, Integer> connectionVertexIndices = new HashMap<>();
        Map<Integer, Integer> connectionNormalIndices = new HashMap<>();
        Map<Integer, Integer> connectionTextureVertexIndices = new HashMap<>();
        Set<Integer> deletenormals = new HashSet<>();
        Set<Integer> deletetextureVertices = new HashSet<>();

        // Обработка полигонов
        List<Polygon> polygons = processPolygons(
                model, modelrez, index, connectionVertexIndices,
                connectionNormalIndices, connectionTextureVertexIndices,
                deletenormals, deletetextureVertices, new_file, hanging_NormalIndices,
                hanging_TexturelIndices, hanging_polygons
        );

        modelrez.polygons = (ArrayList<Polygon>) polygons;

        // Обработка висячих текстур и нормалей
        modelrez.textureVertices = (ArrayList<Vector2f>) processHangingTextures(model, connectionTextureVertexIndices, deletetextureVertices, new_file, hanging_TexturelIndices);
        modelrez.normals = (ArrayList<Vector3f>) processHangingNormals(model, connectionNormalIndices, deletenormals, new_file, hanging_NormalIndices);

        if (new_file) return modelrez;
        else return model = modelrez;
    }

    private static List<Polygon> processPolygons(
            Model model, Model modelrez, List<Integer> index,
            Map<Integer, Integer> connectionVertexIndices,
            Map<Integer, Integer> connectionNormalIndices,
            Map<Integer, Integer> connectionTextureVertexIndices,
            Set<Integer> deletenormals, Set<Integer> deletetextureVertices,
            boolean new_file, boolean hanging_NormalIndices,
            boolean hanging_TexturelIndices, boolean hanging_polygons
    ) {
        List<Polygon> polygons = new ArrayList<>();

        for (Polygon polygon : model.polygons) {
            Polygon processedPolygon = processSinglePolygon(
                    polygon, model, modelrez, index, connectionVertexIndices,
                    connectionNormalIndices, connectionTextureVertexIndices,
                    deletenormals, deletetextureVertices, new_file,
                    hanging_NormalIndices, hanging_TexturelIndices
            );
            if (processedPolygon != null && (hanging_polygons || processedPolygon.getVertexIndices().size() >= 3)) {
                polygons.add(processedPolygon);
            }
        }

        return polygons;
    }

    private static Polygon processSinglePolygon(
            Polygon polygon, Model model, Model modelrez, List<Integer> index,
            Map<Integer, Integer> connectionVertexIndices,
            Map<Integer, Integer> connectionNormalIndices,
            Map<Integer, Integer> connectionTextureVertexIndices,
            Set<Integer> deletenormals, Set<Integer> deletetextureVertices,
            boolean new_file, boolean hanging_NormalIndices,
            boolean hanging_TexturelIndices
    ) {
        Polygon polygonrez = new Polygon();

        ArrayList<Integer> vertexIndices = new ArrayList<>();
        ArrayList<Integer> textureVertexIndices = new ArrayList<>();
        ArrayList<Integer> normalIndices = new ArrayList<>();

        int remainingVertices = 0;
        for (int vertexIndex : polygon.getVertexIndices()) {
            if (!index.contains(vertexIndex)) remainingVertices++;
        }

        if (remainingVertices < 3) return null;

        for (int j = 0; j < polygon.getVertexIndices().size(); j++) {
            int vertexIndex = polygon.getVertexIndices().get(j);

            if (index.contains(vertexIndex)) {
                deletenormals.add(polygon.getNormalIndices().get(j));
                deletetextureVertices.add(polygon.getTextureVertexIndices().get(j));
            } else {
                addVertexIndex(model, modelrez, connectionVertexIndices, vertexIndex, vertexIndices, new_file);
                addTextureIndex(model, modelrez, connectionTextureVertexIndices, polygon, j, textureVertexIndices, new_file, hanging_TexturelIndices);
                addNormalIndex(model, modelrez, connectionNormalIndices, polygon, j, normalIndices, new_file, hanging_NormalIndices);
            }
        }

        polygonrez.setVertexIndices(vertexIndices);
        polygonrez.setTextureVertexIndices(textureVertexIndices);
        polygonrez.setNormalIndices(normalIndices);

        return polygonrez;
    }

    private static void addVertexIndex(Model model, Model modelrez, Map<Integer, Integer> connectionMap, int vertexIndex, List<Integer> indices, boolean new_file) {
        if (!connectionMap.containsKey(vertexIndex)) {
            indices.add(modelrez.vertices.size());
            connectionMap.put(vertexIndex, modelrez.vertices.size());
            modelrez.vertices.add(new_file ? model.vertices.get(vertexIndex).clone() : model.vertices.get(vertexIndex));
        } else {
            indices.add(connectionMap.get(vertexIndex));
        }
    }

    private static void addTextureIndex(Model model, Model modelrez, Map<Integer, Integer> connectionMap, Polygon polygon, int j, List<Integer> indices, boolean new_file, boolean hanging) {
        if (hanging || j >= polygon.getTextureVertexIndices().size()) return;

        int textureIndex = polygon.getTextureVertexIndices().get(j);
        if (!connectionMap.containsKey(textureIndex)) {
            indices.add(modelrez.textureVertices.size());
            connectionMap.put(textureIndex, modelrez.textureVertices.size());
            modelrez.textureVertices.add(new_file ? model.textureVertices.get(textureIndex).clone() : model.textureVertices.get(textureIndex));
        } else {
            indices.add(connectionMap.get(textureIndex));
        }
    }

    private static void addNormalIndex(Model model, Model modelrez, Map<Integer, Integer> connectionMap, Polygon polygon, int j, List<Integer> indices, boolean new_file, boolean hanging) {
        if (hanging || j >= polygon.getNormalIndices().size()) return;

        int normalIndex = polygon.getNormalIndices().get(j);
        if (!connectionMap.containsKey(normalIndex)) {
            indices.add(modelrez.normals.size());
            connectionMap.put(normalIndex, modelrez.normals.size());
            modelrez.normals.add(new_file ? model.normals.get(normalIndex).clone() : model.normals.get(normalIndex));
        } else {
            indices.add(connectionMap.get(normalIndex));
        }
    }

    private static List<Vector3f> processHangingNormals(Model model, Map<Integer, Integer> connectionMap, Set<Integer> deletedNormals, boolean new_file, boolean hanging) {
        if (hanging) return new_file ? model.cloneNormals() : model.normals;

        List<Vector3f> updatedNormals = new ArrayList<>();
        for (int i = 0; i < model.normals.size(); i++) {
            if (!connectionMap.containsKey(i) && !deletedNormals.contains(i)) {
                updatedNormals.add(model.normals.get(i));
            }
        }
        return updatedNormals;
    }

    private static List<Vector2f> processHangingTextures(Model model, Map<Integer, Integer> connectionMap, Set<Integer> deletedTextures, boolean new_file, boolean hanging) {
        if (hanging) return new_file ? model.cloneTextureVertices() : model.textureVertices;

        List<Vector2f> updatedTextures = new ArrayList<>();
        for (int i = 0; i < model.textureVertices.size(); i++) {
            if (!connectionMap.containsKey(i) && !deletedTextures.contains(i)) {
                updatedTextures.add(model.textureVertices.get(i));
            }
        }
        return updatedTextures;
    }

}
