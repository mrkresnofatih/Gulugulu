package com.mrkresnofatihdev.gulugulu.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserFriendGetResponseModel implements IJsonSerializable {
    private String username;
    private String friendUsername;
    private String createdAt;

    public UserFriendGetResponseModel() {
    }

    public UserFriendGetResponseModel(
            String username,
            String friendUsername,
            String createdAt) {
        this.username = username;
        this.friendUsername = friendUsername;
        this.createdAt = createdAt;
    }

    public UserFriendGetResponseModel(UserFriendEntity userFriendEntity) {
        this.username = userFriendEntity.getUsername();
        this.friendUsername = userFriendEntity.getFriendUsername();
        this.createdAt = userFriendEntity.getCreatedAt();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFriendUsername() {
        return friendUsername;
    }

    public void setFriendUsername(String friendUsername) {
        this.friendUsername = friendUsername;
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
