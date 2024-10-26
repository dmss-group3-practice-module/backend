/* (C)2024 */
package nus.iss.team3.backend.dataaccess;

import nus.iss.team3.backend.entity.ERecipeStatus;

/**
 * Contains the postgres connection required content for recipe related functionalities.
 *
 * @author Mao Weining
 */
public class PostgresSqlStatementRecipe {

  // User
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

  // Recipe Input parameters
  public static final String INPUT_RECIPE_ID = "id";
  public static final String INPUT_RECIPE_CREATOR_ID = "creator_id";
  public static final String INPUT_RECIPE_NAME = "name";
  public static final String INPUT_RECIPE_IMAGE = "image";
  public static final String INPUT_RECIPE_DESCRIPTION = "description";
  public static final String INPUT_RECIPE_COOKING_TIME = "cookingtimeinsec";
  public static final String INPUT_RECIPE_DIFFICULTY_LEVEL = "difficultylevel";
  public static final String INPUT_RECIPE_RATING = "rating";
  public static final String INPUT_RECIPE_STATUS = "status";
  // TODO: Notice that the name of cuisine column is cuisine
  public static final String INPUT_RECIPE_CUISINE = "cuisine";
  public static final String INPUT_RECIPE_CREATE_TIME = "create_datetime";
  public static final String INPUT_RECIPE_UPDATE_TIME = "update_datetime";

  // Recipe Table and Column names
  public static final String TABLE_RECIPE = "recipe";
  public static final String COLUMN_RECIPE_ID = "id";
  public static final String COLUMN_RECIPE_CREATOR_ID = "creator_id";
  public static final String COLUMN_RECIPE_NAME = "name";
  public static final String COLUMN_RECIPE_IMAGE = "image";
  public static final String COLUMN_RECIPE_DESCRIPTION = "description";
  public static final String COLUMN_RECIPE_COOKING_TIME = "cookingtimeinsec";
  public static final String COLUMN_RECIPE_DIFFICULTY_LEVEL = "difficultylevel";
  public static final String COLUMN_RECIPE_RATING = "rating";
  public static final String COLUMN_RECIPE_STATUS = "status";
  // TODO: Notice that the name of cuisine column is cuisine
  public static final String COLUMN_RECIPE_CUISINE = "cuisine";
  public static final String COLUMN_RECIPE_CREATE_TIME = "create_datetime";
  public static final String COLUMN_RECIPE_UPDATE_TIME = "update_datetime";

  // Ingredient Input parameters
  public static final String INPUT_INGREDIENT_ID = "ingredient_id";
  public static final String INPUT_INGREDIENT_RECIPE_ID = "recipe_id";
  public static final String INPUT_INGREDIENT_NAME = "name";
  public static final String INPUT_INGREDIENT_QUANTITY = "quantity";
  public static final String INPUT_INGREDIENT_UOM = "uom";

  // Ingredient Table and Column names
  public static final String TABLE_INGREDIENT = "recipe_ingredients";
  public static final String COLUMN_INGREDIENT_ID = "id";
  public static final String COLUMN_INGREDIENT_RECIPE_ID = "recipe_id";
  public static final String COLUMN_INGREDIENT_NAME = "name";
  public static final String COLUMN_INGREDIENT_QUANTITY = "quantity";
  public static final String COLUMN_INGREDIENT_UOM = "uom";

  // CookingStep Input parameters
  public static final String INPUT_COOKING_STEP_ID = "cooking_step_id";
  public static final String INPUT_COOKING_STEP_RECIPE_ID = "recipe_id";
  public static final String INPUT_COOKING_STEP_DESCRIPTION = "description";
  public static final String INPUT_COOKING_STEP_IMAGE = "image";

  // CookingStep Table and Column names
  public static final String TABLE_COOKING_STEP = "recipe_cooking_step";
  public static final String COLUMN_COOKING_STEP_ID = "id";
  public static final String COLUMN_COOKING_STEP_RECIPE_ID = "recipe_id";
  public static final String COLUMN_COOKING_STEP_DESCRIPTION = "description";
  public static final String COLUMN_COOKING_STEP_IMAGE = "image";

