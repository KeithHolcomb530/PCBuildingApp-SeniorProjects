/*
    Name: Keith Holcomb
    Date: May 5th, 2025
    Course: CISS:451 Senior Projects 001
    Description: ViewBuildListController is the controller class for the viewBuildList.fxml scene.
*/
package keith.pcbuildingappproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.HBox;
import java.sql.SQLException;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ViewBuildListController {

    @FXML
    Label errorLabel;
    @FXML
    private GridPane viewGrid;
    // dateFormatter is used when creating the creationDate or modifiedDate
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    // defaultThumbnail loads an image location if the ThumbnailURL is left empty.
    private final Image defaultThumbnail = new Image(getClass().getResourceAsStream("/image/PCBuildingAppExample.jpg"));

    // initialize is called when the scene is first loaded.
    public void initialize() {
        // ColumnConstraints allows customization of columns in a GridPane view.
        ColumnConstraints columnConstraints = new ColumnConstraints();
        // Each column has a max width of 50% of the GridPane
        columnConstraints.setPercentWidth(50);
        // Add two columns to the GridPane
        viewGrid.getColumnConstraints().addAll(columnConstraints, columnConstraints);
        // Add published PC Build Guides to the GridPane view
        loadPublishedBuildLists();
    }

    // Adds any published PC Build Guides to the GridPane view
    private void loadPublishedBuildLists() {
        try {
            // Calls on PostgreSQLController to get any published PC Build Guides and add them to the published BuildLists list.
            List<PCBuildList> publishedBuildLists = PostgreSQLController.getPublishedBuildLists();
            // Clear the GridPane of any rows.
            viewGrid.getChildren().clear();
            viewGrid.getRowConstraints().clear();

            // Check if there are any publishedBuildLists
            if (publishedBuildLists != null && !publishedBuildLists.isEmpty()) {
                // GridPane row and column identifiers
                int row = 0;
                int col = 0;
                // For each PCBuildList in the publishedBuildLists display them
                for (PCBuildList buildList : publishedBuildLists) {
                    // Call on PostgreSQLController to get the author's username of the given list by their account id
                    String authorUsername = PostgreSQLController.getUsernameByAccountId(buildList.getAccountId());
                    // Display the author's name given it isn't empty.
                    String authorDisplay = (authorUsername != null && !authorUsername.isEmpty()) ? " by " + authorUsername : " (Unknown Author)";
                    // Display the buildList name along with the authorDisplay
                    Label nameAuthorLabel = new Label(buildList.getPcBuildListName() + authorDisplay);
                    // Make the name + authorDisplay bold and 20% bigger than default.
                    nameAuthorLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 1.2em;");
                    // Add Labels for each column of the build list
                    Label descriptionLabel = new Label("Description: " + buildList.getDescription());
                    Label creationDateLabel = new Label("Created: " + buildList.getCreationDate().format(dateFormatter));
                    Label modifiedDateLabel = new Label("Modified: " + buildList.getModifiedDate().format(dateFormatter));
                    Label typeLabel = new Label("Type: " + buildList.getListType());
                    Label contentLabel = new Label("Content: " + (buildList.getContent() != null ? buildList.getContent() : "N/A"));
                    Label categoryLabel = new Label("Category: " + (buildList.getCategory() != null ? buildList.getCategory() : "N/A"));
                    // Add an ImageView of the ThumbnailURL that is locked to 150px by 150px
                    ImageView thumbnailView = createThumbnailView(buildList.getThumbnailURL(), 150, 150); // Larger size
                    VBox imageContainer = new VBox(thumbnailView);
                    // Align the image to the top-left, making it display to the left of all the Labels.
                    imageContainer.setAlignment(javafx.geometry.Pos.TOP_LEFT);
                    // Add some padding to the right of the image so Labels aren't too close.
                    imageContainer.setPadding(new Insets(0, 10, 0, 0));
                    // Wrap all the Labels in a VBox so they can be beside the ImageView
                    VBox textInfo = new VBox(nameAuthorLabel, descriptionLabel, creationDateLabel, modifiedDateLabel, typeLabel, contentLabel, categoryLabel);
                    // Add some text spacing to make the view better.
                    textInfo.setSpacing(3);
                    // Let the text grow vertically with the VBox.
                    VBox.setVgrow(textInfo, Priority.ALWAYS);
                    // Add an HBox which contains the ImageView, followed by the textInfo (Labels)
                    HBox buildListEntry = new HBox(imageContainer, textInfo);
                    // Set some space between the ImageView and the Label text
                    buildListEntry.setSpacing(15);
                    // Add a border and some padding to the PC Build Guide entry.
                    buildListEntry.setStyle("-fx-padding: 10px; -fx-border-color: #ccc; -fx-border-width: 1px; -fx-margin-bottom: 10px; -fx-pref-width: Infinity;");
                    // Allow the Labels to take any available horizontal space.
                    HBox.setHgrow(textInfo, Priority.ALWAYS);
                    // Add the HBox to the GridPane
                    viewGrid.add(buildListEntry, col, row);
                    // Iterate the GridPane row and columns as entries begin to fill the GridPane.
                    col++;
                    if (col > 1) {
                        col = 0;
                        row++;
                    }
                }
                // Clear the errorLabel since there were no issues.
                errorLabel.setText("");
            } else {
                // No published builds were found, so display an error to the user.
                errorLabel.setText("No published build lists found.");
            }
        } catch (SQLException e) {
            // Likely a connection error
            errorLabel.setText("Error: Connection failed while trying to get published PC Build Guides");
        }
    }

    // Fits an image from the ThumbnailURL into an ImageView
    private ImageView createThumbnailView(String thumbnailUrl, double fitWidth, double fitHeight) {
        // Create an ImageView
        ImageView thumbnailView = new ImageView();
        // Set the width and height
        thumbnailView.setFitHeight(fitHeight);
        thumbnailView.setFitWidth(fitWidth);
        // Preserve the square ratio
        thumbnailView.setPreserveRatio(true);
        // If the ThumbnailURL isn't empty try to get the image from the URL
        if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
            try {
                Image image = new Image(thumbnailUrl);
                thumbnailView.setImage(image);
            } catch (Exception e) {
                // If the ThumbnailURL image gives an error, use the defaultThumbnail as a backup.
                System.err.println("Error loading image from URL: " + thumbnailUrl + " - using default.");
                thumbnailView.setImage(defaultThumbnail);
            }
        } else {
            // If no ThumbnailURL was provided, use the default ThumbnailURL
            thumbnailView.setImage(defaultThumbnail);
        }
        // Return the ImageView to be used.
        return thumbnailView;
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
