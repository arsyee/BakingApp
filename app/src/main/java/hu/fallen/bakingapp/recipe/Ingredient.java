package hu.fallen.bakingapp.recipe;

import java.util.Locale;

class Ingredient {
    private double quantity;
    private String measure;
    private String ingredient;

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "%f %s %s", quantity, measure, ingredient);
    }
}
