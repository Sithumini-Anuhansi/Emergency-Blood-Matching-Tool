package com.bloodnetwork.app.model;

public class User {
    private String id;
    private String username;
    private String password;
    private UserRole role;
    private String linkedId; // hospitalId, donorId, or bloodBankId
    private boolean approved;

    public User(String id, String username, String password, UserRole role, String linkedId) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.linkedId = linkedId;
        this.approved = (role == UserRole.ADMIN); // admin auto-approved
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public UserRole getRole() { return role; }
    public String getLinkedId() { return linkedId; }
    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }

    @Override
    public String toString() {
        return username + " [" + role + "]" + (approved ? "" : " (pending)");
    }
}
