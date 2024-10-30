package nus.iss.team3.backend.domainService.ingredient;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import nus.iss.team3.backend.ProfileConfig;
import nus.iss.team3.backend.domainService.webservice.IWebserviceCaller;
import nus.iss.team3.backend.entity.UserIngredient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Profile(ProfileConfig.PROFILE_NOT + ProfileConfig.PROFILE_INGREDIENT)
public class IngredientWebCaller implements IIngredientService {
  private static final Logger logger = LogManager.getLogger(IngredientWebCaller.class);

  @Value("${service.url.ingredient.address}")
  private String serviceUrl;

  @Value("${service.url.ingredient.port}")
  private String servicePort;

  @Autowired private IWebserviceCaller webServiceCaller;

  private String getUrl(String path) {
    return "http://" + serviceUrl + ":" + servicePort + path;
  }

  @PostConstruct
  public void postContruct() {
    logger.info("Ingredient Service Web Caller initialized.");
  }

  /**
   * @param ingredient
   * @return
   */
  @Override
  public boolean addIngredient(UserIngredient ingredient) {
    String url = getUrl("/ingredient/add");

    ResponseEntity<Boolean> response = webServiceCaller.postCall(url, ingredient, Boolean.class);
    if (response.getStatusCode().is2xxSuccessful()) {
      if (response.getBody() != null) return response.getBody();
    }
    return false;
  }

  /**
   * @param ingredient
   * @return
   */
  @Override
  public boolean updateIngredient(UserIngredient ingredient) {
    String url = getUrl("/ingredient/update");
    ResponseEntity<Boolean> response = webServiceCaller.postCall(url, ingredient, Boolean.class);
    if (response.getStatusCode().is2xxSuccessful()) {
      if (response.getBody() != null) return response.getBody();
    }
    return false;
  }

  /**
   * @param id
   * @return
   */
  @Override
  public boolean deleteIngredientById(Integer id) {
    String url = getUrl("/ingredient/delete/" + id);
    ResponseEntity<Boolean> response = webServiceCaller.deleteCall(url, Boolean.class);
    if (response.getStatusCode().is2xxSuccessful()) {
      if (response.getBody() != null) return response.getBody();
    }
    return false;
  }

  /**
   * @param id
   * @return
   */
  @Override
  public UserIngredient getIngredientById(Integer id) {
    String url = getUrl("/ingredient/get/" + id);
    ResponseEntity<UserIngredient> response = webServiceCaller.getCall(url, UserIngredient.class);
    if (response.getStatusCode().is2xxSuccessful()) {
      return response.getBody();
    }
    return null;
  }

  /**
   * @param name
   * @return
   */
  @Override
  public List<UserIngredient> getIngredientsByName(String name) {
    String url = getUrl("/ingredient/get/name/" + name);
    ParameterizedTypeReference<List<UserIngredient>> typeRef =
        new ParameterizedTypeReference<>() {};
    ResponseEntity<List<UserIngredient>> response = webServiceCaller.getCall(url, typeRef);
    if (response.getStatusCode().is2xxSuccessful()) {
      return response.getBody();
    } else {
      logger.error("Failed to retrieve all recipes. Status code: {}", response.getStatusCode());
      return null;
    }
  }

  /**
   * @return
   */
  @Override
  public List<UserIngredient> getIngredientsByUser(Integer id) {
    String url = getUrl("/ingredient/getAll/" + id);
    ParameterizedTypeReference<List<UserIngredient>> typeRef =
        new ParameterizedTypeReference<>() {};
    ResponseEntity<List<UserIngredient>> response = webServiceCaller.getCall(url, typeRef);
    if (response.getStatusCode().is2xxSuccessful()) {
      return response.getBody();
    } else {
      logger.error("Failed to retrieve all recipes. Status code: {}", response.getStatusCode());
      return null;
    }
  }

  /**
   * @param userId
   * @return
   */
  @Override
  public boolean deleteIngredientsByUser(Integer userId) {
    String url = getUrl("/ingredient/delete/user/" + userId);
    ResponseEntity<Boolean> response = webServiceCaller.getCall(url, Boolean.class);
    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
      return response.getBody();
    }
    return false;
  }

  @Override
  public List<UserIngredient> getExpiringIngredients(Integer userId, int days) {
    String url = getUrl("/ingredient/expiring-list/?userId=" + userId + "&days=" + days);
    ParameterizedTypeReference<List<UserIngredient>> typeRef =
        new ParameterizedTypeReference<>() {};
    ResponseEntity<List<UserIngredient>> response = webServiceCaller.getCall(url, typeRef);
    if (response.getStatusCode().is2xxSuccessful()) {
      return response.getBody();
    } else {
      logger.error(
          "Failed to retrieve expiring ingredients. Status code: {}", response.getStatusCode());
      return null;
    }
  }

  @Override
  public List<UserIngredient> getExpiringIngredientsInRange() {
    String url = getUrl("/ingredient/expiring-range");
    ParameterizedTypeReference<List<UserIngredient>> typeRef =
        new ParameterizedTypeReference<>() {};
    ResponseEntity<List<UserIngredient>> response = webServiceCaller.getCall(url, typeRef);

    if (response.getStatusCode().is2xxSuccessful()) {
      return response.getBody();
    } else {
      logger.error(
          "Failed to retrieve expiring ingredients. Status code: {}", response.getStatusCode());
      return new ArrayList<>();
    }
  }
}