  // SQL statements for Recipe
  public static final String SQL_RECIPE_ADD =
      "INSERT INTO "
          + TABLE_RECIPE
          + " ("
          + COLUMN_RECIPE_CREATOR_ID
          + ", "
          + COLUMN_RECIPE_NAME
          + ", "
          + COLUMN_RECIPE_IMAGE
          + ", "
          + COLUMN_RECIPE_DESCRIPTION
          + ", "
          + COLUMN_RECIPE_COOKING_TIME
          + ", "
          + COLUMN_RECIPE_DIFFICULTY_LEVEL
          + ", "
          + COLUMN_RECIPE_RATING
          + ", "
          + COLUMN_RECIPE_STATUS
          + ", "
          + COLUMN_RECIPE_CUISINE
          + ", "
          + COLUMN_RECIPE_CREATE_TIME
          + ", "
          + COLUMN_RECIPE_UPDATE_TIME
          + ") "
          + "VALUES (:"
          + INPUT_RECIPE_CREATOR_ID
          + ", :"
          + INPUT_RECIPE_NAME
          + ", :"
          + INPUT_RECIPE_IMAGE
          + ", :"
          + INPUT_RECIPE_DESCRIPTION
          + ", :"
          + INPUT_RECIPE_COOKING_TIME
          + ", :"
          + INPUT_RECIPE_DIFFICULTY_LEVEL
          + ", :"
          + INPUT_RECIPE_RATING
          + ", :"
          + INPUT_RECIPE_STATUS
          + ", :"
          + INPUT_RECIPE_CUISINE
          + ", "
          + "now(), now())";
  public static final String SQL_RECIPE_UPDATE =
      "UPDATE "
          + TABLE_RECIPE
          + " SET "
          + COLUMN_RECIPE_CREATOR_ID
          + " = :"
          + INPUT_RECIPE_CREATOR_ID
          + ", "
          + COLUMN_RECIPE_NAME
          + " = :"
          + INPUT_RECIPE_NAME
          + ", "
          + COLUMN_RECIPE_IMAGE
          + " = :"
          + INPUT_RECIPE_IMAGE
          + ", "
          + COLUMN_RECIPE_DESCRIPTION
          + " = :"
          + INPUT_RECIPE_DESCRIPTION
          + ", "
          + COLUMN_RECIPE_COOKING_TIME
          + " = :"
          + INPUT_RECIPE_COOKING_TIME
          + ", "
          + COLUMN_RECIPE_DIFFICULTY_LEVEL
          + " = :"
          + INPUT_RECIPE_DIFFICULTY_LEVEL
          + ", "
          + COLUMN_RECIPE_RATING
          + " = :"
          + INPUT_RECIPE_RATING
          + ", "
          + COLUMN_RECIPE_STATUS
          + " = :"
          + INPUT_RECIPE_STATUS
          + ", "
          + COLUMN_RECIPE_CUISINE
          + " = :"
          + INPUT_RECIPE_CUISINE
          + ", "
          + COLUMN_RECIPE_UPDATE_TIME
          + " = now() WHERE "
          + COLUMN_RECIPE_ID
          + " = :"
          + INPUT_RECIPE_ID;
  public static final String SQL_RECIPE_DELETE_BY_ID =
      "DELETE FROM " + TABLE_RECIPE + " WHERE " + COLUMN_RECIPE_ID + " = :" + INPUT_RECIPE_ID;
  public static final String SQL_RECIPE_GET_BY_ID =
      "SELECT * FROM " + TABLE_RECIPE + " WHERE " + COLUMN_RECIPE_ID + " = :" + INPUT_RECIPE_ID;
  public static final String SQL_RECIPE_GET_BY_CREATOR_ID =
      "SELECT * FROM "
          + TABLE_RECIPE
          + " WHERE "
          + COLUMN_RECIPE_CREATOR_ID
          + " = :"
          + INPUT_RECIPE_CREATOR_ID;
  public static final String SQL_RECIPE_GET_ALL = "SELECT * FROM " + TABLE_RECIPE;
  public static final String SQL_RECIPE_GET_ALL_PUBLISHED =
      "SELECT * FROM "
          + TABLE_RECIPE
          + " WHERE "
          + COLUMN_RECIPE_STATUS
          + " = "
          + ERecipeStatus.PUBLISHED.code;
  public static final String SQL_RECIPE_GET_BY_NAME =
      "SELECT * FROM "
          + TABLE_RECIPE
          + " WHERE "
          + COLUMN_RECIPE_NAME
          + " ILIKE :"
          + INPUT_RECIPE_NAME;

