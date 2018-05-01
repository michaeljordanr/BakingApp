package com.michaeljordanr.baking.interfaces;

public interface AsyncTaskCompleteListener<T> {
    void onTaskComplete(T result, boolean isNetworkAvailable);
}