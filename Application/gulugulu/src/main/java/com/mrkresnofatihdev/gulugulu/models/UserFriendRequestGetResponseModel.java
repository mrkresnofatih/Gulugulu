package com.mrkresnofatihdev.gulugulu.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserFriendRequestGetResponseModel implements IJsonSerializable {
    private String username;
    private String requesterUsername;
    private String createdAt;

    public UserFriendRequestGetResponseModel() {
    }

    public UserFriendRequestGetResponseModel(
            String username,
            String requesterUsername,
            String createdAt) {
        this.username = username;
        this.requesterUsername = requesterUsername;
        this.createdAt = createdAt;
    }

    public UserFriendRequestGetResponseModel(UserFriendRequestEntity userFriendRequestEntity) {
        this.username = userFriendRequestEntity.getUsername();
        this.requesterUsername = userFriendRequestEntity.getRequesterUsername();
        this.createdAt = userFriendRequestEntity.getCreatedAt();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRequesterUsername() {
        return requesterUsername;
    }

    public void setRequesterUsername(String requesterUsername) {
        this.requesterUsername = requesterUsername;
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
