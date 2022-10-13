package com.mrkresnofatihdev.gulugulu.controllers;

import com.mrkresnofatihdev.gulugulu.exceptions.NotMatchingResourceNameException;
import com.mrkresnofatihdev.gulugulu.models.ResponseModel;
import com.mrkresnofatihdev.gulugulu.models.UserCredentialsCreateRequestModel;
import com.mrkresnofatihdev.gulugulu.models.UserCredentialsGetResponseModel;
import com.mrkresnofatihdev.gulugulu.services.IIdentityProfileService;
import com.mrkresnofatihdev.gulugulu.services.IUserCredentialsService;
import com.mrkresnofatihdev.gulugulu.utilities.Constants;
import com.mrkresnofatihdev.gulugulu.utilities.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/user-credentials")
public class UserCredentialsController {
    private final IUserCredentialsService userCredentialsService;
    private final IIdentityProfileService identityProfileService;

    @Autowired
    public UserCredentialsController(
            IUserCredentialsService userCredentialsService,
            IIdentityProfileService identityProfileService) {
        this.userCredentialsService = userCredentialsService;
        this.identityProfileService = identityProfileService;
    }

    @PostMapping("/update")
    public ResponseEntity<ResponseModel<UserCredentialsGetResponseModel>> UpdateUserCredentials(
            @RequestHeader(Constants.Http.Headers.ResourceName) String resourceName,
            @Valid @RequestBody UserCredentialsCreateRequestModel userCredentialsUpdateRequest)
            throws NotMatchingResourceNameException {
        var isResourceNameValid = _UserCredentialsResourceNameValid(resourceName, userCredentialsUpdateRequest.getUsername());
        if (!isResourceNameValid) {
            throw new NotMatchingResourceNameException();
        }
        var updatedUserCred = userCredentialsService.UpdateUserCredentials(userCredentialsUpdateRequest);
        return ResponseHelper.BuildOkResponse(updatedUserCred);
    }

    private boolean _UserCredentialsResourceNameValid(String resourceName, String username) {
        var userIdentityName = identityProfileService.GetIdentityNameOfUser(username);
        var supposedResourceName = String
                .format(Constants
                        .PermissionNames
                        .ResourceNameFormats
                        .UserCredentials, userIdentityName);
        return supposedResourceName.equals(resourceName);
    }
}
