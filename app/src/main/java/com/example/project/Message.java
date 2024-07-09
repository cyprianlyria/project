package com.example.project;

public class Message {
    private String message;
    private String timestamp;
    private String sender;
    private String recipientname;

    // Default constructor required for calls to DataSnapshot.getValue(Message.class)
    public Message() {
    }

    public Message(String message, String timestamp, String sender, String recipientname) {
        this.message = message;
        this.timestamp = timestamp;
        this.sender = sender;
        this.recipientname = recipientname;
    }

    // Getters and setters...
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipientname() {
        return recipientname;
    }

    public void setRecipientname(String recipientname) {
        this.recipientname = recipientname;
    }
}
