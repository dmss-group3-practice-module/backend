/* (C)2024 */
package nus.iss.team3.backend.dataaccess;

/**
 * Contains the postgres connection required content
 *
 * @author Desmond Tan Zhi Heng
 */
public class PostgresSqlStatement {

  // Input parameter names
  public static final String INPUT_USER_ACCOUNT_ID = "id";
  public static final String INPUT_USER_ACCOUNT_NAME = "name";
  public static final String INPUT_USER_ACCOUNT_PASSWORD = "password";
  public static final String INPUT_USER_ACCOUNT_DISPLAY_NAME = "displayName";
  public static final String INPUT_USER_ACCOUNT_EMAIL = "email";
  public static final String INPUT_USER_ACCOUNT_STATUS = "status";
  public static final String INPUT_USER_ACCOUNT_ROLE = "role";
  public static final String INPUT_USER_ACCOUNT_CREATE_DATETIME = "createDatetime";
  public static final String INPUT_USER_ACCOUNT_UPDATE_DATETIME = "updateDatetime";

  // Table name
  public static final String TABLE_USER_ACCOUNT = "user_account";

  // Column names
  public static final String COLUMN_USER_ACCOUNT_ID = "id";
  public static final String COLUMN_USER_ACCOUNT_NAME = "name";
  public static final String COLUMN_USER_ACCOUNT_PASSWORD = "password";
  public static final String COLUMN_USER_ACCOUNT_DISPLAY_NAME = "display_name";
  public static final String COLUMN_USER_ACCOUNT_EMAIL = "email";
  public static final String COLUMN_USER_ACCOUNT_STATUS = "status";
  public static final String COLUMN_USER_ACCOUNT_ROLE = "role";
  public static final String COLUMN_USER_ACCOUNT_CREATE_DATETIME = "create_datetime";
  public static final String COLUMN_USER_ACCOUNT_UPDATE_DATETIME = "update_datetime";

  public static final String SQL_USER_ACCOUNT_ADD =
      "INSERT INTO "
          + TABLE_USER_ACCOUNT
          + "("
          + COLUMN_USER_ACCOUNT_NAME
          + ","
          + COLUMN_USER_ACCOUNT_PASSWORD
          + ","
          + COLUMN_USER_ACCOUNT_DISPLAY_NAME
          + ","
          + COLUMN_USER_ACCOUNT_EMAIL
          + ","
          + COLUMN_USER_ACCOUNT_STATUS
          + ","
          + COLUMN_USER_ACCOUNT_ROLE
          + ","
          + COLUMN_USER_ACCOUNT_CREATE_DATETIME
          + ","
          + COLUMN_USER_ACCOUNT_UPDATE_DATETIME
          + ") VALUES (:"
          + INPUT_USER_ACCOUNT_NAME
          + ",:"
          + INPUT_USER_ACCOUNT_PASSWORD
          + ",:"
          + INPUT_USER_ACCOUNT_DISPLAY_NAME
          + ",:"
          + INPUT_USER_ACCOUNT_EMAIL
          + ",:"
          + INPUT_USER_ACCOUNT_STATUS
          + ",:"
          + INPUT_USER_ACCOUNT_ROLE
          + ",now(),now());";
  public static final String SQL_USER_ACCOUNT_DELETE =
      "DELETE FROM "
          + TABLE_USER_ACCOUNT
          + " WHERE "
          + COLUMN_USER_ACCOUNT_ID
          + " = :"
          + INPUT_USER_ACCOUNT_ID
          + ";";
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
          + COLUMN_USER_ACCOUNT_DISPLAY_NAME
          + " = :"
          + INPUT_USER_ACCOUNT_DISPLAY_NAME
          + ", "
          + COLUMN_USER_ACCOUNT_EMAIL
          + " = :"
          + INPUT_USER_ACCOUNT_EMAIL
          + ", "
          + COLUMN_USER_ACCOUNT_STATUS
          + " = :"
          + INPUT_USER_ACCOUNT_STATUS
          + ", "
          + COLUMN_USER_ACCOUNT_ROLE
          + " = :"
          + INPUT_USER_ACCOUNT_ROLE
          + ", "
          + COLUMN_USER_ACCOUNT_UPDATE_DATETIME
          + " = "
          + "now()"
          + " WHERE "
          + COLUMN_USER_ACCOUNT_ID
          + " = :"
          + INPUT_USER_ACCOUNT_ID
          + ";";

  public static final String SQL_USER_ACCOUNT_GET_BY_ID =
      "SELECT * FROM "
          + TABLE_USER_ACCOUNT
          + " WHERE "
          + COLUMN_USER_ACCOUNT_ID
          + " = :"
          + INPUT_USER_ACCOUNT_ID
          + ";";

  public static final String SQL_USER_ACCOUNT_GET_BY_EMAIL =
      "SELECT * FROM "
          + TABLE_USER_ACCOUNT
          + " WHERE "
          + COLUMN_USER_ACCOUNT_EMAIL
          + " = :"
          + INPUT_USER_ACCOUNT_EMAIL
          + ";";

  public static final String SQL_USER_ACCOUNT_GET_ALL = "SELECT * FROM " + TABLE_USER_ACCOUNT + ";";
}
