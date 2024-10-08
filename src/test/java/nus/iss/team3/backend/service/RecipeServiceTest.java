package nus.iss.team3.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import nus.iss.team3.backend.dataaccess.IRecipeDataAccess;
import nus.iss.team3.backend.entity.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** 单元测试类：RecipeServiceTest 用于测试 RecipeService 类的各个方法，确保其行为符合预期。 */
class RecipeServiceTest {

  private IRecipeDataAccess mockDataAccess; // Mock 的数据访问层
  private RecipeService recipeService; // 被测试的服务类

  /** 初始化测试环境，在每个测试之前创建新的 Mock 对象和 RecipeService 实例 */
  @BeforeEach
  void setUp() {
    mockDataAccess = mock(IRecipeDataAccess.class);
    recipeService = new RecipeService(mockDataAccess);
  }

  /** 测试成功添加一个有效的食谱 验证服务层调用数据访问层的 addRecipe 方法，并返回预期结果 */
  @Test
  void addRecipe_Success() {
    // Arrange: 创建一个有效的食谱对象
    Recipe recipe = new Recipe();
    recipe.setName("Test Recipe");

    // 模拟数据访问层返回 true
    when(mockDataAccess.addRecipe(recipe)).thenReturn(true);

    // Act: 调用服务层的 addRecipe 方法
    boolean result = recipeService.addRecipe(recipe);

    // Assert: 验证结果为 true，且数据访问层被调用一次
    assertTrue(result);
    verify(mockDataAccess, times(1)).addRecipe(recipe);
  }

  /** 测试添加食谱时传入 null 对象 期望服务层抛出 IllegalArgumentException */
  @Test
  void addRecipe_NullRecipe_ThrowsException() {
    // Act & Assert: 调用 addRecipe 方法传入 null，应抛出 IllegalArgumentException
    Exception exception =
        assertThrows(IllegalArgumentException.class, () -> recipeService.addRecipe(null));

    // 验证异常消息是否正确
    assertEquals("食谱不能为空", exception.getMessage());

    // 验证数据访问层的 addRecipe 方法未被调用
    verify(mockDataAccess, never()).addRecipe(any());
  }

  /** 测试添加食谱时传入没有名称的食谱对象 期望服务层抛出 IllegalArgumentException */
  @Test
  void addRecipe_EmptyName_ThrowsException() {
    // Arrange: 创建一个食谱对象但不设置名称
    Recipe recipe = new Recipe();

    // Act & Assert: 调用 addRecipe 方法，应抛出 IllegalArgumentException
    Exception exception =
        assertThrows(IllegalArgumentException.class, () -> recipeService.addRecipe(recipe));

    // 验证异常消息是否正确
    assertEquals("食谱名称不能为空", exception.getMessage());

    // 验证数据访问层的 addRecipe 方法未被调用
    verify(mockDataAccess, never()).addRecipe(any());
  }

  /** 测试成功更新一个有效的食谱 验证服务层调用数据访问层的 updateRecipe 方法，并返回预期结果 */
  @Test
  void updateRecipe_Success() {
    // Arrange: 创建一个有效的食谱对象，并设置 ID
    Recipe recipe = new Recipe();
    recipe.setId(1L);
    recipe.setName("Updated Recipe");

    // 模拟数据访问层返回 true
    when(mockDataAccess.updateRecipe(recipe)).thenReturn(true);

    // Act: 调用服务层的 updateRecipe 方法
    boolean result = recipeService.updateRecipe(recipe);

    // Assert: 验证结果为 true，且数据访问层被调用一次
    assertTrue(result);
    verify(mockDataAccess, times(1)).updateRecipe(recipe);
  }

  /** 测试更新食谱时传入 null 对象 期望服务层抛出 IllegalArgumentException */
  @Test
  void updateRecipe_NullRecipe_ThrowsException() {
    // Act & Assert: 调用 updateRecipe 方法传入 null，应抛出 IllegalArgumentException
    Exception exception =
        assertThrows(IllegalArgumentException.class, () -> recipeService.updateRecipe(null));

    // 验证异常消息是否正确
    assertEquals("食谱不能为空", exception.getMessage());

    // 验证数据访问层的 updateRecipe 方法未被调用
    verify(mockDataAccess, never()).updateRecipe(any());
  }

