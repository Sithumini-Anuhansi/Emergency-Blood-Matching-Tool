package com.bloodnetwork.app.model;

public class AppNotification {
    private String id;
    private String recipientId;  // userId or role string
    private String title;
    private String message;
    private long timestamp;
    private boolean read;

    public AppNotification(String id, String recipientId, String title, String message) {
        this.id = id;
        this.recipientId = recipientId;
        this.title = title;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
        this.read = false;
    }

    public String getId() { return id; }
    public String getRecipientId() { return recipientId; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    @Override
    public String toString() { return title + ": " + message; }
}
