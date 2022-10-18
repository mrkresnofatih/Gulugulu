package com.mrkresnofatihdev.gulugulu.models;

import com.mrkresnofatihdev.gulugulu.utilities.TimeHelper;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class UserPendingFriendEntity {
    private String partitionKey;
    private String sortKey;
    private String username;
    private String createdAt;
    private String friendUsername;

    public UserPendingFriendEntity() {
    }

    public UserPendingFriendEntity(
            String partitionKey,
            String sortKey,
            String username,
            String createdAt,
            String friendUsername) {
        this.partitionKey = partitionKey;
        this.sortKey = sortKey;
        this.username = username;
        this.createdAt = createdAt;
        this.friendUsername = friendUsername;
    }

    public UserPendingFriendEntity(Builder builder) {
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getFriendUsername() {
        return friendUsername;
    }

    public void setFriendUsername(String friendUsername) {
        this.friendUsername = friendUsername;
    }

    public static class Builder
    {
        private String partitionKey;
        private String sortKey;
        private String username;
        private String createdAt;
        private String friendUsername;

        public Builder() {
        }

        public Builder setUsername(String username) {
            this.username = username;
            this.partitionKey = String.format("user-pending-friends#%s", username);
            return this;
        }

        public Builder setFriendUsername(String friendUsername) {
            this.friendUsername = friendUsername;
            return this;
        }

        public Builder setCreatedAtNow() {
            var time = TimeHelper.GetDescUtcMilliTimestamp();
            this.createdAt = time;
            this.sortKey = String.format("pending-friend#%s", time);
            return this;
        }

        public UserPendingFriendEntity build() {
            return new UserPendingFriendEntity(this);
        }
    }
}
