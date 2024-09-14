package nus.iss.team3.backend.service;

import nus.iss.team3.backend.dataaccess.IUserAccountDataAccess;
import nus.iss.team3.backend.entity.UserAccount;
import nus.iss.team3.backend.util.StringUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class with logic for handling user account related queries
 *
 * @author Desmond Tan Zhi Heng
 */
@Service
public class UserAccountService implements IUserAccountService {

    private static final Logger logger = LogManager.getLogger(UserAccountService.class);

    @Autowired
    IUserAccountDataAccess userAccountDataAccess;

    @Override
    public boolean addUser(UserAccount userAccount) {
        logger.info("userAccount is  {}", userAccount);
        if(!validateUserAccount(userAccount)) {
            logger.info("addUser failed, due to validation failed for account {}", (userAccount == null ? "null object" : userAccount.getUserName()));
            return false;
        }
        if(userAccountDataAccess.getUser(userAccount.getUserName())!= null){
            logger.info("addUser failed, due to existing account for {}",userAccount.getUserName());
            return false;
        }

        return userAccountDataAccess.addUser(userAccount);

    }
    @Override
    public boolean updateUser(UserAccount userAccount) {
        if(!validateUserAccount(userAccount)) {
            logger.info("updateUser failed, due to validation failed for account {}", (userAccount == null ? "null object" : userAccount.getUserName()));
            return false;
        }
        if(userAccountDataAccess.getUser(userAccount.getUserName())== null){
            logger.info("updateUser failed, due to existing account for {}",userAccount.getUserName());
            return false;
        }
        return userAccountDataAccess.updateUser(userAccount);
    }

    @Override
    public boolean deleteUser(String userName) {
        if(userName==null|| userName.isBlank()) {
            logger.info("deleteUser failed, due to empty value");
            return false;
        }
        if(userAccountDataAccess.getUser(userName)== null){
            logger.info("updateUser failed, due to missing account for {}",userName);
            return false;
        }
        return userAccountDataAccess.deleteUser(userName);
    }

    @Override
    public UserAccount getUser(String userName) {
        logger.info("looking for {}", userName);
        if(userName==null|| userName.isBlank()) {
            logger.info("getUser failed, due to empty value");
            return null;
        }
        return userAccountDataAccess.getUser(userName);
    }

    @Override
    public List<UserAccount> getAllUser() {
        return userAccountDataAccess.getAllUsers();
    }


    /**
     * Check whether the input useraccount contians value for it to be accepted
     * @return whether the string is null or blank.
     */
    private boolean validateUserAccount(UserAccount userAccount){
        return userAccount != null
                && !StringUtilities.isStringNullOrBlank(userAccount.getUserName())
                && !StringUtilities.isStringNullOrBlank(userAccount.getPassword())
                && !StringUtilities.isStringNullOrBlank(userAccount.getEmail());
    }
}
