package hu.fallen.bakingapp.utilities;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import hu.fallen.bakingapp.R;
import hu.fallen.bakingapp.recipe.Ingredient;

public class StringUtils {
    private static final String TAG = StringUtils.class.getSimpleName();

    private static final Map<String, Integer> dictionary = new HashMap<String, Integer>() {
        {
            put("G", R.plurals.G);
            put("K", R.plurals.K);
            put("OZ", R.plurals.OZ);
            put("CUP", R.plurals.CUP);
            put("UNIT", R.plurals.UNIT);
            put("TBLSP", R.plurals.TBLSP);
            put("TSP", R.plurals.TSP);
        }
    };

    public static String getFormattedIngredients(Context context, List<Ingredient> ingredients) {
        List<String> formattedIngredients = new ArrayList<>();
        for (Ingredient ingredient : ingredients) {
            double q = ingredient.getQuantity();
            String quantityString = getQuantityString(context, ingredient.getMeasure(), q);
            formattedIngredients.add(String.format(Locale.getDefault(),
                    "%s%s%s %s",
                    q == (int) q ? String.format(Locale.getDefault(), "%d", (int) q) : q,
                    quantityString.length() == 0 ? "" : " ",
                    quantityString,
                    ingredient.getIngredient()));
        }
        return TextUtils.join("\n", formattedIngredients);
    }

    private static String getQuantityString(Context context, String measure, double q) {
        if (!dictionary.containsKey(measure.toUpperCase())) return measure;
        int iQ = q == (double) ((int) q) ? (int) q : 2; // if it is not equal, it cannot be 1, so we stick to plural
        Log.d(TAG, String.format("%f %s(%d): %d %s", q, measure, iQ, (int) q, q == (double) ((int) q)));
        return context.getResources().getQuantityString(dictionary.get(measure.toUpperCase()), iQ);
    }

}
