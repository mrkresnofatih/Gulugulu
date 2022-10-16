package com.mrkresnofatihdev.gulugulu.services;

import com.mrkresnofatihdev.gulugulu.models.*;

import java.util.List;

public interface IFriendService {
    void SendFriendRequest(FriendSendFriendRequestModel sendFriendReqRequest);

    List<FriendGetFriendRequestResponseModel> GetSentFriendRequests(FriendGetFriendRequestListRequestModel friendGetFriendRequestListRequest);

    void CancelSendFriendRequest(FriendCancelSendFriendRequestModel friendCancelSendFriendRequestModel);

    List<FriendGetFriendRequestResponseModel> GetReceivedFriendRequests(FriendGetFriendRequestListRequestModel friendGetFriendRequestListRequest);

    void ApproveReceivedFriendRequest(FriendRespondFriendRequestModel friendRespondFriendRequest);

    void RejectReceivedFriendRequest(FriendRespondFriendRequestModel friendRespondFriendRequest);

    List<UserProfileGetResponseModel> GetFriendsList(UserFriendListRequestModel userFriendListRequest);
}
