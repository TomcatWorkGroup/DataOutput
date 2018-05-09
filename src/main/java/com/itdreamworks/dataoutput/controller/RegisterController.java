package com.itdreamworks.dataoutput.controller;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.itdreamworks.dataoutput.client.TemplateClient;
import com.itdreamworks.dataoutput.config.SsmConfig;
import com.itdreamworks.dataoutput.model.Result;

import feign.Feign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

@RestController
@RequestMapping(value = "/register")
public class RegisterController {
    //产品名称:云通信短信API产品,开发者无需替换
    static final String product = "Dysmsapi";
    //产品域名,开发者无需替换
    static final String domain = "dysmsapi.aliyuncs.com";

    private static final String MOBILE_NUMBER="mobile";

    // TODO 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
    static final String accessKeyId = "LTAIf5XDtOqoSIgo";
    static final String accessKeySecret = "TsYsEk9UxpcqgbJWNdh5hgdNfU09YO";
    @Autowired
    private SsmConfig ssmConfig;
    @Value("${feign.datamanage.employee.create.path}")
    private String createEmployeePath;


    @RequestMapping(value = "/ssm/{mobile}")
    public Result getSsm(@PathVariable("mobile") String mobile, HttpServletRequest request) {
        Result result = new Result();

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
                result.setCode(Result.RESULTCODE_FAILD);
                result.setMsg("请五分钟后再次获取验证码");
                return result;
            }
        }
        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        try {
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        } catch (ClientException e1) {
            e1.printStackTrace();
        }
        IAcsClient acsClient = new DefaultAcsClient(profile);

        //随机生成六位验证码
        int code = (int) ((Math.random() * 9 + 1) * 100000);

        request.getSession().setAttribute("code", code);
        request.getSession().setAttribute("fristTime", new Date());


        request.getSession().setAttribute(MOBILE_NUMBER, mobile);

        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest sendSmsRequest = new SendSmsRequest();
        //必填:待发送手机号
        sendSmsRequest.setPhoneNumbers(mobile);
        //必填:短信签名-可在短信控制台中找到，你在签名管理里的内容
        Properties properties = new Properties();
        // 使用InPutStream流读取properties文件
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(ssmConfig.getSsmFilePath()));
            properties.load(bufferedReader);
            // 获取key对应的value值

        } catch (IOException e) {
            e.printStackTrace();
        }

        sendSmsRequest.setSignName(properties.getProperty("moblieMessage.signName"));
        //必填:短信模板-可在短信控制台中找到，你模板管理里的模板编号
        sendSmsRequest.setTemplateCode(properties.getProperty("moblieMessage.template"));
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        sendSmsRequest.setTemplateParam("{\"code\":\"" + code + "\"}");

        //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");

        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        //request.setOutId("yourOutId");

        //hint 此处可能会抛出异常，注意catch


        SendSmsResponse sendSmsResponse = null;
        try {
            sendSmsResponse = acsClient.getAcsResponse(sendSmsRequest);

        } catch (ClientException e) {

            e.printStackTrace();
        }
        //获取发送状态
        String resultCode = sendSmsResponse.getCode();
        if (resultCode != null && "OK".equals(resultCode)) {//发送成功
            result.setCode(Result.RESULTCODE_SUCCESS);
            result.setMsg("验证码发送成功");
        } else {
            result.setCode(Result.RESULTCODE_FAILD);
            result.setMsg(sendSmsResponse.getMessage());
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
                if (client.post(map).equals("true")) {
                    return "{\"code\":1}";
                } else {
                    return getErrorMsg("用户注册失败，请联系管理人员。");
                }
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
