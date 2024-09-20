/* (C)2024 */
package nus.iss.team3.backend.dataaccess;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nus.iss.team3.backend.entity.UserAccount;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * DAO for UserAccount for local caching of data
 *
 * <p>Not in used after 20240919.
 *
 * @author Desmond Tan Zhi Heng
 */
// @Service
public class UserAccountDataAccessLocalCache implements IUserAccountDataAccess {

  private static final Logger logger = LogManager.getLogger(UserAccountDataAccessLocalCache.class);

  Map<String, UserAccount> accountMap = new HashMap<>();

  /**
   * @param userAccount : user to be added to the database
   * @return whether the user was added?
   */
  @Override
  public boolean addUser(UserAccount userAccount) {
    if (userAccount == null
        || userAccount.getUserName() == null
        || userAccount.getUserName().isEmpty()) {
      return false;
    }
    if (accountMap.containsKey(userAccount.getUserId())) {
      return false;
    }
    accountMap.put(userAccount.getUserId(), userAccount);
    return true;
  }

  /**
   * @param userAccount: user to be updated to the database
   * @return whether the user was updated?
   */
  @Override
  public boolean updateUser(UserAccount userAccount) {
    if (userAccount == null
        || userAccount.getUserName() == null
        || userAccount.getUserName().isEmpty()) {
      return false;
    }
    if (!accountMap.containsKey(userAccount.getUserId())) {
      return false;
    }
    accountMap.put(userAccount.getUserId(), userAccount);
    return true;
  }

  /**
   * @param userId: username to be deleted from the database
   * @return whether the user was deleted?
   */
  @Override
  public boolean deleteUserById(String userId) {

    if (!accountMap.containsKey(userId)) {
      return false;
    }
    accountMap.remove(userId);
    return true;
  }

  /**
   * @param userId: userId to be deleted from the database
   * @return whether the user was deleted?
   */
  @Override
  public UserAccount getUserById(String userId) {

    return accountMap.getOrDefault(userId, null);
  }

  /**
   * @param email: email of the user
   * @return the user account for that email
   */
  @Override
  public UserAccount getUserByEmail(String email) {
    for (UserAccount temp : accountMap.values()) {
      if (temp.getEmail().equals(email)) {
        return temp;
      }
    }
    return null;
  }

  /**
   * @return the list of user account details.
   */
  @Override
  public List<UserAccount> getAllUsers() {
    return accountMap.values().stream().toList();
  }
}
