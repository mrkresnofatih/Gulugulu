package com.mrkresnofatihdev.gulugulu.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class IdentityPermissionCreateRequestModel implements IJsonSerializable {
    private String identityName;
    private String permissionName;
    private List<String> resourceNames;

    public IdentityPermissionCreateRequestModel() {
    }

    public IdentityPermissionCreateRequestModel(String identityName, String permissionName, List<String> resourceNames) {
        this.identityName = identityName;
        this.permissionName = permissionName;
        this.resourceNames = resourceNames;
    }

    public String getIdentityName() {
        return identityName;
    }

    public void setIdentityName(String identityName) {
        this.identityName = identityName;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public List<String> getResourceNames() {
        return resourceNames;
    }

    public void setResourceNames(List<String> resourceNames) {
        this.resourceNames = resourceNames;
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
