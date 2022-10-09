package com.mrkresnofatihdev.gulugulu.controllers;

import com.mrkresnofatihdev.gulugulu.models.ResponseModel;
import com.mrkresnofatihdev.gulugulu.models.UserProfileGetRequestModel;
import com.mrkresnofatihdev.gulugulu.models.UserProfileGetResponseModel;
import com.mrkresnofatihdev.gulugulu.models.UserProfileUpdateRequestModel;
import com.mrkresnofatihdev.gulugulu.services.IUserProfileService;
import com.mrkresnofatihdev.gulugulu.utilities.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/user-profile")
public class UserProfileController {
    private final IUserProfileService userProfileService;

    @Autowired
    public UserProfileController(IUserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @PostMapping("/get")
    public ResponseEntity<ResponseModel<UserProfileGetResponseModel>> GetUserProfile(@Valid @RequestBody UserProfileGetRequestModel userProfileGetRequest) {
        var userProfile = userProfileService.GetUserProfile(userProfileGetRequest);
        return ResponseHelper.BuildOkResponse(userProfile);
    }

    @PostMapping("/update")
    public ResponseEntity<ResponseModel<UserProfileGetResponseModel>> UpdateUserProfile(@Valid @RequestBody UserProfileUpdateRequestModel userProfileUpdateRequest) {
        var userProfile = userProfileService.UpdateUserProfile(userProfileUpdateRequest);
        return ResponseHelper.BuildOkResponse(userProfile);
    }
}
