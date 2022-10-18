package com.mrkresnofatihdev.gulugulu.controllers;

import com.mrkresnofatihdev.gulugulu.exceptions.NotMatchingResourceNameException;
import com.mrkresnofatihdev.gulugulu.models.*;
import com.mrkresnofatihdev.gulugulu.services.IFriendService;
import com.mrkresnofatihdev.gulugulu.services.IIdentityProfileService;
import com.mrkresnofatihdev.gulugulu.utilities.Constants;
import com.mrkresnofatihdev.gulugulu.utilities.ResponseHelper;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/friend")
public class FriendController {
    private final RabbitTemplate rabbitTemplate;
    private final DirectExchange guluguluExchange;
    private final IFriendService friendService;
    private final IIdentityProfileService identityProfileService;

    @Autowired
    public FriendController(
            RabbitTemplate rabbitTemplate,
            DirectExchange guluguluExchange,
            IFriendService friendService,
            IIdentityProfileService identityProfileService) {
        this.rabbitTemplate = rabbitTemplate;
        this.guluguluExchange = guluguluExchange;
        this.friendService = friendService;
        this.identityProfileService = identityProfileService;
    }

    @PostMapping("/send-friend-request")
    public ResponseEntity<ResponseModel<String>> SendFriendRequest(
            @RequestHeader(Constants.Http.Headers.ResourceName) String resourceName,
            @Valid @RequestBody FriendSendFriendRequestModel friendSendFriendRequest)
            throws NotMatchingResourceNameException {
        var isResourceNameValid = _UserPendingFriendResourceNameValid(resourceName, friendSendFriendRequest.getRequesterUsername());
        if (!isResourceNameValid) {
            throw new NotMatchingResourceNameException();
        }
        rabbitTemplate.convertAndSend(guluguluExchange.getName(), Constants.Rabbit.RoutingKeys.SendFriendRequestRoute, friendSendFriendRequest);
        return ResponseHelper.BuildOkResponse("Friend Request Sent!");
    }

    @PostMapping("/get-pending-requests")
    public ResponseEntity<ResponseModel<List<FriendGetFriendRequestResponseModel>>> GetPendingRequest(
            @RequestHeader(Constants.Http.Headers.ResourceName) String resourceName,
            @Valid @RequestBody FriendGetFriendRequestListRequestModel friendGetFriendRequestListRequest)
            throws NotMatchingResourceNameException {
        var isResourceNameValid = _UserPendingFriendResourceNameValid(resourceName, friendGetFriendRequestListRequest.getUsername());
        if (!isResourceNameValid) {
            throw new NotMatchingResourceNameException();
        }

        var listOfPendingRequests = friendService
                .GetSentFriendRequests(friendGetFriendRequestListRequest);
        return ResponseHelper.BuildOkResponse(listOfPendingRequests);
    }

    @PostMapping("/get-my-friend-requests")
    public ResponseEntity<ResponseModel<List<FriendGetFriendRequestResponseModel>>> GetMyFriendRequests(
            @RequestHeader(Constants.Http.Headers.ResourceName) String resourceName,
            @Valid @RequestBody FriendGetFriendRequestListRequestModel friendGetFriendRequestListRequest)
            throws NotMatchingResourceNameException {
        var isResourceNameValid = _UserFriendRequestResourceNameValid(resourceName, friendGetFriendRequestListRequest.getUsername());
        if (!isResourceNameValid) {
            throw new NotMatchingResourceNameException();
        }

        var listOfMyFriendRequests = friendService
                .GetReceivedFriendRequests(friendGetFriendRequestListRequest);
        return ResponseHelper.BuildOkResponse(listOfMyFriendRequests);
    }

    @PostMapping("/approve-friend-request")
    public ResponseEntity<ResponseModel<String>> ApproveMyFriendRequest(
            @RequestHeader(Constants.Http.Headers.ResourceName) String resourceName,
            @Valid @RequestBody FriendRespondFriendRequestModel approveFriendRequestModel)
            throws NotMatchingResourceNameException {
        var isResourceNameValid = _UserFriendRequestResourceNameValid(resourceName, approveFriendRequestModel.getUsername());
        if (!isResourceNameValid) {
            throw new NotMatchingResourceNameException();
        }

        friendService.ApproveReceivedFriendRequest(approveFriendRequestModel);
        return ResponseHelper.BuildOkResponse("Approved!");
    }

    @PostMapping("/reject-friend-request")
    public ResponseEntity<ResponseModel<String>> RejectMyFriendRequest(
            @RequestHeader(Constants.Http.Headers.ResourceName) String resourceName,
            @Valid @RequestBody FriendRespondFriendRequestModel rejectFriendRequestModel) throws NotMatchingResourceNameException {
        var isResourceNameValid = _UserFriendRequestResourceNameValid(resourceName, rejectFriendRequestModel.getUsername());
        if (!isResourceNameValid) {
            throw new NotMatchingResourceNameException();
        }

        friendService.RejectReceivedFriendRequest(rejectFriendRequestModel);
        return ResponseHelper.BuildOkResponse("Rejected!");
    }

    @PostMapping("/cancel-friend-request")
    public ResponseEntity<ResponseModel<String>> CancelSentFriendRequest(
            @RequestHeader(Constants.Http.Headers.ResourceName) String resourceName,
            @Valid @RequestBody FriendCancelSendFriendRequestModel friendCancelSendFriendRequest)
            throws NotMatchingResourceNameException {
        var isResourceNameValid = _UserPendingFriendResourceNameValid(resourceName, friendCancelSendFriendRequest.getUsername());
        if (!isResourceNameValid) {
            throw new NotMatchingResourceNameException();
        }

        friendService.CancelSendFriendRequest(friendCancelSendFriendRequest);
        return ResponseHelper.BuildOkResponse("Cancelled!");
    }

    @PostMapping("/get-friends")
    public ResponseEntity<ResponseModel<List<UserProfileGetResponseModel>>> GetFriends(
            @RequestHeader(Constants.Http.Headers.ResourceName) String resourceName,
            @Valid @RequestBody UserFriendListRequestModel userFriendListRequest)
            throws NotMatchingResourceNameException {
        var isResourceNameValid = _UserFriendResourceNameValid(resourceName, userFriendListRequest.getUsername());
        if (!isResourceNameValid) {
            throw new NotMatchingResourceNameException();
        }

        var userFriendList = friendService.GetFriendsList(userFriendListRequest);
        return ResponseHelper.BuildOkResponse(userFriendList);
    }

    private boolean _UserPendingFriendResourceNameValid(String resourceName, String username) {
        var userIdentityName = identityProfileService.GetIdentityNameOfUser(username);
        var supposedResourceName = String
                .format(Constants
                        .PermissionNames
                        .ResourceNameFormats
                        .UserPendingFriend, userIdentityName);
        return supposedResourceName.equals(resourceName);
    }

    private boolean _UserFriendRequestResourceNameValid(String resourceName, String username) {
        var userIdentityName = identityProfileService.GetIdentityNameOfUser(username);
        var supposedResourceName = String
                .format(Constants
                        .PermissionNames
                        .ResourceNameFormats
                        .UserFriendRequest, userIdentityName);
        return supposedResourceName.equals(resourceName);
    }

    private boolean _UserFriendResourceNameValid(String resourceName, String username) {
        var userIdentityName = identityProfileService.GetIdentityNameOfUser(username);
        var supposedResourceName = String
                .format(Constants
                        .PermissionNames
                        .ResourceNameFormats
                        .UserFriend, userIdentityName);
        return supposedResourceName.equals(resourceName);
    }
}
