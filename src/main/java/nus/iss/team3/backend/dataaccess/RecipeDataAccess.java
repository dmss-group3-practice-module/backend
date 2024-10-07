package nus.iss.team3.backend.dataaccess;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nus.iss.team3.backend.dataaccess.postgres.PostgresDataAccess;
import nus.iss.team3.backend.entity.CookingStep;
import nus.iss.team3.backend.entity.Ingredient;
import nus.iss.team3.backend.entity.Recipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository class to connect to postgres for recipe data.
 *
 * @author Mao Weining
 */
@Repository // 标识这是一个仓库组件
public class RecipeDataAccess implements IRecipeDataAccess {

  private static final Logger logger = LogManager.getLogger(RecipeDataAccess.class);
  private final PostgresDataAccess postgresDataAccess;

  // 构造函数用于依赖注入 PostgresDataAccess
  public RecipeDataAccess(PostgresDataAccess postgresDataAccess) {
    this.postgresDataAccess = postgresDataAccess;
  }

  @Override
  @Transactional // 确保整个方法在一个事务中执行
  public boolean addRecipe(Recipe recipe) {
    logger.info("开始添加食谱: {}", recipe.getName());
    try {
      // 验证食谱
      validateRecipe(recipe);
      logger.debug("添加食谱：食谱验证通过");

      // 构建食谱参数的映射
      Map<String, Object> recipeParams = buildRecipeParams(recipe);

      // 插入食谱的 SQL 语句，并返回生成的 id
      String sql =
          "INSERT INTO recipe (creator_id, name, image, description, cookingtimeinsec, "
              + "difficultylevel, rating, status, create_datetime, update_datetime) "
              + "VALUES (:creator_id, :name, :image, :description, :cookingtimeinsec, "
              + ":difficultylevel, :rating, :status, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) RETURNING id";

      // 执行插入语句
      List<Map<String, Object>> result = postgresDataAccess.queryStatement(sql, recipeParams);

      if (result.isEmpty()) {
        logger.warn("插入食谱失败，未返回生成的ID");
        return false; // 如果插入失败，返回 false
      }

      // 获取生成的食谱 ID 并设置到 recipe 对象中
      Long recipeId = ((Number) result.getFirst().get("id")).longValue();
      recipe.setId(recipeId);
      logger.debug("食谱插入成功，生成的ID: {}", recipeId);

      // 在插入配料之前，检查集合是否为空，避免不必要的数据库操作
      if (recipe.getIngredients() != null && !recipe.getIngredients().isEmpty()) {
        insertIngredients(recipe);
        logger.debug("配料插入成功");
      } else {
        logger.debug("食谱无配料需要插入");
      }

      // 在插入烹饪步骤之前，检查集合是否为空，避免不必要的数据库操作
      if (recipe.getCookingSteps() != null && !recipe.getCookingSteps().isEmpty()) {
        insertCookingSteps(recipe);
        logger.debug("烹饪步骤插入成功");
      } else {
        logger.debug("食谱无烹饪步骤需要插入");
      }

      logger.info("食谱添加完成: ID={}", recipeId);
      return true;
    } catch (Exception e) {
      logger.error("添加食谱时发生异常: {}", e.getMessage(), e);
      throw e; // 重新抛出异常以触发事务回滚
    }
  }

