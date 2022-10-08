package com.mrkresnofatihdev.gulugulu.controllers;

import com.mrkresnofatihdev.gulugulu.models.ResponseModel;
import com.mrkresnofatihdev.gulugulu.models.UserCredentialsCreateRequestModel;
import com.mrkresnofatihdev.gulugulu.models.UserCredentialsGetRequestModel;
import com.mrkresnofatihdev.gulugulu.models.UserCredentialsGetResponseModel;
import com.mrkresnofatihdev.gulugulu.services.IUserCredentialsService;
import com.mrkresnofatihdev.gulugulu.utilities.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user-credentials")
public class UserCredentialsController {
    private final IUserCredentialsService userCredentialsService;

    @Autowired
    public UserCredentialsController(IUserCredentialsService userCredentialsService) {
        this.userCredentialsService = userCredentialsService;
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseModel<UserCredentialsGetResponseModel>> CreateUserCredentials(@RequestBody UserCredentialsCreateRequestModel userCredentialsCreateRequest) {
        var newUserCredentials = userCredentialsService.SaveUserCredentials(userCredentialsCreateRequest);
        return ResponseHelper.BuildOkResponse(newUserCredentials);
    }

    @PostMapping("/get")
    public ResponseEntity<ResponseModel<UserCredentialsGetResponseModel>> GetUserCredentials(@RequestBody UserCredentialsGetRequestModel userCredentialsGetRequest) {
        var userCred = userCredentialsService.GetUserCredentials(userCredentialsGetRequest);
        return ResponseHelper.BuildOkResponse(userCred);
    }

    @PostMapping("/update")
    public ResponseEntity<ResponseModel<UserCredentialsGetResponseModel>> UpdateUserCredentials(@RequestBody UserCredentialsCreateRequestModel userCredentialsUpdateRequest) {
        var updatedUserCred = userCredentialsService.UpdateUserCredentials(userCredentialsUpdateRequest);
        return ResponseHelper.BuildOkResponse(updatedUserCred);
    }
}
