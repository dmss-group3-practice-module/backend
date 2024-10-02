/* (C)2024 */
package nus.iss.team3.backend.dataaccess;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nus.iss.team3.backend.dataaccess.postgres.IPostgresDataAccess;
import nus.iss.team3.backend.entity.EUserAccountStatus;
import nus.iss.team3.backend.entity.UserAccount;
import nus.iss.team3.backend.entity.EUserRole;
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
    sqlInput.put("name", userAccount.getName());
    sqlInput.put("password", userAccount.getPassword());
    sqlInput.put("displayName", userAccount.getDisplayName());
    sqlInput.put("email", userAccount.getEmail());
    sqlInput.put("status", userAccount.getStatus().code);
    sqlInput.put("role", userAccount.getRole().code);

//    System.out.println("SQL Input: " + sqlInput);

    // insert ok if returned 1, any other values means insert failed!
    int rowUpdated =
        postgresDataAccess.upsertStatement(PostgresSqlStatement.SQL_USER_ACCOUNT_ADD, sqlInput);
    if (rowUpdated == 1) {
      logger.debug("account created for {}", userAccount.getName());
      return true;
    }
    logger.debug("account creation for {} failed", userAccount.getName());
    return false;
  }

  /**
   * @param userAccount user account to be added
   * @return whether updating the account was successful or not
   */
  @Override
  public boolean updateUser(UserAccount userAccount) {
    Map<String, Object> sqlInput = new HashMap<>();
    sqlInput.put("id", userAccount.getId());
    sqlInput.put("name", userAccount.getName());
    sqlInput.put("password", userAccount.getPassword());
    sqlInput.put("display_name", userAccount.getDisplayName());
    sqlInput.put("email", userAccount.getEmail());
    sqlInput.put("status", userAccount.getStatus().code);
    sqlInput.put("role", userAccount.getRole().code);
    //    sqlInput.put("update_datetime", ZonedDateTime.now());

    // insert ok if returned 1, any other values means insert failed!
    int rowUpdated =
        postgresDataAccess.upsertStatement(PostgresSqlStatement.SQL_USER_ACCOUNT_UPDATE, sqlInput);
    if (rowUpdated == 1) {
      logger.debug("account updated for {}", userAccount.getId());
      return true;
    }
    if (rowUpdated == 0) {
      logger.debug("account update for {} failed, no account found", userAccount.getId());
      // no record for Id found
      return false;
    }
    if (rowUpdated > 1) {
      logger.error(
          "account update for {} happened but multi rows updated, please review",
          userAccount.getId());
    }
    return false;
  }

  /**
   * @param id userId of account to be deleted
   * @return whether the deletion of the account was successful
   */
  @Override
  public boolean deleteUserById(Long id) {

    Map<String, Object> sqlInput = new HashMap<>();
    sqlInput.put(PostgresSqlStatement.INPUT_USER_ACCOUNT_ID, id);

    // insert ok if returned 1, any other values means insert failed!
    int rowUpdated =
        postgresDataAccess.upsertStatement(PostgresSqlStatement.SQL_USER_ACCOUNT_DELETE, sqlInput);
    if (rowUpdated == 1) {
      logger.debug("account deleted for {}", id);
      return true;
    }
    if (rowUpdated == 0) {
      logger.debug("account delete for {} failed, no account found", id);
      // no record for Id found
    }
    if (rowUpdated > 1) {
      logger.error("account delete for {} happened but multi rows was deleted, please review", id);
    }
    return false;
  }

  /**
   * @param id userId of account to be retrieved
   * @return the content of the account that was retrieved
   */
  @Override
  public UserAccount getUserById(Long id) {
    Map<String, Object> sqlInput = new HashMap<>();
    sqlInput.put(PostgresSqlStatement.INPUT_USER_ACCOUNT_ID, id);

    // insert ok if returned 1, any other values means insert failed!
    List<Map<String, Object>> entityReturned =
        postgresDataAccess.queryStatement(
            PostgresSqlStatement.SQL_USER_ACCOUNT_GET_BY_ID, sqlInput);

    if (entityReturned.size() == 1) {
      return translateDBRecordToUserAccount(entityReturned.getFirst());
    }
    if (entityReturned.size() > 1) {
      logger.error("Multiple record found for {}, please review", id);
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
      logger.error("Error retrieving user account from database.");
      return new ArrayList<>();
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

    if (entity.containsKey("id") && (entity.get("id") instanceof Long)) {
      returnItem.setId((Long) entity.get("id"));
    }
    if (entity.containsKey("name") && (entity.get("name") instanceof String)) {
      returnItem.setName((String) entity.get("name"));
    }
    if (entity.containsKey("password") && (entity.get("password") instanceof String)) {
      returnItem.setPassword((String) entity.get("password"));
    }
    if (entity.containsKey("display_name") && (entity.get("display_name") instanceof String)) {
      returnItem.setDisplayName((String) entity.get("display_name"));
    }
    if (entity.containsKey("email") && (entity.get("email") instanceof String)) {
      returnItem.setEmail((String) entity.get("email"));
    }
    if (entity.containsKey("status") && (entity.get("status") instanceof Integer)) {
      returnItem.setStatus(EUserAccountStatus.valueOfCode((Integer) entity.get("status")));
    }
    if (entity.containsKey("role") && (entity.get("role") instanceof Integer)) {
      returnItem.setRole(EUserRole.valueOfCode((Integer) entity.get("role")));
    }
    if (entity.containsKey("create_datetime")
        && (entity.get("create_datetime") instanceof ZonedDateTime)) {
      returnItem.setCreateDateTime((ZonedDateTime) entity.get("create_datetime"));
    }
    if (entity.containsKey("update_datetime")
        && (entity.get("update_datetime") instanceof ZonedDateTime)) {
      returnItem.setUpdateDateTime((ZonedDateTime) entity.get("update_datetime"));
    }

    return returnItem;
  }
}
