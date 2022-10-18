package com.mrkresnofatihdev.gulugulu.models;

public interface IUserNotificationCreatable extends IJsonSerializable {
    String getUsername();
    String getMessage();
    String getImage();
    String getLink();
}
