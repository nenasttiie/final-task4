package com.cgvsu.math;

// Это заготовка для собственной библиотеки для работы с линейной алгеброй
public class Vector2f implements Cloneable {
    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }
    @Override
    public Vector2f clone()  {
        try {
            return (Vector2f) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
    float x, y;

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
