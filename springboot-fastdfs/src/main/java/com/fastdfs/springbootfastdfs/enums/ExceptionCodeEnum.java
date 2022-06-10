package com.fastdfs.springbootfastdfs.enums;

/**
 * @Auther: Mr.Kong
 * @Date: 2020/3/19 16:43
 * @Description: 错误代码
 */
public enum ExceptionCodeEnum {

    OTHER_ERR("10000", "系统异常"),

    REQUEST_PARAMETER_ERR("10001", "请求参数错误"),
    PARAMETER_RESOLVE_ERR("10002", "请求参数解析异常"),
    PARAMETER_TYPE_ERR("10003", "方法参数值与参数类型不匹配"),
    PARAMETER_PATH_ERR("10004", "路径参数绑定失败"),
    PARAMETER_PARSE_ERR("10005", "请求/响应参数转换失败"),
    PARAMETER_MISSING_ERR("10006", "URL参数缺失"),
    PARAMETER_BIND_ERR("10006", "参数绑定失败"),

    RPC_SERVICE_ERR("20000", "远程服务异常"),
    SERVICE_ERR("20001", "业务服务异常"),

    OAUTH_FAIL("60000", "授权失败"),

    HTTP_ACTION_ERR("70001", "HTTP 请求动作类型错误"),

    SQL_UK_ERR("80001", "唯一约束异常"),

    ACCOUNT_NOT_LOGIN_ERROR("90000", "请先登录"),
    ACCOUNT_PASS_ERROR("90001", "账号或密码错误"),
    ACCOUNT_NOT_ACTIVATED("90002", "该账号还未激活"),
    ACCOUNT_STOP("90003", "该账号已被停用"),
    ACCOUNT_NOT_PERMISSION("90004", "数据权限不足"),
    ACCOUNT_EXIST("90005", "账号已存在"),
    ACCOUNT_BIND_WX("90006", "该账号已经绑定了微信用户"),

    PRODUCT_PRICE_ERROR("130001", "商品价格错误"),

    WX_ORDER_ERROR("210001", "微信支付统一下单调用失败"),
    WX_NOTIFY_ERROR("210002", "微信支付签名失败或参数错误"),


    ;
    private String code;

    private String text;

    ExceptionCodeEnum(String code, String text) {
        this.code = code;
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public String getText() {
        return text;
    }
}
