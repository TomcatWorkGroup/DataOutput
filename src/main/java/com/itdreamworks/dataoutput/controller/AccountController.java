package com.itdreamworks.dataoutput.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itdreamworks.dataoutput.service.EmployeeService;
import com.itdreamworks.dataoutput.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;

@RestController
@RequestMapping(value = "/account")
public class AccountController {
    public static final String EMPLOYEE_ID = "EMPLOYEE_ID";
    @Value("${account.loginurl}")
    private String loginUrl;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private TokenService tokenService;

    @PostMapping(value = "/login")
    public String login(HttpServletResponse response, @RequestParam(name = "loginId") String loginId, @RequestParam(name = "password") String password) throws IOException {
        String result ;
        String str = employeeService.getEmployee(loginId);
        if(str.isEmpty()){
            result = String.format("{\"code\":0,\"login\":\"%s\",\"msg\":\"用户名或密码错误！\"}",loginUrl);
        }else{
            LinkedHashMap jsonObj = (LinkedHashMap) mapper.readValue(str, Object.class);
            if(checkEmployeePassword(password,jsonObj.get("password"))){
                if(checkEmployeeStatus(jsonObj)){
                    Integer id = Integer.parseInt(jsonObj.get("id").toString());
                    String devices = employeeService.getDevices(id);
                    result = String.format(
                            "{\"code\":1,\"msg\":\"用户登录成功！\",\"devices\":%s}",
                            devices);
                    Cookie cookie = tokenService.getUserToken(id.toString());
                    response.addCookie(cookie);
                }else {
                    result = String.format("{\"code\":0,\"login\":\"%s\",\"msg\":\"当前用户被禁用，无法登录系统！\"}",loginUrl);
                }
            }else {
                result = String.format("{\"code\":0,\"login\":\"%s\",\"msg\":\"用户名或密码错误！\"}",loginUrl);
            }
        }
        return result;
    }

    @GetMapping(value = "/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){

        return "";
    }

    private boolean checkEmployeeStatus(LinkedHashMap map){
        return map.get("status").equals(1);
    }

    private boolean checkEmployeePassword(String inputPassword,Object password){
        return null != inputPassword && !inputPassword.isEmpty() && inputPassword.equals(password);
    }
}
