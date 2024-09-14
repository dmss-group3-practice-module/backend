package nus.iss.team3.backend.service;

import nus.iss.team3.backend.entity.UserAccount;

import java.util.List;

/**
 * Interface for userAccountService, contains logic involving userAccounts.
 *
 * @author Desmond Tan
 */
public interface IUserAccountService {

    public boolean addUser(UserAccount userAccount);

    public boolean updateUser(UserAccount userAccount);

    public boolean deleteUser(String userName);

    public UserAccount getUser(String userName);

    public List<UserAccount> getAllUser();



}
