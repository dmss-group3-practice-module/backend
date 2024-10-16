package nus.iss.team3.backend.service;

import java.util.Arrays;
import java.util.List;
import nus.iss.team3.backend.entity.UserAccount;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
// @Primary
public class UserWebCaller implements IUserAccountService {

  private static final Logger logger = LogManager.getLogger(UserWebCaller.class);

  @Autowired private RestTemplate restTemplate;

  @Value("${user.service.url}")
  private String userServiceUrl;

  @Override
  public boolean addUser(UserAccount userAccount) {
    try {
      ResponseEntity<Boolean> response =
          restTemplate.postForEntity(userServiceUrl + "/user/add", userAccount, Boolean.class);
      logger.info("Add user request sent for user: {}", userAccount.getName());
      return response.getBody() != null && response.getBody();
    } catch (Exception e) {
      logger.error("Error adding user: {}", userAccount.getName(), e);
      return false;
    }
  }

  @Override
  public boolean updateUser(UserAccount userAccount) {
    try {
      ResponseEntity<Boolean> response =
          restTemplate.postForEntity(userServiceUrl + "/user/update", userAccount, Boolean.class);
      logger.info("Update user request sent for user: {}", userAccount.getName());
      return response.getBody() != null && response.getBody();
    } catch (Exception e) {
      logger.error("Error updating user: {}", userAccount.getName(), e);
      return false;
    }
  }

  @Override
  public boolean deleteUserById(Integer id) {
    try {
      restTemplate.delete(userServiceUrl + "/user/delete/{id}", id);
      logger.info("Delete user request sent for user id: {}", id);
      return true;
    } catch (Exception e) {
      logger.error("Error deleting user with id: {}", id, e);
      return false;
    }
  }

  @Override
  public UserAccount getUserById(Integer id) {
    try {
      ResponseEntity<UserAccount> response =
          restTemplate.getForEntity(userServiceUrl + "/user/get/{id}", UserAccount.class, id);
      logger.info("Get user request sent for user id: {}", id);
      return response.getBody();
    } catch (Exception e) {
      logger.error("Error getting user with id: {}", id, e);
      return null;
    }
  }

  @Override
  public List<UserAccount> getAllUsers() {
    try {
      ResponseEntity<UserAccount[]> response =
          restTemplate.getForEntity(userServiceUrl + "/user/getAll", UserAccount[].class);
      logger.info("Get all users request sent");
      return Arrays.asList(response.getBody());
    } catch (Exception e) {
      logger.error("Error getting all users", e);
      return null;
    }
  }
}
