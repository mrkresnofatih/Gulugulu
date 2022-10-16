package com.mrkresnofatihdev.gulugulu.controllers;

import com.mrkresnofatihdev.gulugulu.models.*;
import com.mrkresnofatihdev.gulugulu.services.IFriendService;
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
import java.util.List;

@RestController
@RequestMapping("/api/v1/friend")
public class FriendController {
    private final RabbitTemplate rabbitTemplate;
    private final DirectExchange guluguluExchange;
    private final IFriendService friendService;

    @Autowired
    public FriendController(
            RabbitTemplate rabbitTemplate,
            DirectExchange guluguluExchange, IFriendService friendService) {
        this.rabbitTemplate = rabbitTemplate;
        this.guluguluExchange = guluguluExchange;
        this.friendService = friendService;
    }

    @PostMapping("/send-friend-request")
    public ResponseEntity<ResponseModel<String>> SendFriendRequest(
            @Valid @RequestBody FriendSendFriendRequestModel friendSendFriendRequest) {
        rabbitTemplate.convertAndSend(guluguluExchange.getName(), Constants.Rabbit.RoutingKeys.SendFriendRequestRoute, friendSendFriendRequest);
        return ResponseHelper.BuildOkResponse("Friend Request Sent!");
    }

    @PostMapping("/get-pending-requests")
    public ResponseEntity<ResponseModel<List<FriendGetFriendRequestResponseModel>>> GetPendingRequest(
            @Valid @RequestBody FriendGetFriendRequestListRequestModel friendGetFriendRequestListRequest) {
        var listOfPendingRequests = friendService
                .GetSentFriendRequests(friendGetFriendRequestListRequest);
        return ResponseHelper.BuildOkResponse(listOfPendingRequests);
    }

    @PostMapping("/get-my-friend-requests")
    public ResponseEntity<ResponseModel<List<FriendGetFriendRequestResponseModel>>> GetMyFriendRequests(
            @Valid @RequestBody FriendGetFriendRequestListRequestModel friendGetFriendRequestListRequest) {
        var listOfMyFriendRequests = friendService
                .GetReceivedFriendRequests(friendGetFriendRequestListRequest);
        return ResponseHelper.BuildOkResponse(listOfMyFriendRequests);
    }

    @PostMapping("/approve-friend-request")
    public ResponseEntity<ResponseModel<String>> ApproveMyFriendRequest(
            @Valid @RequestBody FriendRespondFriendRequestModel approveFriendRequestModel) {
        friendService.ApproveReceivedFriendRequest(approveFriendRequestModel);
        return ResponseHelper.BuildOkResponse("Approved!");
    }

    @PostMapping("/reject-friend-request")
    public ResponseEntity<ResponseModel<String>> RejectMyFriendRequest(
            @Valid @RequestBody FriendRespondFriendRequestModel approveFriendRequestModel) {
        friendService.RejectReceivedFriendRequest(approveFriendRequestModel);
        return ResponseHelper.BuildOkResponse("Rejected!");
    }

    @PostMapping("/get-friends")
    public ResponseEntity<ResponseModel<List<UserProfileGetResponseModel>>> GetFriends(
            @Valid @RequestBody UserFriendListRequestModel userFriendListRequest) {
        var userFriendList = friendService.GetFriendsList(userFriendListRequest);
        return ResponseHelper.BuildOkResponse(userFriendList);
    }
}
