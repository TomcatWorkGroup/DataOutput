package com.itdreamworks.dataoutput.client;

import feign.QueryMap;
import feign.RequestLine;

import java.util.Map;

public interface TemplateClient {
    @RequestLine("GET")
    String get();

    @RequestLine("GET")
    String get(@QueryMap Map<String, String> map);

    @RequestLine("POST")
    String post();

    @RequestLine("POST")
    String post(@QueryMap Map<String, String> map);

    @RequestLine("POST")
    byte[] getBytes();

    @RequestLine("POST")
    byte[] getBytes(@QueryMap Map<String, String> map);
}
