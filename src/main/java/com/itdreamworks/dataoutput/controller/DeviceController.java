package com.itdreamworks.dataoutput.controller;

import com.itdreamworks.dataoutput.annotation.Permission;
import com.itdreamworks.dataoutput.client.DataCacheFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/device")
public class DeviceController {

    @Value("${feign.datacache.snapshots.p1}")
    String str;

    @Autowired
    DataCacheFeignClient client;

    @Permission
    @RequestMapping(value = "/snapshots", method = RequestMethod.POST)
    public String getSnapshots(@RequestParam(name = "ids") String ids) {
        return  client.getDeviceSnapshots(ids);
    }

    @Permission
    @RequestMapping(value = "/get", method = RequestMethod.POST)
    public String getDeviceInfo(@RequestParam(name = "id") String id) {
        return client.getDeviceInfo(id);
    }
}
