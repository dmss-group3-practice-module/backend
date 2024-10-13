package nus.iss.team3.backend.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import nus.iss.team3.backend.entity.UserAccount;
import nus.iss.team3.backend.service.IAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
public class TestAuthController {

  @Autowired private MockMvc mockMvc;

  @MockBean private IAuthService authService;

  private ObjectMapper objectMapper;

  @BeforeEach
  public void setup() {
    objectMapper = new ObjectMapper();
  }

  @Test
  public void testLogin_Success() throws Exception {
    UserAccount user = new UserAccount();
    user.setName("testuser");
    user.setPassword(null); // Password should be null in the returned object

    when(authService.authenticate(eq("testuser"), eq("password"))).thenReturn(user);

    MockHttpSession session = new MockHttpSession();

    mockMvc
        .perform(
            post("/user/login")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"testuser\",\"password\":\"password\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("testuser"))
        .andExpect(jsonPath("$.password").doesNotExist());
  }

  @Test
  public void testLogin_InvalidCredentials() throws Exception {
    when(authService.authenticate(anyString(), anyString())).thenReturn(null);

    MockHttpSession session = new MockHttpSession();

    mockMvc
        .perform(
            post("/user/login")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"testuser\",\"password\":\"wrongpassword\"}"))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string("Invalid username or password"));
  }

  @Test
  public void testLogin_NullUsername() throws Exception {
    MockHttpSession session = new MockHttpSession();

    mockMvc
        .perform(
            post("/user/login")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":null,\"password\":\"password\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Username and password cannot be empty"));
  }

  @Test
  public void testLogin_EmptyPassword() throws Exception {
    MockHttpSession session = new MockHttpSession();

    mockMvc
        .perform(
            post("/user/login")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"testuser\",\"password\":\"\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Username and password cannot be empty"));
  }

  @Test
  public void testLogin_MissingRequestBody() throws Exception {
    MockHttpSession session = new MockHttpSession();

    mockMvc
        .perform(post("/user/login").session(session).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testLogin_ServiceException() throws Exception {
    when(authService.authenticate(anyString(), anyString()))
        .thenThrow(new RuntimeException("Service error"));

    MockHttpSession session = new MockHttpSession();

    mockMvc
        .perform(
            post("/user/login")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"testuser\",\"password\":\"password\"}"))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("An unexpected error occurred"));
  }

  @Test
  public void testLogout_Success() throws Exception {
    UserAccount user = new UserAccount();
    user.setName("testuser");

    MockHttpSession session = new MockHttpSession();
    session.setAttribute("user", user);

    mockMvc
        .perform(post("/user/logout").session(session))
        .andExpect(status().isOk())
        .andExpect(content().string("Logged out successfully"));
  }

  @Test
  public void testLogout_NoActiveSession() throws Exception {
    MockHttpSession session = new MockHttpSession();

    mockMvc
        .perform(post("/user/logout").session(session))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("No active session"));
  }

  @Test
  public void testLogout_NullUserInSession() throws Exception {
    MockHttpSession session = new MockHttpSession();
    session.setAttribute("user", null);

    mockMvc
        .perform(post("/user/logout").session(session))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("No active session"));
  }

  @Test
  public void testLogout_SessionInvalidationError() throws Exception {
    UserAccount user = new UserAccount();
    user.setName("testuser");

    MockHttpSession session =
        new MockHttpSession() {
          @Override
          public void invalidate() {
            throw new IllegalStateException("Session invalidation error");
          }
        };
    session.setAttribute("user", user);

    mockMvc
        .perform(post("/user/logout").session(session))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("An unexpected error occurred"));
  }
}
