package com.mrkresnofatihdev.gulugulu.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserNotificationListGetRequestModel implements IJsonSerializable {
    private String username;
    private String startCreatedAt;
    private int pageSize;

    public UserNotificationListGetRequestModel() {
    }

    public UserNotificationListGetRequestModel(
            String username,
            String startCreatedAt,
            int pageSize) {
        this.username = username;
        this.startCreatedAt = startCreatedAt;
        this.pageSize = pageSize;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStartCreatedAt() {
        return startCreatedAt;
    }

    public void setStartCreatedAt(String startCreatedAt) {
        this.startCreatedAt = startCreatedAt;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toJsonSerialized() {
        try {
            var mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException exception) {
            return "JsonProcessingException";
        }
    }
}
