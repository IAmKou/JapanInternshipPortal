package com.example.jip.dto.response;

public class UserResponse {
    private String userType;
    private Object userDetails;

    public UserResponse(String userType, Object userDetails) {
        this.userType = userType;
        this.userDetails = userDetails;
    }

    public String getUserType() { return userType; }
    public Object getUserDetails() { return userDetails; }

}

