package nus.iss.team3.backend.domainService.ingredient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import nus.iss.team3.backend.domainService.webservice.IWebserviceCaller;
import nus.iss.team3.backend.entity.UserIngredient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class TestIngredientWebCaller {

  @InjectMocks private IngredientWebCaller ingredientWebCaller;

  @Mock private IWebserviceCaller webServiceCaller;

  @BeforeEach
  public void setUp() {
    ingredientWebCaller.postConstruct();
  }

  @Test
  public void addIngredient_Success() {
    UserIngredient ingredient = new UserIngredient();
    String endingUrl = "/ingredient/add";
    ResponseEntity<Boolean> responseEntity = new ResponseEntity<>(true, HttpStatus.OK);

    when(webServiceCaller.postCall(
            endsWith(endingUrl), any(UserIngredient.class), eq(Boolean.class)))
        .thenReturn(responseEntity);

    boolean result = ingredientWebCaller.addIngredient(ingredient);

    assertTrue(result);
    verify(webServiceCaller, times(1))
        .postCall(endsWith(endingUrl), any(UserIngredient.class), eq(Boolean.class));
  }

  @Test
  public void addIngredient_Success_MissingBody() {
    UserIngredient ingredient = new UserIngredient();
    String endingUrl = "/ingredient/add";
    ResponseEntity<Boolean> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

    when(webServiceCaller.postCall(
            endsWith(endingUrl), any(UserIngredient.class), eq(Boolean.class)))
        .thenReturn(responseEntity);

    boolean result = ingredientWebCaller.addIngredient(ingredient);

    assertFalse(result);
    verify(webServiceCaller, times(1))
        .postCall(endsWith(endingUrl), any(UserIngredient.class), eq(Boolean.class));
  }

  @Test
  public void addIngredient_Failure() {
    UserIngredient ingredient = new UserIngredient();
    String endingUrl = "/ingredient/add";
    ResponseEntity<Boolean> responseEntity = new ResponseEntity<>(true, HttpStatus.BAD_REQUEST);

    when(webServiceCaller.postCall(
            endsWith(endingUrl), any(UserIngredient.class), eq(Boolean.class)))
        .thenReturn(responseEntity);

    boolean result = ingredientWebCaller.addIngredient(ingredient);

    assertFalse(result);
    verify(webServiceCaller, times(1))
        .postCall(endsWith(endingUrl), any(UserIngredient.class), eq(Boolean.class));
  }

  @Test
  public void updateIngredient_Success() {
    UserIngredient ingredient = new UserIngredient();
    String endingUrl = "/ingredient/update";
    ResponseEntity<Boolean> responseEntity = new ResponseEntity<>(true, HttpStatus.OK);

    when(webServiceCaller.postCall(
            endsWith(endingUrl), any(UserIngredient.class), eq(Boolean.class)))
        .thenReturn(responseEntity);

    boolean result = ingredientWebCaller.updateIngredient(ingredient);

    assertTrue(result);
    verify(webServiceCaller, times(1))
        .postCall(endsWith(endingUrl), any(UserIngredient.class), eq(Boolean.class));
  }

  @Test
  public void updateIngredient_Success_MissingBody() {
    UserIngredient ingredient = new UserIngredient();
    String endingUrl = "/ingredient/update";
    ResponseEntity<Boolean> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

    when(webServiceCaller.postCall(
            endsWith(endingUrl), any(UserIngredient.class), eq(Boolean.class)))
        .thenReturn(responseEntity);

    boolean result = ingredientWebCaller.updateIngredient(ingredient);

    assertFalse(result);
    verify(webServiceCaller, times(1))
        .postCall(endsWith(endingUrl), any(UserIngredient.class), eq(Boolean.class));
  }

  @Test
  public void updateIngredient_Failure() {
    UserIngredient ingredient = new UserIngredient();
    String endingUrl = "/ingredient/update";
    ResponseEntity<Boolean> responseEntity = new ResponseEntity<>(true, HttpStatus.BAD_REQUEST);

    when(webServiceCaller.postCall(
            endsWith(endingUrl), any(UserIngredient.class), eq(Boolean.class)))
        .thenReturn(responseEntity);

    boolean result = ingredientWebCaller.updateIngredient(ingredient);

    assertFalse(result);
    verify(webServiceCaller, times(1))
        .postCall(endsWith(endingUrl), any(UserIngredient.class), eq(Boolean.class));
  }

  @Test
  public void deleteIngredientById_Success() {
    int ingredientId = 1;
    String endingUrl = "/ingredient/delete/" + ingredientId;
    ResponseEntity<Boolean> responseEntity = new ResponseEntity<>(true, HttpStatus.OK);

    when(webServiceCaller.deleteCall(endsWith(endingUrl), eq(Boolean.class)))
        .thenReturn(responseEntity);

    boolean result = ingredientWebCaller.deleteIngredientById(ingredientId);

    assertTrue(result);
    verify(webServiceCaller, times(1)).deleteCall(endsWith(endingUrl), eq(Boolean.class));
  }

  @Test
  public void deleteIngredientById_Success_MissingBody() {
    int ingredientId = 1;
    String endingUrl = "/ingredient/delete/" + ingredientId;
    ResponseEntity<Boolean> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

    when(webServiceCaller.deleteCall(endsWith(endingUrl), eq(Boolean.class)))
        .thenReturn(responseEntity);

    boolean result = ingredientWebCaller.deleteIngredientById(ingredientId);

    assertFalse(result);
    verify(webServiceCaller, times(1)).deleteCall(endsWith(endingUrl), eq(Boolean.class));
  }

  @Test
  public void deleteIngredientById_Failure() {
    int ingredientId = 1;
    String endingUrl = "/ingredient/delete/" + ingredientId;
    ResponseEntity<Boolean> responseEntity = new ResponseEntity<>(true, HttpStatus.BAD_REQUEST);

    when(webServiceCaller.deleteCall(endsWith(endingUrl), eq(Boolean.class)))
        .thenReturn(responseEntity);

    boolean result = ingredientWebCaller.deleteIngredientById(ingredientId);

    assertFalse(result);
    verify(webServiceCaller, times(1)).deleteCall(endsWith(endingUrl), eq(Boolean.class));
  }

  @Test
  public void getIngredientById_Success() {
    int ingredientId = 1;
    String endingUrl = "/ingredient/get/" + ingredientId;
    ResponseEntity<UserIngredient> responseEntity =
        new ResponseEntity<>(generateSampleUserIngredient(), HttpStatus.OK);

    when(webServiceCaller.getCall(endsWith(endingUrl), eq(UserIngredient.class)))
        .thenReturn(responseEntity);

    UserIngredient result = ingredientWebCaller.getIngredientById(ingredientId);

    assertNotNull(result);
    assertEquals(1, result.getId());
    verify(webServiceCaller, times(1)).getCall(endsWith(endingUrl), eq(UserIngredient.class));
  }

  @Test
  public void getIngredientById_Success_MissingBody() {
    int ingredientId = 1;
    String endingUrl = "/ingredient/get/" + ingredientId;
    ResponseEntity<UserIngredient> responseEntity =
        new ResponseEntity<>(generateSampleUserIngredient(), HttpStatus.OK);

    when(webServiceCaller.getCall(endsWith(endingUrl), eq(UserIngredient.class)))
        .thenReturn(responseEntity);

    UserIngredient result = ingredientWebCaller.getIngredientById(ingredientId);

    assertNotNull(result);
    assertEquals(1, result.getId());
    verify(webServiceCaller, times(1)).getCall(endsWith(endingUrl), eq(UserIngredient.class));
  }

  @Test
  public void getIngredientById_Failure() {
    int ingredientId = 1;
    String endingUrl = "/ingredient/get/" + ingredientId;
    ResponseEntity<UserIngredient> responseEntity =
        new ResponseEntity<>(generateSampleUserIngredient(), HttpStatus.BAD_REQUEST);

    when(webServiceCaller.getCall(endsWith(endingUrl), eq(UserIngredient.class)))
        .thenReturn(responseEntity);

    UserIngredient result = ingredientWebCaller.getIngredientById(ingredientId);

    assertNull(result);
    verify(webServiceCaller, times(1)).getCall(endsWith(endingUrl), eq(UserIngredient.class));
  }

  @Test
  public void getIngredientsByName_Success() {
    String ingredientName = "name";
    String endingUrl = "/ingredient/get/name/" + ingredientName;
    List<UserIngredient> input = new ArrayList<>();
    input.add(generateSampleUserIngredient());
    ResponseEntity<List<UserIngredient>> responseEntity =
        new ResponseEntity<>(input, HttpStatus.OK);

    when(webServiceCaller.getCall(endsWith(endingUrl), any(ParameterizedTypeReference.class)))
        .thenReturn(responseEntity);

    List<UserIngredient> result = ingredientWebCaller.getIngredientsByName(ingredientName);

    assertNotNull(result);
    verify(webServiceCaller, times(1))
        .getCall(endsWith(endingUrl), any(ParameterizedTypeReference.class));
  }

  @Test
  public void getIngredientsByName_Failure() {
    String ingredientName = "name";
    String endingUrl = "/ingredient/get/name/" + ingredientName;
    List<UserIngredient> input = new ArrayList<>();
    input.add(generateSampleUserIngredient());
    ResponseEntity<List<UserIngredient>> responseEntity =
        new ResponseEntity<>(input, HttpStatus.BAD_REQUEST);

    when(webServiceCaller.getCall(endsWith(endingUrl), any(ParameterizedTypeReference.class)))
        .thenReturn(responseEntity);

    List<UserIngredient> result = ingredientWebCaller.getIngredientsByName(ingredientName);

    assertNull(result);
    verify(webServiceCaller, times(1))
        .getCall(endsWith(endingUrl), any(ParameterizedTypeReference.class));
  }

  @Test
  public void getIngredientsByUser_Success() {
    int ingredientId = 1;
    String endingUrl = "/ingredient/getAll/" + ingredientId;
    List<UserIngredient> input = new ArrayList<>();
    input.add(generateSampleUserIngredient());
    ResponseEntity<List<UserIngredient>> responseEntity =
        new ResponseEntity<>(input, HttpStatus.OK);

    when(webServiceCaller.getCall(endsWith(endingUrl), any(ParameterizedTypeReference.class)))
        .thenReturn(responseEntity);

    List<UserIngredient> result = ingredientWebCaller.getIngredientsByUser(ingredientId);

    assertNotNull(result);
    verify(webServiceCaller, times(1))
        .getCall(endsWith(endingUrl), any(ParameterizedTypeReference.class));
  }

  @Test
  public void getIngredientsByUser_Failure() {
    int ingredientId = 1;
    String endingUrl = "/ingredient/getAll/" + ingredientId;
    List<UserIngredient> input = new ArrayList<>();
    input.add(generateSampleUserIngredient());
    ResponseEntity<List<UserIngredient>> responseEntity =
        new ResponseEntity<>(input, HttpStatus.BAD_REQUEST);

    when(webServiceCaller.getCall(endsWith(endingUrl), any(ParameterizedTypeReference.class)))
        .thenReturn(responseEntity);

    List<UserIngredient> result = ingredientWebCaller.getIngredientsByUser(ingredientId);

    assertNull(result);
    verify(webServiceCaller, times(1))
        .getCall(endsWith(endingUrl), any(ParameterizedTypeReference.class));
  }

  @Test
  public void deleteIngredientsByUser_Success() {
    int userId = 1;
    String endingUrl = "/ingredient/delete/user/" + userId;
    ResponseEntity<Boolean> responseEntity = new ResponseEntity<>(true, HttpStatus.OK);

    when(webServiceCaller.getCall(endsWith(endingUrl), eq(Boolean.class)))
        .thenReturn(responseEntity);

    boolean result = ingredientWebCaller.deleteIngredientsByUser(userId);

    assertTrue(result);
    verify(webServiceCaller, times(1)).getCall(endsWith(endingUrl), eq(Boolean.class));
  }

  @Test
  public void deleteIngredientsByUser_Success_MissingBody() {
    int userId = 1;
    String endingUrl = "/ingredient/delete/user/" + userId;
    ResponseEntity<Boolean> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

    when(webServiceCaller.getCall(endsWith(endingUrl), eq(Boolean.class)))
        .thenReturn(responseEntity);

    boolean result = ingredientWebCaller.deleteIngredientsByUser(userId);

    assertFalse(result);
    verify(webServiceCaller, times(1)).getCall(endsWith(endingUrl), eq(Boolean.class));
  }

  @Test
  public void deleteIngredientsByUser_Failure() {
    int userId = 1;
    String endingUrl = "/ingredient/delete/user/" + userId;
    ResponseEntity<Boolean> responseEntity = new ResponseEntity<>(true, HttpStatus.BAD_REQUEST);

    when(webServiceCaller.getCall(endsWith(endingUrl), eq(Boolean.class)))
        .thenReturn(responseEntity);

    boolean result = ingredientWebCaller.deleteIngredientsByUser(userId);

    assertFalse(result);
    verify(webServiceCaller, times(1)).getCall(endsWith(endingUrl), eq(Boolean.class));
  }

  @Test
  public void getExpiringIngredients_Success() {
    int userId = 1;
    int days = 2;
    String endingUrl = "/ingredient/expiring-list/?userId=" + userId + "&days=" + days;
    List<UserIngredient> input = new ArrayList<>();
    input.add(generateSampleUserIngredient());
    ResponseEntity<List<UserIngredient>> responseEntity =
        new ResponseEntity<>(input, HttpStatus.OK);

    when(webServiceCaller.getCall(endsWith(endingUrl), any(ParameterizedTypeReference.class)))
        .thenReturn(responseEntity);

    List<UserIngredient> result = ingredientWebCaller.getExpiringIngredients(userId, days);
    assertNotNull(result);
    verify(webServiceCaller, times(1))
        .getCall(endsWith(endingUrl), any(ParameterizedTypeReference.class));
  }

  @Test
  public void getExpiringIngredients_Failure() {
    int userId = 1;
    int days = 2;
    String endingUrl = "/ingredient/expiring-list/?userId=" + userId + "&days=" + days;
    List<UserIngredient> input = new ArrayList<>();
    input.add(generateSampleUserIngredient());
    ResponseEntity<List<UserIngredient>> responseEntity =
        new ResponseEntity<>(input, HttpStatus.BAD_REQUEST);

    when(webServiceCaller.getCall(endsWith(endingUrl), any(ParameterizedTypeReference.class)))
        .thenReturn(responseEntity);

    List<UserIngredient> result = ingredientWebCaller.getExpiringIngredients(userId, days);

    assertNull(result);
    verify(webServiceCaller, times(1))
        .getCall(endsWith(endingUrl), any(ParameterizedTypeReference.class));
  }

  @Test
  public void getExpiringIngredientsInRange_Success() {
    String endingUrl = "/ingredient/expiring-range";
    List<UserIngredient> input = new ArrayList<>();
    input.add(generateSampleUserIngredient());
    ResponseEntity<List<UserIngredient>> responseEntity =
        new ResponseEntity<>(input, HttpStatus.OK);

    when(webServiceCaller.getCall(endsWith(endingUrl), any(ParameterizedTypeReference.class)))
        .thenReturn(responseEntity);

    List<UserIngredient> result = ingredientWebCaller.getExpiringIngredientsInRange();
    assertNotNull(result);
    verify(webServiceCaller, times(1))
        .getCall(endsWith(endingUrl), any(ParameterizedTypeReference.class));
  }

  @Test
  public void getExpiringIngredientsInRange_Failure() {
    String endingUrl = "/ingredient/expiring-range";
    List<UserIngredient> input = new ArrayList<>();
    input.add(generateSampleUserIngredient());
    ResponseEntity<List<UserIngredient>> responseEntity =
        new ResponseEntity<>(input, HttpStatus.BAD_REQUEST);

    when(webServiceCaller.getCall(endsWith(endingUrl), any(ParameterizedTypeReference.class)))
        .thenReturn(responseEntity);

    List<UserIngredient> result = ingredientWebCaller.getExpiringIngredientsInRange();

    assertEquals(0, result.size());
    verify(webServiceCaller, times(1))
        .getCall(endsWith(endingUrl), any(ParameterizedTypeReference.class));
  }

  private UserIngredient generateSampleUserIngredient() {
    UserIngredient ingredient = new UserIngredient();
    ingredient.setId(1);
    return ingredient;
  }
}
