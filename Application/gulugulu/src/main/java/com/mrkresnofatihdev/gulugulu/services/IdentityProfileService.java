package com.mrkresnofatihdev.gulugulu.services;

import com.mrkresnofatihdev.gulugulu.exceptions.RecordNotFoundException;
import com.mrkresnofatihdev.gulugulu.models.IdentityProfileEntity;
import com.mrkresnofatihdev.gulugulu.models.IdentityProfileGetResponseModel;
import com.mrkresnofatihdev.gulugulu.models.IdentityProfileUserCreateRequestModel;
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
public class IdentityProfileService implements IIdentityProfileService {
    private final DynamoDbClient dynamoDbClient;
    private final Logger logger;

    @Autowired
    public IdentityProfileService(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
        logger = LoggerFactory.getLogger(IdentityProfileService.class);
    }

    private DynamoDbEnhancedClient _GetDynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    private String _GetIdentityProfilePartitionAndSortKey(String identityName) {
        return String.format("identity-profile#%s", identityName);
    }

    private String _GetIdentityUserProfilePartitionAndSortKey(String username) {
        return _GetIdentityProfilePartitionAndSortKey(GetIdentityNameOfUser(username));
    }

    public String GetIdentityNameOfUser(String username) {
        return String.format("user#%s", username);
    }

    @Override
    public IdentityProfileGetResponseModel CreateForUser(IdentityProfileUserCreateRequestModel identityUserProfileCreateRequest) {
        logger.info(String.format("Starting Method: CreateForUser w/ params: %s", identityUserProfileCreateRequest.toJsonSerialized()));
        try {
            var enhancedClient = _GetDynamoDbEnhancedClient();
            DynamoDbTable<IdentityProfileEntity> identityProfileTable = enhancedClient
                    .table(Constants.DynamoDbTableName, TableSchema.fromBean(IdentityProfileEntity.class));
            var newIdentityName = String.format("user#%s", identityUserProfileCreateRequest.getUsername());
            var newIdentityProfile = new IdentityProfileEntity.Builder()
                    .setIdentityName(newIdentityName)
                    .setBanned(false)
                    .build();
            identityProfileTable.putItem(newIdentityProfile);
            var returnIdentityProfile = new IdentityProfileGetResponseModel(newIdentityProfile);
            logger.info("Finishing Method: CreateForUser");
            return returnIdentityProfile;
        }
        catch (DynamoDbException e) {
            logger.error("DynamoDB error at CreateForUser");
            throw e;
        }
    }

    @Override
    public IdentityProfileGetResponseModel GetForUser(IdentityProfileUserCreateRequestModel identityUserProfileCreateRequest) {
        logger.info(String.format("Starting Method: GetForUser w/ params: %s", identityUserProfileCreateRequest.toJsonSerialized()));
        try {
            var enhancedClient = _GetDynamoDbEnhancedClient();
            DynamoDbTable<IdentityProfileEntity> identityProfileTable = enhancedClient
                    .table(Constants.DynamoDbTableName, TableSchema.fromBean(IdentityProfileEntity.class));
            var partitionKey = _GetIdentityUserProfilePartitionAndSortKey(identityUserProfileCreateRequest.getUsername());
            var idProfileKey = Key.builder()
                    .partitionValue(partitionKey)
                    .sortValue(partitionKey)
                    .build();
            var foundIdentityProfile = identityProfileTable.getItem(r -> r.key(idProfileKey));
            if (Objects.isNull(foundIdentityProfile)) {
                throw new RecordNotFoundException();
            }
            var returnIdentityProfile = new IdentityProfileGetResponseModel(foundIdentityProfile);
            logger.info("Finishing Method: GetForUser");
            return returnIdentityProfile;
        }
        catch (DynamoDbException e) {
            logger.error("DynamoDB error at GetForUser");
            throw e;
        }
        catch (RecordNotFoundException e) {
            logger.info("RecordNotFound error at GetForUser");
            throw new RuntimeException(e);
        }
    }
}
