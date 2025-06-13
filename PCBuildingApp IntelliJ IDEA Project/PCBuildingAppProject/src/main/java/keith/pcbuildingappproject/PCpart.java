/*
    Name: Keith Holcomb
    Date: May 5th, 2025
    Course: CISS:451 Senior Projects 001
    Description: PCpart is the class for PC parts within the PC Building App.
*/
package keith.pcbuildingappproject;

// Use of BooleanProperty was a workaround for some issues that occurred in testing.
// This serves as a point of improvement for this Project.
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import java.util.Map;

public class PCpart {

    // Data fields
    private int partID;
    private String partName;
    private String partType;
    private String manufacturer;
    private String model;
    private double price;
    // Specifications is special, in that it is stored in the database as a JSONB.
    // Specifications can use the Map type, and our application will parse the Map into a JSONB later.
    private Map<String, Object> specifications;
    // BooleanProperty is used as a workaround for some issues that occurred during testing.
    private BooleanProperty selected = new SimpleBooleanProperty(false);

    // Constructor with parameters
    public PCpart(int partID, String partName, String partType, String manufacturer, String model, double price, Map<String, Object> specifications) {
        setPartID(partID);
        setPartName(partName);
        setPartType(partType);
        setManufacturer(manufacturer);
        setModel(model);
        setPrice(price);
        setSpecifications(specifications);
    }

    // Getters and setters for each PCpart data field.
    public int getPartID() { return partID; }
    public void setPartID(int partID) { this.partID = partID; }

    public String getPartName() { return partName; }
    public void setPartName(String partName) { this.partName = partName; }

    public String getPartType() { return partType; }
    public void setPartType(String partType) { this.partType = partType; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public Map<String, Object> getSpecifications() { return specifications; }
    public void setSpecifications(Map<String, Object> specifications) { this.specifications = specifications; }

    // Use of BooleanProperty was a workaround for some issues that occurred in testing.
    public BooleanProperty selectedProperty() { return selected; }

    public boolean isSelected() { return selected.get(); }
    public void setSelected(boolean selected) { this.selected.set(selected); }

}
