package com.mrkresnofatihdev.gulugulu.services;

import com.mrkresnofatihdev.gulugulu.models.*;
import com.mrkresnofatihdev.gulugulu.utilities.Constants;
import com.mrkresnofatihdev.gulugulu.utilities.HashHelper;
import com.mrkresnofatihdev.gulugulu.utilities.JwtHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class AuthService implements IAuthService {
    private final IUserCredentialsService userCredentialsService;
    private final IUserProfileService userProfileService;
    private final IIdentityProfileService identityProfileService;
    private final IIdentityPermissionService identityPermissionService;
    private final JwtHelper jwtHelper;
    private final HashHelper hashHelper;
    private final Logger logger;

    @Autowired
    public AuthService(IUserCredentialsService userCredentialsService, IUserProfileService userProfileService, IIdentityProfileService identityProfileService, IIdentityPermissionService identityPermissionService, JwtHelper jwtHelper, HashHelper hashHelper) {
        this.userCredentialsService = userCredentialsService;
        this.userProfileService = userProfileService;
        this.identityProfileService = identityProfileService;
        this.identityPermissionService = identityPermissionService;
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
            logger.info("Password is valid!");
            var identityUserProfile = identityProfileService
                    .GetForUser(new IdentityProfileUserCreateRequestModel(userLoginReq.getUsername()));
            var getTokenResponse = jwtHelper.GetToken(identityUserProfile.getIdentityName());
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
            return;
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
            return;
        }

        logger.info("Starting saving identity profile to db");
        try {
            var identityProfileCreateReq = new IdentityProfileUserCreateRequestModel(authUserSignupReq.getUsername());
            var identityProfileCreateResponse = identityProfileService.CreateForUser(identityProfileCreateReq);
            logger.info(String.format("Completed saving identity profile w/ response: %s", identityProfileCreateResponse.toJsonSerialized()));
        }
        catch (Exception e) {
            logger.error("Failed when saving identity profile!");
            return;
        }

        logger.info("Starting initialize basic permissions for new user");
        try {
            logger.info("Create GetProfile Permission!");
            var username = authUserSignupReq
                    .getUsername();
            var permissionName = Constants.PermissionNames.UserProfile_GetProfile;
            var userProfilePermissionCreateReq = new IdentityPermissionCreateRequestModel();
            userProfilePermissionCreateReq.setPermissionName(permissionName);
            var identityName = identityProfileService
                    .GetIdentityNameOfUser(username);
            userProfilePermissionCreateReq.setIdentityName(identityName);
            List<String> resourceNames = List.of(
                    String.format(Constants.PermissionNames.ResourceNameFormats.UserProfile, "*")
            );
            userProfilePermissionCreateReq.setResourceNames(resourceNames);
            var createResponse = identityPermissionService
                    .CreatePermission(userProfilePermissionCreateReq);
            logger.info(String.format("Create Permission Done w/ response: %s", createResponse.toJsonSerialized()));
        }
        catch (Exception e) {
            logger.error("Failed when saving permission GetProfile");
            return;
        }

        try {
            logger.info("Create UpdateProfile Permission!");
            var username = authUserSignupReq
                    .getUsername();
            var permissionName = Constants.PermissionNames.UserProfile_UpdateProfile;
            var userProfilePermissionCreateReq = new IdentityPermissionCreateRequestModel();
            userProfilePermissionCreateReq.setPermissionName(permissionName);
            var identityName = identityProfileService
                    .GetIdentityNameOfUser(username);
            userProfilePermissionCreateReq.setIdentityName(identityName);
            List<String> resourceNames = List.of(
                    String.format(Constants.PermissionNames.ResourceNameFormats.UserProfile, identityName)
            );
            userProfilePermissionCreateReq.setResourceNames(resourceNames);
            var createResponse = identityPermissionService
                    .CreatePermission(userProfilePermissionCreateReq);
            logger.info(String.format("Create Permission Done w/ response: %s", createResponse.toJsonSerialized()));
        }
        catch (Exception e) {
            logger.error("Failed when saving permission UpdateProfile");
            return;
        }

        try {
            logger.info("Create UpdateCredentials Permission!");
            var username = authUserSignupReq
                    .getUsername();
            var permissionName = Constants.PermissionNames.UserCredentials_UpdateCredentials;
            var userProfilePermissionCreateReq = new IdentityPermissionCreateRequestModel();
            userProfilePermissionCreateReq.setPermissionName(permissionName);
            var identityName = identityProfileService
                    .GetIdentityNameOfUser(username);
            userProfilePermissionCreateReq.setIdentityName(identityName);
            List<String> resourceNames = List.of(
                    String.format(Constants.PermissionNames.ResourceNameFormats.UserCredentials, identityName)
            );
            userProfilePermissionCreateReq.setResourceNames(resourceNames);
            var createResponse = identityPermissionService
                    .CreatePermission(userProfilePermissionCreateReq);
            logger.info(String.format("Create Permission Done w/ response: %s", createResponse.toJsonSerialized()));
        }
        catch (Exception e) {
            logger.error("Failed when saving permission UpdateCredentials");
            return;
        }

        try {
            logger.info("Create SendRequest Permission!");
            var username = authUserSignupReq
                    .getUsername();
            var permissionName = Constants.PermissionNames.UserPendingFriend_SendRequest;
            var userProfilePermissionCreateReq = new IdentityPermissionCreateRequestModel();
            userProfilePermissionCreateReq.setPermissionName(permissionName);
            var identityName = identityProfileService
                    .GetIdentityNameOfUser(username);
            userProfilePermissionCreateReq.setIdentityName(identityName);
            List<String> resourceNames = List.of(
                    String.format(Constants.PermissionNames.ResourceNameFormats.UserPendingFriend, identityName)
            );
            userProfilePermissionCreateReq.setResourceNames(resourceNames);
            var createResponse = identityPermissionService
                    .CreatePermission(userProfilePermissionCreateReq);
            logger.info(String.format("Create Permission Done w/ response: %s", createResponse.toJsonSerialized()));
        }
        catch (Exception e) {
            logger.error("Failed when saving permission SendRequest");
            return;
        }

        try {
            logger.info("Create GetPendingFriend Permission!");
            var username = authUserSignupReq
                    .getUsername();
            var permissionName = Constants.PermissionNames.UserPendingFriend_GetRequest;
            var userProfilePermissionCreateReq = new IdentityPermissionCreateRequestModel();
            userProfilePermissionCreateReq.setPermissionName(permissionName);
            var identityName = identityProfileService
                    .GetIdentityNameOfUser(username);
            userProfilePermissionCreateReq.setIdentityName(identityName);
            List<String> resourceNames = List.of(
                    String.format(Constants.PermissionNames.ResourceNameFormats.UserPendingFriend, identityName)
            );
            userProfilePermissionCreateReq.setResourceNames(resourceNames);
            var createResponse = identityPermissionService
                    .CreatePermission(userProfilePermissionCreateReq);
            logger.info(String.format("Create Permission Done w/ response: %s", createResponse.toJsonSerialized()));
        }
        catch (Exception e) {
            logger.error("Failed when saving permission GetPendingFriend");
            return;
        }

        try {
            logger.info("Create GetFriendRequest Permission!");
            var username = authUserSignupReq
                    .getUsername();
            var permissionName = Constants.PermissionNames.UserFriendRequest_GetRequest;
            var userProfilePermissionCreateReq = new IdentityPermissionCreateRequestModel();
            userProfilePermissionCreateReq.setPermissionName(permissionName);
            var identityName = identityProfileService
                    .GetIdentityNameOfUser(username);
            userProfilePermissionCreateReq.setIdentityName(identityName);
            List<String> resourceNames = List.of(
                    String.format(Constants.PermissionNames.ResourceNameFormats.UserFriendRequest, identityName)
            );
            userProfilePermissionCreateReq.setResourceNames(resourceNames);
            var createResponse = identityPermissionService
                    .CreatePermission(userProfilePermissionCreateReq);
            logger.info(String.format("Create Permission Done w/ response: %s", createResponse.toJsonSerialized()));
        }
        catch (Exception e) {
            logger.error("Failed when saving permission GetFriendRequest");
            return;
        }

        try {
            logger.info("Create RespondFriendRequest Permission!");
            var username = authUserSignupReq
                    .getUsername();
            var permissionName = Constants.PermissionNames.UserFriendRequest_RespondRequest;
            var userProfilePermissionCreateReq = new IdentityPermissionCreateRequestModel();
            userProfilePermissionCreateReq.setPermissionName(permissionName);
            var identityName = identityProfileService
                    .GetIdentityNameOfUser(username);
            userProfilePermissionCreateReq.setIdentityName(identityName);
            List<String> resourceNames = List.of(
                    String.format(Constants.PermissionNames.ResourceNameFormats.UserFriendRequest, identityName)
            );
            userProfilePermissionCreateReq.setResourceNames(resourceNames);
            var createResponse = identityPermissionService
                    .CreatePermission(userProfilePermissionCreateReq);
            logger.info(String.format("Create Permission Done w/ response: %s", createResponse.toJsonSerialized()));
        }
        catch (Exception e) {
            logger.error("Failed when saving permission RespondFriendRequest");
            return;
        }

        try {
            logger.info("Create CancelPendingFriend Permission!");
            var username = authUserSignupReq
                    .getUsername();
            var permissionName = Constants.PermissionNames.UserPendingFriend_CancelRequest;
            var userProfilePermissionCreateReq = new IdentityPermissionCreateRequestModel();
            userProfilePermissionCreateReq.setPermissionName(permissionName);
            var identityName = identityProfileService
                    .GetIdentityNameOfUser(username);
            userProfilePermissionCreateReq.setIdentityName(identityName);
            List<String> resourceNames = List.of(
                    String.format(Constants.PermissionNames.ResourceNameFormats.UserPendingFriend, identityName)
            );
            userProfilePermissionCreateReq.setResourceNames(resourceNames);
            var createResponse = identityPermissionService
                    .CreatePermission(userProfilePermissionCreateReq);
            logger.info(String.format("Create Permission Done w/ response: %s", createResponse.toJsonSerialized()));
        }
        catch (Exception e) {
            logger.error("Failed when saving permission CancelPendingFriend");
            return;
        }

        try {
            logger.info("Create GetFriends Permission!");
            var username = authUserSignupReq
                    .getUsername();
            var permissionName = Constants.PermissionNames.UserFriend_GetFriends;
            var userProfilePermissionCreateReq = new IdentityPermissionCreateRequestModel();
            userProfilePermissionCreateReq.setPermissionName(permissionName);
            var identityName = identityProfileService
                    .GetIdentityNameOfUser(username);
            userProfilePermissionCreateReq.setIdentityName(identityName);
            List<String> resourceNames = List.of(
                    String.format(Constants.PermissionNames.ResourceNameFormats.UserFriend, identityName)
            );
            userProfilePermissionCreateReq.setResourceNames(resourceNames);
            var createResponse = identityPermissionService
                    .CreatePermission(userProfilePermissionCreateReq);
            logger.info(String.format("Create Permission Done w/ response: %s", createResponse.toJsonSerialized()));
        }
        catch (Exception e) {
            logger.error("Failed when saving permission GetFriends");
            return;
        }

        try {
            logger.info("Create GetUserNotifications Permission!");
            var username = authUserSignupReq
                    .getUsername();
            var permissionName = Constants.PermissionNames.UserNotification_GetNotification;
            var userProfilePermissionCreateReq = new IdentityPermissionCreateRequestModel();
            userProfilePermissionCreateReq.setPermissionName(permissionName);
            var identityName = identityProfileService
                    .GetIdentityNameOfUser(username);
            userProfilePermissionCreateReq.setIdentityName(identityName);
            List<String> resourceNames = List.of(
                    String.format(Constants.PermissionNames.ResourceNameFormats.UserNotification, identityName)
            );
            userProfilePermissionCreateReq.setResourceNames(resourceNames);
            var createResponse = identityPermissionService
                    .CreatePermission(userProfilePermissionCreateReq);
            logger.info(String.format("Create GetUserNotifications Done w/ response: %s", createResponse.toJsonSerialized()));
        }
        catch (Exception e) {
            logger.error("Failed when saving permission GetUserNotifications");
            return;
        }

        logger.info("Finishing method: UserSignup");
    }
}
