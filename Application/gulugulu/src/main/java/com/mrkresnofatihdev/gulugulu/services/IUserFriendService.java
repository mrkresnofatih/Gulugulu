package com.mrkresnofatihdev.gulugulu.services;

import com.mrkresnofatihdev.gulugulu.models.UserFriendCreateRequestModel;
import com.mrkresnofatihdev.gulugulu.models.UserFriendGetRequestModel;
import com.mrkresnofatihdev.gulugulu.models.UserFriendGetResponseModel;
import com.mrkresnofatihdev.gulugulu.models.UserFriendListRequestModel;

import java.util.List;

public interface IUserFriendService {
    UserFriendGetResponseModel SaveUserFriend(UserFriendCreateRequestModel userFriendCreateRequest);

    UserFriendGetResponseModel GetUserFriend(UserFriendGetRequestModel userFriendGetRequest);

    void DeleteUserFriend(UserFriendGetRequestModel userFriendDeleteRequest);

    List<UserFriendGetResponseModel> GetUserFriendList(UserFriendListRequestModel userFriendListRequest);
}
