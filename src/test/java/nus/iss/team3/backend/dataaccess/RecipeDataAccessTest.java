package nus.iss.team3.backend.dataaccess;

import nus.iss.team3.backend.dataaccess.postgres.PostgresDataAccess;
import nus.iss.team3.backend.entity.CookingStep;
import nus.iss.team3.backend.entity.Ingredient;
import nus.iss.team3.backend.entity.Recipe;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeDataAccessTest {

  @Mock private PostgresDataAccess postgresDataAccess;
  @InjectMocks private RecipeDataAccess recipeDataAccess;

  /** 测试成功添加一个食谱 验证插入操作是否按预期执行，并检查相关的INSERT语句 */
  @Test
  void addRecipe_Success() {
    // Arrange: 创建一个样本食谱对象
    Recipe recipe = createSampleRecipe();
    Map<String, Object> insertResult = Map.of("id", 1L);
    // 模拟插入食谱返回生成的ID
    when(postgresDataAccess.queryStatement(anyString(), anyMap()))
        .thenReturn(Collections.singletonList(insertResult));

    // Act: 调用添加食谱的方法
    boolean result = recipeDataAccess.addRecipe(recipe);

    // Assert: 验证添加是否成功，以及食谱ID是否被设置
    assertTrue(result);
    assertEquals(1L, recipe.getId());

    // 验证插入食谱的SQL语句被调用一次
    verify(postgresDataAccess, times(1)).queryStatement(contains("INSERT INTO recipe"), anyMap());

    // 验证每个配料的INSERT语句被正确调用
    for (Ingredient ingredient : recipe.getIngredients()) {
      // argThat(params -> { ... }) 是一个参数匹配器，用于验证传递给 upsertStatement 方法的参数是否正确。
      verify(postgresDataAccess, times(1))
          .upsertStatement(
              contains("INSERT INTO recipe_ingredients"),
              argThat(
                  params ->
                      Objects.equals(params.get("recipe_id"), 1L)
                          && Objects.equals(params.get("name"), ingredient.getName())
                          && Objects.equals(params.get("quantity"), ingredient.getQuantity())
                          && Objects.equals(params.get("uom"), ingredient.getUom())));
    }

    // 验证每个烹饪步骤的INSERT语句被正确调用
    for (CookingStep step : recipe.getCookingSteps()) {
      verify(postgresDataAccess, times(1))
          .upsertStatement(
              contains("INSERT INTO recipe_cooking_step"),
              argThat(
                  params ->
                      Objects.equals(params.get("recipe_id"), 1L)
                          && Objects.equals(params.get("description"), step.getDescription())
                          && Objects.equals(params.get("image"), step.getImage())));
    }

    // 确认没有执行任何删除操作
    verify(postgresDataAccess, never()).upsertStatement(contains("DELETE FROM"), anyMap());
  }

  /** 测试添加食谱失败的情况，当插入食谱没有返回任何行时 验证方法应返回false，并且相关的INSERT语句不会被执行 */
  @Test
  void addRecipe_Failure_InsertRecipe() {
    // Arrange: 创建一个样本食谱对象
    Recipe recipe = createSampleRecipe();
    // 模拟插入食谱没有返回任何结果
    when(postgresDataAccess.queryStatement(anyString(), anyMap()))
        .thenReturn(Collections.emptyList());

    // Act: 调用添加食谱的方法
    boolean result = recipeDataAccess.addRecipe(recipe);

    // Assert: 验证添加失败，并且食谱ID保持为null
    assertFalse(result);
    assertNull(recipe.getId());

    // 验证只尝试了插入食谱，而没有插入配料或烹饪步骤
    verify(postgresDataAccess, times(1)).queryStatement(contains("INSERT INTO recipe"), anyMap());
    verify(postgresDataAccess, never())
        .upsertStatement(contains("INSERT INTO recipe_ingredients"), anyMap());
    verify(postgresDataAccess, never())
        .upsertStatement(contains("INSERT INTO recipe_cooking_step"), anyMap());
  }

  /** 测试添加无效食谱时抛出异常 验证方法应抛出IllegalArgumentException，并且不会与PostgresDataAccess进行任何交互 */
  @Test
  void addRecipe_InvalidRecipe_ThrowsException() {
    // Arrange: 创建一个缺失必要字段的无效食谱对象
    Recipe invalidRecipe = new Recipe(); // Missing required fields

    // Act & Assert: 调用添加方法应抛出异常
    Exception exception =
        assertThrows(
            IllegalArgumentException.class, () -> recipeDataAccess.addRecipe(invalidRecipe));

    // 验证异常消息是否正确
    assertEquals("Required fields for recipe cannot be null", exception.getMessage());

    // 验证没有与PostgresDataAccess进行任何交互
    verify(postgresDataAccess, never()).queryStatement(anyString(), anyMap());
    verify(postgresDataAccess, never()).upsertStatement(anyString(), anyMap());
  }

  /** 测试添加食谱时数据库抛出异常 验证方法应抛出相应的异常，并记录错误日志 */
  @Test
  void addRecipe_DatabaseException_ThrowsException() {
    // Arrange: 创建一个样本食谱对象
    Recipe recipe = createSampleRecipeWithMultipleIngredientsAndSteps();
    // 模拟插入食谱时抛出异常
    when(postgresDataAccess.queryStatement(anyString(), anyMap()))
        .thenThrow(new RuntimeException("Database insert error"));

    // Act & Assert: 调用添加食谱的方法应抛出异常
    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> recipeDataAccess.addRecipe(recipe));

    // 验证异常消息是否正确
    assertEquals("Database insert error", exception.getMessage());

    // 验证插入食谱的SQL语句被调用一次
    verify(postgresDataAccess, times(1)).queryStatement(contains("INSERT INTO recipe"), anyMap());

    // 验证不会执行配料和步骤的插入
    verify(postgresDataAccess, never())
        .upsertStatement(contains("INSERT INTO recipe_ingredients"), anyMap());
    verify(postgresDataAccess, never())
        .upsertStatement(contains("INSERT INTO recipe_cooking_step"), anyMap());
  }

  /** 测试成功更新一个食谱 验证更新操作是否按预期执行，并检查相关的UPDATE和DELETE/INSERT语句 */
  @Test
  void updateRecipe_Success() {
    // Arrange: 创建并设置食谱ID
    Recipe recipe = createSampleRecipe();
    recipe.setId(1L);
    // 模拟更新食谱返回1表示成功
    when(postgresDataAccess.upsertStatement(contains("UPDATE recipe"), anyMap())).thenReturn(1);

    // Act: 调用更新食谱的方法
    boolean result = recipeDataAccess.updateRecipe(recipe);

    // Assert: 验证更新是否成功
    assertTrue(result);

    // 验证更新食谱的SQL语句被调用一次
    verify(postgresDataAccess, times(1)).upsertStatement(contains("UPDATE recipe"), anyMap());

    // 验证删除并重新插入配料的DELETE和INSERT语句被正确调用
    verify(postgresDataAccess, times(1))
        .upsertStatement(
            contains("DELETE FROM recipe_ingredients"),
            argThat(params -> Objects.equals(params.get("recipe_id"), 1L)));
    for (Ingredient ingredient : recipe.getIngredients()) {
      verify(postgresDataAccess, times(1))
          .upsertStatement(
              contains("INSERT INTO recipe_ingredients"),
              argThat(
                  params ->
                      Objects.equals(params.get("recipe_id"), 1L)
                          && Objects.equals(params.get("name"), ingredient.getName())
                          && Objects.equals(params.get("quantity"), ingredient.getQuantity())
                          && Objects.equals(params.get("uom"), ingredient.getUom())));
    }

    // 验证删除并重新插入烹饪步骤的DELETE和INSERT语句被正确调用
    verify(postgresDataAccess, times(1))
        .upsertStatement(
            contains("DELETE FROM recipe_cooking_step"),
            argThat(params -> Objects.equals(params.get("recipe_id"), 1L)));
    for (CookingStep step : recipe.getCookingSteps()) {
      verify(postgresDataAccess, times(1))
          .upsertStatement(
              contains("INSERT INTO recipe_cooking_step"),
              argThat(
                  params ->
                      Objects.equals(params.get("recipe_id"), 1L)
                          && Objects.equals(params.get("description"), step.getDescription())
                          && Objects.equals(params.get("image"), step.getImage())));
    }
  }

  /** 测试更新食谱失败的情况，当UPDATE语句未影响任何行时 验证方法应返回false，并且不会执行删除或插入配料和烹饪步骤的操作 */
  @Test
  void updateRecipe_Failure_NoRowsUpdated() {
    // Arrange: 创建并设置食谱ID
    Recipe recipe = createSampleRecipe();
    recipe.setId(1L);
    // 模拟更新食谱返回0表示失败
    when(postgresDataAccess.upsertStatement(contains("UPDATE recipe"), anyMap())).thenReturn(0);

    // Act: 调用更新食谱的方法
    boolean result = recipeDataAccess.updateRecipe(recipe);

    // Assert: 验证更新失败
    assertFalse(result);

    // 验证只尝试了更新食谱，而没有删除或插入配料和烹饪步骤
    verify(postgresDataAccess, times(1)).upsertStatement(contains("UPDATE recipe"), anyMap());
    verify(postgresDataAccess, never())
        .upsertStatement(contains("DELETE FROM recipe_ingredients"), anyMap());
    verify(postgresDataAccess, never())
        .upsertStatement(contains("INSERT INTO recipe_ingredients"), anyMap());
    verify(postgresDataAccess, never())
        .upsertStatement(contains("DELETE FROM recipe_cooking_step"), anyMap());
    verify(postgresDataAccess, never())
        .upsertStatement(contains("INSERT INTO recipe_cooking_step"), anyMap());
  }

  /** 测试更新无效食谱时抛出异常 验证方法应抛出IllegalArgumentException，并且不会与PostgresDataAccess进行任何交互 */
  @Test
  void updateRecipe_InvalidRecipe_ThrowsException() {
    // Arrange: 创建一个缺失必要字段的无效食谱对象
    Recipe invalidRecipe = new Recipe(); // Missing required fields

    // Act & Assert: 调用更新方法应抛出异常
    Exception exception =
        assertThrows(
            IllegalArgumentException.class, () -> recipeDataAccess.updateRecipe(invalidRecipe));

    // 验证异常消息是否正确
    assertEquals("Required fields for recipe cannot be null", exception.getMessage());

    // 验证没有与PostgresDataAccess进行任何交互
    verify(postgresDataAccess, never()).upsertStatement(anyString(), anyMap());
  }

  /** 测试更新食谱时数据库抛出异常 验证方法应抛出相应的异常，并记录错误日志 */
  @Test
  void updateRecipe_DatabaseException_ThrowsException() {
    // Arrange: 创建一个样本食谱对象
    Recipe recipe = createSampleRecipeWithMultipleIngredientsAndSteps();
    recipe.setId(1L);
    // 模拟更新食谱时抛出异常
    when(postgresDataAccess.upsertStatement(anyString(), anyMap()))
        .thenThrow(new RuntimeException("Database update error"));

    // Act & Assert: 调用更新食谱的方法应抛出异常
    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> recipeDataAccess.updateRecipe(recipe));

    // 验证异常消息是否正确
    assertEquals("Database update error", exception.getMessage());

    // 验证更新食谱的SQL语句被调用一次
    verify(postgresDataAccess, times(1)).upsertStatement(contains("UPDATE recipe SET"), anyMap());
    // 验证不会执行配料和步骤的更新
    verify(postgresDataAccess, never())
        .upsertStatement(contains("INSERT INTO recipe_ingredients"), anyMap());
    verify(postgresDataAccess, never())
        .upsertStatement(contains("INSERT INTO recipe_cooking_step"), anyMap());
  }

  /** 测试成功按ID删除食谱 验证删除操作是否按预期执行，并检查相关的DELETE语句 */
  @Test
  void deleteRecipeById_Success() {
    // Arrange: 设置食谱ID
    Long recipeId = 1L;
    // 模拟删除操作返回1表示成功
    when(postgresDataAccess.upsertStatement(anyString(), anyMap())).thenReturn(1);

    // Act: 调用删除食谱的方法
    boolean result = recipeDataAccess.deleteRecipeById(recipeId);

    // Assert: 验证删除是否成功
    assertTrue(result);

    // 验证删除配料的DELETE语句被调用一次
    verify(postgresDataAccess, times(1))
        .upsertStatement(
            contains("DELETE FROM recipe_ingredients"),
            argThat(params -> Objects.equals(params.get("recipe_id"), recipeId)));
    // 验证删除烹饪步骤的DELETE语句被调用一次
    verify(postgresDataAccess, times(1))
        .upsertStatement(
            contains("DELETE FROM recipe_cooking_step"),
            argThat(params -> Objects.equals(params.get("recipe_id"), recipeId)));
    // 验证删除食谱的DELETE语句被调用一次
    verify(postgresDataAccess, times(1))
        .upsertStatement(
            contains("DELETE FROM recipe"),
            argThat(params -> Objects.equals(params.get("id"), recipeId)));
  }

  /** 测试按ID删除食谱失败的情况，当DELETE语句未影响任何行时 验证方法应返回false，并且所有相关的DELETE语句被执行 */
  @Test
  void deleteRecipeById_Failure_NoRowsDeleted() {
    // Arrange: 设置食谱ID
    Long recipeId = 1L;
    // 模拟删除食谱返回0表示没有行被删除
    when(postgresDataAccess.upsertStatement(contains("DELETE FROM recipe"), anyMap()))
        .thenReturn(0);

    // Act: 调用删除食谱的方法
    boolean result = recipeDataAccess.deleteRecipeById(recipeId);

    // Assert: 验证删除失败
    assertFalse(result);

    // 验证所有相关的DELETE语句被调用一次
    verify(postgresDataAccess, times(1))
        .upsertStatement(
            contains("DELETE FROM recipe_ingredients"),
            argThat(params -> Objects.equals(params.get("recipe_id"), recipeId)));
    verify(postgresDataAccess, times(1))
        .upsertStatement(
            contains("DELETE FROM recipe_cooking_step"),
            argThat(params -> Objects.equals(params.get("recipe_id"), recipeId)));
    verify(postgresDataAccess, times(1))
        .upsertStatement(
            contains("DELETE FROM recipe"),
            argThat(params -> Objects.equals(params.get("id"), recipeId)));
  }

  /** 测试删除食谱时删除食谱步骤抛出异常 验证方法应抛出相应的异常，并记录错误日志 */
  @Test
  void deleteRecipeById_DeleteRecipe_DatabaseException_ThrowsException() {
    // Arrange: 设置食谱ID
    Long recipeId = 1L;

    // 模拟删除配料正常执行
    when(postgresDataAccess.upsertStatement(
            eq("DELETE FROM recipe_ingredients WHERE recipe_id = :recipe_id"),
            argThat(params -> Objects.equals(params.get("recipe_id"), recipeId))))
        .thenReturn(1);

    // 模拟删除烹饪步骤正常执行
    when(postgresDataAccess.upsertStatement(
            eq("DELETE FROM recipe_cooking_step WHERE recipe_id = :recipe_id"),
            argThat(params -> Objects.equals(params.get("recipe_id"), recipeId))))
        .thenReturn(1);

    // 模拟删除食谱时抛出异常
    when(postgresDataAccess.upsertStatement(
            eq("DELETE FROM recipe WHERE id = :id"),
            argThat(params -> Objects.equals(params.get("id"), recipeId))))
        .thenThrow(new RuntimeException("Database error on deleting recipe"));

    // Act & Assert: 调用删除食谱的方法应抛出异常
    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> recipeDataAccess.deleteRecipeById(recipeId));

    // 验证异常消息是否正确
    assertEquals("Database error on deleting recipe", exception.getMessage());

    // 验证删除配料的SQL语句被调用一次
    verify(postgresDataAccess, times(1))
        .upsertStatement(
            eq("DELETE FROM recipe_ingredients WHERE recipe_id = :recipe_id"),
            argThat(params -> Objects.equals(params.get("recipe_id"), recipeId)));
    // 验证删除烹饪步骤的SQL语句被调用一次
    verify(postgresDataAccess, times(1))
        .upsertStatement(
            eq("DELETE FROM recipe_cooking_step WHERE recipe_id = :recipe_id"),
            argThat(params -> Objects.equals(params.get("recipe_id"), recipeId)));

    // 验证删除食谱的SQL语句被调用一次
    verify(postgresDataAccess, times(1))
        .upsertStatement(
            eq("DELETE FROM recipe WHERE id = :id"),
            argThat(params -> Objects.equals(params.get("id"), recipeId)));
  }

  /** 测试根据ID获取食谱，食谱存在的情况 验证查询操作是否按预期执行，并正确组装食谱对象 */
  @Test
  void getRecipeById_Found() {
    // Arrange: 设置食谱ID，并模拟查询返回的食谱数据
    Long recipeId = 1L;
    Map<String, Object> recipeRow = createSampleRecipeMap();
    List<Map<String, Object>> recipeResult = Collections.singletonList(recipeRow);
    when(postgresDataAccess.queryStatement(contains("SELECT * FROM recipe WHERE id"), anyMap()))
        .thenReturn(recipeResult);
    // 模拟查询配料和烹饪步骤的数据
    when(postgresDataAccess.queryStatement(contains("SELECT * FROM recipe_ingredients"), anyMap()))
        .thenReturn(Collections.singletonList(createSampleIngredientMap()));
    when(postgresDataAccess.queryStatement(contains("SELECT * FROM recipe_cooking_step"), anyMap()))
        .thenReturn(Collections.singletonList(createSampleCookingStepMap()));

    // Act: 调用获取食谱的方法
    Recipe recipe = recipeDataAccess.getRecipeById(recipeId);

    // Assert: 验证食谱对象被正确组装
    assertNotNull(recipe);
    assertEquals(1L, recipe.getId());
    assertEquals("Sample Recipe", recipe.getName());
    assertEquals(1, recipe.getIngredients().size());
    assertEquals(1, recipe.getCookingSteps().size());

    // 验证SELECT查询被正确调用
    verify(postgresDataAccess, times(1))
        .queryStatement(
            contains("SELECT * FROM recipe WHERE id"),
            argThat(params -> Objects.equals(params.get("id"), recipeId)));
    verify(postgresDataAccess, times(1))
        .queryStatement(
            contains("SELECT * FROM recipe_ingredients"),
            argThat(params -> Objects.equals(params.get("recipe_id"), recipeId)));
    verify(postgresDataAccess, times(1))
        .queryStatement(
            contains("SELECT * FROM recipe_cooking_step"),
            argThat(params -> Objects.equals(params.get("recipe_id"), recipeId)));
  }

  /** 测试根据ID获取食谱，食谱不存在的情况 验证方法应返回null，并且不会查询配料和烹饪步骤 */
  @Test
  void getRecipeById_NotFound() {
    // Arrange: 设置食谱ID，并模拟查询不返回任何结果
    Long recipeId = 1L;
    when(postgresDataAccess.queryStatement(contains("SELECT * FROM recipe WHERE id"), anyMap()))
        .thenReturn(Collections.emptyList());

    // Act: 调用获取食谱的方法
    Recipe recipe = recipeDataAccess.getRecipeById(recipeId);

    // Assert: 验证方法返回null
    assertNull(recipe);

    // 验证只进行了食谱的SELECT查询，没有查询配料和烹饪步骤
    verify(postgresDataAccess, times(1))
        .queryStatement(
            contains("SELECT * FROM recipe WHERE id"),
            argThat(params -> Objects.equals(params.get("id"), recipeId)));
    verify(postgresDataAccess, never())
        .queryStatement(contains("SELECT * FROM recipe_ingredients"), anyMap());
    verify(postgresDataAccess, never())
        .queryStatement(contains("SELECT * FROM recipe_cooking_step"), anyMap());
  }

  /** 测试根据名称获取食谱时数据库抛出异常 验证方法应抛出相应的异常，并记录错误日志 */
  @Test
  void getRecipesByName_DatabaseException_ThrowsException() {
    // Arrange: 设置搜索名称
    String name = "Sample";
    // 模拟查询食谱时抛出异常
    when(postgresDataAccess.queryStatement(
            contains("SELECT * FROM recipe WHERE name ILIKE"), anyMap()))
        .thenThrow(new RuntimeException("Database query error"));

    // Act & Assert: 调用根据名称获取食谱的方法应抛出异常
    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> recipeDataAccess.getRecipesByName(name));

    // 验证异常消息是否正确
    assertEquals("Database query error", exception.getMessage());

    // 验证查询食谱的SELECT语句被调用
    verify(postgresDataAccess, times(1))
        .queryStatement(
            contains("SELECT * FROM recipe WHERE name ILIKE"),
            argThat(params -> Objects.equals(params.get("name"), "%" + name + "%")));
    // 验证不会查询配料和烹饪步骤
    verify(postgresDataAccess, never())
        .queryStatement(contains("SELECT * FROM recipe_ingredients"), anyMap());
    verify(postgresDataAccess, never())
        .queryStatement(contains("SELECT * FROM recipe_cooking_step"), anyMap());
  }

  /** 测试获取所有食谱 验证查询操作是否按预期执行，并正确组装食谱列表 */
  @Test
  void getAllRecipes() {
    // Arrange: 模拟查询返回的所有食谱数据
    Map<String, Object> recipeRow = createSampleRecipeMap();
    List<Map<String, Object>> recipeResult = Collections.singletonList(recipeRow);
    when(postgresDataAccess.queryStatement(contains("SELECT * FROM recipe"), isNull()))
        .thenReturn(recipeResult);
    // 模拟查询配料和烹饪步骤的数据
    when(postgresDataAccess.queryStatement(contains("SELECT * FROM recipe_ingredients"), anyMap()))
        .thenReturn(Collections.singletonList(createSampleIngredientMap()));
    when(postgresDataAccess.queryStatement(contains("SELECT * FROM recipe_cooking_step"), anyMap()))
        .thenReturn(Collections.singletonList(createSampleCookingStepMap()));

    // Act: 调用获取所有食谱的方法
    List<Recipe> recipes = recipeDataAccess.getAllRecipes();

    // Assert: 验证食谱列表被正确组装
    assertNotNull(recipes);
    assertEquals(1, recipes.size());
    Recipe recipe = recipes.getFirst();
    assertEquals(1L, recipe.getId());
    assertEquals("Sample Recipe", recipe.getName());
    assertEquals(1, recipe.getIngredients().size());
    assertEquals(1, recipe.getCookingSteps().size());

    // 验证SELECT查询被正确调用
    verify(postgresDataAccess, times(1)).queryStatement(contains("SELECT * FROM recipe"), isNull());
    verify(postgresDataAccess, times(1))
        .queryStatement(
            contains("SELECT * FROM recipe_ingredients"),
            argThat(params -> Objects.equals(params.get("recipe_id"), 1L)));
    verify(postgresDataAccess, times(1))
        .queryStatement(
            contains("SELECT * FROM recipe_cooking_step"),
            argThat(params -> Objects.equals(params.get("recipe_id"), 1L)));
  }

  /** 测试根据名称获取食谱，食谱存在的情况 验证查询操作是否按预期执行，并正确组装食谱列表 */
  @Test
  void getRecipesByName_Found() {
    // Arrange: 设置搜索名称，并模拟查询返回的食谱数据
    String name = "Sample";
    Map<String, Object> recipeRow = createSampleRecipeMap();
    List<Map<String, Object>> recipeResult = Collections.singletonList(recipeRow);
    when(postgresDataAccess.queryStatement(
            contains("SELECT * FROM recipe WHERE name ILIKE"), anyMap()))
        .thenReturn(recipeResult);
    // 模拟查询配料和烹饪步骤的数据
    when(postgresDataAccess.queryStatement(contains("SELECT * FROM recipe_ingredients"), anyMap()))
        .thenReturn(Collections.singletonList(createSampleIngredientMap()));
    when(postgresDataAccess.queryStatement(contains("SELECT * FROM recipe_cooking_step"), anyMap()))
        .thenReturn(Collections.singletonList(createSampleCookingStepMap()));

    // Act: 调用根据名称获取食谱的方法
    List<Recipe> recipes = recipeDataAccess.getRecipesByName(name);

    // Assert: 验证食谱列表被正确组装
    assertNotNull(recipes);
    assertEquals(1, recipes.size());
    Recipe recipe = recipes.getFirst();
    assertEquals("Sample Recipe", recipe.getName());
    assertEquals(1, recipe.getIngredients().size());
    assertEquals(1, recipe.getCookingSteps().size());

    // 验证SELECT查询被正确调用
    verify(postgresDataAccess, times(1))
        .queryStatement(
            contains("SELECT * FROM recipe WHERE name ILIKE"),
            argThat(params -> Objects.equals(params.get("name"), "%" + name + "%")));
    verify(postgresDataAccess, times(1))
        .queryStatement(
            contains("SELECT * FROM recipe_ingredients"),
            argThat(params -> Objects.equals(params.get("recipe_id"), 1L)));
    verify(postgresDataAccess, times(1))
        .queryStatement(
            contains("SELECT * FROM recipe_cooking_step"),
            argThat(params -> Objects.equals(params.get("recipe_id"), 1L)));
  }

  /** 测试根据名称获取食谱，食谱不存在的情况 验证方法应返回空列表，并且不会查询配料和烹饪步骤 */
  @Test
  void getRecipesByName_NotFound() {
    // Arrange: 设置搜索名称，并模拟查询不返回任何结果
    String name = "Nonexistent";
    when(postgresDataAccess.queryStatement(
            contains("SELECT * FROM recipe WHERE name ILIKE"), anyMap()))
        .thenReturn(Collections.emptyList());

    // Act: 调用根据名称获取食谱的方法
    List<Recipe> recipes = recipeDataAccess.getRecipesByName(name);

    // Assert: 验证方法返回空列表
    assertNotNull(recipes);
    assertTrue(recipes.isEmpty());

    // 验证只进行了食谱的SELECT查询，没有查询配料和烹饪步骤
    verify(postgresDataAccess, times(1))
        .queryStatement(
            contains("SELECT * FROM recipe WHERE name ILIKE"),
            argThat(params -> Objects.equals(params.get("name"), "%" + name + "%")));
    verify(postgresDataAccess, never())
        .queryStatement(contains("SELECT * FROM recipe_ingredients"), anyMap());
    verify(postgresDataAccess, never())
        .queryStatement(contains("SELECT * FROM recipe_cooking_step"), anyMap());
  }

  /** 辅助方法：创建一个样本食谱对象，用于测试 */
  private Recipe createSampleRecipe() {
    Recipe recipe = new Recipe();
    recipe.setCreatorId(1L);
    recipe.setName("Sample Recipe");
    recipe.setImage("sample.jpg");
    recipe.setDescription("A sample recipe description.");
    recipe.setCookingTimeInSec(3600);
    recipe.setDifficultyLevel(2);
    recipe.setRating(4.5);
    recipe.setStatus(1);
    recipe.setCreateDatetime(Timestamp.valueOf(java.time.LocalDateTime.now()));
    recipe.setUpdateDatetime(Timestamp.valueOf(java.time.LocalDateTime.now()));

    Ingredient ingredient = createIngredient("Sugar", 100.0, "grams");
    recipe.setIngredients(Collections.singletonList(ingredient));

    CookingStep step = createCookingStep("Mix ingredients.", "step1.jpg");
    recipe.setCookingSteps(Collections.singletonList(step));

    return recipe;
  }

  /** 辅助方法：创建一个样本配料对象，用于测试 */
  private Ingredient createIngredient(String name, double quantity, String uom) {
    Ingredient ingredient = new Ingredient();
    ingredient.setName(name);
    ingredient.setQuantity(quantity);
    ingredient.setUom(uom);
    return ingredient;
  }

  /** 辅助方法：创建一个样本烹饪步骤对象，用于测试 */
  private CookingStep createCookingStep(String description, String image) {
    CookingStep step = new CookingStep();
    step.setDescription(description);
    step.setImage(image);
    return step;
  }

  /** 辅助方法：创建一个样本食谱的Map表示，用于模拟数据库查询结果 */
  private Map<String, Object> createSampleRecipeMap() {
    Map<String, Object> map = new HashMap<>();
    map.put("id", 1L);
    map.put("creator_id", 1L);
    map.put("name", "Sample Recipe");
    map.put("image", "sample.jpg");
    map.put("description", "A sample recipe description.");
    map.put("cooking_time_in_sec", 3600);
    map.put("difficulty_level", 2);
    map.put("rating", 4.5);
    map.put("status", 1);
    map.put("create_datetime", new Timestamp(System.currentTimeMillis()));
    map.put("update_datetime", new Timestamp(System.currentTimeMillis()));
    return map;
  }

  /** 辅助方法：创建一个样本配料的Map表示，用于模拟数据库查询结果 */
  private Map<String, Object> createSampleIngredientMap() {
    Map<String, Object> map = new HashMap<>();
    map.put("id", 1L);
    map.put("recipe_id", 1L);
    map.put("name", "Sugar");
    map.put("quantity", 100.0);
    map.put("uom", "grams");
    return map;
  }

  /** 辅助方法：创建一个样本烹饪步骤的Map表示，用于模拟数据库查询结果 */
  private Map<String, Object> createSampleCookingStepMap() {
    Map<String, Object> map = new HashMap<>();
    map.put("id", 1);
    map.put("recipe_id", 1L);
    map.put("description", "Mix ingredients.");
    map.put("image", "step1.jpg");
    return map;
  }

  /** 辅助方法：创建一个包含多个配料和烹饪步骤的样本食谱对象，用于测试 */
  private Recipe createSampleRecipeWithMultipleIngredientsAndSteps() {
    Recipe recipe = new Recipe();
    recipe.setCreatorId(1L);
    recipe.setName("Sample Recipe");
    recipe.setImage("sample.jpg");
    recipe.setDescription("A sample recipe description.");
    recipe.setCookingTimeInSec(3600);
    recipe.setDifficultyLevel(2);
    recipe.setRating(4.5);
    recipe.setStatus(1);
    recipe.setCreateDatetime(Timestamp.valueOf(java.time.LocalDateTime.now()));
    recipe.setUpdateDatetime(Timestamp.valueOf(java.time.LocalDateTime.now()));

    List<Ingredient> ingredients =
        Arrays.asList(
            createIngredient("Sugar", 100.0, "grams"), createIngredient("Flour", 200.0, "grams"));
    recipe.setIngredients(ingredients);

    List<CookingStep> steps =
        Arrays.asList(
            createCookingStep("Mix sugar and flour.", "step1.jpg"),
            createCookingStep("Bake the mixture.", "step2.jpg"));
    recipe.setCookingSteps(steps);

    return recipe;
  }
}
