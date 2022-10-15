package com.mrkresnofatihdev.gulugulu.services;

import com.mrkresnofatihdev.gulugulu.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FriendService implements IFriendService {
    private final Logger logger;
    private final IUserFriendRequestService userFriendRequestService;
    private final IUserPendingFriendService userPendingFriendService;
    private final IUserProfileService userProfileService;
    private final IUserNotificationService userNotificationService;

    @Autowired
    public FriendService(
            IUserFriendRequestService userFriendRequestService,
            IUserPendingFriendService userPendingFriendService,
            IUserProfileService userProfileService,
            IUserNotificationService userNotificationService) {
        this.userFriendRequestService = userFriendRequestService;
        this.userPendingFriendService = userPendingFriendService;
        this.userProfileService = userProfileService;
        this.userNotificationService = userNotificationService;
        this.logger = LoggerFactory.getLogger(FriendService.class);
    }

    @Override
    public void SendFriendRequest(FriendSendFriendRequestModel sendFriendReqRequest) {
        logger.info(String.format("Start method: FriendService.SendFriendRequest w/ param: %s", sendFriendReqRequest.toJsonSerialized()));
        UserProfileGetResponseModel requesterProfile;
        UserProfileGetResponseModel targetUserProfile;
        try {
            requesterProfile = userProfileService
                    .GetUserProfile(
                            new UserProfileGetRequestModel(sendFriendReqRequest
                                    .getRequesterUsername()));
            targetUserProfile = userProfileService
                    .GetUserProfile(
                            new UserProfileGetRequestModel(sendFriendReqRequest
                                    .getTargetUsername()));
        }
        catch (Exception e) {
            logger.error("Failed to retrieve profile of request sender or target user");
            throw e;
        }

        UserFriendRequestGetResponseModel createdFriendRequest;
        try {
            createdFriendRequest = userFriendRequestService
                    .SaveFriendRequest(
                            new UserFriendRequestCreateRequestModel(
                                    targetUserProfile.getUsername(),
                                    requesterProfile.getUsername())
                    );
        }
        catch (Exception e) {
            logger.error(String.format("Failed to create friend request of %s", targetUserProfile.getUsername()));
            throw e;
        }
        logger.info(String.format("Successfully created friend request: %s", createdFriendRequest.toJsonSerialized()));

        UserPendingFriendGetResponseModel createdPendingFriend;
        try {
            createdPendingFriend = userPendingFriendService
                    .SavePendingFriend(
                            new UserPendingFriendCreateRequestModel(
                                    requesterProfile.getUsername(),
                                    targetUserProfile.getUsername()
                            ));
        }
        catch (Exception e) {
            logger.error(String.format("Failed to create pending friend of %s", requesterProfile.getUsername()));
            throw e;
        }
        logger.info(String.format("Successfully created friend request: %s", createdPendingFriend.toJsonSerialized()));

        var friendRequestNotificationCreateReq = new FriendRequestReceivedNotificationCreateModel(
                requesterProfile.getUsername(),
                targetUserProfile.getUsername(),
                requesterProfile.getAvatar()
        );
        var notificationCreated = userNotificationService
                .CreateUserNotification(friendRequestNotificationCreateReq);
        logger.info(String.format("Friend request received notification created: %s", notificationCreated.toJsonSerialized()));
    }

    @Override
    public List<FriendGetFriendRequestResponseModel> GetSentFriendRequests(FriendGetFriendRequestListRequestModel friendGetFriendRequestListRequest) {
        logger.info(String.format("Start method: GetSentFriendRequests w/ params: %s", friendGetFriendRequestListRequest.toJsonSerialized()));
        var sentFriendRequests = userPendingFriendService
                .GetPendingFriendList(
                        new UserPendingFriendListRequestModel(
                                friendGetFriendRequestListRequest.getUsername(),
                                friendGetFriendRequestListRequest.getStartCreatedAt(),
                                friendGetFriendRequestListRequest.getPageSize()));
        var listOfFriendRequests = new ArrayList<FriendGetFriendRequestResponseModel>();
        for(var sentFriendReq : sentFriendRequests) {
            try {
                var foundProfile = userProfileService
                        .GetUserProfile(new UserProfileGetRequestModel(sentFriendReq.getFriendUsername()));
                listOfFriendRequests
                        .add(new FriendGetFriendRequestResponseModel(
                                sentFriendReq.getFriendUsername(),
                                sentFriendReq.getCreatedAt(),
                                foundProfile
                        ));
            }
            catch (Exception e) {
                logger.error(String.format("Cannot find profile for user %s", sentFriendReq.getFriendUsername()));
            }
        }
        return listOfFriendRequests;
    }

    @Override
    public void CancelSendFriendRequest(FriendCancelSendFriendRequestModel friendCancelSendFriendRequestModel) {

    }

    @Override
    public List<FriendGetFriendRequestResponseModel> GetReceivedFriendRequests(FriendGetFriendRequestListRequestModel friendGetFriendRequestListRequest) {
        logger.info(String.format("Start method: GetReceivedFriendRequests w/ params: %s", friendGetFriendRequestListRequest.toJsonSerialized()));
        var receivedRequests = userFriendRequestService
                .GetFriendRequestList(
                        new UserFriendRequestListRequestModel(
                                friendGetFriendRequestListRequest.getUsername(),
                                friendGetFriendRequestListRequest.getStartCreatedAt(),
                                friendGetFriendRequestListRequest.getPageSize()));
        var listOfRequests = new ArrayList<FriendGetFriendRequestResponseModel>();
        for(var receivedReq : receivedRequests) {
            try {
                var foundProfile = userProfileService
                        .GetUserProfile(new UserProfileGetRequestModel(receivedReq.getRequesterUsername()));
                listOfRequests
                        .add(new FriendGetFriendRequestResponseModel(
                                receivedReq.getUsername(),
                                receivedReq.getCreatedAt(),
                                foundProfile
                        ));
            }
            catch (Exception e) {
                logger.error(String.format("Cannot find profile for user %s", receivedReq.getRequesterUsername()));
            }
        }
        return listOfRequests;
    }

    @Override
    public void ApproveReceivedFriendRequest(FriendRespondFriendRequestModel friendRespondFriendRequest) {

    }

    @Override
    public void RejectReceivedFriendRequest(FriendRespondFriendRequestModel friendRespondFriendRequest) {

    }
}
