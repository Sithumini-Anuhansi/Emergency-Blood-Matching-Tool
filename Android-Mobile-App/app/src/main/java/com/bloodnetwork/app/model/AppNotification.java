package com.bloodnetwork.app.model;

public class AppNotification {
    private String id;
    private String recipientId;  // userId or role string
    private String title;
    private String message;
    private String requestId; // Linked emergency request ID
    private long timestamp;
    private boolean read;

    public AppNotification(String id, String recipientId, String title, String message) {
        this(id, recipientId, title, message, null);
    }

    public AppNotification(String id, String recipientId, String title, String message, String requestId) {
        this.id = id;
        this.recipientId = recipientId;
        this.title = title;
        this.message = message;
        this.requestId = requestId;
        this.timestamp = System.currentTimeMillis();
        this.read = false;
    }

    public String getId() { return id; }
    public String getRecipientId() { return recipientId; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getRequestId() { return requestId; }
    public void setTitle(String title) { this.title = title; }
    public void setMessage(String message) { this.message = message; }
    public long getTimestamp() { return timestamp; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    @Override
    public String toString() { return title + ": " + message; }
}
