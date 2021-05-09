package com.unipi.p17050.mytourguide;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;
import com.unipi.p17050.mytourguide.Models.Destination;

import java.util.ArrayList;
import java.util.Locale;

public class DestinationsRecyclerViewAdapter extends RecyclerView.Adapter<DestinationsRecyclerViewAdapter.ViewHolder>{
    private ArrayList<Destination> destinations=new ArrayList<>();
    private Context context;

    public DestinationsRecyclerViewAdapter(Context context) {
        this.context=context;
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
       if(Locale.getDefault().getDisplayLanguage().contains("English")) {
           holder.nameTxt.setText(destinations.get(position).getName());
           holder.typeTxt.setText(destinations.get(position).getType());
           holder.infoTxt.setText(destinations.get(position).getInfo());
       }
       else {
           holder.nameTxt.setText(destinations.get(position).getName_gr());
           holder.typeTxt.setText(destinations.get(position).getType_gr());
           holder.infoTxt.setText(destinations.get(position).getInfo_gr());
       }

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
        if(destinations.get(position).getSite()!=null){
            holder.infoButton.setVisibility(View.VISIBLE);
            holder.infoButton.setOnClickListener(v -> {
                Uri uri = Uri.parse(destinations.get(position).getSite()); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            });
        }
        holder.directionButton.setOnClickListener(v -> {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?daddr="+String.valueOf(destinations.get(position).getLocation().getLatitude())+"," +String.valueOf(destinations.get(position).getLocation().getLongitude())));
            context.startActivity(intent);
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
        private final TextView nameTxt;
        private final TextView typeTxt;
        private final TextView infoTxt;
        private final ImageView imageview;
        private final ImageButton drop_button;
        private final ConstraintLayout expandable_view;
        private final MaterialButton infoButton;
        private final MaterialButton directionButton;
        private final MaterialCardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt=itemView.findViewById(R.id.txtName);
            typeTxt=itemView.findViewById(R.id.txtType);
            infoTxt=itemView.findViewById(R.id.infoTxt);
            imageview=itemView.findViewById(R.id.dest_image);
            drop_button=itemView.findViewById(R.id.hide_show_button);
            expandable_view=itemView.findViewById(R.id.expandable_view);
            infoButton=itemView.findViewById(R.id.infoButton);
            cardView=itemView.findViewById(R.id.card);
            directionButton=itemView.findViewById(R.id.directionButton);
        }
    }
}
