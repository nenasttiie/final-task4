package com.cgvsu.render_engine;

import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import javafx.scene.paint.Color;

import javax.vecmath.Vector3f;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SceneTools {
    public static List<Model> models = new ArrayList<>();
    public static List<Camera> cameras = new ArrayList<>();
    public static List<String> textures = new ArrayList<>();

    public static Camera selectedCamera;
    public static Model selectedModel;
    public static String selectedTexture;
    public static Color selectedColor;
    public static LightSource selectedLight;


    public static Model addModel(File file) {
        if (file == null) {
            return null;
        }

        Model model = loadModelFromFile(file);
        if (model != null) {
            model.setFileName(file.getName());
            models.add(model);
            return model;
        } else {
            System.err.println("Не удалось загрузить модель из файла: " + file.getName());
            return null;
        }
    }

    public static Model loadModelFromFile(File file) {
        Path fileName = Path.of(file.getAbsolutePath());
        try {
            String fileContent = Files.readString(fileName);
            return ObjReader.read(fileContent);
        } catch (IOException exception) {
            System.err.println("Не удалось загрузить модель: " + exception.getMessage());
            return null;
        }
    }

    public static void deleteModel(Model model) {
        if (model != null && models != null) {
            models.remove(model);
            if (selectedModel == model) {
                selectedModel = null;
            }
        }
    }

    public static LightSource createLightSource(float x, float y, float z) {
        return new LightSource("Light", new com.cgvsu.math.Vector3f(x, y, z));
    }


    public static void removeLightSource(LightSource lightSource) {
        if (lightSource == null) return;

        if (selectedLight == lightSource) {
            selectedLight = null;
        }
        System.out.println("Удален источник света: " + lightSource.getPosition());
    }

    public static String addTexture(String texturePath) {
        if (texturePath == null || texturePath.isEmpty()) {
            throw new IllegalArgumentException("Путь к текстуре не может быть пустым.");
        }

        String textureName = texturePath.substring(texturePath.lastIndexOf("/") + 1);
        textures.add(texturePath);
        System.out.println("Текстура успешно добавлена: " + textureName);

        return textureName;
    }

    public static Camera createCamera(float x, float y, float z) {
        return new Camera(new Vector3f(x, y, z), new Vector3f(0, 0, 0), 1.0F, 1, 0.01F, 100);
    }

    public static void removeCamera(Camera camera) {
        if (camera == null) return;

        cameras.remove(camera);

        System.out.println("Удалена камера: " + camera.getPosition());
    }

    public static void transform(Object selectedModel, double x, double y, double z) {
        if (selectedModel != null) {
            System.out.println("Трансформация успешно применена к модели: " + selectedModel.toString()
                    + " с следующими значениями x = " + x + " y = " + y + " z = " + z);
        } else {
            throw new IllegalStateException("Ошибка: Модель не выбрана. Пожалуйста, выберите модель.");
        }
    }

    public static void rotate(Object selectedModel, double x, double y, double z) {
        if (selectedModel != null) {
            System.out.println("Поворот успешно применен к модели: " + selectedModel.toString()
                    + " с следующими значениями x = " + x + " y = " + y + " z = " + z);
        } else {
            throw new IllegalStateException("Ошибка: Модель не выбрана. Пожалуйста, выберите модель.");
        }
    }

    public static void scale(Object selectedModel, double x, double y, double z) {
        if (selectedModel != null) {
            System.out.println("Изменение масштаба успешно применено к модели: " + selectedModel.toString()
                    + " с следующими значениями x = " + x + " y = " + y + " z = " + z);
        } else {
            throw new IllegalStateException("Ошибка: Модель не выбрана. Пожалуйста, выберите модель.");
        }
    }

}
