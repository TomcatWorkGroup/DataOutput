package com.itdreamworks.dataoutput.controller;

import com.itdreamworks.dataoutput.client.TemplateClient;
import com.itdreamworks.dataoutput.config.SsmConfig;
import com.itdreamworks.dataoutput.model.Result;

import com.itdreamworks.dataoutput.service.SmsService;
import feign.Feign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping(value = "/register")
public class RegisterController {
    private static final String MOBILE_NUMBER="mobile";

    @Autowired
    private SsmConfig ssmConfig;
    @Value("${feign.datamanage.employee.create.path}")
    private String createEmployeePath;
    @Autowired
    private SmsService smsService;


    @RequestMapping(value = "/ssm/{mobile}")
    public Result getSsm(@PathVariable("mobile") String mobile, HttpServletRequest request) {
        String oldNumber = request.getSession().getAttribute("number") + "";

        if (oldNumber != null && oldNumber.equals(mobile)) {
            Date oldDate = (Date) request.getSession().getAttribute("fristTime");
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            Calendar c3 = Calendar.getInstance();
            c1.setTime(new Date());//要判断的日期
            c2.setTime(oldDate);//初始日期
            c3.setTime(oldDate);//也给初始日期 把分钟加五
            c3.add(Calendar.MINUTE, 5);

            if (c1.after(c2) && c1.before(c3)) {
                Result result = new Result();
                result.setCode(Result.RESULTCODE_FAILD);
                result.setMsg("请五分钟后再次获取验证码");
                return result;
            }
        }
        Result result = smsService.sendSms(mobile);
        if(result.getCode() != Result.RESULTCODE_FAILD){
            request.getSession().setAttribute("code", result.getCode());
            request.getSession().setAttribute("fristTime", new Date());
            request.getSession().setAttribute(MOBILE_NUMBER, mobile);
            result.setCode(Result.RESULTCODE_SUCCESS);
            result.setMsg("验证码发送成功");
        }
        return result;
    }

    @RequestMapping(value = "/save", method = RequestMethod.GET)
    public String saveEmployee(String validCode, String password, String realName, String mobile, HttpServletRequest request, Map<String, String> map) {
        //从session中获取验证码
        String code = request.getSession().getAttribute("code") + "";
        if (code != null && validCode.equals(code)) {
            TemplateClient client =
                    Feign.builder().target(TemplateClient.class, createEmployeePath);
            map.put("orgType","0");
            map.put("orgId","10");
            map.put("password", password);
            map.put("realName", realName);
            map.put("mobile", mobile);
            map.put("email", mobile);

            try {
                return client.post(map);
            } catch (Exception ex) {
                return getErrorMsg(ex.getMessage());
            }
        } else {
            return getErrorMsg("验证码错误！");
        }
    }

    private String getErrorMsg(String msg) {
        return String.format("{\"code\":0,\"msg\":\"%s\"}", msg);
    }
}
