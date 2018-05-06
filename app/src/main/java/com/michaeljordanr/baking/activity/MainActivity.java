package com.michaeljordanr.baking.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.michaeljordanr.baking.R;
import com.michaeljordanr.baking.adapter.RecipeAdapter;
import com.michaeljordanr.baking.model.Recipe;
import com.michaeljordanr.baking.task.RecipeTaskLoader;
import com.michaeljordanr.baking.util.Constants;
import com.michaeljordanr.baking.util.NetworkUtils;
import com.michaeljordanr.baking.util.SimpleIdlingResource;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<Recipe>>, RecipeAdapter.RecipeAdapterOnClickListener{

    private static int RECIPE_LOADER = 23;

    @BindView(R.id.rv_recipes)
    RecyclerView mRecipeListRecyclerView;
    @BindView(R.id.pb_recipes)
    ProgressBar mRecipeProgressBar;

    @Nullable
    private SimpleIdlingResource mIdlingResource;

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<List<Recipe>> recipesLoader = loaderManager.getLoader(RECIPE_LOADER);

        if(NetworkUtils.isNetworkAvailable(this)) {
            mRecipeProgressBar.setVisibility(View.VISIBLE);
            if (recipesLoader == null) {
                loaderManager.initLoader(RECIPE_LOADER, null, this).forceLoad();
            } else {
                loaderManager.restartLoader(RECIPE_LOADER, null, this).forceLoad();
            }
        }else{
            mRecipeProgressBar.setVisibility(View.INVISIBLE);
            NetworkUtils.showDialogErrorNetwork(this);
        }

        getIdlingResource();

        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(false);
        }
    }

    @Override
    public void onClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(Constants.RECIPE_ARG, recipe);
        startActivity(intent);
    }

    @Override
    public Loader<List<Recipe>> onCreateLoader(int id, Bundle args) {
        return new RecipeTaskLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<Recipe>> loader, List<Recipe> data) {
        mRecipeProgressBar.setVisibility(View.INVISIBLE);
        RecipeAdapter adapter = new RecipeAdapter(this);
        adapter.setRecipeData(data);
        mRecipeListRecyclerView.setAdapter(adapter);
        mRecipeListRecyclerView.setLayoutManager(new GridLayoutManager(this,
                getResources().getInteger(R.integer.recipe_list_cols)));

        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(true);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Recipe>> loader) { }


}
