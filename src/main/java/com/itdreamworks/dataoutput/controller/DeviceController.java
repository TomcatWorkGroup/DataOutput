package com.itdreamworks.dataoutput.controller;

import com.itdreamworks.dataoutput.annotation.Permission;
import com.itdreamworks.dataoutput.client.DataCacheFeignClinet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/device")
public class DeviceController {

    @Value("${feign.datacache.snapshots.p1}")
    String str;

    @Autowired
    DataCacheFeignClinet client;

    @Permission
    @RequestMapping(value = "/snapshots", method = RequestMethod.POST)
    public String getSnapshots(@RequestParam(name = "ids") String ids) {
        return  client.getDeviceSnapshots(ids);
    }

    @Permission
    @RequestMapping(value = "/device", method = RequestMethod.POST)
    public String getDeviceInfo(@PathVariable(name = "id") String id) {
        return client.getDeviceInfo(id);
    }
}
