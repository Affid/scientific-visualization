package org.affid;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@JsonPropertyOrder({"weight", "x", "y", "z", "color", "links"})
public class Sphere {
    private final double weight;
    private double x;
    private double y;
    private double z;
    private final ArrayList<String> links;
    private double[] move;
    private final Color color;
    private static double k;

    public String getColor() {
        return color.toString();
    }

    public double getWeight() {
        return weight;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public ArrayList<String> getLinks() {
        return links;
    }

    public Sphere(double weight, int x, int y, int z, ArrayList<String> links, Color color) {
        this.weight = weight;
        this.x = x;
        this.y = y;
        this.z = z;
        this.links = links;
        this.color = color;
    }

    public Sphere(double weight, double edge, ArrayList<String> links, Color color) {
        this.weight = weight;
        this.x = Main.getRandom(edge);
        this.y = Main.getRandom(edge);
        this.z = Main.getRandom(edge);
        this.links = links;
        this.color = color;
    }

    public static void replaceSpheres(HashMap<String, Sphere> spheres, double k1) {
        k = k1;
        for (Map.Entry<String, Sphere> sphere : spheres.entrySet()) {
            sphere.getValue().move = new double[3];
            sphere.getValue().move[0] = 0;
            sphere.getValue().move[1] = 0;
            sphere.getValue().move[2] = 0;
            for (Map.Entry<String, Sphere> sphere1 : spheres.entrySet()) {
                if (!sphere.getValue().equals(sphere1.getValue()))
                    for (int i = 0; i < 3; i++) {
                        sphere.getValue().move[i] -= sphere.getValue().getForce(sphere1.getValue(), sphere.getValue().links.contains(sphere1.getKey()))[i];
                    }
            }
            sphere.getValue().updatePos();
        }

        for (Map.Entry<String, Sphere> sphere : spheres.entrySet()) {
            sphere.getValue().updatePos();
        }
    }

    private void updatePos() {
        this.x += move[0];
        this.y += move[1];
        this.z += move[2];
    }

    private double[] getForce(Sphere another, boolean isNeighbor) {
        double[] forceA = new double[3];
        double[] forceR = new double[3];
        double dx = this.x - another.x;
        double dy = this.y - another.y;
        double dz = this.z - another.z;
        double c4 = 0.01 / (this.weight * Main.maxRate + 1);
        double sum = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2) + Math.pow(dz, 2));
        forceA[0] = Math.abs(dx) / sum;
        forceA[1] = Math.abs(dy) / sum;
        forceA[2] = Math.abs(dz) / sum;

        forceR[0] = Math.abs(dx) / sum;
        forceR[1] = Math.abs(dy) / sum;
        forceR[2] = Math.abs(dz) / sum;

        double dist = getDistance(another);

        double attractive = attractiveForce(dist/80, (another.weight * Main.maxRate + 1));

        double repulsive = repulsiveForse(dist/80);
        if (!isNeighbor) {
            repulsive *= 3;
        }

        for (int i = 0; i < 3; i++) {
            forceA[i] *= attractive;
            forceR[i] *= repulsive;
            if (isNeighbor) {
                forceR[i] -= forceA[i];
            }
//            else
//                forceR[i] -= forceA[i]*0.000001;
            forceR[i] *= c4;
        }

        return forceR;
    }

    private double getDistance(Sphere sphere) {
        return Math.sqrt(Math.pow((this.x - sphere.x), 2) + Math.pow((this.y - sphere.y), 2) + Math.pow((this.z - sphere.z), 2));
    }

    private double attractiveForce(double d, double m) {
        //double G = 6.67430*Math.pow(10,1);
        double g = 8;
        return Math.pow(d, 2) * m / (k * g);
    }

    private double repulsiveForse(double d) {
        //double c3 = 6.67430*Math.pow(10,3);
        return Math.pow(k, 2) / d;
    }
}
