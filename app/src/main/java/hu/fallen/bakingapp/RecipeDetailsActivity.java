package hu.fallen.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hu.fallen.bakingapp.recipe.Ingredient;
import hu.fallen.bakingapp.recipe.Recipe;
import hu.fallen.bakingapp.recipe.Step;

import java.util.List;
import java.util.Locale;

/**
 * An activity representing a list of Recipes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RecipeStepActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RecipeDetailsActivity extends AppCompatActivity {

    public static final String ARG_ITEM = "item";
    private String TAG = RecipeDetailsActivity.class.getSimpleName();

    private boolean mTwoPane;

    private SimpleItemRecyclerViewAdapter mAdapter;
    private Recipe mRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (findViewById(R.id.recipe_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        Intent incomingIntent = getIntent();
        if (incomingIntent.hasExtra(ARG_ITEM)) {
            mRecipe = incomingIntent.getParcelableExtra(ARG_ITEM);
        }

        setTitle(mRecipe.getName());

        View recyclerView = findViewById(R.id.recipe_list);
        assert recyclerView != null;
        if (mRecipe != null) {
            setupRecyclerView((RecyclerView) recyclerView, mRecipe.getSteps(), mRecipe.getIngredients());
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<Step> steps, List<Ingredient> ingredients) {
        mAdapter = new SimpleItemRecyclerViewAdapter(this, steps, ingredients, mTwoPane);
        recyclerView.setAdapter(mAdapter);
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int VIEW_TYPE_INGREDIENTS = 0;
        private static final int VIEW_TYPE_STEP = 1;
        private final RecipeDetailsActivity mParentActivity;
        private List<Step> mValues;
        private List<Ingredient> mIngredients;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Step item = (Step) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(RecipeStepFragment.ARG_ITEM, item);
                    RecipeStepFragment fragment = new RecipeStepFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recipe_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, RecipeStepActivity.class);
                    intent.putExtra(RecipeStepFragment.ARG_ITEM, item);

                    context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(RecipeDetailsActivity parent,
                                      List<Step> items,
                                      List<Ingredient> ingredients,
                                      boolean twoPane) {
            mValues = items;
            mIngredients = ingredients;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case VIEW_TYPE_INGREDIENTS:
                    return new IngredientsViewHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.ingredients_content, parent, false));
                case VIEW_TYPE_STEP:
                    return new StepViewHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.step_list_content, parent, false));
                default:
                    throw new UnsupportedOperationException(String.format("Unexpected viewtype: %d", viewType));
            }
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder rvHolder, int position) {
            switch (getItemViewType(position)) {
                case VIEW_TYPE_INGREDIENTS:
                    IngredientsViewHolder iHolder = (IngredientsViewHolder) rvHolder;
                    iHolder.mContent.setText(TextUtils.join("\n", mIngredients));
                    break;
                case VIEW_TYPE_STEP:
                    StepViewHolder sHolder = (StepViewHolder) rvHolder;
                    Step step = mValues.get(position-1);
                    sHolder.mIdView.setText(String.format(Locale.getDefault(), "%d", step.getId()));
                    sHolder.mContentView.setText(step.getShortDescription());

                    sHolder.itemView.setTag(step);
                    sHolder.itemView.setOnClickListener(mOnClickListener);
                    break;
                default:
                    throw new UnsupportedOperationException(String.format("Unexpected viewtype: %d", getItemViewType(position)));
            }
        }

        @Override
        public int getItemCount() {
            return mValues == null ? 0 : mValues.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) return VIEW_TYPE_INGREDIENTS;
            return VIEW_TYPE_STEP;
        }

        class IngredientsViewHolder extends RecyclerView.ViewHolder {
            final TextView mContent;
            IngredientsViewHolder(View view) {
                super(view);
                mContent = (TextView) view.findViewById(R.id.content);
            }
        }

        class StepViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;

            StepViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.content);
            }
        }
    }
}
