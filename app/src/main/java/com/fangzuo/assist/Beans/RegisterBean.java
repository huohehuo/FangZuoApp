package com.fangzuo.assist.Beans;

public class RegisterBean {
    public String Register_code;
    public String type;
    public String val1;
    public String val2;
    public String val3;
    public String val4;
    public String val5;

    public RegisterBean(){}
    public RegisterBean(String type, String val1) {
        this.type = type;
        this.val1 = val1;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRegister_code() {
        return Register_code;
    }

    public void setRegister_code(String register_code) {
        Register_code = register_code;
    }

    public String getVal1() {
        return val1;
    }

    public void setVal1(String val1) {
        this.val1 = val1;
    }

    public String getVal2() {
        return val2;
    }

    public void setVal2(String val2) {
        this.val2 = val2;
    }

    public String getVal3() {
        return val3;
    }

    public void setVal3(String val3) {
        this.val3 = val3;
    }

    public String getVal4() {
        return val4;
    }

    public void setVal4(String val4) {
        this.val4 = val4;
    }

    public String getVal5() {
        return val5;
    }

    public void setVal5(String val5) {
        this.val5 = val5;
    }
}
