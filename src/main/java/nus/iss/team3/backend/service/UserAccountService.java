/* (C)2024 */
package nus.iss.team3.backend.service;

import java.time.ZonedDateTime;
import java.util.List;
import nus.iss.team3.backend.dataaccess.IUserAccountDataAccess;
import nus.iss.team3.backend.entity.UserAccount;
import nus.iss.team3.backend.util.StringUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class with logic for handling user account related queries
 *
 * @author Desmond Tan Zhi Heng
 */
@Service
public class UserAccountService implements IUserAccountService {

  private static final Logger logger = LogManager.getLogger(UserAccountService.class);

  @Autowired IUserAccountDataAccess userAccountDataAccess;

  @Override
  public boolean addUser(UserAccount userAccount) {
    if (!validateUserAccount(userAccount)) {
      logger.info(
          "addUser failed, due to validation failed for account {}",
          (userAccount == null ? "null object" : userAccount.getName()));
      return false;
    }
    if (userAccountDataAccess.getUserByEmail(userAccount.getEmail()) != null) {
      logger.info("addUser failed, due to existing account for Email: {}", userAccount.getEmail());
      return false;
    }

    userAccount.setCreateDateTime(ZonedDateTime.now());
    userAccount.setUpdateDateTime(ZonedDateTime.now());

    return userAccountDataAccess.addUser(userAccount);
  }

  @Override
  public boolean updateUser(UserAccount userAccount) {
    if (!validateUserAccount(userAccount)) {
      logger.info(
          "updateUser failed, due to validation failed for account {}",
          (userAccount == null ? "null object" : userAccount.getId()));
      return false;
    }
    if (userAccount.getId() == null) {
      logger.info("updateUser failed, due to missing Id for account");
      return false;
    }
    UserAccount existingAccount = userAccountDataAccess.getUserById(userAccount.getId());
    if (existingAccount == null) {
      logger.info("updateUser failed, due to missing account for {}", userAccount.getId());
      return false;
    }
    UserAccount accountWithSameEmail = userAccountDataAccess.getUserByEmail(userAccount.getEmail());
    if (accountWithSameEmail != null && !accountWithSameEmail.getId().equals(userAccount.getId())) {
      logger.info(
          "updateUser failed, due to existing account for Email: {}", userAccount.getEmail());
      return false;
    }

    userAccount.setUpdateDateTime(ZonedDateTime.now());
    return userAccountDataAccess.updateUser(userAccount);
  }

  @Override
  public boolean deleteUserById(Integer id) {
    if (userAccountDataAccess.getUserById(id) == null) {
      logger.info("deleteUser failed, due to missing account for {}", id);
      return false;
    }
    return userAccountDataAccess.deleteUserById(id);
  }

  @Override
  public UserAccount getUserById(Integer id) {
    logger.info("looking for user with id {}", id);
    return userAccountDataAccess.getUserById(id);
  }

  @Override
  public List<UserAccount> getAllUser() {
    return userAccountDataAccess.getAllUsers();
  }

  /**
   * Check whether the input useraccount contians value for it to be accepted
   *
   * @return whether the string is null or blank.
   */
  private boolean validateUserAccount(UserAccount userAccount) {
    return userAccount != null
        && !StringUtilities.isStringNullOrBlank(userAccount.getName())
        && !StringUtilities.isStringNullOrBlank(userAccount.getPassword())
        && !StringUtilities.isStringNullOrBlank(userAccount.getDisplayName())
        && !StringUtilities.isStringNullOrBlank(userAccount.getEmail())
        && userAccount.getStatus() != null
        && userAccount.getRole() != null;
  }
}
