package com.mrkresnofatihdev.gulugulu.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserNotificationGetResponseModel implements IJsonSerializable {
    private String username;
    private String createdAt;
    private String message;
    private String image;
    private String link;

    public UserNotificationGetResponseModel() {
    }

    public UserNotificationGetResponseModel(
            String username,
            String createdAt,
            String message,
            String image,
            String link) {
        this.username = username;
        this.createdAt = createdAt;
        this.message = message;
        this.image = image;
        this.link = link;
    }

    public UserNotificationGetResponseModel(UserNotificationEntity userNotificationEntity) {
        this.username = userNotificationEntity.getUsername();
        this.message = userNotificationEntity.getMessage();
        this.image = userNotificationEntity.getImage();
        this.link = userNotificationEntity.getLink();
        this.createdAt = userNotificationEntity.getCreatedAt();
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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
