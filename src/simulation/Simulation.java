package simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import physics.*;

public class Simulation implements Constants{
    private Box outer;
    private Ball ball;
    private Box bluePaddle;
    private Box redPaddle;
    private Lock lock;
    private Goal blueGoal;
    private Goal redGoal;
    private int width;
    private int height;
    private Vector initialVector;
    private int blueScore = 0;
    private int redScore = 0;
    private Text displayScore;
    
    public Simulation(int width,int height,int dX,int dY)
    {
        this.width = width;
        this.height = height;
        initialVector = new Vector(dX,dY);
        outer = new Box(0,0,width,height,false);
        ball = new Ball(width/2,height/2,dX,dY);
        bluePaddle = new Box(width/2-20, 40, 40, 20,true,BLUE);
        redPaddle = new Box(width/2 - 20, height-40, 40, 20,true,RED);
        lock = new ReentrantLock();
        blueGoal = new Goal(width/3,width/3,0,BLUE);
        redGoal = new Goal(width/3,width/3,height,RED);
        displayScore = new Text(width/2, height+20,"BLUE: "+blueScore+"\nRED: "+redScore);
    }
    
    public void evolve(double time)
    {
        lock.lock();
        Ray newLoc = bluePaddle.bounceRay(ball.getRay(), time);
        if(newLoc != null)
            ball.setRay(newLoc);
        newLoc = redPaddle.bounceRay(ball.getRay(), time);
        if(newLoc != null)
            ball.setRay(newLoc);
        else if(blueGoal.isScored(ball.getRay().toSegment(time))) {
            System.out.println("Red Scored");
            ball.getRay().restart(new Point(width/2,height/2), initialVector);
            ball.getRay().multiplySpeed(1.1);
            redScore++;
            try{Thread.sleep(1000);}catch(InterruptedException e){}
        } else if(redGoal.isScored(ball.getRay().toSegment(time))) {
            System.out.println("Blue Scored");
            ball.getRay().restart(new Point(width/2,height/2), initialVector);
            ball.getRay().multiplySpeed(1.1);
            blueScore++;
            try{Thread.sleep(1000);}catch(InterruptedException e){}
        }
        else {
            newLoc = outer.bounceRay(ball.getRay(), time);
            if(newLoc != null)
                ball.setRay(newLoc);
            else
                ball.move(time);
        } 
        double x = ball.getRay().origin.x;
        double y = ball.getRay().origin.y;
        if(x>width+2 || x<0-2 || y>height+2 || y<0-2) { //Restarts if the ball escapes the outer box;
            try {
                Thread.sleep(1000);
                ball.getRay().restart(new Point(width/2,height/2));
            }catch(InterruptedException e) {
                
            } 
        }
        if(redScore>=10 || blueScore>=10) {
            ball.getRay().restart(new Point(width/2,height/2), initialVector);
            ball.getRay().speed = initialVector.length();
            bluePaddle.move((width/2-20)-bluePaddle.x,40-bluePaddle.y);
            redPaddle.move((width/2-20)-redPaddle.x, (height-40)-redPaddle.y);
            redScore=0;
            blueScore=0;
            try{Thread.sleep(2000);}
            catch(InterruptedException e) {}
        }
        lock.unlock();
    }
    
    public void moveInner(int deltaX,int deltaY, int color)
    {
        Box inner=null;
        if(color==BLUE)
            inner = bluePaddle;
        else if(color==RED)
            inner = redPaddle;
        lock.lock();
        int dX = deltaX;
        int dY = deltaY;
        if(inner.x + deltaX < 0)
          dX = -inner.x;
        if(inner.x + inner.width + deltaX > outer.width)
          dX = outer.width - inner.width - inner.x;
       
        if(inner.y + deltaY < 0)
           dY = -inner.y;
        if(inner.y + inner.height + deltaY > outer.height)
           dY = outer.height - inner.height - inner.y;
        
        inner.move(dX,dY);
        if(inner.contains(ball.getRay().origin)) {
            // If we have discovered that the box has just jumped on top of
            // the ball, we nudge them apart until the box no longer
            // contains the ball.
            int bumpX = -1;
            if(dX < 0) bumpX = 1;
            int bumpY = -1;
            if(dY < 0) bumpY = 1;
            do {
            inner.move(bumpX, bumpY);
            ball.getRay().origin.x += -bumpX;
            ball.getRay().origin.y += -bumpY;
            } while(inner.contains(ball.getRay().origin));
        }
        lock.unlock();
    }
    
    public List<Shape> setUpShapes()
    {
        ArrayList<Shape> newShapes = new ArrayList<Shape>();
        newShapes.add(outer.getShape());
        newShapes.add(bluePaddle.getShape());
        newShapes.add(redPaddle.getShape());
        newShapes.add(ball.getShape());
        newShapes.add(blueGoal.getShape());
        newShapes.add(redGoal.getShape());
        newShapes.add(displayScore);
      //  newShapes.add(new Text(width/2, height+22,"RED: "+redScore));
        return newShapes;
    }
    
    public void updateShapes()
    {
        bluePaddle.updateShape();
        redPaddle.updateShape();
        ball.updateShape();
        displayScore.setText("BLUE: "+blueScore+"\nRED: "+redScore);
    }
    
    public int[] sendChangingValues() {
        int[] values = new int[8];
        values[0] = bluePaddle.x;
        values[1] = bluePaddle.y;
        values[2] = redPaddle.x;
        values[3] = redPaddle.y;
        values[4] = (int) Math.round(ball.getRay().origin.x);
        values[5] = (int) Math.round(ball.getRay().origin.y);
        values[6] = blueScore;
        values[7] = redScore;
        return values;
    }
    public int getScore(int color) { 
        if(color==BLUE)    
            return blueScore;
        else if(color==RED)
            return redScore;
        return -1;
    }
}
