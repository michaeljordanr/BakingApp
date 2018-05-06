package com.michaeljordanr.baking.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.michaeljordanr.baking.R;
import com.michaeljordanr.baking.model.Ingredient;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder>{
    private List<Ingredient> mListIngredients;

    public IngredientAdapter(List<Ingredient> ingredients){
        mListIngredients = ingredients;
    }

    @Override
    public IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int idLayourForListItem = R.layout.ingredients_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(idLayourForListItem, parent, false);
        return new IngredientViewHolder(view);
    }


    @Override
    public void onBindViewHolder(IngredientViewHolder holder, int position) {
        Ingredient ingredient = mListIngredients.get(position);

        String ingredientText = ingredient.getQuantity() + " " +
                ingredient.getMeasure() + " " + ingredient.getIngredient();

        holder.mIngredientTextView.setText(ingredientText);
    }

    @Override
    public int getItemCount() {
        if(mListIngredients == null) return 0;
        return mListIngredients.size();
    }

    class IngredientViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_ingredient)
        TextView mIngredientTextView;

        IngredientViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
