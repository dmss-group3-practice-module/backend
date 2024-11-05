package nus.iss.team3.backend.domainService.recipe;

import nus.iss.team3.backend.entity.Recipe;

import java.util.List;

public class RecommendByDifficulty implements RecommendStrategy  {

    @Override
    public List<Recipe> recommendRecipes(boolean isDesc) {
        return List.of();
    }
}