  /** 测试更新食谱时传入没有 ID 的食谱对象 期望服务层抛出 IllegalArgumentException */
  @Test
  void updateRecipe_NullId_ThrowsException() {
    // Arrange: 创建一个食谱对象但不设置 ID
    Recipe recipe = new Recipe();
    recipe.setName("Recipe without ID");

    // Act & Assert: 调用 updateRecipe 方法，应抛出 IllegalArgumentException
    Exception exception =
        assertThrows(IllegalArgumentException.class, () -> recipeService.updateRecipe(recipe));

    // 验证异常消息是否正确
    assertEquals("食谱 ID 不能为空", exception.getMessage());

    // 验证数据访问层的 updateRecipe 方法未被调用
    verify(mockDataAccess, never()).updateRecipe(any());
  }

  /** 测试更新食谱时传入没有名称的食谱对象 期望服务层抛出 IllegalArgumentException */
  @Test
  void updateRecipe_EmptyName_ThrowsException() {
    // Arrange: 创建一个食谱对象，设置 ID 但不设置名称
    Recipe recipe = new Recipe();
    recipe.setId(1L);

    // Act & Assert: 调用 updateRecipe 方法，应抛出 IllegalArgumentException
    Exception exception =
        assertThrows(IllegalArgumentException.class, () -> recipeService.updateRecipe(recipe));

    // 验证异常消息是否正确
    assertEquals("食谱名称不能为空", exception.getMessage());

    // 验证数据访问层的 updateRecipe 方法未被调用
    verify(mockDataAccess, never()).updateRecipe(any());
  }

  /** 测试成功根据 ID 删除食谱 验证服务层调用数据访问层的 deleteRecipeById 方法，并返回预期结果 */
  @Test
  void deleteRecipeById_Success() {
    // Arrange: 设置食谱 ID
    Long recipeId = 1L;

    // 模拟数据访问层返回 true
    when(mockDataAccess.deleteRecipeById(recipeId)).thenReturn(true);

    // Act: 调用服务层的 deleteRecipeById 方法
    boolean result = recipeService.deleteRecipeById(recipeId);

    // Assert: 验证结果为 true，且数据访问层被调用一次
    assertTrue(result);
    verify(mockDataAccess, times(1)).deleteRecipeById(recipeId);
  }

  /** 测试删除食谱时传入 null ID 期望服务层抛出 NullPointerException */
  @Test
  void deleteRecipeById_NullId_ThrowsException() {
    // Act & Assert: 调用 deleteRecipeById 方法传入 null，应抛出 NullPointerException
    Exception exception =
        assertThrows(NullPointerException.class, () -> recipeService.deleteRecipeById(null));

    // 验证异常消息是否正确
    assertEquals("食谱 ID 不能为空", exception.getMessage());

    // 验证数据访问层的 deleteRecipeById 方法未被调用
    verify(mockDataAccess, never()).deleteRecipeById(any());
  }

  /** 测试成功根据 ID 获取食谱 验证服务层调用数据访问层的 getRecipeById 方法，并返回预期结果 */
  @Test
  void getRecipeById_Found() {
    // Arrange: 设置食谱 ID 和模拟返回的食谱对象
    Long recipeId = 1L;
    Recipe recipe = new Recipe();
    recipe.setId(recipeId);
    recipe.setName("Existing Recipe");

    when(mockDataAccess.getRecipeById(recipeId)).thenReturn(recipe);

    // Act: 调用服务层的 getRecipeById 方法
    Recipe result = recipeService.getRecipeById(recipeId);

    // Assert: 验证返回的食谱对象与预期相符，并且数据访问层被调用一次
    assertNotNull(result);
    assertEquals(recipeId, result.getId());
    assertEquals("Existing Recipe", result.getName());
    verify(mockDataAccess, times(1)).getRecipeById(recipeId);
  }

  /** 测试根据 ID 获取食谱时传入 null ID 期望服务层抛出 NullPointerException */
  @Test
  void getRecipeById_NullId_ThrowsException() {
    // Act & Assert: 调用 getRecipeById 方法传入 null，应抛出 NullPointerException
    Exception exception =
        assertThrows(NullPointerException.class, () -> recipeService.getRecipeById(null));

    // 验证异常消息是否正确
    assertEquals("食谱 ID 不能为空", exception.getMessage());

    // 验证数据访问层的 getRecipeById 方法未被调用
    verify(mockDataAccess, never()).getRecipeById(any());
  }

