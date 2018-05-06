package com.michaeljordanr.baking.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.michaeljordanr.baking.R;
import com.michaeljordanr.baking.model.Recipe;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private List<Recipe> mListRecipes;
    private final RecipeAdapterOnClickListener mCallback;

    public interface RecipeAdapterOnClickListener{
        void onClick(Recipe recipe);
    }

    public RecipeAdapter(RecipeAdapterOnClickListener callback){
        mCallback = callback;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int idLayoutForListItem = R.layout.item_recipe;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(idLayoutForListItem, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        Recipe recipe = mListRecipes.get(position);

        holder.titleRecipeTextView.setText(recipe.getName());

        Picasso.with(holder.backgroundImageView.getContext())
                .load(recipe.getImage())
                .into(holder.backgroundImageView);
    }

    @Override
    public int getItemCount() {
        if(mListRecipes == null) return 0;
        return mListRecipes.size();
    }

    public void setRecipeData(List<Recipe> recipes){
        mListRecipes = recipes;
        notifyDataSetChanged();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.iv_recipe)
        ImageView backgroundImageView;
        @BindView(R.id.tv_recipe_name)
        TextView titleRecipeTextView;


        RecipeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Recipe recipe = mListRecipes.get(position);
            mCallback.onClick(recipe);
        }
    }
}
