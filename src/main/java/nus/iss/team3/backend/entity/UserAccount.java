package nus.iss.team3.backend.entity;


/**
 * Contains records that is related to a user account
 *
 * @author Desmond Tan Zhi Heng
 */
//@Entity
public class UserAccount {

    private String userName;
    private String password;
    private String email;


    public UserAccount(){

    }
    public UserAccount(String userName, String password,String email) {
        this.userName = userName;
        this.password = password;
        this.email= email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "UserAccount{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
