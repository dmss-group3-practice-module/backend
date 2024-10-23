package nus.iss.team3.backend.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import nus.iss.team3.backend.entity.EUserAccountStatus;
import nus.iss.team3.backend.entity.EUserRole;
import nus.iss.team3.backend.entity.UserAccount;
import nus.iss.team3.backend.service.user.IUserAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserAccountController.class)
public class TestUserAccountController {
  @Autowired private MockMvc mockMvc;

  @MockBean private IUserAccountService userAccountService;

  private ObjectMapper objectMapper;

  @BeforeEach
  public void setup() {
    objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Test
  public void testAddUser_Success() throws Exception {
    UserAccount userAccount = createValidUserAccount();
    when(userAccountService.addUser(any(UserAccount.class))).thenReturn(true);

    mockMvc
        .perform(
            post("/user/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userAccount)))
        .andExpect(status().isOk())
        .andExpect(content().string("true"));
  }

  @Test
  public void testAddUser_NullAccount() throws Exception {
    mockMvc
        .perform(
            post("/user/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(null)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testAddUser_InvalidName() throws Exception {
    UserAccount userAccount = createValidUserAccount();
    userAccount.setName("");
    when(userAccountService.addUser(any(UserAccount.class)))
        .thenThrow(new IllegalArgumentException("Username cannot be empty or blank"));

    mockMvc
        .perform(
            post("/user/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userAccount)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Username cannot be empty or blank"));
  }

  @Test
  public void testAddUser_InvalidPassword() throws Exception {
    UserAccount userAccount = createValidUserAccount();
    userAccount.setPassword("");
    when(userAccountService.addUser(any(UserAccount.class))).thenReturn(false);

    mockMvc
        .perform(
            post("/user/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userAccount)))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("false"));
  }

  @Test
  public void testAddUser_InvalidEmail() throws Exception {
    UserAccount userAccount = createValidUserAccount();
    userAccount.setEmail("");
    when(userAccountService.addUser(any(UserAccount.class)))
        .thenThrow(new IllegalArgumentException("Username cannot be empty or blank"));

    mockMvc
        .perform(
            post("/user/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userAccount)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Username cannot be empty or blank"));
  }

  @Test
  public void testAddUser_DuplicateEmail() throws Exception {
    UserAccount userAccount = createValidUserAccount();
    when(userAccountService.addUser(any(UserAccount.class))).thenReturn(false);

    mockMvc
        .perform(
            post("/user/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userAccount)))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("false"));
  }

  @Test
  public void testUpdateUser_Success() throws Exception {
    UserAccount userAccount = createValidUserAccount();
    userAccount.setId(1);
    when(userAccountService.updateUser(any(UserAccount.class))).thenReturn(true);

    mockMvc
        .perform(
            post("/user/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userAccount)))
        .andExpect(status().isOk())
        .andExpect(content().string("true"));
  }

  @Test
  public void testUpdateUser_NullAccount() throws Exception {
    mockMvc
        .perform(post("/user/update").contentType(MediaType.APPLICATION_JSON).content(""))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testUpdateUser_MissingId() throws Exception {
    UserAccount userAccount = createValidUserAccount();
    when(userAccountService.updateUser(any(UserAccount.class)))
        .thenThrow(new IllegalArgumentException("User ID cannot be null"));

    mockMvc
        .perform(
            post("/user/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userAccount)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("User ID cannot be null"));
  }

  @Test
  public void testUpdateUser_NonExistingAccount() throws Exception {
    UserAccount userAccount = createValidUserAccount();
    userAccount.setId(999);
    when(userAccountService.updateUser(any(UserAccount.class))).thenReturn(false);

    mockMvc
        .perform(
            post("/user/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userAccount)))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("false"));
  }

  @Test
  public void testUpdateUser_DuplicateEmail() throws Exception {
    UserAccount userAccount = createValidUserAccount();
    userAccount.setId(1);
    when(userAccountService.updateUser(any(UserAccount.class))).thenReturn(false);

    mockMvc
        .perform(
            post("/user/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userAccount)))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("false"));
  }

  @Test
  public void testDeleteUser_Success() throws Exception {
    when(userAccountService.deleteUserById(1)).thenReturn(true);

    mockMvc
        .perform(delete("/user/delete/1"))
        .andExpect(status().isOk())
        .andExpect(content().string("true"));
  }

  @Test
  public void testDeleteUser_NonExistingUser() throws Exception {
    when(userAccountService.deleteUserById(999)).thenReturn(false);

    mockMvc
        .perform(delete("/user/delete/999"))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("false"));
  }

  @Test
  public void testGetUser_Success() throws Exception {
    UserAccount userAccount = createValidUserAccount();
    userAccount.setId(1);
    when(userAccountService.getUserById(1)).thenReturn(userAccount);

    mockMvc
        .perform(get("/user/get/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Test User"));
  }

  @Test
  public void testGetUser_NotFound() throws Exception {
    when(userAccountService.getUserById(999)).thenReturn(null);

    mockMvc
        .perform(get("/user/get/999"))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("false"));
  }

  @Test
  public void testGetAllUsers() throws Exception {
    List<UserAccount> userList = Arrays.asList(createValidUserAccount(), createValidUserAccount());
    userList.get(0).setId(1);
    userList.get(1).setId(2);

    when(userAccountService.getAllUsers()).thenReturn(userList);

    mockMvc
        .perform(get("/user/getAll"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[1].id").value(2))
        .andExpect(jsonPath("$[0].name").value("Test User"))
        .andExpect(jsonPath("$[1].name").value("Test User"));
  }

  private UserAccount createValidUserAccount() {
    UserAccount userAccount = new UserAccount();
    userAccount.setName("Test User");
    userAccount.setPassword("password");
    userAccount.setDisplayName("Test Display Name");
    userAccount.setEmail("test@example.com");
    userAccount.setStatus(EUserAccountStatus.ACTIVE);
    userAccount.setRole(EUserRole.USER);
    return userAccount;
  }
}
