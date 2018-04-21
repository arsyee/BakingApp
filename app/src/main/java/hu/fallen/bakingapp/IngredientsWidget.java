package hu.fallen.bakingapp;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class IngredientsWidget extends AppWidgetProvider {

    private static final String TAG = AppWidgetProvider.class.getSimpleName();
    public static final String ACTION_RECIPE_CHANGED = "android.appwidget.action.APPWIDGET_UPDATE"; // "hu.fallen.bakingapp.RECIPE_CHANGED";
    public static final String RECIPE_NAME = "recipe_name";
    public static final String RECIPE_INGREDIENTS = "recipe_ingredients";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredients_widget);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, String.format("Received event: %s", intent.getAction()));
        if (intent.getAction() != null && intent.getAction().equals(ACTION_RECIPE_CHANGED)) {
            Log.d(TAG, String.format("Received event: %s", ACTION_RECIPE_CHANGED));
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredients_widget);
            if (intent.hasExtra(RECIPE_NAME) && intent.getStringExtra(RECIPE_NAME) != null) {
                views.setTextViewText(R.id.title, intent.getStringExtra(RECIPE_NAME));
            }
            if (intent.hasExtra(RECIPE_INGREDIENTS) && intent.getStringExtra(RECIPE_INGREDIENTS) != null) {
                views.setTextViewText(R.id.content, intent.getStringExtra(RECIPE_INGREDIENTS));
            }
            AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, IngredientsWidget.class), views);
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

