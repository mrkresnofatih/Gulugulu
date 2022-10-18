package com.mrkresnofatihdev.gulugulu.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserFriendListRequestModel implements IJsonSerializable {
    @NotNull(message = "Username must not be null")
    @NotEmpty(message = "Username must not be empty")
    @Size(min = 6, max = 30, message = "Username must be 6-30 chars")
    private String username;

    @NotNull(message = "StartFriendUsername must not be null")
    private String startFriendUsername;

    @NotNull
    private int pageSize;

    public UserFriendListRequestModel() {
    }

    public UserFriendListRequestModel(String username, String startFriendUsername, int pageSize) {
        this.username = username;
        this.startFriendUsername = startFriendUsername;
        this.pageSize = pageSize;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStartFriendUsername() {
        return startFriendUsername;
    }

    public void setStartFriendUsername(String startFriendUsername) {
        this.startFriendUsername = startFriendUsername;
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
