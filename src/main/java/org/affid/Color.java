package org.affid;

import java.util.ArrayList;
import java.util.HashMap;

public class Color {
    private final int r;
    private final int g;
    private final int b;

    public static Color red = new Color(255, 0, 0);
    public static Color green = new Color(0, 255, 0);
    public static Color blue = new Color(0, 0, 255);
    public static Color sky = new Color(66 , 170, 255);
    public static Color magenta = new Color(255, 0, 255);
    public static Color yellow = new Color(255, 255, 0);
    public static Color orange = new Color(255, 165, 0);
    public static Color white = new Color(255, 255, 255);
    public static Color black = new Color(0, 0, 0);

    private final static HashMap<String, Color> colors = new HashMap<>();

    public Color(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public static void addColor(String name, Color color) {
        colors.put(name, color);
    }

    public static void addColor(String name) {
        Color color = getRandomColor();
        while (colors.containsValue(color) && !colors.containsKey(name)) {
            color = getRandomColor();
        }
        colors.put(name, color);
    }

    public static Color mixColors(ArrayList<String> keys) {
        int[] rs = new int[keys.size()];
        int[] gs = new int[keys.size()];
        int[] bs = new int[keys.size()];
        for (int i = 0; i < keys.size(); i++) {
            rs[i] = colors.get(keys.get(i)).r;
            gs[i] = colors.get(keys.get(i)).g;
            bs[i] = colors.get(keys.get(i)).b;
        }
        return new Color(average(rs), average(gs), average(bs));
    }

    private static int average(int[] vals) {
        int sum = 0;
        for (int i : vals) {
            sum += i;
        }
        return vals.length == 0 ? 255 : sum / vals.length;
    }

    public static ArrayList<Color> getGradient(Color start, Color end, int steps) {
        ArrayList<Color> gradient = new ArrayList<>();
        Color diff = end.difference(start);
        Color step = new Color(diff.r / steps, diff.g / steps, diff.b / steps);
        for (int i = 0; i < steps; i++) {
            gradient.add(start.add(step.multiply(i)));
        }
        return gradient;
    }

    public Color add(Color color) {
        return new Color(Math.min(this.r + color.r, 255), Math.min(this.g + color.g, 255), Math.min(this.b + color.b, 255));
    }

    private Color multiply(int scale) {
        return new Color(this.r * scale, this.g * scale, this.b * scale);
    }

    private Color difference(Color color) {
        return new Color(this.r - color.r, this.g - color.g, this.b - color.b);
    }

    public Color subtract(Color color) {
        return new Color(Math.max(this.r - color.r, 0), Math.max(this.g - color.g, 0), Math.max(this.b - color.b, 0));
    }

    public static Color getColor(String name) {
        return colors.get(name);
    }

    static Color getRandomColor() {
        return new Color((int) (50 + Math.random() * 205), (int) (50 + Math.random() * 205), (int) (50 + Math.random() * 205));
    }

    public boolean equals(Color color) {
        return this.r == color.r && this.g == color.g && this.b == color.b;
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }

    @Override
    public String toString() {
        return String.format("rgb(%d, %d, %d)", r, g, b);
    }
}
