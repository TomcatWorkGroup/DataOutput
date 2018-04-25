package com.itdreamworks.dataoutput.controller;

import com.itdreamworks.dataoutput.client.TemplateClient;
import feign.Feign;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping(value = "/device")
public class DeviceController {

    @Value("${feign.datacache.url}")
    private String baseUrl;
    @Value("${feign.datacache.snapshots.path}")
    private String snapshotsPath;
    @Value("${feign.datacache.deviceinfo.path}")
    private String deviceinfoPath;

   // @Permission
    @RequestMapping(value = "/snapshots", method = RequestMethod.POST)
    public String getSnapshots(@RequestParam(name = "ids") String ids, Map<String,String> map) {
        TemplateClient client =
                Feign.builder().target(TemplateClient.class, String.format("%s%s",baseUrl,snapshotsPath));

        map.put("ids",ids);
        return  client.post(map);
    }

   // @Permission
    @RequestMapping(value = "/get", method = RequestMethod.POST)
    public String getDeviceInfo(@RequestParam(name = "id") String id, Map<String,String> map) {
        TemplateClient client =
                Feign.builder().target(TemplateClient.class, String.format("%s%s",baseUrl,deviceinfoPath));

        map.put("id",id);
        return  client.post(map);
    }
}
