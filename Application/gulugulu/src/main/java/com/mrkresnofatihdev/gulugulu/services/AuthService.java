package com.mrkresnofatihdev.gulugulu.services;

import com.mrkresnofatihdev.gulugulu.models.*;
import com.mrkresnofatihdev.gulugulu.utilities.HashHelper;
import com.mrkresnofatihdev.gulugulu.utilities.JwtHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthService implements IAuthService {
    private final IUserCredentialsService userCredentialsService;
    private final IUserProfileService userProfileService;
    private final IIdentityProfileService identityProfileService;
    private final JwtHelper jwtHelper;
    private final HashHelper hashHelper;
    private final Logger logger;

    @Autowired
    public AuthService(IUserCredentialsService userCredentialsService, IUserProfileService userProfileService, IIdentityProfileService identityProfileService, JwtHelper jwtHelper, HashHelper hashHelper) {
        this.userCredentialsService = userCredentialsService;
        this.userProfileService = userProfileService;
        this.identityProfileService = identityProfileService;
        this.jwtHelper = jwtHelper;
        this.hashHelper = hashHelper;
        this.logger = LoggerFactory.getLogger(AuthService.class);
    }

    @Override
    public AuthUserLoginResponseModel UserLogin(AuthUserLoginRequestModel userLoginReq) {
        logger.info(String.format("Starting Method: UserLogin w/ params: %s", userLoginReq.toJsonSerialized()));
        var getUserCredReq = new UserCredentialsGetRequestModel(userLoginReq.getUsername());
        var foundUserCredentials = userCredentialsService.GetUserCredentials(getUserCredReq);
        var isPasswordValid = hashHelper.CheckHash(userLoginReq.getPassword(), foundUserCredentials.getPassword());
        if (isPasswordValid) {
            var getTokenResponse = jwtHelper.GetToken(foundUserCredentials.getUsername());
            if (!Objects.isNull(getTokenResponse.getErrorMessage())) {
                logger.error("Get token response error message is not null");
                throw new RuntimeException(new Exception("Failed To Generate Token"));
            }
            logger.info("Finishing method: UserLogin");
            return new AuthUserLoginResponseModel(foundUserCredentials.getUsername(), getTokenResponse.getData());
        }
        logger.error("Password is invalid!");
        throw new RuntimeException(new Exception("Password for login is invalid!"));
    }

    @Override
    public void UserSignup(AuthUserSignupRequestModel authUserSignupReq) {
        logger.info(String.format("Starting Method: UserSignup w/ params: %s", authUserSignupReq.toJsonSerialized()));

        logger.info("Starting saving user credentials to db");
        try {
            var userCredCreateReq = new UserCredentialsCreateRequestModel();
            userCredCreateReq.setUsername(authUserSignupReq.getUsername());
            userCredCreateReq.setPassword(authUserSignupReq.getPassword());
            var userCredCreateResponse = userCredentialsService.SaveUserCredentials(userCredCreateReq);
            logger.info(String.format("Completed saving user credentials w/ response: %s", userCredCreateResponse.toJsonSerialized()));
        }
        catch (Exception e) {
            logger.error("Failed when saving user credentials!");
            throw e;
        }

        logger.info("Starting saving user profile to db");
        try {
            var userProfileCreateReq = new UserProfileCreateRequestModel();
            userProfileCreateReq.setUsername(authUserSignupReq.getUsername());
            userProfileCreateReq.setEmail(authUserSignupReq.getEmail());
            userProfileCreateReq.setFullname(authUserSignupReq.getFullname());
            var userProfileCreateResponse = userProfileService.SaveUserProfile(userProfileCreateReq);
            logger.info(String.format("Completed saving user profile w/ response: %s", userProfileCreateResponse.toJsonSerialized()));
        }
        catch (Exception e) {
            logger.error("Failed when saving user profile!");
            throw e;
        }

        logger.info("Starting saving identity profile to db");
        try {
            var identityProfileCreateReq = new IdentityUserProfileCreateRequestModel(authUserSignupReq.getUsername());
            var identityProfileCreateResponse = identityProfileService.CreateForUser(identityProfileCreateReq);
            logger.info(String.format("Completed saving identity profile w/ response: %s", identityProfileCreateResponse.toJsonSerialized()));
        }
        catch (Exception e) {
            logger.error("Failed when saving identity profile!");
            throw e;
        }
    }
}
