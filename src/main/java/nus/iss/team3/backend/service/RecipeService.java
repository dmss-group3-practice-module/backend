package nus.iss.team3.backend.service;

import java.util.List;
import java.util.Objects;
import nus.iss.team3.backend.dataaccess.IRecipeDataAccess;
import nus.iss.team3.backend.entity.Recipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * Service class with logic for handling recipe related queries.
 *
 * @author Mao Weining
 */
@Service // 使用 @Service 注解将这个类标记为一个 Spring 服务组件
public class RecipeService implements IRecipeService {

  private static final Logger logger = LogManager.getLogger(RecipeService.class);

  private final IRecipeDataAccess recipeDataAccess;

  // 构造函数，通过依赖注入方式初始化 IRecipeDataAccess
  public RecipeService(IRecipeDataAccess recipeDataAccess) {
    this.recipeDataAccess = recipeDataAccess;
  }

  // 实现添加食谱的方法
  @Override
  public boolean addRecipe(Recipe recipe) {
    // 调用验证方法，确保食谱数据符合添加的要求
    validateRecipeForAddition(recipe);
    logger.info("正在添加食谱: {}", recipe.getName());
    // 调用数据访问层的方法，添加食谱到数据库
    boolean result = recipeDataAccess.addRecipe(recipe);
    if (result) {
      logger.info("成功添加食谱，ID为: {}", recipe.getId());
    } else {
      logger.warn("添加食谱失败: {}", recipe.getName());
    }
    return result;
  }

  // 实现更新食谱的方法
  @Override
  public boolean updateRecipe(Recipe recipe) {
    // 调用验证方法，确保食谱数据符合更新的要求
    validateRecipeForUpdate(recipe);
    logger.info("正在更新食谱，ID为: {}", recipe.getId());
    // 调用数据访问层的方法，更新数据库中的食谱
    boolean result = recipeDataAccess.updateRecipe(recipe);
    if (result) {
      logger.info("成功更新食谱，ID为: {}", recipe.getId());
    } else {
      logger.warn("更新食谱失败，ID为: {}", recipe.getId());
    }
    return result;
  }

  // 实现根据食谱ID删除食谱的方法
  @Override
  public boolean deleteRecipeById(Long recipeId) {
    // 检查传入的食谱ID是否为null，若为null则抛出异常
    Objects.requireNonNull(recipeId, "食谱 ID 不能为空");
    logger.info("正在删除食谱，ID为: {}", recipeId);
    // 调用数据访问层的方法，删除指定ID的食谱
    boolean result = recipeDataAccess.deleteRecipeById(recipeId);
    if (result) {
      logger.info("成功删除食谱，ID为: {}", recipeId);
    } else {
      logger.warn("删除食谱失败，ID为: {}", recipeId);
    }
    return result;
  }

  // 实现根据食谱ID获取食谱的方法
  @Override
  public Recipe getRecipeById(Long recipeId) {
    // 检查传入的食谱ID是否为null，若为null则抛出异常
    Objects.requireNonNull(recipeId, "食谱 ID 不能为空");
    logger.info("正在获取食谱，ID为: {}", recipeId);
    // 调用数据访问层的方法，获取指定ID的食谱
    Recipe recipe = recipeDataAccess.getRecipeById(recipeId);
    if (recipe != null) {
      logger.info("成功获取食谱，ID为: {}", recipeId);
    } else {
      logger.warn("未找到 ID 为 {} 的食谱", recipeId);
    }
    return recipe;
  }

  // 实现获取所有食谱的方法
  @Override
  public List<Recipe> getAllRecipes() {
    logger.info("正在获取所有食谱");
    // 调用数据访问层的方法，获取所有食谱的列表
    List<Recipe> recipes = recipeDataAccess.getAllRecipes();
    logger.info("成功获取 {} 个食谱", recipes.size());
    return recipes;
  }

  // 实现根据食谱名称获取食谱的方法
  @Override
  public List<Recipe> getRecipesByName(String name) {
    // 检查传入的名称是否为null或为空，若是则返回空列表
    if (name == null || name.trim().isEmpty()) {
      logger.warn("搜索名称为空或 null，返回空列表");
      return List.of();
    }
    logger.info("正在按名称搜索食谱: {}", name);
    // 调用数据访问层的方法，根据名称获取食谱
    List<Recipe> recipes = recipeDataAccess.getRecipesByName(name);
    logger.info("找到 {} 个名称为 {} 的食谱", recipes.size(), name);
    return recipes;
  }

  @Override
  public boolean recipeExists(Long id) {
    return recipeDataAccess.getRecipeById(id) != null;
  }

  // 辅助方法：验证食谱添加的有效性
  private void validateRecipeForAddition(Recipe recipe) {
    // 检查食谱对象是否为null，若为null则抛出异常
    if (recipe == null) {
      logger.error("尝试添加空的食谱");
      throw new IllegalArgumentException("食谱不能为空");
    }
    // 检查食谱名称是否为null或为空，若是则抛出异常
    if (recipe.getName() == null || recipe.getName().trim().isEmpty()) {
      logger.error("尝试添加名称为空的食谱");
      throw new IllegalArgumentException("食谱名称不能为空");
    }
  }

  // 辅助方法：验证食谱更新的有效性
  private void validateRecipeForUpdate(Recipe recipe) {
    // 检查食谱对象是否为null，若为null则抛出异常
    if (recipe == null) {
      logger.error("尝试更新空的食谱");
      throw new IllegalArgumentException("食谱不能为空");
    }
    // 检查食谱ID是否为null，若是则抛出异常
    if (recipe.getId() == null) {
      logger.error("尝试更新没有 ID 的食谱");
      throw new IllegalArgumentException("食谱 ID 不能为空");
    }
    // 检查食谱名称是否为null或为空，若是则抛出异常
    if (recipe.getName() == null || recipe.getName().trim().isEmpty()) {
      logger.error("尝试更新名称为空的食谱");
      throw new IllegalArgumentException("食谱名称不能为空");
    }
  }
}
