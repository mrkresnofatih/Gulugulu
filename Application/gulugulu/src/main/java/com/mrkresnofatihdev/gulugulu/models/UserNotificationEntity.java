package com.mrkresnofatihdev.gulugulu.models;

import com.mrkresnofatihdev.gulugulu.utilities.TimeHelper;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class UserNotificationEntity {
    private String partitionKey;
    private String sortKey;
    private String username;
    private String createdAt;
    private String message;
    private String link;
    private String image;
    private boolean acknowledged;

    public UserNotificationEntity() {
    }

    public UserNotificationEntity(
            String partitionKey,
            String sortKey,
            String username,
            String createdAt,
            String message,
            String link,
            String image,
            boolean acknowledged) {
        this.partitionKey = partitionKey;
        this.sortKey = sortKey;
        this.username = username;
        this.createdAt = createdAt;
        this.message = message;
        this.link = link;
        this.image = image;
        this.acknowledged = acknowledged;
    }

    public UserNotificationEntity(Builder builder) {
        this.partitionKey = builder.partitionKey;
        this.sortKey = builder.sortKey;
        this.username = builder.username;
        this.message = builder.message;
        this.image = builder.image;
        this.link = builder.link;
        this.createdAt = builder.createdAt;
        this.acknowledged = builder.acknowledged;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isAcknowledged() {
        return acknowledged;
    }

    public void setAcknowledged(boolean acknowledged) {
        this.acknowledged = acknowledged;
    }

    public static class Builder
    {
        private String partitionKey;
        private String sortKey;
        private String username;
        private String createdAt;
        private String message;
        private String link;

        private String image;
        private boolean acknowledged;

        public Builder() {
        }

        public Builder setUsername(String username) {
            this.username = username;
            this.partitionKey = String.format("user-notifications#%s", username);
            this.acknowledged = false;
            return this;
        }

        public Builder setCreatedAtNow() {
            this.createdAt = TimeHelper.GetDescUtcMilliTimestamp();
            this.sortKey = String.format("notification#%s", TimeHelper.GetDescUtcMilliTimestamp());
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setLink(String link) {
            this.link = link;
            return this;
        }

        public Builder setImage(String image) {
            this.image = image;
            return this;
        }

        public UserNotificationEntity build() {
            return new UserNotificationEntity(this);
        }
    }
}