  // SQL statements for Ingredient
  public static final String SQL_INGREDIENT_ADD =
      "INSERT INTO "
          + TABLE_INGREDIENT
          + " ("
          + COLUMN_INGREDIENT_RECIPE_ID
          + ", "
          + COLUMN_INGREDIENT_NAME
          + ", "
          + COLUMN_INGREDIENT_QUANTITY
          + ", "
          + COLUMN_INGREDIENT_UOM
          + ") "
          + "VALUES (:"
          + INPUT_INGREDIENT_RECIPE_ID
          + ", :"
          + INPUT_INGREDIENT_NAME
          + ", :"
          + INPUT_INGREDIENT_QUANTITY
          + ", :"
          + INPUT_INGREDIENT_UOM
          + ")";
  public static final String SQL_INGREDIENT_UPDATE =
      "UPDATE "
          + TABLE_INGREDIENT
          + " SET "
          + COLUMN_INGREDIENT_NAME
          + " = :"
          + INPUT_INGREDIENT_NAME
          + ", "
          + COLUMN_INGREDIENT_QUANTITY
          + " = :"
          + INPUT_INGREDIENT_QUANTITY
          + ", "
          + COLUMN_INGREDIENT_UOM
          + " = :"
          + INPUT_INGREDIENT_UOM
          + " WHERE "
          + COLUMN_INGREDIENT_ID
          + " = :"
          + INPUT_INGREDIENT_ID;
  public static final String SQL_INGREDIENT_DELETE_BY_RECIPE_ID =
      "DELETE FROM "
          + TABLE_INGREDIENT
          + " WHERE "
          + COLUMN_INGREDIENT_RECIPE_ID
          + " = :"
          + INPUT_INGREDIENT_RECIPE_ID;
  public static final String SQL_INGREDIENT_GET_BY_RECIPE_ID =
      "SELECT * FROM "
          + TABLE_INGREDIENT
          + " WHERE "
          + COLUMN_INGREDIENT_RECIPE_ID
          + " = :"
          + INPUT_INGREDIENT_RECIPE_ID
          + " ORDER BY id";

  // SQL statements for CookingStep
  public static final String SQL_COOKING_STEP_ADD =
      "INSERT INTO "
          + TABLE_COOKING_STEP
          + " ("
          + COLUMN_COOKING_STEP_RECIPE_ID
          + ", "
          + COLUMN_COOKING_STEP_DESCRIPTION
          + ", "
          + COLUMN_COOKING_STEP_IMAGE
          + ") "
          + "VALUES (:"
          + INPUT_COOKING_STEP_RECIPE_ID
          + ", :"
          + INPUT_COOKING_STEP_DESCRIPTION
          + ", :"
          + INPUT_COOKING_STEP_IMAGE
          + ") RETURNING "
          + COLUMN_COOKING_STEP_ID;
  public static final String SQL_COOKING_STEP_UPDATE =
      "UPDATE "
          + TABLE_COOKING_STEP
          + " SET "
          + COLUMN_COOKING_STEP_DESCRIPTION
          + " = :"
          + INPUT_COOKING_STEP_DESCRIPTION
          + ", "
          + COLUMN_COOKING_STEP_IMAGE
          + " = :"
          + INPUT_COOKING_STEP_IMAGE
          + " WHERE "
          + COLUMN_COOKING_STEP_ID
          + " = :"
          + INPUT_COOKING_STEP_ID;
  public static final String SQL_COOKING_STEP_DELETE_RECIPE_ID =
      "DELETE FROM "
          + TABLE_COOKING_STEP
          + " WHERE "
          + COLUMN_COOKING_STEP_RECIPE_ID
          + " = :"
          + INPUT_COOKING_STEP_RECIPE_ID;
  public static final String SQL_COOKING_STEP_GET_BY_RECIPE_ID =
      "SELECT * FROM "
          + TABLE_COOKING_STEP
          + " WHERE "
          + COLUMN_COOKING_STEP_RECIPE_ID
          + " = :"
          + INPUT_COOKING_STEP_RECIPE_ID
          + " ORDER BY id";

  // RecipeReview Input parameters
  public static final String INPUT_REVIEW_ID = "review_id";
  public static final String INPUT_REVIEW_RECIPE_ID = "recipe_id";
  public static final String INPUT_REVIEW_CREATOR_ID = "creator_id";
  public static final String INPUT_REVIEW_RATING = "rating";
  public static final String INPUT_REVIEW_CREATE_TIME = "create_datetime";
  public static final String INPUT_REVIEW_UPDATE_TIME = "update_datetime";
  public static final String INPUT_REVIEW_COMMENTS = "comments";

