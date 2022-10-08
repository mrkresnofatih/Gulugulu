package com.mrkresnofatihdev.gulugulu.utilities;

import java.util.List;

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

    public static class Jwt {
        public static class Claim
        {
            public static final String IdentityName = "identityName";
            public static final String WillExpireAt = "willExpireAt";
            public static final String JwtTknIssuer = "jwtTknIssuer";
        }

        public static final String JwtTknIssuer = "Gulugulu.com";
    }
}
