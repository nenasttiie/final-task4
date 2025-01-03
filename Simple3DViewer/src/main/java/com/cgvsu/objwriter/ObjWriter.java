package com.cgvsu.objwriter;

import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class ObjWriter {
    public static void write(Model model, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Vector3f vertex : model.vertices) {
                writer.write(String.format(Locale.US, "v %f %f %f\n", vertex.getX(), vertex.getY(), vertex.getZ()));
            }

            for (Vector2f textureVertex : model.textureVertices) {
                writer.write(String.format(Locale.US, "vt %f %f\n", textureVertex.getX(), textureVertex.getY()));
            }

            for (Vector3f normal : model.normals) {
                writer.write(String.format(Locale.US, "vn %f %f %f\n", normal.getX(), normal.getY(), normal.getZ()));
            }

            for (Polygon polygon : model.polygons) {
                writer.write("f");
                ArrayList<Integer> vertexIndices = polygon.getVertexIndices();
                ArrayList<Integer> textureVertexIndices = polygon.getTextureVertexIndices();
                ArrayList<Integer> normalIndices = polygon.getNormalIndices();

                for (int i = 0; i < vertexIndices.size(); i++) {
                    int vertexIndex = vertexIndices.get(i) + 1;
                    int textureVertexIndex = textureVertexIndices.isEmpty() ? 0 : textureVertexIndices.get(i) + 1;
                    int normalIndex = normalIndices.isEmpty() ? 0 : normalIndices.get(i) + 1;
                    writer.write(String.format(Locale.US, " %d/%d/%d", vertexIndex, textureVertexIndex, normalIndex));
                }
                writer.write("\n");
            }
        }
    }
}
