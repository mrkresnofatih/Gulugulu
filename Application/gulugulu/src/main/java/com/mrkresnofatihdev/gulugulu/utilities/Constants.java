package com.mrkresnofatihdev.gulugulu.utilities;

import java.util.List;
import java.util.Map;

public class Constants {
    public static final String DynamoDbTableName = "GuluguluTb";

    public static class DefaultValues {
        public static final String UserProfileAvatar = "https://i.imgur.com/hlMIFj7.jpg";
    }

    public static final List<String> AuthProtectedURIPrefixes = List
            .of(
                    "/api/v1/user-profile",
                    "/api/v1/user-credentials"
            );

    public static final Map<String, String> PermissionProtectedURIPrefixes = Map.of(
            "/api/v1/user-profile/get", PermissionNames.UserProfile_GetProfile,
            "/api/v1/user-profile/update", PermissionNames.UserProfile_UpdateProfile,
            "/api/v1/user-credentials/update", PermissionNames.UserCredentials_UpdateCredentials
    );

    public static final Map<String, String> URIPrefixResourceNamePrefixMap = Map.of(
            "/api/v1/user-profile", PermissionNames.ResourceNameFormats.UserProfile,
            "/api/v1/user-credentials", PermissionNames.ResourceNameFormats.UserCredentials
    );

    public static class PermissionNames
    {
        public static final String UserProfile_GetProfile = "UserProfile.GetProfile";
        public static final String UserProfile_UpdateProfile = "UserProfile.UpdateProfile";
        public static final String UserCredentials_UpdateCredentials = "UserCredentials.UpdateCredentials";

        public static class ResourceNameFormats
        {
            public static final String UserProfile = "UserProfile#%s";
            public static final String UserCredentials = "UserCredentials#%s";
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
        }

        public static final String ExchangeName = "guluguluExchange";

        public static class RoutingKeys
        {
            public static final String UserSignupRoute = "userSignupHandlerRoute";
        }
    }
}
