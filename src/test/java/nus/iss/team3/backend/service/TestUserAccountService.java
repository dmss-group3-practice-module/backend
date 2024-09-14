package nus.iss.team3.backend.service;


import nus.iss.team3.backend.dataaccess.IUserAccountDataAccess;
import nus.iss.team3.backend.entity.UserAccount;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class TestUserAccountService {

    @InjectMocks
    private UserAccountService userAccountService;

    @Mock
    private IUserAccountDataAccess userAccountDataAccess;

    @Test
    public void addUser_nullAccount(){
        {
            UserAccount inputUserAccount = null;
            UserAccount dbUserAccount =null;
            boolean addUserResult =false;

            when(userAccountDataAccess.getUser(anyString())).thenReturn(dbUserAccount);
            when(userAccountDataAccess.addUser(any())).thenReturn(addUserResult);

            assertEquals(addUserResult,userAccountService.addUser(inputUserAccount));
        }
    }
    @Test
    public void addUser_validAccount_nullUsername(){
        {
            UserAccount inputUserAccount = new UserAccount();
            inputUserAccount.setUserName(null);
            UserAccount dbUserAccount =null;
            boolean addUserResult =false;

            when(userAccountDataAccess.getUser(anyString())).thenReturn(dbUserAccount);
            when(userAccountDataAccess.addUser(any())).thenReturn(addUserResult);

            assertEquals(addUserResult,userAccountService.addUser(inputUserAccount));
        }
    }
    @Test
    public void addUser_validAccount_emptyUsername(){
        {
            UserAccount inputUserAccount = new UserAccount();
            inputUserAccount.setUserName("");
            UserAccount dbUserAccount =null;
            boolean addUserResult =false;

            when(userAccountDataAccess.getUser(anyString())).thenReturn(dbUserAccount);
            when(userAccountDataAccess.addUser(any())).thenReturn(addUserResult);

            assertEquals(addUserResult,userAccountService.addUser(inputUserAccount));
        }
    }
    @Test
    public void addUser_validAccount_validUsername_nullPassword(){
        {
            UserAccount inputUserAccount = new UserAccount();
            inputUserAccount.setUserName("valid");
            inputUserAccount.setPassword(null);
            UserAccount dbUserAccount =null;
            boolean addUserResult =false;

            when(userAccountDataAccess.getUser(anyString())).thenReturn(dbUserAccount);
            when(userAccountDataAccess.addUser(any())).thenReturn(addUserResult);

            assertEquals(addUserResult,userAccountService.addUser(inputUserAccount));
        }
    }
    @Test
    public void addUser_validAccount_validUsername_emptyPassword(){
        {
            UserAccount inputUserAccount = new UserAccount();
            inputUserAccount.setUserName("valid");
            inputUserAccount.setPassword("");
            UserAccount dbUserAccount =null;
            boolean addUserResult =false;

            when(userAccountDataAccess.getUser(anyString())).thenReturn(dbUserAccount);
            when(userAccountDataAccess.addUser(any())).thenReturn(addUserResult);

            assertEquals(addUserResult,userAccountService.addUser(inputUserAccount));
        }
    }
    @Test
    public void addUser_validAccount_validUsername_validPassword_nullEmail(){
        {
            UserAccount inputUserAccount = new UserAccount();
            inputUserAccount.setUserName("valid");
            inputUserAccount.setPassword("valid");
            inputUserAccount.setEmail(null);
            UserAccount dbUserAccount =null;
            boolean addUserResult =false;

            when(userAccountDataAccess.getUser(anyString())).thenReturn(dbUserAccount);
            when(userAccountDataAccess.addUser(any())).thenReturn(addUserResult);

            assertEquals(addUserResult,userAccountService.addUser(inputUserAccount));
        }
    }
    @Test
    public void addUser_validAccount_validUsername_validPassword_emptyEmail(){
        {
            UserAccount inputUserAccount = new UserAccount();
            inputUserAccount.setUserName("valid");
            inputUserAccount.setPassword("valid");
            inputUserAccount.setEmail("");
            UserAccount dbUserAccount =null;
            boolean addUserResult =false;

            when(userAccountDataAccess.getUser(anyString())).thenReturn(dbUserAccount);
            when(userAccountDataAccess.addUser(any())).thenReturn(addUserResult);

            assertEquals(addUserResult,userAccountService.addUser(inputUserAccount));
        }
    }
    @Test
    public void addUser_validAccount_validUsername_validPassword_validEmail_nullAccount(){
        {
            UserAccount inputUserAccount = new UserAccount();
            inputUserAccount.setUserName("valid");
            inputUserAccount.setPassword("valid");
            inputUserAccount.setEmail("valid");
            UserAccount dbUserAccount =null;
            boolean addUserResult =true;

            when(userAccountDataAccess.getUser(anyString())).thenReturn(dbUserAccount);
            when(userAccountDataAccess.addUser(any())).thenReturn(addUserResult);

            assertEquals(addUserResult,userAccountService.addUser(inputUserAccount));
        }
    }    @Test
    public void addUser_validAccount_validUsername_validPassword_validEmail_validAccount(){
        {
            UserAccount inputUserAccount = new UserAccount();
            inputUserAccount.setUserName("valid");
            inputUserAccount.setPassword("valid");
            inputUserAccount.setEmail("valid");
            UserAccount dbUserAccount =new UserAccount();
            boolean addUserResult =false;

            when(userAccountDataAccess.getUser(anyString())).thenReturn(dbUserAccount);
            when(userAccountDataAccess.addUser(any())).thenReturn(addUserResult);

            assertEquals(addUserResult,userAccountService.addUser(inputUserAccount));
        }
    }
}
