/* (C)2024 */
package nus.iss.team3.backend.dataaccess;

/**
 * Contains the postgres connection required content
 *
 * @author Desmond Tan Zhi Heng
 */
public class PostgresSqlStatement {

  public static final String INPUT_USER_ACCOUNT_ID = "userId";
  public static final String INPUT_USER_ACCOUNT_NAME = "userName";
  public static final String INPUT_USER_ACCOUNT_PASSWORD = "userPassword";
  public static final String INPUT_USER_ACCOUNT_EMAIL = "userEmail";
  public static final String INPUT_USER_ACCOUNT_STATUS = "userStatus";

  public static final String TABLE_USER_ACCOUNT = "user_account";

  public static final String COLUMN_USER_ACCOUNT_ID = "user_id";
  public static final String COLUMN_USER_ACCOUNT_NAME = "user_name";
  public static final String COLUMN_USER_ACCOUNT_PASSWORD = "user_password";
  public static final String COLUMN_USER_ACCOUNT_EMAIL = "user_email";
  public static final String COLUMN_USER_ACCOUNT_STATUS = "user_status";

  public static final String SQL_USER_ACCOUNT_ADD =
      "INSERT INTO "
          + TABLE_USER_ACCOUNT
          + "("
          + COLUMN_USER_ACCOUNT_ID
          + ","
          + COLUMN_USER_ACCOUNT_NAME
          + ","
          + COLUMN_USER_ACCOUNT_PASSWORD
          + ","
          + COLUMN_USER_ACCOUNT_EMAIL
          + ","
          + COLUMN_USER_ACCOUNT_STATUS
          + ") VALUES (:"
          + INPUT_USER_ACCOUNT_ID
          + ",:"
          + INPUT_USER_ACCOUNT_NAME
          + ",:"
          + INPUT_USER_ACCOUNT_PASSWORD
          + ",:"
          + INPUT_USER_ACCOUNT_EMAIL
          + ",1);";
  public static final String SQL_USER_ACCOUNT_DELETE =
      "DELETE FROM " + TABLE_USER_ACCOUNT + " WHERE " + COLUMN_USER_ACCOUNT_ID + " = :userId;";
  public static final String SQL_USER_ACCOUNT_UPDATE =
      "UPDATE "
          + TABLE_USER_ACCOUNT
          + " SET "
          + COLUMN_USER_ACCOUNT_NAME
          + " = :"
          + INPUT_USER_ACCOUNT_NAME
          + ", "
          + COLUMN_USER_ACCOUNT_PASSWORD
          + " = :"
          + INPUT_USER_ACCOUNT_PASSWORD
          + ", "
          + COLUMN_USER_ACCOUNT_EMAIL
          + " = :"
          + INPUT_USER_ACCOUNT_EMAIL
          + " WHERE "
          + COLUMN_USER_ACCOUNT_ID
          + " = :"
          + INPUT_USER_ACCOUNT_ID
          + ";";
  public static final String SQL_USER_ACCOUNT_GET_BY_ID =
      "SELECT * FROM user_account where " + COLUMN_USER_ACCOUNT_ID + " = :" + INPUT_USER_ACCOUNT_ID;
  public static final String SQL_USER_ACCOUNT_GET_BY_EMAIL =
      "SELECT * FROM user_account where "
          + COLUMN_USER_ACCOUNT_EMAIL
          + " = :"
          + INPUT_USER_ACCOUNT_EMAIL;
  public static final String SQL_USER_ACCOUNT_GET_ALL = "SELECT * FROM user_account;";
}
