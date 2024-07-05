package com.starkcore.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.starkbank.ellipticcurve.Ecdsa;
import com.starkbank.ellipticcurve.PublicKey;
import com.starkbank.ellipticcurve.Signature;
import com.starkbank.ellipticcurve.utils.ByteString;
import com.starkcore.error.InvalidSignatureError;
import com.starkcore.user.User;

import java.lang.reflect.Type;
import java.util.HashMap;

public final class Parse{
    
    public static <T extends Resource> T parseAndVerify(String content, String signature, String sdkVersion, String apiVersion, String host, Resource.ClassData resource, User user, String language, int timeout) throws Exception {
        String verifiedContent = verify(content, signature, sdkVersion, apiVersion, host, user, language, timeout);

        Gson gson = GsonEvent.getInstance();
        JsonObject contentJson = gson.fromJson(verifiedContent, JsonObject.class);
        JsonObject jsonObject = contentJson.getAsJsonObject();

        if (Api.getLastName(resource).equals("event")){
            jsonObject = contentJson.get(Api.getLastName(resource)).getAsJsonObject();
        }

        return gson.fromJson(jsonObject, (Type) resource.cls);
    }

    public static <T extends Resource> String verify (String content, String signature, String sdkVersion, String apiVersion, String host, User user, String language, int timeout) throws Exception {
        Signature signatureObject;
        try {
            signatureObject = Signature.fromBase64(new ByteString(signature.getBytes()));
        } catch (Error | RuntimeException e) {
            throw new InvalidSignatureError("The provided signature is not valid");
        }

        PublicKey publicKey = getPublicKey(
            sdkVersion, 
            apiVersion, 
            host, 
            user, 
            language, 
            timeout
        );
        
        if (isSignatureValid(content, signatureObject, publicKey)) {
            return content;
        }
        
        publicKey = getPublicKey(
            sdkVersion, 
            apiVersion, 
            host, 
            user, 
            language, 
            timeout,
            true
        );
        
        if (isSignatureValid(content, signatureObject, publicKey)) {
            return content;
        }

        throw new InvalidSignatureError("The provided signature and content do not match the Stark Infra public key");
    }

    private static boolean isSignatureValid(String content, Signature signature, PublicKey publicKey) throws Exception {
        if (Ecdsa.verify(content, signature, publicKey)) {
            return true;
        }
        return false;
    }

    private static PublicKey getPublicKey(String sdkVersion, String apiVersion, String host, User user, String language, int timeout) throws Exception {
        return getPublicKey(sdkVersion, apiVersion, host, user, language, timeout, false);
    }

    private static PublicKey getPublicKey(String sdkVersion, String apiVersion, String host, User user, String language, int timeout, Boolean refresh) throws Exception {
        PublicKey publicKey = Cache.starkPublicKey;
        if (publicKey != null || refresh) {
            return publicKey;
        }
        HashMap<String, Object> query = new HashMap<>();
        query.put("limit", "1");
        String content = Rest.getRaw(
            sdkVersion,
            host,
            apiVersion,
            "/public-key",
            user,
            language,
            timeout,
            query,
            null,
            null,
            true
        ).content();
        JsonObject contentJson = new Gson().fromJson(content, JsonObject.class);
        JsonArray publicKeys = contentJson.get("publicKeys").getAsJsonArray();
        publicKey = PublicKey.fromPem(
            publicKeys.get(0).getAsJsonObject().get("content").getAsString()
        );
        Cache.starkPublicKey = publicKey;
        return publicKey;
    }
}
