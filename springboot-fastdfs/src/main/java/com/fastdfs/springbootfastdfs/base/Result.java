package com.fastdfs.springbootfastdfs.base;

import java.io.Serializable;

/**
 * @Auther: Mr.Kong
 * @Date: 2020/3/19 16:36
 * @Description: 返回的 数据模型
 */
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 8942453556523397917L;
    private String code;
    private String msg;
    private Boolean success;
    private T data;

    public Result(Boolean success, String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.success = success;
    }

    public Result(Boolean success, String code, String msg) {
        this.code = code;
        this.msg = msg;
        this.success = success;
    }

    public Result(Boolean success, T data) {
        this.data = data;
        this.success = success;
    }

    public Result(Boolean success) {
        this.success = success;
    }

    public Result() {

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @SuppressWarnings("unchecked")
    public <F> Result<F> transferIgnoreData() {
        this.setData(null);
        return (Result<F>) this;
    }

}
