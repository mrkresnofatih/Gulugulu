package com.mrkresnofatihdev.gulugulu.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class IdentityProfileGetResponseModel implements IJsonSerializable {

    private String identityName;
    private boolean banned;

    public IdentityProfileGetResponseModel() {
    }

    public IdentityProfileGetResponseModel(String identityName, boolean banned) {
        this.identityName = identityName;
        this.banned = banned;
    }

    public IdentityProfileGetResponseModel(IdentityProfileEntity identityProfileEntity) {
        this.identityName = identityProfileEntity.getIdentityName();
        this.banned = identityProfileEntity.isBanned();
    }

    public String getIdentityName() {
        return identityName;
    }

    public void setIdentityName(String identityName) {
        this.identityName = identityName;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
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
