package nus.iss.team3.backend.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import nus.iss.team3.backend.businessService.ingredient.IIngredientBusinessService;
import nus.iss.team3.backend.domainService.user.IUserAccountService;
import nus.iss.team3.backend.service.jwt.JwtRequestFilter;
import nus.iss.team3.backend.service.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.OncePerRequestFilter;

@WebMvcTest(ExpiryController.class)
public class TestExpiryController {

  @MockBean private JwtUtil jwtUtil;
  @MockBean private IUserAccountService userAccountService;
  @InjectMocks private JwtRequestFilter jwtRequestFilter;
  @Autowired private WebApplicationContext context;

  @Autowired private MockMvc mockMvc;

  @MockBean private IIngredientBusinessService ingredientBusinessService;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.webAppContextSetup(context)
            .addFilters((OncePerRequestFilter) jwtRequestFilter)
            .build();
  }

  @Test
  public void expiryCheck_Success() throws Exception {

    mockMvc.perform(post("/expiry/check"));

    verify(ingredientBusinessService, times(1)).checkIngredientsExpiry();
  }
}
