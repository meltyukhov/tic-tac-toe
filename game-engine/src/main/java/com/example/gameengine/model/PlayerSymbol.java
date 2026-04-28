package com.example.gameengine.model;

public enum PlayerSymbol {
    X,
    O;

    public static PlayerSymbol from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Player symbol is required.");
        }

        return PlayerSymbol.valueOf(value.trim().toUpperCase());
    }
}
