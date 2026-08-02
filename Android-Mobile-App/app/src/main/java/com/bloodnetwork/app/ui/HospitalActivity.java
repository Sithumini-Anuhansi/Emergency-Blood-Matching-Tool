package com.bloodnetwork.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bloodnetwork.app.R;
import com.bloodnetwork.app.controller.EmergencyController;
import com.bloodnetwork.app.model.AppNotification;
import com.bloodnetwork.app.model.BloodBank;
import com.bloodnetwork.app.model.DonorMatch;
import com.bloodnetwork.app.model.DonorRequest;
import com.bloodnetwork.app.model.EmergencyRequest;
import com.bloodnetwork.app.model.Hospital;
import com.bloodnetwork.app.model.RequestPriority;
import com.bloodnetwork.app.model.RequestStatus;
import com.bloodnetwork.app.model.RouteResult;
import com.bloodnetwork.app.system.EmergencyBloodSystem;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HospitalActivity extends AppCompatActivity {

    private EmergencyController controller;
    private DrawerLayout drawerLayout;
    private TextView tvHospitalName, tvSectionTitle, txtResult;
    private LinearLayout layoutDashboard, layoutCreateRequest, layoutResult, listContainer;
    private Spinner spnBloodGroup;
    private EditText etQuantity;
    private RadioGroup rgPriority;
    private Button btnSubmitRequest;

    private Hospital currentHospital;
    private String hospitalId, userId;
    private EmergencyRequest currentRequest;

    private static final String[] BLOOD_GROUPS = {"A+","A-","B+","B-","O+","O-","AB+","AB-"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_home);

        hospitalId = getIntent().getStringExtra("hospitalId");
        userId     = getIntent().getStringExtra("userId");

        try {
            controller = EmergencyBloodSystem.getInstance(this).getController();
        } catch (IOException e) {
            Toast.makeText(this, "Failed to load system", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (hospitalId == null || hospitalId.isEmpty()) {
            List<Hospital> all = controller.getAllHospitals();
            if (!all.isEmpty()) {
                currentHospital = all.get(0);
                hospitalId = currentHospital.getId();
            }
        } else {
            currentHospital = controller.getHospital(hospitalId);
        }

        tvHospitalName      = findViewById(R.id.tvHospitalName);
        tvSectionTitle      = findViewById(R.id.tvSectionTitle);
        layoutDashboard     = findViewById(R.id.layoutDashboard);
        layoutCreateRequest = findViewById(R.id.layoutCreateRequest);
        layoutResult        = findViewById(R.id.layoutResult);
        listContainer       = findViewById(R.id.listContainer);
        txtResult           = findViewById(R.id.txtResult);
        spnBloodGroup       = findViewById(R.id.spnBloodGroup);
        etQuantity          = findViewById(R.id.etQuantity);
        rgPriority          = findViewById(R.id.rgPriority);
        btnSubmitRequest    = findViewById(R.id.btnSubmitRequest);

        tvHospitalName.setText(currentHospital != null ? currentHospital.getName() : "Hospital");
        TextView tvDrawerName = findViewById(R.id.tvDrawerHospitalName);
        if (tvDrawerName != null && currentHospital != null) {
            tvDrawerName.setText(currentHospital.getName());
        }

        drawerLayout = findViewById(R.id.drawerLayout);
        findViewById(R.id.btnMenu).setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        findViewById(R.id.tvNotifBadge).setOnClickListener(v -> showNotifications());

        spnBloodGroup.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, BLOOD_GROUPS));

        showDashboard();

        // Dashboard Buttons
        findViewById(R.id.btnCreateRequest).setOnClickListener(v -> showCreateRequest());
        findViewById(R.id.cardStatTotal).setOnClickListener(v -> showRequestHistory());
        findViewById(R.id.cardStatCompleted).setOnClickListener(v -> showRequestHistory());
        findViewById(R.id.cardStatPending).setOnClickListener(v -> showRequestHistory());
        findViewById(R.id.cardStatBanks).setOnClickListener(v -> showBloodBanks());

        // Drawer Navigation Items
        findViewById(R.id.navDashboard).setOnClickListener(v -> {
            showDashboard();
            drawerLayout.closeDrawers();
        });
        findViewById(R.id.navCreateRequest).setOnClickListener(v -> {
            showCreateRequest();
            drawerLayout.closeDrawers();
        });
        findViewById(R.id.btnViewBloodBanks).setOnClickListener(v -> {
            showBloodBanks();
            drawerLayout.closeDrawers();
        });
        findViewById(R.id.btnRequestHistory).setOnClickListener(v -> {
            showRequestHistory();
            drawerLayout.closeDrawers();
        });
        findViewById(R.id.btnNotifications).setOnClickListener(v -> {
            showNotifications();
            drawerLayout.closeDrawers();
        });
        findViewById(R.id.btnLogout).setOnClickListener(v -> logout());

        // Back Buttons
        findViewById(R.id.btnBackFromCreate).setOnClickListener(v -> showDashboard());
        findViewById(R.id.btnBackFromResult).setOnClickListener(v -> showDashboard());

        btnSubmitRequest.setOnClickListener(v -> submitRequest());
    }

    private void showDashboard() {
        layoutDashboard.setVisibility(View.VISIBLE);
        layoutCreateRequest.setVisibility(View.GONE);
        layoutResult.setVisibility(View.GONE);
        tvSectionTitle.setText("Dashboard");

        // Update Stats
        if (controller != null && hospitalId != null) {
            int total = 0, completed = 0, pending = 0;
            for (EmergencyRequest er : controller.getAllEmergencyRequests().values()) {
                if (hospitalId.equals(er.getHospitalId())) {
                    total++;
                    if (er.getStatus() == RequestStatus.COMPLETED) completed++;
                    else pending++;
                }
            }
            ((TextView) findViewById(R.id.tvStatTotal)).setText(String.valueOf(total));
            ((TextView) findViewById(R.id.tvStatCompleted)).setText(String.valueOf(completed));
            ((TextView) findViewById(R.id.tvStatPending)).setText(String.valueOf(pending));
            
            int unread = controller.getUnreadNotificationCount(hospitalId);
            TextView tvNotif = findViewById(R.id.tvNotifBadge);
            if (tvNotif != null) {
                tvNotif.setText(unread > 0 ? "🔔 (" + unread + ")" : "🔔");
            }
        }
    }

    private void showCreateRequest() {
        layoutDashboard.setVisibility(View.GONE);
        layoutCreateRequest.setVisibility(View.VISIBLE);
        layoutResult.setVisibility(View.GONE);
        tvSectionTitle.setText("Create Blood Request");
    }

    private void submitRequest() {
        if (currentHospital == null) return;
        String bg = (String) spnBloodGroup.getSelectedItem();
        int qty;
        try {
            qty = Integer.parseInt(etQuantity.getText().toString().trim());
            if (qty <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter a valid quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestPriority priority = rgPriority.getCheckedRadioButtonId() == R.id.rbEmergency
                ? RequestPriority.EMERGENCY : RequestPriority.NORMAL;

        EmergencyRequest er = controller.createRequest(currentHospital.getId(), bg, qty, priority);
        if (er == null) {
            showListResult("Failed to create request.", null);
            return;
        }
        currentRequest = er;

        List<com.bloodnetwork.app.model.MatchResult> matches = controller.findMatches(bg, qty, currentHospital.getLocationId());
        
        if (matches.isEmpty()) {
            showListResult("Request " + er.getId() + " | No matches", "No blood banks or donors found for " + bg + ".");
            return;
        }

        controller.processRequest(er, matches);

        StringBuilder sb = new StringBuilder("Request " + er.getId() + " created.\n");
        sb.append("Priority: ").append(priority).append("\n\n");
        sb.append("Top matches notified:\n");
        
        int count = Math.min(matches.size(), 20);
        for (int i = 0; i < count; i++) {
            com.bloodnetwork.app.model.MatchResult m = matches.get(i);
            sb.append(i + 1).append(". [").append(m.getType()).append("] ")
                    .append(m.getName()).append("  -  ")
                    .append(String.format("%.1f km", m.getDistance())).append("\n");
        }

        showListResult("Request Processing", sb.toString());
    }

    private void showBloodBanks() {
        StringBuilder sb = new StringBuilder();
        for (BloodBank bb : controller.getAllBloodBanks()) {
            sb.append(bb.getName()).append("\n");
            for (Map.Entry<String, Integer> e : bb.getAllStock().entrySet()) {
                sb.append("  ").append(e.getKey()).append(": ").append(e.getValue()).append(" units\n");
            }
            sb.append("\n");
        }
        showListResult("Blood Banks", sb.toString());
    }

    private void showRequestHistory() {
        tvSectionTitle.setText("Request History");
        txtResult.setText("Your Requests");
        listContainer.removeAllViews();
        
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
        boolean found = false;
        
        for (EmergencyRequest er : controller.getAllEmergencyRequests().values()) {
            if (er.getHospitalId().equals(hospitalId)) {
                found = true;
                addRequestCard(er, sdf);
            }
        }
        
        if (!found) {
            TextView tv = new TextView(this);
            tv.setText("No requests yet.");
            tv.setPadding(16, 12, 16, 12);
            listContainer.addView(tv);
        }
        
        layoutResult.setVisibility(View.VISIBLE);
        layoutCreateRequest.setVisibility(View.GONE);
        layoutDashboard.setVisibility(View.GONE);
    }

    private void addRequestCard(EmergencyRequest er, SimpleDateFormat sdf) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(24, 16, 24, 16);
        card.setBackgroundResource(R.drawable.card_bg);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, 0, 0, 12);
        card.setLayoutParams(p);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(er.getId() + " - " + er.getBloodGroup() + " x" + er.getQuantity());
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        card.addView(tvTitle);

        TextView tvDetail = new TextView(this);
        String timeStr = sdf.format(new Date(er.getTimestamp()));
        tvDetail.setText("Status: " + er.getStatus() + " | " + timeStr);
        tvDetail.setTextSize(12);
        tvDetail.setTextColor(0xFF757575);
        card.addView(tvDetail);

        if (er.getStatus() == RequestStatus.COMPLETED) {
            String fulfiller = controller.getFulfillerDisplay(er);
            if (fulfiller != null) {
                TextView tvFulfiller = new TextView(this);
                tvFulfiller.setText("Fulfilled by: " + fulfiller);
                tvFulfiller.setTextSize(12);
                tvFulfiller.setTextColor(0xFF2E7D32);
                tvFulfiller.setPadding(0, 4, 0, 0);
                card.addView(tvFulfiller);
            }
        }

        if (er.getStatus() == RequestStatus.PENDING) {
            card.setClickable(true);
            card.setFocusable(true);
            TypedValue outValue = new TypedValue();
            getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            card.setBackgroundResource(outValue.resourceId);
            card.setOnClickListener(v -> refreshRequestMatches(er));
            
            TextView tvHint = new TextView(this);
            tvHint.setText("(Click to check for new matches)");
            tvHint.setTextSize(10);
            tvHint.setTextColor(0xFF388E3C);
            card.addView(tvHint);
        }

        listContainer.addView(card);
    }

    private void refreshRequestMatches(EmergencyRequest er) {
        List<com.bloodnetwork.app.model.MatchResult> matches = controller.findMatches(
                er.getBloodGroup(), er.getQuantity(), currentHospital.getLocationId());
        
        controller.processRequest(er, matches);
        
        StringBuilder sb = new StringBuilder("Request " + er.getId() + " updated.\n");
        sb.append("Checked for new donors/banks.\n\n");
        sb.append("Top matches notified:\n");
        
        int count = Math.min(matches.size(), 20);
        for (int i = 0; i < count; i++) {
            com.bloodnetwork.app.model.MatchResult m = matches.get(i);
            sb.append(i + 1).append(". [").append(m.getType()).append("] ")
                    .append(m.getName()).append(" - ").append(String.format("%.1f km", m.getDistance())).append("\n");
        }
        
        showListResult("Matches Updated", sb.toString());
    }

    private void showNotifications() {
        tvSectionTitle.setText("Notifications");
        txtResult.setText("Notification History");
        listContainer.removeAllViews();
        
        List<AppNotification> notifs = controller.getNotifications(hospitalId);
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
            controller.markNotificationsRead(hospitalId);
        }
        
        TextView tvNotif = findViewById(R.id.tvNotifBadge);
        if (tvNotif != null) tvNotif.setText("🔔");

        layoutResult.setVisibility(View.VISIBLE);
        layoutCreateRequest.setVisibility(View.GONE);
        layoutDashboard.setVisibility(View.GONE);
    }

    private void showListResult(String title, String body) {
        tvSectionTitle.setText(title);
        txtResult.setText(title);
        listContainer.removeAllViews();
        if (body != null && !body.isEmpty()) {
            TextView tv = new TextView(this);
            tv.setText(body);
            tv.setPadding(16, 12, 16, 12);
            tv.setTextSize(14);
            listContainer.addView(tv);
        }
        layoutResult.setVisibility(View.VISIBLE);
        layoutCreateRequest.setVisibility(View.GONE);
        layoutDashboard.setVisibility(View.GONE);
    }

    private void logout() {
        startActivity(new Intent(this, LoginActivity.class));
        finishAffinity();
    }
}
