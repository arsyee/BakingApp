package hu.fallen.bakingapp.recipe;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;

class Ingredient implements Parcelable {
    private double quantity;
    private String measure;
    private String ingredient;

    protected Ingredient(Parcel in) {
        quantity = in.readDouble();
        measure = in.readString();
        ingredient = in.readString();
    }

    public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "%f %s %s", quantity, measure, ingredient);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(quantity);
        dest.writeString(measure);
        dest.writeString(ingredient);
    }
}
