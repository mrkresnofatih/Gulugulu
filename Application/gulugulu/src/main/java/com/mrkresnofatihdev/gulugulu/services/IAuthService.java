package com.mrkresnofatihdev.gulugulu.services;

import com.mrkresnofatihdev.gulugulu.models.AuthUserLoginRequestModel;
import com.mrkresnofatihdev.gulugulu.models.AuthUserLoginResponseModel;
import com.mrkresnofatihdev.gulugulu.models.AuthUserSignupRequestModel;

public interface IAuthService {
    AuthUserLoginResponseModel UserLogin(AuthUserLoginRequestModel authUserLoginRequest);

    void UserSignup(AuthUserSignupRequestModel authUserSignupRequestModel);
}
