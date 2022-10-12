package com.mrkresnofatihdev.gulugulu.models;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.util.List;

@DynamoDbBean
public class IdentityPermissionEntity {
    private String partitionKey;
    private String sortKey;
    private String permissionName;
    private List<String> resourceNames;
    private String identityName;

    public IdentityPermissionEntity() {
    }

    public IdentityPermissionEntity(Builder builder) {
        this.partitionKey = builder.partitionKey;
        this.sortKey = builder.sortKey;
        this.identityName = builder.identityName;
        this.permissionName = builder.permissionName;
        this.resourceNames = builder.resourceNames;
    }

    public IdentityPermissionEntity(
            String partitionKey,
            String sortKey,
            String permissionName,
            List<String> resourceNames,
            String identityName) {
        this.partitionKey = partitionKey;
        this.sortKey = sortKey;
        this.permissionName = permissionName;
        this.resourceNames = resourceNames;
        this.identityName = identityName;
    }

    @DynamoDbPartitionKey
    public String getPartitionKey() {
        return partitionKey;
    }

    public void setPartitionKey(String partitionKey) {
        this.partitionKey = partitionKey;
    }

    @DynamoDbSortKey
    public String getSortKey() {
        return sortKey;
    }

    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
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

    public String getIdentityName() {
        return identityName;
    }

    public void setIdentityName(String identityName) {
        this.identityName = identityName;
    }

    public static class Builder
    {
        private String partitionKey;
        private String sortKey;
        private String permissionName;
        private List<String> resourceNames;
        private String identityName;

        public Builder() {
        }

        public Builder setPermissionName(String permissionName) {
            this.permissionName = permissionName;
            this.sortKey = String.format("permission#%s", permissionName);
            return this;
        }

        public Builder setIdentityName(String identityName) {
            this.identityName = identityName;
            this.partitionKey = String.format("identity-permissions#%s", identityName);
            return this;
        }

        public Builder setResourceNames(List<String> resourceNames) {
            this.resourceNames = resourceNames;
            return this;
        }

        public IdentityPermissionEntity build() {
            return new IdentityPermissionEntity(this);
        }
    }
}
