package com.bloodnetwork.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bloodnetwork.app.R;
import com.bloodnetwork.app.model.BloodBank;

import java.util.List;
import java.util.Map;

public class BloodBankAdapter extends RecyclerView.Adapter<BloodBankAdapter.ViewHolder> {

    private static final String[] BLOOD_GROUP_ORDER = {
            "O+", "A+", "B+", "AB+", "O-", "A-", "B-", "AB-"
    };

    private final List<BloodBank> banks;

    public BloodBankAdapter(List<BloodBank> banks) {
        this.banks = banks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_blood_bank, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BloodBank bank = banks.get(position);
        holder.name.setText(bank.getName());

        Map<String, Integer> stock = bank.getAllStock();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < BLOOD_GROUP_ORDER.length; i++) {
            String group = BLOOD_GROUP_ORDER[i];
            sb.append(group).append(": ").append(stock.getOrDefault(group, 0)).append(" units");
            if (i == 3) sb.append("\n");
            else if (i < BLOOD_GROUP_ORDER.length - 1) sb.append("   ");
        }
        holder.stock.setText(sb.toString());
    }

    @Override
    public int getItemCount() {
        return banks.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, stock;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtBankName);
            stock = itemView.findViewById(R.id.txtBankStock);
        }
    }
}
