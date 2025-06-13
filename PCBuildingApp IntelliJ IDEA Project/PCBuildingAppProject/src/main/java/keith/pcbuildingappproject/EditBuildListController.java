/*
    Name: Keith Holcomb
    Date: May 5th, 2025
    Course: CISS:451 Senior Projects 001
    Description: EditBuildListController is the controller class for the editBuildList.fxml scene.
*/
package keith.pcbuildingappproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class EditBuildListController {

    @FXML
    private Label errorLabel;
    @FXML
    private TextField PCBuildListNameTextField;
    @FXML
    private TextField PCBuildListDescriptionTextField;
    @FXML
    private RadioButton ListTypeNormalRadioButton;
    @FXML
    private RadioButton ListTypeGuideRadioButton;
    @FXML
    private TextField GuideListContentTextField;
    @FXML
    private TextField GuideListCategoryTextField;
    @FXML
    private TextField GuideListThumbnailURLTextField;
    @FXML
    private Label PCGuideContentLabel;
    @FXML
    private Label PCGuideCategoryLabel;
    @FXML
    private Label PCGuideThumbnailURLLabel;
    @FXML
    private Label PCGuidePublishLabel;
    @FXML
    private RadioButton PublishYesRadioButton;
    @FXML
    private RadioButton PublishNoRadioButton;

    private int listId;
    private PCBuildList currentBuildList;

    // Sets the listid and calls populateBuildListDetails()
    public void setListId(int listId) {
        this.listId = listId;
        populateBuildListDetails();
    }

    // Fills the PC Build List details within the scene
    private void populateBuildListDetails() {
        try {
            currentBuildList = PostgreSQLController.getPCBuildListById(this.listId);
            if (currentBuildList != null) {
                PCBuildListNameTextField.setText(currentBuildList.getPcBuildListName());
                PCBuildListDescriptionTextField.setText(currentBuildList.getDescription());
                String listType = currentBuildList.getListType();
                if ("Normal".equalsIgnoreCase(listType)) {
                    ListTypeNormalRadioButton.setSelected(true);
                    // Disable Guide specific fields
                    GuideListContentTextField.setDisable(true);
                    GuideListCategoryTextField.setDisable(true);
                    GuideListThumbnailURLTextField.setDisable(true);
                    PCGuideContentLabel.setDisable(true);
                    PCGuideCategoryLabel.setDisable(true);
                    PCGuideThumbnailURLLabel.setDisable(true);
                    PCGuidePublishLabel.setDisable(true);
                    PublishYesRadioButton.setDisable(true);
                    PublishNoRadioButton.setDisable(true);
                } else if ("Guide".equalsIgnoreCase(listType)) {
                    ListTypeGuideRadioButton.setSelected(true);
                    GuideListContentTextField.setText(currentBuildList.getContent());
                    GuideListCategoryTextField.setText(currentBuildList.getCategory());
                    GuideListThumbnailURLTextField.setText(currentBuildList.getThumbnailURL());
                    // Enable Guide specific fields
                    GuideListContentTextField.setDisable(false);
                    GuideListCategoryTextField.setDisable(false);
                    GuideListThumbnailURLTextField.setDisable(false);
                    PCGuideContentLabel.setDisable(false);
                    PCGuideCategoryLabel.setDisable(false);
                    PCGuideThumbnailURLLabel.setDisable(false);
                    PCGuidePublishLabel.setDisable(false);
                    PublishYesRadioButton.setDisable(false);
                    PublishNoRadioButton.setDisable(false);
                }
            } else {
                errorLabel.setText("Error: Build list with ID " + this.listId + " not found.");
            }
        } catch (SQLException e) {
            // Error
            errorLabel.setText("Database error: " + e.getMessage());
        }
    }

    // Grabs all the control fields, validates the input, then attempt to update the PC Build List
    @FXML
    void saveButtonAction(ActionEvent event) throws IOException {
        errorLabel.setText("");
        // Get user input
        String pcBuildListName = PCBuildListNameTextField.getText();
        String description = PCBuildListDescriptionTextField.getText();
        String listType = ListTypeNormalRadioButton.isSelected() ? "Normal" : "Guide";
        String content = GuideListContentTextField.getText();
        String category = GuideListCategoryTextField.getText();
        String thumbnailUrl = GuideListThumbnailURLTextField.getText();
        // Get whether the list type is guide and publish yes are both true
        boolean publish = ListTypeGuideRadioButton.isSelected() && PublishYesRadioButton.isSelected();

        // Input Validation (same as create)
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
        // Check for uniqueness of Build List Name (if it has changed)
        if (!pcBuildListName.equals(currentBuildList.getPcBuildListName()) && !PostgreSQLController.isBuildListNameUnique(pcBuildListName)) {
            errorLabel.setText("Build List Name Already Exists: Choose a unique name.");
            return;
        }
        // Update PCBuildList object
        currentBuildList.setPcBuildListName(pcBuildListName);
        currentBuildList.setDescription(description);
        currentBuildList.setModifiedDate(LocalDate.now());
        currentBuildList.setListType(listType);
        currentBuildList.setContent(content);
        currentBuildList.setCategory(category);
        currentBuildList.setThumbnailURL(thumbnailUrl);
        currentBuildList.setPublish(publish);
        // Return whether the PC Build List was successfully updated or not
        boolean updated = PostgreSQLController.updatePCBuildList(currentBuildList);
        if (updated) {
            errorLabel.setText("Build List updated successfully.");
            Main.getInstance().sceneLoad("loadBuildList.fxml");
            LoadBuildListController controller = (LoadBuildListController) Main.getInstance().getController("loadBuildList.fxml");
            // Pass the listid to the controller
            controller.setListId(listId);
            // Close the window the event occurred from
            ((Node) event.getSource()).getScene().getWindow().hide();
        } else {
            errorLabel.setText("Failed to update PC Build List.");
        }
    }

    // Initialize occurs when the scene is first loaded
    @FXML
    public void initialize() {
        // Add listeners to handle the enabling/disabling of Guide-specific fields
        ListTypeNormalRadioButton.setOnAction(event -> {
            GuideListContentTextField.setDisable(true);
            GuideListCategoryTextField.setDisable(true);
            GuideListThumbnailURLTextField.setDisable(true);
            PCGuideContentLabel.setDisable(true);
            PCGuideCategoryLabel.setDisable(true);
            PCGuideThumbnailURLLabel.setDisable(true);
            PCGuidePublishLabel.setDisable(true);
            PublishYesRadioButton.setDisable(true);
            PublishNoRadioButton.setDisable(true);
        });
        ListTypeGuideRadioButton.setOnAction(event -> {
            GuideListContentTextField.setDisable(false);
            GuideListCategoryTextField.setDisable(false);
            GuideListThumbnailURLTextField.setDisable(false);
            PCGuideContentLabel.setDisable(false);
            PCGuideCategoryLabel.setDisable(false);
            PCGuideThumbnailURLLabel.setDisable(false);
            PCGuidePublishLabel.setDisable(false);
            PublishYesRadioButton.setDisable(false);
            PublishNoRadioButton.setDisable(false);
        });
    }

    // Cancel button returns user to the loadBuildList scene.
    public void cancelButtonAction(ActionEvent event) throws IOException {
        Main.getInstance().sceneLoad("loadBuildList.fxml");
        LoadBuildListController controller = (LoadBuildListController) Main.getInstance().getController("loadBuildList.fxml");
        // Pass the listid to the controller
        controller.setListId(listId);
        // Close the window the event occurred in
        ((Node) event.getSource()).getScene().getWindow().hide();
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
