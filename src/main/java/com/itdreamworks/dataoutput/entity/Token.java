package com.itdreamworks.systemmanage.entity;

import java.io.Serializable;
import java.util.UUID;

public class Token implements Serializable{
    public String getTokenString() {
        return tokenString;
    }

    private String tokenString;

    public String getIdentity() {
        return identity;
    }

    private String identity;

    private Token(){
    }

    public static Token getInstance(String identity){
        Token token = new Token();
        token.identity = identity;
        token.tokenString = UUID.randomUUID().toString();
        return token;
    }
}
