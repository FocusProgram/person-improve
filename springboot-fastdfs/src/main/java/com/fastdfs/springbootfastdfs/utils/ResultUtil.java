package com.fastdfs.springbootfastdfs.utils;

import com.fastdfs.springbootfastdfs.base.Result;
import com.fastdfs.springbootfastdfs.enums.ExceptionCodeEnum;
import com.fastdfs.springbootfastdfs.enums.ResultCodeEnum;

/**
 * @Auther: Mr.Kong
 * @Date: 2020/3/19 16:33
 * @Description: 返回工具类
 */
public class ResultUtil {

    /**
     * 请求成功
     *
     * @return
     */
    public static <T> Result<T> success() {
        return new Result<T>(true);
    }

    /**
     * 成功请求
     *
     * @param data
     * @return
     */
    public static <T> Result<T> success(T data) {
        return new Result<T>(true, data);
    }

    /**
     * 操作失败
     *
     * @return
     */
    public static <T> Result<T> fail() {
        return new Result<T>(false);
    }

    /**
     * 服务器错误
     *
     * @return
     */
    public static <T> Result<T> error() {
        return new Result<T>(false, ResultCodeEnum.INTERNAL_SERVER_ERROR.getCode(), ResultCodeEnum.INTERNAL_SERVER_ERROR.getMessage());
    }


    /**
     * 服务器错误
     *
     * @param data
     * @return
     */
    public static <T> Result<T> error(T data) {
        return new Result<T>(false, ResultCodeEnum.INTERNAL_SERVER_ERROR.getCode(), ResultCodeEnum.INTERNAL_SERVER_ERROR.getMessage(), data);
    }

    /**
     * 参数错误
     *
     * @return
     */
    public static <T> Result<T> paramError() {
        return new Result<T>(false, ResultCodeEnum.INVALID_REQUEST.getCode(), ResultCodeEnum.INVALID_REQUEST.getMessage());
    }

    /**
     * 参数错误
     *
     * @param data
     * @return
     */
    public static <T> Result<T> paramError(T data) {
        return new Result<T>(false, ResultCodeEnum.INVALID_REQUEST.getCode(), ResultCodeEnum.INVALID_REQUEST.getMessage(), data);
    }

    /**
     * 没有权限
     *
     * @return
     */
    public static <T> Result<T> unAuthorized() {
        return new Result<T>(false, ResultCodeEnum.UNAUTHORIZED.getCode(), ResultCodeEnum.UNAUTHORIZED.getMessage());
    }

    /**
     * 没有权限
     *
     * @param data
     * @return
     */
    public static <T> Result<T> unAuthorized(T data) {
        return new Result<T>(false, ResultCodeEnum.UNAUTHORIZED.getCode(), ResultCodeEnum.UNAUTHORIZED.getMessage(), data);
    }

    /**
     * 服务降级
     *
     * @return
     */
    public static <T> Result<T> intfDown() {
        return new Result<T>(false, ResultCodeEnum.INTF_OUT_OF_SERVICE.getCode(), ResultCodeEnum.INTF_OUT_OF_SERVICE.getMessage());
    }

    /**
     * 禁止访问
     *
     * @return
     */
    public static <T> Result<T> forbidden() {
        return new Result<T>(false, ResultCodeEnum.FORBIDDEN.getCode(), ResultCodeEnum.FORBIDDEN.getMessage());
    }

    /**
     * 禁止访问
     *
     * @param data
     * @return
     */
    public static <T> Result<T> forbidden(T data) {
        return new Result<T>(false, ResultCodeEnum.FORBIDDEN.getCode(), ResultCodeEnum.FORBIDDEN.getMessage(), data);
    }


    /**
     * 资源不存在
     *
     * @return
     */
    public static <T> Result<T> notFound() {
        return new Result<T>(false, ResultCodeEnum.NOT_FOUND.getCode(), ResultCodeEnum.NOT_FOUND.getMessage());
    }


    /**
     * 资源不存在
     *
     * @param data
     * @return
     */
    public static <T> Result<T> notFound(T data) {
        return new Result<T>(false, ResultCodeEnum.NOT_FOUND.getCode(), ResultCodeEnum.NOT_FOUND.getMessage(), data);
    }


