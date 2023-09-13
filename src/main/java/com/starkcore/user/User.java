package com.starkcore.user;

import com.starkbank.ellipticcurve.PrivateKey;
import com.starkcore.utils.Check;
import com.starkcore.utils.Resource;


public abstract class User extends Resource{
    public final String pem;
    public final String environment;

    public User(String environment, String id, String privateKey) throws Exception {
        super(id);
        this.environment = Check.environment(environment);
        this.pem = Check.PrivateKey(privateKey);
    }

    public abstract String accessId();

    public PrivateKey privateKey(){
        return PrivateKey.fromPem(this.pem);
    }
}
