package com.michaeljordanr.baking.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.michaeljordanr.baking.R;
import com.michaeljordanr.baking.interfaces.ToolbarControlListener;
import com.michaeljordanr.baking.model.Step;
import com.michaeljordanr.baking.util.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import butterknife.Unbinder;

public class StepDetailFragment extends Fragment {

    private static String STATE_RESUME_WINDOW = "state_resume";
    private static String STATE_RESUME_POSITION = "state_position";

    private int mResumeWindow;
    private long mResumePosition;


    private ToolbarControlListener mToolbarCallback;

    private List<Step> mStepList;
    private int mPositionSelected;

    private SimpleExoPlayer mExoPlayer;
    MediaSource mMediaSource;

    @BindView(R.id.tv_description)
    TextView descriptionTextView;
    @BindView(R.id.tv_next_step)
    TextView nextStepTextView;
    @BindView(R.id.tv_previous_step)
    TextView previousStepTextView;
    @BindView(R.id.playerView)
    PlayerView mPlayerView;
    @BindView(R.id.cv_description)
    CardView mDescriptionCardView;

    private Unbinder unbinder;
    private boolean mTwoPane;

    public static StepDetailFragment newInstance(ArrayList<Step> stepList, int position,
                                                 boolean twoPane){
        StepDetailFragment fragment = new StepDetailFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(Constants.STEP_LIST_ARG, stepList);
        args.putInt(Constants.STEP_LIST_POSITION_ARG, position);
        args.putBoolean(Constants.TWO_PANE_ARG, twoPane);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mToolbarCallback = (ToolbarControlListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement ToolbarControlListener");
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            mStepList = getArguments().getParcelableArrayList(Constants.STEP_LIST_ARG);
            mPositionSelected = getArguments().getInt(Constants.STEP_LIST_POSITION_ARG);
            mTwoPane = getArguments().getBoolean(Constants.TWO_PANE_ARG);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_step_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        setRetainInstance(true);

        if(mTwoPane){
            nextStepTextView.setVisibility(View.GONE);
            previousStepTextView.setVisibility(View.GONE);
        }

        setupView();
        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void setupView(){
        Step step = mStepList.get(mPositionSelected);

        mToolbarCallback.setTitle(getResources().getString(R.string.step_title, step.getId()));

        descriptionTextView.setText(step.getDescription());
        if(!mTwoPane) {
            if (!(previousStepTextView == null || nextStepTextView == null)) {
                previousStepTextView
                        .setVisibility(mPositionSelected == 0 ? View.INVISIBLE : View.VISIBLE);
                nextStepTextView
                        .setVisibility(mPositionSelected == mStepList.size() - 1 ? View.INVISIBLE : View.VISIBLE);
            }
        }

        int cvVisibility = getResources().getInteger(R.integer.cv_description_visibility);
        if(step.getVideoUrl() == null || step.getVideoUrl().isEmpty()){
            mPlayerView.setVisibility(View.GONE);
            mDescriptionCardView.setVisibility(View.VISIBLE);
        }else {
            mDescriptionCardView.setVisibility(cvVisibility == View.VISIBLE ? View.VISIBLE : View.GONE);
            mPlayerView.setVisibility(View.VISIBLE);
            startPlayer(step.getVideoUrl());
        }

    }

    private void startPlayer(String videoURL) {
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), new DefaultTrackSelector());

        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getContext(),
                Util.getUserAgent(getContext(), getString(R.string.app_name)), bandwidthMeter);

        mMediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(videoURL));

        mExoPlayer.prepare(mMediaSource);
        mExoPlayer.setPlayWhenReady(true);
        mPlayerView.setPlayer(mExoPlayer);

        mExoPlayer.addListener(new ExoPlayer.DefaultEventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == PlaybackStateCompat.STATE_SKIPPING_TO_NEXT) {
                    onNextStepClick();
                }
                if (playbackState == PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS) {
                    onPreviousStepClick();
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                mExoPlayer.stop();

            }
        });
    }

    private void releasePlayer(){
        if(mExoPlayer != null){
            mExoPlayer.release();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mPlayerView == null){
            setupView();
        }else {
            resumePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPlayerView != null && mPlayerView.getPlayer() != null) {
            mResumeWindow = mPlayerView.getPlayer().getCurrentWindowIndex();
            mResumePosition = Math.max(0, mPlayerView.getPlayer().getContentPosition());
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_RESUME_WINDOW, mResumeWindow);
        outState.putLong(STATE_RESUME_POSITION, mResumePosition);
        super.onSaveInstanceState(outState);
    }

    private void resumePlayer() {

        boolean haveResumePosition = mResumeWindow != C.INDEX_UNSET;

        if (haveResumePosition) {
            mPlayerView.getPlayer().seekTo(mResumeWindow, mResumePosition);
        }
    }

    @OnClick({R.id.tv_previous_step})
    @Optional
    public void onPreviousStepClick() {
        if (this.mPositionSelected > 0) {
            this.mPositionSelected--;
            releasePlayer();
            setupView();
        }
    }

    @OnClick({R.id.tv_next_step})
    @Optional
    public void onNextStepClick() {
        if (this.mPositionSelected < this.mStepList.size()) {
            this.mPositionSelected++;
            releasePlayer();
            setupView();
        }
    }
}
