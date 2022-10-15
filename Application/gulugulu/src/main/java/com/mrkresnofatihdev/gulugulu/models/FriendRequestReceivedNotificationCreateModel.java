package com.mrkresnofatihdev.gulugulu.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FriendRequestReceivedNotificationCreateModel implements IUserNotificationCreatable {
    private String requesterUsername;
    private String receiverUsername;
    private String requesterAvatar;

    public FriendRequestReceivedNotificationCreateModel() {
    }

    public FriendRequestReceivedNotificationCreateModel(
            String requesterUsername,
            String receiverUsername,
            String requesterAvatar) {
        this.requesterUsername = requesterUsername;
        this.receiverUsername = receiverUsername;
        this.requesterAvatar = requesterAvatar;
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
        return receiverUsername;
    }

    @Override
    public String getMessage() {
        return String.format("%s sent you a friend request!", requesterUsername);
    }

    @Override
    public String getImage() {
        return requesterAvatar;
    }

    @Override
    public String getLink() {
        return null;
    }
}
