package com.michaeljordanr.baking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.michaeljordanr.baking.R;
import com.michaeljordanr.baking.adapter.IngredientAdapter;
import com.michaeljordanr.baking.adapter.StepAdapter;
import com.michaeljordanr.baking.fragment.StepDetailFragment;
import com.michaeljordanr.baking.model.Recipe;
import com.michaeljordanr.baking.model.Step;
import com.michaeljordanr.baking.util.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailActivity extends AppCompatActivity implements
        StepAdapter.StepAdapterOnClickListener {
    @BindView(R.id.iv_poster)
    ImageView mPosterImageView;
    @BindView(R.id.rv_ingredients)
    RecyclerView mIngredientsRecyclerView;
    @BindView(R.id.rv_steps)
    RecyclerView mStepsRecyclerView;

    private Recipe mRecipe;
    private boolean mTwoPane;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_detail);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent().getExtras() != null) {
            mRecipe = getIntent().getExtras().getParcelable(Constants.RECIPE_ARG);
        }

        getSupportActionBar().setTitle(this.mRecipe.getName());

        Picasso.with(this).
                load(this.mRecipe.getImage())
                .into(this.mPosterImageView);

        if(mRecipe.getIngredients() != null){
            mIngredientsRecyclerView.setAdapter(new IngredientAdapter(mRecipe.getIngredients()));
            mIngredientsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }

        if(mRecipe.getSteps() != null){
            mStepsRecyclerView.setAdapter(new StepAdapter(mRecipe.getSteps(), this));
            mStepsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }

        if (findViewById(R.id.step_detail_container) != null) {
            mTwoPane = true;
        }

        if(mTwoPane){
            onClick(mRecipe.getSteps().get(0));
        }
    }

    @Override
    public void onClick(Step step) {
        if(mTwoPane){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.step_detail_container, StepDetailFragment.newInstance(
                            new ArrayList<>(mRecipe.getSteps()), step.getId(), mTwoPane))
                    .addToBackStack(null)
                    .commit();
        }else {
            Intent intent = new Intent(this, StepDetailActivity.class);
            intent.putExtra(Constants.STEP_LIST_ARG, new ArrayList(mRecipe.getSteps()));
            intent.putExtra(Constants.STEP_LIST_POSITION_ARG, step.getId());
            startActivity(intent);
        }
    }

}