  // RecipeReview Table and Column names
  public static final String TABLE_REVIEW = "recipe_review";
  public static final String COLUMN_REVIEW_ID = "id";
  public static final String COLUMN_REVIEW_RECIPE_ID = "recipe_id";
  public static final String COLUMN_REVIEW_CREATOR_ID = "creator_id";
  public static final String COLUMN_REVIEW_RATING = "rating";
  public static final String COLUMN_REVIEW_CREATE_TIME = "create_datetime";
  public static final String COLUMN_REVIEW_UPDATE_TIME = "update_datetime";
  public static final String COLUMN_REVIEW_COMMENTS = "comments";

  // SQL statements for RecipeReview
  public static final String SQL_REVIEW_ADD =
      "INSERT INTO "
          + TABLE_REVIEW
          + " ("
          + COLUMN_REVIEW_RECIPE_ID
          + ", "
          + COLUMN_REVIEW_CREATOR_ID
          + ", "
          + COLUMN_REVIEW_RATING
          + ", "
          + COLUMN_REVIEW_CREATE_TIME
          + ", "
          + COLUMN_REVIEW_UPDATE_TIME
          + ", "
          + COLUMN_REVIEW_COMMENTS
          + ") "
          + "VALUES (:"
          + INPUT_REVIEW_RECIPE_ID
          + ", :"
          + INPUT_REVIEW_CREATOR_ID
          + ", :"
          + INPUT_REVIEW_RATING
          + ", now(), now(), :"
          + INPUT_REVIEW_COMMENTS
          + ")";
  public static final String SQL_REVIEW_UPDATE =
      "UPDATE "
          + TABLE_REVIEW
          + " SET "
          + COLUMN_REVIEW_RATING
          + " = :"
          + INPUT_REVIEW_RATING
          + ", "
          + COLUMN_REVIEW_COMMENTS
          + " = :"
          + INPUT_REVIEW_COMMENTS
          + ", "
          + COLUMN_REVIEW_UPDATE_TIME
          + " = now() "
          + "WHERE "
          + COLUMN_REVIEW_RECIPE_ID
          + " = :"
          + INPUT_REVIEW_RECIPE_ID
          + " AND "
          + COLUMN_REVIEW_CREATOR_ID
          + " = :"
          + INPUT_REVIEW_CREATOR_ID;
  public static final String SQL_REVIEW_DELETE =
      "DELETE FROM "
          + TABLE_REVIEW
          + " WHERE "
          + COLUMN_REVIEW_RECIPE_ID
          + " = :"
          + INPUT_REVIEW_RECIPE_ID
          + " AND "
          + COLUMN_REVIEW_CREATOR_ID
          + " = :"
          + INPUT_REVIEW_CREATOR_ID;
  public static final String SQL_REVIEW_DELETE_BY_RECIPE_ID =
      "DELETE FROM "
          + TABLE_REVIEW
          + " WHERE "
          + COLUMN_REVIEW_RECIPE_ID
          + " = :"
          + INPUT_REVIEW_RECIPE_ID;
  public static final String SQL_REVIEW_DELETE_BY_CREATOR_ID =
      "DELETE FROM "
          + TABLE_REVIEW
          + " WHERE "
          + COLUMN_REVIEW_CREATOR_ID
          + " = :"
          + INPUT_REVIEW_CREATOR_ID;
  public static final String SQL_REVIEW_GET_BY_RECIPE_AND_CREATOR =
      "SELECT * FROM "
          + TABLE_REVIEW
          + " WHERE "
          + COLUMN_REVIEW_RECIPE_ID
          + " = :"
          + INPUT_REVIEW_RECIPE_ID
          + " AND "
          + COLUMN_REVIEW_CREATOR_ID
          + " = :"
          + INPUT_REVIEW_CREATOR_ID;
  public static final String SQL_REVIEW_GET_BY_RECIPE_ID =
      "SELECT * FROM "
          + TABLE_REVIEW
          + " WHERE "
          + COLUMN_REVIEW_RECIPE_ID
          + " = :"
          + INPUT_REVIEW_RECIPE_ID;
  public static final String SQL_REVIEW_GET_BY_CREATOR_ID =
      "SELECT * FROM "
          + TABLE_REVIEW
          + " WHERE "
          + COLUMN_REVIEW_CREATOR_ID
          + " = :"
          + INPUT_REVIEW_CREATOR_ID;
}
