package com.bloodnetwork.app.service;

import android.content.Context;
import com.bloodnetwork.app.model.AppNotification;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NotificationService {
    private static final String FILE_NAME = "notifications.json";
    private Map<String, AppNotification> notifications = new LinkedHashMap<>();
    private int counter = 0;
    private Context context;

    public NotificationService(Context context) {
        this.context = context;
        loadFromStorage();
    }

    public AppNotification send(String recipientId, String title, String message) {
        String id = "NOTIF" + (++counter);
        AppNotification n = new AppNotification(id, recipientId, title, message);
        notifications.put(id, n);
        saveToStorage();
        return n;
    }

    public List<AppNotification> getForRecipient(String recipientId) {
        List<AppNotification> list = new ArrayList<>();
        for (AppNotification n : notifications.values()) {
            if (n.getRecipientId().equals(recipientId)) list.add(n);
        }
        return list;
    }

    public int getUnreadCount(String recipientId) {
        int count = 0;
        for (AppNotification n : notifications.values()) {
            if (n.getRecipientId().equals(recipientId) && !n.isRead()) count++;
        }
        return count;
    }

    public void markRead(String notificationId) {
        AppNotification n = notifications.get(notificationId);
        if (n != null) {
            n.setRead(true);
            saveToStorage();
        }
    }

    public void markAllRead(String recipientId) {
        boolean changed = false;
        for (AppNotification n : notifications.values()) {
            if (n.getRecipientId().equals(recipientId) && !n.isRead()) {
                n.setRead(true);
                changed = true;
            }
        }
        if (changed) saveToStorage();
    }

    private void saveToStorage() {
        try {
            JSONArray arr = new JSONArray();
            for (AppNotification n : notifications.values()) {
                JSONObject o = new JSONObject();
                o.put("id", n.getId());
                o.put("recipientId", n.getRecipientId());
                o.put("title", n.getTitle());
                o.put("message", n.getMessage());
                o.put("timestamp", n.getTimestamp());
                o.put("read", n.isRead());
                arr.put(o);
            }
            File file = new File(context.getFilesDir(), FILE_NAME);
            try (FileWriter fw = new FileWriter(file)) {
                fw.write(arr.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFromStorage() {
        File file = new File(context.getFilesDir(), FILE_NAME);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            JSONArray arr = new JSONArray(sb.toString());
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                AppNotification n = new AppNotification(
                        o.getString("id"),
                        o.getString("recipientId"),
                        o.getString("title"),
                        o.getString("message")
                );
                if (o.has("timestamp")) {
                    try {
                        java.lang.reflect.Field field = AppNotification.class.getDeclaredField("timestamp");
                        field.setAccessible(true);
                        field.set(n, o.getLong("timestamp"));
                    } catch (Exception ignored) {}
                }
                n.setRead(o.getBoolean("read"));
                notifications.put(n.getId(), n);
                try {
                    int num = Integer.parseInt(n.getId().replace("NOTIF", ""));
                    if (num > counter) counter = num;
                } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
