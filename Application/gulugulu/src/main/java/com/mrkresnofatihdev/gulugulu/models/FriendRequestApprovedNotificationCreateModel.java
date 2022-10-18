package com.mrkresnofatihdev.gulugulu.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FriendRequestApprovedNotificationCreateModel implements IUserNotificationCreatable {
    private String approvedUsername;
    private String approverUsername;
    private String approverAvatar;

    public FriendRequestApprovedNotificationCreateModel() {
    }

    public FriendRequestApprovedNotificationCreateModel(
            String approvedUsername,
            String approverUsername,
            String approverAvatar) {
        this.approvedUsername = approvedUsername;
        this.approverUsername = approverUsername;
        this.approverAvatar = approverAvatar;
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

    @Override
    public String getUsername() {
        return approvedUsername;
    }

    @Override
    public String getMessage() {
        return String.format("@%s accepted your friend request! Start chatting now!", approverUsername);
    }

    @Override
    public String getImage() {
        return approverAvatar;
    }

    @Override
    public String getLink() {
        return null;
    }
}
