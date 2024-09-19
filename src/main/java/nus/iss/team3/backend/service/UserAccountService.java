/* (C)2024 */
package nus.iss.team3.backend.service;

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
    if (validateUserAccount(userAccount)) {
      logger.info(
          "addUser failed, due to validation failed for account {}",
          (userAccount == null ? "null object" : userAccount.getUserName()));
      return false;
    }
    if (userAccountDataAccess.getUserById(userAccount.getUserId()) != null) {
      logger.info("addUser failed, due to existing account for Id {}", userAccount.getUserName());
      return false;
    }
    if (userAccountDataAccess.getUserByEmail(userAccount.getEmail()) != null) {
      logger.info(
          "addUser failed, due to existing account for Email: {}", userAccount.getUserName());
      return false;
    }

    return userAccountDataAccess.addUser(userAccount);
  }

  @Override
  public boolean updateUser(UserAccount userAccount) {
    if (validateUserAccount(userAccount)) {
      logger.info(
          "updateUser failed, due to validation failed for account {}",
          (userAccount == null ? "null object" : userAccount.getUserId()));
      return false;
    }
    if (StringUtilities.isStringNullOrBlank(userAccount.getUserId())) {
      logger.info("updateUser failed, due to missing Id account for {}", userAccount.getUserId());
      return false;
    }
    UserAccount otherAccount = userAccountDataAccess.getUserById(userAccount.getUserId());
    if (otherAccount == null) {
      logger.info("updateUser failed, due to missing account for {}", userAccount.getUserId());
      return false;
    }
    otherAccount = userAccountDataAccess.getUserByEmail(userAccount.getEmail());
    if (otherAccount != null && !otherAccount.getUserId().equals(userAccount.getUserId())) {
      logger.info(
          "updateUser failed, due to existing account for Email: {}", userAccount.getUserName());
      return false;
    }
    return userAccountDataAccess.updateUser(userAccount);
  }

  @Override
  public boolean deleteUserById(String userId) {
    if (userAccountDataAccess.getUserById(userId) == null) {
      logger.info("updateUser failed, due to missing account for {}", userId);
      return false;
    }
    return userAccountDataAccess.deleteUserById(userId);
  }

  @Override
  public UserAccount getUserById(String userId) {
    logger.info("looking for {}", userId);
    return userAccountDataAccess.getUserById(userId);
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
    return userAccount == null
        || StringUtilities.isStringNullOrBlank(userAccount.getUserId())
        || StringUtilities.isStringNullOrBlank(userAccount.getUserName())
        || StringUtilities.isStringNullOrBlank(userAccount.getPassword())
        || StringUtilities.isStringNullOrBlank(userAccount.getEmail());
  }
}
