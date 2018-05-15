package com.itdreamworks.dataoutput.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itdreamworks.dataoutput.client.TemplateClient;
import com.itdreamworks.security.DeCoder;
import feign.Feign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;


@RestController
@RequestMapping(value = "/decoder")
public class DecoderController {

    @Value("${decoder.des.key}")
    private String desKey;
    @Value("${feign.datamanage.device.find.path}")
    private String deviceFindPath;

    @Autowired
    ObjectMapper mapper;

    @PostMapping(value = "/decode")
    public  String decoderDES(@RequestParam("data") String data, Map<String, String> map) throws Exception{
        String deviceNo = DeCoder.DeCode(data);
        TemplateClient client =
                Feign.builder().target(TemplateClient.class, deviceFindPath);
        map.put("deviceNo",deviceNo);
        String msg ;
        try {
            String jsonStr = client.post(map);
            LinkedHashMap jsonObj = (LinkedHashMap)mapper.readValue(jsonStr,Object.class);
            if(jsonObj.keySet().contains("deviceNo")) {
                msg = String.format("{\"code\":1,\"deviceNo\":\"%s\",\"nickName\":\"%s\"}",jsonObj.get("deviceNo"),jsonObj.get("nickName"));
            }else {
                msg = "{\"code\":0,\"msg\":\"设备信息无效\"}";
            }
        } catch (Exception ex) {
            msg = String.format("{\"code\":0,\"msg\":\"%s\",}",ex.getMessage());
        }
        return msg;
    }

    public static byte[] convertHexString(String ss)
    {
        byte digest[] = new byte[ss.length() / 2];
        for(int i = 0; i < digest.length; i++)
        {
            String byteString = ss.substring(2 * i, 2 * i + 2);
            int byteValue = Integer.parseInt(byteString, 16);
            digest[i] = (byte)byteValue;
        }
        return digest;
    }
}
