package nus.iss.team3.backend.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Timestamp;
import java.util.List;
import nus.iss.team3.backend.entity.Recipe;
import nus.iss.team3.backend.service.IRecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** 单元测试类：RecipeControllerTest 用于测试 RecipeController 类的各个端点，确保其行为符合预期。 */
@WebMvcTest(RecipeController.class)
class RecipeControllerTest {

  @Autowired private MockMvc mockMvc; // 模拟HTTP请求的工具

  @MockBean private IRecipeService recipeService; // 模拟服务层的依赖

  @Autowired private ObjectMapper objectMapper; // 用于将对象序列化为JSON

  private Recipe sampleRecipe; // 示例食谱对象，用于测试

  /** 在每个测试方法执行前初始化示例食谱对象 */
  @BeforeEach
  void setUp() {
    sampleRecipe = new Recipe();
    sampleRecipe.setId(1L);
    sampleRecipe.setCreatorId(1L);
    sampleRecipe.setName("Sample Recipe");
    sampleRecipe.setImage("https://example.com/image.png");
    sampleRecipe.setDescription("A delicious sample recipe.");
    sampleRecipe.setCookingTimeInSec(3600);
    sampleRecipe.setDifficultyLevel(2);
    sampleRecipe.setRating(4.5);
    sampleRecipe.setStatus(1);
    sampleRecipe.setCreateDatetime(new Timestamp(System.currentTimeMillis()));
    sampleRecipe.setUpdateDatetime(new Timestamp(System.currentTimeMillis()));
  }

