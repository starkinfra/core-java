package com.starkcore.utils;


public abstract class Resource extends SubResource {

    protected static ClassData data;

    public String id;
    protected Resource(String id){
        this.id = id;
    }
}
