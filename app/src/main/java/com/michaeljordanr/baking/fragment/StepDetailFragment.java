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
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

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
    private static String PLAY_WHEN_READY = "play_when_ready";

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
    @BindView(R.id.iv_thumbnail)
    ImageView mThumbnailImageView;

    private Unbinder unbinder;
    private boolean mIsTablet;
    private boolean mHasVideo;
    private boolean mPlayWhenReady;

    private Step mStep;

    public static StepDetailFragment newInstance(ArrayList<Step> stepList, int position,
                                                 boolean isTablet){
        StepDetailFragment fragment = new StepDetailFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(Constants.STEP_LIST_ARG, stepList);
        args.putInt(Constants.STEP_LIST_POSITION_ARG, position);
        args.putBoolean(Constants.IS_TABLET_ARG, isTablet);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(!getResources().getBoolean(R.bool.isTablet)) {
            try {
                mToolbarCallback = (ToolbarControlListener) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString()
                        + " must implement ToolbarControlListener");
            }
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            mStepList = getArguments().getParcelableArrayList(Constants.STEP_LIST_ARG);
            mPositionSelected = getArguments().getInt(Constants.STEP_LIST_POSITION_ARG);
            mIsTablet = getArguments().getBoolean(Constants.IS_TABLET_ARG);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_step_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        setRetainInstance(true);

        if (savedInstanceState != null) {
            mResumeWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW, mResumeWindow);
            mResumePosition = savedInstanceState.getLong(STATE_RESUME_POSITION, mResumePosition);
            mStep = savedInstanceState.getParcelable(Constants.STEP_ARG);
            mPlayWhenReady = savedInstanceState.getBoolean(PLAY_WHEN_READY, mExoPlayer.getPlayWhenReady());
        }

        if(mIsTablet){
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
        mStep = mStepList.get(mPositionSelected);

        if(!mIsTablet) {
            mToolbarCallback.setTitle(getResources().getString(R.string.step_title, String.valueOf(mStep.getId())));
        }

        descriptionTextView.setText(mStep.getDescription());
        if(!mIsTablet) {
            if (!(previousStepTextView == null || nextStepTextView == null)) {
                previousStepTextView
                        .setVisibility(mPositionSelected == 0 ? View.INVISIBLE : View.VISIBLE);
                nextStepTextView
                        .setVisibility(mPositionSelected == mStepList.size() - 1 ? View.INVISIBLE : View.VISIBLE);
            }
        }

        if(mStep.getThumbnailUrl() != null && !mStep.getThumbnailUrl().isEmpty()){
            Picasso.with(getContext()).
                    load(mStep.getThumbnailUrl())
                    .into(mThumbnailImageView);
        }

        int cvVisibility = getResources().getInteger(R.integer.cv_description_visibility);
        if(mStep.getVideoUrl() == null || mStep.getVideoUrl().isEmpty()){
            mHasVideo = true;
            mPlayerView.setVisibility(View.GONE);
            mDescriptionCardView.setVisibility(View.VISIBLE);
        }else {
            mHasVideo = false;
            mDescriptionCardView.setVisibility(cvVisibility == View.VISIBLE ? View.VISIBLE : View.GONE);
            mPlayerView.setVisibility(View.VISIBLE);
            startPlayer();
        }

    }

    private void startPlayer() {
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), new DefaultTrackSelector());

        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getContext(),
                Util.getUserAgent(getContext(), getString(R.string.app_name)), bandwidthMeter);

        mMediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(mStep.getVideoUrl()));

        mExoPlayer.prepare(mMediaSource);
        mExoPlayer.setPlayWhenReady(mPlayWhenReady);
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
        if(!mIsTablet) {
            if (mPlayerView == null) {
                setupView();
            } else {
                startPlayer();
                resumePlayer();
            }
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
        Bundle bundle = new Bundle();
        bundle.putInt(STATE_RESUME_WINDOW, mResumeWindow);
        bundle.putLong(STATE_RESUME_POSITION, mResumePosition);
        bundle.putParcelable(Constants.STEP_ARG, mStep);
        bundle.putBoolean(PLAY_WHEN_READY, mExoPlayer.getPlayWhenReady());
        outState.putAll(bundle);
        super.onSaveInstanceState(outState);
    }

    private void resumePlayer() {
        if(mHasVideo) {
            boolean haveResumePosition = mResumeWindow != C.INDEX_UNSET;

            if (haveResumePosition) {
                mPlayerView.getPlayer().seekTo(mResumeWindow, mResumePosition);
            }
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
