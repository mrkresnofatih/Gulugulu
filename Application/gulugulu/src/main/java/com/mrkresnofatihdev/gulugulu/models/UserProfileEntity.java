package com.mrkresnofatihdev.gulugulu.models;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class UserProfileEntity {
    private String partitionKey;
    private String sortKey;
    private String username;
    private String fullname;
    private String avatar;
    private String bio;
    private String email;

    public UserProfileEntity() {
    }

    public UserProfileEntity(String partitionKey, String sortKey, String username, String fullname, String avatar, String bio, String email) {
        this.partitionKey = partitionKey;
        this.sortKey = sortKey;
        this.username = username;
        this.fullname = fullname;
        this.avatar = avatar;
        this.bio = bio;
        this.email = email;
    }

    public UserProfileEntity(Builder builder) {
        this.partitionKey = builder.partitionKey;
        this.sortKey = builder.sortKey;
        this.username = builder.username;
        this.fullname = builder.fullname;
        this.avatar = builder.avatar;
        this.bio = builder.bio;
        this.email = builder.email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    @Override
    public String toString() {
        return String
                .format("{username: %s, fullname: %s, avatar: %s, bio: %s, email: %s}",
                        this.username, this.fullname, this.avatar, this.bio, this.email);
    }

    public static class Builder
    {
        private String partitionKey;
        private String sortKey;
        private String username;
        private String fullname;
        private String avatar;
        private String bio;
        private String email;

        public Builder() {
        }

        public Builder setUsername(String username) {
            this.username = username;
            this.partitionKey = String.format("user-profile#%s", username);
            this.sortKey = String.format("user-profile#%s", username);
            return this;
        }

        public Builder setFullname(String fullname) {
            this.fullname = fullname;
            return this;
        }

        public Builder setAvatar(String avatar) {
            this.avatar = avatar;
            return this;
        }

        public Builder setBio(String bio) {
            this.bio = bio;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public UserProfileEntity build() {
            return new UserProfileEntity(this);
        }
    }
}
