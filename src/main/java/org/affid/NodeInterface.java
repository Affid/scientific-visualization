package org.affid;

import java.util.LinkedList;

public interface NodeInterface {

    Coordinate getDisp();

    void setDisp(float x, float y);

    void setDisp(Coordinate d);

    int getId();

    void setId(int id) ;

    LinkedList<NodeInterface> getIncidentList() ;

    void addEdge(NodeInterface n);

    void removeEdge(NodeInterface n);

     int getColor();

    void setColor(int color);

    Coordinate getCoord() ;

    void setCoord(float x, float y) ;

    int getDegree() ;

    boolean isAdjacent(NodeInterface n) ;
}
