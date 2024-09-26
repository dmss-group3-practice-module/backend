/* (C)2024 */
package nus.iss.team3.backend.dataaccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nus.iss.team3.backend.dataaccess.postgres.IPostgresDataAccess;
import nus.iss.team3.backend.entity.EUserAccountStatus;
import nus.iss.team3.backend.entity.UserAccount;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Repository class to connect to postgres for useraccount Data
 *
 * @author Desmond Tan Zhi Heng
 */
@Repository
public class UserAccountDataAccessPostgres implements IUserAccountDataAccess {

  private static final Logger logger = LogManager.getLogger(UserAccountDataAccessPostgres.class);

  @Autowired IPostgresDataAccess postgresDataAccess;

  /**
   * @param userAccount user account to be added
   * @return whether adding the account was successful or not
   */
  @Override
  public boolean addUser(UserAccount userAccount) {

    Map<String, Object> sqlInput = new HashMap<>();
    sqlInput.put(PostgresSqlStatement.INPUT_USER_ACCOUNT_ID, userAccount.getUserId());
    sqlInput.put(PostgresSqlStatement.INPUT_USER_ACCOUNT_NAME, userAccount.getUserName());
    sqlInput.put(PostgresSqlStatement.INPUT_USER_ACCOUNT_PASSWORD, userAccount.getPassword());
    sqlInput.put(PostgresSqlStatement.INPUT_USER_ACCOUNT_EMAIL, userAccount.getEmail());

    // insert ok if returned 1, any other values means insert failed!
    int rowUpdated =
        postgresDataAccess.upsertStatement(PostgresSqlStatement.SQL_USER_ACCOUNT_ADD, sqlInput);
    if (rowUpdated == 1) {
      logger.debug("account created for {}", userAccount.getUserId());
      return true;
    }
    logger.debug("account creation for {} failed", userAccount.getUserId());
    return false;
  }

  /**
   * @param userAccount user account to be added
   * @return whether updating the account was successful or not
   */
  @Override
  public boolean updateUser(UserAccount userAccount) {

    Map<String, Object> sqlInput = new HashMap<>();
    sqlInput.put(PostgresSqlStatement.INPUT_USER_ACCOUNT_ID, userAccount.getUserId());
    sqlInput.put(PostgresSqlStatement.INPUT_USER_ACCOUNT_NAME, userAccount.getUserName());
    sqlInput.put(PostgresSqlStatement.INPUT_USER_ACCOUNT_PASSWORD, userAccount.getPassword());
    sqlInput.put(PostgresSqlStatement.INPUT_USER_ACCOUNT_EMAIL, userAccount.getEmail());
    sqlInput.put(PostgresSqlStatement.INPUT_USER_ACCOUNT_STATUS, userAccount.getStatus().code);

    // insert ok if returned 1, any other values means insert failed!
    int rowUpdated =
        postgresDataAccess.upsertStatement(PostgresSqlStatement.SQL_USER_ACCOUNT_UPDATE, sqlInput);
    if (rowUpdated == 1) {
      logger.debug("account updated for {}", userAccount.getUserId());
      return true;
    }
    if (rowUpdated == 0) {
      logger.debug("account update for {} failed, no account found", userAccount.getUserId());
      // no record for Id found
      return false;
    }
    if (rowUpdated > 1) {
      logger.error(
          "account update for {} happened but multi rows updated, please review",
          userAccount.getUserId());
    }

    return false;
  }

  /**
   * @param userId userId of account to be deleted
   * @return whether the deletion of the account was successful
   */
  @Override
  public boolean deleteUserById(String userId) {

    Map<String, Object> sqlInput = new HashMap<>();
    sqlInput.put(PostgresSqlStatement.INPUT_USER_ACCOUNT_ID, userId);

    // insert ok if returned 1, any other values means insert failed!
    int rowUpdated =
        postgresDataAccess.upsertStatement(PostgresSqlStatement.SQL_USER_ACCOUNT_DELETE, sqlInput);
    if (rowUpdated == 1) {
      logger.debug("account deleted for {}", userId);
      return true;
    }
    if (rowUpdated == 0) {
      logger.debug("account delete for {} failed, no account found", userId);
      // no record for Id found
    }
    if (rowUpdated > 1) {
      logger.error(
          "account delete for {} happened but multi rows was deleted, please review", userId);
    }
    return false;
  }

