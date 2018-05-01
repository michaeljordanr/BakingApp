package com.michaeljordanr.baking.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.google.gson.Gson;
import com.michaeljordanr.baking.R;
import com.michaeljordanr.baking.interfaces.AsyncTaskCompleteListener;
import com.michaeljordanr.baking.model.Recipe;
import com.michaeljordanr.baking.ultil.NetworkUtils;

import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class RecipeTask extends AsyncTaskLoader<List<Recipe>> {

    private final Context mContext;
    private final AsyncTaskCompleteListener mCallback;
    private ProgressDialog progressDialog;
    private List<Recipe> recipes = new ArrayList<>();

    boolean isNetworkAvailable;

    public RecipeTask(Context context, AsyncTaskCompleteListener listener) {
        super(context);
        mContext = context;
        mCallback = listener;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        progressDialog = new ProgressDialog(mContext, R.style.AppTheme);
        progressDialog.setTitle(mContext.getString(R.string.loading));
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    @Override
    public List<Recipe> loadInBackground() {
        URL recipesUrl = null;
        String jsonResponse;

        isNetworkAvailable = NetworkUtils.isNetworkAvailable(mContext);

        if(isNetworkAvailable){
            try {
                recipesUrl = NetworkUtils.buildUrlRecipes();
                jsonResponse = NetworkUtils.run(recipesUrl);

                recipes = new Gson().fromJson(jsonResponse, recipes.getClass());
            }catch (UnknownHostException e){
                return null;
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return recipes;
    }

    @Override
    public void deliverResult(List<Recipe> data) {
        if(progressDialog != null){
            progressDialog.dismiss();
        }

        mCallback.onTaskComplete(recipes, isNetworkAvailable);
    }
}
