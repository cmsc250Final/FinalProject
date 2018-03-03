package physicsdemo;

import java.util.List;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

public class GamePane extends Pane {
    public GamePane() {
        
    }
    
    public void setShapes(List<Shape> newShapes) {
        this.getChildren().clear();
        this.getChildren().addAll(newShapes);
    }

}
