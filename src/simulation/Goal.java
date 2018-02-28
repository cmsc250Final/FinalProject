/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;


import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import physics.LineSegment;
import physics.Point;

/**
 *
 * @author gabrielhartmark
 */
public class Goal implements Constants {
    private int width;
    private int startX;
    private int startY;
    private int color;
    private Rectangle r;
    private LineSegment target;
    
    public Goal(int w, int x, int y, int c) {
        width = w;
        startX = x;
        startY = y;
        color = c;
        Point start;
        Point end;
        if(c==BLUE) {
            start = new Point(startX,startY); 
            end = new Point(startX+width,startY);
        } else {
            start = new Point(startX+width,startY);
            end = new Point(startX,startY);
        }
        target = new LineSegment(start,end);
    }
    
    public Rectangle getShape() {
        r = new Rectangle(startX-2, startY-2, width, 4);
        if(color==Constants.BLUE) {
            r.setFill(Color.BLUE);
            r.setStroke(Color.BLUE);
        } else {
            r.setFill(Color.RED);
            r.setStroke(Color.RED);          
        }
        return r;
    }
    
    public boolean isScored(LineSegment l) {
        if(target.intersection(l)!=null) {
            return true;
        }
        return false;
    }
}
