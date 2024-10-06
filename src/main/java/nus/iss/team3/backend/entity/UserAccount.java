/* (C)2024 */
package nus.iss.team3.backend.entity;

import java.time.ZonedDateTime;

/**
 * Contains records that is related to a user account, - userId : used to login, must be unique,
 * primary key to the table - email : required to be unique
 *
 * @author Desmond Tan Zhi Heng
 */
// @Entity
public class UserAccount {

  private Integer id;
  private String name;
  private String password;
  private String displayName;
  private String email;
  private EUserAccountStatus status;
  private EUserRole role;
  private ZonedDateTime createDateTime;
  private ZonedDateTime updateDateTime;

  public UserAccount() {}

  public UserAccount(
      String name,
      String password,
      String displayName,
      String email,
      EUserAccountStatus status,
      EUserRole role) {
    this.name = name;
    this.password = password;
    this.displayName = displayName;
    this.email = email;
    this.status = status;
    this.role = role;
  }

  // Getters and setters for all fields

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public EUserAccountStatus getStatus() {
    return status;
  }

  public void setStatus(EUserAccountStatus status) {
    this.status = status;
  }

  public EUserRole getRole() {
    return role;
  }

  public void setRole(EUserRole role) {
    this.role = role;
  }

  // Add these new methods
  public void setStatus(int statusCode) {
    this.status = EUserAccountStatus.valueOfCode(statusCode);
  }

  public void setRole(int roleCode) {
    this.role = EUserRole.valueOfCode(roleCode);
  }

  public ZonedDateTime getCreateDateTime() {
    return createDateTime;
  }

  public void setCreateDateTime(ZonedDateTime createDateTime) {
    this.createDateTime = createDateTime;
  }

  public ZonedDateTime getUpdateDateTime() {
    return updateDateTime;
  }

  public void setUpdateDateTime(ZonedDateTime updateDateTime) {
    this.updateDateTime = updateDateTime;
  }

  @Override
  public String toString() {
    return "UserAccount{"
        + "id="
        + id
        + ", name='"
        + name
        + '\''
        + ", password='"
        + password
        + '\''
        + ", displayName='"
        + displayName
        + '\''
        + ", email='"
        + email
        + '\''
        + ", status="
        + status
        + ", role="
        + role
        + ", createDateTime="
        + createDateTime
        + ", updateDateTime="
        + updateDateTime
        + '}';
  }
}
