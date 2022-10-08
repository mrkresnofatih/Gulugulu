package com.mrkresnofatihdev.gulugulu.models;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class UserCredentialsEntity {
    private String partitionKey;
    private String sortKey;
    private String username;
    private String password;

    public UserCredentialsEntity() {
    }

    public UserCredentialsEntity(String partitionKey, String sortKey, String username, String password) {
        this.partitionKey = partitionKey;
        this.sortKey = sortKey;
        this.username = username;
        this.password = password;
    }

    public UserCredentialsEntity(Builder builder) {
        this.partitionKey = builder.partitionKey;
        this.sortKey = builder.sortKey;
        this.username = builder.username;
        this.password = builder.password;
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

    public static class Builder
    {
        private String partitionKey;
        private String sortKey;
        private String username;
        private String password;

        public Builder() {
        }

        public Builder setUsername(String username) {
            this.username = username;
            this.partitionKey = String.format("user-credentials#%s", username);
            this.sortKey = String.format("user-credentials#%s", username);
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public UserCredentialsEntity build() {
            return new UserCredentialsEntity(this);
        }
    }
}
