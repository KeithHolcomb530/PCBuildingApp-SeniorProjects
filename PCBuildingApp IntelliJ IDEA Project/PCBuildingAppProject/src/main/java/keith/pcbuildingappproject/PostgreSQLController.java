/*
    Name: Keith Holcomb
    Date: May 5th, 2025
    Course: CISS:451 Senior Projects 001
    Description: PostgreSQLController for PCBuildingApp. Manages all queries to the PostgreSQL database.
*/
package keith.pcbuildingappproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostgreSQLController {

    // Connect success is a Boolean the application will use
    // to determine if the database server was reached during an action.
    // This allows the JavaFX application to have more specific error handling if something goes wrong.
    private static boolean connectSuccess = false;

    // Getter for connectSuccess.
    public static boolean connectSuccess() {
        return connectSuccess;
    }

    // login info the PCBuildingApp uses to make changes to the database.

    // Within PostgreSQL's pgAdmin 4, a custom user was setup with specific login that the application can use.
    // getUrl() specifies the string for the server location.
    private static String getUrl() { return "jdbc:postgresql://localhost:5432/pcbuildingapp"; }
    // getUser() specifies the user to log in to the DB.
    private static String getUser() { return "pcbuildingapp_user"; }
    // getPass() specifies the password to log in to the DB.
    private static String getPass() { return "PCBuildingApp246!"; }

    // Return whether a query result for a specific username in useraccount was successful or not.
    public static boolean usernameExists(String username) {
        // Start out assuming connection failed.
        connectSuccess = false;

        // The query string to use.
        // Any question marks are replaced within try/catch block.
        String query = "SELECT 1 FROM pcbuildingappschema.useraccount WHERE username = ?";

        // Try to connect to the DB, set the query statement to the query string.
        try (Connection connect = DriverManager.getConnection(getUrl(), getUser(), getPass());
             PreparedStatement statement = connect.prepareStatement(query)) {
            // Change the query ? to be the username.
            statement.setString(1, username);
            ResultSet result = statement.executeQuery();
            // Connection was successful since the query executed.
            connectSuccess = true;
            // Returns true if username exists.
            return result.next();

        } catch (SQLException ex) {
            // Connection to the DB failed.
            connectSuccess = false;
            // Assume username doesn't exist on error.
            return false;
        }
    }

    // Return the accountid of a useraccount based on a username search.
    public static int getAccountid(String username) throws SQLException {
        // Start out assuming connection failed.
        connectSuccess = false;
        // The application will handle an accountId of -1 as an error.
        int accountId = -1;

        // The query string to use.
        // Any question marks are replaced within try/catch block.
        String query = "SELECT accountid FROM pcbuildingappschema.useraccount WHERE username = ?";

        // Try to connect to the DB, set the query statement to the query string.
        try (Connection connect = DriverManager.getConnection(getUrl(), getUser(), getPass());
             PreparedStatement statement = connect.prepareStatement(query)) {
            // Change the query ? to be the username.
            statement.setString(1, username);
            // Execute the query and place results in a ResultSet
            ResultSet result = statement.executeQuery();
            // Connection was successful since the query executed.
            connectSuccess = true;
            // For each result in the ResultSet
            if (result.next()) {
                // Set accountID to the integer value of the 'accountid' column in the ResultSet.
                accountId = result.getInt("accountid");
            }
        } catch (SQLException ex) {
            // Connection to the DB failed.
            connectSuccess = false;
            // Should return -1, which the application will handle.
            return accountId;
        }
        // Return the accountId.
        return accountId;
    }

    // Hash the password for safe storage in the database.
    private static String hashPassword(String password) {
        try {
            // MessageDigest grabs an instance of the SHA-256 algorithm.
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // MessageDigest then converts the password to an array of bytes using the algorithm.
            byte[] encodedPass = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            // Convert the array of bytes to a hexadecimal for storing in the database.
            return bytesToHex(encodedPass);
        } catch (NoSuchAlgorithmException e) {
            // Catches an error if the 'SHA-256' algorithm wasn't found.
            return null;
        }
    }

    // Convert byte arrays to hexadecimal values to store in database.
    private static String bytesToHex(byte[] hash) {
        // StringBuilder can build a string from the given byte array.
        // Set the StringBuilder capacity to be double the byte array length.
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        // Iterate between the entire byte array
        for (int i = 0; i < hash.length; i++) {
            // Set a string to the hexadecimal of the current byte array iteration.
            String hex = Integer.toHexString(0xff & hash[i]);
            // If the strings length was 1, it should be appended to the StringBuilder as 0.
            if (hex.length() == 1) {
                hexString.append('0');
            }
            // Append the string to the StringBuilder
            hexString.append(hex);
        }
        // After iteration is finished, return the StringBuilder as a String.
        return hexString.toString();
    }

    // Return a boolean on whether writing the useraccount was successful or not.
    public static boolean writeUserAccount(String username, String password) {
        // Start out assuming connection failed.
        connectSuccess = false;
        // Hash the password before storing to the database.
        String hashedPassword = hashPassword(password);

        // Set the query string to add a column to useraccount table.
        // Any question marks are replaced within try/catch block.
        String query = "INSERT INTO pcbuildingappschema.useraccount(username, password) VALUES(?, ?)";

        // Try to connect to the DB, set the query statement to the query string.
        try (Connection connect = DriverManager.getConnection(getUrl(), getUser(), getPass());
             PreparedStatement statement = connect.prepareStatement(query)) {
            // Set the first question mark to be the username.
            statement.setString(1, username);
            // Set the second question mark to be the password.
            statement.setString(2, hashedPassword);
            // Execute the query
            statement.executeUpdate();
            // Connection was successful.
            connectSuccess = true;
            return true;
        } catch (SQLException ex) {
            // Connection failed.
            connectSuccess = false;
            return false;
        }
    }

    // Get hash password from database.
    private static String getHashPassword(String username) {
        // Start out assuming connection failed.
        connectSuccess = false;
        // Set the query string to get the password hash from a useraccount row with a matching username.
        // Any question marks are replaced within try/catch block.
        String query = "SELECT password FROM pcbuildingappschema.useraccount WHERE username = ?";

        // Try to connect to the DB, set the query statement to the query string.
        try (Connection connect = DriverManager.getConnection(getUrl(), getUser(), getPass());
             PreparedStatement statement = connect.prepareStatement(query)) {
            // Set the question mark to be the username.
            statement.setString(1, username);
            // Execute the query and place results in a ResultSet
            ResultSet resultSet = statement.executeQuery();
            // For each result in the ResultSet
            if (resultSet.next()) {
                // Return the hash password
                return resultSet.getString("password");
            } else {
                // Since there is no next result, query executed, but no results were found.
                // Connection successful.
                connectSuccess = true;
                // Search failed.
                return null;
            }

        } catch (SQLException ex) {
            // Connection failed.
            connectSuccess = false;
            return null;
        }
    }

    // Return a boolean for whether the username and password match a DB useraccount table entry.
    public static boolean authUserAccount(String username, String password) {
        // Before comparing passwords, password needs to be hashed.
        String storedHashPassword = getHashPassword(username);
        // Double check that the username in the database had a non-null password.
        if (storedHashPassword == null) {
            // Username not found.
            return false;
        }
        // Convert input password to a hash.
        String hashedPassword = hashPassword(password);
        // Will return whether the passwords match or not.
        return hashedPassword != null && hashedPassword.equals(storedHashPassword);
    }

    public static ObservableList<PCpart> getPCpartsFromDatabase(String partType) {
        // Start out assuming connection failed.
        connectSuccess = false;
        // Create empty ObservableList of PCpart objects
        ObservableList<PCpart> parts = FXCollections.observableArrayList();
        // Try to connect to the DB.
        try (Connection connection = DriverManager.getConnection(getUrl(), getUser(), getPass())) {
            // Declare the query string.
            String query;
            // Declare the statement.
            PreparedStatement statement;
            // Define the query and statement when
            // There is a partType filter.
            if (partType != null && !partType.isEmpty()) {
                // Set query string to get all columns of any PCpart table entries that have the partType of ?
                query = "SELECT partid, partname, parttype, manufacturer, model, price, specifications FROM pcbuildingappschema.pcpart WHERE parttype = ?";
                // Set the query statement
                statement = connection.prepareStatement(query);
                // Set the ? to be the specified partType filter.
                statement.setString(1, partType);
            // When there isn't a partType filter.
            } else {
                // Set the query string to get all columns of all PCpart table entries.
                query = "SELECT partid, partname, parttype, manufacturer, model, price, specifications FROM pcbuildingappschema.pcpart";
                // Set the query statement
                statement = connection.prepareStatement(query);
            }
            // Try to fill a RestulSet with the executed queries results.
            try (ResultSet resultSet = statement.executeQuery()) {
                // While there are still results
                while (resultSet.next()) {
                    // Set each column
                    int partid = resultSet.getInt("partid");
                    String partname = resultSet.getString("partname");
                    String parttypeFromDB = resultSet.getString("parttype"); // Renamed to avoid shadowing
                    String manufacturer = resultSet.getString("manufacturer");
                    String model = resultSet.getString("model");
                    double price = resultSet.getDouble("price");
                    // JSONB column is temporarily stored as a large String.
                    String specificationsJson = resultSet.getString("specifications");
                    // Use a Map to store the parsed Specifications
                    Map<String, Object> specifications = parseSpecifications(specificationsJson);
                    // Use the PCpart class to define a PCpart object from the DB table entry.
                    PCpart part = new PCpart(
                            partid,
                            partname,
                            parttypeFromDB,
                            manufacturer,
                            model,
                            price,
                            specifications
                    );
                    // Add the PCpart object to the ObservableList of PCpart objects.
                    parts.add(part);
                }
            }
            // Connection was successful.
            connectSuccess = true;
            // Return the ObservableList of PCpart objects.
            return parts;
        } catch (SQLException e) {
            // Connection failed.
            connectSuccess = false;
            // Return an empty ObservableList to avoid null pointer issues.
            return FXCollections.observableArrayList();
        }
    }

    // Parses the specifications column of a pcpart DB table entry from a JSON to a Map.
    private static Map<String, Object> parseSpecifications(String specificationsJson) {
        // Set a Map as an empty HashMap<>()
        Map<String, Object> specifications = new HashMap<>();
        // If the specificationsJson was empty
        if (specificationsJson == null || specificationsJson.isEmpty()) {
            // Return the empty Map
            return specifications;
        }
        // Parse the specificationsJson into the Map
        specifications = parseJsonString(specificationsJson);
        // Return the Map
        return specifications;
    }

    // Parses specificationsJson string from a pcpart DB table entry.
    private static Map<String, Object> parseJsonString(String json) {
        // Set a Map as an empty HashMap<>().
        Map<String, Object> map = new HashMap<>();
        // Begin to trim the JSON string.
        json = json.trim();
        // JSON parsing.
        // This was researched during the implementation of PCpart compatibility checks.
        // The JSON has to start and end with {}
        if (json.startsWith("{") && json.endsWith("}")) {
            // Determine the JSON length is the length from { to }
            json = json.substring(1, json.length() - 1);
            // Split the JSON into pairs based on any commas
            String[] pairs = json.split(",");
            // For each JSON pair
            for (String pair : pairs) {
                // Hold a string array for the value shown after a colon
                String[] keyValue = pair.split(":");
                // If the value is exactly 2 length
                if (keyValue.length == 2) {
                    // The 2 values should be quotations. ""
                    // Replace with ""
                    String key = keyValue[0].trim().replaceAll("^\"|\"$", "");
                    // Trim the second value in the string.
                    String valueString = keyValue[1].trim();
                    // Parse a string key value
                    Object value = parseJsonValue(valueString);
                    // Add the key value to the Map.
                    map.put(key, value);
                }
            }
        }
        // Return the Map.
        return map;
    }

    // Parses a key value in the specifications string from a pcpart DB table entry.
    private static Object parseJsonValue(String valueString) {
        // Begin trimming the value
        valueString = valueString.trim();
        // If the value starts with \" or ends with \"
        if (valueString.startsWith("\"") && valueString.endsWith("\"")) {
            // Return the string
            return valueString.substring(1, valueString.length() - 1);
        // If the value can ignore casing
        } else if (valueString.equalsIgnoreCase("true")) {
            // Returns a boolean
            return true;
        // If the value can't ignore casing
        } else if (valueString.equalsIgnoreCase("false")) {
            // Returns a boolean
            return false;
        // If the string matches a regex.
        } else if (valueString.matches("-?\\d+(\\.\\d+)?")) {
            try {
                // Check if it contains a period, means it's a Double.
                // Otherwise, it is an Integer
                if (valueString.contains(".")) {
                    // Return the parsed Double value
                    return Double.parseDouble(valueString); // Double
                } else {
                    // Return the parsed Int value
                    return Integer.parseInt(valueString); // Integer
                }
            } catch (NumberFormatException e) {
                // Return string if formatting fails
                return valueString;
            }
        // If the value starts and ends with {} it needs to go through the parseJsonString
        } else if (valueString.startsWith("{") && valueString.endsWith("}")){
            // Return the value after going through the parseJsonString method
            return parseJsonString(valueString);
        }
        // String value wasn't recognized
        else {
            // Return the string as is.
            return valueString;
        }
    }

    // Returns a boolean for whether the PC Build List name given is taken or not.
    public static boolean isBuildListNameUnique(String pcBuildListName) {
        // Start out assuming connection failed.
        connectSuccess = false;
        // Set query string.
        // Any question marks are replaced within try/catch block.
        String query = "SELECT COUNT(*) FROM pcbuildingappschema.pcbuildlist WHERE pcbuildlistname = ?";
        // Try to connect to DB and prepare query statement
        try (Connection connect = DriverManager.getConnection(getUrl(), getUser(), getPass());
             PreparedStatement statement = connect.prepareStatement(query)) {
            // Set the question mark to the pcBuildListName
            statement.setString(1, pcBuildListName);
            // Store the query result in a ResultSet
            ResultSet result = statement.executeQuery();
            // Connection was successful
            connectSuccess = true;
            // For each query result
            if (result.next()) {
                // Return if a column was found
                return result.getInt(1) == 0;
            }
            // If there is no result, return false.
            return false;
        } catch (SQLException ex) {
            // Connection failed.
            connectSuccess = false;
            return false;
        }
    }

    // Creates a pcbuildlist within DB and returns it's generated listid
    public static int addPCBuildList(PCBuildList pcBuildList) {
        // Start out assuming connection failed.
        connectSuccess = false;
        // Set query string
        // Any question marks are replaced within try/catch block.
        String query = "INSERT INTO pcbuildingappschema.pcbuildlist (accountid, pcbuildlistname, description, creationdate, modifieddate, totalprice, listtype, content, category, thumbnailurl) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        // Try to connect to DB and prepare the query statement
        // Note that the Statement.RETURN_GENERATED_KEYS is going to return the listid generated for the pcbuildlist entry.
        try (Connection connect = DriverManager.getConnection(getUrl(), getUser(), getPass());
             PreparedStatement statement = connect.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            // Set the question marks in the query
            statement.setInt(1, pcBuildList.getAccountId());
            statement.setString(2, pcBuildList.getPcBuildListName());
            statement.setString(3, pcBuildList.getDescription());
            statement.setDate(4, Date.valueOf(pcBuildList.getCreationDate()));
            statement.setDate(5, Date.valueOf(pcBuildList.getModifiedDate()));
            statement.setDouble(6, pcBuildList.getTotalPrice());
            statement.setString(7, pcBuildList.getListType());
            statement.setString(8, pcBuildList.getContent());
            statement.setString(9, pcBuildList.getCategory());
            statement.setString(10, pcBuildList.getThumbnailURL());
            // Store query result as rowsInserted
            int rowsInserted = statement.executeUpdate();
            // Connection successful
            connectSuccess = true;
            // If any rows were inserted
            if (rowsInserted > 0) {
                // Get the generatedKey or listid
                ResultSet generatedKeys = statement.getGeneratedKeys();
                // If a listid was generated
                if (generatedKeys.next()) {
                    // Return the listid
                    return generatedKeys.getInt(1);
                }
            }
            // The application will handle -1 as an error
            return -1;
        } catch (SQLException ex) {
            // Connection failed
            connectSuccess = false;
            return -1;
        }
    }

    // Returns a PCBuildList from the DB based on its listID
    public static PCBuildList getPCBuildListById(int listId) throws SQLException {
        // Start out assuming connection failed.
        connectSuccess = false;
        // Set query string
        // Any question marks are replaced within try/catch block.
        String query = "SELECT pcbuildlistid, accountid, pcbuildlistname, description, creationdate, modifieddate, totalprice, listtype, content, category, thumbnailurl, publish FROM pcbuildingappschema.pcbuildlist WHERE pcbuildlistid = ?";
        // Try to connect to the DB
        try (Connection connect = DriverManager.getConnection(getUrl(), getUser(), getPass())) {
            // Prepare the query statement
            PreparedStatement statement = connect.prepareStatement(query);
            // Set the question mark to be the listid
            statement.setInt(1, listId);
            // Return the query result as a ResultSet
            ResultSet result = statement.executeQuery();
            // Connection successful
            connectSuccess = true;
            // For each result
            if (result.next()) {
                // Create a PCBuildList object
                PCBuildList pcBuildList = new PCBuildList();
                // Fill the PCBuildList object credentials
                pcBuildList.setPcBuildListId(result.getInt("pcbuildlistid"));
                pcBuildList.setAccountId(result.getInt("accountid"));
                pcBuildList.setPcBuildListName(result.getString("pcbuildlistname"));
                pcBuildList.setDescription(result.getString("description"));
                pcBuildList.setCreationDate(result.getDate("creationdate").toLocalDate());
                pcBuildList.setModifiedDate(result.getDate("modifieddate").toLocalDate());
                pcBuildList.setTotalPrice(result.getDouble("totalprice"));
                pcBuildList.setListType(result.getString("listtype"));
                pcBuildList.setContent(result.getString("content"));
                pcBuildList.setCategory(result.getString("category"));
                pcBuildList.setThumbnailURL(result.getString("thumbnailurl"));
                pcBuildList.setPublish(result.getBoolean("publish"));
                // Return the PCBuildList
                return pcBuildList;
            }
        } catch (SQLException ex) {
            // Connection failed
            connectSuccess = false;
            return null;
        }
        return null;
    }

    // Returns a boolean whether a query successfully updates a 'pcbuildlist' entry or not
    public static boolean updatePCBuildList(PCBuildList pcBuildList) {
        // Start out assuming connection failed.
        connectSuccess = false;
        // Set the query string
        // Any question marks are replaced within the try/catch block.
        String query = "UPDATE pcbuildingappschema.pcbuildlist SET pcbuildlistname = ?, description = ?, modifieddate = ?, listtype = ?, content = ?, category = ?, thumbnailurl = ?, publish = ? WHERE pcbuildlistid = ?";
        // Try to connect to DB and prepare the query statement
        try (Connection connect = DriverManager.getConnection(getUrl(), getUser(), getPass());
             PreparedStatement statement = connect.prepareStatement(query)) {
            // Replace question marks with associated PCBuildList info.
            statement.setString(1, pcBuildList.getPcBuildListName());
            statement.setString(2, pcBuildList.getDescription());
            statement.setDate(3, Date.valueOf(pcBuildList.getModifiedDate()));
            statement.setString(4, pcBuildList.getListType());
            statement.setString(5, pcBuildList.getContent());
            statement.setString(6, pcBuildList.getCategory());
            statement.setString(7, pcBuildList.getThumbnailURL());
            statement.setBoolean(8, pcBuildList.isPublish());
            statement.setInt(9, pcBuildList.getPcBuildListId());
            // Return the query results as an Integer
            int rowsUpdated = statement.executeUpdate();
            // Connection successful
            connectSuccess = true;
            // Return whether the query actually updated any entries
            return rowsUpdated > 0;
        } catch (SQLException ex) {
            // Connection failed
            connectSuccess = false;
            return false;
        }
    }

    // Returns a list of any 'pcbuildlist' entries that are published
    public static List<PCBuildList> getPublishedBuildLists() throws SQLException {
        // Start out assuming connection failed.
        connectSuccess = false;
        // Create empty list
        List<PCBuildList> publishedLists = new ArrayList<>();
        // Set query string
        String query = "SELECT pcbuildlistid, accountid, pcbuildlistname, description, creationdate, modifieddate, totalprice, listtype, content, category, thumbnailurl, publish " +
                "FROM pcbuildingappschema.pcbuildlist WHERE publish = TRUE";
        // Try to connect to the DB and prepare the query statement
        try (Connection connect = DriverManager.getConnection(getUrl(), getUser(), getPass());
             PreparedStatement statement = connect.prepareStatement(query)) {
            // Return the query result in a ResultSet
            ResultSet result = statement.executeQuery();
            // Connection successful
            connectSuccess = true;
            // For each result in the ResultSet
            while (result.next()) {
                // Create a PCBuildList object
                PCBuildList pcBuildList = new PCBuildList();
                // Fill the PCBuildList object credentials
                pcBuildList.setPcBuildListId(result.getInt("pcbuildlistid"));
                pcBuildList.setAccountId(result.getInt("accountid"));
                pcBuildList.setPcBuildListName(result.getString("pcbuildlistname"));
                pcBuildList.setDescription(result.getString("description"));
                pcBuildList.setCreationDate(result.getDate("creationdate").toLocalDate());
                pcBuildList.setModifiedDate(result.getDate("modifieddate").toLocalDate());
                pcBuildList.setTotalPrice(result.getDouble("totalprice"));
                pcBuildList.setListType(result.getString("listtype"));
                pcBuildList.setContent(result.getString("content"));
                pcBuildList.setCategory(result.getString("category"));
                pcBuildList.setThumbnailURL(result.getString("thumbnailurl"));
                pcBuildList.setPublish(result.getBoolean("publish"));
                // Add the PCBuildList object to the list
                publishedLists.add(pcBuildList);
            }
        } catch (SQLException ex) {
            // Connection failed
            connectSuccess = false;
            return null;
        }
        // Return the list
        return publishedLists;
    }

    // Returns a string of the username associated with a given accountid in the 'useraccount' table
    public static String getUsernameByAccountId(int accountId) throws SQLException {
        // Start out assuming connection failed.
        connectSuccess = false;
        // Set the query string
        // Any question marks will be replaced within the try/catch block
        String query = "SELECT username FROM pcbuildingappschema.useraccount WHERE accountid = ?";
        // Try to connect to the DB and prepare the query statement
        try (Connection connect = DriverManager.getConnection(getUrl(), getUser(), getPass());
             PreparedStatement statement = connect.prepareStatement(query)) {
            // Set the question mark to be the accountid
            statement.setInt(1, accountId);
            // Return the query result within a ResultSet
            ResultSet result = statement.executeQuery();
            // Connection successful
            connectSuccess = true;
            // For each result in the ResultSet
            if (result.next()) {
                // return the username
                return result.getString("username");
            }
        } catch (SQLException ex) {
            // Connection failed
            connectSuccess = false;
            return null;
        }
        return null;
    }

    // Return a list of PCpart objects based on the associated pcbuildlist listid
    public static List<PCpart> getPartsForBuildList(int listId) {
        // Start out assuming connection failed.
        connectSuccess = false;
        // Create empty list as an ArrayList<>()
        List<PCpart> parts = new ArrayList<>();
        // Set the query string
        // Any question marks will be replaced within the try/catch block
        String query = "SELECT p.* FROM pcbuildingappschema.pcpart p " +
                "JOIN pcbuildingappschema.pcbuildlist_pcpart pp ON p.partid = pp.partid " +
                "WHERE pp.listid = ?";
        // Try to connect to the DB and prepare the query statement
        try (Connection connect = DriverManager.getConnection(getUrl(), getUser(), getPass());
             PreparedStatement statement = connect.prepareStatement(query)) {
            // Set the question mark to the listid
            statement.setInt(1, listId);
            // Return the result of the query as a ResultSet
            ResultSet resultSet = statement.executeQuery();
            // Connection successful
            connectSuccess = true;
            // For each result within the ResultSet
            while (resultSet.next()) {
                // Set variables to hold PCpart object variables
                int partid = resultSet.getInt("partid");
                String partname = resultSet.getString("partname");
                String parttype = resultSet.getString("parttype");
                String manufacturer = resultSet.getString("manufacturer");
                String model = resultSet.getString("model");
                double price = resultSet.getDouble("price");
                String specificationsJson = resultSet.getString("specifications");
                // Parse the specifications JSONB
                Map<String, Object> specifications = parseSpecifications(specificationsJson);
                // Create a PCpart object based on the variables
                PCpart part = new PCpart(
                        partid,
                        partname,
                        parttype,
                        manufacturer,
                        model,
                        price,
                        specifications
                );
                // Add the PCpart object to the list
                parts.add(part);
            }
        } catch (SQLException ex) {
            // Connection failed
            connectSuccess = false;
            return parts;
        }
        // Return the list
        return parts;
    }

    // Returns a list of PCBuildList objects based on the useraccount accountid that owns them
    public static List<PCBuildList> getBuildListsForAccount(int accountId) {
        // Start out assuming connection failed.
        connectSuccess = false;
        // Create empty lst as an ArrayList<>()
        List<PCBuildList> buildLists = new ArrayList<>();
        // Set query string
        // Any question marks are replaced within try/catch block
        String query = "SELECT * FROM pcbuildingappschema.pcbuildlist WHERE accountid = ?";
        // Try to connect to DB and prepare query statement
        try (Connection connection = DriverManager.getConnection(getUrl(), getUser(), getPass());
             PreparedStatement statement = connection.prepareStatement(query)) {
            // Replace question mark with accountid
            statement.setInt(1, accountId);
            // Try to return the query result as a ResultSet
            try (ResultSet resultSet = statement.executeQuery()) {
                // For each result within the ResultSet
                while (resultSet.next()) {
                    // Create a PCBuildList object and fill its variables
                    PCBuildList buildList = new PCBuildList(
                            resultSet.getInt("pcbuildlistid"),
                            resultSet.getInt("accountid"),
                            resultSet.getString("pcbuildlistname"),
                            resultSet.getString("description"),
                            resultSet.getDate("creationdate").toLocalDate(),
                            resultSet.getDate("modifieddate").toLocalDate(),
                            resultSet.getDouble("totalprice"),
                            resultSet.getString("listtype"),
                            resultSet.getString("content"),
                            resultSet.getString("category"),
                            resultSet.getString("thumbnailurl"),
                            resultSet.getBoolean("publish")
                    );
                    // Add the PCBuildList object to the list
                    buildLists.add(buildList);
                }
            }
            // Connection successful
            connectSuccess = true;
            // Return the list
            return buildLists;
        } catch (SQLException e) {
            // Connection failed
            connectSuccess = false;
            return buildLists;
        }
    }

    // Return a boolean for whether adding a pcbuildlist_pcpart entry was successful or not
    public static boolean addPartToList(int listId, int partId) {
        // Start out assuming the connection failed
        connectSuccess = false;
        // Set the query string
        // Any question marks will be replaced within the try/catch block
        String query = "INSERT INTO pcbuildingappschema.pcbuildlist_pcpart (listid, partid) VALUES (?, ?)";
        // Try to connect to the DB and prepare the query statement
        try (Connection connection = DriverManager.getConnection(getUrl(), getUser(), getPass());
             PreparedStatement statement = connection.prepareStatement(query)) {
            // Set the question marks with the listid and partid
            statement.setInt(1, listId);
            statement.setInt(2, partId);
            // Return the query result as an Integer
            int rowsInserted = statement.executeUpdate();
            // Connection successful
            connectSuccess = true;
            // Return whether the Integer is greater than 0
            return rowsInserted > 0;
        } catch (SQLException e) {
            // Connection failed
            connectSuccess = false;
            return false;
        }
    }

    // Return a boolean for whether removing a pcbuildlist_pcpart entry was successful or not
    public static boolean removePartFromList(int listId, int partId) {
        // Start out assuming connection failed
        connectSuccess = false;
        // Set the query string
        // Any question marks will be replaced within the try/catch block
        String query = "DELETE FROM pcbuildingappschema.pcbuildlist_pcpart WHERE listid = ? AND partid = ?";
        // Try to connect to the DB and prepare the query statement
        try (Connection connection = DriverManager.getConnection(getUrl(), getUser(), getPass());
             PreparedStatement statement = connection.prepareStatement(query)) {
            // Replace the question marks with the listid and partid
            statement.setInt(1, listId);
            statement.setInt(2, partId);
            // Return the query result as an Integer
            int rowsDeleted = statement.executeUpdate();
            // Connection successful
            connectSuccess = true;
            // Return whether the Integer is greater than 0
            return rowsDeleted > 0;
        } catch (SQLException e) {
            // Connection failed
            connectSuccess = false;
            return false;
        }
    }

    // Return a boolean for whether removing a pcbuildlist entry was successful or not
    public static boolean deleteBuildList(int listId) {
        // Start out assuming connection failed
        connectSuccess = false;
        // Set the query string
        // Any question marks will be replaced within the try/catch block
        String query = "DELETE FROM pcbuildingappschema.pcbuildlist WHERE pcbuildlistid = ?";
        // Try to connect to the DB and prepare the query statement
        try (Connection connection = DriverManager.getConnection(getUrl(), getUser(), getPass());
             PreparedStatement statement = connection.prepareStatement(query)) {
            // Prepare a second query string to delete any associated pcbuildlist_pcpart entries.
            // Any question marks will be replaced within the try block
            String deletePartsQuery = "DELETE FROM pcbuildingappschema.pcbuildlist_pcpart WHERE listid = ?";
            // Try to connect to the Db and prepare the second query statement
            try (PreparedStatement deletePartsStatement = connection.prepareStatement(deletePartsQuery)){
                // Replace question mark with listid
                deletePartsStatement.setInt(1, listId);
                // Execute the query
                deletePartsStatement.executeUpdate();
            }
            // Replace question mark of first query with listid
            statement.setInt(1, listId);
            // Return the first query's result as an Integer
            int rowsDeleted = statement.executeUpdate();
            // Connection successful
            connectSuccess = true;
            // Return whether the Integer is greater than 0
            return rowsDeleted > 0;
        } catch (SQLException e) {
            // Connection failed
            connectSuccess = false;
            return false;
        }
    }

    // Return a Double for the current totalPrice of a pcbuildlist entry
    public static double getTotalPrice(int listId) {
        // Start out assuming connection failed
        connectSuccess = false;
        // Set totalPrice Double to 0 by default
        double totalPrice = 0.0;
        // Set the query string
        // Any question marks will be replaced within the try/catch block
        String query = "SELECT totalprice FROM pcbuildingappschema.pcbuildlist WHERE pcbuildlistid = ?";
        // Try to connect to DB and prepare the query statement
        try (Connection connect = DriverManager.getConnection(getUrl(), getUser(), getPass());
             PreparedStatement statement = connect.prepareStatement(query)) {
            // Set the question mark to the listid
            statement.setInt(1, listId);
            // Return the result of the query as a ResultSet
            ResultSet resultSet = statement.executeQuery();
            // If the ResultSet isn't empty
            if (resultSet.next()) {
                // Set the totalPrice to the 'totalPrice' column
                totalPrice = resultSet.getDouble("totalprice");
            }
            // Connection successful
            connectSuccess = true;
        } catch (SQLException ex) {
            // Connection failed
            connectSuccess = false;
            return totalPrice;
        }
        return totalPrice;
    }

    // Return a boolean for whether the totalPrice of a pcbuildlist entry was updated or not
    public static boolean updateTotalPrice(int listId, double totalPrice) {
        // Start out assuming connection failed
        connectSuccess = false;
        // Set the query string
        // Any question marks will be replaced within try/catch block
        String query = "UPDATE pcbuildingappschema.pcbuildlist SET totalprice = ? WHERE pcbuildlistid = ?";
        // Try to connect to Db and prepare the query statement
        try (Connection connect = DriverManager.getConnection(getUrl(), getUser(), getPass());
             PreparedStatement statement = connect.prepareStatement(query)) {
            // Set the question marks to the totalPrice and listid
            statement.setDouble(1, totalPrice);
            statement.setInt(2, listId);
            // Return the query result as an Integer
            int rowsUpdated = statement.executeUpdate();
            // Connection successful
            connectSuccess = true;
            // Return whether the Integer is greater than 0
            return rowsUpdated > 0;
        } catch (SQLException ex) {
            // Connection failed
            connectSuccess = false;
            return false;
        }
    }

    // Return a PCBuildList object based on a listid
    public static PCBuildList getBuildListById(int listId) {
        // Start out assuming connection failed
        connectSuccess = false;
        // Set the query string
        // Any question marks will be replaced within the try/catch block
        String query = "SELECT * FROM pcbuildingappschema.pcbuildlist WHERE pcbuildlistid = ?";
        // Try to connect to the DB and prepare the query statement
        try (Connection connection = DriverManager.getConnection(getUrl(), getUser(), getPass());
             PreparedStatement statement = connection.prepareStatement(query)) {
            // Set the question mark to the listid
            statement.setInt(1, listId);
            // Return the query result as a ResultSet
            ResultSet resultSet = statement.executeQuery();
            // If the ResultSet has a result
            if (resultSet.next()) {
                // Create a PCBuildList object
                PCBuildList buildList = new PCBuildList(
                        // Fill the PCBuildList with variables associated with pcbuildlist entry columns
                        resultSet.getInt("pcbuildlistid"),
                        resultSet.getInt("accountid"),
                        resultSet.getString("pcbuildlistname"),
                        resultSet.getString("description"),
                        resultSet.getObject("creationdate", LocalDate.class),
                        resultSet.getObject("modifieddate", LocalDate.class),
                        resultSet.getDouble("totalprice"),
                        resultSet.getString("listtype"),
                        resultSet.getString("content"),
                        resultSet.getString("category"),
                        resultSet.getString("thumbnailurl"),
                        resultSet.getBoolean("publish")
                );
                // Connection successful
                connectSuccess = true;
                // Return the created PCBuildList object
                return buildList;
            }

        } catch (SQLException e) {
            // Connection failed
            connectSuccess = false;
        }
        return null;
    }

    // Return a boolean for whether the modifieddate of a pcbuildlist entry was updated or not
    public static boolean updateModifiedDate(int listId) {
        // Start out assuming connection failed
        connectSuccess = false;
        // Set the query string
        // Any question marks will be replaced within try/catch block
        String query = "UPDATE pcbuildingappschema.pcbuildlist SET modifieddate = ? WHERE pcbuildlistid = ?";
        // Try to connect to the DB and prepare the query statement
        try (Connection connection = DriverManager.getConnection(getUrl(), getUser(), getPass());
             PreparedStatement statement = connection.prepareStatement(query)) {
            // Set the first question mark to the current date
            statement.setObject(1, LocalDate.now());
            // Set the second question mark to the listid
            statement.setInt(2, listId);
            // Return the result of the query statement as an Integer
            int rowsUpdated = statement.executeUpdate();
            // Connection successful
            connectSuccess = true;
            // Return whether the Integer is greater than 0
            return rowsUpdated > 0;
        } catch (SQLException e) {
            // Connection failed
            connectSuccess = false;
            return false;
        }
    }
}
