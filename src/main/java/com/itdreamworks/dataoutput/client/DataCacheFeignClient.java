package com.itdreamworks.dataoutput.client;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@org.springframework.cloud.netflix.feign.FeignClient(name = "Test", url = "${feign.datacache.url}")
public interface DataCacheFeignClient {

    @RequestMapping(value = "${feign.datacache.snapshots.path}", method = RequestMethod.POST)
    String getDeviceSnapshots(@RequestParam(name = "ids") String p1);

    @RequestMapping(value = "${feign.datacache.deviceinfo.path}", method = RequestMethod.POST)
    String getDeviceInfo(@RequestParam(name = "id") String p1);

}
