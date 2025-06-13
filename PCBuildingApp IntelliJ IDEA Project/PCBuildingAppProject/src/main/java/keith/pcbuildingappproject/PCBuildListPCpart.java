/*
    Name: Keith Holcomb
    Date: May 5th, 2025
    Course: CISS:451 Senior Projects 001
    Description: PCBuildListPCpart is the class that associates PC Build Lists and PC Parts within the PC Building App.
*/
package keith.pcbuildingappproject;

// This class goes mostly unused, but the table representing it in the Database does get used.
// This was an implementation error when working on the bulk of the project, and is room for improvement.
public class PCBuildListPCpart {

    private int listPartId;
    private int listId;
    private int partId;
    private int quantity;

    // Default Constructor
    public PCBuildListPCpart() {}

    // Constructor with parameters
    public PCBuildListPCpart(int listPartId, int listId, int partId, int quantity) {
        this.listPartId = listPartId;
        this.listId = listId;
        this.partId = partId;
        this.quantity = quantity;
    }

    // Getters and Setters
    public int getListPartId() { return listPartId; }
    public void setListPartId(int listPartId) { this.listPartId = listPartId; }

    public int getListId() { return listId; }
    public void setListId(int listId) { this.listId = listId; }

    public int getPartId() { return partId; }
    public void setPartId(int partId) { this.partId = partId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
