package com.mrkresnofatihdev.gulugulu.utilities;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.mrkresnofatihdev.gulugulu.models.ResponseModel;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public class JwtHelper {
    private final String secret;

    public JwtHelper(String secret) {
        this.secret = secret;
    }

    public ResponseModel<String> GetToken(String identityName) {
        try {
            var algorithm = Algorithm.HMAC256(secret);
            var token = JWT.create()
                    .withClaim(Constants.Jwt.Claim.IdentityName, identityName)
                    .withClaim(Constants.Jwt.Claim.WillExpireAt, Instant.now().plus(60, ChronoUnit.MINUTES))
                    .withClaim(Constants.Jwt.Claim.JwtTknIssuer, Constants.Jwt.JwtTknIssuer)
                    .sign(algorithm);
            return new ResponseModel<>(token, null);
        }
        catch (JWTCreationException exception) {
            return new ResponseModel<>(null, "Failed when creating JWT");
        }
    }

    public ResponseModel<Boolean> VerifyToken(String token) {
        try {
            var algorithm = Algorithm.HMAC256(secret);
            var verifier = JWT.require(algorithm)
                    .withClaimPresence(Constants.Jwt.Claim.JwtTknIssuer)
                    .withClaimPresence(Constants.Jwt.Claim.IdentityName)
                    .withClaimPresence(Constants.Jwt.Claim.WillExpireAt)
                    .withClaim(Constants.Jwt.Claim.JwtTknIssuer, Constants.Jwt.JwtTknIssuer)
                    .build();
            var decodedToken = verifier.verify(token);
            var willExpireAt = decodedToken.getClaim(Constants.Jwt.Claim.WillExpireAt).asInstant();
            var isNotExpired = Instant.now().isBefore(willExpireAt);
            if (!isNotExpired) {
                throw new JWTVerificationException("Token Expired");
            }
            return new ResponseModel<>(true, null);
        }
        catch (JWTVerificationException exception) {
            return new ResponseModel<>(false, exception.getMessage());
        }
    }

    public ResponseModel<Map<String, Claim>> GetClaims(String token) {
        try {
            var algorithm = Algorithm.HMAC256(secret);
            var verifier = JWT.require(algorithm)
                    .withClaimPresence(Constants.Jwt.Claim.JwtTknIssuer)
                    .withClaimPresence(Constants.Jwt.Claim.IdentityName)
                    .withClaimPresence(Constants.Jwt.Claim.WillExpireAt)
                    .withClaim(Constants.Jwt.Claim.JwtTknIssuer, Constants.Jwt.JwtTknIssuer)
                    .build();
            var decodedToken = verifier.verify(token);
            var claimsMap = decodedToken.getClaims();
            return new ResponseModel<>(claimsMap, null);
        }
        catch (JWTVerificationException e) {
            return new ResponseModel<>(null, "Failed getting claims from token!");
        }
    }
}
