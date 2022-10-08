package com.mrkresnofatihdev.gulugulu.controllers;

import com.mrkresnofatihdev.gulugulu.models.AuthUserLoginRequestModel;
import com.mrkresnofatihdev.gulugulu.models.AuthUserLoginResponseModel;
import com.mrkresnofatihdev.gulugulu.models.ResponseModel;
import com.mrkresnofatihdev.gulugulu.services.IAuthService;
import com.mrkresnofatihdev.gulugulu.utilities.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final IAuthService authService;

    @Autowired
    public AuthController(IAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/user-login")
    public ResponseEntity<ResponseModel<AuthUserLoginResponseModel>> UserLogin(@RequestBody AuthUserLoginRequestModel loginReq) {
        var loginResponse = authService.UserLogin(loginReq);
        return ResponseHelper.BuildOkResponse(loginResponse);
    }
}
