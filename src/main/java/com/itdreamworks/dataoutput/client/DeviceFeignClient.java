package com.itdreamworks.dataoutput.client;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@org.springframework.cloud.netflix.feign.FeignClient(name = "Test", url = "${feign.datamanage.url}")
public interface DeviceFeignClient {

    @RequestMapping(value = "/device/sell", method = RequestMethod.GET)
    String getSellDevices();

    @RequestMapping(value = "/device/user", method = RequestMethod.POST)
    String getUserDevices(@RequestParam("userId") int userId);

}
