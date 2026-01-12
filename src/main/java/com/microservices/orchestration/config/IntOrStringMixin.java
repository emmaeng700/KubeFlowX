package com.microservices.orchestration.config;

import com.fasterxml.jackson.annotation.JsonValue;
import io.kubernetes.client.custom.IntOrString;

public abstract class IntOrStringMixin {
    @JsonValue
    public abstract String getValue();
} 