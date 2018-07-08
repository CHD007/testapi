package com.chernyshov777.domain;

public enum State {
    CREATED,
    APPROVED,
    FAILED;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
