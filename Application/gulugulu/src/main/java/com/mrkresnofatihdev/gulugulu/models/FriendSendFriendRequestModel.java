package com.mrkresnofatihdev.gulugulu.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class FriendSendFriendRequestModel implements IJsonSerializable {
    @NotNull(message = "RequesterUsername must not be null")
    @NotEmpty(message = "RequesterUsername must not be empty")
    @Size(min = 6, max = 30, message = "RequesterUsername must be 6-30 chars")
    private String requesterUsername;

    @NotNull(message = "TargetUsername must not be null")
    @NotEmpty(message = "TargetUsername must not be empty")
    @Size(min = 6, max = 30, message = "TargetUsername must be 6-30 chars")
    private String targetUsername;

    public FriendSendFriendRequestModel() {
    }

    public FriendSendFriendRequestModel(String requesterUsername, String targetUsername) {
        this.requesterUsername = requesterUsername;
        this.targetUsername = targetUsername;
    }

    public String getRequesterUsername() {
        return requesterUsername;
    }

    public void setRequesterUsername(String requesterUsername) {
        this.requesterUsername = requesterUsername;
    }

    public String getTargetUsername() {
        return targetUsername;
    }

    public void setTargetUsername(String targetUsername) {
        this.targetUsername = targetUsername;
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
