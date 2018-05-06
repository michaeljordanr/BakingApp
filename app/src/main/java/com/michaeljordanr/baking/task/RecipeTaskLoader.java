package com.michaeljordanr.baking.task;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
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

public class RecipeTaskLoader extends AsyncTaskLoader<List<Recipe>> {

    @SuppressLint("StaticFieldLeak")
    private final Context mContext;

    public RecipeTaskLoader(Context context) {
        super(context);
        mContext = context;
    }

    private List<Recipe> mRecipesList = new ArrayList<>();

    @Override
    public List<Recipe> loadInBackground() {
        URL recipesUrl;
        String jsonResponse;

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
    public void deliverResult(List<Recipe> data) {
        mRecipesList = data;
        super.deliverResult(data);
    }

}
