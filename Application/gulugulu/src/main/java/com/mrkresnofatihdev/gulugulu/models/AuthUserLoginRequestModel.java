package com.mrkresnofatihdev.gulugulu.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class AuthUserLoginRequestModel implements IJsonSerializable {
    @NotNull(message = "Username must not be null")
    @NotEmpty(message = "Username must not be empty")
    @Size(min = 6, max = 30, message = "Username must be 6-30 chars")
    @Pattern(regexp = "^[A-Za-z0-9_]*$", message = "Username must match set regex")
    private String username;

    @NotNull(message = "Password must not be null")
    @NotEmpty(message = "Password must not be empty")
    @Size(min = 6, message = "Password must be at least 6 chars")
    @Pattern(regexp = "^[A-Za-z0-9_!@./#&+]*$", message = "Password must match set regex")
    private String password;

    public AuthUserLoginRequestModel() {
    }

    public AuthUserLoginRequestModel(String username, String password) {
        this.username = username;
        this.password = password;
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
