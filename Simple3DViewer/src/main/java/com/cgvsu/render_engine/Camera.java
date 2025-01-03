package com.cgvsu.render_engine;
import javax.vecmath.Vector3f;
import javax.vecmath.Matrix4f;

public class Camera {

    public Camera(
            final Vector3f position,
            final Vector3f target,
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        this.position = position;
        this.target = target;
        this.fov = fov;
        this.aspectRatio = aspectRatio;
        this.nearPlane = nearPlane;
        this.farPlane = farPlane;
    }

    public void setPosition(final Vector3f position) {
        this.position = position;
    }

    public void setTarget(final Vector3f target) {
        this.target = target;
    }

    public void setFov(float fov) {
        this.fov = fov;
    }

    public float getFov() {
        return fov;
    }



    public static void setCamera(Camera camera, float x, float y, float z, float targetX, float targetY, float targetZ, float fov) {
        camera.setPosition(new Vector3f(x, y, z));
        camera.setTarget(new Vector3f(targetX, targetY, targetZ));
        camera.setFov(fov);

        System.out.println("Камера обновлена: позиция = (" + x + ", " + y + ", " + z + "), цель = (" + targetX + ", " + targetY + ", " + targetZ + "), FOV = " + fov);
    }

    public void setAspectRatio(final float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getTarget() {
        return target;
    }

    public void movePosition(final Vector3f translation) {
        this.position.add(translation);
    }

    public void moveTarget(final Vector3f translation) {
        this.target.add(target);
    }

    @Override
    public String toString() {
        return "Camera " + position;
    }

    Matrix4f getViewMatrix() {
        return GraphicConveyor.lookAt(position, target);
    }

    Matrix4f getProjectionMatrix() {
        return GraphicConveyor.perspective(fov, aspectRatio, nearPlane, farPlane);
    }

    private Vector3f position;
    private Vector3f target;
    private float fov;
    private float aspectRatio;
    private float nearPlane;
    private float farPlane;
}