    /**
     * 请求的格式不正确
     *
     * @return
     */
    public static <T> Result<T> notAcceptable() {
        return new Result<T>(false, ResultCodeEnum.NOT_ACCEPTABLE.getCode(), ResultCodeEnum.NOT_ACCEPTABLE.getMessage());
    }


    /**
     * 请求的格式不正确
     *
     * @param data
     * @return
     */
    public static <T> Result<T> notAcceptable(T data) {
        return new Result<T>(false, ResultCodeEnum.NOT_ACCEPTABLE.getCode(), ResultCodeEnum.NOT_ACCEPTABLE.getMessage(), data);
    }


    /**
     * 数据已经被删除
     *
     * @return
     */
    public static <T> Result<T> gone() {
        return new Result<T>(false, ResultCodeEnum.GONE.getCode(), ResultCodeEnum.GONE.getMessage());
    }


    /**
     * 数据已经被删除
     *
     * @param data
     * @return
     */
    public static <T> Result<T> gone(T data) {
        return new Result<T>(false, ResultCodeEnum.GONE.getCode(), ResultCodeEnum.GONE.getMessage(), data);
    }


    /**
     * 实体参数校验错误
     *
     * @return
     */
    public static <T> Result<T> unprocesableEntity() {
        return new Result<T>(false, ResultCodeEnum.UNPROCESABLE_ENTITY.getCode(), ResultCodeEnum.UNPROCESABLE_ENTITY.getMessage());
    }


    /**
     * 实体参数校验错误
     *
     * @param data
     * @return
     */
    public static <T> Result<T> unprocesableEntity(T data) {
        return new Result<T>(false, ResultCodeEnum.UNPROCESABLE_ENTITY.getCode(), ResultCodeEnum.UNPROCESABLE_ENTITY.getMessage(), data);
    }

    public static <T> Result<T> invalidRequestQoEmpty() {
        return new Result<T>(false, ResultCodeEnum.INVALID_REQUEST_QO_EMPTY.getCode(), ResultCodeEnum.INVALID_REQUEST_QO_EMPTY.getMessage());
    }

    public static <T> Result<T> invalidRequestmissingRequireField() {
        return new Result<T>(false, ResultCodeEnum.INVALID_REQUEST_MISSING_REQUIRE_FIELD.getCode(), ResultCodeEnum.INVALID_REQUEST_MISSING_REQUIRE_FIELD.getMessage());
    }

    /**
     * 未知错误
     *
     * @return
     */
    public static <T> Result<T> unKnowError() {
        return new Result<T>(false, ResultCodeEnum.UN_KNOW_ERROR.getCode(), ResultCodeEnum.UN_KNOW_ERROR.getMessage());
    }

    /**
     * 未知错误
     *
     * @param data
     * @return
     */
    public static <T> Result<T> unKnowError(T data) {
        return new Result<T>(false, ResultCodeEnum.UN_KNOW_ERROR.getCode(), ResultCodeEnum.UN_KNOW_ERROR.getMessage(), data);
    }


    /**
     * 自定义返回
     *
     * @param e
     * @return
     */
    public static <T> Result<T> custom(Boolean success, ResultCodeEnum e) {
        return custom(success, e.getCode(), e.getMessage());
    }

    /**
     * 自定义返回
     *
     * @param e
     * @return
     */
    public static <T> Result<T> custom(Boolean success) {
        return custom(success, null, null);
    }

    /**
     * 自定义返回
     *
     * @param error
     * @return
     */
    public static <T> Result<T> custom(Boolean success, String code, String error) {
        return custom(success, code, error, null);
    }

    /**
     * 自定义返回
     *
     * @param error
     * @return
     */
    public static <T> Result<T> custom(String code, String error) {
        return custom(false, code, error, null);
    }

    /**
     * 自定义返回
     *
     * @param exceptionCodeEnum
     * @return
     */
    public static <T> Result<T> custom(ExceptionCodeEnum exceptionCodeEnum) {
        return custom(false, exceptionCodeEnum.getCode(), exceptionCodeEnum.getText(), null);
    }

    /**
     * 自定义返回
     *
     * @param error
     * @param data
     * @return
     */
    public static <T> Result<T> custom(Boolean success, String code, String error, T data) {
        return new Result<T>(success, code, error, data);
    }

    public static <T> Result<T> custom(Boolean success, String code) {
        return custom(success, code, null);
    }

    public static <T> Result<T> tip(String msg) {
        return custom(false, null, msg);
    }

}
