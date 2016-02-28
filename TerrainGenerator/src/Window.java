import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.text.ParseException;

/**
 * Created by Marcello on 24/02/2016.
 */
public class Window extends Application {

    MapRenderer mapRenderer;

    int renderType = 0;
    int octaveCount = 3;
    float persistance = 0.5f;
    long seed = System.currentTimeMillis();

    TextField seedField;
    Slider octaveCountSlider, persistanceSlider;

    @Override
    public void start(Stage primaryStage) throws Exception {
        mapRenderer = new MapRenderer(500, 500);

        primaryStage.setScene(new Scene(generateUI()));

        //Generates and renders a noise map
        primaryStage.setTitle("TerrainGeneration");
        double[][] initialNoiseMap = Noise.generateNoiseMap(Noise.generateWhiteNoise(500, 500, seed), octaveCount, persistance);
        mapRenderer.renderNoiseMap(initialNoiseMap, 500, 500);

        primaryStage.show();
    }

    //Generates the UI controls and pieces and organises them in a BorderPane
    private BorderPane generateUI() {
        BorderPane borderPane = new BorderPane();
        borderPane.setLeft(mapRenderer);

        borderPane.setRight(generateControls());

        borderPane.setTop(generateMenuBar());

        return borderPane;
    }

    //Makes the small menu bar at the top with the save and quit button
    private MenuBar generateMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu mainMenu = new Menu("Terrain Generator");

        MenuItem quitItem = new MenuItem("Quit");
        quitItem.setOnAction(e -> System.exit(0));

        MenuItem saveItem = new MenuItem("Save Image");
        saveItem.setOnAction(e -> mapRenderer.saveRender());

        mainMenu.getItems().addAll(saveItem, quitItem);
        menuBar.getMenus().add(mainMenu);

        return menuBar;
    }

    //Creates the set of controls that sit on the right side of the screen
    private GridPane generateControls() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10, 5, 10, 5));

        gridPane.add(new Label("Seed"), 0, 0);
        seedField = new TextField("" + seed);
        gridPane.add(seedField, 1, 0);

        Label octaveLabel = new Label("Octave Count (" + format2Digit(octaveCount) + ")");
        gridPane.add(octaveLabel, 0, 1);
        octaveCountSlider = new Slider(1, 10, octaveCount);
        octaveCountSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            octaveCount = newValue.intValue();
            octaveLabel.setText("Octave Count (" + format2Digit(octaveCount) + ")");
        });
        gridPane.add(octaveCountSlider, 1, 1);

        Label persistanceLabel = new Label("Persistance (" + formatPercent(persistance) + ")");
        gridPane.add(persistanceLabel, 0, 2);
        persistanceSlider = new Slider(1, 100, persistance * 100);
        persistanceSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            persistance = newValue.floatValue() / 100;
            persistanceLabel.setText("Persistance (" + formatPercent(persistance) + ")");
        });
        gridPane.add(persistanceSlider, 1, 2);

        //Allows you to swap the render type
        //When you change it, it also re renders the image in that type
        ChoiceBox choiceBox = new ChoiceBox();
        choiceBox.getItems().addAll("Noise Map", "Terrain Map", "Neon Map");
        choiceBox.setValue("Noise Map");
        choiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.toString().equals("Noise Map")){
                renderType = 0;
            }else if(newValue.toString().equals("Terrain Map")){
                renderType = 1;
            }else if(newValue.toString().equals("Neon Map")){
                renderType = 2;
            }
            render();
        });
        gridPane.add(choiceBox, 0, 3, 2, 1);

        Button renderButton = new Button("Render");
        renderButton.setOnAction(e -> render());
        gridPane.add(renderButton, 0, 4);

        return gridPane;
    }

    //This handles the rendering, it passes the appropriate values to the noise
    //Functions to generate the noise map
    //And then based on the render type selected it will render the image
    private void render() {
        try{
            seed = Long.parseLong(seedField.getText());
        }catch(Exception e){
            System.out.println("Broke whilst parsing the seed"); //Perfect method for reporting errors
            seed = System.currentTimeMillis();
            seedField.setText("" + seed);
        }
        double[][] noiseMap = Noise.generateNoiseMap(Noise.generateWhiteNoise(500, 500, seed), octaveCount, persistance);

        switch(renderType){
            case 0:
                mapRenderer.renderNoiseMap(noiseMap, 500, 500);
                break;
            case 1:
                mapRenderer.renderTerrainMap(noiseMap, 500, 500);
                break;
            case 2:
                mapRenderer.renderNeonMap(noiseMap, 500, 500);
                break;
        }
    }

    //Formats an integer to always have 2 digits, even if it is less than ten
    //SAMPLE: 9 -> 09
    private String format2Digit(int value) {
        if(value >= 10){
            return "" + value;
        }else{
            return "0" + value;
        }
    }

    //Formats a float between 1 and 0 to a percentage
    //SAMPLE: 0.56 -> 056% 0.03 -> 003%
    private String formatPercent(float percent) {
        int value = (int)(percent * 100);
        if(value == 100){
            return value + "%";
        }else if(value >= 10){
            return "0" + value + "%";
        }else{
            return "00" + value + "%";
        }
    }
}
