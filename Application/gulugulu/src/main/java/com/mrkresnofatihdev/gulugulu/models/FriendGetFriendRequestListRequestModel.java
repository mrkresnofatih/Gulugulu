package com.mrkresnofatihdev.gulugulu.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class FriendGetFriendRequestListRequestModel implements IJsonSerializable {
    @NotNull(message = "Username must not be null")
    @NotEmpty(message = "Username must not be empty")
    @Size(min = 6, max = 30, message = "Username must be 6-30 chars")
    private String username;

    @NotNull(message = "startCreatedAt must not be null")
    @NotEmpty(message = "startCreatedAt must not be empty")
    @Size(min = 15, max = 15, message = "startCreatedAt must be 15 chars")
    @Pattern(regexp = "[0-9]{15}", message = "startCreatedAt must [0-9]{15}")
    private String startCreatedAt;

    @NotNull(message = "pageSize must not be null")
    private int pageSize;

    public FriendGetFriendRequestListRequestModel() {
    }

    public FriendGetFriendRequestListRequestModel(String username, String startCreatedAt, int pageSize) {
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
