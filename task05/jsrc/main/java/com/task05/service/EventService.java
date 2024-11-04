package com.task05.service;

import com.task05.entity.Event;

import java.util.Map;

public interface EventService {
    Event putEvent(int principalId, Map<String, Object> body);
}
