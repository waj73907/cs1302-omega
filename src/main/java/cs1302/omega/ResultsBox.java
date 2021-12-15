package cs1302.omega;

import javafx.scene.text.TextFlow;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;

/**
 * Class for the results box which is where the results
 * of the race are printed.
 */

public class ResultsBox extends VBox {

    public TextFlow textBox = null;

    /**
     * Constructor for the resultsBox class.
     */

    public ResultsBox() {
        super();
        this.textBox = new TextFlow();
        this.getChildren().addAll(textBox);
    }

}
