/*
    Name: Keith Holcomb
    Date: May 5th, 2025
    Course: CISS:451 Senior Projects 001
    Description: LoadBuildListController is the controller class for the loadBuildList.fxml scene.
*/
package keith.pcbuildingappproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class LoadBuildListController {

    @FXML
    private VBox partsVBox;
    @FXML
    private Label errorLabel;
    @FXML
    private ComboBox<PCBuildList> buildListComboBox;

    private int listId;

    // Initialize() is called whenever the scene is loaded
    @FXML
    public void initialize() {
        populateBuildListComboBox();
    }

    // Get the list of PC Build Lists associated with the current user and populate the ComboBox
    private void populateBuildListComboBox() {
        UserAccount user = UserAccount.getInstance();
        if (user.isLoggedIn()) {
            List<PCBuildList> buildLists = PostgreSQLController.getBuildListsForAccount(user.getAccountid());
            ObservableList<PCBuildList> observableBuildLists = FXCollections.observableArrayList(buildLists);
            buildListComboBox.setItems(observableBuildLists);
        }
    }

    // Set the listid, get the parts associated with the list, and display those parts
    public void setListId(int listId) {
        this.listId = listId;
        List<PCpart> parts = PostgreSQLController.getPartsForBuildList(listId);
        displayParts(parts);
    }

    // Displays the PCparts
    private void displayParts(List<PCpart> parts) {
        partsVBox.getChildren().clear();
        PCBuildList buildList = PostgreSQLController.getBuildListById(listId);
        if (buildList != null) {
            // Add the PC Build Lists details
            addBuildListDetails(buildList);
            // Add the TotalPrice display
            addTotalAndSeparator();
        }
        // Add the PC parts to the display
        addPCparts(parts);
        // Check the compatibility of the PC parts in the list
        checkCompatibility(parts);
    }

    // Adds the PC Build Lists details to the display
    private void addBuildListDetails(PCBuildList buildList) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        VBox nameButtonBox = new VBox(5);
        nameButtonBox.setAlignment(Pos.CENTER);
        Label nameLabel = new Label(buildList.getPcBuildListName() + " PC Build List");
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        // Edit Details Button control changes the scene to the editBuildList.fxml scene
        Button editButton = new Button("Edit Details");
        editButton.setOnAction(event -> {
            try {
                Main.getInstance().sceneLoad("editBuildList.fxml");
                EditBuildListController controller = (EditBuildListController) Main.getInstance().getController("editBuildList.fxml");
                // Pass the listid to the controller
                controller.setListId(this.listId);
                // Close the window the event occurred in
                ((Node) event.getSource()).getScene().getWindow().hide();
            } catch (IOException e) {
                errorLabel.setText("Error: Could not load edit details scene.");
            }
        });
        nameButtonBox.getChildren().addAll(nameLabel, editButton);
        partsVBox.setAlignment(Pos.CENTER);
        partsVBox.getChildren().addAll(nameButtonBox, new Label(""));
        Label typeLabel = createLabel("Build List Type: " + buildList.getListType());
        Label descLabel = createLabel("Build List Description: " + buildList.getDescription());
        Label creationLabel = createLabel("Creation Date: " + buildList.getCreationDate().format(formatter));
        Label modifiedLabel = createLabel("Modified Date: " + buildList.getModifiedDate().format(formatter));
        partsVBox.getChildren().addAll(typeLabel, descLabel, creationLabel, modifiedLabel);
        if ("Guide".equals(buildList.getListType())) {
            Label contentLabel = createLabel("Guide Content: " + buildList.getContent());
            Label categoryLabel = createLabel("Guide Category: " + buildList.getCategory());
            Label thumbnailLabel = createLabel("Guide Thumbnail URL: " + buildList.getThumbnailURL());
            Label publishLabel = createLabel("Published: " + buildList.isPublish());
            partsVBox.getChildren().addAll(contentLabel, categoryLabel, thumbnailLabel, publishLabel);
        }
    }

    // Any Label created using this method has it's font weight and font size set here
    private Label createLabel(String text) {
        return createLabel(text, FontWeight.NORMAL, 14);
    }

    // Any Label created using this override method aligns to the center and takes up an entire line
    private Label createLabel(String text, FontWeight weight, int size) {
        Label label = new Label(text);
        label.setFont(Font.font("System", weight, size));
        label.setAlignment(Pos.CENTER);
        label.setMaxWidth(Double.MAX_VALUE);
        return label;
    }

    // Adds the TotalPrice HBox and a seperator
    private void addTotalAndSeparator() {
        HBox totalPriceBox = createTotalPriceBox();
        partsVBox.getChildren().addAll(totalPriceBox, new Label(""), new Separator(), new Label(""));
    }

    // Creates the TotalPrice HBox
    private HBox createTotalPriceBox() {
        HBox box = new HBox(10);
        Label priceLabel = new Label("Total Price: $" + String.format("%.2f", PostgreSQLController.getTotalPrice(listId)));
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(event -> refreshBuildList());
        box.getChildren().addAll(priceLabel, refreshButton);
        box.setAlignment(Pos.CENTER);
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    // Adds the PC part objects to the PC Build List display
    private void addPCparts(List<PCpart> parts) {
        String[] partTypes = {"CPU", "Motherboard", "CPU Cooler", "RAM", "Storage", "GPU", "Power Supply", "Case", "Case Fan"};
        for (String partType : partTypes) {
            Label typeLabel = createLabel(partType, FontWeight.BOLD, 16);
            partsVBox.getChildren().add(typeLabel);

            List<PCpart> partsOfType = parts.stream()
                    .filter(part -> part.getPartType().equals(partType))
                    .collect(Collectors.toList());
            if (!partsOfType.isEmpty()) {
                for (PCpart part : partsOfType) {
                    HBox partBox = createPartBox(part, partType);
                    partsVBox.getChildren().addAll(partBox, new Label(""));
                }
            } else {
                HBox addButtonBox = createAddButtonBox(partType);
                partsVBox.getChildren().addAll(addButtonBox, new Label(""));
            }
        }
    }

    // Creates an HBox for each PC part
    private HBox createPartBox(PCpart part, String partType) {
        HBox box = new HBox(10);
        Label partLabel = new Label(part.getPartName());
        Button removeButton = new Button("Remove " + partType);
        removeButton.setOnAction(event -> removePart(part, event));
        box.getChildren().addAll(partLabel, removeButton);
        box.setAlignment(Pos.CENTER);
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    // Creates a button for any empty PC part slots in a PC Build List
    private HBox createAddButtonBox(String partType) {
        HBox box = new HBox();
        Button addButton = new Button("Add " + partType);
        addButton.setOnAction(event -> handleAddPart(partType, event));
        box.getChildren().add(addButton);
        box.setAlignment(Pos.CENTER);
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    // Creates the button to refresh the PC Build List display
    private void refreshBuildList() {
        errorLabel.setText("");
        List<PCpart> parts = PostgreSQLController.getPartsForBuildList(listId);
        displayParts(parts);
    }

    // Updates the total price of the pcbuildlist database table entry
    private void updateTotalPriceInDatabase(double totalPrice) {
        errorLabel.setText("");
        boolean updated = PostgreSQLController.updateTotalPrice(listId, totalPrice);
        if (!updated) {
            errorLabel.setText("Error: Failed to update PC Build List total price.");
        }
    }

    // Removes the PC part from the PC Build List at the database and display levels
    private void removePart(PCpart part, ActionEvent event) {
        errorLabel.setText("");
        boolean removed = PostgreSQLController.removePartFromList(listId, part.getPartID());
        if (removed) {
            // Recalculate total price based on remaining parts.
            List<PCpart> remainingParts = PostgreSQLController.getPartsForBuildList(listId);
            double newTotalPrice = 0.0;
            for (PCpart remainingPart : remainingParts) {
                newTotalPrice += remainingPart.getPrice();
            }
            // Update the database with the new total price.
            updateTotalPriceInDatabase(newTotalPrice);
            PostgreSQLController.updateModifiedDate(listId);
            // Refresh the display
            refreshBuildList();
        } else {
            errorLabel.setText("Error: Failed to remove PC part from PC Build List.");
        }
    }

    // Adds any compatibility error messages to be displayed
    private void checkCompatibility(List<PCpart> parts) {
        if (parts == null || parts.isEmpty()) {
            return;
        }

        Map<String, List<String>> errorMessages = new HashMap<>();
        CompatibilityChecker compatibilityChecker = new CompatibilityChecker();

        compatibilityChecker.cpuCompatibility(parts, errorMessages);
        compatibilityChecker.motherboardCompatibility(parts, errorMessages);
        compatibilityChecker.cpuCoolerCompatibility(parts, errorMessages);
        compatibilityChecker.ramCompatibility(parts, errorMessages);
        compatibilityChecker.gpuCompatibility(parts, errorMessages);
        compatibilityChecker.psuCompatibility(parts, errorMessages);
        compatibilityChecker.caseCompatibility(parts, errorMessages);
        compatibilityChecker.caseFanCompatibility(parts, errorMessages);
        // Update UI with compatibility issues
        updateCompatibilityUI(errorMessages);
    }

    // Updates the PC Build List display to show any error messages from compatibility checks
    private void updateCompatibilityUI(Map<String, List<String>> errorMessages) {
        // Store nodes to add later
        Map<Label, List<Label>> labelErrorMap = new HashMap<>();
        // Store error labels for each part type label
        for (javafx.scene.Node node : partsVBox.getChildren()) {
            if (node instanceof Label) {
                Label label = (Label) node;
                String labelText = label.getText().replaceAll("\\s*\\(\\d+\\)$", "");
                if (errorMessages.containsKey(labelText)) {
                    List<String> errors = errorMessages.get(labelText);
                    label.setText(labelText + " (" + errors.size() + ")");
                    label.setTextFill(Color.RED);
                    label.setFont(Font.font("System", FontWeight.BOLD, 16));
                    List<Label> errorLabels = new ArrayList<>();
                    for (String error : errors) {
                        Label errorLabel = new Label("  - " + error);
                        errorLabel.setTextFill(Color.RED);
                        errorLabel.setAlignment(Pos.CENTER);
                        errorLabel.setMaxWidth(Double.MAX_VALUE);
                        errorLabels.add(errorLabel);
                    }
                    labelErrorMap.put(label, errorLabels);
                }
            }
        }
        // Add error labels after part type labels
        for (Map.Entry<Label, List<Label>> entry : labelErrorMap.entrySet()) {
            Label partTypeLabel = entry.getKey();
            List<Label> errorLabels = entry.getValue();

            int insertIndex = partsVBox.getChildren().indexOf(partTypeLabel) + 1;
            // Insert after part type label
            for (Label errorLabel : errorLabels) {
                partsVBox.getChildren().add(insertIndex, errorLabel);
                insertIndex++; // Increment for each error label
            }
        }
    }

    // When any Add part button is pressed, change the scene to the pcpartlist.fxml scene filtered with the part type
    private void handleAddPart(String partType, ActionEvent event) {
        errorLabel.setText("");
        try {
            Main.getInstance().sceneLoad("pcpartlist.fxml");
            // Load the pcpartlist.fxml scene
            PCPartListController controller = (PCPartListController) Main.getInstance().getController("pcpartlist.fxml");
            // Pass the part type to the controller
            controller.setPartType(partType);
            // Close the window the event occurred in
            ((Node) event.getSource()).getScene().getWindow().hide();
        } catch (Exception e) {
            errorLabel.setText("Error: Failed to add PC part");
        }
    }

    // Load a different PC Build List to the display
    public void loadBuildListAction(ActionEvent event) throws IOException {
        errorLabel.setText("");
        PCBuildList selectedBuildList = buildListComboBox.getValue();
        if (selectedBuildList != null) {
            setListId(selectedBuildList.getPcBuildListId());
        } else {
            errorLabel.setText("Error: No PC Build List was selected.");
        }
    }

    // delete the current build list, then return the user to the mainMenu.fxml scene.
    public void deleteBuildListAction(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Build List");
        alert.setHeaderText("Confirm Deletion");
        alert.setContentText("Are you sure you want to delete this build list?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean deleted = PostgreSQLController.deleteBuildList(listId);
            if (deleted) {
                Main.getInstance().sceneLoad("mainMenu.fxml");
                ((Node) event.getSource()).getScene().getWindow().hide();
            } else {
                errorLabel.setText("Error: Failed to delete the PC Build List.");
            }
        }
    }

    // Returns the user to the mainMenu.fxml scene
    public void mainMenuAction(ActionEvent event) throws IOException {
        Main.getInstance().sceneLoad("mainMenu.fxml");
        ((Node) event.getSource()).getScene().getWindow().hide();
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