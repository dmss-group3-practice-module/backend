package nus.iss.team3.backend.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import nus.iss.team3.backend.businessService.ingredient.IIngredientBusinessService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ExpiryController.class)
public class TestExpiryController {

  @Autowired private MockMvc mockMvc;

  @MockBean private IIngredientBusinessService ingredientBusinessService;

  @Test
  public void expiryCheck_Success() throws Exception {

    mockMvc.perform(post("/expiry/check"));

    verify(ingredientBusinessService, times(1)).checkIngredientsExpiry();
  }
}
