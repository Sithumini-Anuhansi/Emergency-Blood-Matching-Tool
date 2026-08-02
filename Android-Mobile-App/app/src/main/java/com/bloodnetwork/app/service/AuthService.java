package com.bloodnetwork.app.service;

import android.content.Context;

import com.bloodnetwork.app.model.User;
import com.bloodnetwork.app.model.UserRole;
import com.bloodnetwork.app.util.CSVReader;
import com.bloodnetwork.app.util.UserCsvWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AuthService {

    private static final String USERS_FILE = "users_extra.json";

    private final Context context;
    private final Map<String, User> users     = new LinkedHashMap<>();
    private final Map<String, User> byUsername = new LinkedHashMap<>();
    private int counter = 0;

    public AuthService(Context context) throws IOException {
        this.context = context;
        loadFromCsv();
        loadFromStorage();
    }

    // ── Load pre-seeded accounts from assets/users.csv ────────────────────

    private void loadFromCsv() throws IOException {
        List<String[]> rows = CSVReader.readFromAssets(context, "users.csv");
        for (String[] r : rows) {
            // UserID,Username,Password,Role,LinkedID,Approved
            String id       = r[0].trim();
            String username = r[1].trim();
            String password = r[2].trim();
            UserRole role   = UserRole.valueOf(r[3].trim());
            String linkedId = r[4].trim();
            boolean approved = Boolean.parseBoolean(r[5].trim());

            User user = new User(id, username, password, role, linkedId);
            user.setApproved(approved);
            users.put(id, user);
            byUsername.put(username, user);

            // track highest counter so new IDs don't collide
            try {
                int num = Integer.parseInt(id.replace("USR", ""));
                if (num > counter) counter = num;
            } catch (NumberFormatException ignored) {}
        }
    }

    // ── Persist & load new registrations from internal storage ─────────────

    private void loadFromStorage() {
        File file = new File(context.getFilesDir(), USERS_FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            JSONArray arr = new JSONArray(sb.toString());
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                String id       = o.getString("id");
                String username = o.getString("username");
                String password = o.getString("password");
                UserRole role   = UserRole.valueOf(o.getString("role"));
                String linkedId = o.getString("linkedId");
                boolean approved = o.getBoolean("approved");
                User user = new User(id, username, password, role, linkedId);
                user.setApproved(approved);
                if (!users.containsKey(id)) {
                    users.put(id, user);
                    byUsername.put(username, user);
                    try {
                        int num = Integer.parseInt(id.replace("USR", ""));
                        if (num > counter) counter = num;
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveToStorage() {
        // only save non-CSV (dynamically registered) users
        try {
            JSONArray arr = new JSONArray();
            for (User u : users.values()) {
                // skip users loaded from CSV (IDs up to USR30 are seeded)
                try {
                    int num = Integer.parseInt(u.getId().replace("USR", ""));
                    if (num <= 30) continue;
                } catch (NumberFormatException ignored) {}
                JSONObject o = new JSONObject();
                o.put("id",       u.getId());
                o.put("username", u.getUsername());
                o.put("password", u.getPassword());
                o.put("role",     u.getRole().name());
                o.put("linkedId", u.getLinkedId());
                o.put("approved", u.isApproved());
                arr.put(o);
            }
            File file = new File(context.getFilesDir(), USERS_FILE);
            try (FileWriter fw = new FileWriter(file)) {
                fw.write(arr.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Public API ─────────────────────────────────────────────────────────

    public User register(String username, String password, UserRole role, String linkedId) {
        if (byUsername.containsKey(username)) return null;
        String id = "USR" + (++counter);
        User user = new User(id, username, password, role, linkedId);
        // ADMIN auto-approved, others need admin approval
        user.setApproved(role == UserRole.ADMIN);
        users.put(id, user);
        byUsername.put(username, user);
        saveToStorage();
        UserCsvWriter.append(context, user);
        return user;
    }

    public User login(String username, String password) {
        User user = byUsername.get(username);
        if (user == null || !user.getPassword().equals(password)) return null;
        if (!user.isApproved()) return null;
        return user;
    }

    public boolean approve(String userId) {
        User u = users.get(userId);
        if (u == null) return false;
        u.setApproved(true);
        saveToStorage();
        UserCsvWriter.rewrite(context, getDynamicUsers());
        return true;
    }

    public boolean reject(String userId) {
        User u = users.get(userId);
        if (u == null) return false;
        byUsername.remove(u.getUsername());
        users.remove(userId);
        saveToStorage();
        UserCsvWriter.rewrite(context, getDynamicUsers());
        return true;
    }

    public List<User> getPendingUsers() {
        List<User> list = new ArrayList<>();
        for (User u : users.values()) {
            if (!u.isApproved()) list.add(u);
        }
        return list;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User findById(String id) {
        return users.get(id);
    }

    /** Returns pending-but-not-approved check for login feedback */
    public boolean isPendingApproval(String username, String password) {
        User u = byUsername.get(username);
        return u != null && u.getPassword().equals(password) && !u.isApproved();
    }

    /** Returns only dynamically registered users (not seeded from CSV). */
    private List<User> getDynamicUsers() {
        List<User> list = new ArrayList<>();
        for (User u : users.values()) {
            try {
                int num = Integer.parseInt(u.getId().replace("USR", ""));
                if (num > 30) list.add(u);
            } catch (NumberFormatException ignored) {}
        }
        return list;
    }
}
