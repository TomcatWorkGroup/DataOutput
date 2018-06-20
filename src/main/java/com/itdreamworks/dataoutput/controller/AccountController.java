package com.itdreamworks.dataoutput.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itdreamworks.dataoutput.client.TemplateClient;
import com.itdreamworks.dataoutput.model.Result;
import com.itdreamworks.dataoutput.service.EmployeeService;
import com.itdreamworks.dataoutput.service.SmsService;
import com.itdreamworks.dataoutput.service.TokenService;
import feign.Feign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/account")
public class AccountController {
    public static final String EMPLOYEE_ID = "EMPLOYEE_ID";
    @Value("${account.loginurl}")
    private String loginUrl;
    @Value("${feign.datamanage.employee.find.path}")
    private String  findUserUrl;
    @Value("${feign.datamanage.employee.changePassword.path}")
    private String  changePasswordUrl;

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private ObjectMapper jsonMapper;

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
                    //String devices = employeeService.getDevices(id);
                    result = "{\"code\":1,\"msg\":\"用户登录成功！\"}";
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


    //找回密码
    @PostMapping("/findPassword")
    public Result findPassword(String number, HttpServletRequest request, Map<String, String> map)  {
        //调用查找账号的接口
        Result result = new Result();
        TemplateClient clientF = Feign.builder().target(TemplateClient.class, findUserUrl);
        map.put("loginId", number);
        //调用并返回结果
        String jsonStrF = clientF.post(map);
        //判断账号是否存在
        if (!jsonStrF.equals("")) {
            result = smsService.sendSms(number);
            //获取发送状态
            if (result.getCode() != Result.RESULTCODE_FAILD) {//发送成功
                ////调用修改密码的接口
                TemplateClient clientC =
                        Feign.builder().target(TemplateClient.class, changePasswordUrl);
                map.put("loginId", number);
                map.put("password", String.valueOf(result.getCode()));
                //调用并返回结果
                String jsonStrC = clientC.post(map);
                if (jsonStrC.equals("true")) {
                    result.setCode(Result.RESULTCODE_SUCCESS);
                    result.setMsg("找回密码成功");
                } else {
                    result.setCode(Result.RESULTCODE_SUCCESS);
                    result.setMsg("找回密码失败");
                }
            }
        } else {
            result.setMsg("该账号不存在,请重新输入");
        }
        return result;
    }

    //修改密码
    @PostMapping("/changePassword")
    public String changePassword(String loginId, String pwd, String password, Map<String, String> map) throws IOException {

        TemplateClient client =
                Feign.builder().target(TemplateClient.class, findUserUrl);
        map.put("loginId", loginId);

        String jsonStr = client.post(map);//调用并返回结果
        if (!jsonStr.equals("")) {
            LinkedHashMap object = (LinkedHashMap) jsonMapper.readValue(jsonStr, Object.class);

            if (pwd.equals(object.get("password"))) {
                TemplateClient client2 =
                        Feign.builder().target(TemplateClient.class, changePasswordUrl);
                map.put("password", password);
                map.put("loginId", loginId);
                String jsonStr2 = client2.post(map);//调用并返回结果
                return "{\"msg\":\"成功！\"}";
            } else {
                return "{\"msg\":\"密码不正确！\"}";
            }

        } else {
            return "{\"msg\":\"用户不存在！\"}";
        }
    }
}
