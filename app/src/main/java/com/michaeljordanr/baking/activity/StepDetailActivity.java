package com.michaeljordanr.baking.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.WindowManager;

import com.michaeljordanr.baking.R;
import com.michaeljordanr.baking.fragment.StepDetailFragment;
import com.michaeljordanr.baking.interfaces.ToolbarControlListener;
import com.michaeljordanr.baking.model.Step;
import com.michaeljordanr.baking.util.Constants;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class StepDetailActivity extends AppCompatActivity implements ToolbarControlListener {

    private int mPositionSelected;
    private ArrayList<Step> mStepList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_detail_activity);
        ButterKnife.bind(this);

        if (getIntent().getExtras() != null) {
            mStepList = getIntent().getExtras().getParcelableArrayList(Constants.STEP_LIST_ARG);
            mPositionSelected = getIntent().getExtras().getInt(Constants.STEP_LIST_POSITION_ARG);
        }
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction().add(
                            R.id.step_detail_container,
                            StepDetailFragment.newInstance(mStepList, mPositionSelected, false))
                    .commit();
        }

        int display_mode = getResources().getConfiguration().orientation;
        if (display_mode == Configuration.ORIENTATION_LANDSCAPE) {
            getSupportActionBar().hide();

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }else {
            ActionBar actionBar = getSupportActionBar();

            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            getSupportActionBar().setTitle(getResources().getString(R.string.step_title,
                    mStepList.get(mPositionSelected).getId()));
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != android.R.id.home) {
            return super.onOptionsItemSelected(item);
        }
        onBackPressed();
        return true;
    }

    @Override
    public void setTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }
}
