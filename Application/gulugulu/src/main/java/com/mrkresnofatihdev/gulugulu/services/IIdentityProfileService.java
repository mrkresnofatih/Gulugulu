package com.mrkresnofatihdev.gulugulu.services;

import com.mrkresnofatihdev.gulugulu.models.IdentityProfileGetResponseModel;
import com.mrkresnofatihdev.gulugulu.models.IdentityUserProfileCreateRequestModel;

public interface IIdentityProfileService {
    IdentityProfileGetResponseModel CreateForUser(IdentityUserProfileCreateRequestModel identityUserProfileCreateRequest);
    IdentityProfileGetResponseModel GetForUser(IdentityUserProfileCreateRequestModel identityUserProfileCreateRequest);
    String GetIdentityNameOfUser(String username);
}