  /** 测试成功添加一个新的食谱 验证返回201 CREATED状态，并且服务层的 addRecipe 方法被调用 */
  @Test
  void addRecipe_Success() throws Exception {
    // Arrange: 模拟服务层成功添加食谱
    when(recipeService.addRecipe(any(Recipe.class))).thenReturn(true);

    // Act & Assert: 发送POST请求并验证响应状态和内容
    mockMvc
        .perform(
            post("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRecipe)))
        .andExpect(status().isCreated())
        .andExpect(content().string("Recipe added successfully"));

    // 验证服务层方法被调用一次
    verify(recipeService, times(1)).addRecipe(any(Recipe.class));
  }

  /** 测试添加食谱时传入无效数据，服务层抛出IllegalArgumentException 验证返回400 BAD REQUEST状态和错误消息 */
  @Test
  void addRecipe_InvalidData_ReturnsBadRequest() throws Exception {
    // Arrange: 模拟服务层抛出 IllegalArgumentException
    when(recipeService.addRecipe(any(Recipe.class)))
        .thenThrow(new IllegalArgumentException("Invalid recipe data"));

    // Act & Assert: 发送POST请求并验证响应状态和错误消息
    mockMvc
        .perform(
            post("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRecipe)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Invalid recipe data"));

    // 验证服务层方法被调用一次
    verify(recipeService, times(1)).addRecipe(any(Recipe.class));
  }

  /** 测试添加食谱时发生服务器错误 验证返回500 INTERNAL SERVER ERROR状态 */
  @Test
  void addRecipe_ServerError_ReturnsInternalServerError() throws Exception {
    // Arrange: 模拟服务层抛出通用异常
    when(recipeService.addRecipe(any(Recipe.class)))
        .thenThrow(new RuntimeException("Database error"));

    // Act & Assert: 发送POST请求并验证响应状态和错误消息
    mockMvc
        .perform(
            post("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRecipe)))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("Failed to add recipe"));

    // 验证服务层方法被调用一次
    verify(recipeService, times(1)).addRecipe(any(Recipe.class));
  }

  /** 测试成功更新一个现有的食谱 验证返回200 OK状态，并且服务层的 updateRecipe 方法被调用 */
  @Test
  void updateRecipe_Success() throws Exception {
    // Arrange: 模拟服务层成功更新食谱
    when(recipeService.recipeExists(anyLong())).thenReturn(true);
    when(recipeService.updateRecipe(any(Recipe.class))).thenReturn(true);

    Long recipeId = sampleRecipe.getId();

    // Act & Assert: 发送PUT请求并验证响应状态和内容
    mockMvc
        .perform(
            put("/api/recipes/{id}", recipeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRecipe)))
        .andExpect(status().isOk())
        .andExpect(content().string("Recipe updated successfully"));

    // 验证服务层方法被调用一次，并且ID被正确设置
    verify(recipeService, times(1))
        .updateRecipe(argThat(recipe -> recipe.getId().equals(recipeId)));
  }

  /** 测试更新食谱时传入无效数据，服务层抛出IllegalArgumentException 验证返回400 BAD REQUEST状态和错误消息 */
  @Test
  void updateRecipe_InvalidData_ReturnsBadRequest() throws Exception {
    // Arrange: 模拟服务层抛出 IllegalArgumentException
    when(recipeService.recipeExists(anyLong())).thenReturn(true);
    when(recipeService.updateRecipe(any(Recipe.class)))
        .thenThrow(new IllegalArgumentException("Invalid recipe data"));
    Long recipeId = sampleRecipe.getId();

    // Act & Assert: 发送PUT请求并验证响应状态和错误消息
    mockMvc
        .perform(
            put("/api/recipes/{id}", recipeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRecipe)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Invalid recipe data"));

    // 验证服务层方法被调用一次
    verify(recipeService, times(1)).updateRecipe(any(Recipe.class));
  }

  /** 测试更新食谱时发生服务器错误 验证返回500 INTERNAL SERVER ERROR状态 */
  @Test
  void updateRecipe_ServerError_ReturnsInternalServerError() throws Exception {
    // Arrange: 模拟服务层抛出通用异常
    when(recipeService.recipeExists(anyLong())).thenReturn(true);
    when(recipeService.updateRecipe(any(Recipe.class)))
        .thenThrow(new RuntimeException("Database error"));
    Long recipeId = sampleRecipe.getId();

    // Act & Assert: 发送PUT请求并验证响应状态和错误消息
    mockMvc
        .perform(
            put("/api/recipes/{id}", recipeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRecipe)))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("Failed to update recipe"));

    // 验证服务层方法被调用一次
    verify(recipeService, times(1)).updateRecipe(any(Recipe.class));
  }

  /** 测试成功删除一个食谱 验证返回200 OK状态，并且服务层的 deleteRecipeById 方法被调用 */
  @Test
  void deleteRecipe_Success() throws Exception {
    // Arrange: 模拟服务层成功删除食谱
    when(recipeService.recipeExists(anyLong())).thenReturn(true); // 确保食谱存在
    Long recipeId = sampleRecipe.getId();

    // Act & Assert: 发送DELETE请求并验证响应状态和内容
    mockMvc
        .perform(delete("/api/recipes/{id}", recipeId))
        .andExpect(status().isOk())
        .andExpect(content().string("Recipe deleted successfully"));

    // 验证服务层方法被调用一次
    verify(recipeService, times(1)).deleteRecipeById(recipeId);
  }

  /** 测试当请求的食谱 ID 不存在时，控制器应返回 404 状态和相应的错误消息 */
  @Test
  void deleteRecipe_NotFound_ReturnsNotFound() throws Exception {
    // Arrange: 模拟服务层方法
    when(recipeService.recipeExists(anyLong())).thenReturn(false); // 确保食谱不存在
    Long recipeId = sampleRecipe.getId();

    // Act & Assert: 发送DELETE请求并验证响应状态和内容
    mockMvc
        .perform(delete("/api/recipes/{id}", recipeId))
        .andExpect(status().isNotFound())
        .andExpect(content().string("Recipe is not exist"));

    // 验证服务层方法被调用一次
    verify(recipeService, times(1)).recipeExists(recipeId);
  }

  /** 测试删除食谱时服务层抛出IllegalArgumentException 验证返回400 BAD REQUEST状态和错误消息 */
  @Test
  void deleteRecipe_InvalidData_ReturnsBadRequest() throws Exception {
    // Arrange: 模拟服务层抛出 IllegalArgumentException
    when(recipeService.recipeExists(anyLong())).thenReturn(true);
    doThrow(new IllegalArgumentException("Invalid recipe ID"))
        .when(recipeService)
        .deleteRecipeById(anyLong());
    Long recipeId = sampleRecipe.getId();

    // Act & Assert: 发送DELETE请求并验证响应状态和错误消息
    mockMvc
        .perform(delete("/api/recipes/{id}", recipeId))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Invalid recipe ID"));

    // 验证服务层方法被调用一次
    verify(recipeService, times(1)).deleteRecipeById(recipeId);
  }

  /** 测试成功获取一个存在的食谱 验证返回200 OK状态和食谱数据 */
  @Test
  void getRecipe_Found() throws Exception {
    // Arrange: 模拟服务层返回存在的食谱
    when(recipeService.getRecipeById(anyLong())).thenReturn(sampleRecipe);
    Long recipeId = sampleRecipe.getId();

    // Act & Assert: 发送GET请求并验证响应状态和返回的JSON内容
    mockMvc
        .perform(get("/api/recipes/{id}", recipeId))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(sampleRecipe.getId().intValue())))
        .andExpect(jsonPath("$.name", is(sampleRecipe.getName())))
        .andExpect(jsonPath("$.description", is(sampleRecipe.getDescription())));

    // 验证服务层方法被调用一次
    verify(recipeService, times(1)).getRecipeById(recipeId);
  }

