package com.bloodnetwork.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bloodnetwork.app.R;
import com.bloodnetwork.app.controller.EmergencyController;
import com.bloodnetwork.app.model.DonorRequest;
import com.bloodnetwork.app.model.EmergencyRequest;

import java.util.List;

// Shows a donor's pending requests with inline Accept/Decline actions, resolving each
// request's parent EmergencyRequest through the controller for a readable summary.
public class DonorRequestAdapter extends RecyclerView.Adapter<DonorRequestAdapter.ViewHolder> {

    public interface OnResponseListener {
        void onAccept(DonorRequest request, EmergencyRequest emergencyRequest);
        void onReject(DonorRequest request);
    }

    private final List<DonorRequest> requests;
    private final EmergencyController controller;
    private final OnResponseListener listener;

    public DonorRequestAdapter(List<DonorRequest> requests, EmergencyController controller,
                                OnResponseListener listener) {
        this.requests = requests;
        this.controller = controller;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_donor_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DonorRequest request = requests.get(position);
        EmergencyRequest er = controller.getEmergencyRequest(request.getEmergencyRequestId());

        if (er != null) {
            holder.summary.setText(er.getBloodGroup() + " x" + er.getQuantity() + " needed");
        } else {
            holder.summary.setText("Emergency request " + request.getEmergencyRequestId());
        }
        holder.meta.setText("Request " + request.getId());

        holder.accept.setOnClickListener(v -> {
            if (er != null && listener != null) listener.onAccept(request, er);
        });
        holder.reject.setOnClickListener(v -> {
            if (listener != null) listener.onReject(request);
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView summary, meta;
        com.google.android.material.button.MaterialButton accept, reject;

        ViewHolder(View itemView) {
            super(itemView);
            summary = itemView.findViewById(R.id.txtRequestSummary);
            meta = itemView.findViewById(R.id.txtRequestMeta);
            accept = itemView.findViewById(R.id.btnAccept);
            reject = itemView.findViewById(R.id.btnReject);
        }
    }
}
