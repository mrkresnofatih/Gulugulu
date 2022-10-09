package com.mrkresnofatihdev.gulugulu.services;


import com.mrkresnofatihdev.gulugulu.exceptions.RecordNotFoundException;
import com.mrkresnofatihdev.gulugulu.models.*;
import com.mrkresnofatihdev.gulugulu.utilities.Constants;
import com.mrkresnofatihdev.gulugulu.utilities.HashHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.Objects;

@Service
public class UserCredentialsService implements IUserCredentialsService {
    private final DynamoDbClient dynamoDbClient;
    private final Logger logger;
    private final HashHelper hashHelper;

    public UserCredentialsService(DynamoDbClient dynamoDbClient, HashHelper hashHelper) {
        this.dynamoDbClient = dynamoDbClient;
        this.hashHelper = hashHelper;
        logger = LoggerFactory.getLogger(UserCredentialsService.class);
    }

    private DynamoDbEnhancedClient _GetDynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    private String _GetUserCredentialsPartitionAndSortKey(String username) {
        return String.format("user-credentials#%s", username);
    }

    @Override
    public UserCredentialsGetResponseModel SaveUserCredentials(UserCredentialsCreateRequestModel userCredentialsCreateRequest) {
        logger.info(String.format("Starting Method: SaveUserCredentials w/ Param: %s", userCredentialsCreateRequest.toJsonSerialized()));
        try {
            var enhancedClient = _GetDynamoDbEnhancedClient();
            DynamoDbTable<UserCredentialsEntity> userCredentialsTable = enhancedClient
                    .table(Constants.DynamoDbTableName, TableSchema.fromBean(UserCredentialsEntity.class));
            var newUserCredentials = new UserCredentialsEntity.Builder()
                    .setUsername(userCredentialsCreateRequest.getUsername())
                    .setPassword(hashHelper.HashData(userCredentialsCreateRequest.getPassword()))
                    .build();
            userCredentialsTable.putItem(newUserCredentials);
            var returnUserCredentials = new UserCredentialsGetResponseModel(newUserCredentials);
            logger.info("Finishing Method: SaveUserCredentials");
            return returnUserCredentials;
        }
        catch (DynamoDbException e) {
            logger.error("DynamoDB error at SaveUserCredentials");
            throw e;
        }
    }

    @Override
    public UserCredentialsGetResponseModel GetUserCredentials(UserCredentialsGetRequestModel userCredentialsGetRequest) {
        logger.info(String.format("Starting Method: GetUserCredentials w/ params: %s", userCredentialsGetRequest.toJsonSerialized()));
        try {
            var enhancedClient = _GetDynamoDbEnhancedClient();
            DynamoDbTable<UserCredentialsEntity> userCredentialsTable = enhancedClient
                    .table(Constants.DynamoDbTableName, TableSchema.fromBean(UserCredentialsEntity.class));
            var partitionKey = _GetUserCredentialsPartitionAndSortKey(userCredentialsGetRequest.getUsername());
            var userCredentialsKey = Key.builder()
                    .partitionValue(partitionKey)
                    .sortValue(partitionKey)
                    .build();
            var foundUserCredentials = userCredentialsTable.getItem(r -> r.key(userCredentialsKey));
            if (Objects.isNull(foundUserCredentials)) {
                throw new RecordNotFoundException();
            }
            var returnUserCredentials = new UserCredentialsGetResponseModel(foundUserCredentials);
            logger.info("Finishing Method: GetUserCredentials");
            return returnUserCredentials;
        }
        catch (DynamoDbException e) {
            logger.error("DynamoDB error at GetUserCredentials");
            throw e;
        } catch (RecordNotFoundException e) {
            logger.info("RecordNotFound error at GetUserCredentials");
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserCredentialsGetResponseModel UpdateUserCredentials(UserCredentialsCreateRequestModel userCredentialsUpdateRequest) {
        logger.info(String.format("Starting Method: UpdateUserCredentials w/ params: %s", userCredentialsUpdateRequest.toJsonSerialized()));
        try {
            var enhancedClient = _GetDynamoDbEnhancedClient();
            DynamoDbTable<UserCredentialsEntity> userCredentialsTable = enhancedClient
                    .table(Constants.DynamoDbTableName, TableSchema.fromBean(UserCredentialsEntity.class));
            var partitionKey = _GetUserCredentialsPartitionAndSortKey(userCredentialsUpdateRequest.getUsername());
            var userCredentialsKey = Key.builder()
                    .partitionValue(partitionKey)
                    .sortValue(partitionKey)
                    .build();
            var foundUserCredentials = userCredentialsTable.getItem(r -> r.key(userCredentialsKey));
            if (Objects.isNull(foundUserCredentials)) {
                throw new RecordNotFoundException();
            }
            foundUserCredentials.setPassword(hashHelper.HashData(userCredentialsUpdateRequest.getPassword()));
            userCredentialsTable.updateItem(r -> r.item(foundUserCredentials));
            logger.info("Finishing Method: UpdateUserCredentials");
            return new UserCredentialsGetResponseModel(foundUserCredentials);
        }
        catch (DynamoDbException e) {
            logger.info("DynamoDB error at UpdateUserCredentials");
            throw e;
        }
        catch (RecordNotFoundException e) {
            logger.info("RecordNotFound error at UpdateUserCredentials");
            throw new RuntimeException(e);
        }
    }
}
