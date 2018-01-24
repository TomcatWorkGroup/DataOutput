package com.itdreamworks.dataoutput.controller;

import com.itdreamworks.dataoutput.client.FeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HomeController {

    @Autowired
    FeignService client;

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String index() {
        System.out.println("---------------进入/home--------------");
        return client.getSellDevice();
    }
}
