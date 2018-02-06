package com.itdreamworks.dataoutput.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;

@Service
public class TokenService {
    @Value("${token.user}")
    String userTokenName;

    public Cookie getUserToken(String userId){
        Cookie cookie = new Cookie(userTokenName,userId);
        cookie.setPath("/");
        return cookie;
    }

    public boolean verifyUserToken(Cookie tokenCookie){
        if(null == tokenCookie)
            return false;
        return true;
    }

}
