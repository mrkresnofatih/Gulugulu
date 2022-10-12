package com.mrkresnofatihdev.gulugulu.services;

import com.mrkresnofatihdev.gulugulu.models.IdentityProfileGetResponseModel;
import com.mrkresnofatihdev.gulugulu.models.IdentityProfileUserCreateRequestModel;

public interface IIdentityProfileService {
    IdentityProfileGetResponseModel CreateForUser(IdentityProfileUserCreateRequestModel identityUserProfileCreateRequest);
    IdentityProfileGetResponseModel GetForUser(IdentityProfileUserCreateRequestModel identityUserProfileCreateRequest);
    String GetIdentityNameOfUser(String username);
}
