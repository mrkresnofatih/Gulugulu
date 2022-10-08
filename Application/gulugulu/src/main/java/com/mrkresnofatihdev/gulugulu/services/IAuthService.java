package com.mrkresnofatihdev.gulugulu.services;

import com.mrkresnofatihdev.gulugulu.models.AuthUserLoginRequestModel;
import com.mrkresnofatihdev.gulugulu.models.AuthUserLoginResponseModel;

public interface IAuthService {
    AuthUserLoginResponseModel UserLogin(AuthUserLoginRequestModel authUserLoginRequest);
}
