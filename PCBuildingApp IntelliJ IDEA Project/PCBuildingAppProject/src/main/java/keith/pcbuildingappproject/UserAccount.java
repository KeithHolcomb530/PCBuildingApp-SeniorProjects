/*
    Name: Keith Holcomb
    Date: May 5th, 2025
    Course: CISS:451 Senior Projects 001
    Description: UserAccount is the class for User Accounts within the PC Building App.
*/
package keith.pcbuildingappproject;

public class UserAccount {

    // Data fields
    private String username;
    private Integer accountid;

    // UserAccount has an instance. Used to determine login state.
    private static UserAccount instance;
    // UserAccount has a login state which is false by default.
    private boolean isLoggedIn = false;

    // Default constructor
    private UserAccount() {}

    // Return the current instance of the UserAccount
    public static UserAccount getInstance() {
        // If there is no UserAccount instance, make one.
        if (instance == null) {
            instance = new UserAccount();
        }
        return instance;
    }

    // Getters and setters for UserAccount
    public boolean isLoggedIn() { return isLoggedIn; }
    public void setLoggedIn(boolean isLoggedIn) { this.isLoggedIn = isLoggedIn; }

    // getUsername goes unused, as the username is only meant to be set, not accessed.
    public String getUsername() { return username; }
    public void setUsername(String userName) { this.username = userName; }

    public Integer getAccountid() { return accountid; }
    public void setAccountid(Integer accountId) { this.accountid = accountId; }
}
