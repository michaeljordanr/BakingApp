package com.michaeljordanr.baking.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.michaeljordanr.baking.model.Recipe;
import com.michaeljordanr.baking.util.Constants;
import com.michaeljordanr.baking.util.NetworkUtils;

import java.lang.reflect.Type;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 *
 * helper methods.
 */
public class RecipeIntentService extends IntentService {

    public static final String ACTION_FETCH_RECIPES = "com.michaeljordanr.baking.widget.action.FETCH_RECIPES";

    public RecipeIntentService() {
        super("RecipeIntentService");
    }

    public static void startActionFetchRecipes(Context context) {
        Intent intent = new Intent(context, RecipeIntentService.class);
        intent.setAction(ACTION_FETCH_RECIPES);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FETCH_RECIPES.equals(action)) {
                new AsyncClass(this).execute();
            }
        }
    }


    class AsyncClass extends AsyncTask<Void, Void, List<Recipe>> {
        Context mContext;

        public AsyncClass(Context context){
            mContext = context;
        }

        @Override
        protected List<Recipe> doInBackground(Void... voids) {
            URL recipesUrl;
            String jsonResponse;
            List<Recipe> mRecipesList = new ArrayList<>();

            boolean isNetworkAvailable = NetworkUtils.isNetworkAvailable(mContext);

            if(isNetworkAvailable) {
                try {
                    recipesUrl = NetworkUtils.buildUrlRecipes();
                    jsonResponse = NetworkUtils.run(recipesUrl);

                    Type listType = new TypeToken<ArrayList<Recipe>>(){}.getType();
                    mRecipesList = new Gson().fromJson(jsonResponse, listType);

                    mRecipesList.get(0).setImage(Constants.NUTELLA_PIE_IMG_URL);
                    mRecipesList.get(1).setImage(Constants.BROWNIES_IMG_URL);
                    mRecipesList.get(2).setImage(Constants.YELLOW_CAKE_IMG_URL);
                    mRecipesList.get(3).setImage(Constants.CHEESECAKE_IMG_URL);

                } catch (UnknownHostException e) {
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return mRecipesList;
        }

        @Override
        protected void onPostExecute(List<Recipe> recipes) {
            com.michaeljordanr.baking.widget.RecipeWidgetProvider.setRecipeList(recipes);

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
            int[] appWidgetsId = appWidgetManager.getAppWidgetIds(new ComponentName(mContext, com.michaeljordanr.baking.widget.RecipeWidgetProvider.class));
            com.michaeljordanr.baking.widget.RecipeWidgetProvider.updateAppWidgets(mContext, appWidgetManager, appWidgetsId, 0);

        }
    }
}