  /** 测试获取一个不存在的食谱 验证返回404 NOT FOUND状态 */
  @Test
  void getRecipe_NotFound() throws Exception {
    // Arrange: 模拟服务层未找到食谱
    when(recipeService.getRecipeById(anyLong())).thenReturn(null);
    Long recipeId = sampleRecipe.getId();

    // Act & Assert: 发送GET请求并验证响应状态
    mockMvc.perform(get("/api/recipes/{id}", recipeId)).andExpect(status().isNotFound());

    // 验证服务层方法被调用一次
    verify(recipeService, times(1)).getRecipeById(recipeId);
  }

  /** 测试获取食谱时发生服务器错误 验证返回500 INTERNAL SERVER ERROR状态 */
  @Test
  void getRecipe_ServerError_ReturnsInternalServerError() throws Exception {
    // Arrange: 模拟服务层抛出通用异常
    when(recipeService.getRecipeById(anyLong())).thenThrow(new RuntimeException("Database error"));
    Long recipeId = sampleRecipe.getId();

    // Act & Assert: 发送GET请求并验证响应状态
    mockMvc.perform(get("/api/recipes/{id}", recipeId)).andExpect(status().isInternalServerError());

    // 验证服务层方法被调用一次
    verify(recipeService, times(1)).getRecipeById(recipeId);
  }

