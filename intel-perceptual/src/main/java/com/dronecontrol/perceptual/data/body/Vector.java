package com.dronecontrol.perceptual.data.body;

public class Vector {


    public static class NoVector extends Vector {
        public NoVector() {
            super(0, 0, 0);
        }
    }

    public static final Vector NO_VECTOR = new NoVector();

    public final float x;
    public final float y;
    public final float z;

    public Vector(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public Vector plus(Vector vector) {
        return new Vector(x + vector.x, y + vector.y, z + vector.z);
    }

    public Vector minus(Vector vector) {
        return new Vector(x - vector.x, y - vector.y, z - vector.z);
    }

    public Vector divideBy(float number) {
        return new Vector(x / number, y / number, z / number);
    }

    public Vector multiplyWith(float number) {
        return new Vector(x * number, y * number, z * number);
    }

    public Vector invert() {
        return multiplyWith(-1);
    }

    public Vector normalize() {
        float length = (float) Math.sqrt(x * x + y * y + z * z);
        return length == 0 ? NO_VECTOR : divideBy(length);
    }

    public Vector crossProduct(Vector otherVector) {
        return new Vector(
                y * otherVector.z - z * otherVector.y,
                z * otherVector.x - x * otherVector.z,
                x * otherVector.y - y * otherVector.x
        );
    }

    @Override
    public String toString() {
        return String.format("X: [%s], Y:[%s], Z:[%s]", getX(), getY(), getZ());
    }
}