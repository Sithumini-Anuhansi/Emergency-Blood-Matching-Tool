package com.bloodnetwork.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bloodnetwork.app.R;
import com.bloodnetwork.app.model.BloodBank;
import com.bloodnetwork.app.model.Donor;
import com.bloodnetwork.app.model.Hospital;
import com.bloodnetwork.app.model.User;
import com.bloodnetwork.app.model.UserRole;
import com.bloodnetwork.app.service.AuthService;
import com.bloodnetwork.app.service.BloodBankService;
import com.bloodnetwork.app.service.DonorService;
import com.bloodnetwork.app.service.HospitalService;
import com.bloodnetwork.app.system.EmergencyBloodSystem;

import java.io.IOException;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private RadioGroup rgRole;
    private LinearLayout layoutHospital, layoutDonor, layoutBloodBank;

    // Hospital fields
    private EditText etHospName, etHospLocation, etHospPhone, etHospEmail, etHospUser, etHospPass;
    // Donor fields
    private EditText etDonorName, etDonorAge, etDonorDistrict, etDonorWeight,
            etDonorHemo, etDonorPhone, etDonorUser, etDonorPass;
    private Spinner spnBloodGroup;
    // Blood bank fields
    private EditText etBankName, etBankLocation, etBankPhone, etBankUser, etBankPass;
    private Button btnRegister;

    private AuthService authService;
    private HospitalService hospitalService;
    private DonorService donorService;
    private BloodBankService bloodBankService;
    private EmergencyBloodSystem system;

    private static final String[] BLOOD_GROUPS = {"A+","A-","B+","B-","O+","O-","AB+","AB-"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        try {
            system           = EmergencyBloodSystem.getInstance(this);
            authService      = system.getAuthService();
            hospitalService  = system.getController().getHospitalService();
            donorService     = system.getController().getDonorService();
            bloodBankService = system.getController().getBloodBankService();
        } catch (IOException e) {
            Toast.makeText(this, "System error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        rgRole          = findViewById(R.id.rgRole);
        layoutHospital  = findViewById(R.id.layoutHospital);
        layoutDonor     = findViewById(R.id.layoutDonor);
        layoutBloodBank = findViewById(R.id.layoutBloodBank);

        etHospName     = findViewById(R.id.etHospName);
        etHospLocation = findViewById(R.id.etHospLocation);
        etHospPhone    = findViewById(R.id.etHospPhone);
        etHospEmail    = findViewById(R.id.etHospEmail);
        etHospUser     = findViewById(R.id.etHospUser);
        etHospPass     = findViewById(R.id.etHospPass);

        etDonorName     = findViewById(R.id.etDonorName);
        etDonorAge      = findViewById(R.id.etDonorAge);
        etDonorDistrict = findViewById(R.id.etDonorDistrict);
        etDonorWeight   = findViewById(R.id.etDonorWeight);
        etDonorHemo     = findViewById(R.id.etDonorHemo);
        etDonorPhone    = findViewById(R.id.etDonorPhone);
        etDonorUser     = findViewById(R.id.etDonorUser);
        etDonorPass     = findViewById(R.id.etDonorPass);
        spnBloodGroup   = findViewById(R.id.spnBloodGroup);

        etBankName     = findViewById(R.id.etBankName);
        etBankLocation = findViewById(R.id.etBankLocation);
        etBankPhone    = findViewById(R.id.etBankPhone);
        etBankUser     = findViewById(R.id.etBankUser);
        etBankPass     = findViewById(R.id.etBankPass);

        btnRegister = findViewById(R.id.btnRegister);

        spnBloodGroup.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, BLOOD_GROUPS));

        rgRole.setOnCheckedChangeListener((group, checkedId) -> updateVisibility(checkedId));
        updateVisibility(rgRole.getCheckedRadioButtonId());

        btnRegister.setOnClickListener(v -> register());
    }

    private void updateVisibility(int checkedId) {
        layoutHospital.setVisibility(View.GONE);
        layoutDonor.setVisibility(View.GONE);
        layoutBloodBank.setVisibility(View.GONE);
        if (checkedId == R.id.rbHospital)       layoutHospital.setVisibility(View.VISIBLE);
        else if (checkedId == R.id.rbDonor)     layoutDonor.setVisibility(View.VISIBLE);
        else if (checkedId == R.id.rbBloodBank) layoutBloodBank.setVisibility(View.VISIBLE);
    }

    private void register() {
        int selected = rgRole.getCheckedRadioButtonId();
        if (selected == -1) {
            Toast.makeText(this, "Select a role", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selected == R.id.rbHospital)       registerHospital();
        else if (selected == R.id.rbDonor)     registerDonor();
        else                                    registerBloodBank();
    }

    private void registerHospital() {
        String name = etHospName.getText().toString().trim();
        String loc  = etHospLocation.getText().toString().trim();
        String user = etHospUser.getText().toString().trim();
        String pass = etHospPass.getText().toString().trim();

        if (name.isEmpty() || loc.isEmpty() || user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String hospId = "H" + (hospitalService.getAllHospitals().size() + 100);
        String locId  = findLocationId(loc);
        hospitalService.addHospital(new Hospital(hospId, name, locId));

        User u = authService.register(user, pass, UserRole.HOSPITAL, hospId);
        if (u == null) {
            Toast.makeText(this, "Username already taken", Toast.LENGTH_SHORT).show();
            return;
        }
        showSuccess(user, "Hospital");
    }

    private void registerDonor() {
        String name     = etDonorName.getText().toString().trim();
        String ageStr   = etDonorAge.getText().toString().trim();
        String district = etDonorDistrict.getText().toString().trim();
        String user     = etDonorUser.getText().toString().trim();
        String pass     = etDonorPass.getText().toString().trim();
        String phone    = etDonorPhone.getText().toString().trim();
        String bg       = (String) spnBloodGroup.getSelectedItem();

        if (name.isEmpty() || ageStr.isEmpty() || district.isEmpty()
                || user.isEmpty() || pass.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age < 18 || age > 65) {
                Toast.makeText(this, "Age must be between 18 and 65", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter a valid age", Toast.LENGTH_SHORT).show();
            return;
        }

        String donorId = "D" + (donorService.getAllDonors().size() + 1);
        String locId   = findLocationId(district);
        donorService.addDonor(new Donor(donorId, name, bg, locId, phone, age, true));

        User u = authService.register(user, pass, UserRole.DONOR, donorId);
        if (u == null) {
            Toast.makeText(this, "Username already taken", Toast.LENGTH_SHORT).show();
            return;
        }
        showSuccess(user, "Donor");
    }

    private void registerBloodBank() {
        String name = etBankName.getText().toString().trim();
        String loc  = etBankLocation.getText().toString().trim();
        String phone = etBankPhone.getText().toString().trim();
        String user = etBankUser.getText().toString().trim();
        String pass = etBankPass.getText().toString().trim();

        if (name.isEmpty() || loc.isEmpty() || user.isEmpty() || pass.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String bankId = "BB" + (bloodBankService.getAllBloodBanks().size() + 100);
        String locId  = findLocationId(loc);
        bloodBankService.addBloodBank(new BloodBank(bankId, name, locId, phone));

        User u = authService.register(user, pass, UserRole.BLOOD_BANK, bankId);
        if (u == null) {
            Toast.makeText(this, "Username already taken", Toast.LENGTH_SHORT).show();
            return;
        }
        showSuccess(user, "Blood Bank");
    }

    /** Maps a city name or location ID string to the graph location ID. */
    private String findLocationId(String input) {
        String lower = input.toLowerCase().trim();
        // Direct ID match (e.g. "L001")
        if (lower.matches("l\\d+")) return input.toUpperCase();

        // Use graph for more robust lookup
        if (system != null && system.getController() != null) {
            com.bloodnetwork.app.graph.Graph graph = system.getController().getGraph();
            if (graph != null) {
                for (com.bloodnetwork.app.model.Location loc : graph.getAllLocations().values()) {
                    if (loc.getName().toLowerCase().equals(lower)) {
                        return loc.getId();
                    }
                }
            }
        }

        // City name lookup (fallback if graph lookup fails or partial match needed)
        if (lower.contains("kandy"))         return "L001";
        if (lower.contains("colombo"))       return "L002";
        if (lower.contains("anuradhapura"))  return "L003";
        if (lower.contains("matara"))        return "L004";
        if (lower.contains("gampaha"))       return "L005";
        if (lower.contains("negombo"))       return "L006";
        if (lower.contains("jaffna"))        return "L007";
        if (lower.contains("kurunegala"))    return "L008";
        if (lower.contains("galle"))         return "L009";
        if (lower.contains("kalutara"))      return "L010";
        return "L001"; // default fallback
    }

    private void showSuccess(String username, String role) {
        try {
            system.getController().notifyAdminNewRegistration(username, role);
        } catch (Exception ignored) {}
        Toast.makeText(this, "Registration submitted. Awaiting admin approval.",
                Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