  /** 测试成功获取所有食谱 验证返回200 OK状态和食谱列表 */
  @Test
  void getAllRecipes_Success() throws Exception {
    // Arrange: 创建食谱列表并模拟服务层返回
    Recipe recipe1 = new Recipe();
    recipe1.setId(1L);
    recipe1.setName("Recipe One");
    Recipe recipe2 = new Recipe();
    recipe2.setId(2L);
    recipe2.setName("Recipe Two");
    List<Recipe> recipes = List.of(recipe1, recipe2);

    when(recipeService.getAllRecipes()).thenReturn(recipes);

    // Act & Assert: 发送GET请求并验证响应状态和返回的JSON内容
    mockMvc
        .perform(get("/api/recipes"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.length()", is(recipes.size())))
        .andExpect(jsonPath("$[0].id", is(recipe1.getId().intValue())))
        .andExpect(jsonPath("$[0].name", is(recipe1.getName())))
        .andExpect(jsonPath("$[1].id", is(recipe2.getId().intValue())))
        .andExpect(jsonPath("$[1].name", is(recipe2.getName())));

    // 验证服务层方法被调用一次
    verify(recipeService, times(1)).getAllRecipes();
  }

  /** 测试获取所有食谱时服务层返回空列表 验证返回200 OK状态和空列表 */
  @Test
  void getAllRecipes_EmptyList() throws Exception {
    // Arrange: 模拟服务层返回空列表
    when(recipeService.getAllRecipes()).thenReturn(List.of());

    // Act & Assert: 发送GET请求并验证响应状态和返回的JSON内容
    mockMvc
        .perform(get("/api/recipes"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.length()", is(0)));

    // 验证服务层方法被调用一次
    verify(recipeService, times(1)).getAllRecipes();
  }

  /** 测试获取所有食谱时发生服务器错误 验证返回500 INTERNAL SERVER ERROR状态 */
  @Test
  void getAllRecipes_ServerError_ReturnsInternalServerError() throws Exception {
    // Arrange: 模拟服务层抛出通用异常
    when(recipeService.getAllRecipes()).thenThrow(new RuntimeException("Database error"));

    // Act & Assert: 发送GET请求并验证响应状态
    mockMvc.perform(get("/api/recipes")).andExpect(status().isInternalServerError());

    // 验证服务层方法被调用一次
    verify(recipeService, times(1)).getAllRecipes();
  }

  /** 测试成功根据名称搜索食谱 验证返回200 OK状态和匹配的食谱列表 */
  @Test
  void searchRecipes_Found() throws Exception {
    // Arrange: 创建匹配的食谱列表并模拟服务层返回
    String searchName = "Chicken";
    Recipe recipe = new Recipe();
    recipe.setId(1L);
    recipe.setName("Chicken Curry");
    List<Recipe> recipes = List.of(recipe);

    when(recipeService.getRecipesByName(searchName)).thenReturn(recipes);

    // Act & Assert: 发送GET请求并验证响应状态和返回的JSON内容
    mockMvc
        .perform(get("/api/recipes/search").param("name", searchName))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.length()", is(recipes.size())))
        .andExpect(jsonPath("$[0].id", is(recipe.getId().intValue())))
        .andExpect(jsonPath("$[0].name", is(recipe.getName())));

    // 验证服务层方法被调用一次
    verify(recipeService, times(1)).getRecipesByName(searchName);
  }

  /** 测试根据名称搜索食谱时没有匹配结果 验证返回200 OK状态和空列表 */
  @Test
  void searchRecipes_NotFound() throws Exception {
    // Arrange: 模拟服务层返回空列表
    String searchName = "Beef";
    when(recipeService.getRecipesByName(searchName)).thenReturn(List.of());

    // Act & Assert: 发送GET请求并验证响应状态和返回的JSON内容
    mockMvc
        .perform(get("/api/recipes/search").param("name", searchName))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.length()", is(0)));

    // 验证服务层方法被调用一次
    verify(recipeService, times(1)).getRecipesByName(searchName);
  }

  /** 测试根据名称搜索食谱时服务层抛出异常 验证返回500 INTERNAL SERVER ERROR状态 */
  @Test
  void searchRecipes_ServerError_ReturnsInternalServerError() throws Exception {
    // Arrange: 模拟服务层抛出通用异常
    String searchName = "Pasta";
    when(recipeService.getRecipesByName(searchName))
        .thenThrow(new RuntimeException("Database error"));

    // Act & Assert: 发送GET请求并验证响应状态
    mockMvc
        .perform(get("/api/recipes/search").param("name", searchName))
        .andExpect(status().isInternalServerError());

    // 验证服务层方法被调用一次
    verify(recipeService, times(1)).getRecipesByName(searchName);
  }
}
