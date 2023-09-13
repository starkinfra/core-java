package com.starkcore.user;

public class PublicUser extends User {

    public PublicUser(String environment) throws Exception {
        super(environment, "", null);
    }


    @Override
    public String accessId() {
        return null;
    }
}
