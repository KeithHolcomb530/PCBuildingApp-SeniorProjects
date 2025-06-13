/*
    Name: Keith Holcomb
    Date: May 5th, 2025
    Course: CISS:451 Senior Projects 001
    Description: CreateBuildListController is the controller class for the createBuildList.fxml scene.
*/
package keith.pcbuildingappproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.time.LocalDate;

public class CreateBuildListController {

    @FXML
    private TextField PCBuildListNameTextField;
    @FXML
    private TextField PCBuildListDescriptionTextField;
    @FXML
    private RadioButton ListTypeNormalRadioButton;
    @FXML
    private RadioButton ListTypeGuideRadioButton;
    @FXML
    private Label PCGuideContentLabel;
    @FXML
    private Label PCGuideCategoryLabel;
    @FXML
    private Label PCGuideThumbnailURLLabel;
    @FXML
    private TextField GuideListContentTextField;
    @FXML
    private TextField GuideListCategoryTextField;
    @FXML
    private TextField GuideListThumbnailURLTextField;
    @FXML
    private Label errorLabel;

    @FXML
    public void initialize() {
        errorLabel.setText("");
        // Initially hide the guide fields
        PCGuideContentLabel.setDisable(true);
        PCGuideCategoryLabel.setDisable(true);
        PCGuideThumbnailURLLabel.setDisable(true);
        GuideListContentTextField.setDisable(true);
        GuideListCategoryTextField.setDisable(true);
        GuideListThumbnailURLTextField.setDisable(true);

        // Add listeners to the radio buttons
        ListTypeGuideRadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // Show guide fields
                PCGuideContentLabel.setDisable(false);
                PCGuideCategoryLabel.setDisable(false);
                PCGuideThumbnailURLLabel.setDisable(false);
                GuideListContentTextField.setDisable(false);
                GuideListCategoryTextField.setDisable(false);
                GuideListThumbnailURLTextField.setDisable(false);
            } else {
                // Hide guide fields
                PCGuideContentLabel.setDisable(true);
                PCGuideCategoryLabel.setDisable(true);
                PCGuideThumbnailURLLabel.setDisable(true);
                GuideListContentTextField.setDisable(true);
                GuideListCategoryTextField.setDisable(true);
                GuideListThumbnailURLTextField.setDisable(true);

                // Clear guide TextFields when hidden
                GuideListContentTextField.clear();
                GuideListCategoryTextField.clear();
                GuideListThumbnailURLTextField.clear();
            }
        });
    }

    // Cancel button returns user to the main menu scene.
    public void cancelButtonAction(ActionEvent event) throws IOException {
        Main.getInstance().sceneLoad("mainMenu.fxml");
        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    // create button checks user input and calls a function from the PostgreSQLController to make a new entry to the Build List table.
    public void createButtonAction(ActionEvent event) throws IOException {
        errorLabel.setText("");
        // Get user input
        String pcBuildListName = PCBuildListNameTextField.getText();
        String description = PCBuildListDescriptionTextField.getText();
        String listType = ListTypeNormalRadioButton.isSelected() ? "Normal" : "Guide";
        String content = GuideListContentTextField.getText();
        String category = GuideListCategoryTextField.getText();
        String thumbnailUrl = GuideListThumbnailURLTextField.getText();
        // Input Validation
        if (!validateBuildListName(pcBuildListName)) {
            errorLabel.setText("Invalid Build List Name: Must be 8 to 64 characters.");
            return;
        }
        if (description != null && description.length() > 255) {
            errorLabel.setText("Invalid Description: Must be 255 characters or less.");
            return;
        }
        if (listType.equals("Guide")) {
            if (content != null && content.length() > 64) {
                errorLabel.setText("Invalid Content: Must be 64 characters or less.");
                return;
            }
            if (category != null && category.length() > 64) {
                errorLabel.setText("Invalid Category: Must be 64 characters or less.");
                return;
            }
            if (thumbnailUrl != null && thumbnailUrl.length() > 255) {
                errorLabel.setText("Invalid Thumbnail URL: Must be 255 characters or less.");
                return;
            }
        }
        // Check for uniqueness of Build List Name (using PostgreSQLController)
        if (!PostgreSQLController.isBuildListNameUnique(pcBuildListName)) {
            errorLabel.setText("Build List Name Already Exists: Choose a unique name.");
            return;
        }
        // Create PCBuildList object and add to database
        PCBuildList pcBuildList = new PCBuildList();
        // Grab the accountID of the current user account instance.
        pcBuildList.setAccountId(UserAccount.getInstance().getAccountid());
        pcBuildList.setPcBuildListName(pcBuildListName);
        pcBuildList.setDescription(description);
        pcBuildList.setCreationDate(LocalDate.now());
        pcBuildList.setModifiedDate(LocalDate.now());
        pcBuildList.setTotalPrice(0.0);
        pcBuildList.setListType(listType);
        pcBuildList.setContent(content);
        pcBuildList.setCategory(category);
        pcBuildList.setThumbnailURL(thumbnailUrl);
        // Get the generated PC Build List listid after created the pcbuildlist entry
        int listId = PostgreSQLController.addPCBuildList(pcBuildList);
        // If the PC Build List didn't fail
        if (listId != -1) {
            errorLabel.setText("Build List created successfully.");
            // Try to load the loadBuildList scene with the new PC Build List.
            try {
                Main.getInstance().sceneLoad("loadBuildList.fxml");
                LoadBuildListController controller = (LoadBuildListController) Main.getInstance().getController("loadBuildList.fxml");
                // Pass the listid to the controller
                controller.setListId(listId);
                // Close window the event was called in.
                ((Node) event.getSource()).getScene().getWindow().hide();
            } catch (Exception e) {
                errorLabel.setText("Failed to load loadBuildList scene.");
            }
        } else {
            errorLabel.setText("Failed to create PC Build List.");
        }
    }

    // Returns boolean for whether a PC Build List name is valid or not
    private boolean validateBuildListName(String name) {
        return name != null && name.length() >= 8 && name.length() <= 64;
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
}