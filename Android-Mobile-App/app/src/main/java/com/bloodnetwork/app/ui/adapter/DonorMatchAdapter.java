package com.bloodnetwork.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bloodnetwork.app.R;
import com.bloodnetwork.app.model.DonorMatch;

import java.util.List;
import java.util.Locale;

// Shows ranked donor matches (nearest first), each with its blood group, name, and route.
public class DonorMatchAdapter extends RecyclerView.Adapter<DonorMatchAdapter.ViewHolder> {

    private final List<DonorMatch> matches;

    public DonorMatchAdapter(List<DonorMatch> matches) {
        this.matches = matches;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_donor_match, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DonorMatch match = matches.get(position);
        holder.badge.setText(match.getDonor().getBloodGroup());
        holder.name.setText(match.getDonor().getName());
        holder.route.setText(String.format(Locale.getDefault(), "%.1f km, %d min away",
                match.getDistance(), match.getTravelTime()));
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView badge, name, route;

        ViewHolder(View itemView) {
            super(itemView);
            badge = itemView.findViewById(R.id.txtBloodGroupBadge);
            name = itemView.findViewById(R.id.txtDonorName);
            route = itemView.findViewById(R.id.txtRoute);
        }
    }
}
