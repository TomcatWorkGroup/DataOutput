package com.itdreamworks.dataoutput.service;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itdreamworks.dataoutput.config.SsmConfig;
import com.itdreamworks.dataoutput.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

@Service
public class SmsService {
    //产品名称:云通信短信API产品,开发者无需替换
    static final String product = "Dysmsapi";
    //产品域名,开发者无需替换
    static final String domain = "dysmsapi.aliyuncs.com";

    private static final String MOBILE_NUMBER="mobile";

    // TODO 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
    static final String accessKeyId = "LTAIf5XDtOqoSIgo";
    static final String accessKeySecret = "TsYsEk9UxpcqgbJWNdh5hgdNfU09YO";
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SsmConfig ssmConfig;

    public Result sendSms(String mobileNumber){
        Result result = new Result();
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


        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest sendSmsRequest = new SendSmsRequest();
        //必填:待发送手机号
        sendSmsRequest.setPhoneNumbers(mobileNumber);
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
//            result.setCode(Result.RESULTCODE_SUCCESS);
            result.setCode(code);
            result.setMsg("验证码发送成功");
        } else {
            result.setCode(Result.RESULTCODE_FAILD);
            result.setMsg(sendSmsResponse.getMessage());
        }
        return result;
    }
}
