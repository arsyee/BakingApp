package hu.fallen.bakingapp.recipe;

import java.util.List;
import java.util.Locale;

public class Recipe {
    private int id;
    private String name;
    private List<Ingredient> ingredients;
    private List<Step> steps;
    private int servings;
    private String image;

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "Recipe %d: %s; ingredients: %s", id, name, ingredients);
    }
}
