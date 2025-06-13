/*
    Name: Keith Holcomb
    Date: May 5th, 2025
    Course: CISS:451 Senior Projects 001
    Description: PCBuildList is the class for PC Build List within the PC Building App.
*/
package keith.pcbuildingappproject;

import java.time.LocalDate;

public class PCBuildList {

    private int pcBuildListId;
    private int accountId;
    private String pcBuildListName;
    private String description;
    private LocalDate creationDate;
    private LocalDate modifiedDate;
    private double totalPrice;
    private String listType;
    private String content;
    private String category;
    private String thumbnailURL;
    private boolean publish;

    // Default empty constructor
    public PCBuildList() {}

    // Constructor with parameters
    public PCBuildList(int pcBuildListId, int accountId, String pcBuildListName, String description, LocalDate creationDate, LocalDate modifiedDate, double totalPrice, String listType, String content, String category, String thumbnailURL, Boolean publish) {
        setPcBuildListId(pcBuildListId);
        setAccountId(accountId);
        setPcBuildListName(pcBuildListName);
        setDescription(description);
        setCreationDate(creationDate);
        setModifiedDate(modifiedDate);
        setTotalPrice(totalPrice);
        setListType(listType);
        setContent(content);
        setCategory(category);
        setThumbnailURL(thumbnailURL);
        setPublish(publish);
    }

    @Override
    public String toString() {
        return pcBuildListName;
    }

    // Getters and Setters
    public int getPcBuildListId() { return pcBuildListId; }
    public void setPcBuildListId(int pcBuildListId) { this.pcBuildListId = pcBuildListId; }

    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }

    public String getPcBuildListName() { return pcBuildListName; }
    public void setPcBuildListName(String pcBuildListName) { this.pcBuildListName = pcBuildListName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDate creationDate) { this.creationDate = creationDate; }

    public LocalDate getModifiedDate() { return modifiedDate; }
    public void setModifiedDate(LocalDate modifiedDate) { this.modifiedDate = modifiedDate; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getListType() { return listType; }
    public void setListType(String listType) { this.listType = listType; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getThumbnailURL() { return thumbnailURL; }
    public void setThumbnailURL(String thumbnailURL) { this.thumbnailURL = thumbnailURL; }

    public Boolean isPublish() { return publish; }
    public void setPublish(Boolean publish) { this.publish = publish; }
}
