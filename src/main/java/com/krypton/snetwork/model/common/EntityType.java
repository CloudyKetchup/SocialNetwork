package com.krypton.snetwork.model.common;

public enum EntityType {

    GROUP("Group"),
    USER("User");

    private final String type;

    EntityType(String type) {
        this.type = type;
    }

    public boolean equalsType(String type) {
        return this.type.equals(type);
    }

    public String toString() {
        return this.type;
    }
}
