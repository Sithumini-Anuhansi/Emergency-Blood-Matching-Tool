package com.bloodnetwork.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bloodnetwork.app.R;
import com.bloodnetwork.app.model.User;
import com.bloodnetwork.app.service.AuthService;
import com.bloodnetwork.app.ui.AdminActivity;
import com.bloodnetwork.app.ui.BloodBankActivity;
import com.bloodnetwork.app.ui.DonorActivity;
import com.bloodnetwork.app.ui.HospitalActivity;
import com.bloodnetwork.app.ui.RegisterActivity;
import com.bloodnetwork.app.system.EmergencyBloodSystem;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegister, tvForgot;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin   = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgot   = findViewById(R.id.tvForgot);

        try {
            authService = EmergencyBloodSystem.getInstance(this).getAuthService();
        } catch (IOException e) {
            Toast.makeText(this, "System error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        btnLogin.setOnClickListener(v -> attemptLogin());
        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
        tvForgot.setOnClickListener(v ->
                Toast.makeText(this, "Contact admin to reset password.", Toast.LENGTH_SHORT).show());
    }

    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = authService.login(username, password);
        if (user == null) {
            if (authService.isPendingApproval(username, password)) {
                Toast.makeText(this, "Account pending admin approval.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Invalid credentials.", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        Intent intent;
        switch (user.getRole()) {
            case ADMIN:
                intent = new Intent(this, AdminActivity.class);
                break;
            case HOSPITAL:
                intent = new Intent(this, HospitalActivity.class);
                intent.putExtra("hospitalId", user.getLinkedId());
                intent.putExtra("userId", user.getId());
                break;
            case DONOR:
                intent = new Intent(this, DonorActivity.class);
                intent.putExtra("donorId", user.getLinkedId());
                intent.putExtra("userId", user.getId());
                break;
            case BLOOD_BANK:
                intent = new Intent(this, BloodBankActivity.class);
                intent.putExtra("bankId", user.getLinkedId());
                intent.putExtra("userId", user.getId());
                break;
            default:
                Toast.makeText(this, "Unknown role", Toast.LENGTH_SHORT).show();
                return;
        }
        startActivity(intent);
        finish();
    }
}
