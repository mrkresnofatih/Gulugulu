package com.mrkresnofatihdev.gulugulu.services;

import com.mrkresnofatihdev.gulugulu.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FriendService implements IFriendService {
    private final Logger logger;
    private final IUserFriendRequestService userFriendRequestService;
    private final IUserPendingFriendService userPendingFriendService;
    private final IUserProfileService userProfileService;
    private final IUserNotificationService userNotificationService;
    private final IUserFriendService userFriendService;

    @Autowired
    public FriendService(
            IUserFriendRequestService userFriendRequestService,
            IUserPendingFriendService userPendingFriendService,
            IUserProfileService userProfileService,
            IUserNotificationService userNotificationService, IUserFriendService userFriendService) {
        this.userFriendRequestService = userFriendRequestService;
        this.userPendingFriendService = userPendingFriendService;
        this.userProfileService = userProfileService;
        this.userNotificationService = userNotificationService;
        this.userFriendService = userFriendService;
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

        try {
            var foundUserFriend = userFriendService
                    .GetUserFriend(
                            new UserFriendGetRequestModel(
                                    requesterProfile.getUsername(),
                                    targetUserProfile.getUsername()
                            ));
            if (!Objects.isNull(foundUserFriend)) {
                logger.warn("userFriend w/ provided friend username already exists, will terminate send-friend-request process");
                return;
            }
        }
        catch (Exception e) {
            logger.info("UserFriend doesn't exist yet, will continue to send friend request");
        }

        var existingRequesterFriendRequest = _GetFriendRequestOfRequester(
                targetUserProfile.getUsername(),
                requesterProfile.getUsername()
        );
        if (!Objects.isNull(existingRequesterFriendRequest)) {
            _RemoveFriendRequestsGetRequester(
                    new FriendRespondFriendRequestModel(
                            existingRequesterFriendRequest.getUsername(),
                            existingRequesterFriendRequest.getCreatedAt()
                    ));
        }

        var existingFriendRequest = _GetFriendRequestOfRequester(
                requesterProfile.getUsername(),
                targetUserProfile.getUsername()
        );
        if (!Objects.isNull(existingFriendRequest)) {
            _RemoveFriendRequestsGetRequester(
                    new FriendRespondFriendRequestModel(
                            existingFriendRequest.getUsername(),
                            existingFriendRequest.getCreatedAt()
                    ));
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
                                sentFriendReq.getUsername(),
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
    public void CancelSendFriendRequest(FriendCancelSendFriendRequestModel friendCancelSendFriendRequest) {
        logger.info(String.format("Start method: CancelSendFriendRequest w/ param: %s", friendCancelSendFriendRequest.toJsonSerialized()));
        var foundUserPendingFriend = userPendingFriendService
                .GetPendingFriend(
                        new UserPendingFriendGetRequestModel(
                                friendCancelSendFriendRequest.getUsername(),
                                friendCancelSendFriendRequest.getCreatedAt()));
        logger.info("Found user pending friend");
        var requesterUsername = friendCancelSendFriendRequest.getUsername();
        var friendUsername = foundUserPendingFriend.getFriendUsername();
        var isFriendRequestFound = false;
        var startCreatedAt = "000000000000000";
        var loopFinish = false;
        UserFriendRequestGetResponseModel foundUserFriendRequest = null;
        while (!isFriendRequestFound && !loopFinish) {
            var friendRequestList = userFriendRequestService
                    .GetFriendRequestList(new UserFriendRequestListRequestModel(
                            friendUsername,
                            startCreatedAt,
                            20
                    ));
            var friendReqMap = friendRequestList.stream()
                    .collect(Collectors.toMap(UserFriendRequestGetResponseModel::getRequesterUsername, s -> s));
            if (friendReqMap.keySet().size() < 20) {
                if (friendReqMap.containsKey(requesterUsername)) {
                    logger.info("Found user friend request");
                    foundUserFriendRequest = friendReqMap.get(requesterUsername);
                }
                loopFinish = true;
            } else {
                if (friendReqMap.containsKey(requesterUsername)) {
                    logger.info("Found user friend request");
                    foundUserFriendRequest = friendReqMap.get(requesterUsername);
                    loopFinish = true;
                } else {
                    startCreatedAt = friendRequestList.get(19).getCreatedAt();
                }
            }
        }

        if (!Objects.isNull(foundUserFriendRequest)) {
            userFriendRequestService.DeleteFriendRequest(new UserFriendRequestGetRequestModel(
                    foundUserFriendRequest.getUsername(),
                    foundUserFriendRequest.getCreatedAt()
            ));
            logger.info("deleted user friend request");
        } else {
            logger.warn("Failed to find friend request that we're trying to delete, probably already deleted");
        }

        userPendingFriendService
                .DeletePendingFriend(
                        new UserPendingFriendGetRequestModel(
                                foundUserPendingFriend.getUsername(),
                                foundUserPendingFriend.getCreatedAt()
                        ));
        logger.info("deleted user pending friend");
        logger.info("Finished method: CancelSendFriendRequest");
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
        logger.info(String.format("Start method: ApproveReceivedFriendRequest w/ params: %s", friendRespondFriendRequest.toJsonSerialized()));
        var requesterUsername = _RemoveFriendRequestsGetRequester(friendRespondFriendRequest);
        var requestedUserFriend = userFriendService
                .SaveUserFriend(
                        new UserFriendCreateRequestModel(
                                friendRespondFriendRequest.getUsername(),
                                requesterUsername)
                );
        logger.info(String.format("successfully created user friend for requested friend w/ response: %s", requestedUserFriend.toJsonSerialized()));

        var requesterUserFriend = userFriendService
                .SaveUserFriend(
                        new UserFriendCreateRequestModel(
                                requesterUsername,
                                friendRespondFriendRequest.getUsername()
                        )
                );
        logger.info(String.format("successfully created user friend for requester w/ response: %s", requesterUserFriend.toJsonSerialized()));

        var approverProfile = userProfileService
                .GetUserProfile(
                        new UserProfileGetRequestModel(friendRespondFriendRequest.getUsername()));
        var friendReqApprovedNotificationCreateRequest = new FriendRequestApprovedNotificationCreateModel(
                requesterUsername,
                approverProfile.getUsername(),
                approverProfile.getAvatar()
        );
        var createdNotification = userNotificationService.CreateUserNotification(friendReqApprovedNotificationCreateRequest);
        logger.info(String.format("Created friend-request-approved notification w/ response: %s", createdNotification.toJsonSerialized()));
        logger.info("Finish method: ApproveReceivedFriendRequest");
    }

    @Override
    public void RejectReceivedFriendRequest(FriendRespondFriendRequestModel friendRespondFriendRequest) {
        logger.info(String.format("Start method: RejectReceivedFriendRequest w/ params: %s", friendRespondFriendRequest.toJsonSerialized()));
        _RemoveFriendRequestsGetRequester(friendRespondFriendRequest);
        logger.info("Finish method: RejectReceivedFriendRequest");
    }

    @Override
    public List<UserProfileGetResponseModel> GetFriendsList(UserFriendListRequestModel userFriendListRequest) {
        logger.info(String.format("Start method: GetFriendsList w/ param: %s", userFriendListRequest.toJsonSerialized()));
        var userFriendList = userFriendService
                .GetUserFriendList(
                        new UserFriendListRequestModel(
                                userFriendListRequest.getUsername(),
                                userFriendListRequest.getStartFriendUsername(),
                                userFriendListRequest.getPageSize()
                        )
                );
        var userFriendProfiles = new ArrayList<UserProfileGetResponseModel>();
        for(var userFriend : userFriendList) {
            var userFriendProfile = userProfileService
                    .GetUserProfile(
                            new UserProfileGetRequestModel(userFriend.getFriendUsername()));
            userFriendProfiles.add(userFriendProfile);
        }
        logger.info("Finish method: GetFriendList");
        return userFriendProfiles;
    }

    private String _RemoveFriendRequestsGetRequester(FriendRespondFriendRequestModel friendRespondFriendRequest) {
        var foundUserFriendRequest = userFriendRequestService
                .GetFriendRequest(
                        new UserFriendRequestGetRequestModel(
                                friendRespondFriendRequest.getUsername(),
                                friendRespondFriendRequest.getCreatedAt()
                        ));
        logger.info("Found user friend request");
        var requesterUsername = foundUserFriendRequest.getRequesterUsername();
        var friendUsername = foundUserFriendRequest.getUsername();
        var isPendingFriendFound = false;
        var startCreatedAt = "000000000000000";
        var loopFinish = false;
        UserPendingFriendGetResponseModel foundUserPendingFriend = null;
        while (!isPendingFriendFound && !loopFinish) {
            var pendingFriendsList = userPendingFriendService
                    .GetPendingFriendList(new UserPendingFriendListRequestModel(
                            requesterUsername,
                            startCreatedAt,
                            20
                    ));
            var pdgFriendMap = pendingFriendsList.stream()
                    .collect(Collectors.toMap(UserPendingFriendGetResponseModel::getFriendUsername, s -> s));
            if (pdgFriendMap.keySet().size() < 20) {
                if (pdgFriendMap.containsKey(friendUsername)) {
                    logger.info("Found user pending friend");
                    foundUserPendingFriend = pdgFriendMap.get(friendUsername);
                }
                loopFinish = true;
            } else {
                if (pdgFriendMap.containsKey(friendUsername)) {
                    logger.info("Found user pending friend");
                    foundUserPendingFriend = pdgFriendMap.get(friendUsername);
                    loopFinish = true;
                } else {
                    startCreatedAt = pendingFriendsList.get(19).getCreatedAt();
                }
            }
        }

        if (!Objects.isNull(foundUserPendingFriend)) {
            userPendingFriendService.DeletePendingFriend(new UserPendingFriendGetRequestModel(
                    foundUserPendingFriend.getUsername(),
                    foundUserPendingFriend.getCreatedAt()
            ));
            logger.info("deleted user pending friend");
        } else {
            logger.warn("Failed to find pending friend that we're trying to delete, probably already deleted");
        }

        userFriendRequestService
                .DeleteFriendRequest(
                        new UserFriendRequestGetRequestModel(
                                foundUserFriendRequest.getUsername(),
                                foundUserFriendRequest.getCreatedAt()
                        ));
        logger.info("deleted user friend request");
        logger.info("finished method: ApproveReceivedFriendRequest");

        return requesterUsername;
    }

    private UserFriendRequestGetResponseModel _GetFriendRequestOfRequester(String requesterUsername, String friendUsername) {
        var isFriendRequestFound = false;
        var startCreatedAt = "000000000000000";
        var loopFinish = false;
        UserFriendRequestGetResponseModel foundUserFriendRequest = null;
        while (!isFriendRequestFound && !loopFinish) {
            var friendRequestList = userFriendRequestService
                    .GetFriendRequestList(new UserFriendRequestListRequestModel(
                            friendUsername,
                            startCreatedAt,
                            20
                    ));
            var friendReqMap = friendRequestList.stream()
                    .collect(Collectors.toMap(UserFriendRequestGetResponseModel::getRequesterUsername, s -> s));
            if (friendReqMap.keySet().size() < 20) {
                if (friendReqMap.containsKey(requesterUsername)) {
                    logger.info("Found user friend request");
                    foundUserFriendRequest = friendReqMap.get(requesterUsername);
                }
                loopFinish = true;
            } else {
                if (friendReqMap.containsKey(requesterUsername)) {
                    logger.info("Found user friend request");
                    foundUserFriendRequest = friendReqMap.get(requesterUsername);
                    loopFinish = true;
                } else {
                    startCreatedAt = friendRequestList.get(19).getCreatedAt();
                }
            }
        }
        return foundUserFriendRequest;
    }
}
