import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MapRenderer extends Canvas {

    GraphicsContext gc;
    double width, height;

    //A collection of the terrain types and the heights they have jurisdiction over
    private TerrainType[] terrains = {
            new TerrainType(new Color(30, 60, 255), 0.2f), //Deep Water
            new TerrainType(new Color(30, 80, 255), 0.3f), //Shallow Water
            new TerrainType(new Color(160, 140, 100) ,0.35f), //ShoreLine
            new TerrainType(new Color(100, 180, 100), 0.5f), //Grass
            new TerrainType(new Color(30, 80, 30), 0.6f), //Grassier Grass
            new TerrainType(new Color(80, 40, 40), 0.7f), //Dirty Dirt
            new TerrainType(new Color(60, 40, 60), 0.8f), //Rocky
            new TerrainType(new Color(70, 55, 70), 0.95f), //Mountains
            new TerrainType(new Color(255, 255, 255), 1.0f) //Snow
    };

    //Fun extra rendering mode
    private TerrainType[] neonTerrains = {
            new TerrainType(new Color(120, 200, 255), 0.2f), //Blue
            new TerrainType(new Color(255, 0, 255), 0.3f), //Razor Pink
            new TerrainType(new Color(0, 255, 0) ,0.4f), //Bright Green
            new TerrainType(new Color(100, 0, 100), 0.5f), //Purple
            new TerrainType(new Color(180, 80, 0), 0.6f), //Dark Orange
            new TerrainType(new Color(20, 150, 200), 0.7f), //Different Blue
            new TerrainType(new Color(170, 255, 80), 0.8f), //Pale Green
            new TerrainType(new Color(255, 0, 0), 0.95f), //Very Red
            new TerrainType(new Color(255, 255, 0), 1.0f) //Fairly Yellow
    };

    //Creator for the MapRenderer
    public MapRenderer(double _width, double _height){
        super(_width, _height);
        width = _width;
        height = _height;
        gc = getGraphicsContext2D();
    }

    //Creates a gray scale noise image
    public void renderNoiseMap(double[][] noiseMap, int mapWidth, int mapHeight){
        BufferedImage image = new BufferedImage(mapWidth, mapHeight, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                int value = (int)(noiseMap[x][y] * 255);
                image.setRGB(x, y, new Color(value, value, value).getRGB());
            }
        }
        gc.drawImage(SwingFXUtils.toFXImage(image, null), 0, 0, width, height);
    }

    //Renders the Coloured terrain map using the array of TerrainTypes
    public void renderTerrainMap(double[][] noiseMap, int mapWidth, int mapHeight) {
        BufferedImage image = new BufferedImage(mapWidth, mapHeight, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                for (int i = 0; i < terrains.length; i++) {
                    if(noiseMap[x][y] <= terrains[i].height){
                        //if the "height" is below the level of the terrain type then it gets that colour
                        image.setRGB(x, y, terrains[i].color.getRGB());
                        break;
                    }
                }
            }
        }

        gc.drawImage(SwingFXUtils.toFXImage(image, null), 0, 0, width, height);
    }

    //Renders the Coloured terrain map using the array of neon colour terrain types
    public void renderNeonMap(double[][] noiseMap, int mapWidth, int mapHeight) {
        BufferedImage image = new BufferedImage(mapWidth, mapHeight, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                for (int i = 0; i < neonTerrains.length; i++) {
                    if(noiseMap[x][y] <= neonTerrains[i].height){
                        //if the "height" is below the level of the terrain type then it gets that colour
                        image.setRGB(x, y, neonTerrains[i].color.getRGB());
                        break;
                    }
                }
            }
        }

        gc.drawImage(SwingFXUtils.toFXImage(image, null), 0, 0, width, height);
    }

    //Saves the curent contents of the canvas to an image
    public void saveRender() {
        WritableImage canvas = snapshot(null, null);
        BufferedImage saveImage = SwingFXUtils.fromFXImage(canvas, null);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Render...");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Portable Network Graphic", ".png")
        );

        try {
            File fileToSave = fileChooser.showSaveDialog(new Stage());
            ImageIO.write(saveImage, "png", fileToSave);
        }catch (Exception e){
            System.out.println("Ya done goofed"); //More great error messaging
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Image Save Error");
            alert.setHeaderText("Well, this is embarrassing...");
            alert.setContentText("There was an error saving your image, so I would suggest you try again but do it a little differently, just my advice");
            alert.show();
        }

    }
}

//Simplifies the system of colouring the terrain map
class TerrainType {
    Color color;
    float height;

    public TerrainType(Color _color, float _height){
        color = _color;
        height = _height;
    }
}