  /**
   * @param userId userId of account to be retrieved
   * @return the content of the account that was retrieved
   */
  @Override
  public UserAccount getUserById(String userId) {
    Map<String, Object> sqlInput = new HashMap<>();
    sqlInput.put(PostgresSqlStatement.INPUT_USER_ACCOUNT_ID, userId);

    // insert ok if returned 1, any other values means insert failed!
    List<Map<String, Object>> entityReturned =
        postgresDataAccess.queryStatement(
            PostgresSqlStatement.SQL_USER_ACCOUNT_GET_BY_ID, sqlInput);

    // test aikido check...
    entityReturned =
        postgresDataAccess.queryStatement("SELECT * from User where userId =" + userId, sqlInput);
    if (entityReturned.size() == 1) {

      return translateDBRecordToUserAccount(entityReturned.getFirst());
    }
    if (entityReturned.size() > 1) {
      logger.error("Multiple record found for {}, please review", userId);
    }
    return null;
  }

  /**
   * @param email
   * @return
   */
  @Override
  public UserAccount getUserByEmail(String email) {
    Map<String, Object> sqlInput = new HashMap<>();
    sqlInput.put(PostgresSqlStatement.INPUT_USER_ACCOUNT_EMAIL, email);

    // insert ok if returned 1, any other values means insert failed!
    List<Map<String, Object>> entityReturned =
        postgresDataAccess.queryStatement(
            PostgresSqlStatement.SQL_USER_ACCOUNT_GET_BY_EMAIL, sqlInput);

    if (entityReturned.size() == 1) {

      return translateDBRecordToUserAccount(entityReturned.getFirst());
    }
    if (entityReturned.size() > 1) {
      logger.error("Multiple record found with same email {}, please review", email);
    }
    return null;
  }

  /**
   * @return the whole list of user in the systems
   */
  @Override
  public List<UserAccount> getAllUsers() {
    List<Map<String, Object>> entityReturned =
        postgresDataAccess.queryStatement(PostgresSqlStatement.SQL_USER_ACCOUNT_GET_ALL, null);

    if (entityReturned == null) {
      logger.error("Error retriving user account from database.");
    }
    List<UserAccount> returnList = new ArrayList<>();
    for (Map<String, Object> entity : entityReturned) {

      returnList.add(translateDBRecordToUserAccount(entity));
    }
    return returnList;
  }

  // private functions
  private UserAccount translateDBRecordToUserAccount(Map<String, Object> entity) {
    UserAccount returnItem = new UserAccount();

    if (entity.containsKey(PostgresSqlStatement.COLUMN_USER_ACCOUNT_ID)
        && (entity.get(PostgresSqlStatement.COLUMN_USER_ACCOUNT_ID) instanceof String)) {
      returnItem.setUserId((String) entity.get(PostgresSqlStatement.COLUMN_USER_ACCOUNT_ID));
    }
    if (entity.containsKey(PostgresSqlStatement.COLUMN_USER_ACCOUNT_NAME)
        && (entity.get(PostgresSqlStatement.COLUMN_USER_ACCOUNT_NAME) instanceof String)) {
      returnItem.setUserName((String) entity.get(PostgresSqlStatement.COLUMN_USER_ACCOUNT_NAME));
    }
    if (entity.containsKey(PostgresSqlStatement.COLUMN_USER_ACCOUNT_PASSWORD)
        && (entity.get(PostgresSqlStatement.COLUMN_USER_ACCOUNT_PASSWORD) instanceof String)) {
      returnItem.setPassword(
          (String) entity.get(PostgresSqlStatement.COLUMN_USER_ACCOUNT_PASSWORD));
    }
    if (entity.containsKey(PostgresSqlStatement.COLUMN_USER_ACCOUNT_EMAIL)
        && (entity.get(PostgresSqlStatement.COLUMN_USER_ACCOUNT_EMAIL) instanceof String)) {
      returnItem.setEmail((String) entity.get(PostgresSqlStatement.COLUMN_USER_ACCOUNT_EMAIL));
    }
    if (entity.containsKey(PostgresSqlStatement.COLUMN_USER_ACCOUNT_STATUS)
        && (entity.get(PostgresSqlStatement.COLUMN_USER_ACCOUNT_STATUS) instanceof Integer)) {
      returnItem.setStatus(
          EUserAccountStatus.valueOfCode(
              (Integer) entity.get(PostgresSqlStatement.COLUMN_USER_ACCOUNT_STATUS)));
    }
    return returnItem;
  }
}
