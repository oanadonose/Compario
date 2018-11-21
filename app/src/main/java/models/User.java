package models;

public class User {
    public String username;
    public String email;
    //public String password;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
        //this.password = password;
    }

}
