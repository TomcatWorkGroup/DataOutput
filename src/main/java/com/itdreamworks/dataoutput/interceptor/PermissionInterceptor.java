package com.itdreamworks.dataoutput.interceptor;

import com.itdreamworks.dataoutput.annotation.Permission;
import com.itdreamworks.dataoutput.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.lang.reflect.Method;

@Service
public class PermissionInterceptor implements HandlerInterceptor {
    @Value("${token.user}")
    String userTokenName;
    @Value("${account.loginurl}")
    String loginUrl;

    @Autowired
    TokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        if (o instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) o;
            Permission permission = method.getMethodAnnotation(Permission.class);
            if (null == permission || !permission.loginReqired())
                return true;
            else {
                if (isLogin(request)) {
                    return true;
                } else {
                    response.setCharacterEncoding("utf-8");
                    PrintWriter out = response.getWriter();
                    out.print(String.format("{\"code\":0,\"login\":\"%s\",\"msg\":\"token无效！\"}", loginUrl));
                }
            }
        }
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

    /**
     * 判断一个方法是否需要登录
     *
     * @param method
     * @return
     */
    private boolean isLoginRequired(Method method) {
        boolean result = true;
        if (method.isAnnotationPresent(Permission.class)) {
            result = method.getAnnotation(Permission.class).loginReqired();
        }
        return result;
    }

    //判断是否已经登录
    private boolean isLogin(HttpServletRequest request) {
        Cookie tokenCookie = WebUtils.getCookie(request, userTokenName);
        return tokenService.verifyUserToken(tokenCookie);
    }
}
