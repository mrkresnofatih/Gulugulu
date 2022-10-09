package com.mrkresnofatihdev.gulugulu.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserProfileUpdateRequestModel implements IJsonSerializable {
    @NotNull(message = "Username must not be null")
    @NotEmpty(message = "Username must not be empty")
    @Size(min = 6, max = 30, message = "Username must be 6-30 chars")
    private String username;

    @NotNull(message = "Fullname must not be null")
    @NotEmpty(message = "Fullname must not be empty")
    private String fullname;

    @NotNull(message = "Avatar must not be null")
    @NotEmpty(message = "Avatar must not be empty")
    private String avatar;

    @NotNull(message = "Bio must not be null")
    @NotEmpty(message = "Bio must not be empty")
    private String bio;

    public UserProfileUpdateRequestModel() {
    }

    public UserProfileUpdateRequestModel(String username, String fullname, String avatar, String bio) {
        this.username = username;
        this.fullname = fullname;
        this.avatar = avatar;
        this.bio = bio;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
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
