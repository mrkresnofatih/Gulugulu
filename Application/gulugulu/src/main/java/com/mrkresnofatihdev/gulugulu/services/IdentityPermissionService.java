package com.mrkresnofatihdev.gulugulu.services;

import com.mrkresnofatihdev.gulugulu.exceptions.RecordNotFoundException;
import com.mrkresnofatihdev.gulugulu.models.IdentityPermissionCreateRequestModel;
import com.mrkresnofatihdev.gulugulu.models.IdentityPermissionEntity;
import com.mrkresnofatihdev.gulugulu.models.IdentityPermissionGetRequestModel;
import com.mrkresnofatihdev.gulugulu.models.IdentityPermissionGetResponseModel;
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
public class IdentityPermissionService implements IIdentityPermissionService {
    private final DynamoDbClient dynamoDbClient;
    private final Logger logger;

    @Autowired
    public IdentityPermissionService(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
        this.logger = LoggerFactory.getLogger(IdentityPermissionService.class);
    }

    private DynamoDbEnhancedClient _GetDynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    private String _GetIdentityPermissionPartitionKey(String identityName) {
        return String.format("identity-permissions#%s", identityName);
    }

    private String _GetIdentityPermissionSortKey(String permissionName) {
        return String.format("permission#%s", permissionName);
    }

    @Override
    public IdentityPermissionGetResponseModel GetPermission(IdentityPermissionGetRequestModel identityPermissionGetRequest) {
        logger.info(String.format("Starting method: GetPermission w/ param: %s", identityPermissionGetRequest.toJsonSerialized()));
        try {
            var enhancedClient = _GetDynamoDbEnhancedClient();
            DynamoDbTable<IdentityPermissionEntity> identityPermissionTable = enhancedClient
                    .table(Constants.DynamoDbTableName, TableSchema.fromBean(IdentityPermissionEntity.class));
            var idPermissionKey = Key.builder()
                    .partitionValue(_GetIdentityPermissionPartitionKey(identityPermissionGetRequest.getIdentityName()))
                    .sortValue(_GetIdentityPermissionSortKey(identityPermissionGetRequest.getPermissionName()))
                    .build();
            var foundPermission = identityPermissionTable.getItem(r -> r.key(idPermissionKey));
            if (Objects.isNull(foundPermission)) {
                throw new RecordNotFoundException();
            }
            var returnIdentityPermission = new IdentityPermissionGetResponseModel(foundPermission);
            logger.info("Finishing method: GetPermission");
            return returnIdentityPermission;
        } catch (RecordNotFoundException e) {
            logger.error("RecordNotFound error at GetPermission");
            throw new RuntimeException(e);
        } catch (DynamoDbException e) {
            logger.error("DynamoDB error at GetPermission");
        }
        return null;
    }

    @Override
    public IdentityPermissionGetResponseModel CreatePermission(IdentityPermissionCreateRequestModel identityPermissionCreateRequest) {
        logger.info(String.format("Start method: CreatePermission w/ params: %s", identityPermissionCreateRequest.toJsonSerialized()));
        try {
            var enhancedClient = _GetDynamoDbEnhancedClient();
            DynamoDbTable<IdentityPermissionEntity> identityPermissionTable = enhancedClient
                    .table(Constants.DynamoDbTableName, TableSchema.fromBean(IdentityPermissionEntity.class));
            var newIdentityPermission = new IdentityPermissionEntity.Builder()
                    .setIdentityName(identityPermissionCreateRequest.getIdentityName())
                    .setPermissionName(identityPermissionCreateRequest.getPermissionName())
                    .setResourceNames(identityPermissionCreateRequest.getResourceNames())
                    .build();
            identityPermissionTable.putItem(newIdentityPermission);
            var returnIdentityPermission = new IdentityPermissionGetResponseModel(newIdentityPermission);
            logger.info("Finishing method: CreatePermission");
            return returnIdentityPermission;
        } catch (DynamoDbException e) {
            logger.error("DynamoDB error at CreatePermission");
            throw e;
        }
    }

    @Override
    public IdentityPermissionGetResponseModel UpdatePermission(IdentityPermissionCreateRequestModel identityPermissionUpdateRequest) {
        try {
            var enhancedClient = _GetDynamoDbEnhancedClient();
            DynamoDbTable<IdentityPermissionEntity> identityPermissionTable = enhancedClient
                    .table(Constants.DynamoDbTableName, TableSchema.fromBean(IdentityPermissionEntity.class));
            var idPermissionKey = Key.builder()
                    .partitionValue(_GetIdentityPermissionPartitionKey(identityPermissionUpdateRequest.getIdentityName()))
                    .sortValue(_GetIdentityPermissionSortKey(identityPermissionUpdateRequest.getPermissionName()))
                    .build();
            var foundPermission = identityPermissionTable.getItem(r -> r.key(idPermissionKey));
            if (Objects.isNull(foundPermission)) {
                throw new RecordNotFoundException();
            }
            foundPermission.setResourceNames(identityPermissionUpdateRequest.getResourceNames());
            identityPermissionTable.updateItem(r -> r.item(foundPermission));
            logger.info("Finishing method: UpdatePermission");
            return new IdentityPermissionGetResponseModel(foundPermission);
        } catch (RecordNotFoundException e) {
            logger.error("RecordNotFound error at UpdatePermission");
            throw new RuntimeException(e);
        } catch (DynamoDbException e) {
            logger.error("DynamoDB error at UpdatePermission");
            throw e;
        }
    }
}
