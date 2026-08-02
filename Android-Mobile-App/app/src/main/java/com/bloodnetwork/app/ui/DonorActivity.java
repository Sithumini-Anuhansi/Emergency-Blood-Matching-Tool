package com.bloodnetwork.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bloodnetwork.app.R;
import com.bloodnetwork.app.controller.EmergencyController;
import com.bloodnetwork.app.model.AppNotification;
import com.bloodnetwork.app.model.Donor;
import com.bloodnetwork.app.model.DonorRequest;
import com.bloodnetwork.app.model.EmergencyRequest;
import com.bloodnetwork.app.model.Hospital;
import com.bloodnetwork.app.model.RequestStatus;
import com.bloodnetwork.app.system.EmergencyBloodSystem;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DonorActivity extends AppCompatActivity {

    private EmergencyController controller;
    private DrawerLayout drawerLayout;

    private TextView tvDonorName, tvDrawerDonorName, tvSectionTitle, txtResult;
    private TextView tvStatDonations, tvStatPendingReqs, tvStatusBadge;
    private LinearLayout layoutDashboard, layoutRequests, layoutResult, listContainer;
    private Switch switchAvailability;

    private Donor currentDonor;
    private String donorId, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_home);

        donorId = getIntent().getStringExtra("donorId");
        userId  = getIntent().getStringExtra("userId");

        try {
            controller = EmergencyBloodSystem.getInstance(this).getController();
        } catch (IOException e) {
            Toast.makeText(this, "Failed to load system", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (donorId != null && !donorId.isEmpty())
            currentDonor = controller.getDonorService().findDonor(donorId);
        if (currentDonor == null) {
            List<Donor> all = controller.getDonorService().getAllDonors();
            if (!all.isEmpty()) { currentDonor = all.get(0); donorId = currentDonor.getId(); }
        }

        drawerLayout      = findViewById(R.id.drawerLayout);
        tvDonorName       = findViewById(R.id.tvDonorName);
        tvDrawerDonorName = findViewById(R.id.tvDrawerDonorName);
        tvSectionTitle    = findViewById(R.id.tvSectionTitle);
        layoutDashboard   = findViewById(R.id.layoutDashboard);
        layoutRequests    = findViewById(R.id.layoutRequests);
        layoutResult      = findViewById(R.id.layoutResult);
        listContainer     = findViewById(R.id.listContainer);
        txtResult         = findViewById(R.id.txtResult);
        switchAvailability = findViewById(R.id.switchAvailability);
        tvStatDonations   = findViewById(R.id.tvStatDonations);
        tvStatPendingReqs = findViewById(R.id.tvStatPendingReqs);
        tvStatusBadge     = findViewById(R.id.tvStatusBadge);

        String name = currentDonor != null ? currentDonor.getName() : "Donor";
        tvDonorName.setText("Hello, " + name);
        tvDrawerDonorName.setText(name);
        if (currentDonor != null) switchAvailability.setChecked(currentDonor.isAvailable());

        switchAvailability.setOnCheckedChangeListener((v, checked) -> {
            if (currentDonor != null) {
                controller.getDonorService().updateAvailability(donorId, checked);
                updateStatusBadge(checked);
                Toast.makeText(this,
                        checked ? "You are now Available" : "You are Unavailable",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Toolbar
        findViewById(R.id.btnMenu).setOnClickListener(
                v -> drawerLayout.openDrawer(Gravity.START));

        // Drawer items
        findViewById(R.id.navDashboard).setOnClickListener(
                v -> { closeDrawer(); showDashboard(); });
        findViewById(R.id.btnPendingRequestsNav).setOnClickListener(
                v -> { closeDrawer(); showPendingRequests(); });
        findViewById(R.id.btnDonationHistory).setOnClickListener(
                v -> { closeDrawer(); showDonationHistory(); });
        findViewById(R.id.btnProfile).setOnClickListener(
                v -> { closeDrawer(); showProfile(); });
        findViewById(R.id.btnNotifications).setOnClickListener(
                v -> { closeDrawer(); showNotifications(); });
        findViewById(R.id.btnLogout).setOnClickListener(
                v -> logout());

        // Dashboard quick button
        findViewById(R.id.btnPendingRequests).setOnClickListener(
                v -> showPendingRequests());
        findViewById(R.id.cardStatDonations).setOnClickListener(
                v -> showDonationHistory());
        findViewById(R.id.cardStatPending).setOnClickListener(
                v -> showPendingRequests());

        // Back buttons
        findViewById(R.id.btnBackFromRequests).setOnClickListener(
                v -> showDashboard());
        findViewById(R.id.btnBackFromResult).setOnClickListener(
                v -> showDashboard());

        showDashboard();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START);
        } else if (layoutDashboard.getVisibility() != View.VISIBLE) {
            showDashboard();
        } else {
            super.onBackPressed();
        }
    }

    private void closeDrawer() { drawerLayout.closeDrawer(Gravity.START); }

    private void updateStatusBadge(boolean available) {
        tvStatusBadge.setText(available ? "Available" : "Unavailable");
        tvStatusBadge.setTextColor(available ? 0xFF388E3C : 0xFFC62828);
    }

    // ── Dashboard ──────────────────────────────────────────────────────────

    private void showDashboard() {
        layoutDashboard.setVisibility(View.VISIBLE);
        layoutRequests.setVisibility(View.GONE);
        layoutResult.setVisibility(View.GONE);
        tvSectionTitle.setText("Home");
        refreshStats();
    }

    private void refreshStats() {
        int donations = 0, pending = 0;
        for (DonorRequest dr : controller.getQueueService().getAllRequests()) {
            if (!dr.getDonorId().equals(donorId)) continue;
            if (dr.getStatus() == RequestStatus.ACCEPTED) donations++;
            else if (dr.getStatus() == RequestStatus.PENDING) pending++;
        }
        tvStatDonations.setText(String.valueOf(donations));
        tvStatPendingReqs.setText(String.valueOf(pending));
        updateStatusBadge(currentDonor != null && currentDonor.isAvailable());

        int unread = controller.getUnreadNotificationCount(donorId);
        TextView tvNotifBadge = findViewById(R.id.tvNotifBadge);
        if (tvNotifBadge != null) {
            tvNotifBadge.setText(unread > 0 ? "🔔 (" + unread + ")" : "🔔");
        }
    }

    // ── Pending requests ───────────────────────────────────────────────────

    private void showPendingRequests() {
        layoutDashboard.setVisibility(View.GONE);
        layoutRequests.setVisibility(View.VISIBLE);
        layoutResult.setVisibility(View.GONE);
        tvSectionTitle.setText("Pending Requests");
        listContainer.removeAllViews();

        if (currentDonor == null) return;
        List<DonorRequest> requests = controller.getPendingRequestsForDonor(donorId);
        if (requests.isEmpty()) {
            addCard("No pending requests.", null, null);
        } else {
            for (DonorRequest dr : requests) {
                EmergencyRequest er = controller.getEmergencyRequest(dr.getEmergencyRequestId());
                Hospital hospital   = er != null ? controller.getHospital(er.getHospitalId()) : null;
                
                String locationName = "?";
                if (hospital != null) {
                    com.bloodnetwork.app.model.Location loc = controller.getGraph().getLocation(hospital.getLocationId());
                    if (loc != null) locationName = loc.getName();
                }

                String detail = "Blood: " + (er != null ? er.getBloodGroup() : "?")
                        + "\nHospital: " + (hospital != null ? hospital.getName() : "?")
                        + " (" + locationName + ")"
                        + "\nRequested: " + (er != null
                                ? new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(new Date(er.getTimestamp()))
                                : "?")
                        + "\nRequest ID: " + dr.getId();
                addCard("Emergency Blood Request", detail, dr.getId());
            }
        }
    }

    private void addCard(String title, String detail, String requestId) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(24, 20, 24, 20);
        card.setBackgroundResource(R.drawable.card_bg);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, 0, 0, 16);
        card.setLayoutParams(p);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setTextSize(15);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        card.addView(tvTitle);

        if (detail != null) {
            TextView tvDetail = new TextView(this);
            tvDetail.setText(detail);
            tvDetail.setTextSize(13);
            tvDetail.setPadding(0, 8, 0, 8);
            card.addView(tvDetail);
        }

        if (requestId != null) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);

            Button btnAccept = new Button(this);
            btnAccept.setText("Accept");
            btnAccept.setBackgroundResource(R.drawable.btn_accept);
            btnAccept.setTextColor(0xFFFFFFFF);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            lp.setMargins(0, 0, 8, 0);
            btnAccept.setLayoutParams(lp);
            btnAccept.setOnClickListener(v -> acceptRequest(requestId));

            Button btnReject = new Button(this);
            btnReject.setText("Reject");
            btnReject.setBackgroundResource(R.drawable.btn_reject);
            btnReject.setTextColor(0xFFFFFFFF);
            btnReject.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            btnReject.setOnClickListener(v -> rejectRequest(requestId));

            row.addView(btnAccept);
            row.addView(btnReject);
            card.addView(row);
        }
        listContainer.addView(card);
    }

    private void acceptRequest(String requestId) {
        DonorRequest dr = controller.getQueueService().getRequest(requestId);
        if (dr == null) { Toast.makeText(this, "Request not found", Toast.LENGTH_SHORT).show(); return; }
        EmergencyRequest er = controller.getEmergencyRequest(dr.getEmergencyRequestId());
        if (er == null) { Toast.makeText(this, "Emergency not found", Toast.LENGTH_SHORT).show(); return; }
        boolean ok = controller.donorAccept(requestId, er);
        Toast.makeText(this, ok ? "Accepted. Hospital notified." : "Could not accept.",
                Toast.LENGTH_LONG).show();
        showPendingRequests();
        refreshStats();
    }

    private void rejectRequest(String requestId) {
        boolean ok = controller.donorReject(requestId);
        Toast.makeText(this, ok ? "Rejected. Next donor notified." : "Could not reject.",
                Toast.LENGTH_SHORT).show();
        showPendingRequests();
        refreshStats();
    }

    // ── Other screens ──────────────────────────────────────────────────────

    private void showDonationHistory() {
        StringBuilder sb = new StringBuilder();
        boolean found = false;
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
        for (DonorRequest dr : controller.getQueueService().getAllRequests()) {
            if (dr.getDonorId().equals(donorId) && dr.getStatus() == RequestStatus.ACCEPTED) {
                EmergencyRequest er = controller.getEmergencyRequest(dr.getEmergencyRequestId());
                Hospital hospital = er != null ? controller.getHospital(er.getHospitalId()) : null;
                sb.append(dr.getId()).append("  ")
                  .append(er != null ? er.getBloodGroup() : "?")
                  .append("  Completed\n")
                  .append("Hospital: ").append(hospital != null ? hospital.getName() : "?")
                  .append("  |  ").append(er != null ? sdf.format(new Date(er.getTimestamp())) : "?")
                  .append("\n\n");
                found = true;
            }
        }
        if (!found) sb.append("No donations yet.");
        showTextResult("Donation History", sb.toString());
    }

    private void showProfile() {
        if (currentDonor == null) return;
        
        String locationName = currentDonor.getLocationId();
        com.bloodnetwork.app.model.Location loc = controller.getGraph().getLocation(currentDonor.getLocationId());
        if (loc != null) locationName = loc.getName();

        String info = "Name:        " + currentDonor.getName()
                + "\nBlood Group: " + currentDonor.getBloodGroup()
                + "\nAge:         " + currentDonor.getAge()
                + "\nLocation:    " + locationName
                + "\nAvailable:   " + (currentDonor.isAvailable() ? "Yes" : "No")
                + "\nEligible:    " + (currentDonor.isEligible() ? "Yes" : "No");
        showTextResult("My Profile", info);
    }

    private void showNotifications() {
        layoutDashboard.setVisibility(View.GONE);
        layoutRequests.setVisibility(View.GONE);
        layoutResult.setVisibility(View.VISIBLE);
        tvSectionTitle.setText("Notifications");
        txtResult.setText("Notification History");
        listContainer.removeAllViews();
        
        List<AppNotification> notifs = controller.getNotifications(donorId);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
        
        if (notifs.isEmpty()) {
            TextView tv = new TextView(this);
            tv.setText("No notifications.");
            tv.setPadding(16, 12, 16, 12);
            listContainer.addView(tv);
        } else {
            // Show latest first
            for (int i = notifs.size() - 1; i >= 0; i--) {
                AppNotification n = notifs.get(i);
                String timeStr = sdf.format(new Date(n.getTimestamp()));
                
                LinearLayout card = new LinearLayout(this);
                card.setOrientation(LinearLayout.VERTICAL);
                card.setPadding(24, 16, 24, 16);
                card.setBackgroundResource(R.drawable.card_bg);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                p.setMargins(0, 0, 0, 12);
                card.setLayoutParams(p);

                TextView tvTitle = new TextView(this);
                tvTitle.setText((n.isRead() ? "" : "[NEW] ") + n.getTitle());
                tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
                card.addView(tvTitle);

                TextView tvBody = new TextView(this);
                tvBody.setText(n.getMessage());
                tvBody.setTextSize(13);
                card.addView(tvBody);

                TextView tvTime = new TextView(this);
                tvTime.setText(timeStr);
                tvTime.setTextSize(11);
                tvTime.setTextColor(0xFF757575);
                tvTime.setPadding(0, 4, 0, 0);
                card.addView(tvTime);

                listContainer.addView(card);
            }
            controller.markNotificationsRead(donorId);
        }
        
        TextView tvNotifBadge = findViewById(R.id.tvNotifBadge);
        if (tvNotifBadge != null) tvNotifBadge.setText("🔔");
    }

    private void showTextResult(String title, String body) {
        tvSectionTitle.setText(title);
        txtResult.setText(body);
        layoutResult.setVisibility(View.VISIBLE);
        layoutRequests.setVisibility(View.GONE);
        layoutDashboard.setVisibility(View.GONE);
    }

    private void logout() {
        startActivity(new Intent(this, LoginActivity.class));
        finishAffinity();
    }
}
