package com.itdreamworks.dataoutput.client;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Component
@org.springframework.cloud.netflix.feign.FeignClient(name = "Test",url = "${feign.datamanage.url}")
public interface FeignService {

    @RequestMapping(value = "${feign.datamanage.device}",method = RequestMethod.GET)
    public String getSellDevice();
}
