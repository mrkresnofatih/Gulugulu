package com.mrkresnofatihdev.gulugulu.models;

import com.mrkresnofatihdev.gulugulu.utilities.TimeHelper;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class UserFriendEntity {
    private String partitionKey;
    private String sortKey;
    private String username;
    private String friendUsername;
    private String createdAt;

    public UserFriendEntity() {
    }

    public UserFriendEntity(
            String partitionKey,
            String sortKey,
            String username,
            String friendUsername,
            String createdAt) {
        this.partitionKey = partitionKey;
        this.sortKey = sortKey;
        this.username = username;
        this.friendUsername = friendUsername;
        this.createdAt = createdAt;
    }

    public UserFriendEntity(Builder builder) {
        this.partitionKey = builder.partitionKey;
        this.sortKey = builder.sortKey;
        this.username = builder.username;
        this.friendUsername = builder.friendUsername;
        this.createdAt = builder.createdAt;
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

    public String getFriendUsername() {
        return friendUsername;
    }

    public void setFriendUsername(String friendUsername) {
        this.friendUsername = friendUsername;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public static class Builder
    {
        private String partitionKey;
        private String sortKey;
        private String username;
        private String friendUsername;
        private String createdAt;

        public Builder() {
        }

        public Builder setUsername(String username) {
            this.username = username;
            this.partitionKey = String.format("user-friends#%s", username);
            return this;
        }

        public Builder setFriendUsername(String friendUsername) {
            this.friendUsername = friendUsername;
            this.sortKey = String.format("friend#%s", friendUsername);
            return this;
        }

        public Builder setCreatedAtNow() {
            this.createdAt = TimeHelper.GetDescUtcMilliTimestamp();
            return this;
        }

        public UserFriendEntity build() {
            return new UserFriendEntity(this);
        }
    }
}