  @Override
  @Transactional // 确保整个方法在一个事务中执行
  public boolean updateRecipe(Recipe recipe) {
    logger.info("开始更新食谱: ID={}", recipe.getId());
    try {
      // 验证食谱
      validateRecipe(recipe);
      logger.debug("更新食谱：食谱验证通过");

      // 构建食谱参数的映射
      Map<String, Object> recipeParams = buildRecipeParams(recipe);
      recipeParams.put("id", recipe.getId()); // 将食谱 ID 添加到参数中

      // 更新食谱的 SQL 语句
      String sql =
          "UPDATE recipe SET creator_id = :creator_id, name = :name, image = :image, "
              + "description = :description, cookingtimeinsec = :cookingtimeinsec, "
              + "difficultylevel = :difficultylevel, rating = :rating, status = :status, "
              + "update_datetime = CURRENT_TIMESTAMP WHERE id = :id";

      // 执行更新语句
      int updatedRows = postgresDataAccess.upsertStatement(sql, recipeParams);

      if (updatedRows == 0) {
        logger.warn("未找到ID={} 的食谱进行更新", recipe.getId());
        return false; // 如果没有行被更新，返回 false
      }
      logger.debug("食谱更新成功，更新的行数: {}", updatedRows);

      // 更新配料：先删除现有的配料，再插入新的配料
      deleteIngredients(recipe.getId());
      insertIngredients(recipe);
      logger.debug("配料更新成功");

      // 更新烹饪步骤：先删除现有的步骤，再插入新的步骤
      deleteCookingSteps(recipe.getId());
      insertCookingSteps(recipe);
      logger.debug("烹饪步骤更新成功");

      logger.info("食谱更新完成: ID={}", recipe.getId());
      return true;
    } catch (Exception e) {
      logger.error("更新食谱时发生异常: {}", e.getMessage(), e);
      throw e; // 重新抛出异常以触发事务回滚
    }
  }

  @Override
  @Transactional // 确保整个方法在一个事务中执行
  public boolean deleteRecipeById(Long recipeId) {
    logger.info("开始删除食谱: ID={}", recipeId);
    try {
      // 删除相关的配料
      deleteIngredients(recipeId);
      logger.debug("配料删除成功");

      // 删除相关烹饪步骤
      deleteCookingSteps(recipeId);
      logger.debug("烹饪步骤删除成功");

      // 删除食谱的 SQL 语句
      String deleteRecipeSql = "DELETE FROM recipe WHERE id = :id";
      Map<String, Object> deleteRecipeParams = new HashMap<>();
      deleteRecipeParams.put("id", recipeId);

      // 执行删除语句
      int deletedRows = postgresDataAccess.upsertStatement(deleteRecipeSql, deleteRecipeParams);

      if (deletedRows > 0) {
        logger.info("食谱删除成功: ID={}", recipeId);
        return true;
      } else {
        logger.warn("未找到ID={} 的食谱进行删除", recipeId);
        return false;
      }
    } catch (Exception e) {
      logger.error("删除食谱时发生异常: {}", e.getMessage(), e);
      throw e; // 重新抛出异常以触发事务回滚
    }
  }

  @Override
  public Recipe getRecipeById(Long recipeId) {
    logger.info("查询食谱: ID={}", recipeId);
    try {
      // 查询食谱的 SQL 语句
      String sql = "SELECT * FROM recipe WHERE id = :id";
      Map<String, Object> params = new HashMap<>();
      params.put("id", recipeId);

      // 执行查询
      List<Map<String, Object>> result = postgresDataAccess.queryStatement(sql, params);

      if (result.isEmpty()) {
        logger.warn("未找到ID={} 的食谱", recipeId);
        return null; // 如果没有找到食谱，返回 null
      }

      // 将查询结果映射为 Recipe 对象
      Recipe recipe = mapToRecipe(result.getFirst());
      logger.debug("食谱映射成功: ID={}", recipeId);

      // 查询并设置食谱的配料
      recipe.setIngredients(getIngredientsForRecipe(recipeId));
      logger.debug("配料加载成功: 食谱ID={}", recipeId);

      // 查询并设置食谱的烹饪步骤
      recipe.setCookingSteps(getCookingStepsForRecipe(recipeId));
      logger.debug("烹饪步骤加载成功: 食谱ID={}", recipeId);

      logger.info("食谱查询完成: ID={}", recipeId);
      return recipe;
    } catch (Exception e) {
      logger.error("查询食谱时发生异常: {}", e.getMessage(), e);
      throw e;
    }
  }

