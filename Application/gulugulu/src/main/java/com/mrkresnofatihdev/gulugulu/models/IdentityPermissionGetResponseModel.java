package com.mrkresnofatihdev.gulugulu.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class IdentityPermissionGetResponseModel implements IJsonSerializable {
    private String permissionName;
    private List<String> resourceNames;
    private String identityName;

    public IdentityPermissionGetResponseModel() {
    }

    public IdentityPermissionGetResponseModel(String permissionName, List<String> resourceNames, String identityName) {
        this.permissionName = permissionName;
        this.resourceNames = resourceNames;
        this.identityName = identityName;
    }

    public IdentityPermissionGetResponseModel(IdentityPermissionEntity identityPermissionEntity) {
        this.permissionName = identityPermissionEntity.getPermissionName();
        this.identityName = identityPermissionEntity.getIdentityName();
        this.resourceNames = identityPermissionEntity.getResourceNames();
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
