package com.itdreamworks.dataoutput.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SsmConfig {

    public String getSsmFilePath() {
        return ssmFilePath;
    }

    @Value("${web.ssm.file}")
    private String ssmFilePath;


}
