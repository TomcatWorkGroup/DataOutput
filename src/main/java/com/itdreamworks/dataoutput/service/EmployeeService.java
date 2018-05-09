package com.itdreamworks.dataoutput.service;

import com.itdreamworks.dataoutput.client.TemplateClient;
import feign.Feign;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmployeeService {
    @Value("${feign.datamanage.url}")
    private String manageUrl;

    public String getEmployee(String loginId){
        TemplateClient client =
                Feign.builder().target(TemplateClient.class, String.format("%s%s",manageUrl,"/employee/find"));
        Map<String,String> map = new HashMap<>(1);
        map.put("loginId",loginId);
        return client.post(map);
    }

    public String getDevices(int employeeId){
        TemplateClient client =
                Feign.builder().target(TemplateClient.class, String.format("%s%s",manageUrl,"/employee/devices"));
        Map<String,String> map = new HashMap<>(1);
        map.put("employeeId",String.format("%s",employeeId));
        return client.post(map);
    }

    public String[] getEmployeeDevicesId(String employId) {
        return null;
    }

    public String getPassword(String loginId) {
        return loginId.equals("admin") ? "123456" : null;
    }
}
