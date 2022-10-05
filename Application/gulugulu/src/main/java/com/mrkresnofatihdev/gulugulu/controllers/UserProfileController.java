package com.mrkresnofatihdev.gulugulu.controllers;

import com.mrkresnofatihdev.gulugulu.models.ResponseModel;
import com.mrkresnofatihdev.gulugulu.models.UserProfileEntity;
import com.mrkresnofatihdev.gulugulu.models.UserProfileCreateRequestModel;
import com.mrkresnofatihdev.gulugulu.models.UserProfileGetResponseModel;
import com.mrkresnofatihdev.gulugulu.services.IUserProfileService;
import com.mrkresnofatihdev.gulugulu.utilities.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user-profile")
public class UserProfileController {
    private final IUserProfileService userProfileService;

    @Autowired
    public UserProfileController(IUserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseModel<UserProfileGetResponseModel>> CreateUserProfile(@RequestBody UserProfileCreateRequestModel userProfileCreateRequest) {
        var newUserProfile = userProfileService.SaveUserProfile(userProfileCreateRequest);
        return ResponseHelper.BuildOkResponse(newUserProfile);
    }

    @GetMapping("/get/{username}")
    public ResponseEntity<ResponseModel<UserProfileGetResponseModel>> GetUserProfile(@PathVariable String username) {
        var userProfile = userProfileService.GetUserProfile(username);
        return ResponseHelper.BuildOkResponse(userProfile);
    }
}
