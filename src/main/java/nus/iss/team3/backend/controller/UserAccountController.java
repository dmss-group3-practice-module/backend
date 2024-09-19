/* (C)2024 */
package nus.iss.team3.backend.controller;

import java.util.List;
import nus.iss.team3.backend.entity.UserAccount;
import nus.iss.team3.backend.service.IUserAccountService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
  public boolean addUser(@RequestBody UserAccount userAccount) {
    if (userAccountService.addUser(userAccount)) {
      logger.info("Creation of User Account: {} completed", userAccount.getUserName());
      return true;
    }
    logger.info("Creation of User Account: {} failed", userAccount.getUserName());

    return false;
  }

  @PostMapping("/update")
  public boolean updateUser(@RequestBody UserAccount userAccount) {
    if (userAccountService.updateUser(userAccount)) {
      logger.info("Update of User Account: {} completed", userAccount.getUserName());
      return true;
    }
    logger.info("Update of User Account: {} failed", userAccount.getUserName());

    return false;
  }

  @PostMapping("/delete")
  public boolean deleteUser(@RequestBody String userId) {
    if (userAccountService.deleteUserById(userId)) {
      logger.info("Deletion of User Account: {} completed", userId);
      return true;
    }
    logger.info("Deletion of User Account: {} failed", userId);

    return false;
  }

  @PostMapping("/get")
  public UserAccount getUser(@RequestBody String userId) {

    return userAccountService.getUserById(userId);
  }

  @GetMapping("/getAll")
  public List<UserAccount> getAllUser() {

    return userAccountService.getAllUser();
  }
}
