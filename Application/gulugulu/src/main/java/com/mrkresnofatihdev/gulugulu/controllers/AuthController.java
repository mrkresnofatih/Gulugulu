package com.mrkresnofatihdev.gulugulu.controllers;

import com.mrkresnofatihdev.gulugulu.models.AuthUserLoginRequestModel;
import com.mrkresnofatihdev.gulugulu.models.AuthUserLoginResponseModel;
import com.mrkresnofatihdev.gulugulu.models.AuthUserSignupRequestModel;
import com.mrkresnofatihdev.gulugulu.models.ResponseModel;
import com.mrkresnofatihdev.gulugulu.services.IAuthService;
import com.mrkresnofatihdev.gulugulu.utilities.Constants;
import com.mrkresnofatihdev.gulugulu.utilities.ResponseHelper;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final IAuthService authService;
    private final RabbitTemplate rabbitTemplate;
    private final DirectExchange guluguluExchange;

    @Autowired
    public AuthController(
            IAuthService authService,
            RabbitTemplate rabbitTemplate,
            DirectExchange guluguluExchange) {
        this.authService = authService;
        this.guluguluExchange = guluguluExchange;
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping("/user-login")
    public ResponseEntity<ResponseModel<AuthUserLoginResponseModel>> UserLogin(@Valid @RequestBody AuthUserLoginRequestModel loginReq) {
        var loginResponse = authService.UserLogin(loginReq);
        return ResponseHelper.BuildOkResponse(loginResponse);
    }

    @PostMapping("/user-signup")
    public ResponseEntity<ResponseModel<String>> UserSignup(@Valid @RequestBody AuthUserSignupRequestModel authUserSignupRequestModel) {
        rabbitTemplate.convertAndSend(guluguluExchange.getName(), Constants.Rabbit.RoutingKeys.UserSignupRoute, authUserSignupRequestModel);
        return ResponseHelper.BuildOkResponse("User Signup Initialized!");
    }
}
