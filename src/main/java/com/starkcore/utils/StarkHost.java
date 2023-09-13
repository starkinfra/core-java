package com.starkcore.utils;

public enum StarkHost {
    infra("infra"),
    bank("bank"),
    sign("sign");

    final String host;
    StarkHost(String host) {
        this.host = host;
    }
}
