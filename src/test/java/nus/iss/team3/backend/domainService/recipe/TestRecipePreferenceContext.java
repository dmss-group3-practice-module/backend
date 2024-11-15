package nus.iss.team3.backend.domainService.recipe;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import nus.iss.team3.backend.entity.Recipe;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class TestRecipePreferenceContext {

  @InjectMocks private RecipePreferenceContext recipePreferenceContext;

  @Test
  public void recommend() throws IOException {
    IRecipeService recipeService = mock(IRecipeService.class);
    int userId = 1;
    boolean isDesc = true;
    List<Recipe> returnList = new ArrayList<>();
    RecommendStrategy mockStrategy = mock(RecommendByRating.class);
    when(mockStrategy.recommendRecipes(any(), anyInt(), anyBoolean())).thenReturn(returnList);
    recipePreferenceContext.setRecommendStrategy(mockStrategy);
    recipePreferenceContext.recommend(recipeService, userId, isDesc);

    verify(mockStrategy, times(1)).recommendRecipes(any(), anyInt(), anyBoolean());
  }
}
