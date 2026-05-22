package com.drone.rental.common;

/**
 * 系统常量类
 */
public class Constants {

    private Constants() {
        throw new IllegalStateException("Constants class");
    }

    // ================= 用户角色 =================
    /** 普通用户 */
    public static final int ROLE_USER = 0;
    /** 管理员 */
    public static final int ROLE_ADMIN = 1;

    // ================= 用户状态 =================
    /** 禁用 */
    public static final int USER_STATUS_DISABLED = 0;
    /** 正常 */
    public static final int USER_STATUS_NORMAL = 1;

    // ================= 诚信状态 =================
    /** 不良 */
    public static final int CREDIT_STATUS_BAD = 0;
    /** 正常 */
    public static final int CREDIT_STATUS_NORMAL = 1;

    // ================= 审核状态 =================
    /** 待审核 */
    public static final int AUDIT_STATUS_PENDING = 0;
    /** 审核通过 */
    public static final int AUDIT_STATUS_APPROVED = 1;
    /** 审核拒绝 */
    public static final int AUDIT_STATUS_REJECTED = 2;

    // ================= 无人机状态 =================
    /** 缺货 */
    public static final int DRONE_STATUS_OUT_OF_STOCK = 0;
    /** 在售 */
    public static final int DRONE_STATUS_AVAILABLE = 1;
    /** 维护中 */
    public static final int DRONE_STATUS_MAINTENANCE = 2;

    // ================= 上架状态 =================
    /** 下架 */
    public static final int ON_SHELF_NO = 0;
    /** 上架 */
    public static final int ON_SHELF_YES = 1;

    // ================= 库存变更类型 =================
    /** 入库 */
    public static final int STOCK_CHANGE_IN = 1;
    /** 出租 */
    public static final int STOCK_CHANGE_RENT = 2;
    /** 归还 */
    public static final int STOCK_CHANGE_RETURN = 3;
    /** 维修占用 */
    public static final int STOCK_CHANGE_MAINTENANCE = 4;
    /** 维修归还 */
    public static final int STOCK_CHANGE_MAINTENANCE_RETURN = 5;

    // ================= 订单状态 =================
    /** 待支付 */
    public static final int ORDER_STATUS_UNPAID = 0;
    /** 待发货（已支付） */
    public static final int ORDER_STATUS_PAID = 1;
    /** 待收货（已发货） */
    public static final int ORDER_STATUS_SHIPPED = 2;
    /** 租赁中（已收货） */
    public static final int ORDER_STATUS_RENTING = 3;
    /** 已归还 */
    public static final int ORDER_STATUS_RETURNED = 4;
    /** 已取消 */
    public static final int ORDER_STATUS_CANCELLED = 5;
    /** 已退款 */
    public static final int ORDER_STATUS_REFUNDED = 6;

    // ================= 支付类型 =================
    /** 订单支付 */
    public static final int PAYMENT_TYPE_ORDER = 1;
    /** 押金支付 */
    public static final int PAYMENT_TYPE_DEPOSIT = 2;
    /** 维修费支付 */
    public static final int PAYMENT_TYPE_MAINTENANCE = 3;

    // ================= 支付状态 =================
    /** 未支付 */
    public static final int PAYMENT_STATUS_UNPAID = 0;
    /** 已支付 */
    public static final int PAYMENT_STATUS_PAID = 1;
    /** 已退款 */
    public static final int PAYMENT_STATUS_REFUNDED = 2;

    // ================= 故障审核状态 =================
    /** 待审核 */
    public static final int FAULT_AUDIT_PENDING = 0;
    /** 确认故障 */
    public static final int FAULT_AUDIT_CONFIRMED = 1;
    /** 非故障 */
    public static final int FAULT_AUDIT_NOT_FAULT = 2;

    // ================= 维修工单状态 =================
    /** 待维修 */
    public static final int MAINTENANCE_STATUS_PENDING = 0;
    /** 维修中 */
    public static final int MAINTENANCE_STATUS_IN_PROGRESS = 1;
    /** 已完成 */
    public static final int MAINTENANCE_STATUS_COMPLETED = 2;
    /** 已取消 */
    public static final int MAINTENANCE_STATUS_CANCELLED = 3;

    // ================= 评论状态 =================
    /** 已屏蔽 */
    public static final int COMMENT_STATUS_BLOCKED = 0;
    /** 正常 */
    public static final int COMMENT_STATUS_NORMAL = 1;

    // ================= 分页默认值 =================
    /** 默认页码 */
    public static final int DEFAULT_PAGE_NUM = 1;
    /** 默认每页大小 */
    public static final int DEFAULT_PAGE_SIZE = 10;
    /** 最大每页大小 */
    public static final int MAX_PAGE_SIZE = 100;
}
