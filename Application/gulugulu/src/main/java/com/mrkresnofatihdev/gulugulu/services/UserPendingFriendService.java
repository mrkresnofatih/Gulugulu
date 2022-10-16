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
public class UserPendingFriendService implements IUserPendingFriendService {
    private final Logger logger;
    private final DynamoDbClient dynamoDbClient;

    @Autowired
    public UserPendingFriendService(DynamoDbClient dynamoDbClient) {
        this.logger = LoggerFactory.getLogger(UserPendingFriendService.class);
        this.dynamoDbClient = dynamoDbClient;
    }

    private DynamoDbEnhancedClient _GetDynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    private String _GetUserPendingFriendPartitionKey(String username) {
        return String.format("user-pending-friends#%s", username);
    }

    private String _GetUserPendingFriendSortKey(String createdAt) {
        return String.format("pending-friend#%s", createdAt);
    }

    @Override
    public void DeletePendingFriend(UserPendingFriendGetRequestModel userPendingFriendDeleteRequest) {
        logger.info(String.format("Start method: deleteUserPendingFriend w/ param: %s", userPendingFriendDeleteRequest.toJsonSerialized()));
        try {
            var enhancedClient = _GetDynamoDbEnhancedClient();
            DynamoDbTable<UserPendingFriendEntity> userPendingFriendTable = enhancedClient
                    .table(Constants.DynamoDbTableName, TableSchema.fromBean(UserPendingFriendEntity.class));
            var partitionKey = _GetUserPendingFriendPartitionKey(userPendingFriendDeleteRequest.getUsername());
            var sortKey = _GetUserPendingFriendSortKey(userPendingFriendDeleteRequest.getCreatedAt());
            var userPendingFriendKey = Key.builder()
                    .partitionValue(partitionKey)
                    .sortValue(sortKey)
                    .build();
            var foundUserPendingFriend = userPendingFriendTable.getItem(r -> r.key(userPendingFriendKey));
            if (Objects.isNull(foundUserPendingFriend)) {
                throw new RecordNotFoundException();
            }
            userPendingFriendTable.deleteItem(r -> r.key(userPendingFriendKey));
            logger.info("Finish method: deleteUserPendingFriend");
        }
        catch (DynamoDbException e) {
            logger.error("dynamodb error at deleteUserPendingFriend");
            throw e;
        }
        catch (RecordNotFoundException e) {
            logger.error("record not found when delete user pending friend");
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<UserPendingFriendGetResponseModel> GetPendingFriendList(UserPendingFriendListRequestModel userPendingFriendListRequest) {
        logger.info(String.format("Start method: GetPendingFriendList w/ param: %s", userPendingFriendListRequest.toJsonSerialized()));
        try {
            var enhancedClient = _GetDynamoDbEnhancedClient();
            DynamoDbTable<UserPendingFriendEntity> userPendingFriendTable = enhancedClient
                    .table(Constants.DynamoDbTableName, TableSchema.fromBean(UserPendingFriendEntity.class));
            var queryStartKey = Key.builder()
                    .partitionValue(_GetUserPendingFriendPartitionKey(userPendingFriendListRequest.getUsername()))
                    .sortValue(_GetUserPendingFriendSortKey(userPendingFriendListRequest.getStartCreatedAt()))
                    .build();
            var queryRequest = QueryEnhancedRequest.builder()
                    .limit(userPendingFriendListRequest.getPageSize())
                    .queryConditional(QueryConditional.sortGreaterThanOrEqualTo(queryStartKey))
                    .build();
            var results = userPendingFriendTable
                    .query(queryRequest)
                    .items()
                    .iterator();
            UserPendingFriendGetResponseModel userPdgFriend;
            var userPendingFriendList = new ArrayList<UserPendingFriendGetResponseModel>();
            while (results.hasNext()) {
                userPdgFriend = new UserPendingFriendGetResponseModel();
                var userPendingFriendFromTable = results.next();
                userPdgFriend.setUsername(userPendingFriendFromTable.getUsername());
                userPdgFriend.setFriendUsername(userPendingFriendFromTable.getFriendUsername());
                userPdgFriend.setCreatedAt(userPendingFriendFromTable.getCreatedAt());

                userPendingFriendList.add(userPdgFriend);
            }
            logger.info("Finish method: GetPendingFriendList");
            return userPendingFriendList;
        }
        catch (DynamoDbException e) {
            logger.error("DynamoDB error at GetPendingFriendList");
            throw e;
        }
    }

    @Override
    public UserPendingFriendGetResponseModel SavePendingFriend(UserPendingFriendCreateRequestModel userPendingFriendCreateRequest) {
        logger.info(String.format("start method: SavePendingFriend w/ param: %s", userPendingFriendCreateRequest.toJsonSerialized()));
        try {
            var enhancedClient = _GetDynamoDbEnhancedClient();
            DynamoDbTable<UserPendingFriendEntity> userPendingFriendTable = enhancedClient
                    .table(Constants.DynamoDbTableName, TableSchema.fromBean(UserPendingFriendEntity.class));
            var newUserPendingFriend = new UserPendingFriendEntity.Builder()
                    .setUsername(userPendingFriendCreateRequest.getUsername())
                    .setFriendUsername(userPendingFriendCreateRequest.getFriendUsername())
                    .setCreatedAtNow()
                    .build();
            userPendingFriendTable.putItem(newUserPendingFriend);
            var returnUserPendingFriend = new UserPendingFriendGetResponseModel(newUserPendingFriend);
            logger.info("Finish method: SavePendingFriend");
            return returnUserPendingFriend;
        }
        catch (DynamoDbException e) {
            logger.error("DynamoDB error at SavePendingFriend");
            throw e;
        }
    }

    @Override
    public UserPendingFriendGetResponseModel GetPendingFriend(UserPendingFriendGetRequestModel userPendingFriendGetRequest) {
        logger.info(String.format("Start method: GetPendingFriend w/ param: %s", userPendingFriendGetRequest.toJsonSerialized()));
        try {
            var enhancedClient = _GetDynamoDbEnhancedClient();
            DynamoDbTable<UserPendingFriendEntity> userPendingFriendTable = enhancedClient
                    .table(Constants.DynamoDbTableName, TableSchema.fromBean(UserPendingFriendEntity.class));
            var partitionKey = _GetUserPendingFriendPartitionKey(userPendingFriendGetRequest.getUsername());
            var sortKey = _GetUserPendingFriendSortKey(userPendingFriendGetRequest.getCreatedAt());
            var userPendingFriendKey = Key.builder()
                    .partitionValue(partitionKey)
                    .sortValue(sortKey)
                    .build();
            var foundPendingFriend = userPendingFriendTable.getItem(r -> r.key(userPendingFriendKey));
            if (Objects.isNull(foundPendingFriend)) {
                throw new RecordNotFoundException();
            }
            var returnUserPendingFriend = new UserPendingFriendGetResponseModel(foundPendingFriend);
            logger.info("Finish method: GetUserPendingFriend");
            return returnUserPendingFriend;
        }
        catch (DynamoDbException e) {
            logger.error("DynamoDB error at GetUserPendingFriend");
            throw e;
        } catch (RecordNotFoundException e) {
            logger.error("RecordNotFound error at GetUserPendingFriend");
            throw new RuntimeException(e);
        }
    }
}
