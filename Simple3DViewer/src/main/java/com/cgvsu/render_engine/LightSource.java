package com.cgvsu.render_engine;


public class LightSource {
    private final String name;
    private final com.cgvsu.math.Vector3f position;

    public LightSource(String name, com.cgvsu.math.Vector3f position) {
        this.name = name;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public com.cgvsu.math.Vector3f getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "Light ("+ position.getX() + ", " + position.getY()+ ", " + position.getZ() + ")";
    }
}

