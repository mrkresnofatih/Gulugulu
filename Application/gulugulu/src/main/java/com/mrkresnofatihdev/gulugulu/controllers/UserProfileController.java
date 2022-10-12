package com.mrkresnofatihdev.gulugulu.controllers;

import com.mrkresnofatihdev.gulugulu.exceptions.NotMatchingResourceNameException;
import com.mrkresnofatihdev.gulugulu.models.ResponseModel;
import com.mrkresnofatihdev.gulugulu.models.UserProfileGetRequestModel;
import com.mrkresnofatihdev.gulugulu.models.UserProfileGetResponseModel;
import com.mrkresnofatihdev.gulugulu.models.UserProfileUpdateRequestModel;
import com.mrkresnofatihdev.gulugulu.services.IIdentityProfileService;
import com.mrkresnofatihdev.gulugulu.services.IUserProfileService;
import com.mrkresnofatihdev.gulugulu.utilities.Constants;
import com.mrkresnofatihdev.gulugulu.utilities.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/user-profile")
public class UserProfileController {
    private final IUserProfileService userProfileService;
    private final IIdentityProfileService identityProfileService;

    @Autowired
    public UserProfileController(
            IUserProfileService userProfileService,
            IIdentityProfileService identityProfileService) {
        this.userProfileService = userProfileService;
        this.identityProfileService = identityProfileService;
    }

    @PostMapping("/get")
    public ResponseEntity<ResponseModel<UserProfileGetResponseModel>> GetUserProfile(
            @RequestHeader(Constants.Http.Headers.ResourceName) String resourceName,
            @Valid @RequestBody UserProfileGetRequestModel userProfileGetRequest)
            throws NotMatchingResourceNameException {
        var isResourceNameValid = _UserProfileResourceNameValid(resourceName, userProfileGetRequest.getUsername());
        if (!isResourceNameValid) {
            throw new NotMatchingResourceNameException();
        }
        var userProfile = userProfileService.GetUserProfile(userProfileGetRequest);
        return ResponseHelper.BuildOkResponse(userProfile);
    }

    @PostMapping("/update")
    public ResponseEntity<ResponseModel<UserProfileGetResponseModel>> UpdateUserProfile(
            @RequestHeader(Constants.Http.Headers.ResourceName) String resourceName,
            @Valid @RequestBody UserProfileUpdateRequestModel userProfileUpdateRequest)
            throws NotMatchingResourceNameException {
        var isResourceNameValid = _UserProfileResourceNameValid(resourceName, userProfileUpdateRequest.getUsername());
        if (!isResourceNameValid) {
            throw new NotMatchingResourceNameException();
        }
        var userProfile = userProfileService.UpdateUserProfile(userProfileUpdateRequest);
        return ResponseHelper.BuildOkResponse(userProfile);
    }

    private boolean _UserProfileResourceNameValid(String resourceName, String username) {
        var userIdentityName = identityProfileService.GetIdentityNameOfUser(username);
        var supposedResourceName = String
                .format(Constants
                        .PermissionNames
                        .ResourceNameFormats
                        .UserProfile, userIdentityName);
        return supposedResourceName.equals(resourceName);
    }
}
