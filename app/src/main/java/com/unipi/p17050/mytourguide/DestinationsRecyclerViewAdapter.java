package com.unipi.p17050.mytourguide;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.unipi.p17050.mytourguide.Models.Destination;
import com.unipi.p17050.mytourguide.Models.Profile;
import com.unipi.p17050.mytourguide.ViewModels.ProfilesViewModel;

import java.util.ArrayList;
import java.util.Locale;

public class DestinationsRecyclerViewAdapter extends RecyclerView.Adapter<DestinationsRecyclerViewAdapter.ViewHolder> {
    private ArrayList<Destination> destinations = new ArrayList<>();
    private Context context;
    private Profile profile;

    public DestinationsRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.destinations_lists_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (Locale.getDefault().getDisplayLanguage().contains("English")) {
            holder.nameTxt.setText(destinations.get(position).getName());
            holder.typeTxt.setText(destinations.get(position).getType());
            holder.infoTxt.setText(destinations.get(position).getInfo());
        } else {
            holder.nameTxt.setText(destinations.get(position).getName_gr());
            holder.typeTxt.setText(destinations.get(position).getType_gr());
            holder.infoTxt.setText(destinations.get(position).getInfo_gr());
        }

        Picasso.get().load(destinations.get(position).getImage()).into(holder.imageview);
        holder.drop_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.expandable_view.getVisibility() == View.GONE) {
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
        if (destinations.get(position).getSite() != null) {
            holder.infoButton.setVisibility(View.VISIBLE);
            holder.infoButton.setOnClickListener(v -> {
                Uri uri = Uri.parse(destinations.get(position).getSite()); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            });
        }
        holder.directionButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?daddr=" + String.valueOf(destinations.get(position).getLocation().getLatitude()) + "," + String.valueOf(destinations.get(position).getLocation().getLongitude())));
            context.startActivity(intent);
        });

        if (profile != null) {
            if (profile.getFavorites().contains(destinations.get(position).getName())) {
                holder.favoriteButton.setImageResource(R.drawable.ic_baseline_favorite_24);

            }


            holder.favoriteButton.setOnClickListener(v -> {
                if (profile.getFavorites().contains(destinations.get(position).getName())) {
                    holder.favoriteButton.setImageResource(R.drawable.ic_baseline_favorite_border_24);

                    profile.getFavorites().remove(destinations.get(position).getName());
                } else {
                    holder.favoriteButton.setImageResource(R.drawable.ic_baseline_favorite_24);

                    profile.getFavorites().add(destinations.get(position).getName());
                }
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                assert user != null;
                mDatabase.child("Profiles").child(user.getUid()).setValue(profile);
            });
        }
        else
            holder.favoriteButton.setVisibility(View.GONE);
    }


    @Override
    public int getItemCount() {
        return destinations.size();
    }

    public void setDestinations(ArrayList<Destination> destinations) {
        this.destinations = destinations;
        notifyDataSetChanged();
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
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
        private final ImageButton favoriteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.txtName);
            typeTxt = itemView.findViewById(R.id.txtType);
            infoTxt = itemView.findViewById(R.id.infoTxt);
            imageview = itemView.findViewById(R.id.dest_image);
            drop_button = itemView.findViewById(R.id.hide_show_button);
            expandable_view = itemView.findViewById(R.id.expandable_view);
            infoButton = itemView.findViewById(R.id.infoButton);
            cardView = itemView.findViewById(R.id.card);
            directionButton = itemView.findViewById(R.id.directionButton);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);

        }
    }
}
