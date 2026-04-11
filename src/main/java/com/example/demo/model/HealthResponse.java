package com.example.demo.model;

public class HealthResponse {

    private String status;
    private String timestamp;
    private String service;

    public HealthResponse() {
    }

    public HealthResponse(String status, String timestamp, String service) {
        this.status = status;
        this.timestamp = timestamp;
        this.service = service;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
}
