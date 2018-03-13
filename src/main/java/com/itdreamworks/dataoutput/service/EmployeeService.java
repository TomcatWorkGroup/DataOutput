package com.itdreamworks.dataoutput.service;

import com.itdreamworks.dataoutput.client.EmployeeFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeFeignClient employeeFeignClient;

    public String getEmployee(String loginId){
        return employeeFeignClient.getEmployee(loginId);
    }

    public String getDevices(int employeeId){
        return employeeFeignClient.getDevices(employeeId);
    }

    public String[] getEmployeeDevicesId(String employId) {
        return null;
    }

    public String getPassword(String loginId) {
        return loginId.equals("admin") ? "123456" : null;
    }
}
