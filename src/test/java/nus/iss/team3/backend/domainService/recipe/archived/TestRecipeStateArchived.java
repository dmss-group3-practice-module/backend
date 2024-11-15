package nus.iss.team3.backend.domainService.recipe.archived;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

import nus.iss.team3.backend.dataaccess.IRecipeDataAccess;
import nus.iss.team3.backend.domainService.recipe.status.RecipeStateArchived;
import nus.iss.team3.backend.entity.ERecipeStatus;
import nus.iss.team3.backend.entity.Recipe;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class TestRecipeStateArchived {

  @InjectMocks private RecipeStateArchived recipeStateArchived;

  @Mock private IRecipeDataAccess recipeDataAccess;

  @Test
  public void addRecipe_nullRecipe() {
    Recipe inputRecipe = null;
    Recipe expected = null;
    Recipe dbRecipe = null;

    when(recipeDataAccess.addRecipe(inputRecipe)).thenReturn(expected);
    //    when(recipeDataAccess.getRecipeById(inputRecipe.getId())).thenReturn(dbRecipe);

    assertThrows(IllegalArgumentException.class, () -> recipeStateArchived.addRecipe(inputRecipe));

    verify(recipeDataAccess, times(0)).addRecipe(any(Recipe.class));
    verify(recipeDataAccess, times(0)).getRecipeById(anyLong());
    verify(recipeDataAccess, times(0)).getRecipeByDraftId(anyLong());
    verify(recipeDataAccess, times(0)).deleteRecipeById(anyLong());
    verify(recipeDataAccess, times(0)).updateRecipe(any(Recipe.class));
  }

  @Test
  public void addRecipe_emptyRecipeId() {
    Recipe inputRecipe = new Recipe();
    inputRecipe.setId(null);
    Recipe expected = null;
    Recipe dbRecipe = null;

    when(recipeDataAccess.addRecipe(inputRecipe)).thenReturn(expected);

    Recipe actual = recipeStateArchived.addRecipe(inputRecipe);
    assertEquals(expected, actual);
    verify(recipeDataAccess, times(1)).addRecipe(any(Recipe.class));
    verify(recipeDataAccess, times(0)).getRecipeById(anyLong());
    verify(recipeDataAccess, times(0)).getRecipeByDraftId(anyLong());
    verify(recipeDataAccess, times(0)).deleteRecipeById(anyLong());
    verify(recipeDataAccess, times(0)).updateRecipe(any(Recipe.class));
  }

  @Test
  public void addRecipe_invalidRecipeId() {
    Recipe inputRecipe = new Recipe();
    inputRecipe.setId(-1L);
    Recipe expected = null;
    Recipe dbRecipe = null;

    when(recipeDataAccess.addRecipe(inputRecipe)).thenReturn(expected);

    Recipe actual = recipeStateArchived.addRecipe(inputRecipe);
    assertEquals(expected, actual);
    verify(recipeDataAccess, times(1)).addRecipe(any(Recipe.class));
    verify(recipeDataAccess, times(0)).getRecipeById(anyLong());
    verify(recipeDataAccess, times(0)).getRecipeByDraftId(anyLong());
    verify(recipeDataAccess, times(0)).deleteRecipeById(anyLong());
    verify(recipeDataAccess, times(0)).updateRecipe(any(Recipe.class));
  }

  @Test
  public void addRecipe_validRecipeId_nullDbRecipe() {
    Recipe inputRecipe = new Recipe();
    inputRecipe.setId(1L);
    Recipe expected = null;
    Recipe dbRecipe = null;

    when(recipeDataAccess.addRecipe(inputRecipe)).thenReturn(expected);
    when(recipeDataAccess.getRecipeById(inputRecipe.getId())).thenReturn(dbRecipe);

    Recipe actual = recipeStateArchived.addRecipe(inputRecipe);
    assertEquals(expected, actual);
    verify(recipeDataAccess, times(1)).addRecipe(any(Recipe.class));
    verify(recipeDataAccess, times(1)).getRecipeById(anyLong());
    verify(recipeDataAccess, times(0)).getRecipeByDraftId(anyLong());
    verify(recipeDataAccess, times(0)).deleteRecipeById(anyLong());
    verify(recipeDataAccess, times(0)).updateRecipe(any(Recipe.class));
  }

  @Test
  public void addRecipe_validRecipeId_statelessDbRecipe() {
    Recipe inputRecipe = new Recipe();
    inputRecipe.setId(1L);
    Recipe expected = null;
    Recipe dbRecipe = new Recipe();

    when(recipeDataAccess.addRecipe(inputRecipe)).thenReturn(expected);
    when(recipeDataAccess.getRecipeById(inputRecipe.getId())).thenReturn(dbRecipe);

    Recipe actual = recipeStateArchived.addRecipe(inputRecipe);
    assertEquals(expected, actual);
    verify(recipeDataAccess, times(0)).addRecipe(any(Recipe.class));
    verify(recipeDataAccess, times(1)).getRecipeById(anyLong());
    verify(recipeDataAccess, times(0)).getRecipeByDraftId(anyLong());
    verify(recipeDataAccess, times(0)).deleteRecipeById(anyLong());
    verify(recipeDataAccess, times(0)).updateRecipe(any(Recipe.class));
  }

  @Test
  public void addRecipe_validRecipeId_draftDbRecipe_dbMainRecipeNull() {
    Recipe inputRecipe = new Recipe();
    inputRecipe.setId(1L);
    Recipe expected = null;
    Recipe dbRecipe = new Recipe();
    Recipe dbMainRecipe = null;

    dbRecipe.setStatus(ERecipeStatus.DRAFT);

    when(recipeDataAccess.addRecipe(inputRecipe)).thenReturn(expected);
    when(recipeDataAccess.getRecipeById(inputRecipe.getId())).thenReturn(dbRecipe);
    when(recipeDataAccess.getRecipeByDraftId(inputRecipe.getId())).thenReturn(dbMainRecipe);

    Recipe actual = recipeStateArchived.addRecipe(inputRecipe);
    assertEquals(expected, actual);
    verify(recipeDataAccess, times(1)).addRecipe(any(Recipe.class));
    verify(recipeDataAccess, times(1)).getRecipeById(anyLong());
    verify(recipeDataAccess, times(1)).getRecipeByDraftId(anyLong());
    verify(recipeDataAccess, times(0)).deleteRecipeById(anyLong());
    verify(recipeDataAccess, times(0)).updateRecipe(any(Recipe.class));
  }

  @Test
  public void addRecipe_validRecipeId_draftDbRecipe_dbMainRecipeEmptyStatus() {
    Recipe inputRecipe = new Recipe();
    inputRecipe.setId(1L);
    Recipe expected = null;
    Recipe dbRecipe = new Recipe();
    Recipe dbMainRecipe = new Recipe();

    dbRecipe.setStatus(ERecipeStatus.DRAFT);

    when(recipeDataAccess.addRecipe(inputRecipe)).thenReturn(expected);
    when(recipeDataAccess.getRecipeById(inputRecipe.getId())).thenReturn(dbRecipe);
    when(recipeDataAccess.getRecipeByDraftId(inputRecipe.getId())).thenReturn(dbMainRecipe);

    Recipe actual = recipeStateArchived.addRecipe(inputRecipe);
    assertEquals(expected, actual);
    verify(recipeDataAccess, times(0)).addRecipe(any(Recipe.class));
    verify(recipeDataAccess, times(1)).getRecipeById(anyLong());
    verify(recipeDataAccess, times(1)).getRecipeByDraftId(anyLong());
    verify(recipeDataAccess, times(0)).deleteRecipeById(anyLong());
    verify(recipeDataAccess, times(0)).updateRecipe(any(Recipe.class));
  }

  @Test
  public void addRecipe_validRecipeId_draftDbRecipe_dbMainRecipeDraftStatus() {
    Recipe inputRecipe = new Recipe();
    inputRecipe.setId(1L);
    Recipe expected = null;
    Recipe dbRecipe = new Recipe();
    dbRecipe.setStatus(ERecipeStatus.DRAFT);
    Recipe dbMainRecipe = new Recipe();
    dbMainRecipe.setStatus(ERecipeStatus.DRAFT);

    when(recipeDataAccess.addRecipe(inputRecipe)).thenReturn(expected);
    when(recipeDataAccess.getRecipeById(inputRecipe.getId())).thenReturn(dbRecipe);
    when(recipeDataAccess.getRecipeByDraftId(inputRecipe.getId())).thenReturn(dbMainRecipe);

    Recipe actual = recipeStateArchived.addRecipe(inputRecipe);
    assertEquals(expected, actual);
    verify(recipeDataAccess, times(0)).addRecipe(any(Recipe.class));
    verify(recipeDataAccess, times(1)).getRecipeById(anyLong());
    verify(recipeDataAccess, times(1)).getRecipeByDraftId(anyLong());
    verify(recipeDataAccess, times(0)).deleteRecipeById(anyLong());
    verify(recipeDataAccess, times(0)).updateRecipe(any(Recipe.class));
  }

  @Test
  public void addRecipe_validRecipeId_draftDbRecipe_dbMainRecipePublishedStatus() {
    Recipe inputRecipe = new Recipe();
    inputRecipe.setId(1L);
    Recipe expected = inputRecipe;
    Recipe dbRecipe = new Recipe();
    dbRecipe.setStatus(ERecipeStatus.DRAFT);
    Recipe dbMainRecipe = new Recipe();
    dbMainRecipe.setStatus(ERecipeStatus.PUBLISHED);

    when(recipeDataAccess.addRecipe(inputRecipe)).thenReturn(expected);
    when(recipeDataAccess.getRecipeById(inputRecipe.getId())).thenReturn(dbRecipe);
    when(recipeDataAccess.getRecipeByDraftId(inputRecipe.getId())).thenReturn(dbMainRecipe);

    Recipe actual = recipeStateArchived.addRecipe(inputRecipe);
    assertEquals(expected, actual);
    verify(recipeDataAccess, times(0)).addRecipe(any(Recipe.class));
    verify(recipeDataAccess, times(1)).getRecipeById(anyLong());
    verify(recipeDataAccess, times(1)).getRecipeByDraftId(anyLong());
    verify(recipeDataAccess, times(1)).deleteRecipeById(anyLong());
    verify(recipeDataAccess, times(1)).updateRecipe(any(Recipe.class));
  }

  @Test
  public void addRecipe_validRecipeId_draftDbRecipe_dbMainRecipeArchivedStatus() {
    Recipe inputRecipe = new Recipe();
    inputRecipe.setId(1L);
    Recipe expected = null;
    Recipe dbRecipe = new Recipe();
    dbRecipe.setStatus(ERecipeStatus.DRAFT);
    Recipe dbMainRecipe = new Recipe();
    dbMainRecipe.setStatus(ERecipeStatus.ARCHIVED);

    when(recipeDataAccess.addRecipe(inputRecipe)).thenReturn(expected);
    when(recipeDataAccess.getRecipeById(inputRecipe.getId())).thenReturn(dbRecipe);
    when(recipeDataAccess.getRecipeByDraftId(inputRecipe.getId())).thenReturn(dbMainRecipe);

    Recipe actual = recipeStateArchived.addRecipe(inputRecipe);
    assertEquals(expected, actual);
    verify(recipeDataAccess, times(0)).addRecipe(any(Recipe.class));
    verify(recipeDataAccess, times(1)).getRecipeById(anyLong());
    verify(recipeDataAccess, times(1)).getRecipeByDraftId(anyLong());
    verify(recipeDataAccess, times(0)).deleteRecipeById(anyLong());
    verify(recipeDataAccess, times(0)).updateRecipe(any(Recipe.class));
  }

  @Test
  public void addRecipe_validRecipeId_draftDbRecipe_dbMainRecipeInvalidStatus() {
    Recipe inputRecipe = new Recipe();
    inputRecipe.setId(1L);
    Recipe expected = null;
    Recipe dbRecipe = new Recipe();
    dbRecipe.setStatus(ERecipeStatus.DRAFT);
    Recipe dbMainRecipe = new Recipe();
    dbMainRecipe.setStatus(null);

    when(recipeDataAccess.addRecipe(inputRecipe)).thenReturn(expected);
    when(recipeDataAccess.getRecipeById(inputRecipe.getId())).thenReturn(dbRecipe);
    when(recipeDataAccess.getRecipeByDraftId(inputRecipe.getId())).thenReturn(dbMainRecipe);

    Recipe actual = recipeStateArchived.addRecipe(inputRecipe);
    assertEquals(expected, actual);
    verify(recipeDataAccess, times(0)).addRecipe(any(Recipe.class));
    verify(recipeDataAccess, times(1)).getRecipeById(anyLong());
    verify(recipeDataAccess, times(1)).getRecipeByDraftId(anyLong());
    verify(recipeDataAccess, times(0)).deleteRecipeById(anyLong());
    verify(recipeDataAccess, times(0)).updateRecipe(any(Recipe.class));
  }

  @Test
  public void addRecipe_validRecipeId_publishedDbRecipe() {
    Recipe inputRecipe = new Recipe();
    inputRecipe.setId(1L);
    Recipe expected = null;
    Recipe dbRecipe = new Recipe();
    Recipe dbMainRecipe = null;

    dbRecipe.setStatus(ERecipeStatus.PUBLISHED);

    when(recipeDataAccess.addRecipe(inputRecipe)).thenReturn(expected);
    when(recipeDataAccess.getRecipeById(inputRecipe.getId())).thenReturn(dbRecipe);
    when(recipeDataAccess.getRecipeByDraftId(inputRecipe.getId())).thenReturn(dbMainRecipe);

    Recipe actual = recipeStateArchived.addRecipe(inputRecipe);
    assertEquals(expected, actual);
    verify(recipeDataAccess, times(1)).addRecipe(any(Recipe.class));
    verify(recipeDataAccess, times(1)).getRecipeById(anyLong());
    verify(recipeDataAccess, times(0)).getRecipeByDraftId(anyLong());
    verify(recipeDataAccess, times(0)).deleteRecipeById(anyLong());
    verify(recipeDataAccess, times(0)).updateRecipe(any(Recipe.class));
  }

  @Test
  public void addRecipe_validRecipeId_archivedDbRecipe() {
    Recipe inputRecipe = new Recipe();
    inputRecipe.setId(1L);
    Recipe expected = null;
    Recipe dbRecipe = new Recipe();
    Recipe dbMainRecipe = null;

    dbRecipe.setStatus(ERecipeStatus.ARCHIVED);

    when(recipeDataAccess.addRecipe(inputRecipe)).thenReturn(expected);
    when(recipeDataAccess.getRecipeById(inputRecipe.getId())).thenReturn(dbRecipe);
    when(recipeDataAccess.getRecipeByDraftId(inputRecipe.getId())).thenReturn(dbMainRecipe);

    Recipe actual = recipeStateArchived.addRecipe(inputRecipe);
    assertEquals(expected, actual);
    verify(recipeDataAccess, times(1)).addRecipe(any(Recipe.class));
    verify(recipeDataAccess, times(1)).getRecipeById(anyLong());
    verify(recipeDataAccess, times(0)).getRecipeByDraftId(anyLong());
    verify(recipeDataAccess, times(0)).deleteRecipeById(anyLong());
    verify(recipeDataAccess, times(0)).updateRecipe(any(Recipe.class));
  }

  @Test
  public void addRecipe_validRecipeId_invalidDbRecipe() {
    Recipe inputRecipe = new Recipe();
    inputRecipe.setId(1L);
    Recipe expected = null;
    Recipe dbRecipe = new Recipe();
    Recipe dbMainRecipe = null;

    dbRecipe.setStatus(null);

    when(recipeDataAccess.addRecipe(inputRecipe)).thenReturn(expected);
    when(recipeDataAccess.getRecipeById(inputRecipe.getId())).thenReturn(dbRecipe);
    when(recipeDataAccess.getRecipeByDraftId(inputRecipe.getId())).thenReturn(dbMainRecipe);

    Recipe actual = recipeStateArchived.addRecipe(inputRecipe);
    assertEquals(expected, actual);
    verify(recipeDataAccess, times(0)).addRecipe(any(Recipe.class));
    verify(recipeDataAccess, times(1)).getRecipeById(anyLong());
    verify(recipeDataAccess, times(0)).getRecipeByDraftId(anyLong());
    verify(recipeDataAccess, times(0)).deleteRecipeById(anyLong());
    verify(recipeDataAccess, times(0)).updateRecipe(any(Recipe.class));
  }

  @Test
  public void updateRecipe_nullInput() {
    Recipe inputRecipe = null;

    boolean expected = false;

    when(recipeDataAccess.addRecipe(inputRecipe)).thenReturn(inputRecipe);

    assertThrows(
        IllegalArgumentException.class, () -> recipeStateArchived.updateRecipe(inputRecipe));

    verify(recipeDataAccess, times(0)).getRecipeById(anyLong()); // 1
    verify(recipeDataAccess, times(0)).getRecipeByDraftId(anyLong()); // 1
    verify(recipeDataAccess, times(0)).deleteRecipeById(anyLong()); // 1
    verify(recipeDataAccess, times(0)).updateRecipe(any(Recipe.class)); // 2
  }

  @Test
  public void updateRecipe_validInput_nullRecipeId() {
    Recipe inputRecipe = new Recipe();
    inputRecipe.setId(null);

    boolean expected = false;

    assertThrows(
        IllegalArgumentException.class, () -> recipeStateArchived.updateRecipe(inputRecipe));

    verify(recipeDataAccess, times(0)).getRecipeById(anyLong()); // 1
    verify(recipeDataAccess, times(0)).getRecipeByDraftId(anyLong()); // 1
    verify(recipeDataAccess, times(0)).deleteRecipeById(anyLong()); // 1
    verify(recipeDataAccess, times(0)).updateRecipe(any(Recipe.class)); // 2
  }

  @Test
  public void updateRecipe_validInput_Minus1RecipeId() {
    Recipe inputRecipe = new Recipe();
    inputRecipe.setId(-1L);

    boolean expected = false;

    assertThrows(
        IllegalArgumentException.class, () -> recipeStateArchived.updateRecipe(inputRecipe));

    verify(recipeDataAccess, times(0)).getRecipeById(anyLong()); // 1
    verify(recipeDataAccess, times(0)).getRecipeByDraftId(anyLong()); // 1
    verify(recipeDataAccess, times(0)).deleteRecipeById(anyLong()); // 1
    verify(recipeDataAccess, times(0)).updateRecipe(any(Recipe.class)); // 2
  }

  @Test
  public void updateRecipe_validInput_validRecipeId_nullDbRecipe() {
    Recipe inputRecipe = new Recipe();
    inputRecipe.setId(1L);
    Recipe dbRecipe = null;

    boolean expected = false;

    when(recipeDataAccess.getRecipeById(inputRecipe.getId())).thenReturn(dbRecipe);

    assertThrows(
        IllegalArgumentException.class, () -> recipeStateArchived.updateRecipe(inputRecipe));

    verify(recipeDataAccess, times(1)).getRecipeById(anyLong()); // 1
    verify(recipeDataAccess, times(0)).getRecipeByDraftId(anyLong()); // 1
    verify(recipeDataAccess, times(0)).deleteRecipeById(anyLong()); // 1
    verify(recipeDataAccess, times(0)).updateRecipe(any(Recipe.class)); // 2
  }

  @Test
  public void updateRecipe_validInput_validRecipeId_validRecipe_draftStatus_dbMainRecipeNull() {
    Recipe inputRecipe = new Recipe();
    inputRecipe.setId(1L);
    Recipe dbRecipe = new Recipe();
    dbRecipe.setStatus(ERecipeStatus.DRAFT);
    Recipe dbMainRecipe = null;

    boolean expected = false;

    when(recipeDataAccess.getRecipeById(inputRecipe.getId())).thenReturn(dbRecipe);
    when(recipeDataAccess.getRecipeByDraftId(inputRecipe.getId())).thenReturn(dbMainRecipe);

    Boolean actual = recipeStateArchived.updateRecipe(inputRecipe);
    assertEquals(expected, actual);

    verify(recipeDataAccess, times(1)).getRecipeById(anyLong()); // 1
    verify(recipeDataAccess, times(1)).getRecipeByDraftId(anyLong()); // 1
    verify(recipeDataAccess, times(0)).deleteRecipeById(anyLong()); // 1
    verify(recipeDataAccess, times(1)).updateRecipe(any(Recipe.class)); // 2
  }

  @Test
  public void updateRecipe_validInput_validRecipeId_validRecipe_draftStatus_dbMainRecipeValid() {
    Recipe inputRecipe = new Recipe();
    inputRecipe.setId(1L);
    Recipe dbRecipe = new Recipe();
    dbRecipe.setId(2L);
    dbRecipe.setStatus(ERecipeStatus.DRAFT);
    Recipe dbMainRecipe = new Recipe();

    boolean expected = false;
    when(recipeDataAccess.getRecipeById(inputRecipe.getId())).thenReturn(dbRecipe);
    when(recipeDataAccess.getRecipeByDraftId(inputRecipe.getId())).thenReturn(dbMainRecipe);

    Boolean actual = recipeStateArchived.updateRecipe(inputRecipe);
    assertEquals(expected, actual);
    verify(recipeDataAccess, times(1)).getRecipeById(anyLong()); // 1
    verify(recipeDataAccess, times(1)).getRecipeByDraftId(anyLong()); // 1
    verify(recipeDataAccess, times(1)).deleteRecipeById(anyLong()); // 1
    verify(recipeDataAccess, times(1)).updateRecipe(any(Recipe.class)); // 2
  }

  @Test
  public void updateRecipe_validInput_validRecipeId_validRecipe_publishedStatus_NullDraftRecipe() {
    Recipe inputRecipe = new Recipe();
    inputRecipe.setId(1L);
    Recipe dbRecipe = new Recipe();
    dbRecipe.setId(2L);
    dbRecipe.setStatus(ERecipeStatus.PUBLISHED);
    dbRecipe.setDraftRecipe(null);
    Recipe dbMainRecipe = new Recipe();

    boolean expected = false;
    when(recipeDataAccess.getRecipeById(inputRecipe.getId())).thenReturn(dbRecipe);

    Boolean actual = recipeStateArchived.updateRecipe(inputRecipe);
    assertEquals(expected, actual);
    verify(recipeDataAccess, times(1)).getRecipeById(anyLong()); // 1
    verify(recipeDataAccess, times(0)).getRecipeByDraftId(anyLong()); // 1
    verify(recipeDataAccess, times(0)).deleteRecipeById(anyLong()); // 1
    verify(recipeDataAccess, times(1)).updateRecipe(any(Recipe.class)); // 2
  }

  @Test
  public void
      updateRecipe_validInput_validRecipeId_validRecipe_publishedStatus_DraftValidRecipeId() {
    Recipe inputRecipe = new Recipe();
    inputRecipe.setId(1L);
    Recipe dbRecipe = new Recipe();
    dbRecipe.setId(2L);
    dbRecipe.setStatus(ERecipeStatus.PUBLISHED);
    dbRecipe.setDraftRecipe(new Recipe());
    Recipe dbMainRecipe = new Recipe();

    boolean expected = false;
    when(recipeDataAccess.getRecipeById(inputRecipe.getId())).thenReturn(dbRecipe);

    Boolean actual = recipeStateArchived.updateRecipe(inputRecipe);
    assertEquals(expected, actual);
    verify(recipeDataAccess, times(1)).getRecipeById(anyLong()); // 1
    verify(recipeDataAccess, times(0)).getRecipeByDraftId(anyLong()); // 1
    verify(recipeDataAccess, times(0)).deleteRecipeById(anyLong()); // 1
    verify(recipeDataAccess, times(1)).updateRecipe(any(Recipe.class)); // 2
  }

  @Test
  public void
      updateRecipe_validInput_validRecipeId_validRecipe_publishedStatus_DraftRecipeIdValue() {
    Recipe inputRecipe = new Recipe();
    inputRecipe.setId(1L);
    Recipe dbRecipe = new Recipe();
    dbRecipe.setId(2L);
    dbRecipe.setStatus(ERecipeStatus.PUBLISHED);
    {
      Recipe draftRecipe = new Recipe();
      draftRecipe.setId(3L);
      dbRecipe.setDraftRecipe(draftRecipe);
    }
    Recipe dbMainRecipe = new Recipe();

    boolean expected = false;
    when(recipeDataAccess.getRecipeById(inputRecipe.getId())).thenReturn(dbRecipe);

    Boolean actual = recipeStateArchived.updateRecipe(inputRecipe);
    assertEquals(expected, actual);
    verify(recipeDataAccess, times(1)).getRecipeById(anyLong()); // 1
    verify(recipeDataAccess, times(0)).getRecipeByDraftId(anyLong()); // 1
    verify(recipeDataAccess, times(1)).deleteRecipeById(anyLong()); // 1
    verify(recipeDataAccess, times(1)).updateRecipe(any(Recipe.class)); // 2
  }

  @Test
  public void updateRecipe_validInput_validRecipeId_validRecipe_archivedStatus() {
    Recipe inputRecipe = new Recipe();
    inputRecipe.setId(1L);
    Recipe dbRecipe = new Recipe();
    dbRecipe.setId(2L);
    dbRecipe.setStatus(ERecipeStatus.ARCHIVED);

    Recipe dbMainRecipe = new Recipe();

    boolean expected = false;
    when(recipeDataAccess.getRecipeById(inputRecipe.getId())).thenReturn(dbRecipe);

    Boolean actual = recipeStateArchived.updateRecipe(inputRecipe);
    assertEquals(expected, actual);
    verify(recipeDataAccess, times(1)).getRecipeById(anyLong()); // 1
    verify(recipeDataAccess, times(0)).getRecipeByDraftId(anyLong()); // 1
    verify(recipeDataAccess, times(0)).deleteRecipeById(anyLong()); // 1
    verify(recipeDataAccess, times(0)).updateRecipe(any(Recipe.class)); // 2
  }

  @Test
  public void updateRecipe_validInput_validRecipeId_validRecipe_nullStatus() {
    Recipe inputRecipe = new Recipe();
    inputRecipe.setId(1L);
    Recipe dbRecipe = new Recipe();
    dbRecipe.setId(2L);
    dbRecipe.setStatus(null);

    Recipe dbMainRecipe = new Recipe();

    boolean expected = false;
    when(recipeDataAccess.getRecipeById(inputRecipe.getId())).thenReturn(dbRecipe);

    Boolean actual = recipeStateArchived.updateRecipe(inputRecipe);
    assertEquals(expected, actual);
    verify(recipeDataAccess, times(1)).getRecipeById(anyLong()); // 1
    verify(recipeDataAccess, times(0)).getRecipeByDraftId(anyLong()); // 1
    verify(recipeDataAccess, times(0)).deleteRecipeById(anyLong()); // 1
    verify(recipeDataAccess, times(0)).updateRecipe(any(Recipe.class)); // 2
  }
}
