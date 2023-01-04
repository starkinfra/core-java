package com.starkcore;

public enum Environment {
    sandbox("sandbox"),
    production("production");
    private String description;

    Environment(String description) {
        this.description = description;
    }
}
