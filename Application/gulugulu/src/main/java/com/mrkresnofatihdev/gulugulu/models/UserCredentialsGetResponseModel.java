package com.mrkresnofatihdev.gulugulu.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserCredentialsGetResponseModel implements IJsonSerializable {
    private String username;
    private String password;

    public UserCredentialsGetResponseModel() {
    }

    public UserCredentialsGetResponseModel(UserCredentialsEntity userCredentialsEntity) {
        this.username = userCredentialsEntity.getUsername();
        this.password = userCredentialsEntity.getPassword();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
