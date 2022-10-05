package com.mrkresnofatihdev.gulugulu.services;

import com.mrkresnofatihdev.gulugulu.models.UserProfileEntity;
import com.mrkresnofatihdev.gulugulu.models.UserProfileCreateRequestModel;
import com.mrkresnofatihdev.gulugulu.models.UserProfileGetResponseModel;

public interface IUserProfileService {
    UserProfileGetResponseModel SaveUserProfile(UserProfileCreateRequestModel userProfileCreateRequest);
    UserProfileGetResponseModel GetUserProfile(String username);
}
