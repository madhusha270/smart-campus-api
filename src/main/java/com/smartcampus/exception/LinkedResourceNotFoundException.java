package com.smartcampus.exception;

public class LinkedResourceNotFoundException extends RuntimeException {

    private final String targetId;
    private final String resourceType;

    public LinkedResourceNotFoundException(String resourceType, String targetId) {
        super(resourceType + " with ID '" + targetId + "' not found. Cannot link resource.");
        this.targetId = targetId;
        this.resourceType = resourceType;
    }

    public String getTargetId() {
        return targetId;
    }

    public String getResourceType() {
        return resourceType;
    }
}
