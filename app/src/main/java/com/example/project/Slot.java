package com.example.project;

public class Slot {
    private String id;
    private String date;
    private String time;
    private String doctorId;
    private String doctorName;
    private String doctorEmail;
    private String patientId; // this will be null for available slots

    public Slot() {
        // Default constructor required for calls to DataSnapshot.getValue(Slot.class)
    }

    public Slot(String id, String date, String time, String doctorId, String doctorName, String doctorEmail, String patientId) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.doctorEmail = doctorEmail;
        this.patientId = patientId;
    }

    // Getters and setters for each field
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getDoctorEmail() { return doctorEmail; }
    public void setDoctorEmail(String doctorEmail) { this.doctorEmail = doctorEmail; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public boolean isBooked() {
        return patientId != null && !patientId.isEmpty();
    }
    }