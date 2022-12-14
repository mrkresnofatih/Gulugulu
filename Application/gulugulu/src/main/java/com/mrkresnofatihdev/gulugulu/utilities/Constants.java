package com.mrkresnofatihdev.gulugulu.utilities;

import java.util.List;
import java.util.Map;

public class Constants {
    public static final String DynamoDbTableName = "GuluguluTb";

    public static class DefaultValues {
        public static final String UserProfileAvatar = "https://i.imgur.com/hlMIFj7.jpg";
    }

    public static class PermissionNames
    {
        public static final String UserProfile_GetProfile = "UserProfile.GetProfile";
        public static final String UserProfile_UpdateProfile = "UserProfile.UpdateProfile";
        public static final String UserCredentials_UpdateCredentials = "UserCredentials.UpdateCredentials";
        public static final String UserPendingFriend_SendRequest = "UserPendingFriend.SendRequest";
        public static final String UserPendingFriend_GetRequest = "UserPendingFriend.GetRequest";
        public static final String UserPendingFriend_CancelRequest = "UserPendingFriend.CancelRequest";
        public static final String UserFriendRequest_RespondRequest = "UserFriendRequest.RespondRequest";
        public static final String UserFriendRequest_GetRequest = "UserFriendRequest.GetRequest";
        public static final String UserFriend_GetFriends = "UserFriend.GetFriends";
        public static final String UserNotification_GetNotification = "UserNotification.GetNotification";

        public static class ResourceNameFormats
        {
            public static final String UserProfile = "UserProfile#%s";
            public static final String UserCredentials = "UserCredentials#%s";
            public static final String UserPendingFriend = "UserPendingFriend#%s";
            public static final String UserFriendRequest = "UserFriendRequest#%s";
            public static final String UserFriend = "UserFriend#%s";
            public static final String UserNotification = "UserNotification#%s";
        }
    }

    public static class Jwt {
        public static class Claim
        {
            public static final String IdentityName = "identityName";
            public static final String WillExpireAt = "willExpireAt";
            public static final String JwtTknIssuer = "jwtTknIssuer";
        }

        public static final String JwtTknIssuer = "Gulugulu.com";
    }

    public static class Http
    {
        public static class Headers
        {
            public static final String ResourceName = "ResourceName";
        }
    }

    public static class Rabbit
    {
        public static class QueueNames
        {
            public static final String UserSignupQueue = "userSignupHandlerQueue";
            public static final String SendFriendRequestQueue = "sendFriendRequestHandlerQueue";
            public static final String AcknowledgeNotificationsQueue = "acknowledgeNotificationsHandlerQueue";
        }

        public static final String ExchangeName = "guluguluExchange";

        public static class RoutingKeys
        {
            public static final String UserSignupRoute = "userSignupHandlerRoute";
            public static final String SendFriendRequestRoute = "sendFriendRequestHandlerRoute";
            public static final String AcknowledgeNotificationsRoute = "acknowledgeNotificationsHandlerRoute";
        }
    }
}
