package com.itdreamworks.dataoutput.service;

import com.itdreamworks.dataoutput.entity.Token;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;

@Service
public class TokenService {
    @Value("${token.user}")
    String userTokenName;

    public Cookie getUserToken(String userId) {
        Token token = Token.getInstance(userId);
        Cookie cookie = new Cookie(userTokenName, token.getTokenString());
        cookie.setPath("/");
        return cookie;
    }

    public boolean verifyUserToken(Cookie tokenCookie) {
        if (null == tokenCookie)
            return false;
        return true;
    }

}
