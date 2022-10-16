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
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UserFriendService implements IUserFriendService {
    private final Logger logger;
    private final DynamoDbClient dynamoDbClient;

    @Autowired
    public UserFriendService(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
        logger = LoggerFactory.getLogger(UserFriendService.class);
    }

    private DynamoDbEnhancedClient _GetDynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    private String _GetUserFriendPartitionKey(String username) {
        return String.format("user-friends#%s", username);
    }

    private String _GetUserFriendSortKey(String friendUsername) {
        return String.format("friend#%s", friendUsername);
    }

    @Override
    public UserFriendGetResponseModel SaveUserFriend(UserFriendCreateRequestModel userFriendCreateRequest) {
        logger.info(String.format("Start method: SaveUserFriend w/ param: %s", userFriendCreateRequest.toJsonSerialized()));
        try {
            var enhancedClient = _GetDynamoDbEnhancedClient();
            DynamoDbTable<UserFriendEntity> userFriendTable = enhancedClient
                    .table(Constants.DynamoDbTableName, TableSchema.fromBean(UserFriendEntity.class));
            var newUserFriend = new UserFriendEntity.Builder()
                    .setUsername(userFriendCreateRequest.getUsername())
                    .setFriendUsername(userFriendCreateRequest.getFriendUsername())
                    .setCreatedAtNow()
                    .build();
            userFriendTable.putItem(newUserFriend);
            var returnUserFriend = new UserFriendGetResponseModel(newUserFriend);
            logger.info("Finish method: SaveUserFriend");
            return returnUserFriend;
        }
        catch (DynamoDbException e) {
            logger.error("DynamoDB error at SaveUserFriend");
            throw e;
        }
    }

    @Override
    public UserFriendGetResponseModel GetUserFriend(UserFriendGetRequestModel userFriendGetRequest) {
        logger.info(String.format("Start method: GetUserFriend w/ param: %s", userFriendGetRequest.toJsonSerialized()));
        try {
            var enhancedClient = _GetDynamoDbEnhancedClient();
            DynamoDbTable<UserFriendEntity> userFriendTable = enhancedClient
                    .table(Constants.DynamoDbTableName, TableSchema.fromBean(UserFriendEntity.class));
            var partitionKey = _GetUserFriendPartitionKey(userFriendGetRequest.getUsername());
            var sortKey = _GetUserFriendSortKey(userFriendGetRequest.getFriendUsername());
            var userFriendKey = Key.builder()
                    .partitionValue(partitionKey)
                    .sortValue(sortKey)
                    .build();
            var foundUserFriend = userFriendTable.getItem(r -> r.key(userFriendKey));
            if (Objects.isNull(foundUserFriend)) {
                throw new RecordNotFoundException();
            }
            var returnUserFriend = new UserFriendGetResponseModel(foundUserFriend);
            logger.info("Finish method: GetUserFriend");
            return returnUserFriend;
        }
        catch (DynamoDbException e) {
            logger.error("DynamoDB error at GetUserFriend");
            throw e;
        }
        catch (RecordNotFoundException e) {
            logger.error("RecordNotFound error at GetUserFriend");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void DeleteUserFriend(UserFriendGetRequestModel userFriendDeleteRequest) {
        logger.info(String.format("Start method: DeleteUserFriend w/ param: %s", userFriendDeleteRequest.toJsonSerialized()));
        try {
            var enhancedClient = _GetDynamoDbEnhancedClient();
            DynamoDbTable<UserFriendEntity> userFriendTable = enhancedClient
                    .table(Constants.DynamoDbTableName, TableSchema.fromBean(UserFriendEntity.class));
            var partitionKey = _GetUserFriendPartitionKey(userFriendDeleteRequest.getUsername());
            var sortKey = _GetUserFriendSortKey(userFriendDeleteRequest.getFriendUsername());
            var userFriendKey = Key.builder()
                    .partitionValue(partitionKey)
                    .sortValue(sortKey)
                    .build();
            var foundUserFriend = userFriendTable.getItem(r -> r.key(userFriendKey));
            if (Objects.isNull(foundUserFriend)) {
                throw new RecordNotFoundException();
            }
            userFriendTable.deleteItem(r -> r.key(userFriendKey));
            logger.info("Finish method: DeleteUserFriend");
        }
        catch (DynamoDbException e) {
            logger.error("DynamoDB error at DeleteUserFriend");
            throw e;
        } catch (RecordNotFoundException e) {
            logger.error("RecordNotFound error at DeleteUserFriend");
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<UserFriendGetResponseModel> GetUserFriendList(UserFriendListRequestModel userFriendListRequest) {
        logger.info(String.format("Start method: GetUserFriendList w/ params: %s", userFriendListRequest.toJsonSerialized()));
        try {
            var enhancedClient = _GetDynamoDbEnhancedClient();
            DynamoDbTable<UserFriendEntity> userFriendTable = enhancedClient
                    .table(Constants.DynamoDbTableName, TableSchema.fromBean(UserFriendEntity.class));
            var queryStartKey = Key.builder()
                    .partitionValue(_GetUserFriendPartitionKey(userFriendListRequest.getUsername()))
                    .sortValue(_GetUserFriendSortKey(userFriendListRequest.getStartFriendUsername()))
                    .build();
            var queryRequest = QueryEnhancedRequest.builder()
                    .limit(userFriendListRequest.getPageSize())
                    .queryConditional(QueryConditional.sortGreaterThanOrEqualTo(queryStartKey))
                    .build();
            var results = userFriendTable
                    .query(queryRequest)
                    .items()
                    .iterator();
            UserFriendGetResponseModel userFriend;
            var userFriendList = new ArrayList<UserFriendGetResponseModel>();
            while (results.hasNext()) {
                userFriend = new UserFriendGetResponseModel();
                var userFriendFromTable = results.next();
                userFriend.setUsername(userFriendFromTable.getUsername());
                userFriend.setFriendUsername(userFriendFromTable.getFriendUsername());
                userFriend.setCreatedAt(userFriendFromTable.getCreatedAt());

                userFriendList.add(userFriend);
            }
            logger.info("Finish method: GetUserFriendList");
            return userFriendList;
        }
        catch (DynamoDbException e) {
            logger.error("DynamoDB error at GetUserFriendList");
            throw e;
        }
    }
}