  @Override
  public List<Recipe> getAllRecipes() {
    logger.info("查询所有食谱");
    try {
      // 查询所有食谱的 SQL 语句
      String sql = "SELECT * FROM recipe";

      // 执行查询
      List<Map<String, Object>> result = postgresDataAccess.queryStatement(sql, null);

      List<Recipe> recipes = new ArrayList<>();
      for (Map<String, Object> row : result) {
        //        logger.debug("对象信息: {}", row.toString());
        // 将每一行映射为 Recipe 对象
        Recipe recipe = mapToRecipe(row);

        // 查询并设置食谱的配料
        recipe.setIngredients(getIngredientsForRecipe(recipe.getId()));

        // 查询并设置食谱的烹饪步骤
        recipe.setCookingSteps(getCookingStepsForRecipe(recipe.getId()));

        recipes.add(recipe); // 添加到食谱列表
      }

      logger.info("查询所有食谱完成，数量: {}", recipes.size());
      return recipes;
    } catch (Exception e) {
      logger.error("查询所有食谱时发生异常: {}", e.getMessage(), e);
      throw e;
    }
  }

  @Override
  public List<Recipe> getRecipesByName(String name) {
    logger.info("按名称查询食谱: 名称包含 '{}'", name);
    try {
      // 按名称模糊查询食谱的 SQL 语句
      String sql = "SELECT * FROM recipe WHERE name ILIKE :name";
      Map<String, Object> params = new HashMap<>();
      params.put("name", "%" + name + "%"); // 使用通配符进行模糊查询

      // 执行查询
      List<Map<String, Object>> result = postgresDataAccess.queryStatement(sql, params);

      List<Recipe> recipes = new ArrayList<>();
      for (Map<String, Object> row : result) {
        // 将每一行映射为 Recipe 对象
        Recipe recipe = mapToRecipe(row);

        // 查询并设置食谱的配料
        recipe.setIngredients(getIngredientsForRecipe(recipe.getId()));

        // 查询并设置食谱的烹饪步骤
        recipe.setCookingSteps(getCookingStepsForRecipe(recipe.getId()));

        recipes.add(recipe); // 添加到食谱列表
      }

      logger.info("按名称查询食谱完成，找到 {} 条记录", recipes.size());
      return recipes;
    } catch (Exception e) {
      logger.error("按名称查询食谱时发生异常: {}", e.getMessage(), e);
      throw e;
    }
  }

  // 辅助方法：验证食谱
  private void validateRecipe(Recipe recipe) {
    logger.debug("验证食谱: {}", recipe.getName());
    if (recipe.getCreatorId() == null
        || recipe.getName() == null
        || recipe.getCookingTimeInSec() == null
        || recipe.getDifficultyLevel() == null
        || recipe.getRating() == null
        || recipe.getStatus() == null) {
      logger.error("食谱验证失败，存在必填字段为空");
      throw new IllegalArgumentException("Required fields cannot be null");
    }
  }

  // 辅助方法：构建食谱参数的映射
  private Map<String, Object> buildRecipeParams(Recipe recipe) {
    Map<String, Object> recipeParams = new HashMap<>();
    recipeParams.put("creator_id", recipe.getCreatorId());
    recipeParams.put("name", recipe.getName());
    recipeParams.put("image", recipe.getImage());
    recipeParams.put("description", recipe.getDescription());
    recipeParams.put("cookingtimeinsec", recipe.getCookingTimeInSec());
    recipeParams.put("difficultylevel", recipe.getDifficultyLevel());
    recipeParams.put("rating", recipe.getRating());
    recipeParams.put("status", recipe.getStatus());
    return recipeParams;
  }

  // 辅助方法：验证配料
  private void validateIngredient(Ingredient ingredient) {
    logger.debug("验证配料: {}", ingredient.getName());
    if (ingredient.getName() == null
        || ingredient.getQuantity() == null
        || ingredient.getUom() == null) {
      logger.error("配料验证失败，存在必填字段为空");
      throw new IllegalArgumentException("Required fields for ingredient cannot be null");
    }
  }

