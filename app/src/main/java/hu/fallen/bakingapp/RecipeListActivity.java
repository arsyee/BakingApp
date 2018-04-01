package hu.fallen.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import hu.fallen.bakingapp.recipe.Recipe;

public class RecipeListActivity extends AppCompatActivity {

    private static String TAG = RecipeListActivity.class.getSimpleName();

    private RequestQueue requestQueue;
    private Gson gson;
    private SimpleItemRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        View recyclerView = findViewById(R.id.recipe_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        gson = new GsonBuilder().create();

        requestQueue = Volley.newRequestQueue(this);
        fetchRecipes();
    }

    private void fetchRecipes() {
        String recipesUrl = getString(R.string.recipes_url);
        StringRequest request = new StringRequest(Request.Method.GET,
                recipesUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseRecipesAndUpdate(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, String.format("VolleyError: %s", error));
                    }
                });
        requestQueue.add(request);
    }

    private void parseRecipesAndUpdate(String response) {
        List<Recipe> recipes = Arrays.asList(gson.fromJson(response, Recipe[].class));
        for (Recipe recipe : recipes) {
            Log.d(TAG, String.format("Recipe found: %s", recipe));
            if (getResources().getBoolean(R.bool.testing)) { // Add images to recipes for testing
                switch (recipe.getName()) {
                    case "Nutella Pie":
                        recipe.setImage("blah, blah"); // not even a url
                        break;
                    case "Yellow Cake":
                        recipe.setImage("https://en.wikipedia.org/wiki/Yellowcake"); // url, but not a pic
                        break;
                    case "Brownies":
                        recipe.setImage("https://i1.wp.com/www.briana-thomas.com/wp-content/uploads/2018/01/Cream-Cheese-Chocolate-Chip-Brownie-Cake.jpg?w=2000&ssl=1");
                        break;
                    default:
                        // keep original
                }
            }
        }
        mAdapter.setValues(recipes);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        mAdapter = new SimpleItemRecyclerViewAdapter(this, null);
        recyclerView.setAdapter(mAdapter);
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final RecipeListActivity mParentActivity;
        private List<Recipe> mValues;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Recipe item = (Recipe) view.getTag();
                Context context = view.getContext();
                Intent intent = new Intent(context, RecipeDetailsActivity.class);
                intent.putExtra(RecipeDetailsActivity.ARG_ITEM, item);
                context.startActivity(intent);
            }
        };

        SimpleItemRecyclerViewAdapter(RecipeListActivity parent,
                                      List<Recipe> items) {
            mValues = items;
            mParentActivity = parent;
        }

        void setValues(List<Recipe> recipes) {
            mValues = recipes;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recipe_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            position = position % mValues.size();
            Recipe recipe = mValues.get(position);
            holder.mIdView.setText(String.format(Locale.getDefault(), "%d", recipe.getId()));
            holder.mContentView.setText(recipe.getName());
            String contentDescription = mParentActivity.getString(R.string.recipe_image_description, recipe.getName());
            Log.d(TAG, String.format("ContentDescription set to: %s", contentDescription));
            holder.mImageView.setContentDescription(contentDescription);
            try {
                Picasso.get().load(recipe.getImage()).resize(75, 75).centerCrop().into(holder.mImageView);
            } catch (Exception e) {
                Log.d(TAG, String.format("Image not found: %s", e.getMessage()));
            }

            holder.itemView.setTag(recipe);
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues == null ? 0 : mValues.size() * (mParentActivity.getResources().getBoolean(R.bool.testing) ? 10 : 1);
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;
            final ImageView mImageView;

            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.content);
                mImageView = (ImageView) view.findViewById(R.id.image);
            }
        }
    }
}
