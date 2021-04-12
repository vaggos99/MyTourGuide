package com.unipi.p17050.mytourguide;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        holder.textView.setText(destinations.get(position).getName());
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
        private TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.txtName);
        }
    }
}
