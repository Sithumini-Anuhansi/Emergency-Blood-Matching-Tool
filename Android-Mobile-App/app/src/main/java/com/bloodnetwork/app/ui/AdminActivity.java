package com.bloodnetwork.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bloodnetwork.app.R;
import com.bloodnetwork.app.controller.EmergencyController;
import com.bloodnetwork.app.model.AppNotification;
import com.bloodnetwork.app.model.BloodBank;
import com.bloodnetwork.app.model.Donor;
import com.bloodnetwork.app.model.EmergencyRequest;
import com.bloodnetwork.app.model.Hospital;
import com.bloodnetwork.app.model.RequestStatus;
import com.bloodnetwork.app.model.User;
import com.bloodnetwork.app.model.UserRole;
import com.bloodnetwork.app.service.AuthService;
import com.bloodnetwork.app.system.EmergencyBloodSystem;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {

    private EmergencyController controller;
    private AuthService authService;
    private DrawerLayout drawerLayout;

    private TextView tvSectionTitle, txtResult;
    private TextView tvStatUsers, tvStatPending, tvStatRequests, tvStatStock;
    private LinearLayout layoutDashboard, layoutResult, listContainer;
    private Button tvNotifBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        try {
            EmergencyBloodSystem sys = EmergencyBloodSystem.getInstance(this);
            controller  = sys.getController();
            authService = sys.getAuthService();
        } catch (IOException e) {
            Toast.makeText(this, "System error", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        drawerLayout    = findViewById(R.id.drawerLayout);
        tvSectionTitle  = findViewById(R.id.tvSectionTitle);
        tvNotifBadge    = findViewById(R.id.tvNotifBadge);
        layoutDashboard = findViewById(R.id.layoutDashboard);
        layoutResult    = findViewById(R.id.layoutResult);
        listContainer   = findViewById(R.id.listContainer);
        txtResult       = findViewById(R.id.txtResult);
        tvStatUsers     = findViewById(R.id.tvStatUsers);
        tvStatPending   = findViewById(R.id.tvStatPending);
        tvStatRequests  = findViewById(R.id.tvStatRequests);
        tvStatStock     = findViewById(R.id.tvStatStock);

        // Toolbar
        findViewById(R.id.btnMenu).setOnClickListener(
                v -> drawerLayout.openDrawer(Gravity.START));
        tvNotifBadge.setOnClickListener(
                v -> showNotifications());

        // Drawer items
        findViewById(R.id.navDashboard).setOnClickListener(
                v -> { closeDrawer(); showDashboard(); });
        findViewById(R.id.btnAllUsers).setOnClickListener(
                v -> { closeDrawer(); showAllUsers(); });
        findViewById(R.id.btnPendingUsers).setOnClickListener(
                v -> { closeDrawer(); showPendingUsers(); });
        findViewById(R.id.btnDonors).setOnClickListener(
                v -> { closeDrawer(); showDonors(); });
        findViewById(R.id.btnHospitals).setOnClickListener(
                v -> { closeDrawer(); showHospitals(); });
        findViewById(R.id.btnBloodBanks).setOnClickListener(
                v -> { closeDrawer(); showBloodBanks(); });
        findViewById(R.id.btnRequests).setOnClickListener(
                v -> { closeDrawer(); showRequests(); });
        findViewById(R.id.btnReports).setOnClickListener(
                v -> { closeDrawer(); showReports(); });
        findViewById(R.id.btnNotifications).setOnClickListener(
                v -> { closeDrawer(); showNotifications(); });
        findViewById(R.id.btnLogout).setOnClickListener(
                v -> logout());

        // Result back
        findViewById(R.id.btnBackToDashboard).setOnClickListener(
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

    // ── Dashboard ──────────────────────────────────────────────────────────

    private void showDashboard() {
        layoutDashboard.setVisibility(View.VISIBLE);
        layoutResult.setVisibility(View.GONE);
        tvSectionTitle.setText("Dashboard");
        refreshStats();
    }

    private void refreshStats() {
        int totalStock = 0;
        for (BloodBank bb : controller.getAllBloodBanks())
            for (int v : bb.getAllStock().values()) totalStock += v;

        tvStatUsers.setText(String.valueOf(authService.getAllUsers().size()));
        tvStatPending.setText(String.valueOf(authService.getPendingUsers().size()));
        tvStatRequests.setText(String.valueOf(controller.getAllEmergencyRequests().size()));
        tvStatStock.setText(String.valueOf(totalStock));

        int unread = controller.getUnreadNotificationCount("ADMIN");
        tvNotifBadge.setText(unread > 0 ? "🔔 (" + unread + ")" : "🔔");
    }

    // ── All users ──────────────────────────────────────────────────────────

    private void showAllUsers() {
        listContainer.removeAllViews();
        List<User> all = authService.getAllUsers();
        txtResult.setText("All Users (" + all.size() + ")");

        addSectionHeader("Hospitals");
        for (User u : all) if (u.getRole() == UserRole.HOSPITAL)    addUserRow(u);
        addSectionHeader("Blood Banks");
        for (User u : all) if (u.getRole() == UserRole.BLOOD_BANK)  addUserRow(u);
        addSectionHeader("Donors");
        for (User u : all) if (u.getRole() == UserRole.DONOR)       addUserRow(u);
        addSectionHeader("Admin");
        for (User u : all) if (u.getRole() == UserRole.ADMIN)       addUserRow(u);

        showResult("All Users");
    }

    // ── Pending approval ───────────────────────────────────────────────────

    private void showPendingUsers() {
        listContainer.removeAllViews();
        List<User> pending = authService.getPendingUsers();
        txtResult.setText("Pending: " + pending.size());
        if (pending.isEmpty()) {
            addText("No pending registrations.");
        } else {
            for (User u : pending) addApprovalCard(u);
        }
        showResult("Pending Approval");
    }

    // ── Donors active/inactive ─────────────────────────────────────────────

    private void showDonors() {
        listContainer.removeAllViews();
        List<Donor> donors = controller.getDonorService().getAllDonors();
        int active = 0, inactive = 0;
        for (Donor d : donors) { if (d.isAvailable()) active++; else inactive++; }
        txtResult.setText("Total: " + donors.size()
                + "  Active: " + active + "  Inactive: " + inactive);

        addSectionHeader("Active (" + active + ")");
        for (Donor d : donors) if (d.isAvailable())  addDonorRow(d, true);

        addSectionHeader("Inactive (" + inactive + ")");
        for (Donor d : donors) if (!d.isAvailable()) addDonorRow(d, false);

        showResult("Donors");
    }

    // ── Hospitals ──────────────────────────────────────────────────────────

    private void showHospitals() {
        listContainer.removeAllViews();
        List<Hospital> hospitals = controller.getAllHospitals();
        txtResult.setText("Hospitals (" + hospitals.size() + ")");
        for (Hospital h : hospitals) {
            int reqs = 0;
            for (EmergencyRequest er : controller.getAllEmergencyRequests().values())
                if (er.getHospitalId().equals(h.getId())) reqs++;
            addInfoCard(h.getName() + "  [" + h.getId() + "]",
                    "Location: " + controller.getLocationName(h.getLocationId()) + "  |  Requests: " + reqs);
        }
        showResult("Hospitals");
    }

    // ── Blood banks ────────────────────────────────────────────────────────

    private void showBloodBanks() {
        listContainer.removeAllViews();
        List<BloodBank> banks = controller.getAllBloodBanks();
        txtResult.setText("Blood Banks (" + banks.size() + ")");
        for (BloodBank bb : banks) {
            int total = 0;
            for (int v : bb.getAllStock().values()) total += v;
            StringBuilder stock = new StringBuilder();
            for (Map.Entry<String, Integer> e : bb.getAllStock().entrySet())
                stock.append(e.getKey()).append(":").append(e.getValue()).append("  ");
            addInfoCard(bb.getName() + "  [" + bb.getId() + "]",
                    "Total: " + total + " units  |  " + stock);
        }
        showResult("Blood Banks");
    }

    // ── Requests ───────────────────────────────────────────────────────────

    private void showRequests() {
        listContainer.removeAllViews();
        Map<String, EmergencyRequest> reqs = controller.getAllEmergencyRequests();
        int completed = 0, pending = 0;
        for (EmergencyRequest er : reqs.values()) {
            if (er.getStatus() == RequestStatus.COMPLETED) completed++;
            else if (er.getStatus() == RequestStatus.PENDING) pending++;
        }
        txtResult.setText("Total: " + reqs.size()
                + "  Completed: " + completed + "  Pending: " + pending);
        if (reqs.isEmpty()) {
            addText("No requests yet.");
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
            for (EmergencyRequest er : reqs.values()) {
                Hospital h = controller.getHospital(er.getHospitalId());
                String timeStr = sdf.format(new Date(er.getTimestamp()));
                String detail = (h != null ? h.getName() : er.getHospitalId()) + "  |  " + timeStr;
                String fulfiller = controller.getFulfillerDisplay(er);
                if (fulfiller != null) {
                    detail += "\nFulfilled by: " + fulfiller;
                }
                addInfoCard(er.getId() + "  " + er.getBloodGroup()
                                + " x" + er.getQuantity() + "  [" + er.getStatus() + "]",
                        detail);
            }
        }
        showResult("All Requests");
    }

    // ── Reports ────────────────────────────────────────────────────────────

    private void showReports() {
        int totalStock = 0, lowBanks = 0, completed = 0, activeDonors = 0;
        for (BloodBank bb : controller.getAllBloodBanks()) {
            boolean hasLow = false;
            for (int v : bb.getAllStock().values()) {
                totalStock += v;
                if (v <= 3) hasLow = true;
            }
            if (hasLow) lowBanks++;
        }
        for (EmergencyRequest er : controller.getAllEmergencyRequests().values())
            if (er.getStatus() == RequestStatus.COMPLETED) completed++;
        for (Donor d : controller.getDonorService().getAllDonors())
            if (d.isAvailable()) activeDonors++;

        String report =
                "Total Users:       " + authService.getAllUsers().size() + "\n"
              + "Pending Approval:  " + authService.getPendingUsers().size() + "\n\n"
              + "Hospitals:         " + controller.getAllHospitals().size() + "\n"
              + "Blood Banks:       " + controller.getAllBloodBanks().size() + "\n"
              + "Total Donors:      " + controller.getDonorService().getAllDonors().size() + "\n"
              + "Active Donors:     " + activeDonors + "\n\n"
              + "Total Requests:    " + controller.getAllEmergencyRequests().size() + "\n"
              + "Completed:         " + completed + "\n\n"
              + "Total Blood Stock: " + totalStock + " units\n"
              + "Low Stock Banks:   " + lowBanks;
        showTextResult("System Report", report);
    }

    // ── Notifications ──────────────────────────────────────────────────────

    private void showNotifications() {
        closeDrawer();
        List<AppNotification> notifs = controller.getNotifications("ADMIN");
        listContainer.removeAllViews();
        txtResult.setText("Notification History");
        
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
        
        if (notifs.isEmpty()) {
            addText("No notifications.");
        } else {
            // Show latest first
            for (int i = notifs.size() - 1; i >= 0; i--) {
                AppNotification n = notifs.get(i);
                String timeStr = sdf.format(new Date(n.getTimestamp()));
                addInfoCard((n.isRead() ? "" : "[NEW] ") + n.getTitle(),
                        n.getMessage() + "\n" + timeStr);
            }
            controller.markNotificationsRead("ADMIN");
        }
        showResult("Notifications");
        tvNotifBadge.setText("🔔");
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private void addApprovalCard(User user) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(24, 16, 24, 16);
        card.setBackgroundResource(R.drawable.card_bg);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, 0, 0, 14);
        card.setLayoutParams(p);

        TextView tvInfo = new TextView(this);
        tvInfo.setText(user.getUsername() + "\nRole: " + user.getRole()
                + "  |  ID: " + user.getLinkedId());
        tvInfo.setTextSize(14);
        card.addView(tvInfo);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 8, 0, 0);

        Button btnApprove = new Button(this);
        btnApprove.setText("Approve");
        btnApprove.setBackgroundResource(R.drawable.btn_accept);
        btnApprove.setTextColor(0xFFFFFFFF);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        lp.setMargins(0, 0, 8, 0);
        btnApprove.setLayoutParams(lp);
        btnApprove.setOnClickListener(v -> {
            authService.approve(user.getId());
            controller.notifyRegistrationApproved(user.getId(), user.getUsername());
            Toast.makeText(this, user.getUsername() + " approved", Toast.LENGTH_SHORT).show();
            showPendingUsers();
            refreshStats();
        });

        Button btnReject = new Button(this);
        btnReject.setText("Reject");
        btnReject.setBackgroundResource(R.drawable.btn_reject);
        btnReject.setTextColor(0xFFFFFFFF);
        btnReject.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        btnReject.setOnClickListener(v -> {
            controller.notifyRegistrationRejected(user.getUsername());
            authService.reject(user.getId());
            Toast.makeText(this, user.getUsername() + " rejected", Toast.LENGTH_SHORT).show();
            showPendingUsers();
            refreshStats();
        });

        row.addView(btnApprove);
        row.addView(btnReject);
        card.addView(row);
        listContainer.addView(card);
    }

    private void addDonorRow(Donor d, boolean active) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(16, 12, 16, 12);
        row.setBackgroundResource(R.drawable.card_bg);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, 0, 0, 8);
        row.setLayoutParams(p);

        TextView tvInfo = new TextView(this);
        tvInfo.setText(d.getName() + "  " + d.getBloodGroup() + "  [" + d.getId() + "]");
        tvInfo.setTextSize(13);
        tvInfo.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        row.addView(tvInfo);

        TextView tvStatus = new TextView(this);
        tvStatus.setText(active ? "Active" : "Inactive");
        tvStatus.setTextColor(active ? 0xFF388E3C : 0xFF757575);
        tvStatus.setTextSize(12);
        row.addView(tvStatus);

        listContainer.addView(row);
    }

    private void addUserRow(User u) {
        TextView tv = new TextView(this);
        tv.setText((u.isApproved() ? "[OK]  " : "[--]  ")
                + u.getUsername() + "  " + u.getRole() + "  " + u.getLinkedId());
        tv.setTextSize(13);
        tv.setPadding(16, 10, 16, 10);
        listContainer.addView(tv);
    }

    private void addInfoCard(String title, String detail) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(20, 14, 20, 14);
        card.setBackgroundResource(R.drawable.card_bg);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, 0, 0, 10);
        card.setLayoutParams(p);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setTextSize(14);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        card.addView(tvTitle);

        if (detail != null && !detail.isEmpty()) {
            TextView tvDetail = new TextView(this);
            tvDetail.setText(detail);
            tvDetail.setTextSize(12);
            tvDetail.setTextColor(0xFF757575);
            tvDetail.setPadding(0, 4, 0, 0);
            card.addView(tvDetail);
        }
        listContainer.addView(card);
    }

    private void addSectionHeader(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(12);
        tv.setTypeface(null, android.graphics.Typeface.BOLD);
        tv.setTextColor(0xFF4A148C);
        tv.setPadding(4, 14, 4, 6);
        listContainer.addView(tv);
    }

    private void addText(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(16, 12, 16, 12);
        tv.setTextSize(14);
        listContainer.addView(tv);
    }

    private void showTextResult(String title, String body) {
        listContainer.removeAllViews();
        addText(body);
        showResult(title);
    }

    private void showResult(String title) {
        tvSectionTitle.setText(title);
        layoutResult.setVisibility(View.VISIBLE);
        layoutDashboard.setVisibility(View.GONE);
    }

    private void logout() {
        startActivity(new Intent(this, LoginActivity.class));
        finishAffinity();
    }
}
