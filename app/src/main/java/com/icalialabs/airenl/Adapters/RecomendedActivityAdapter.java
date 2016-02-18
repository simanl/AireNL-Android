package com.icalialabs.airenl.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.icalialabs.airenl.Models.Recomendation;
import com.icalialabs.airenl.R;

import java.util.List;

/**
 * Created by Compean on 08/10/15.
 */
public class RecomendedActivityAdapter extends RecyclerView.Adapter<RecomendedActivityAdapter.ActivityHolder> {

    List<Recomendation> imageIds;

    public static class ActivityHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        FrameLayout itemView;

        ActivityHolder(View itemView) {
            super(itemView);
            this.itemView = (FrameLayout)itemView;
            imageView = (ImageView)itemView.findViewById(R.id.activity_image);
        }
    }

    public RecomendedActivityAdapter(List<Recomendation> imageIds) {
        this.imageIds = imageIds;
    }

    public void setImageIds(List<Recomendation> imageIds) {
        this.imageIds = imageIds;
    }

    @Override
    public ActivityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recomended_activity_item, parent, false);
        final ActivityHolder activityHolder = new ActivityHolder(itemView);
        final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)activityHolder.imageView.getLayoutParams();
//        layoutParams.height = activityHolder.itemView.getLayoutParams().width - layoutParams.leftMargin - layoutParams.rightMargin;
        layoutParams.height = activityHolder.itemView.getLayoutParams().width + 20;
        activityHolder.imageView.setLayoutParams(layoutParams);
//        activityHolder.imageView.post(new Runnable() {
//            @Override
//            public void run() {
//                layoutParams.height = activityHolder.imageView.getWidth();
//                activityHolder.imageView.setLayoutParams(layoutParams);
//                activityHolder.imageView.postInvalidate();
//            }
//        });
        return activityHolder;
    }

    @Override
    public void onBindViewHolder(ActivityHolder holder, int position) {
        holder.imageView.setImageResource(imageIds.get(position).getResourceId());
//        final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)holder.imageView.getLayoutParams();
//        layoutParams.leftMargin = (position == 0)? layoutParams.rightMargin : 0;
//        holder.imageView.setLayoutParams(layoutParams);
//        holder.imageView.postInvalidate();
    }

    @Override
    public int getItemCount() {
        if (imageIds == null) {
            return 0;
        }
        return imageIds.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
