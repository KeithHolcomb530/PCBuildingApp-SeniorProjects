/*
    Name: Keith Holcomb
    Date: May 5th, 2025
    Course: CISS:451 Senior Projects 001
    Description: CreateAccountController is the controller class for the createAccount.fxml scene
*/
package keith.pcbuildingappproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class CreateAccountController {

    @FXML
    private Label errorLabel;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField passwordConfirmField;

    // Event handler for createAccount button
    public void createAccountAction(ActionEvent event) throws IOException, SQLException {
        // Get UserAccount instance.
        UserAccount user = UserAccount.getInstance();
        if (!user.isLoggedIn()) {
            // If User isn't logged in, proceed with account creation.
            // Username should be between 8 and 32 characters.
            if (usernameField.getText().length() >= 8 && usernameField.getText().length() <= 32 ) {
                // Call on the PostgreSQLController to check if the username exists already.
                boolean usernameTaken = PostgreSQLController.usernameExists(usernameField.getText());
                // Username should be unique.
                if (!usernameTaken) {
                    // If the username is unique (not taken), proceed to check the password.
                    if (passwordCheck()) {
                        // Call on the PostgreSQLController to attempt to write the new user account.
                        if (PostgreSQLController.writeUserAccount(usernameField.getText(), passwordField.getText())) {
                            // Account creation successful, update the user instance to match details of the user account.
                            user.setLoggedIn(true);
                            // Set user instance's username.
                            user.setUsername(usernameField.getText());
                            // Find user instance's account id based on the username in the database.
                            int accountId = PostgreSQLController.getAccountid(usernameField.getText());
                            // Set user instance's account id.
                            user.setAccountid(accountId);
                            // Return the user to the mainMenu scene, after login status is set to true.
                            Main.getInstance().sceneLoad("mainMenu.fxml");
                            // Close window that the event occurred in.
                            ((Node) event.getSource()).getScene().getWindow().hide();
                        }
                        // Writing the user info to the database failed.
                        else {
                            // Check whether connection to the database was successful or not.
                            if (!PostgreSQLController.connectSuccess()) {
                                // Connection failed.
                                errorLabel.setText("Account creation failed. Unable to connect to the database.");
                            }
                            // Only other error is that the username was taken.
                            else {
                                // Account creation failed, but connection was successful.
                                errorLabel.setText("Account creation failed. Username already exists.");
                            }
                        }
                    }
                }
                // Username was unavailable.
                else {
                    // Check whether connection to the database was successful or not.
                    if (!PostgreSQLController.connectSuccess()) {
                        // Connection failed.
                        errorLabel.setText("Account creation failed. Unable to connect to the database.");
                    }
                    // Only other error is that the username was taken.
                    else {
                        // Account creation failed, but connection was successful.
                        errorLabel.setText("Account creation failed. Username already exists.");
                    }
                }
            }
            // Username was not between 8 and 32 characters.
            else {
                // Username should be between 8 and 32 characters.
                errorLabel.setText("Account creation failed. Username must be between 8 and 32 characters long.");
            }
        }
        // The user somehow got to the create account scene whilst being logged in.
        else {
            // This is a special case (that should never happen), and requires the application to return to the main menu.
            // Give the user an error alert to let them know an unhandled error happened.
            errorAlert();
            // Return user to the main menu.
            Main.getInstance().sceneLoad("mainMenu.fxml");
            // Close window that the event occurred in.
            ((Node) event.getSource()).getScene().getWindow().hide();
        }
    }

    // Special unhandled error alert
    public void errorAlert() {
        // An alert is created using error details.
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR!");
        alert.setHeaderText("USER IS CURRENTLY LOGGED INTO AN EXISTING ACCOUNT!");
        alert.setContentText("Returning the user to the main menu. Please log out of the existing account before attempting to create a new account.");
        // Display the alert and await the user to interact.
        alert.showAndWait();
    }

    // Check the password and return a Boolean value.
    public boolean passwordCheck() {
        // Password must be between 8 and 100 characters in length.
        if (passwordField.getText().isEmpty() || (passwordField.getText().length() < 8 || passwordField.getText().length() > 100)) {
            errorLabel.setText("Password must be between 8 and 100 characters in length");
            return false;
        }

        // Password needs to contain at least an Uppercase, lowercase and 1 digit.
        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;

        // Check each character in the passwordField, and check off password requirements as they are found.
        for (char c : passwordField.getText().toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowercase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }
        }

        // Check if any of the password requirements failed.
        if (!hasUppercase || !hasLowercase || !hasDigit) {
            // If any of the password requirements failed, throw an error message to the user.
            errorLabel.setText("Password must contain at least one uppercase, lowercase and digit character.");
            return false;
        }
        // If none of the password requirements failed, check that the confirm-field matches the password field.
        else if (!Objects.equals(passwordField.getText(), passwordConfirmField.getText())) {
            // passwordField and passwordConfirmField don't match.
            errorLabel.setText("Password and password confirmation mismatch.");
            return false;
        }
        else {
            // Password check was successful.
            return true;
        }
    }
}
