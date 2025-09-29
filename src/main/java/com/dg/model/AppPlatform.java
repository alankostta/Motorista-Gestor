package com.dg.model;

public enum AppPlatform {
    UBER("Uber"),
    NOVENTA_NOVE("99"),
    INDRIVER("InDriver"),
    OUTROS("Outros");

    private String displayName;

    AppPlatform(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
