package cs1302.omega;

import javafx.scene.control.TextField;
import javafx.scene.control.Separator;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class searchBar extends HBox {

    TextField searchField = null;
    Button searchButton = null;

    public searchBar() {
        super();
        this.searchField = new TextField();
        this.searchButton = new Button("Search");
        this.getChildren().addAll(searchField, new Separator(), searchButton);
    }
}
