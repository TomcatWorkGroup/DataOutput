package com.itdreamworks.dataoutput.client;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@org.springframework.cloud.netflix.feign.FeignClient(name = "Test", url = "${feign.datamanage.url}")
public interface EmployeeFeignClient {
    @RequestMapping(value = "/employee/find", method = RequestMethod.POST)
    String getEmployee(@RequestParam("loginId") String loginId);

    @RequestMapping(value = "/employee/devices", method = RequestMethod.POST)
    String getDevices(@RequestParam("employeeId") int employeeId);
}
