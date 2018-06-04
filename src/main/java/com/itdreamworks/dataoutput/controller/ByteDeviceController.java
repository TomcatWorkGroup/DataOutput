package com.itdreamworks.dataoutput.controller;

import com.itdreamworks.dataoutput.client.TemplateClient;
import feign.Feign;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping(value = "/device2")
public class ByteDeviceController {
    @Value("${feign.datacache.url}")
    private String baseUrl;
    @Value("${feign.datacache.find.path}")
    private String findDevicesPath;
    @Value("${feign.datacache.get.path}")
    private String getDevicePath;

    @RequestMapping(value = "/find", method = RequestMethod.POST)
    public void findDevices(@RequestParam(name = "ids") String ids, Map<String, String> map, HttpServletResponse response) {
        TemplateClient client =
                Feign.builder().target(TemplateClient.class, String.format("%s%s", baseUrl, findDevicesPath));

        map.put("ids", ids);
        byte[] data = client.getBytes(map);
        outputData(data, response);
    }

    @RequestMapping(value = "/get", method = RequestMethod.POST)
    public void getDevice(@RequestParam(name = "id") String id, Map<String, String> map, HttpServletResponse response) {
        TemplateClient client =
                Feign.builder().target(TemplateClient.class, String.format("%s%s", baseUrl, getDevicePath));
        map.put("id", id);
        byte[] data = client.getBytes(map);
        outputData(data, response);
    }

    private void outputData(byte[] data, HttpServletResponse response) {
        try {
            ServletOutputStream stream = response.getOutputStream();
            stream.write(data);
            stream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
