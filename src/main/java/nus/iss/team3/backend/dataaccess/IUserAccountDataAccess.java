package nus.iss.team3.backend.dataaccess;

import nus.iss.team3.backend.entity.UserAccount;

import java.util.List;

/**
 * Interface class for UserAccountDataAccess, should contains all functionality needed for useraccount.
 *
 * @author Desmond Tan Zhi Heng
 */
public interface IUserAccountDataAccess {

    public boolean addUser(UserAccount userAccount);

    public boolean updateUser(UserAccount userAccount);

    public boolean deleteUser(String userName);

    public UserAccount getUser(String userName);

    public List<UserAccount> getAllUsers();
}
