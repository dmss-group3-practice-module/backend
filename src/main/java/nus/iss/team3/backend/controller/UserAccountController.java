/* (C)2024 */
package nus.iss.team3.backend.controller;

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

  @GetMapping("/")
  public String userPage() {
    return "user page enabled";
  }

  @PostMapping("/add")
  public ResponseEntity addUser(@RequestBody UserAccount userAccount) {
    if (userAccountService.addUser(userAccount)) {
      logger.info("Creation of User Account: {} completed", userAccount.getName());
      return new ResponseEntity(true, HttpStatus.OK);
    }
    logger.info("Creation of User Account: {} failed", userAccount.getName());
    return new ResponseEntity(false, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @PostMapping("/update")
  public ResponseEntity updateUser(@RequestBody UserAccount userAccount) {
    if (userAccountService.updateUser(userAccount)) {
      logger.info("Update of User Account: {} completed", userAccount.getName());
      return new ResponseEntity(true, HttpStatus.OK);
    }
    logger.info("Update of User Account: {} failed", userAccount.getName());
    return new ResponseEntity(false, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity deleteUser(@PathVariable Integer id) {
    if (userAccountService.deleteUserById(id)) {
      logger.info("Deletion of User Account: {} completed", id);
      return new ResponseEntity(true, HttpStatus.OK);
    }
    logger.info("Deletion of User Account: {} failed", id);
    return new ResponseEntity(false, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @GetMapping("/get/{id}")
  public ResponseEntity getUser(@PathVariable Integer id) {
    UserAccount user = userAccountService.getUserById(id);
    if (user != null) {
      logger.info("Retrieved User Account: {}", id);
    } else {
      logger.info("User Account not found: {}", id);
    }
    return new ResponseEntity(user, HttpStatus.OK);
  }

  @GetMapping("/getAll")
  public ResponseEntity getAllUser() {
    List<UserAccount> users = userAccountService.getAllUser();
    logger.info("Retrieved {} User Accounts", users.size());
    return new ResponseEntity(users, HttpStatus.OK);
  }
}
