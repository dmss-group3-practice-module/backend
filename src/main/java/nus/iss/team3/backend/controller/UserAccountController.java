/* (C)2024 */
package nus.iss.team3.backend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import java.util.List;
import nus.iss.team3.backend.entity.UserAccount;
import nus.iss.team3.backend.service.IUserAccountService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class to handle web call for user related queries
 *
 * @author Desmond Tan Zhi Heng
 */
@RestController
@RequestMapping("user")
public class UserAccountController {

  private static final Logger logger = LogManager.getLogger(UserAccountController.class);

  @Autowired IUserAccountService userAccountService;

  @PostMapping("/add")
  public ResponseEntity<?> addUser(@RequestBody UserAccount userAccount) {
    try {
      if (userAccountService.addUser(userAccount)) {
        logger.info("Creation of User Account: {} completed", userAccount.getName());
        return new ResponseEntity<>(true, HttpStatus.OK);
      }
      logger.info("Creation of User Account: {} failed", userAccount.getName());
      return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (IllegalArgumentException e) {
      logger.error("Invalid input during creation of User Account: {}", userAccount.getName(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      logger.error("Unexpected error", e);
      return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/update")
  public ResponseEntity<?> updateUser(@RequestBody UserAccount userAccount) {
    try {
      if (userAccountService.updateUser(userAccount)) {
        logger.info("Update of User Account: {} completed", userAccount.getName());
        return new ResponseEntity<>(true, HttpStatus.OK);
      }
      logger.info("Update of User Account: {} failed", userAccount.getName());
      return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (IllegalArgumentException e) {
      logger.error("Invalid input during update of User Account: {}", userAccount.getName(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      logger.error("Unexpected error", e);
      return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
    try {
      if (userAccountService.deleteUserById(id)) {
        logger.info("Deletion of User Account: {} completed", id);
        return new ResponseEntity<>(true, HttpStatus.OK);
      }
      logger.info("Deletion of User Account: {} failed", id);
      return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (IllegalArgumentException e) {
      logger.error("Invalid input during deletion of User Account: {}", id, e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      logger.error("Unexpected error", e);
      return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @JsonView(UserAccount.WithoutPasswordView.class)
  @GetMapping("/get/{id}")
  public ResponseEntity<?> getUser(@PathVariable Integer id) {
    try {
      UserAccount user = userAccountService.getUserById(id);
      if (user != null) {
        logger.info("Retrieved User Account: {}", id);
        return new ResponseEntity<>(user, HttpStatus.OK);
      }
      logger.info("User Account not found: {}", id);
      return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (IllegalArgumentException e) {
      logger.error("Invalid input during retrieval of User Account: {}", id, e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      logger.error("Unexpected error", e);
      return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @JsonView(UserAccount.WithoutPasswordView.class)
  @GetMapping("/getAll")
  public ResponseEntity<List<UserAccount>> getAllUsers() {
    try {
      List<UserAccount> users = userAccountService.getAllUsers();
      return new ResponseEntity<>(users, HttpStatus.OK);
    } catch (Exception e) {
      logger.error("Unexpected error during retrieval of all users", e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
