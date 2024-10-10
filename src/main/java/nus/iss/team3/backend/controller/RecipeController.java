package nus.iss.team3.backend.controller;

import java.util.List;
import java.util.Objects;
import nus.iss.team3.backend.entity.Recipe;
import nus.iss.team3.backend.service.IRecipeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class to handle web call for recipe related queries.
 *
 * @author Mao Weining
 */
// 使用 @RestController 注解将这个类标记为一个REST控制器
@RestController
// 设置基础URL路径为 /api/recipes，所有的映射将基于这个路径
@RequestMapping("/api/recipes")
public class RecipeController {

  private static final Logger logger = LogManager.getLogger(RecipeController.class);

  private final IRecipeService recipeService;

  // 构造函数，通过依赖注入方式初始化 IRecipeService
  public RecipeController(IRecipeService recipeService) {
    this.recipeService = recipeService;
  }

  /**
   * 添加新的食谱
   *
   * @param recipe 从请求体中获取的食谱对象
   * @return 操作结果的响应实体
   */
  @PostMapping
  public ResponseEntity<String> addRecipe(@RequestBody Recipe recipe) {
    /* Notices:
     * 1. PostMapping 注解: 映射HTTP POST请求到该方法，路径为 /api/recipes。
     * 2. RequestBody Recipe recipe: 从请求体中绑定和反序列化JSON数据到 Recipe 对象。
     * */
    logger.info("接收到添加食谱的请求: {}", recipe.getName());
    logger.info("接收到的添加食谱信息: {}", recipe);
    try {
      // 调用服务层的方法添加食谱
      recipeService.addRecipe(recipe);
      logger.info("食谱添加成功: {}", recipe.getName());
      // 返回201 CREATED状态表示成功创建
      return new ResponseEntity<>("Recipe added successfully", HttpStatus.CREATED);
    } catch (IllegalArgumentException e) {
      logger.warn("添加食谱时发生验证错误: {}", e.getMessage());
      // 如果验证失败，返回400 BAD REQUEST状态和错误信息
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      logger.error("添加食谱失败: {}", e.getMessage(), e);
      // 处理其他异常，返回500 INTERNAL SERVER ERROR状态
      return new ResponseEntity<>("Failed to add recipe", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * 更新现有的食谱
   *
   * @param id 路径变量中的食谱ID
   * @param recipe 从请求体中获取的食谱对象
   * @return 操作结果的响应实体
   */
  @PutMapping("/{id}")
  public ResponseEntity<String> updateRecipe(@PathVariable Long id, @RequestBody Recipe recipe) {
    /* Notices:
     * 1. PutMapping("/{id}") 注解: 映射HTTP PUT请求到该方法，路径为 /api/recipes/{id}，其中{id} 是路径变量。
     * 2. PathVariable Long id: 从URL路径中提取 id 参数。
     * 3. RequestBody Recipe recipe: 从请求体中绑定和反序列化JSON数据到 Recipe 对象。
     */
    logger.info("接收到更新食谱的请求: ID={}, 名称={}", id, recipe.getName());
    logger.info("接收到的更新食谱信息: {}", recipe);

    try {
      if (!recipeService.recipeExists(id)) {
        // 验证食谱是否存在
        return new ResponseEntity<>("Recipe is not exist", HttpStatus.NOT_FOUND);
      }

      // 设置食谱的ID为路径变量中的ID
      recipe.setId(id);
      // 调用服务层的方法更新食谱
      boolean updated = recipeService.updateRecipe(recipe);

      if (!updated) {
        return new ResponseEntity<>("Failed to update recipe", HttpStatus.INTERNAL_SERVER_ERROR);
      }

      logger.info("食谱更新成功: ID={}, 名称={}", id, recipe.getName());
      // 返回200 OK状态表示成功更新
      return new ResponseEntity<>("Recipe updated successfully", HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      logger.warn("更新食谱时发生验证错误: {}", e.getMessage());
      // 如果验证失败，返回400 BAD REQUEST状态和错误信息
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      logger.error("更新食谱失败: {}", e.getMessage(), e);
      // 处理其他异常，返回500 INTERNAL SERVER ERROR状态
      return new ResponseEntity<>("Failed to update recipe", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * 根据ID删除食谱
   *
   * @param id 路径变量中的食谱ID
   * @return 操作结果的响应实体
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteRecipe(@PathVariable Long id) {
    /* Notices:
     * 1. DeleteMapping("/{id}") 注解: 映射HTTP DELETE请求到该方法，路径为/api/recipes/{id}。
     * 2. PathVariable Long id: 从URL路径中提取 id 参数。
     */
    logger.info("接收到删除食谱的请求: ID={}", id);

    try {
      if (!recipeService.recipeExists(id)) {
        // 验证食谱是否存在
        return new ResponseEntity<>("Recipe is not exist", HttpStatus.NOT_FOUND);
      }

      // 调用服务层的方法删除指定ID的食谱
      recipeService.deleteRecipeById(id);

      logger.info("食谱删除成功: ID={}", id);
      // 返回200 OK状态表示成功删除
      return new ResponseEntity<>("Recipe deleted successfully", HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      logger.warn("删除食谱时发生验证错误: {}", e.getMessage());
      // 如果验证失败，返回400 BAD REQUEST状态和错误信息
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      logger.error("删除食谱失败: {}", e.getMessage(), e);
      // 处理其他异常，返回500 INTERNAL SERVER ERROR状态
      return new ResponseEntity<>("Failed to delete recipe", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * 根据ID获取特定的食谱
   *
   * @param id 路径变量中的食谱ID
   * @return 包含食谱的响应实体
   */
  @GetMapping("/{id}")
  public ResponseEntity<Recipe> getRecipe(@PathVariable Long id) {
    /* Notices:
     * 1. GetMapping("/{id}") 注解: 映射HTTP GET请求到该方法，路径为 /api/recipes/{id}。
     * 2. PathVariable Long id: 从URL路径中提取 id 参数。
     */
    logger.info("接收到获取食谱的请求: ID={}", id);
    try {
      // 调用服务层的方法获取指定ID的食谱
      Recipe recipe = recipeService.getRecipeById(id);
      if (Objects.nonNull(recipe)) {
        logger.info("找到食谱: ID={}, 名称={}", recipe.getId(), recipe.getName());
        // 如果食谱存在，返回200 OK状态和食谱对象
        return new ResponseEntity<>(recipe, HttpStatus.OK);
      } else {
        logger.warn("未找到ID为 {} 的食谱", id);
        // 如果食谱不存在，返回404 NOT FOUND状态
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      logger.error("获取食谱失败: {}", e.getMessage(), e);
      // 处理其他异常，返回500 INTERNAL SERVER ERROR状态
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * 获取所有食谱
   *
   * @return 包含所有食谱的响应实体
   */
  @GetMapping
  public ResponseEntity<List<Recipe>> getAllRecipes() {
    /* Notices:
     * 1. GetMapping 注解: 映射HTTP GET请求到该方法，路径为 /api/recipes
     */
    logger.info("接收到获取所有食谱的请求");
    try {
      // 调用服务层的方法获取所有食谱
      List<Recipe> recipes = recipeService.getAllRecipes();
      logger.info("找到 {} 个食谱", recipes.size());
      // 返回200 OK状态和食谱列表
      return new ResponseEntity<>(recipes, HttpStatus.OK);
    } catch (Exception e) {
      logger.error("获取所有食谱失败: {}", e.getMessage(), e);
      // 处理其他异常，返回500 INTERNAL SERVER ERROR状态
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * 根据名称搜索食谱
   *
   * @param name 请求参数中的食谱名称
   * @return 包含匹配食谱的响应实体
   */
  @GetMapping("/search")
  public ResponseEntity<List<Recipe>> searchRecipes(@RequestParam String name) {
    /* Notices:
     * 1. GetMapping("/search") 注解: 映射HTTP GET请求到该方法，路径为/api/recipes/search。
     * 2. RequestParam String name: 从请求参数中提取 name 参数，用于搜索食谱。
     */
    logger.info("接收到搜索食谱的请求: 名称={}", name);
    try {
      // 调用服务层的方法根据名称搜索食谱
      List<Recipe> recipes = recipeService.getRecipesByName(name);
      logger.info("找到 {} 个名称包含 '{}' 的食谱", recipes.size(), name);
      // 返回200 OK状态和匹配的食谱列表
      return new ResponseEntity<>(recipes, HttpStatus.OK);
    } catch (Exception e) {
      logger.error("搜索食谱失败: {}", e.getMessage(), e);
      // 处理其他异常，返回500 INTERNAL SERVER ERROR状态
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
