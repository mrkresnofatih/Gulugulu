package com.mrkresnofatihdev.gulugulu.services;

import com.mrkresnofatihdev.gulugulu.models.AuthUserLoginRequestModel;
import com.mrkresnofatihdev.gulugulu.models.AuthUserLoginResponseModel;
import com.mrkresnofatihdev.gulugulu.models.UserCredentialsGetRequestModel;
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
    private final JwtHelper jwtHelper;
    private final HashHelper hashHelper;
    private final Logger logger;

    @Autowired
    public AuthService(IUserCredentialsService userCredentialsService, JwtHelper jwtHelper, HashHelper hashHelper) {
        this.userCredentialsService = userCredentialsService;
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
}
