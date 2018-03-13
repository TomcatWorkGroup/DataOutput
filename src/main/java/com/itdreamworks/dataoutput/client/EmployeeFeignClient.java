package com.itdreamworks.dataoutput.client;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@org.springframework.cloud.netflix.feign.FeignClient(name = "Test", url = "http://39.106.168.77:8081/datamanage")
public interface EmployeeFeignClinet {

    @RequestMapping(value = "/employee/find", method = RequestMethod.GET)
    String getSellDevices();

    @RequestMapping(value = "/device/user", method = RequestMethod.POST)
    String getUserDevices(@RequestParam("userId") int userId);

}
