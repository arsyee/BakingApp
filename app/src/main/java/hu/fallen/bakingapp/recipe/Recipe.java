package hu.fallen.bakingapp.recipe;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Recipe implements Parcelable {
    private static final String TAG = Recipe.class.getSimpleName();

    private int id;
    private String name;
    private List<Ingredient> ingredients;
    private ArrayList<Step> steps;
    private int servings;
    private String image;

    private Recipe(Parcel in) {
        id = in.readInt();
        name = in.readString();

        Parcelable[] pIngredients = in.readParcelableArray(Ingredient.class.getClassLoader());
        ingredients = Arrays.asList(Arrays.copyOf(pIngredients, pIngredients.length, Ingredient[].class));

        Parcelable[] pSteps = in.readParcelableArray(Step.class.getClassLoader());
        steps = new ArrayList<>();
        for (Parcelable step : pSteps) {
            steps.add((Step) step);
        }

        servings = in.readInt();
        image = in.readString();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "Recipe %d: %s; ingredients: %s", id, name, ingredients);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeParcelableArray(ingredients.toArray(new Ingredient[0]), 0);
        dest.writeParcelableArray(steps.toArray(new Step[0]), 0);
        dest.writeInt(servings);
        dest.writeString(image);
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }
}