  // 辅助方法：插入与食谱关联的配料
  private void insertIngredients(Recipe recipe) {
    logger.info("开始插入配料，食谱ID={}", recipe.getId());

    // 检查配料是否为 null
    if (recipe.getIngredients() == null) {
      logger.info("配料为 null，跳过插入，食谱ID={}", recipe.getId());
      return; // 直接返回，不执行插入
    }

    String ingredientSql =
        "INSERT INTO recipe_ingredients (recipe_id, name, quantity, uom) "
            + "VALUES (:recipe_id, :name, :quantity, :uom)";
    for (Ingredient ingredient : recipe.getIngredients()) {
      // 验证配料
      validateIngredient(ingredient);

      Map<String, Object> ingredientParams = new HashMap<>();
      ingredientParams.put("recipe_id", recipe.getId());
      ingredientParams.put("name", ingredient.getName());
      ingredientParams.put("quantity", ingredient.getQuantity());
      ingredientParams.put("uom", ingredient.getUom());
      postgresDataAccess.upsertStatement(ingredientSql, ingredientParams); // 执行插入
      logger.debug("插入配料: {}", ingredient.getName());
    }
    logger.info("配料插入完成，食谱ID={}", recipe.getId());
  }

  // 辅助方法：删除与食谱关联的配料
  private void deleteIngredients(Long recipeId) {
    logger.info("开始删除配料，食谱ID={}", recipeId);
    String deleteIngredientsSql = "DELETE FROM recipe_ingredients WHERE recipe_id = :recipe_id";
    Map<String, Object> deleteIngredientsParams = new HashMap<>();
    deleteIngredientsParams.put("recipe_id", recipeId);
    postgresDataAccess.upsertStatement(deleteIngredientsSql, deleteIngredientsParams); // 执行删除
    logger.debug("配料删除完成，食谱ID={}", recipeId);
  }

  // 辅助方法：验证烹饪步骤
  private void validateCookingStep(CookingStep step) {
    logger.debug("验证烹饪步骤");
    if (step.getDescription() == null) {
      logger.error("烹饪步骤验证失败，描述为空");
      throw new IllegalArgumentException("Description for cooking step cannot be null");
    }
  }

  // 辅助方法：插入与食谱关联的烹饪步骤
  private void insertCookingSteps(Recipe recipe) {
    logger.info("开始插入烹饪步骤，食谱ID={}", recipe.getId());

    // 检查配料是否为 null
    if (recipe.getCookingSteps() == null) {
      logger.info("烹饪步骤为 null，跳过插入，食谱ID={}", recipe.getId());
      return; // 直接返回，不执行插入
    }

    String stepSql =
        "INSERT INTO recipe_cooking_step (recipe_id, description, image) "
            + "VALUES (:recipe_id, :description, :image)";
    for (CookingStep step : recipe.getCookingSteps()) {
      // 验证非空字段
      validateCookingStep(step);
      Map<String, Object> stepParams = new HashMap<>();
      stepParams.put("recipe_id", recipe.getId());
      stepParams.put("description", step.getDescription());
      stepParams.put("image", step.getImage());
      postgresDataAccess.upsertStatement(stepSql, stepParams); // 执行插入
      logger.debug("插入烹饪步骤: {}", step.getDescription());
    }
    logger.info("烹饪步骤插入完成，食谱ID={}", recipe.getId());
  }

  // 辅助方法：删除与食谱关联的烹饪步骤
  private void deleteCookingSteps(Long recipeId) {
    logger.info("开始删除烹饪步骤，食谱ID={}", recipeId);
    String deleteStepsSql = "DELETE FROM recipe_cooking_step WHERE recipe_id = :recipe_id";
    Map<String, Object> deleteStepsParams = new HashMap<>();
    deleteStepsParams.put("recipe_id", recipeId);
    postgresDataAccess.upsertStatement(deleteStepsSql, deleteStepsParams); // 执行删除
    logger.debug("烹饪步骤删除完成，食谱ID={}", recipeId);
  }

