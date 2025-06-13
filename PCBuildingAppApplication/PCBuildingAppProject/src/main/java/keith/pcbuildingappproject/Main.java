/*
    Name: Keith Holcomb
    Date: May 5th, 2025
    Course: CISS:451 Senior Projects 001
    Description: Main.java is the application class for the PC Building App
*/
package keith.pcbuildingappproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main extends Application {

    // Main has a static instance set.
    private static Main instance;
    // The controllers Map holds strings of each controller file name as needed.
    private Map<String, Object> controllers = new HashMap<>();

    // Application start override.
    @Override
    public void start(Stage mainStage) throws IOException {
        // Define the instance of the Application as it currently is.
        instance = this;
        // Load the start scene.
        sceneLoad("start.fxml");
    }

    // Loads a scene based on the provided string. Ex. sceneLoad("name.fxml").
    // All scenes use this Main function to load themselves.
    public void sceneLoad(String fxml) throws IOException {
        // FXML loader handles loading a scene from an FXML file.
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Parent root = loader.load();

        // Standard JavaFX stage building
        Stage stage = new Stage();
        // The scenes have predefined resolutions, so there's no need for the windows to be resizeable
        stage.setResizable(false);
        // Set the scene
        stage.setScene(new Scene(root));
        // Title will always be 'PC Building App'.
        stage.setTitle("PC Building App");
        // Show the scene
        stage.show();

        // Store the controller
        controllers.put(fxml, loader.getController());
    }

    // Returns the current controller.
    public Object getController(String fxmlFile) { return controllers.get(fxmlFile); }

    // Getter for Main instance.
    public static Main getInstance() { return instance; }

    // Launch arguments
    public static void main(String[] args) { launch(); }
}
