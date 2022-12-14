package com.mrkresnofatihdev.gulugulu.services;

import com.mrkresnofatihdev.gulugulu.models.UserFriendRequestCreateRequestModel;
import com.mrkresnofatihdev.gulugulu.models.UserFriendRequestGetRequestModel;
import com.mrkresnofatihdev.gulugulu.models.UserFriendRequestGetResponseModel;
import com.mrkresnofatihdev.gulugulu.models.UserFriendRequestListRequestModel;

import java.util.List;

public interface IUserFriendRequestService {
    void DeleteFriendRequest(UserFriendRequestGetRequestModel userFriendRequestDeleteRequest);

    UserFriendRequestGetResponseModel SaveFriendRequest(UserFriendRequestCreateRequestModel userFriendRequestCreateRequest);

    List<UserFriendRequestGetResponseModel> GetFriendRequestList(UserFriendRequestListRequestModel userFriendRequestListRequest);

    UserFriendRequestGetResponseModel GetFriendRequest(UserFriendRequestGetRequestModel userFriendRequestGetRequestModel);
}
