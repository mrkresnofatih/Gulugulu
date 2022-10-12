package com.mrkresnofatihdev.gulugulu.controllers;

import com.mrkresnofatihdev.gulugulu.models.AuthUserSignupRequestModel;
import com.mrkresnofatihdev.gulugulu.services.IAuthService;
import com.mrkresnofatihdev.gulugulu.utilities.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthListener {
    private final IAuthService authService;
    private final Logger logger;

    @Autowired
    public AuthListener(IAuthService authService) {
        this.authService = authService;
        this.logger = LoggerFactory.getLogger(AuthListener.class);
    }

    @RabbitListener(queues = Constants.Rabbit.QueueNames.UserSignupQueue)
    private void handleUserSignup(AuthUserSignupRequestModel userSignupRequest) {
        logger.info(String.format("Starting method: handleUserSignup w/ params: %s", userSignupRequest.toJsonSerialized()));
        authService.UserSignup(userSignupRequest);
    }
}
