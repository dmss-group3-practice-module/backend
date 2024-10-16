/* (C)2024 */
package nus.iss.team3.backend.service;

import java.time.ZonedDateTime;
import java.util.List;
import nus.iss.team3.backend.dataaccess.IUserAccountDataAccess;
import nus.iss.team3.backend.entity.UserAccount;
import nus.iss.team3.backend.util.StringUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class with logic for handling user account related queries
 *
 * @author Desmond Tan Zhi Heng, REN JIARUI
 */
@Service
// @Profile("user")
public class UserAccountService implements IUserAccountService {

  private static final Logger logger = LogManager.getLogger(UserAccountService.class);

  @Autowired IUserAccountDataAccess userAccountDataAccess;

  @Override
  public boolean addUser(UserAccount userAccount) {
    validateUserAccount(userAccount, false);
    if (!isUserNameAndEmailAvailable(userAccount, null)) {
      logger.warn("Username or email already exists for: {}", userAccount.getName());
      return false;
    }

    String hashedPassword = BCrypt.hashpw(userAccount.getPassword(), BCrypt.gensalt());
    userAccount.setPassword(hashedPassword);

    userAccount.setCreateDateTime(ZonedDateTime.now());
    userAccount.setUpdateDateTime(ZonedDateTime.now());
    boolean result = userAccountDataAccess.addUser(userAccount);
    if (result) {
      logger.info("User account created successfully for: {}", userAccount.getName());
    } else {
      logger.error("Failed to create user account for: {}", userAccount.getName());
    }
    return result;
  }

  @Override
  public boolean updateUser(UserAccount userAccount) {

    validateUserAccount(userAccount, true);
    if (userAccount.getId() == null) {
      throw new IllegalArgumentException("updateUser failed, due to missing Id for account");
    }
    UserAccount existingAccount = userAccountDataAccess.getUserById(userAccount.getId());
    if (existingAccount == null) {
      logger.warn("User not found for update: {}", userAccount.getId());
      return false;
    }
    if (!isUserNameAndEmailAvailable(userAccount, userAccount.getId())) {
      return false;
    }
    if (!StringUtilities.isStringNullOrBlank(userAccount.getPassword())) {
      String hashedPassword = BCrypt.hashpw(userAccount.getPassword(), BCrypt.gensalt());
      userAccount.setPassword(hashedPassword);
    } else {
      userAccount.setPassword(existingAccount.getPassword());
    }
    boolean result = userAccountDataAccess.updateUser(userAccount);
    if (result) {
      logger.info("User account updated successfully for ID: {}", userAccount.getId());
    } else {
      logger.error("Failed to update user account for ID: {}", userAccount.getId());
    }
    return result;
  }

  @Override
  public boolean deleteUserById(Integer id) {
    if (id == null) {
      throw new IllegalArgumentException("User ID cannot be null");
    }
    boolean result = userAccountDataAccess.deleteUserById(id);
    if (result) {
      logger.info("User account deleted successfully for ID: {}", id);
    } else {
      logger.warn("Failed to delete user account for ID: {}", id);
    }
    return result;
  }

  @Override
  public UserAccount getUserById(Integer id) {
    if (id == null) {
      throw new IllegalArgumentException("User ID cannot be null");
    }
    UserAccount user = userAccountDataAccess.getUserById(id);
    if (user == null) {
      logger.warn("User not found for ID: {}", id);
    }
    return user;
  }

  @Override
  public List<UserAccount> getAllUsers() {
    return userAccountDataAccess.getAllUsers();
  }

  private boolean isUserNameAndEmailAvailable(UserAccount userAccount, Integer currentUserId) {
    UserAccount existingUserWithName = userAccountDataAccess.getUserByName(userAccount.getName());
    if (existingUserWithName != null && !existingUserWithName.getId().equals(currentUserId)) {
      logger.warn("Username already exists: {}", userAccount.getName());
      return false;
    }
    UserAccount existingUserWithEmail =
        userAccountDataAccess.getUserByEmail(userAccount.getEmail());
    if (existingUserWithEmail != null && !existingUserWithEmail.getId().equals(currentUserId)) {
      logger.warn("Email already exists: {}", userAccount.getEmail());
      return false;
    }
    return true;
  }

  private void validateUserAccount(UserAccount userAccount, boolean isUpdate) {
    if (userAccount == null) {
      throw new IllegalArgumentException("User account cannot be null");
    }
    if (StringUtilities.isStringNullOrBlank(userAccount.getName())) {
      throw new IllegalArgumentException("Username cannot be empty or blank");
    }
    if (StringUtilities.isStringNullOrBlank(userAccount.getPassword())) {
      throw new IllegalArgumentException("Password cannot be empty or blank");
    }
    if (StringUtilities.isStringNullOrBlank(userAccount.getDisplayName())) {
      throw new IllegalArgumentException("Display name cannot be empty or blank");
    }
    if (StringUtilities.isStringNullOrBlank(userAccount.getEmail())) {
      throw new IllegalArgumentException("Email cannot be empty or blank");
    } else if (!isValidEmail(userAccount.getEmail())) {
      throw new IllegalArgumentException("Invalid email format");
    }
    if (userAccount.getStatus() == null) {
      throw new IllegalArgumentException("User status cannot be null");
    }
    if (userAccount.getRole() == null) {
      throw new IllegalArgumentException("User role cannot be null");
    }
  }

  private boolean isValidEmail(String email) {
    // a regex for email validation
    String emailRegex =
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    return email.matches(emailRegex);
  }
}
