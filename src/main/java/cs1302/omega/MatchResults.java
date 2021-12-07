package cs1302.omega;

import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class MatchResults extends VBox {

    TextArea resultsBox = null;

    public MatchResults() {
        super();
        resultsBox = new TextArea("This is where the match results will be displayed");
        this.getChildren().add(resultsBox);
    }

}
