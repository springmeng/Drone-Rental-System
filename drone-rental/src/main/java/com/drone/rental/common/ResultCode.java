package com.drone.rental.common;

import lombok.Getter;

/**
 * 响应状态码枚举
 */
@Getter
public enum ResultCode {

    // 成功
    SUCCESS(200, "操作成功"),

    // 客户端错误 4xx
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "没有操作权限"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),

    // 服务端错误 5xx
    ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),

    // 业务错误 1xxx
    USER_NOT_EXIST(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "用户名或密码错误"),
    USER_DISABLED(1003, "用户已被禁用"),
    USER_ALREADY_EXISTS(1004, "用户名已存在"),
    USER_CREDIT_BAD(1005, "用户诚信状态不良，禁止操作"),

    // 资质相关 11xx
    QUALIFICATION_NOT_EXIST(1101, "飞行资质不存在"),
    QUALIFICATION_NOT_APPROVED(1102, "飞行资质未通过审核"),
    QUALIFICATION_EXPIRED(1103, "飞行资质已过期"),

    // 无人机相关 12xx
    DRONE_NOT_EXIST(1201, "无人机不存在"),
    DRONE_NOT_AVAILABLE(1202, "无人机不可租赁"),
    DRONE_STOCK_NOT_ENOUGH(1203, "无人机库存不足"),
    DRONE_IN_MAINTENANCE(1204, "无人机正在维修中"),
    DRONE_OFF_SHELF(1205, "无人机已下架"),

    // 订单相关 13xx
    ORDER_NOT_EXIST(1301, "订单不存在"),
    ORDER_ALREADY_PAID(1302, "订单已支付"),
    ORDER_ALREADY_CANCELLED(1303, "订单已取消"),
    ORDER_CANNOT_CANCEL(1304, "订单状态不允许取消"),
    ORDER_CREATE_FAILED(1305, "订单创建失败"),

    // 支付相关 14xx
    PAYMENT_NOT_EXIST(1401, "支付记录不存在"),
    PAYMENT_ALREADY_DONE(1402, "已完成支付"),
    PAYMENT_FAILED(1403, "支付失败"),
    REFUND_FAILED(1404, "退款失败"),

    // 空域备案相关 15xx
    AIRSPACE_NOT_EXIST(1501, "空域备案不存在"),
    AIRSPACE_NOT_APPROVED(1502, "空域备案未通过审核"),
    AIRSPACE_REQUIRED(1503, "下单必须绑定空域备案"),

    // 故障相关 16xx
    FAULT_NOT_EXIST(1601, "故障记录不存在"),
    FAULT_ALREADY_PROCESSED(1602, "故障已处理"),

    // 维修相关 17xx
    MAINTENANCE_NOT_EXIST(1701, "维修工单不存在"),
    MAINTENANCE_ALREADY_COMPLETED(1702, "维修工单已完成"),

    // 评论相关 18xx
    COMMENT_NOT_EXIST(1801, "评论不存在"),
    COMMENT_BLOCKED(1802, "评论已被屏蔽"),

    // 参数校验 19xx
    PARAM_ERROR(1901, "参数校验失败"),
    PARAM_MISSING(1902, "缺少必要参数");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
