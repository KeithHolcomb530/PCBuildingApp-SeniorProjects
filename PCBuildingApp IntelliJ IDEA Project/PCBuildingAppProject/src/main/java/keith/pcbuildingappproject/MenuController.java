/*
    Name: Keith Holcomb
    Date: May 5th, 2025
    Course: CISS:451 Senior Projects 001
    Description: MenuController is the controller class for the mainMenu.fxml scene.
*/
package keith.pcbuildingappproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import java.io.IOException;
import java.util.List;

public class MenuController {

    @FXML
    private Label errorLabel;
    @FXML
    private ComboBox<PCBuildList> buildListComboBox;
    // initialize() is called when the scene is loaded.
    @FXML
    public void initialize() {
        populateBuildListComboBox();
    }

    // Fill in a combo box of any PC Build Lists associated with the user.
    private void populateBuildListComboBox() {
        UserAccount user = UserAccount.getInstance();
        // Only try to populate the PC Build List combo box if the user is logged in.
        if (user.isLoggedIn()) {
            // Call upon PostgreSQLController to fill a List<PCBuildList> buildlists for any PC Build Lists the user owns.
            List<PCBuildList> buildLists = PostgreSQLController.getBuildListsForAccount(user.getAccountid());
            // Fill an ObservableList<PCBuildList> with the buildLists
            ObservableList<PCBuildList> observableBuildLists = FXCollections.observableArrayList(buildLists);
            // Fill the combo box with the ObservableList values.
            buildListComboBox.setItems(observableBuildLists);
        }
    }

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

    // Event handler for logout button
    public void logoutAction(ActionEvent event) throws IOException {
        // Get UserAccount instance.
        UserAccount user = UserAccount.getInstance();
        if (user.isLoggedIn()) {
            // If user is logged in, proceed to the start scene
            // Clear the current UserAccount instance
            user.setUsername(null);
            user.setAccountid(null);
            // Update UserAccount login status to be false.
            user.setLoggedIn(false);
            errorLabel.setText("User logout successful.");
            // Change to start scene.
            Main.getInstance().sceneLoad("start.fxml");
            // Close window that the event occurred in
            ((Node) event.getSource()).getScene().getWindow().hide();
        }
        else {
            // User isn't logged in, so logging out does nothing.
            errorLabel.setText("There is no account to logout of; User is already a guest.");
        }
    }

    // Event handler for viewParts button
    public void viewPartsAction(ActionEvent event) throws IOException {
        // Change to pcpartlist scene.
        Main.getInstance().sceneLoad("pcpartlist.fxml");
        // Close window that the event occurred in
        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    // Event handler for viewBuildGuide button
    public void viewBuildGuideAction(ActionEvent event) throws IOException {
        // Change to viewBuildList scene.
        Main.getInstance().sceneLoad("viewBuildList.fxml");
        // Close window that the event occurred in
        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    // Event handler for createBuildList button
    public void createBuildListAction(ActionEvent event) throws IOException {
        UserAccount user = UserAccount.getInstance();
        if (user.isLoggedIn()) {
            // Only change to the createBuildList scene if the user is logged in.
            Main.getInstance().sceneLoad("createBuildList.fxml");
            // Close window that the event occurred in
            ((Node) event.getSource()).getScene().getWindow().hide();
        }
        else {
            // User isn't logged in, so they can't create PC Build Lists.
            errorLabel.setText("To create a PC Build List, the user needs to be logged into an account.");
        }
    }

    // Event handler for loadBuildList button
    public void loadBuildListAction(ActionEvent event) throws IOException {
        // Get the currently selected PCBuildList from the combo box.
        PCBuildList selectedBuildList = buildListComboBox.getValue();
        if (selectedBuildList != null) {
            // If there is a currently selected PCBuildList try sending the PCBuildList to the loadBuildList scene
            try {
                // Change to loadBuildList scene
                Main.getInstance().sceneLoad("loadBuildList.fxml");
                // Get the controller from the loadBuildList scene
                LoadBuildListController controller = (LoadBuildListController) Main.getInstance().getController("loadBuildList.fxml");
                // Call upon the setListId function within the loadBuildListController
                controller.setListId(selectedBuildList.getPcBuildListId());
                // Close window that the event occurred in
                ((Node) event.getSource()).getScene().getWindow().hide();
            } catch (IOException e) {
                // Handle IOException
                errorLabel.setText("Error: Failed to load selected PC Build List.");
            }
        } else {
            // No PCBuildList was selected in the combo box.
            errorLabel.setText("Error: No PC Build List selected.");
        }
    }
}
