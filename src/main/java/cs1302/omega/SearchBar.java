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

/**
 * This is the main component for the User interface
 * and the backend.
 */

public class SearchBar extends VBox {

    private TextField yearField = null;
    private TextField roundField = null;
    private Button generateButton = null;
    private String raceCountry = null;
    private String raceYear = null;
    public JsonArray resultsArray = null;
    private ResultsBox resultsBox = null;
    private TextField youtubeUrl = null;

    /**
     * Constructor for the SearchBar class that initializes some
     * instance variables as well as creating the event handler for
     * the button.
     */
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
            parseYouTubeJson(this.constructYoutubeUrl());
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
    /**
     * Method that builds the url based on the user input inside
     * the textFields.
     * @return String returns the url that was built.
     */

    public String buildUrl() {
        String msg = "http://ergast.com/api/f1/";
        msg += this.yearField.getText() + "/";
        msg += this.roundField.getText() + "/";
        msg += "results.json";
        return msg;
    }
    //converts the url into a JsonElement that can be parsed

    /**
     * Method that takes in the url from the buildUrl
     * method and converts it into a JsonElement.
     * @param url url that will be converted to Json.
     * @return JsonElement returns the json of the url param.
     */

    public JsonElement returnJson(String url)  {
        JsonElement json = null;
        try {
            json = Tools.getJson(url);
        } catch (IOException IOE) {
            System.out.println(IOE.getMessage());
        }
        return json;
    }
    // assigning the appropriate json elements to their appropriate instance variables
    /**
     * Method that fills in the instance variables based on
     * the json url that is passed through.
     * @param url url that will be parsed for the instance
     * variables.
     */

    public void buildJsonResults(String url) {
        try {
            JsonElement root = this.returnJson(url);
            JsonElement racesArray = Tools.get(root,"MRData","RaceTable","Races",0);
            this.raceCountry = Tools.get(racesArray,"Circuit","Location","country").getAsString();
            JsonElement raceYearJson = Tools.get(root, "MRData", "RaceTable", "Races", 0, "season");
            this.raceYear = raceYearJson.getAsString();
            this.resultsArray = this.getResultsArray(root);
        } catch (IndexOutOfBoundsException IOE) {
            System.out.println(IOE.getMessage());
            this.youtubeUrl.setText("Please Try a lower race round");
        }
    }

    // parsing through the root element to get to the results array and return it
    /**
     * Method that converts the given JsonElement into a JsonArray.
     * @param root JsonElement that is going to be converted to JsonArray.
     * @return JsonArray JsonElement that is then turned into JsonArray.
     */

    public JsonArray getResultsArray(JsonElement root) {
        JsonElement resultsElement = Tools.get(root,"MRData","RaceTable","Races",0,"Results");
        JsonArray resultsArray = resultsElement.getAsJsonArray();
        return resultsArray;
    }

    /**
     * Method that parses through the results array and
     * prints the output to the textFlow object.
     * @param resultsArray pass in a Json array that contains the
     * race information.
     * @return Text returns the text object that contains all of
     * the information for a specific place in the race (I.E. 1st 2nd 3rd).
     */
    public Text printDrivers(JsonArray resultsArray) {
        Text text = null;
        this.resultsBox.textBox.getChildren().clear();
        Text text1 = new Text("RACE COUNTRY: " + this.raceCountry + "\n");
        Text text2 = new Text("RACE YEAR: " + this.raceYear + "\n\n");
        this.resultsBox.textBox.getChildren().add(text1);
        this.resultsBox.textBox.getChildren().add(text2);
        for (JsonElement e : resultsArray) {
            String position = Tools.get(e,"position").getAsString() + " | ";
            String points = Tools.get(e,"points").getAsString() + " | ";
            String driverFirst = Tools.get(e,"Driver","givenName").getAsString() + " ";
            String driverLast = Tools.get(e,"Driver","familyName").getAsString() + " | ";
            String constructor = Tools.get(e,"Constructor","name").getAsString() + "\n";
            text = new Text(position + points + driverFirst + driverLast + constructor);
            this.resultsBox.textBox.getChildren().add(text);
        }
        return text;
    }

    /**
     * Method that contructs the youtube link based on the user's input.
     * @return String returns the url for the race highlights IF the race is after
     * 2016
     */

    public String constructYoutubeUrl() {
        String ytEnd = "";
        if (Integer.parseInt(this.raceYear) <= 2016) {
            this.youtubeUrl.setText("NOTE: year must be after 2016 for a link");
            return ytEnd;
        }
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

    /**
     * Method that retrieves the json file based on the search parameters
     * of the constructUrl method and pares through it to get the videoId.
     * @param url url of the search request.
     */

    public void parseYouTubeJson(String url) {
        try {
            JsonElement root = Tools.getJson(url);
            String link = "https://www.youtube.com/watch?v=";
            String videoId = Tools.get(root, "items", 0, "id", "videoId").getAsString();
            link += videoId;
            this.youtubeUrl.setText(link);
        } catch (IOException IOE) {
            System.out.println(IOE.getMessage());
        }

    }
}
