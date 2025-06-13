/*
    Name: Keith Holcomb
    Date: May 5th, 2025
    Course: CISS:451 Senior Projects 001
    Description: StartController is the controller class for the start.fxml scene.
*/
package keith.pcbuildingappproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import java.io.IOException;

public class StartController {

    @FXML
    private Label errorLabel;

    // Event handler for createAccount button
    public void createAccountAction(ActionEvent event) throws IOException {
        // Get UserAccount instance
        UserAccount user = UserAccount.getInstance();
        if (!user.isLoggedIn()) {
            // If the user isn't logged in, proceed to createAccount scene.
            // Load the associated scene.
            Main.getInstance().sceneLoad("createAccount.fxml");
            // Close window that the event occurred in
            ((Node) event.getSource()).getScene().getWindow().hide();
        }
        else {
            // User is logged in, which is an error in this case.
            // User was already logged in (Shouldn't happen).
            errorLabel.setText("User is already logged into an account.");
            // Change scene to mainMenu scene.
            Main.getInstance().sceneLoad("mainMenu.fxml");
            // Close window that the event occurred in
            ((Node) event.getSource()).getScene().getWindow().hide();
        }
    }

    // Event handler for login button
    public void loginAction(ActionEvent event) throws IOException {
        // Get UserAccount instance.
        UserAccount user = UserAccount.getInstance();
        if (!user.isLoggedIn()) {
            // If the user isn't logged in, proceed to loginAccount scene.
            // Change scene to login scene.
            Main.getInstance().sceneLoad("loginAccount.fxml");
            // Close window that the event occurred in
            ((Node) event.getSource()).getScene().getWindow().hide();
        }
        else {
            // User is logged in, which is an error in this case.
            // User was already logged in (Shouldn't happen).
            errorLabel.setText("User is already logged into an account.");
            // Change scene to mainMenu scene.
            Main.getInstance().sceneLoad("mainMenu.fxml");
            // Close window that the event occurred in
            ((Node) event.getSource()).getScene().getWindow().hide();
        }
    }

    // Event handler for guest button
    public void guestAction(ActionEvent event) throws IOException {
        // Get UserAccount instance.
        UserAccount user = UserAccount.getInstance();
        if (!user.isLoggedIn()) {
            // If the user isn't logged in, proceed to the mainMenu scene.
            // Change scene to mainMenu scene.
            Main.getInstance().sceneLoad("mainMenu.fxml");
            // Close window that the event occurred in
            ((Node) event.getSource()).getScene().getWindow().hide();
        }
        else {
            // User is logged in, which is an error in this case.
            // User was already logged in (Shouldn't happen).
            errorLabel.setText("User is already logged into an account.");
            // Change scene to mainMenu scene.
            Main.getInstance().sceneLoad("mainMenu.fxml");
            // Close window that the event occurred in
            ((Node) event.getSource()).getScene().getWindow().hide();
        }
    }
}
