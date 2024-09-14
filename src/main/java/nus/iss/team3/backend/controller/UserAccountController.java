package nus.iss.team3.backend.controller;
import nus.iss.team3.backend.entity.UserAccount;
import nus.iss.team3.backend.service.IUserAccountService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Controller class to handle web call for user related queries
 *
 * @author Desmond Tan Zhi Heng
 */
@RestController
@RequestMapping("user")
public class UserAccountController {

    private static final Logger logger = LogManager.getLogger(UserAccountController.class);

    @Autowired
    IUserAccountService userAccountService;

    @GetMapping("/")
    public String userPage(){
        return "user page enabled";
    }

    @PostMapping("/add")
    public boolean addUser(@RequestBody UserAccount userAccount){
        if(userAccountService.addUser(userAccount)){
            logger.info("Creation of User Account: {} completed",userAccount.getUserName());
            return true;
        }
        logger.info("Creation of User Account: {} failed",userAccount.getUserName());

        return false;
    }

    @PostMapping("/update")
    public boolean updateUser(@RequestBody UserAccount userAccount){
        if(userAccountService.updateUser(userAccount)){
            logger.info("Update of User Account: {} completed",userAccount.getUserName());
            return true;
        }
        logger.info("Update of User Account: {} failed",userAccount.getUserName());

        return false;
    }

    @PostMapping("/delete")
    public boolean updateUser(@RequestBody String userName){
        if(userAccountService.deleteUser(userName)){
            logger.info("Deletion of User Account: {} completed",userName);
            return true;
        }
        logger.info("Deletion of User Account: {} failed",userName);

        return false;
    }


    @PostMapping("/get")
    public UserAccount getUser(@RequestBody String userName){

        return userAccountService.getUser(userName);

    }

    @GetMapping("/getAll")
    public List<UserAccount> getAllUser(){

        return userAccountService.getAllUser();

    }
}
