package hu.fallen.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import hu.fallen.bakingapp.recipe.Step;

/**
 * An activity representing a single Recipe detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RecipeDetailsActivity}.
 */
public class RecipeStepActivity extends AppCompatActivity {

    public static final String STEPS = "steps";
    private Step prev;
    private Step next;
    private ArrayList<Step> steps;
    private Step step;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        Button prevButton = findViewById(R.id.bt_prev);
        Button nextButton = findViewById(R.id.bt_next);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            step = getIntent().getParcelableExtra(RecipeStepFragment.ARG_ITEM);
            arguments.putParcelable(RecipeStepFragment.ARG_ITEM, step);
            RecipeStepFragment fragment = new RecipeStepFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.recipe_detail_container, fragment)
                    .commit();

            steps = getIntent().getParcelableArrayListExtra(RecipeStepActivity.STEPS);
        } else {
            step = savedInstanceState.getParcelable(RecipeStepFragment.ARG_ITEM);
            steps = savedInstanceState.getParcelableArrayList(RecipeStepActivity.STEPS);
        }
        setupButtons(prevButton, nextButton);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(RecipeStepFragment.ARG_ITEM, step);
        outState.putParcelableArrayList(RecipeStepActivity.STEPS, steps);
    }

    private void setupButtons(Button prevButton, Button nextButton) {
        for (int i = 0; i < steps.size(); ++i) {
            if (steps.get(i).getId() == step.getId()) {
                if (i > 0) {
                    prev = steps.get(i - 1);
                    prevButton.setEnabled(true);
                }
                if (i < steps.size() - 1) {
                    next = steps.get(i + 1);
                    nextButton.setEnabled(true);
                }
            }
        }
    }

    public void showPrev(View view) {
        if (prev != null) {
            RecipeDetailsActivity.showStep(view.getContext(), prev, steps);
        }
    }

    public void showNext(View view) {
        if (next != null) {
            RecipeDetailsActivity.showStep(view.getContext(), next, steps);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, RecipeDetailsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
