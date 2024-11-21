package com.example.gestiondeclavesegura;

public class PasswordModel {
    private String id;
    private String siteName;
    private String username;
    private String password;
    private String notes;

    // Constructor vacío requerido por Firestore
    public PasswordModel() {}

    // Constructor completo
    public PasswordModel(String id, String siteName, String username, String password, String notes) {
        this.id = id;
        this.siteName = siteName;
        this.username = username;
        this.password = password;
        this.notes = notes;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
