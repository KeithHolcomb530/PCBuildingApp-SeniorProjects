/*
    Name: Keith Holcomb
    Date: May 5th, 2025
    Course: CISS:451 Senior Projects 001
    Description: PCPartListController is the controller class for the pcpartlist.fxml scene.
*/
package keith.pcbuildingappproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;
import javafx.scene.control.CheckBox;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PCPartListController {

    @FXML
    private Label errorLabel;
    @FXML
    private ScrollPane partsScrollPane;
    @FXML
    private TextField searchBar;
    @FXML
    // buildListComboBox will hold any PC Build Lists that the user owns.
    private ComboBox<PCBuildList> buildListComboBox;
    // allData ObservableList<PCpart> will hold all PCpart in the database as an observableArrayList().
    private ObservableList<PCpart> allData = FXCollections.observableArrayList();
    private String partType;

    // setPartType filters the PCPartList to only contain parts of the given type.
    // This is used as a way to initialize the scene when trying to add a part type to a list.
    public void setPartType(String partType) {
        // Set current partType to the given one.
        this.partType = partType;
        // Initialize the scene.
        initialize();
    }

    // Event handler for compare button
    @FXML
    public void compareAction(ActionEvent event) throws IOException {
        // Get the partsTable from the partsScrollPane
        TableView<PCpart> partsTable = (TableView<PCpart>) partsScrollPane.getContent();
        // Convert the partsTable to an ObservableList
        ObservableList<PCpart> items = partsTable.getItems();
        // Create a List<PCpart> to hold any selected PC parts for comparison
        List<PCpart> selectedParts = new ArrayList<>();

        // Get selected PC parts
        for (PCpart part : items) {
            if (part.isSelected()) {
                // Add selected PC parts to the selectedParts list for comparison
                selectedParts.add(part);
            }
        }

        // Check if at least two of the selected parts are comparable
        if (!isValidComparison(selectedParts)) {
            errorLabel.setText("Select at least two parts of the same type.");
            return;
        }

        // Implementation error. This is an instance where Main's sceneLoad was not used to change scenes.
        // Load comparison scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("pcpartcomparison.fxml")); // Replace "comparison.fxml"
        Parent root = loader.load();

        // Get the controller
        PCPartComparisonController comparisonController = loader.getController();

        // Pass the selectedParts list to the comparison scene.
        comparisonController.setPartsToCompare(selectedParts);
        comparisonController.displayComparison();

        // Set the stage and display the scene.
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("PC Building App");
        stage.show();
        // The comparison scene should NOT close the previous scene.
    }

    // Check that the user has selected at least 2 parts that can be compared
    private boolean isValidComparison(List<PCpart> selectedParts) {
        // The user needs to select at least two parts
        if (selectedParts.size() < 2) {
            return false;
        }
        // Create a HashMap<>() for the parts by their type.
        Map<String, List<PCpart>> partsByType = new HashMap<>();
        // For each selected part, fill the HashMap by each part type.
        for (PCpart part : selectedParts) {
            partsByType.computeIfAbsent(part.getPartType(), k -> new ArrayList<>()).add(part);
        }
        // Check each part type in the HashMap<>(), if there are at least 2 entries in a single part type, return true
        for (List<PCpart> parts : partsByType.values()) {
            // If there are two parts in a single part type in the HashMap<>().
            if (parts.size() >= 2) {
                return true;
            }
        }
        // Of selected parts, there are none that are comparable to each other.
        return false;
    }

    // Initialize is called when the scene is first loaded.
    @FXML
    public void initialize() {
        // Clear the errorLabel of any existing errors.
        errorLabel.setText("");
        // Calls upon PostgreSQLController to fill buildListComboBox with user owned PC Build Lists.
        loadBuildLists();
        // Clear allData
        allData.clear();
        // If the partType is currently empty
        if (partType == null || partType.isEmpty()) {
            // Call on PostgreSQLController to add all PC parts in the database to the allData list.
            allData.addAll(PostgreSQLController.getPCpartsFromDatabase(null));
        } else {
            // If a partType is specified, call on PostgreSQLController to add all PC parts of that type from the database to the allData list.
            allData.addAll(PostgreSQLController.getPCpartsFromDatabase(partType));
        }

        // Create the TableView
        TableView<PCpart> partsTable = new TableView<>();

        // Part Id Column
        TableColumn<PCpart, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("partID"));
        // This column is used for sorting, not for the user to view
        idColumn.setVisible(false);

        // Part Name column
        TableColumn<PCpart, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("partName"));

        // Part Type column
        TableColumn<PCpart, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("partType"));

        // Part Manufacturer column
        TableColumn<PCpart, String> manufacturerColumn = new TableColumn<>("Manufacturer");
        manufacturerColumn.setCellValueFactory(new PropertyValueFactory<>("manufacturer"));

        // Part Model column
        TableColumn<PCpart, String> modelColumn = new TableColumn<>("Model");
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));

        // Part Price column
        TableColumn<PCpart, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Part Checkbox column
        // These checkboxes allow parts to be selected for either adding to a list or comparing
        TableColumn<PCpart, Boolean> selectColumn = new TableColumn<>("Select");
        // Fill the column with a checkbox
        selectColumn.setCellFactory(col -> {
            TableCell<PCpart, Boolean> cell = new TableCell<>() {
                final CheckBox checkBox = new CheckBox();
                {
                    // When the checkbox is clicked on.
                    checkBox.setOnAction(event -> {
                        // Get the PC part
                        PCpart pcpart = getTableView().getItems().get(getIndex());
                        // Set the PC part in the row to be selected
                        pcpart.setSelected(checkBox.isSelected());
                    });
                }

                // This is an override of how the checkbox is visually updated.
                // This fixes an issue with the checkbox visually not updating.
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    // Although overriding the checkbox, still need it to do its normal functionality.
                    super.updateItem(item, empty);
                    // If the checkbox is empty, don't show a graphic (no checkmark).
                    if (empty) {
                        setGraphic(null);
                    } else {
                        // Get the PC part
                        PCpart pcpart = getTableView().getItems().get(getIndex());
                        // Set the Checkbox to be selected if the PC part itself is set to be selected.
                        checkBox.setSelected(pcpart.isSelected());
                        // Since the checkbox isn't empty, update the graphic (add the checkmark).
                        setGraphic(checkBox);
                    }
                }
            };
            return cell;
        });
        selectColumn.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());

        // Add columns to the TableView
        partsTable.getColumns().addAll(idColumn, nameColumn, typeColumn, manufacturerColumn, modelColumn, priceColumn, selectColumn);

        // Specific width and height of the table.
        partsTable.setPrefWidth(1074);
        partsTable.setPrefHeight(559);

        // Wrapping ensures that the searchBar, checkboxes, and TableView sorting all work.
        // Wrap the ObservableList in a FilteredList
        FilteredList<PCpart> filteredData = new FilteredList<>(allData, p -> true);
        // Wrap the FilteredList in a SortedList.
        SortedList<PCpart> sortedData = new SortedList<>(filteredData);
        // Bind the SortedList's comparator to the TableView's comparator.
        sortedData.comparatorProperty().bind(partsTable.comparatorProperty());
        // Add the listener to the search bar
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(pcpart -> {
                // Make sure an empty search resets the TableView filter.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                // Make it so the searchBar is not case-sensitive.
                String lowerCaseFilter = newValue.toLowerCase();
                if (pcpart.getPartName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (pcpart.getPartType().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (pcpart.getManufacturer().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (pcpart.getModel().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });
        // Set the sorted list to the TableView
        partsTable.setItems(sortedData);
        // Set the hidden ID column as the default sort column.
        partsTable.getSortOrder().add(idColumn);
        // Sort the TableView
        partsTable.sort();
        // Add the TableView to the ScrollPane
        partsScrollPane.setContent(partsTable);
    }

    // Fills the buildListComboBox with PC Build Lists the user owns.
    private void loadBuildLists() {
        // Get current user instance
        UserAccount user = UserAccount.getInstance();
        // If the user is logged in
        if(user.isLoggedIn()){
            // Call on PostgreSQLController to get the PC Build Lists that the user owns via the user's accountId
            List<PCBuildList> buildLists = PostgreSQLController.getBuildListsForAccount(user.getAccountid());
            // Create an ObservableList to hold an ArrayList of all the PC Build Lists the user owns.
            ObservableList<PCBuildList> observableBuildLists = FXCollections.observableArrayList(buildLists);
            // Populate the ComboBox using the ObservableList
            buildListComboBox.setItems(observableBuildLists);
        }
    }

    // Event handler for addToList button
    @FXML
    public void addToListAction(ActionEvent event) {
        // Clear error label of any previous errors
        errorLabel.setText("");
        // Get the PC Build List selected from the buildListComboBox
        PCBuildList selectedBuildList = buildListComboBox.getValue();
        // If the selectedBuildList exists
        if (selectedBuildList != null) {
            // For all the PC parts in the PC parts list
            for (PCpart part : ((TableView<PCpart>) partsScrollPane.getContent()).getItems()) {
                // If a PC part's checkbox is selected
                if (part.isSelected()) {
                    // Call on PostgreSQLController to add the PC part to the PC Build List selected in the ComboBox.
                    PostgreSQLController.addPartToList(selectedBuildList.getPcBuildListId(), part.getPartID());
                }
            }
            // Call upon PostgreSQLController to get the PC parts within the list
            List<PCpart> parts = PostgreSQLController.getPartsForBuildList(selectedBuildList.getPcBuildListId());
            double totalPrice = 0.0;
            // For each of the PC parts in within the list
            for (PCpart pcpart : parts){
                // Get each PC parts price and add it to a totalPrice
                totalPrice += pcpart.getPrice();
            }

            // Call on PostgreSQLController to update the totalPrice to the new totalPrice
            PostgreSQLController.updateTotalPrice(selectedBuildList.getPcBuildListId(), totalPrice);
            // Call on PostgreSQLController to update the modifiedDate to the current date.
            PostgreSQLController.updateModifiedDate(selectedBuildList.getPcBuildListId());

            try {
                // Change scene to the loadBuildList scene
                Main.getInstance().sceneLoad("loadBuildList.fxml");
                // Set the controller to loadBuildList's controller
                LoadBuildListController controller = (LoadBuildListController) Main.getInstance().getController("loadBuildList.fxml");
                // Pass the PC Build List's id to the controller.
                controller.setListId(selectedBuildList.getPcBuildListId());
                // Close window the event was called from
                ((Node) event.getSource()).getScene().getWindow().hide();
            } catch (IOException e) {
                // Error while trying to change to the loadBuildList scene
                errorLabel.setText("Error: Couldn't load into PC Build List scene.");
            }
        } else {
            // Hit the addToList button without selecting a PC Build List.
            errorLabel.setText("Error: No PC Build List selected.");
        }
    }

    // Event handler for the returnToList button
    @FXML
    public void returnToListAction(ActionEvent event) {
        // Clear error label of previous errors.
        errorLabel.setText("");
        // Get PC Build List from ComboBox selection
        PCBuildList selectedBuildList = buildListComboBox.getValue();
        // If the selectedBuildList exists
        if (selectedBuildList != null) {
            try {
                // Change scene to loadBuildList scene
                Main.getInstance().sceneLoad("loadBuildList.fxml");
                // Set the controller to loadBuildList's controller
                LoadBuildListController controller = (LoadBuildListController) Main.getInstance().getController("loadBuildList.fxml");
                // Pass the PC Build List's id to the controller.
                controller.setListId(selectedBuildList.getPcBuildListId());
                // Close window the event was called from
                ((Node) event.getSource()).getScene().getWindow().hide();
            } catch (IOException e) {
                // Error while trying to change to the loadBuildList scene
                errorLabel.setText("Error: Failed to load selected PC Build List.");
            }
        } else {
            // Hit the addToList button without selecting a PC Build List.
            errorLabel.setText("Error: No PC Build List selected.");
        }
    }

    // Event handler for mainMenu button
    public void mainMenuAction(ActionEvent event) throws IOException {
        // Change window to mainMenu scene
        Main.getInstance().sceneLoad("mainMenu.fxml");
        // Close window the event occurred in
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
