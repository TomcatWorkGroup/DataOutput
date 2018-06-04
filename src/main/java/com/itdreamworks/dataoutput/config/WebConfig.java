package com.itdreamworks.dataoutput.config;

import com.itdreamworks.dataoutput.interceptor.PermissionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Autowired
    PermissionInterceptor permissionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册自定义拦截器，添加拦截路径和排除拦截路径
        registry.addInterceptor(permissionInterceptor).addPathPatterns("/device/**");
        registry.addInterceptor(permissionInterceptor).addPathPatterns("/bytedevice/**");
        super.addInterceptors(registry);
    }
}

