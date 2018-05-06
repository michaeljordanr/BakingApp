package com.michaeljordanr.baking.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.michaeljordanr.baking.R;
import com.michaeljordanr.baking.model.Step;
import com.michaeljordanr.baking.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rafae on 25/09/2017.
 */

public class StepViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private List<Step> mStepList;

    public StepViewsFactory(Context applicationContext) {
        mContext = applicationContext;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        mStepList = RecipeWidgetProvider.getStepsFromRecipe();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mStepList == null ? 0 : mStepList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Step step = mStepList.get(position);

        RemoteViews row = new RemoteViews(mContext.getPackageName(), R.layout.item_widget_step_list);

        row.setTextViewText(R.id.tv_id, String.valueOf(step.getId()));
        row.setTextViewText(R.id.tv_short_description, step.getShortDescription());

        Intent fillInIntent = new Intent();
        fillInIntent.putParcelableArrayListExtra(Constants.STEP_LIST_ARG, new ArrayList<Parcelable>(mStepList));
        fillInIntent.putExtra(Constants.STEP_LIST_POSITION_ARG, position);
        row.setOnClickFillInIntent(R.id.ll_container, fillInIntent);

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
