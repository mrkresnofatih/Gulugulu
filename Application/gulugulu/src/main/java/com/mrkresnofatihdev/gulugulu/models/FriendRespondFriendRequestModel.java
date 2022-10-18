package com.mrkresnofatihdev.gulugulu.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class FriendRespondFriendRequestModel implements IJsonSerializable {
    @NotEmpty(message = "username must not be empty")
    @NotNull(message = "username must not be null")
    @Size(min = 6, max = 30, message = "username must be 6-30 chars")
    private String username;

    @NotEmpty(message = "createdAt must not be empty")
    @NotNull(message = "createdAt must not be null")
    @Size(min = 15, max = 15, message = "startCreatedAt must be 15 chars")
    @Pattern(regexp = "[0-9]{15}", message = "startCreatedAt must mast [0-9]{15}")
    private String createdAt;

    public FriendRespondFriendRequestModel() {
    }

    public FriendRespondFriendRequestModel(String username, String createdAt) {
        this.username = username;
        this.createdAt = createdAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
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
