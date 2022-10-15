package com.mrkresnofatihdev.gulugulu.services;

import com.mrkresnofatihdev.gulugulu.models.UserPendingFriendCreateRequestModel;
import com.mrkresnofatihdev.gulugulu.models.UserPendingFriendDeleteRequestModel;
import com.mrkresnofatihdev.gulugulu.models.UserPendingFriendGetResponseModel;
import com.mrkresnofatihdev.gulugulu.models.UserPendingFriendListRequestModel;

import java.util.List;

public interface IUserPendingFriendService {
    void DeletePendingFriend(UserPendingFriendDeleteRequestModel userPendingFriendDeleteRequest);

    List<UserPendingFriendGetResponseModel> GetPendingFriendList(UserPendingFriendListRequestModel userPendingFriendListRequest);

    UserPendingFriendGetResponseModel SavePendingFriend(UserPendingFriendCreateRequestModel userPendingFriendCreateRequest);
}
