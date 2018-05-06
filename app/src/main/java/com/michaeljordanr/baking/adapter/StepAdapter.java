package com.michaeljordanr.baking.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.michaeljordanr.baking.R;
import com.michaeljordanr.baking.model.Step;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepViewHolder>{
    private List<Step> mListSteps;
    private final StepAdapterOnClickListener mCallback;

    public interface StepAdapterOnClickListener{
        void onClick(Step step);
    }

    public StepAdapter(List<Step> steps, StepAdapterOnClickListener callback){
        mListSteps = steps;
        mCallback = callback;
    }

    @Override
    public StepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int idLayoutForListItem = R.layout.steps_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(idLayoutForListItem, parent, false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StepViewHolder holder, int position) {
        Step step = mListSteps.get(position);

        holder.stepNumberTextView.setText(String.valueOf(step.getId()));
        holder.stepShortDescription.setText(step.getShortDescription());
    }

    @Override
    public int getItemCount() {
        if(mListSteps == null) return 0;
        return mListSteps.size();
    }

    class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.tv_id)
        TextView stepNumberTextView;
        @BindView(R.id.tv_short_description)
        TextView stepShortDescription;


        StepViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Step step = mListSteps.get(getAdapterPosition());
            mCallback.onClick(step);
        }
    }
}
