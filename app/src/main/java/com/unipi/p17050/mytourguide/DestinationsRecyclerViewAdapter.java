package com.unipi.p17050.mytourguide;

import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;
import com.unipi.p17050.mytourguide.Models.Destination;

import java.util.ArrayList;

public class DestinationsRecyclerViewAdapter extends RecyclerView.Adapter<DestinationsRecyclerViewAdapter.ViewHolder>{
    private ArrayList<Destination> destinations=new ArrayList<>();


    public DestinationsRecyclerViewAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.destinations_lists_item,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nameTxt.setText(destinations.get(position).getName());
        holder.typeTxt.setText(destinations.get(position).getType());
        Picasso.get().load(destinations.get(position).getImage()).into(holder.imageview);
        holder.drop_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.expandable_view.getVisibility()==View.GONE){
                    TransitionManager.beginDelayedTransition(holder.cardView, new AutoTransition());
                    holder.expandable_view.setVisibility(View.VISIBLE);
                    holder.drop_button.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24);
                } else {
                    TransitionManager.beginDelayedTransition(holder.cardView, new AutoTransition());
                    holder.expandable_view.setVisibility(View.GONE);
                    holder.drop_button.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return destinations.size();
    }

    public void setDestinations(ArrayList<Destination> destinations) {
        this.destinations = destinations;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTxt;
        private TextView typeTxt;
        private ImageView imageview;
        private ImageButton drop_button;
        private ConstraintLayout expandable_view;
        private MaterialCardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt=itemView.findViewById(R.id.txtName);
            typeTxt=itemView.findViewById(R.id.txtType);
            imageview=itemView.findViewById(R.id.dest_image);
            drop_button=itemView.findViewById(R.id.hide_show_button);
            expandable_view=itemView.findViewById(R.id.expandable_view);
            cardView=itemView.findViewById(R.id.card);
        }
    }
}
