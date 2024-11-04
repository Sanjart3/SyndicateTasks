package com.task06.service;

import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;

public interface AuditService {
    void putAddEvent(DynamodbStreamRecord record);
    void putUpdateEvent(DynamodbStreamRecord record);
}
