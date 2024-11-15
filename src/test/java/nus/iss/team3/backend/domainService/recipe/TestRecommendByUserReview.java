package nus.iss.team3.backend.domainService.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
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
public class TestRecommendByUserReview {
  @InjectMocks private RecommendByUserReview recommendByUserReview;

  @Test
  public void recommend() throws IOException {
    IRecipeService mockRecommendByUserReview = mock(RecipeService.class);
    when(mockRecommendByUserReview.getRecipesByUserReview(anyInt(), anyBoolean()))
        .thenReturn(new ArrayList<>());
    int userId = 1;
    boolean isDesc = true;

    List<Recipe> actuals =
        recommendByUserReview.recommendRecipes(mockRecommendByUserReview, userId, isDesc);

    assertEquals(0, actuals.size());

    verify(mockRecommendByUserReview, times(1)).getRecipesByUserReview(anyInt(), anyBoolean());
  }
}
