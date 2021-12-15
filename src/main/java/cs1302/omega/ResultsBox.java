package cs1302.omega;

import javafx.scene.text.TextFlow;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;

public class ResultsBox extends VBox {

    public TextFlow textBox = null;

    public ResultsBox() {
        super();
        this.textBox = new TextFlow();
        this.getChildren().addAll(textBox);
    }

}
