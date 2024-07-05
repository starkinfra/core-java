package com.starkcore.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


public class GsonEvent {
    private static Gson instance;

    protected GsonEvent() {}

    private static Map<Type, Object> typeObjectHashMap = new HashMap<>();

    public static void registerTypeAdapter(Type type, Object typeAdapter) {
        typeObjectHashMap.put(type, typeAdapter);
    }

    public static synchronized Gson getInstance()
    {
        if(instance == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();

            for (Map.Entry<Type, Object> entry : typeObjectHashMap.entrySet()) {
                gsonBuilder.registerTypeAdapter(entry.getKey(), entry.getValue());
            }

            instance = gsonBuilder
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ")
                    .create();
        }
        return instance;
    }
}
