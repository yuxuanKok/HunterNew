package com.example.hunter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RewardsRecyclerViewAdapter extends RecyclerView.Adapter<RewardsRecyclerViewAdapter.RecyclerViewHolder> {
    private ArrayList<RewardsItem> rewardsItems;
    private Context context;
    private OnItemClickListener mListener;


    public interface OnItemClickListener{
        void OnItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener=listener;
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView title;
        public TextView desc, point;

        public RecyclerViewHolder(@NonNull View itemView,OnItemClickListener listener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.cardImg);
            title = itemView.findViewById(R.id.cardName);
            desc = itemView.findViewById(R.id.cardDesc);
            point=itemView.findViewById(R.id.cardPoint);
            itemView.setOnClickListener(v -> {
                if(listener !=null){
                    int position = getAdapterPosition();
                    if (position!=RecyclerView.NO_POSITION){
                        listener.OnItemClick(position);
                    }
                }
            });
        }
    }

    public RewardsRecyclerViewAdapter(ArrayList<RewardsItem> rewardsItems) {
        this.rewardsItems = rewardsItems;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rewards, parent, false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(v,mListener);
        context = parent.getContext();
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        RewardsItem currentItem = rewardsItems.get(position);
        holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(context).load(Uri.parse(currentItem.getImages())).into(holder.imageView);
        holder.title.setText(currentItem.getTitle());
        holder.desc.setText(currentItem.getDesc());
        holder.point.setText(currentItem.getPoints()+ " points");

    }

    @Override
    public int getItemCount() {
        return rewardsItems.size();
    }

}
