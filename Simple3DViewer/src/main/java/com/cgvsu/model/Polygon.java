package com.cgvsu.model;

import java.util.ArrayList;

public class Polygon implements Cloneable {

    private ArrayList<Integer> vertexIndices;           // Индексы вершин полигона
    private ArrayList<Integer> textureVertexIndices;    // Индексы текстурных координат полигона
    private ArrayList<Integer> normalIndices;           // Индексы нормалей полигона

    // Конструктор по умолчанию
    public Polygon() {
        vertexIndices = new ArrayList<>();
        textureVertexIndices = new ArrayList<>();
        normalIndices = new ArrayList<>();
    }

    // Метод clone
    @Override
    public Polygon clone() {
        try {
            Polygon clonedPolygon = (Polygon) super.clone();

            // Глубокое копирование списков
            clonedPolygon.vertexIndices = new ArrayList<>(this.vertexIndices);
            clonedPolygon.textureVertexIndices = new ArrayList<>(this.textureVertexIndices);
            clonedPolygon.normalIndices = new ArrayList<>(this.normalIndices);

            return clonedPolygon;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Этот код никогда не должен выполняться
        }
    }

    // Сеттеры и геттеры (остаются неизменными)
    public void setVertexIndices(ArrayList<Integer> vertexIndices) {

        this.vertexIndices = vertexIndices;
    }

    public void setTextureVertexIndices(ArrayList<Integer> textureVertexIndices) {

        this.textureVertexIndices = textureVertexIndices;
    }

    public void setNormalIndices(ArrayList<Integer> normalIndices) {

        this.normalIndices = normalIndices;
    }

    public ArrayList<Integer> getVertexIndices() {
        return vertexIndices;
    }

    public ArrayList<Integer> getTextureVertexIndices() {
        return textureVertexIndices;
    }

    public ArrayList<Integer> getNormalIndices() {
        return normalIndices;
    }
}
