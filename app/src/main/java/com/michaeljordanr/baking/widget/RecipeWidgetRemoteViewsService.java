package com.michaeljordanr.baking.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;


public class RecipeWidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new com.michaeljordanr.baking.widget.StepViewsFactory(this.getApplicationContext());
    }
}
