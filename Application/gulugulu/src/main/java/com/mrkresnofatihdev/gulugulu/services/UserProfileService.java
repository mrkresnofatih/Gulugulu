package com.mrkresnofatihdev.gulugulu.services;

import com.mrkresnofatihdev.gulugulu.exceptions.RecordNotFoundException;
import com.mrkresnofatihdev.gulugulu.models.*;
import com.mrkresnofatihdev.gulugulu.utilities.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.Objects;

@Service
public class UserProfileService implements IUserProfileService {
    private final DynamoDbClient dynamoDbClient;
    private final Logger logger;

    @Autowired
    public UserProfileService(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
        logger = LoggerFactory.getLogger(UserProfileService.class);
    }

    private DynamoDbEnhancedClient _GetDynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    private String _GetUserProfilePartitionAndSortKey(String username) {
        return String.format("user-profile#%s", username);
    }

    @Override
    public UserProfileGetResponseModel SaveUserProfile(UserProfileCreateRequestModel userProfileCreateRequest) {
        logger.info(String.format("Starting Method: SaveUserProfile w/ Param: %s", userProfileCreateRequest.toJsonSerialized()));
        try {
            var enhancedClient = _GetDynamoDbEnhancedClient();
            DynamoDbTable<UserProfileEntity> userProfileTable = enhancedClient
                    .table(Constants.DynamoDbTableName, TableSchema.fromBean(UserProfileEntity.class));
            var newUserProfile = new UserProfileEntity.Builder()
                    .setUsername(userProfileCreateRequest.getUsername())
                    .setFullname(userProfileCreateRequest.getFullname())
                    .setAvatar(Constants.DefaultValues.UserProfileAvatar)
                    .setEmail(userProfileCreateRequest.getEmail())
                    .setBio(String.format("Hi! I am %s!", userProfileCreateRequest.getUsername()))
                    .build();
            userProfileTable.putItem(newUserProfile);
            var returnUserProfile = new UserProfileGetResponseModel(newUserProfile);
            logger.info("Finishing Method: SaveUserProfile");
            return returnUserProfile;
        } catch (DynamoDbException e) {
            logger.error("DynamoDB error at SaveUserProfile");
            throw e;
        }
    }

    @Override
    public UserProfileGetResponseModel GetUserProfile(UserProfileGetRequestModel userProfileGetRequest) {
        logger.info(String.format("Starting Method: GetUserProfile w/ Param: %s", userProfileGetRequest.toJsonSerialized()));
        try {
            var enhancedClient = _GetDynamoDbEnhancedClient();
            DynamoDbTable<UserProfileEntity> userProfileTable = enhancedClient
                    .table(Constants.DynamoDbTableName, TableSchema.fromBean(UserProfileEntity.class));
            var partitionSortKey = _GetUserProfilePartitionAndSortKey(userProfileGetRequest.getUsername());
            var userProfileKey = Key.builder()
                    .partitionValue(partitionSortKey)
                    .sortValue(partitionSortKey)
                    .build();
            var foundUserProfile = userProfileTable.getItem(r -> r.key(userProfileKey));
            if (Objects.isNull(foundUserProfile)) {
                throw new RecordNotFoundException();
            }
            var returnUserProfile = new UserProfileGetResponseModel(foundUserProfile);
            logger.info("Finishing Method: GetUserProfile");
            return returnUserProfile;
        } catch (DynamoDbException e) {
            logger.error("DynamoDB error at GetUserProfile");
            throw e;
        } catch (RecordNotFoundException e) {
            logger.error("RecordNotFound error at GetUserProfile");
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserProfileGetResponseModel UpdateUserProfile(UserProfileUpdateRequestModel userProfileUpdateRequest) {
        logger.info(String.format("Starting Method: UpdateUserProfile w/ Param: %s", userProfileUpdateRequest.toJsonSerialized()));
        try {
            var enhancedClient = _GetDynamoDbEnhancedClient();
            DynamoDbTable<UserProfileEntity> userProfileTable = enhancedClient
                    .table(Constants.DynamoDbTableName, TableSchema.fromBean(UserProfileEntity.class));
            var partitionSortKey = _GetUserProfilePartitionAndSortKey(userProfileUpdateRequest.getUsername());
            var userProfileKey = Key.builder()
                    .partitionValue(partitionSortKey)
                    .sortValue(partitionSortKey)
                    .build();
            var foundUserProfile = userProfileTable.getItem(r -> r.key(userProfileKey));
            if (Objects.isNull(foundUserProfile)) {
                throw new RecordNotFoundException();
            }
            foundUserProfile.setAvatar(userProfileUpdateRequest.getAvatar());
            foundUserProfile.setBio(userProfileUpdateRequest.getBio());
            foundUserProfile.setFullname(userProfileUpdateRequest.getFullname());
            userProfileTable.updateItem(r -> r.item(foundUserProfile));
            logger.info("Finishing Method: UpdateUserProfile");
            return new UserProfileGetResponseModel(foundUserProfile);
        }
        catch (DynamoDbException e) {
            logger.info("DynamoDB error at UpdateUserProfile");
            throw e;
        }
        catch (RecordNotFoundException e) {
            logger.info("RecordNotFound error at UpdateUserProfile");
            throw new RuntimeException(e);
        }
    }
}
