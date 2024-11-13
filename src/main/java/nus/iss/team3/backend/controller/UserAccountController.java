/* (C)2024 */
package nus.iss.team3.backend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import java.util.List;
import nus.iss.team3.backend.domainService.user.IUserAccountService;
import nus.iss.team3.backend.domainService.user.IUserStatusService;
import nus.iss.team3.backend.entity.EUserStatus;
import nus.iss.team3.backend.entity.LoginRequest;
import nus.iss.team3.backend.entity.UserAccount;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class to handle web call for user related queries
 *
 * @author Desmond Tan Zhi Heng, REN JIARUI
 */
@RestController
@RequestMapping("user")
public class UserAccountController {

  private static final Logger logger = LogManager.getLogger(UserAccountController.class);

  @Autowired IUserAccountService userAccountService;
  @Autowired IUserStatusService userStatusService;

  @PostMapping("/add")
  public ResponseEntity<?> addUser(@RequestBody UserAccount userAccount) {
    logger.info("account info received for adding user :" + userAccount.toString());
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

  @GetMapping("/getAllUserIds")
  public ResponseEntity<List<Integer>> getAllUserIds() {
    try {
      List<Integer> usersIdList = userAccountService.getAllUserIds();
      return new ResponseEntity<>(usersIdList, HttpStatus.OK);
    } catch (Exception e) {
      logger.error("Unexpected error during retrieval of all users", e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // for microservice call from AuthWebCaller
  @PostMapping("/check")
  public ResponseEntity<?> loginCall(@RequestBody LoginRequest loginRequest) {
    try {
      UserAccount user =
          userAccountService.authenticate(loginRequest.getName(), loginRequest.getPassword());
      if (user != null) {
        // Check for BANNED status
        if (user.getStatus() == EUserStatus.BANNED) {
          logger.warn("Login attempt by banned user: {}", loginRequest.getName());
          return new ResponseEntity<>(
              "Your account has been banned. Please contact administrator.", HttpStatus.FORBIDDEN);
        }

        logger.info("User logged in successfully: {}", user.getName());
        return new ResponseEntity<>(user, HttpStatus.OK);
      } else {
        logger.warn("Login failed for user: {}", loginRequest.getName());
        return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
      }
    } catch (IllegalArgumentException e) {
      logger.warn("Login attempt with invalid input: {}", e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      logger.error("Unexpected error during login", e);
      return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/getByName/{name}")
  public ResponseEntity<UserAccount> getByName(@PathVariable String name) {
    try {
      UserAccount userAccount = userAccountService.getUserByName(name);
      return new ResponseEntity<>(userAccount, HttpStatus.OK);
    } catch (Exception e) {
      logger.error("Unexpected error during retrieval of all users", e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping("/{userId}/ban")
  public ResponseEntity<?> banUser(@PathVariable Integer userId) {
    logger.info("Received request to ban user: {}", userId);
    try {
      userStatusService.banUser(userId);
      return new ResponseEntity<>(true, HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      logger.warn("Failed to ban user {}: {}", userId, e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    } catch (IllegalStateException e) {
      logger.warn("Cannot ban admin user {}: {}", userId, e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      logger.error("Unexpected error while banning user {}", userId, e);
      return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping("/{userId}/unban")
  public ResponseEntity<?> unbanUser(@PathVariable Integer userId) {
    logger.info("Received request to unban user: {}", userId);
    try {
      userStatusService.unbanUser(userId);
      return new ResponseEntity<>(true, HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      logger.warn("Failed to unban user {}: {}", userId, e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    } catch (IllegalStateException e) {
      logger.warn("Cannot unban admin user {}: {}", userId, e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      logger.error("Unexpected error while unbanning user {}", userId, e);
      return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/status/undo")
  public ResponseEntity<Void> undoLastStatusChange() {
    userStatusService.undoLastStatusChange();
    return ResponseEntity.ok().build();
  }
}
