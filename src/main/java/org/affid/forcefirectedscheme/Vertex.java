package org.affid.forcefirectedscheme;

public class Vertex {

    float weight;

    Point pos;
    Point disp;

    public Vertex(Point coordinates) {
        this.pos = coordinates;
        this.disp = new Point(0,0,0);
//        this.weight = weight;
    }

    public Point getPos() {
        return pos;
    }
}
