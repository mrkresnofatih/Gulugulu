package com.mrkresnofatihdev.gulugulu.services;

import com.mrkresnofatihdev.gulugulu.models.UserPendingFriendCreateRequestModel;
import com.mrkresnofatihdev.gulugulu.models.UserPendingFriendGetRequestModel;
import com.mrkresnofatihdev.gulugulu.models.UserPendingFriendGetResponseModel;
import com.mrkresnofatihdev.gulugulu.models.UserPendingFriendListRequestModel;

import java.util.List;

public interface IUserPendingFriendService {
    void DeletePendingFriend(UserPendingFriendGetRequestModel userPendingFriendDeleteRequest);

    List<UserPendingFriendGetResponseModel> GetPendingFriendList(UserPendingFriendListRequestModel userPendingFriendListRequest);

    UserPendingFriendGetResponseModel SavePendingFriend(UserPendingFriendCreateRequestModel userPendingFriendCreateRequest);

    UserPendingFriendGetResponseModel GetPendingFriend(UserPendingFriendGetRequestModel userPendingFriendGetRequest);
}
