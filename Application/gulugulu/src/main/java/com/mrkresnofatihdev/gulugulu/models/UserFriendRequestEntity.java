package com.mrkresnofatihdev.gulugulu.models;

import com.mrkresnofatihdev.gulugulu.utilities.TimeHelper;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class UserFriendRequestEntity {
    private String partitionKey;
    private String sortKey;
    private String username;
    private String createdAt;
    private String requesterUsername;

    public UserFriendRequestEntity() {
    }

    public UserFriendRequestEntity(
            String partitionKey,
            String sortKey,
            String username,
            String createdAt,
            String requesterUsername) {
        this.partitionKey = partitionKey;
        this.sortKey = sortKey;
        this.username = username;
        this.createdAt = createdAt;
        this.requesterUsername = requesterUsername;
    }

    public UserFriendRequestEntity(Builder builder) {
        this.partitionKey = builder.partitionKey;
        this.sortKey = builder.sortKey;
        this.username = builder.username;
        this.createdAt = builder.createdAt;
        this.requesterUsername = builder.requesterUsername;
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

    public String getRequesterUsername() {
        return requesterUsername;
    }

    public void setRequesterUsername(String requesterUsername) {
        this.requesterUsername = requesterUsername;
    }

    public static class Builder
    {
        private String partitionKey;
        private String sortKey;
        private String username;
        private String createdAt;
        private String requesterUsername;

        public Builder() {
        }

        public Builder setUsername(String username) {
            this.username = username;
            this.partitionKey = String.format("user-friend-requests#%s", username);
            return this;
        }

        public Builder setRequesterUsername(String requesterUsername) {
            this.requesterUsername = requesterUsername;
            return this;
        }

        public Builder setCreatedAtNow() {
            var timestamp = TimeHelper.GetDescUtcMilliTimestamp();
            this.createdAt = timestamp;
            this.sortKey = String.format("friend-request#%s", timestamp);
            return this;
        }

        public UserFriendRequestEntity build() {
            return new UserFriendRequestEntity(this);
        }
    }
}
