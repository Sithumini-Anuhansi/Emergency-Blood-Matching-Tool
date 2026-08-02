package com.bloodnetwork.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import androidx.core.view.GravityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bloodnetwork.app.R;
import com.bloodnetwork.app.controller.EmergencyController;
import com.bloodnetwork.app.model.AppNotification;
import com.bloodnetwork.app.model.BloodBank;
import com.bloodnetwork.app.model.StockTransaction;
import com.bloodnetwork.app.system.EmergencyBloodSystem;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BloodBankActivity extends AppCompatActivity {

    private EmergencyController controller;
    private DrawerLayout drawerLayout;

    private TextView tvBankName, tvDrawerBankName, tvSectionTitle, txtResult;
    private TextView tvStatTotalStock, tvStatLowStock, tvStatIssued, tvStatAdded;
    private LinearLayout layoutDashboard, layoutResult, listContainer, layoutRequests, requestsContainer;
    private Button tvNotifBadge;

    private BloodBank currentBank;
    private String bankId, userId;

    private static final String[] BLOOD_GROUPS = {"A+","A-","B+","B-","O+","O-","AB+","AB-"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloodbank_home);

        bankId = getIntent().getStringExtra("bankId");
        userId = getIntent().getStringExtra("userId");

        try {
            controller = EmergencyBloodSystem.getInstance(this).getController();
        } catch (IOException e) {
            Toast.makeText(this, "System error", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (bankId != null && !bankId.isEmpty()) currentBank = controller.getBloodBank(bankId);
        if (currentBank == null) {
            List<BloodBank> all = controller.getAllBloodBanks();
            if (!all.isEmpty()) { currentBank = all.get(0); bankId = currentBank.getId(); }
        }

        drawerLayout     = findViewById(R.id.drawerLayout);
        tvBankName       = findViewById(R.id.tvBankName);
        tvDrawerBankName = findViewById(R.id.tvDrawerBankName);
        tvSectionTitle   = findViewById(R.id.tvSectionTitle);
        tvNotifBadge     = findViewById(R.id.tvNotifBadge);
        layoutDashboard  = findViewById(R.id.layoutDashboard);
        layoutResult     = findViewById(R.id.layoutResult);
        listContainer    = findViewById(R.id.listContainer);
        layoutRequests   = findViewById(R.id.layoutRequests);
        requestsContainer = findViewById(R.id.requestsContainer);
        txtResult        = findViewById(R.id.txtResult);
        tvStatTotalStock = findViewById(R.id.tvStatTotalStock);
        tvStatLowStock   = findViewById(R.id.tvStatLowStock);
        tvStatIssued     = findViewById(R.id.tvStatIssued);
        tvStatAdded      = findViewById(R.id.tvStatAdded);

        String name = currentBank != null ? currentBank.getName() : "Blood Bank";
        tvBankName.setText(name);
        tvDrawerBankName.setText(name);

        // Toolbar
        findViewById(R.id.btnMenu).setOnClickListener(
                v -> drawerLayout.openDrawer(GravityCompat.START));
        tvNotifBadge.setOnClickListener(
                v -> goToLatestNotificationTarget());

        // Drawer items
        findViewById(R.id.navDashboard).setOnClickListener(
                v -> { closeDrawer(); showDashboard(); });
        findViewById(R.id.navStock).setOnClickListener(
                v -> { closeDrawer(); showStock(); });
        findViewById(R.id.navRequests).setOnClickListener(
                v -> { closeDrawer(); showRequests(); });
        findViewById(R.id.btnTransactions).setOnClickListener(
                v -> { closeDrawer(); showTransactions(); });
        findViewById(R.id.btnNotifications).setOnClickListener(
                v -> { closeDrawer(); showNotifications(); });
        findViewById(R.id.btnProfile).setOnClickListener(
                v -> { closeDrawer(); showProfile(); });
        findViewById(R.id.btnLogout).setOnClickListener(
                v -> logout());

        // Dashboard quick button + result back
        findViewById(R.id.btnViewStock).setOnClickListener(v -> showStock());
        findViewById(R.id.btnViewRequests).setOnClickListener(v -> showRequests());
        findViewById(R.id.btnBackToDashboard).setOnClickListener(v -> showDashboard());
        findViewById(R.id.btnBackFromRequests).setOnClickListener(v -> showDashboard());
        findViewById(R.id.cardStatTotal).setOnClickListener(v -> showStock());
        findViewById(R.id.cardStatLow).setOnClickListener(v -> showStock());
        findViewById(R.id.cardStatIssued).setOnClickListener(v -> showTransactions());
        findViewById(R.id.cardStatAdded).setOnClickListener(v -> showTransactions());

        showDashboard();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (controller == null) return;
        if (layoutRequests.getVisibility() == View.VISIBLE) {
            refreshRequests();
        } else if (layoutDashboard.getVisibility() == View.VISIBLE) {
            refreshStats();
        }
        // layoutResult (stock/transactions/notifications/profile) is a shared
        // panel keyed by whatever was tapped last, so it's left as-is here to
        // avoid re-triggering the wrong section; re-tap the drawer item to refresh it.
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (layoutDashboard.getVisibility() != View.VISIBLE) {
            showDashboard();
        } else {
            super.onBackPressed();
        }
    }

    private void closeDrawer() { drawerLayout.closeDrawer(GravityCompat.START); }

    // ── Requests ──────────────────────────────────────────────────────────

    private void showRequests() {
        layoutDashboard.setVisibility(View.GONE);
        layoutResult.setVisibility(View.GONE);
        layoutRequests.setVisibility(View.VISIBLE);
        tvSectionTitle.setText("Emergency Requests");
        refreshRequests();
    }

    private void refreshRequests() {
        requestsContainer.removeAllViews();
        Map<String, com.bloodnetwork.app.model.EmergencyRequest> requests = controller.getAllEmergencyRequests();
        boolean found = false;

        for (com.bloodnetwork.app.model.EmergencyRequest er : requests.values()) {
            if (er.getStatus() == com.bloodnetwork.app.model.RequestStatus.PENDING) {
                addRequestCard(er);
                found = true;
            }
        }

        if (!found) {
            TextView tv = new TextView(this);
            tv.setText("No pending emergency requests.");
            tv.setPadding(20, 20, 20, 20);
            requestsContainer.addView(tv);
        }
    }

    private void addRequestCard(com.bloodnetwork.app.model.EmergencyRequest er) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(24, 20, 24, 20);
        card.setBackgroundResource(R.drawable.card_bg);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, 0, 0, 16);
        card.setLayoutParams(p);

        com.bloodnetwork.app.model.Hospital hospital = controller.getHospital(er.getHospitalId());
        String hospitalName = (hospital != null) ? hospital.getName() : "Unknown Hospital";

        TextView tvTitle = new TextView(this);
        tvTitle.setText(hospitalName);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitle.setTextSize(16);
        card.addView(tvTitle);

        TextView tvDetails = new TextView(this);
        tvDetails.setText("Needs " + er.getBloodGroup() + " (x" + er.getQuantity() + " units)");
        tvDetails.setTextSize(14);
        tvDetails.setPadding(0, 4, 0, 4);
        card.addView(tvDetails);

        TextView tvTime = new TextView(this);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
        tvTime.setText("Requested: " + sdf.format(new Date(er.getTimestamp())));
        tvTime.setTextSize(12);
        tvTime.setTextColor(0xFF757575);
        tvTime.setPadding(0, 0, 0, 8);
        card.addView(tvTime);

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        actions.setGravity(Gravity.END);

        Button btnAccept = new Button(this);
        btnAccept.setText("Fulfill");
        btnAccept.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF2E7D32));
        btnAccept.setOnClickListener(v -> handleBankAccept(er));
        actions.addView(btnAccept);

        card.addView(actions);
        requestsContainer.addView(card);
    }

    private void handleBankAccept(com.bloodnetwork.app.model.EmergencyRequest er) {
        if (currentBank == null) return;
        
        if (!currentBank.hasBlood(er.getBloodGroup(), er.getQuantity())) {
            Toast.makeText(this, "Insufficient stock for " + er.getBloodGroup(), Toast.LENGTH_LONG).show();
            return;
        }

        new android.app.AlertDialog.Builder(this)
                .setTitle("Confirm Fulfillment")
                .setMessage("Are you sure you want to fulfill " + er.getBloodGroup() + 
                           " x" + er.getQuantity() + " for " + er.getHospitalId() + "?")
                .setPositiveButton("Yes, Issue Blood", (d, w) -> {
                    boolean success = controller.bankAccept(er.getId(), bankId);
                    if (success) {
                        Toast.makeText(this, "Request fulfilled successfully", Toast.LENGTH_SHORT).show();
                        showDashboard();
                    } else {
                        Toast.makeText(this, "Failed to fulfill request", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ── Dashboard ──────────────────────────────────────────────────────────

    private void showDashboard() {
        layoutDashboard.setVisibility(View.VISIBLE);
        layoutResult.setVisibility(View.GONE);
        layoutRequests.setVisibility(View.GONE);
        tvSectionTitle.setText("Home");
        refreshStats();
    }

    private void refreshStats() {
        if (currentBank == null) return;
        int total = 0, low = 0, issued = 0, added = 0;
        for (Map.Entry<String, Integer> e : currentBank.getAllStock().entrySet()) {
            total += e.getValue();
            if (e.getValue() <= 3) low++;
        }
        for (StockTransaction t : controller.getTransactionsForBank(bankId)) {
            if ("ISSUE".equals(t.getType())) issued += t.getQuantity();
            else added += t.getQuantity();
        }
        tvStatTotalStock.setText(String.valueOf(total));
        tvStatLowStock.setText(String.valueOf(low));
        tvStatIssued.setText(String.valueOf(issued));
        tvStatAdded.setText(String.valueOf(added));

        int unread = controller.getUnreadNotificationCount(bankId);
        tvNotifBadge.setText(unread > 0 ? "🔔 (" + unread + ")" : "🔔");
    }

    // ── Stock ──────────────────────────────────────────────────────────────

    private void showStock() {
        tvSectionTitle.setText("Blood Stock");
        txtResult.setText("Current Stock — " + (currentBank != null ? currentBank.getName() : ""));
        listContainer.removeAllViews();
        if (currentBank != null) {
            for (String bg : BLOOD_GROUPS) addStockCard(bg, currentBank.getStock(bg));
        }
        layoutResult.setVisibility(View.VISIBLE);
        layoutDashboard.setVisibility(View.GONE);
        layoutRequests.setVisibility(View.GONE);
    }

    private void addStockCard(String bloodGroup, int units) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setPadding(24, 20, 24, 20);
        card.setBackgroundResource(units <= 3 ? R.drawable.card_bg_warning : R.drawable.card_bg);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, 0, 0, 10);
        card.setLayoutParams(p);

        TextView tvBg = new TextView(this);
        tvBg.setText(bloodGroup);
        tvBg.setTextSize(17);
        tvBg.setTypeface(null, android.graphics.Typeface.BOLD);
        tvBg.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        card.addView(tvBg);

        TextView tvUnits = new TextView(this);
        tvUnits.setText(units + " units");
        tvUnits.setTextSize(14);
        tvUnits.setTextColor(units <= 3 ? 0xFFC62828 : 0xFF212121);
        tvUnits.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        card.addView(tvUnits);

        Button btnAdd = new Button(this);
        btnAdd.setText("+");
        btnAdd.setTextColor(0xFF388E3C);
        btnAdd.setBackground(null);
        btnAdd.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        btnAdd.setOnClickListener(v -> promptAdjust(bloodGroup, true, tvUnits));
        card.addView(btnAdd);

        Button btnSub = new Button(this);
        btnSub.setText("-");
        btnSub.setTextColor(0xFFC62828);
        btnSub.setBackground(null);
        btnSub.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        btnSub.setOnClickListener(v -> promptAdjust(bloodGroup, false, tvUnits));
        card.addView(btnSub);

        listContainer.addView(card);
    }

    private void promptAdjust(String bg, boolean isAdd, TextView tvUnits) {
        android.app.AlertDialog.Builder b = new android.app.AlertDialog.Builder(this);
        b.setTitle((isAdd ? "Add" : "Remove") + " stock: " + bg);
        EditText input = new EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        input.setHint("Quantity");
        b.setView(input);
        b.setPositiveButton("Save", (d, w) -> {
            try {
                int qty = Integer.parseInt(input.getText().toString().trim());
                if (qty <= 0) throw new NumberFormatException();
                if (isAdd) {
                    controller.addStock(bankId, bg, qty);
                    Toast.makeText(this, qty + " units added", Toast.LENGTH_SHORT).show();
                } else {
                    if (currentBank.getStock(bg) < qty) {
                        Toast.makeText(this, "Not enough stock", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    controller.reduceStock(bankId, bg, qty);
                    Toast.makeText(this, qty + " units removed", Toast.LENGTH_SHORT).show();
                }
                int updated = currentBank.getStock(bg);
                tvUnits.setText(updated + " units");
                tvUnits.setTextColor(updated <= 3 ? 0xFFC62828 : 0xFF212121);
                refreshStats();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Enter a valid number", Toast.LENGTH_SHORT).show();
            }
        });
        b.setNegativeButton("Cancel", null);
        b.show();
    }

    // ── Other screens ──────────────────────────────────────────────────────

    private void showTransactions() {
        StringBuilder sb = new StringBuilder();
        List<StockTransaction> transactions = controller.getTransactionsForBank(bankId);
        if (transactions.isEmpty()) {
            sb.append("No transactions yet.");
        } else {
            for (StockTransaction t : transactions)
                sb.append(t.getType()).append("  ")
                  .append(t.getQuantity()).append(" units  ")
                  .append(t.getBloodGroup()).append("\n");
        }
        showTextResult("Transactions", sb.toString());
    }

    private void goToLatestNotificationTarget() {
        List<AppNotification> notifs = controller.getNotifications(bankId);
        for (int i = notifs.size() - 1; i >= 0; i--) {
            AppNotification n = notifs.get(i);
            if (!n.isRead() && (n.getTitle().contains("EMERGENCY") || n.getTitle().contains("Request:"))) {
                showRequests();
                return;
            }
        }
        showNotifications();
    }

    private void showNotifications() {
        closeDrawer();
        layoutDashboard.setVisibility(View.GONE);
        layoutResult.setVisibility(View.VISIBLE);
        layoutRequests.setVisibility(View.GONE);
        tvSectionTitle.setText("Notifications");
        txtResult.setText("Notification History");
        listContainer.removeAllViews();
        
        List<AppNotification> notifs = controller.getNotifications(bankId);
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

                // Make emergency notifications clickable
                if (n.getTitle().contains("EMERGENCY") || n.getTitle().contains("Request:")) {
                    card.setClickable(true);
                    card.setFocusable(true);
                    android.util.TypedValue outValue = new android.util.TypedValue();
                    getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                    card.setForeground(androidx.appcompat.content.res.AppCompatResources.getDrawable(this, outValue.resourceId));
                    card.setOnClickListener(v -> showRequests());
                }

                listContainer.addView(card);
            }
            controller.markNotificationsRead(bankId);
        }
        tvNotifBadge.setText("🔔");
    }

    private void showProfile() {
        layoutRequests.setVisibility(View.GONE);
        String info = currentBank != null
                ? "Name:     " + currentBank.getName()
                + "\nLocation: " + controller.getLocationName(currentBank.getLocationId())
                + "\nPhone:    " + currentBank.getPhone()
                : "No data.";
        showTextResult("Profile", info);
    }

    private void showTextResult(String title, String body) {
        tvSectionTitle.setText(title);
        txtResult.setText(title);
        listContainer.removeAllViews();
        TextView tv = new TextView(this);
        tv.setText(body);
        tv.setPadding(4, 8, 4, 8);
        tv.setTextSize(14);
        listContainer.addView(tv);
        layoutResult.setVisibility(View.VISIBLE);
        layoutDashboard.setVisibility(View.GONE);
        layoutRequests.setVisibility(View.GONE);
    }

    private void logout() {
        startActivity(new Intent(this, LoginActivity.class));
        finishAffinity();
    }
}
