/* (C)2024 */
package nus.iss.team3.backend.dataaccess;

import static nus.iss.team3.backend.dataaccess.PostgresSqlStatement.*;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nus.iss.team3.backend.dataaccess.postgres.IPostgresDataAccess;
import nus.iss.team3.backend.entity.EUserAccountStatus;
import nus.iss.team3.backend.entity.EUserRole;
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
    sqlInput.put(INPUT_USER_ACCOUNT_NAME, userAccount.getName());
    sqlInput.put(INPUT_USER_ACCOUNT_PASSWORD, userAccount.getPassword());
    sqlInput.put(INPUT_USER_ACCOUNT_DISPLAY_NAME, userAccount.getDisplayName());
    sqlInput.put(INPUT_USER_ACCOUNT_EMAIL, userAccount.getEmail());
    sqlInput.put(INPUT_USER_ACCOUNT_STATUS, userAccount.getStatus().getCode());
    sqlInput.put(INPUT_USER_ACCOUNT_ROLE, userAccount.getRole().getCode());

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
    sqlInput.put(INPUT_USER_ACCOUNT_ID, userAccount.getId());
    sqlInput.put(INPUT_USER_ACCOUNT_NAME, userAccount.getName());
    sqlInput.put(INPUT_USER_ACCOUNT_PASSWORD, userAccount.getPassword());
    sqlInput.put(INPUT_USER_ACCOUNT_DISPLAY_NAME, userAccount.getDisplayName());
    sqlInput.put(INPUT_USER_ACCOUNT_EMAIL, userAccount.getEmail());
    sqlInput.put(INPUT_USER_ACCOUNT_STATUS, userAccount.getStatus().code);
    sqlInput.put(INPUT_USER_ACCOUNT_ROLE, userAccount.getRole().code);
    //    sqlInput.put("update_datetime", ZonedDateTime.now());

    // insert ok if returned 1, any other values means insert failed!
    int rowUpdated =
        postgresDataAccess.upsertStatement(PostgresSqlStatement.SQL_USER_ACCOUNT_UPDATE, sqlInput);
    if (rowUpdated == 1) {
      logger.debug("account updated for {}", userAccount.getId());
      return true;
    } else if (rowUpdated == 0) {
      logger.debug("account update for {} failed, no account found", userAccount.getId());
      return false;
    } else {
      logger.error(
          "account update for {} affected multiple rows: {}", userAccount.getId(), rowUpdated);
      throw new RuntimeException("Multiple rows affected during update");
    }
  }

  /**
   * @param id userId of account to be deleted
   * @return whether the deletion of the account was successful
   */
  @Override
  public boolean deleteUserById(Integer id) {

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
      // no record for id found
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
  public UserAccount getUserById(Integer id) {
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

    System.out.println(entityReturned);
    if (entityReturned == null) {
      logger.error("Error retrieving user account from database.");
      return new ArrayList<>();
    }
    List<UserAccount> returnList = new ArrayList<>();
    for (Map<String, Object> entity : entityReturned) {
      returnList.add(translateDBRecordToUserAccount(entity));
    }
    //    System.out.println(returnList);
    return returnList;
  }

  // private functions
  private UserAccount translateDBRecordToUserAccount(Map<String, Object> entity) {
    UserAccount returnItem = new UserAccount();

    if (entity.containsKey(COLUMN_USER_ACCOUNT_ID)
        && (entity.get(COLUMN_USER_ACCOUNT_ID) instanceof Integer)) {
      returnItem.setId((Integer) entity.get(COLUMN_USER_ACCOUNT_ID));
    }
    if (entity.containsKey(COLUMN_USER_ACCOUNT_NAME)
        && (entity.get(COLUMN_USER_ACCOUNT_NAME) instanceof String)) {
      returnItem.setName((String) entity.get(COLUMN_USER_ACCOUNT_NAME));
    }
    if (entity.containsKey(COLUMN_USER_ACCOUNT_PASSWORD)
        && (entity.get(COLUMN_USER_ACCOUNT_PASSWORD) instanceof String)) {
      returnItem.setPassword((String) entity.get(COLUMN_USER_ACCOUNT_PASSWORD));
    }
    if (entity.containsKey(COLUMN_USER_ACCOUNT_DISPLAY_NAME)
        && (entity.get(COLUMN_USER_ACCOUNT_DISPLAY_NAME) instanceof String)) {
      returnItem.setDisplayName((String) entity.get(COLUMN_USER_ACCOUNT_DISPLAY_NAME));
    }
    if (entity.containsKey(COLUMN_USER_ACCOUNT_EMAIL)
        && (entity.get(COLUMN_USER_ACCOUNT_EMAIL) instanceof String)) {
      returnItem.setEmail((String) entity.get(COLUMN_USER_ACCOUNT_EMAIL));
    }
    if (entity.containsKey(COLUMN_USER_ACCOUNT_STATUS)
        && (entity.get(COLUMN_USER_ACCOUNT_STATUS) instanceof Integer)) {
      returnItem.setStatus(
          EUserAccountStatus.valueOfCode((Integer) entity.get(COLUMN_USER_ACCOUNT_STATUS)));
    }
    if (entity.containsKey(COLUMN_USER_ACCOUNT_ROLE)
        && (entity.get(COLUMN_USER_ACCOUNT_ROLE) instanceof Integer)) {
      returnItem.setRole(EUserRole.valueOfCode((Integer) entity.get(COLUMN_USER_ACCOUNT_ROLE)));
    }

    logger.info("create datetime  is {}", entity.get(COLUMN_USER_ACCOUNT_CREATE_DATETIME));
    logger.info("update datetime  is {}", entity.get(COLUMN_USER_ACCOUNT_UPDATE_DATETIME));
    logger.info(
        "update datetime class  is {}", entity.get(COLUMN_USER_ACCOUNT_UPDATE_DATETIME).getClass());
    if (entity.containsKey(COLUMN_USER_ACCOUNT_CREATE_DATETIME)
        && (entity.get(COLUMN_USER_ACCOUNT_CREATE_DATETIME) instanceof java.sql.Timestamp)) {
      java.sql.Timestamp timestamp =
          (java.sql.Timestamp) entity.get(COLUMN_USER_ACCOUNT_CREATE_DATETIME);
      returnItem.setCreateDateTime(timestamp.toInstant().atZone(ZoneId.systemDefault()));
    }

    if (entity.containsKey(COLUMN_USER_ACCOUNT_UPDATE_DATETIME)
        && (entity.get(COLUMN_USER_ACCOUNT_UPDATE_DATETIME) instanceof java.sql.Timestamp)) {
      java.sql.Timestamp timestamp =
          (java.sql.Timestamp) entity.get(COLUMN_USER_ACCOUNT_UPDATE_DATETIME);
      returnItem.setUpdateDateTime(timestamp.toInstant().atZone(ZoneId.systemDefault()));
    }

    return returnItem;
  }
}
