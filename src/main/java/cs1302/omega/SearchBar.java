package cs1302.omega;

import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import cs1302.api.Tools;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

public class SearchBar extends VBox {

    private TextField yearField = null;
    private TextField roundField = null;
    private Button generateButton = null;
    private String raceCountry = null;
    private String raceYear = null;
    public JsonArray resultsArray = null;
    private ResultsBox resultsBox = null;
    private TextField youtubeUrl = null;

    public SearchBar() {
        super();
        HBox barBox = new HBox();
        this.yearField = new TextField("Enter year here (2017-2021)");
        this.yearField.setMinWidth(275);
        this.roundField = new TextField("Enter round number here (1-22)");
        this.roundField.setMinWidth(275);
        this.generateButton = new Button("Generate!");
        this.resultsBox = new ResultsBox();
        this.youtubeUrl = new TextField("Your link will appear here");
        this.youtubeUrl.setMinWidth(275);
        EventHandler<ActionEvent> generateJson = (event) -> {
            //constructing url
            String url = this.buildUrl();
            //getting json elements
            buildJsonResults(url);
            //printing drivers
            this.printDrivers(this.resultsArray);
            System.out.println(this.constructYoutubeUrl());
        };
        generateButton.setOnAction(generateJson);
        barBox.getChildren().add(this.yearField);
        barBox.getChildren().add(new Separator());
        barBox.getChildren().add(this.roundField);
        barBox.getChildren().add(new Separator());
        barBox.getChildren().add(this.generateButton);
        this.getChildren().add(barBox);
        this.getChildren().add(resultsBox);
        this.getChildren().add(youtubeUrl);
    }
    // constructs the url based on the parameters given by the user
    public String buildUrl() {
        String msg = "http://ergast.com/api/f1/";
        msg += this.yearField.getText() + "/";
        msg += this.roundField.getText() + "/";
        msg += "results.json";
        return msg;
    }
    //converts the url into a JsonElement that can be parsed
    public JsonElement returnJson(String url)  {
        JsonElement json = null;
        try {
            json = Tools.getJson(url);
        } catch(IOException IOE) {
            System.out.println(IOE.getMessage());
        }
        return json;
    }
    // assigning the appropriate json elements to their appropriate instance variables
    public void buildJsonResults(String url) {
        JsonElement root = this.returnJson(url);
        this.raceCountry = Tools.get(root,"MRData","RaceTable","Races",0,"Circuit","Location","country").getAsString();
        this.raceYear = Tools.get(root, "MRData", "RaceTable", "Races", 0, "season").getAsString();
        this.resultsArray = this.getResultsArray(root);
    }

    // parsing through the root element to get to the results array and return it
    public JsonArray getResultsArray(JsonElement root) {
        JsonArray resultsArray = Tools.get(root,"MRData","RaceTable","Races",0,"Results").getAsJsonArray();
        return resultsArray;
    }

    public Text printDrivers(JsonArray resultsArray) {
        Text text = null;
        this.resultsBox.textBox.getChildren().clear();
        text = new Text("RACE COUNTRY: " + this.raceCountry + " RACE YEAR: " + this.raceYear+"\n\n");
        this.resultsBox.textBox.getChildren().add(text);
        for (JsonElement e : resultsArray) {
            String position = Tools.get(e,"position").getAsString() + " | ";
            String points = Tools.get(e,"points").getAsString() + " | ";
            String driverFirst = Tools.get(e,"Driver","givenName").getAsString() + " | ";
            String driverLast = Tools.get(e,"Driver","familyName").getAsString() + " | ";
            String constructor = Tools.get(e,"Constructor","name").getAsString()+ "\n";
            text = new Text(position + points + driverFirst + driverLast+constructor);
            this.resultsBox.textBox.getChildren().add(text);
        }
        return text;
    }

    public String constructYoutubeUrl() {
        String ytEnd = "";
        try {
            String ytStart = "https://youtube.googleapis.com/youtube/v3/search?part=snippet&q=";
            String parameter = "F1 " + this.raceCountry + " " + this.raceYear + " Highlights";
            String apiKey = "&key=AIzaSyD559B2cDz27fMiZRLCHxNgVhKA9xxckno";
            String encodedParameter = URLEncoder.encode(parameter, "UTF-8");
            ytEnd = ytStart + encodedParameter + apiKey;
        } catch (UnsupportedEncodingException UEE) {
            System.out.println(UEE.getMessage());
        }
        return ytEnd;

    }
}
