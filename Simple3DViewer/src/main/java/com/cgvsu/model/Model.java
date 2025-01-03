package com.cgvsu.model;

import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Locale;

public class Model implements Cloneable {
    public ArrayList<Vector3f> vertices = new ArrayList<>();
    public ArrayList<Vector2f> textureVertices = new ArrayList<>();
    public ArrayList<Vector3f> normals = new ArrayList<>();
    public ArrayList<Polygon> polygons = new ArrayList<>();
    public String name;
    private Color color;
    private String texture;

    private boolean verticesVisible = false;
    private boolean polygonMeshEnabled = false;
    private boolean textureEnabled = false;
    private boolean lightingEnabled = false;
    private boolean usingColor = false;
    private boolean usingTexture = false;
    private String fileName;


    public Model() {
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public String toString() {
        return "Model (" +
                fileName +
                ')';
    }

//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }

    // Метод клонирования вершин
    public ArrayList<Vector3f> cloneVertices() {
        ArrayList<Vector3f> clonedVertices = new ArrayList<>();
        for (Vector3f vertex : this.vertices) {
            clonedVertices.add(vertex.clone());
        }
        return clonedVertices;
    }

    // Метод клонирования текстурных вершин
    public ArrayList<Vector2f> cloneTextureVertices() {
        ArrayList<Vector2f> clonedTextureVertices = new ArrayList<>();
        for (Vector2f textureVertex : this.textureVertices) {
            clonedTextureVertices.add(textureVertex.clone());
        }
        return clonedTextureVertices;
    }

    // Метод клонирования нормалей
    public ArrayList<Vector3f> cloneNormals() {
        ArrayList<Vector3f> clonedNormals = new ArrayList<>();
        for (Vector3f normal : this.normals) {
            clonedNormals.add(normal.clone());
        }
        return clonedNormals;
    }

    // Метод клонирования полигонов
    public ArrayList<Polygon> clonePolygons() {
        ArrayList<Polygon> clonedPolygons = new ArrayList<>();
        for (Polygon polygon : this.polygons) {
            clonedPolygons.add(polygon.clone());
        }
        return clonedPolygons;
    }

    public boolean isVerticesVisible() {
        return verticesVisible;
    }

    public void setVerticesVisible(boolean verticesVisible) {
        this.verticesVisible = verticesVisible;
    }

    // Метод clone
    @Override
    public Model clone() {
        Model clonedModel = new Model();
        clonedModel.vertices = this.cloneVertices();
        clonedModel.textureVertices = this.cloneTextureVertices();
        clonedModel.normals = this.cloneNormals();
        clonedModel.polygons = this.clonePolygons();
        return clonedModel;
    }
    public void exportToOBJ() {
        // Устанавливаем локаль для использования точки как разделителя дробной части
        Locale.setDefault(Locale.US);

        // Вывод вершин
        for (Vector3f vertex : vertices) {
            System.out.printf("v %.6f %.6f %.6f%n", vertex.getX(), vertex.getY(), vertex.getZ());
        }

        // Вывод нормалей
        for (Vector3f normal : normals) {
            System.out.printf("vn %.6f %.6f %.6f%n", normal.getX(), normal.getY(), normal.getZ());
        }

        // Вывод текстурных координат
        for (Vector2f textureVertex : textureVertices) {
            System.out.printf("vt %.6f %.6f%n", textureVertex.getX(), textureVertex.getY());
        }

        // Вывод полигонов
        for (Polygon polygon : polygons) {
            System.out.print("f");
            for (int i = 0; i < polygon.getVertexIndices().size(); i++) {
                int vertexIndex = polygon.getVertexIndices().get(i) + 1; // Индексация в OBJ начинается с 1
                String facePart = String.valueOf(vertexIndex);

                if (!polygon.getTextureVertexIndices().isEmpty()) {
                    int textureIndex = polygon.getTextureVertexIndices().get(i) + 1;
                    facePart += "/" + textureIndex;
                }

                if (!polygon.getNormalIndices().isEmpty()) {
                    int normalIndex = polygon.getNormalIndices().get(i) + 1;
                    facePart += (facePart.contains("/") ? "" : "/") + "/" + normalIndex;
                }

                System.out.print(" " + facePart);
            }
            System.out.println();
        }
    }

    public boolean isPolygonMeshEnabled() {
        return polygonMeshEnabled;
    }

    public void setPolygonMeshEnabled(boolean polygonMeshEnabled) {
        this.polygonMeshEnabled = polygonMeshEnabled;
    }

    public boolean isLightingEnabled() {
        return lightingEnabled;
    }

    public boolean isUsingTexture() {
        return usingTexture;
    }

    public void setUsingTexture(boolean usingTexture) {
        this.usingTexture = usingTexture;
    }

    public boolean isUsingColor() {
        return usingColor;
    }

    public void setUsingColor(boolean usingColor) {
        this.usingColor = usingColor;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public void setLightingEnabled(boolean lightingEnabled) {
        this.lightingEnabled = lightingEnabled;
    }
}