  /** 测试根据 ID 获取食谱，当食谱不存在时 验证服务层返回 null */
  @Test
  void getRecipeById_NotFound() {
    // Arrange: 设置食谱 ID，并模拟数据访问层返回 null
    Long recipeId = 1L;
    when(mockDataAccess.getRecipeById(recipeId)).thenReturn(null);

    // Act: 调用服务层的 getRecipeById 方法
    Recipe result = recipeService.getRecipeById(recipeId);

    // Assert: 验证结果为 null，且数据访问层被调用一次
    assertNull(result);
    verify(mockDataAccess, times(1)).getRecipeById(recipeId);
  }

  /** 测试成功获取所有食谱 验证服务层调用数据访问层的 getAllRecipes 方法，并返回预期结果 */
  @Test
  void getAllRecipes_Success() {
    // Arrange: 创建一个食谱列表并模拟数据访问层返回
    Recipe recipe1 = new Recipe();
    recipe1.setId(1L);
    recipe1.setName("Recipe One");

    Recipe recipe2 = new Recipe();
    recipe2.setId(2L);
    recipe2.setName("Recipe Two");

    List<Recipe> recipes = List.of(recipe1, recipe2);
    when(mockDataAccess.getAllRecipes()).thenReturn(recipes);

    // Act: 调用服务层的 getAllRecipes 方法
    List<Recipe> result = recipeService.getAllRecipes();

    // Assert: 验证返回的列表与预期相符，并且数据访问层被调用一次
    assertNotNull(result);
    assertEquals(2, result.size());
    verify(mockDataAccess, times(1)).getAllRecipes();
  }

  /** 测试获取所有食谱时数据访问层返回空列表 验证服务层返回空列表 */
  @Test
  void getAllRecipes_EmptyList() {
    // Arrange: 模拟数据访问层返回空列表
    when(mockDataAccess.getAllRecipes()).thenReturn(Collections.emptyList());

    // Act: 调用服务层的 getAllRecipes 方法
    List<Recipe> result = recipeService.getAllRecipes();

    // Assert: 验证返回的列表为空，并且数据访问层被调用一次
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(mockDataAccess, times(1)).getAllRecipes();
  }

  /** 测试根据名称获取食谱，名称非空且有匹配结果 验证服务层调用数据访问层的 getRecipesByName 方法，并返回预期结果 */
  @Test
  void getRecipesByName_Found() {
    // Arrange: 设置搜索名称并模拟数据访问层返回的食谱列表
    String name = "Chicken";
    Recipe recipe = new Recipe();
    recipe.setId(1L);
    recipe.setName("Chicken Curry");

    when(mockDataAccess.getRecipesByName(name)).thenReturn(List.of(recipe));

    // Act: 调用服务层的 getRecipesByName 方法
    List<Recipe> result = recipeService.getRecipesByName(name);

    // Assert: 验证返回的列表包含预期的食谱，并且数据访问层被调用一次
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("Chicken Curry", result.getFirst().getName());
    verify(mockDataAccess, times(1)).getRecipesByName(name);
  }

  /** 测试根据名称获取食谱，名称非空但没有匹配结果 验证服务层返回空列表 */
  @Test
  void getRecipesByName_NotFound() {
    // Arrange: 设置搜索名称并模拟数据访问层返回空列表
    String name = "Beef";
    when(mockDataAccess.getRecipesByName(name)).thenReturn(Collections.emptyList());

    // Act: 调用服务层的 getRecipesByName 方法
    List<Recipe> result = recipeService.getRecipesByName(name);

    // Assert: 验证返回的列表为空，并且数据访问层被调用一次
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(mockDataAccess, times(1)).getRecipesByName(name);
  }

  /** 测试根据名称获取食谱时传入空字符串 验证服务层返回空列表，并且数据访问层未被调用 */
  @Test
  void getRecipesByName_EmptyName_ReturnsEmptyList() {
    // Arrange: 设置空字符串作为搜索名称
    String name = "   ";

    // Act: 调用服务层的 getRecipesByName 方法
    List<Recipe> result = recipeService.getRecipesByName(name);

    // Assert: 验证返回的列表为空，并且数据访问层的 getRecipesByName 方法未被调用
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(mockDataAccess, never()).getRecipesByName(anyString());
  }

  /** 测试根据名称获取食谱时传入 null 验证服务层返回空列表，并且数据访问层未被调用 */
  @Test
  void getRecipesByName_NullName_ReturnsEmptyList() {
    // Act: 调用服务层的 getRecipesByName 方法传入 null
    List<Recipe> result = recipeService.getRecipesByName(null);

    // Assert: 验证返回的列表为空，并且数据访问层的 getRecipesByName 方法未被调用
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(mockDataAccess, never()).getRecipesByName(anyString());
  }
}
