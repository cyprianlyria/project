package com.example.project.data;

public class UserDetails {
    private String username;
    private String useremail;
    private String userpassword; // Add if needed
    private String userCity; // Add if needed
    private String userphone; // Add if needed
    private String userCategory;

    public UserDetails() {
        // Default constructor required for calls to DataSnapshot.getValue(UserDetails.class)
    }

    public UserDetails(String username, String useremail, String userCategory) {
        this.username = username;
        this.useremail = useremail;
        this.userCategory = userCategory;
    }

    // Getters and setters...
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUseremail() {
        return useremail;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }

    public String getUserpassword() {
        return userpassword;
    }

    public void setUserpassword(String userpassword) {
        this.userpassword = userpassword;
    }

    public String getUserphone() {
        return userphone;
    }

    public void setUserphone(String userphone) {
        this.userphone = userphone;
    }

    public String getUserCategory() {
        return userCategory;
    }

    public void setUserCategory(String userCategory) {
        this.userCategory = userCategory;
    }

    public String getUserCity() {
        return userCity;
    }

    public void setUserCity(String userCity) {
        this.userCity = userCity;
    }
}
