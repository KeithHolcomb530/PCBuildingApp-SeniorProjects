/*
    Name: Keith Holcomb
    Date: May 5th, 2025
    Course: CISS:451 Senior Projects 001
    Description: LoginController is the controller class for the loginAccount.fxml scene.
*/
package keith.pcbuildingappproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private Label errorLabel;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    // Event handler for userLogin button
    public void userLogin(ActionEvent event) throws IOException, SQLException {
        // Check the username and password fields to verify a successful login
        if (checkLogin()) {
            // If login check was true, log the user in.
            // Grab the current instance of UserAccount
            UserAccount user = UserAccount.getInstance();
            // Set user instance to be logged in.
            user.setLoggedIn(true);
            // Set user instance's username to the valid user field.
            user.setUsername(usernameField.getText());
            // Call on the PostgreSQLController to get the account id of the user based on the username.
            int accountId = PostgreSQLController.getAccountid(usernameField.getText());
            // Set user instance's account id.
            user.setAccountid(accountId);
            // Switch to main menu scene now that login is complete.
            Main.getInstance().sceneLoad("mainMenu.fxml");
            // Close window the event occurred in.
            ((Node) event.getSource()).getScene().getWindow().hide();
        }
    }

    // Returns a boolean for whether the login info is correct or not.
    private boolean checkLogin() throws IOException {
        if(!usernameField.getText().isEmpty() && !passwordField.getText().isEmpty()) {
            // If the username and password fields are not empty.
            // Call on PostgreSQLController to check if the usernameField matches a username in the database.
            boolean usernameFound = PostgreSQLController.usernameExists(usernameField.getText());

            if (!usernameFound && PostgreSQLController.connectSuccess()) {
                // Username wasn't found and connection to the Database was successful.
                errorLabel.setText("Login failed. Wrong username or password.");
                return false;
            }
            else if (!PostgreSQLController.connectSuccess()) {
                // Connection failed.
                errorLabel.setText("Login failed. Connection to the database couldn't be established.");
                return false;
            }
            // Call upon PostgreSQLController to verify that the username and password match
            else if(!PostgreSQLController.authUserAccount(usernameField.getText(), passwordField.getText()) && PostgreSQLController.connectSuccess()) {
                // Username and password didn't match.
                errorLabel.setText("Login failed. Wrong username or password.");
                return false;
            }
            else {
                // Only remaining condition is that the authUserAccount was successful.
                // The login check was successful.
                return true;
            }
        }
        else {
            // One or both of the fields were empty.
            errorLabel.setText("Please enter your login credentials.");
            return false;
        }
    }
}