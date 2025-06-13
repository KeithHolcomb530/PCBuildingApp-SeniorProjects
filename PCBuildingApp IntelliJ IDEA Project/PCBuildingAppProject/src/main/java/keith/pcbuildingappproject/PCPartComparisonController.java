/*
    Name: Keith Holcomb
    Date: May 5th, 2025
    Course: CISS:451 Senior Projects 001
    Description: PCPartComparisonController is the controller class for the pcpartcomparison.fxml scene.
*/
package keith.pcbuildingappproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PCPartComparisonController {

    @FXML
    private Label errorLabel;
    @FXML
    private GridPane comparisonGrid;

    private List<PCpart> partsToCompare;

    // Getter and setter for partsToCompare list.
    public List<PCpart> getPartToCompare(List<PCpart> parts) { return partsToCompare; }
    public void setPartsToCompare(List<PCpart> parts) { this.partsToCompare = parts; }

    // Display each part being compared by type
    public void displayComparison() {
        // Make sure there are at least to parts to compare, otherwise it isn't a comparison
        if (partsToCompare != null && partsToCompare.size() >= 2) {
            // Group parts by type in a HashMap<>()
            Map<String, List<PCpart>> partsByType = new HashMap<>();
            // For each part in a part type
            for (PCpart part : partsToCompare) {
                // Add each part to an ArrayList<>()
                partsByType.computeIfAbsent(part.getPartType(), k -> new ArrayList<>()).add(part);
            }
            // Clear any existing content in the comparison GridPane
            comparisonGrid.getChildren().clear();
            comparisonGrid.getRowConstraints().clear();
            // GridPane row index.
            int rowIndex = 0;
            // Iterate through each part type
            for (Map.Entry<String, List<PCpart>> typeEntry : partsByType.entrySet()) {
                String partType = typeEntry.getKey();
                List<PCpart> parts = typeEntry.getValue();
                // Add whitespace between GridPane entries using a blank label
                comparisonGrid.getRowConstraints().add(new RowConstraints());
                rowIndex++;
                Label blankLabelBefore = new Label();
                GridPane.setColumnSpan(blankLabelBefore, 8); // Span across all columns
                comparisonGrid.add(blankLabelBefore, 0, rowIndex - 1);
                // Add a heading for each Part Type
                Label typeHeading = new Label(partType);
                GridPane.setHalignment(typeHeading, HPos.CENTER); // Center the heading
                GridPane.setColumnSpan(typeHeading, 8); // Span across all columns
                comparisonGrid.add(typeHeading, 0, rowIndex);
                comparisonGrid.getRowConstraints().add(new RowConstraints());
                rowIndex++;
                // Add whitespace after each Part Type heading using a blank label
                comparisonGrid.getRowConstraints().add(new RowConstraints());
                rowIndex++;
                Label blankLabelAfter = new Label();
                GridPane.setColumnSpan(blankLabelAfter, 8); // Span across all columns
                comparisonGrid.add(blankLabelAfter, 0, rowIndex - 1);
                // Find the maximum number of specifications for the current part type
                int maxSpecs = 0;
                for (PCpart part : parts) {
                    if (part.getSpecifications() != null) {
                        maxSpecs = Math.max(maxSpecs, part.getSpecifications().size());
                    }
                }
                // Add row constraints for the current type based on how many specifications there are
                for (int i = 0; i < maxSpecs; i++) {
                    comparisonGrid.getRowConstraints().add(new RowConstraints());
                }
                // GridPane column index.
                int columnIndex = 0;
                // Display parts of the current type
                for (PCpart part : parts) {
                    // Add Part Name as a Label
                    Label partNameLabel = new Label(part.getPartName());
                    comparisonGrid.add(partNameLabel, columnIndex, rowIndex);
                    GridPane.setColumnSpan(partNameLabel, 2);
                    // Add a Label for every specification to display
                    Map<String, Object> specs = part.getSpecifications();
                    if (specs != null) {
                        int specRowIndex = rowIndex + 1;
                        for (Map.Entry<String, Object> entry : specs.entrySet()) {
                            Label keyLabel = new Label(entry.getKey() + ":");
                            Label valueLabel = new Label(entry.getValue().toString());
                            comparisonGrid.add(keyLabel, columnIndex, specRowIndex);
                            comparisonGrid.add(valueLabel, columnIndex + 1, specRowIndex);
                            // Iterate specification row
                            specRowIndex++;
                            // Font size
                            keyLabel.setFont(Font.font(14));
                            valueLabel.setFont(Font.font(14));
                        }
                    }
                    // Each part takes up 2 columns, so to iterate, column index needs to move by 2
                    columnIndex += 2;
                    // Font size
                    partNameLabel.setFont(Font.font(14));
                }
                // Move to the next row once the Part Type is done
                rowIndex += maxSpecs + 1;
                // Font size for heading
                typeHeading.setFont(Font.font(18));
            }
            // Add whitespace row after the last comparison as a blank Label
            comparisonGrid.getRowConstraints().add(new RowConstraints());
            Label blankLabelLast = new Label();
            GridPane.setColumnSpan(blankLabelLast, 8); // Span across all columns
            comparisonGrid.add(blankLabelLast, 0, rowIndex);
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
}
