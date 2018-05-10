package com.michaeljordanr.baking.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.michaeljordanr.baking.R;
import com.michaeljordanr.baking.model.Ingredient;
import com.michaeljordanr.baking.model.Recipe;
import com.michaeljordanr.baking.model.Step;
import com.michaeljordanr.baking.util.Constants;

import java.util.ArrayList;
import java.util.List;


public class StepViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private List<Ingredient> mIngredientList;

    public StepViewsFactory(Context applicationContext) {
        mContext = applicationContext;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        mIngredientList = RecipeWidgetProvider.getRecipeList();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mIngredientList == null ? 0 : mIngredientList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Ingredient ingredient = mIngredientList.get(position);

        RemoteViews row = new RemoteViews(mContext.getPackageName(), R.layout.item_widget_step_list);

        row.setTextViewText(R.id.tv_short_description,
                ingredient.getQuantity() + " " +
                        ingredient.getMeasure() + " " +
                        ingredient.getIngredient());

        return (row);
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