  // 辅助方法：将数据库记录映射为 Recipe 对象
  private Recipe mapToRecipe(Map<String, Object> row) {
    logger.debug("映射数据库记录到 Recipe 对象");
    logger.info("mapToRecipe: {}", row);
    Recipe recipe = new Recipe();

    recipe.setId(row.get("id") != null ? ((Number) row.get("id")).longValue() : null);
    recipe.setCreatorId(
        row.get("creator_id") != null ? ((Number) row.get("creator_id")).longValue() : null);
    recipe.setName((String) row.get("name")); // 允许为 null
    recipe.setImage((String) row.get("image")); // 允许为 null
    recipe.setDescription((String) row.get("description")); // 允许为 null
    recipe.setCookingTimeInSec(
        row.get("cookingtimeinsec") != null
            ? ((Number) row.get("cookingtimeinsec")).intValue()
            : null);
    recipe.setDifficultyLevel(
        row.get("difficultylevel") != null
            ? ((Number) row.get("difficultylevel")).intValue()
            : null);
    recipe.setRating(row.get("rating") != null ? ((Number) row.get("rating")).doubleValue() : null);
    recipe.setStatus(row.get("status") != null ? ((Number) row.get("status")).intValue() : null);
    recipe.setCreateDatetime((Timestamp) row.get("create_datetime")); // 允许为 null
    recipe.setUpdateDatetime((Timestamp) row.get("update_datetime")); // 允许为 null

    logger.debug("Recipe 对象映射完成: ID={}", recipe.getId());
    return recipe;
  }

  // 辅助方法：获取与食谱关联的配料
  private List<Ingredient> getIngredientsForRecipe(Long recipeId) {
    logger.info("获取配料，食谱ID={}", recipeId);
    String sql = "SELECT * FROM recipe_ingredients WHERE recipe_id = :recipe_id ORDER BY id";
    Map<String, Object> params = new HashMap<>();
    params.put("recipe_id", recipeId);

    // 执行查询
    List<Map<String, Object>> result = postgresDataAccess.queryStatement(sql, params);

    List<Ingredient> ingredients = new ArrayList<>();
    for (Map<String, Object> row : result) {
      Ingredient ingredient = new Ingredient();
      ingredient.setId(row.get("id") != null ? ((Number) row.get("id")).longValue() : null);
      ingredient.setRecipeId(
          row.get("recipe_id") != null ? ((Number) row.get("recipe_id")).longValue() : null);
      ingredient.setName((String) row.get("name")); // 允许为 null
      ingredient.setQuantity(
          row.get("quantity") != null ? ((Number) row.get("quantity")).doubleValue() : null);
      ingredient.setUom((String) row.get("uom")); // 允许为 null
      ingredients.add(ingredient); // 添加到配料列表
      logger.debug("加载配料: {}", ingredient.getName());
    }

    logger.info("配料加载完成，数量={}", ingredients.size());
    return ingredients;
  }

  // 辅助方法：获取与食谱关联的烹饪步骤
  private List<CookingStep> getCookingStepsForRecipe(Long recipeId) {
    logger.info("获取烹饪步骤，食谱ID={}", recipeId);
    String sql = "SELECT * FROM recipe_cooking_step WHERE recipe_id = :recipe_id ORDER BY id";
    Map<String, Object> params = new HashMap<>();
    params.put("recipe_id", recipeId);

    // 执行查询
    List<Map<String, Object>> result = postgresDataAccess.queryStatement(sql, params);

    List<CookingStep> steps = new ArrayList<>();
    for (Map<String, Object> row : result) {
      CookingStep step = new CookingStep();
      step.setId(row.get("id") != null ? ((Number) row.get("id")).longValue() : null);
      step.setRecipeId(
          row.get("recipe_id") != null ? ((Number) row.get("recipe_id")).longValue() : null);
      step.setDescription((String) row.get("description")); // 允许为 null
      step.setImage((String) row.get("image")); // 允许为 null
      steps.add(step); // 添加到烹饪步骤列表
      logger.debug("加载烹饪步骤: {}", step.getDescription());
    }

    logger.info("烹饪步骤加载完成，数量={}", steps.size());
    return steps;
  }
}
