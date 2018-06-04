package com.itdreamworks.dataoutput.model;

import java.io.Serializable;

public class Result implements Serializable {

    public static final int RESULTCODE_SUCCESS = 1;
    public static final int RESULTCODE_FAILD = 0;

    private String msg;
    private int code;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
