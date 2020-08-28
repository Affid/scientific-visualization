package org.affid.forcefirectedscheme;

import org.affid.Coordinate;

public class Point implements Coordinate {
    float x;
    float y;
    float z;

    public Point(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    @Override
    public Coordinate subtract(Coordinate c) {
        return new Point(x - c.getX(), y - c.getY(), z - c.getZ());
    }

    @Override
    public Coordinate add(Coordinate c) {
        return new Point(x + c.getX(), y + c.getY(), z + c.getZ());
    }

    @Override
    public Coordinate unit() {
        if (length() < 0.001)
            return new Point(0.000001f, 0.0000001f, 0.0000001f);
        else
            return new Point(x / (float) Math.sqrt(x * x + y * y + z * z),
                    y / (float) Math.sqrt(y * y + x * x + z * z),
                    z / (float) Math.sqrt(y * y + x * x + z * z));
    }

    @Override
    public float length() {
        return (float) Math.sqrt(y * y + x * x + z * z);
    }

    @Override
    public float distance(Coordinate c) {
        return (float) Math.sqrt(Math.pow(x - c.getX(), 2) + Math.pow(y - c.getY(), 2) + Math.pow(z - c.getZ(), 2));
    }

    @Override
    public Coordinate scale(float k) {
        return new Point(x * k, y * k, z * k);
    }
}
