package hu.fallen.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import hu.fallen.bakingapp.recipe.Recipe;
import hu.fallen.bakingapp.utilities.StringUtils;

/**
 * Implementation of App Widget functionality.
 */
public class IngredientsWidget extends AppWidgetProvider {

    private static final String TAG = AppWidgetProvider.class.getSimpleName();
    public static final String ACTION_RECIPE_CHANGED = "android.appwidget.action.APPWIDGET_UPDATE"; // "hu.fallen.bakingapp.RECIPE_CHANGED";
    public static final String RECIPE_NAME = "recipe_name";
    public static final String RECIPE_INGREDIENTS = "recipe_ingredients";

    private Recipe mRecipe = null;

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                        int appWidgetId, Recipe recipe) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredients_widget);

        final Intent intent;
        if (recipe != null) {
            views.setTextViewText(R.id.title, recipe.getName());
            views.setTextViewText(R.id.content, StringUtils.getFormattedIngredients(context, recipe.getIngredients()));

            intent = new Intent(context, RecipeDetailsActivity.class);
            intent.putExtra(RecipeDetailsActivity.ARG_ITEM, recipe);
            Log.d(TAG, String.format("reopening recipe: %s", recipe.getName()));
        } else {
            intent = new Intent(context, RecipeListActivity.class);
            Log.d(TAG, "opening recipe list");
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.ingredients_widget, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, mRecipe);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, String.format("Received event: %s", intent.getAction()));
        if (intent.getAction() != null && intent.getAction().equals(ACTION_RECIPE_CHANGED)) {
            Log.d(TAG, String.format("Received event: %s", ACTION_RECIPE_CHANGED));
            if (intent.hasExtra(RecipeDetailsActivity.ARG_ITEM) && intent.getParcelableExtra(RecipeDetailsActivity.ARG_ITEM) != null) {
                mRecipe = intent.getParcelableExtra(RecipeDetailsActivity.ARG_ITEM);
                Log.d(TAG, String.format("setting mRecipe: %s", mRecipe.getName()));
            }
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            onUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(new ComponentName(context, IngredientsWidget.class)));
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

