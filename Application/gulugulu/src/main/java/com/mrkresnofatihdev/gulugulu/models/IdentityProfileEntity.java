package com.mrkresnofatihdev.gulugulu.models;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class IdentityProfileEntity {
    private String partitionKey;
    private String sortKey;
    private String identityName;
    private boolean banned;

    public IdentityProfileEntity() {
    }

    public IdentityProfileEntity(String partitionKey, String sortKey, String identityName, boolean banned) {
        this.partitionKey = partitionKey;
        this.sortKey = sortKey;
        this.identityName = identityName;
        this.banned = banned;
    }

    public IdentityProfileEntity(Builder builder) {
        this.partitionKey = builder.partitionKey;
        this.sortKey = builder.sortKey;
        this.identityName = builder.identityName;
        this.banned = builder.banned;
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

    public static class Builder
    {
        private String partitionKey;
        private String sortKey;
        private String identityName;
        private boolean banned;

        public Builder() {
        }

        public Builder setIdentityName(String identityName) {
            this.identityName = identityName;
            this.partitionKey = String.format("identity-profile#%s", identityName);
            this.sortKey = String.format("identity-profile#%s", identityName);
            return this;
        }

        public Builder setBanned(boolean banned) {
            this.banned = banned;
            return this;
        }

        public IdentityProfileEntity build() {
            return new IdentityProfileEntity(this);
        }
    }
}
