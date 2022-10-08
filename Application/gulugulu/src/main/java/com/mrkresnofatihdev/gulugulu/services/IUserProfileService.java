package com.mrkresnofatihdev.gulugulu.services;

import com.mrkresnofatihdev.gulugulu.models.UserProfileCreateRequestModel;
import com.mrkresnofatihdev.gulugulu.models.UserProfileGetRequestModel;
import com.mrkresnofatihdev.gulugulu.models.UserProfileGetResponseModel;
import com.mrkresnofatihdev.gulugulu.models.UserProfileUpdateRequestModel;

public interface IUserProfileService {
    UserProfileGetResponseModel SaveUserProfile(UserProfileCreateRequestModel userProfileCreateRequest);
    UserProfileGetResponseModel GetUserProfile(UserProfileGetRequestModel userProfileGetRequest);
    UserProfileGetResponseModel UpdateUserProfile(UserProfileUpdateRequestModel userProfileUpdateRequest);
}
