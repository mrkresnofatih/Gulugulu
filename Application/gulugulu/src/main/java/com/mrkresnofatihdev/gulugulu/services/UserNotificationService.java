package com.mrkresnofatihdev.gulugulu.services;

import com.mrkresnofatihdev.gulugulu.models.IUserNotificationCreatable;
import com.mrkresnofatihdev.gulugulu.models.UserNotificationEntity;
import com.mrkresnofatihdev.gulugulu.models.UserNotificationGetResponseModel;
import com.mrkresnofatihdev.gulugulu.models.UserNotificationListGetRequestModel;
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

@Service
public class UserNotificationService implements IUserNotificationService {
    private final Logger logger;
    private final DynamoDbClient dynamoDbClient;

    @Autowired
    public UserNotificationService(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
        this.logger = LoggerFactory.getLogger(UserNotificationService.class);
    }

    private DynamoDbEnhancedClient _GetDynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    private String _GetUserNotificationPartitionKey(String username) {
        return String.format("user-notifications#%s", username);
    }

    private String _GetUserNotificationSortKey(String createdAt) {
        return String.format("notification#%s", createdAt);
    }

    @Override
    public List<UserNotificationGetResponseModel> GetUserNotificationList(UserNotificationListGetRequestModel userNotificationListGetRequest) {
        logger.info(String.format("Starting method: GetUserNotificationList w/ param: %s", userNotificationListGetRequest.toJsonSerialized()));
        try {
            var enhancedClient = _GetDynamoDbEnhancedClient();
            DynamoDbTable<UserNotificationEntity> userNotificationTable = enhancedClient
                    .table(Constants.DynamoDbTableName, TableSchema.fromBean(UserNotificationEntity.class));
            var queryStartKey = Key.builder()
                    .partitionValue(_GetUserNotificationPartitionKey(userNotificationListGetRequest.getUsername()))
                    .sortValue(_GetUserNotificationSortKey(userNotificationListGetRequest.getStartCreatedAt()))
                    .build();
            var queryRequest = QueryEnhancedRequest.builder()
                    .limit(userNotificationListGetRequest.getPageSize())
                    .queryConditional(QueryConditional.sortGreaterThanOrEqualTo(queryStartKey))
                    .build();
            var results = userNotificationTable
                    .query(queryRequest)
                    .items()
                    .iterator();
            UserNotificationGetResponseModel userNotification;
            var userNotificationList = new ArrayList<UserNotificationGetResponseModel>();
            while (results.hasNext()) {
                userNotification = new UserNotificationGetResponseModel();
                var userNotificationFromTable = results.next();
                userNotification.setUsername(userNotificationFromTable.getUsername());
                userNotification.setMessage(userNotificationFromTable.getMessage());
                userNotification.setImage(userNotificationFromTable.getImage());
                userNotification.setLink(userNotificationFromTable.getLink());
                userNotification.setCreatedAt(userNotificationFromTable.getCreatedAt());

                userNotificationList.add(userNotification);
            }
            return userNotificationList;
        }
        catch (DynamoDbException e) {
            logger.error("DynamoDB error at GetUserNotificationList");
            throw e;
        }
    }

    @Override
    public UserNotificationGetResponseModel CreateUserNotification(IUserNotificationCreatable userNotificationCreateRequest) {
        logger.info(String.format("Starting method: CreateUserNotification w/ param: %s", userNotificationCreateRequest.toJsonSerialized()));
        try {
            var enhancedClient = _GetDynamoDbEnhancedClient();
            DynamoDbTable<UserNotificationEntity> userNotificationTable = enhancedClient
                    .table(Constants.DynamoDbTableName, TableSchema.fromBean(UserNotificationEntity.class));
            var newUserNotification = new UserNotificationEntity.Builder()
                    .setUsername(userNotificationCreateRequest.getUsername())
                    .setMessage(userNotificationCreateRequest.getMessage())
                    .setLink(userNotificationCreateRequest.getLink())
                    .setImage(userNotificationCreateRequest.getImage())
                    .setCreatedAtNow()
                    .build();
            userNotificationTable.putItem(newUserNotification);
            var returnUserNotification = new UserNotificationGetResponseModel(newUserNotification);
            logger.info("Finish method: CreateUserNotification");
            return returnUserNotification;
        }
        catch (DynamoDbException e) {
            logger.error("DynamoDB error at CreateUserNotification");
            throw e;
        }
    }
}
