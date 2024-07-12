package com.starkcore.utils;

import com.starkbank.ellipticcurve.PrivateKey;
import com.starkcore.Environment;
import com.starkcore.user.User;
import com.starkcore.Settings;

import java.util.Arrays;


public final class Check {
    public static String environment(String environment) throws Exception {
        for(Environment value : Environment.values()){
            if(value.name().equals(environment)) return environment;
        }
        throw new Exception(
            String.format("Invalid environment, please choose one among %s", Arrays.toString(Environment.values()))
        );
    }

    public static String PrivateKey(String pem) throws Exception {
        try {
            PrivateKey privateKey = PrivateKey.fromPem(pem);
            if (!privateKey.curve.name.equals("secp256k1")) {
                throw new Exception();
            }
        } catch (Exception e) {
            throw new Exception("privateKey must be valid secp256k1 ECDSA string in pem format");
        }
        return pem;
    }

    static User user(User user) throws Error {
        if (user == null) {
            user = Settings.user;
        }
        if (user == null) {
            throw new Error("A user is required to access our API. Check our README: https://github.com/starkinfra/sdk-java/");
        }
        return user;
    }

    public static String language(String language) throws Exception {
        String[] acceptedLanguages = {"en-US", "pt-BR"};
        if (Arrays.asList(acceptedLanguages).contains(language)){
            return language;
        }
        throw new Exception(
            String.format("Language must be one from %s", Arrays.toString(acceptedLanguages))
        );
    }
}
