package nus.iss.team3.backend.dataaccess;

/**
 * Contains the postgres connection required content
 *
 * @author Desmond Tan Zhi Heng, Liu Kun
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
          + ","
          + "now()"
          + ","
          + "now());";
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
          + COLUMN_USER_ACCOUNT_EMAIL
          + " = :"
          + INPUT_USER_ACCOUNT_EMAIL
          + " WHERE "
          + COLUMN_USER_ACCOUNT_ID
          + " = :"
          + INPUT_USER_ACCOUNT_ID
          + ";";

  public static final String SQL_USER_ACCOUNT_GET_BY_ID =
      "SELECT "
          + COLUMN_USER_ACCOUNT_ID
          + ", "
          + COLUMN_USER_ACCOUNT_NAME
          + ", "
          + COLUMN_USER_ACCOUNT_DISPLAY_NAME
          + ", "
          + COLUMN_USER_ACCOUNT_EMAIL
          + ", "
          + COLUMN_USER_ACCOUNT_STATUS
          + ", "
          + COLUMN_USER_ACCOUNT_ROLE
          + ", "
          + COLUMN_USER_ACCOUNT_CREATE_DATETIME
          + ", "
          + COLUMN_USER_ACCOUNT_UPDATE_DATETIME
          + " FROM "
          + TABLE_USER_ACCOUNT
          + " WHERE "
          + COLUMN_USER_ACCOUNT_ID
          + " = :"
          + INPUT_USER_ACCOUNT_ID
          + ";";

  public static final String SQL_USER_ACCOUNT_GET_BY_NAME =
      "SELECT "
          + COLUMN_USER_ACCOUNT_ID
          + ", "
          + COLUMN_USER_ACCOUNT_NAME
          + ", "
          + COLUMN_USER_ACCOUNT_DISPLAY_NAME
          + ", "
          + COLUMN_USER_ACCOUNT_EMAIL
          + ", "
          + COLUMN_USER_ACCOUNT_STATUS
          + ", "
          + COLUMN_USER_ACCOUNT_ROLE
          + ", "
          + COLUMN_USER_ACCOUNT_CREATE_DATETIME
          + ", "
          + COLUMN_USER_ACCOUNT_UPDATE_DATETIME
          + " FROM "
          + TABLE_USER_ACCOUNT
          + " WHERE "
          + COLUMN_USER_ACCOUNT_NAME
          + " = :"
          + INPUT_USER_ACCOUNT_NAME
          + ";";

  public static final String SQL_USER_ACCOUNT_GET_BY_EMAIL =
      "SELECT "
          + COLUMN_USER_ACCOUNT_ID
          + ", "
          + COLUMN_USER_ACCOUNT_NAME
          + ", "
          + COLUMN_USER_ACCOUNT_DISPLAY_NAME
          + ", "
          + COLUMN_USER_ACCOUNT_EMAIL
          + ", "
          + COLUMN_USER_ACCOUNT_STATUS
          + ", "
          + COLUMN_USER_ACCOUNT_ROLE
          + ", "
          + COLUMN_USER_ACCOUNT_CREATE_DATETIME
          + ", "
          + COLUMN_USER_ACCOUNT_UPDATE_DATETIME
          + " FROM "
          + TABLE_USER_ACCOUNT
          + " WHERE "
          + COLUMN_USER_ACCOUNT_EMAIL
          + " = :"
          + INPUT_USER_ACCOUNT_EMAIL
          + ";";

  public static final String SQL_USER_ACCOUNT_GET_ALL =
      "SELECT "
          + COLUMN_USER_ACCOUNT_ID
          + ", "
          + COLUMN_USER_ACCOUNT_NAME
          + ", "
          + COLUMN_USER_ACCOUNT_DISPLAY_NAME
          + ", "
          + COLUMN_USER_ACCOUNT_EMAIL
          + ", "
          + COLUMN_USER_ACCOUNT_STATUS
          + ", "
          + COLUMN_USER_ACCOUNT_ROLE
          + ", "
          + COLUMN_USER_ACCOUNT_CREATE_DATETIME
          + ", "
          + COLUMN_USER_ACCOUNT_UPDATE_DATETIME
          + " FROM "
          + TABLE_USER_ACCOUNT
          + ";";

  //////////////////////////////
  // Ingredient
  public static final String INPUT_INGREDIENT_ID = "id";
  public static final String INPUT_INGREDIENT_USER_ACCOUNT_ID = "userId";
  public static final String INPUT_INGREDIENT_NAME = "name";
  public static final String INPUT_USER_ID = "userId";
  public static final String INPUT_INGREDIENT_UOM = "uom";
  public static final String INPUT_INGREDIENT_QUANTITY = "quantity";
  public static final String INPUT_INGREDIENT_EXPIRY_DATE = "expiryDate";

  public static final String TABLE_INGREDIENT = "user_ingredients";

  public static final String COLUMN_INGREDIENT_ID = "id";
  public static final String COLUMN_INGREDIENT_NAME = "name";
  public static final String COLUMN_USER_ID = "user_id";
  public static final String COLUMN_INGREDIENT_UOM = "uom";
  public static final String COLUMN_INGREDIENT_QUANTITY = "quantity";
  public static final String COLUMN_INGREDIENT_EXPIRY_DATE = "expiry_date";
  public static final String COLUMN_INGREDIENT_CREATE_DATETIME = "create_datetime";
  public static final String COLUMN_INGREDIENT_UPDATE_DATETIME = "update_datetime";

  public static final String SQL_INGREDIENT_ADD =
      "INSERT INTO "
          + TABLE_INGREDIENT
          + "("
          + COLUMN_INGREDIENT_NAME
          + ","
          + COLUMN_USER_ID
          + ","
          + COLUMN_INGREDIENT_UOM
          + ","
          + COLUMN_INGREDIENT_QUANTITY
          + ","
          + COLUMN_INGREDIENT_EXPIRY_DATE
          + ","
          + COLUMN_INGREDIENT_CREATE_DATETIME
          + ","
          + COLUMN_INGREDIENT_UPDATE_DATETIME
          + ") VALUES (:"
          + INPUT_INGREDIENT_NAME
          + ",:"
          + INPUT_USER_ID
          + ",:"
          + INPUT_INGREDIENT_UOM
          + ",:"
          + INPUT_INGREDIENT_QUANTITY
          + ",:"
          + INPUT_INGREDIENT_EXPIRY_DATE
          + ",now(),now());";

  public static final String SQL_INGREDIENT_DELETE =
      "DELETE FROM "
          + TABLE_INGREDIENT
          + " WHERE "
          + COLUMN_INGREDIENT_ID
          + " = :"
          + INPUT_INGREDIENT_ID;

  public static final String SQL_INGREDIENT_UPDATE =
      "UPDATE "
          + TABLE_INGREDIENT
          + " SET "
          + COLUMN_INGREDIENT_NAME
          + " = :"
          + INPUT_INGREDIENT_NAME
          + ", "
          + COLUMN_INGREDIENT_UOM
          + " = :"
          + INPUT_INGREDIENT_UOM
          + ", "
          + COLUMN_INGREDIENT_QUANTITY
          + " = :"
          + INPUT_INGREDIENT_QUANTITY
          + ", "
          + COLUMN_INGREDIENT_EXPIRY_DATE
          + " = :"
          + INPUT_INGREDIENT_EXPIRY_DATE
          + ", "
          + COLUMN_INGREDIENT_UPDATE_DATETIME
          + " = "
          + "now()"
          + " WHERE "
          + COLUMN_INGREDIENT_ID
          + " = :"
          + INPUT_INGREDIENT_ID
          + " AND "
          + COLUMN_USER_ID
          + " = :"
          + INPUT_INGREDIENT_USER_ACCOUNT_ID
          + ";";

  public static final String SQL_INGREDIENT_GET_BY_ID =
      "SELECT * FROM "
          + TABLE_INGREDIENT
          + " WHERE "
          + COLUMN_INGREDIENT_ID
          + " = :"
          + INPUT_INGREDIENT_ID;

  public static final String SQL_INGREDIENTS_GET_BY_USER_ID =
      "SELECT * FROM " + TABLE_INGREDIENT + " WHERE " + COLUMN_USER_ID + " = :" + INPUT_USER_ID;

  public static final String SQL_INGREDIENTS_DELETE_BY_USER_ID =
      "DELETE FROM " + TABLE_INGREDIENT + " WHERE " + COLUMN_USER_ID + " = :" + INPUT_USER_ID;
}