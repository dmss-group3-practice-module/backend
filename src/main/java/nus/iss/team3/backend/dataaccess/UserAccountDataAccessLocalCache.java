package nus.iss.team3.backend.dataaccess;

import nus.iss.team3.backend.entity.UserAccount;
import nus.iss.team3.backend.service.UserAccountService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO for UserAccount
 *
 * @author Desmond Tan
 */
@Repository
public class UserAccountDataAccessLocalCache implements  IUserAccountDataAccess{

    private static final Logger logger = LogManager.getLogger(UserAccountDataAccessLocalCache.class);

    Map<String,UserAccount> accountMap = new HashMap<>();
    /**
     * @param userAccount : user to be added to the database
     * @return whether the user was added?
     */
    @Override
    public boolean addUser(UserAccount userAccount) {
        if(userAccount == null || userAccount.getUserName()==null || userAccount.getUserName().isEmpty()){
            return false;
        }
        if(accountMap.containsKey(userAccount.getUserName())){
            return false;
        }
        accountMap.put(userAccount.getUserName(),userAccount);
        return true;
    }

    /**
     * @param userAccount: user to be updated to the database
     * @return whether the user was updated?
     */
    @Override
    public boolean updateUser(UserAccount userAccount) {
        if(userAccount == null || userAccount.getUserName()==null || userAccount.getUserName().isEmpty()){
            return false;
        }
        if(!accountMap.containsKey(userAccount.getUserName())){
            return false;
        }
        accountMap.put(userAccount.getUserName(),userAccount);
        return true;
    }

    /**
     * @param userName: username to be deleted from the database
     * @return whether the user was deleted?
     */
    @Override
    public boolean deleteUser(String userName) {
        if(userName == null|| userName.isEmpty()){
            return false;
        }
        if(!accountMap.containsKey(userName)){
            return false;
        }
        accountMap.remove(userName);
        return true;
    }

    /**
     * @param userName: username to be deleted from the database
     * @return whether the user was deleted?
     */
    @Override
    public UserAccount getUser(String userName) {
        if(userName == null|| userName.isEmpty()){
            return null;
        }
        return accountMap.getOrDefault(userName,null);
    }

    /**
     * @return the list of user account details.
     */
    @Override
    public List<UserAccount> getAllUsers() {
        return accountMap.values().stream().toList();
    }
}
