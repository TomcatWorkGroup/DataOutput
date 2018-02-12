package com.itdreamworks.dataoutput.controller;

import com.itdreamworks.dataoutput.client.DeviceFeignClinet;
import com.itdreamworks.dataoutput.service.EmployeeService;
import com.itdreamworks.dataoutput.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/account")
public class AccountController {

    @Value("${account.loginurl}")
    String loginUrl;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    DeviceFeignClinet client;
    @Autowired
    TokenService tokenService;

    @PostMapping(value = "/login")
    public String login(HttpServletResponse response, @RequestParam(name = "loginid") String loginId, @RequestParam(name = "password") String password){
        String pass = employeeService.getPassword(loginId);
        String result ;
        if(password.equals(pass)){
            Cookie tokenCookie = tokenService.getUserToken(loginId);
            String devices = client.getSellDevices();
            result = String.format(
                    "{\"code\":1,\"msg\":\"用户登录成功！\",\"devices\":%s}",
                    devices);
            response.addCookie(tokenCookie);
        }else{
            result = String.format("{\"code\":0,\"login\":\"%s\",\"msg\":\"用户名或密码错误！\"}",loginUrl);
        }
        return result;
    }

    @GetMapping(value = "/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){

        return "";
    }

}
