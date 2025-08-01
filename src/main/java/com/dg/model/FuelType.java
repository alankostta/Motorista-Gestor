package com.dg.model;

public enum FuelType {
    GASOLINE("Gasolina"),
    ETHANOL("Etanol"),
    DIESEL("Diesel"),
    FLEX("Flex"),
    ELECTRIC("Elétrico");

    private String displayName;

    FuelType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}