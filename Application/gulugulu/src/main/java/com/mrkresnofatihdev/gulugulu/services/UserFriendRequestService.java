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
public class UserFriendRequestService implements IUserFriendRequestService {
    private final Logger logger;
    private final DynamoDbClient dynamoDbClient;

    @Autowired
    public UserFriendRequestService(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
        this.logger = LoggerFactory.getLogger(UserFriendRequestService.class);
    }

    private DynamoDbEnhancedClient _GetDynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    private String _GetUserFriendRequestPartitionKey(String username) {
        return String.format("user-friend-requests#%s", username);
    }

    private String _GetUserFriendRequestSortKey(String createdAt) {
        return String.format("friend-request#%s", createdAt);
    }

    @Override
    public void DeleteFriendRequest(UserFriendRequestDeleteRequestModel userFriendRequestDeleteRequest) {
        logger.info(String.format("Start method: deleteUserFriendRequest w/ param: %s", userFriendRequestDeleteRequest.toJsonSerialized()));
        try {
            var enhancedClient = _GetDynamoDbEnhancedClient();
            DynamoDbTable<UserFriendRequestEntity> userFriendRequestTable = enhancedClient
                    .table(Constants.DynamoDbTableName, TableSchema.fromBean(UserFriendRequestEntity.class));
            var partitionKey = _GetUserFriendRequestPartitionKey(userFriendRequestDeleteRequest.getUsername());
            var sortKey = _GetUserFriendRequestSortKey(userFriendRequestDeleteRequest.getCreatedAt());
            var userFriendRequestKey = Key.builder()
                    .partitionValue(partitionKey)
                    .sortValue(sortKey)
                    .build();
            var foundUserFriendRequest = userFriendRequestTable.getItem(r -> r.key(userFriendRequestKey));
            if (Objects.isNull(foundUserFriendRequest)) {
                throw new RecordNotFoundException();
            }
            userFriendRequestTable.deleteItem(r -> r.key(userFriendRequestKey));
            logger.info("Finish method: deleteUserFriendRequest");
        }
        catch (DynamoDbException e) {
            logger.error("Dynamodb error at deleteUserFriendRequest");
            throw e;
        } catch (RecordNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserFriendRequestGetResponseModel SaveFriendRequest(UserFriendRequestCreateRequestModel userFriendRequestCreateRequest) {
        logger.info(String.format("Start method: saveFriendRequest w/ param: %s", userFriendRequestCreateRequest.toJsonSerialized()));
        try {
            var enhancedClient = _GetDynamoDbEnhancedClient();
            DynamoDbTable<UserFriendRequestEntity> userFriendRequestTable = enhancedClient
                    .table(Constants.DynamoDbTableName, TableSchema.fromBean(UserFriendRequestEntity.class));
            var newUserFriendRequest = new UserFriendRequestEntity.Builder()
                    .setUsername(userFriendRequestCreateRequest.getUsername())
                    .setRequesterUsername(userFriendRequestCreateRequest.getRequesterUsername())
                    .setCreatedAtNow()
                    .build();
            userFriendRequestTable.putItem(newUserFriendRequest);
            var returnUserFriendRequest = new UserFriendRequestGetResponseModel(newUserFriendRequest);
            logger.info("Finish method: saveFriendRequest");
            return returnUserFriendRequest;
        }
        catch (DynamoDbException e) {
            logger.error("DynamoDB error at saveFriendRequest");
            throw e;
        }
    }

    @Override
    public List<UserFriendRequestGetResponseModel> GetFriendRequestList(UserFriendRequestListRequestModel userFriendRequestListRequest) {
        logger.info(String.format("Start method: getFriendRequestList w/ params: %s", userFriendRequestListRequest.toJsonSerialized()));
        try {
            var enhancedClient = _GetDynamoDbEnhancedClient();
            DynamoDbTable<UserFriendRequestEntity> userFriendRequestTable = enhancedClient
                    .table(Constants.DynamoDbTableName, TableSchema.fromBean(UserFriendRequestEntity.class));
            var queryStartKey = Key.builder()
                    .partitionValue(_GetUserFriendRequestPartitionKey(userFriendRequestListRequest.getUsername()))
                    .sortValue(_GetUserFriendRequestSortKey(userFriendRequestListRequest.getStartCreatedAt()))
                    .build();
            var queryRequest = QueryEnhancedRequest.builder()
                    .limit(userFriendRequestListRequest.getPageSize())
                    .queryConditional(QueryConditional.sortGreaterThanOrEqualTo(queryStartKey))
                    .build();
            var results = userFriendRequestTable
                    .query(queryRequest)
                    .items()
                    .iterator();
            UserFriendRequestGetResponseModel userFriendReq;
            var userFriendRequestList = new ArrayList<UserFriendRequestGetResponseModel>();
            while (results.hasNext()) {
                userFriendReq = new UserFriendRequestGetResponseModel();
                var userFriendReqFromTable = results.next();
                userFriendReq.setUsername(userFriendReqFromTable.getUsername());
                userFriendReq.setRequesterUsername(userFriendReqFromTable.getRequesterUsername());
                userFriendReq.setCreatedAt(userFriendReqFromTable.getCreatedAt());

                userFriendRequestList.add(userFriendReq);
            }
            logger.info("Finish method: GetFriendRequestList");
            return userFriendRequestList;
        }
        catch (DynamoDbException e) {
            logger.error("Dynamodb error at GetFriendRequestList");
            throw e;
        }
    }
}
