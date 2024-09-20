/* (C)2024 */
package nus.iss.team3.backend.entity;

/**
 * Contains records that is related to a user account, - userId : used to login, must be unique,
 * primary key to the table - email : required to be unique
 *
 * @author Desmond Tan Zhi Heng
 */
// @Entity
public class UserAccount {

  private String userId;
  private String userName;
  private String password;
  private String email;
  private EUserAccountStatus status;

  public UserAccount() {}

  public UserAccount(String userName, String password, String email) {
    this.userName = userName;
    this.password = password;
    this.email = email;
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

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public EUserAccountStatus getStatus() {
    return status;
  }

  public void setStatus(EUserAccountStatus status) {
    this.status = status;
  }

  @Override
  public String toString() {
    return "UserAccount{"
        + "userName='"
        + userName
        + '\''
        + ", password='"
        + password
        + '\''
        + ", email='"
        + email
        + '\''
        + '}';
  }
}
