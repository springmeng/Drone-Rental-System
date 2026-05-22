package com.drone.rental.security;

import lombok.Data;

/**
 * 用户上下文，存储当前登录用户信息
 */
public class UserContext {

    private static final ThreadLocal<UserInfo> USER_HOLDER = new ThreadLocal<>();

    /**
     * 设置当前用户信息
     */
    public static void setCurrentUser(UserInfo userInfo) {
        USER_HOLDER.set(userInfo);
    }

    /**
     * 获取当前用户信息
     */
    public static UserInfo getCurrentUser() {
        return USER_HOLDER.get();
    }

    /**
     * 获取当前用户ID
     */
    public static Long getCurrentUserId() {
        UserInfo userInfo = USER_HOLDER.get();
        return userInfo != null ? userInfo.getUserId() : null;
    }

    /**
     * 获取当前用户名
     */
    public static String getCurrentUsername() {
        UserInfo userInfo = USER_HOLDER.get();
        return userInfo != null ? userInfo.getUsername() : null;
    }

    /**
     * 获取当前用户角色
     */
    public static Integer getCurrentRole() {
        UserInfo userInfo = USER_HOLDER.get();
        return userInfo != null ? userInfo.getRole() : null;
    }

    /**
     * 判断当前用户是否为管理员
     */
    public static boolean isAdmin() {
        Integer role = getCurrentRole();
        return role != null && role == 1;
    }

    /**
     * 清除当前用户信息
     */
    public static void clear() {
        USER_HOLDER.remove();
    }

    /**
     * 用户信息内部类
     */
    @Data
    public static class UserInfo {
        private Long userId;
        private String username;
        private Integer role;

        public UserInfo() {}

        public UserInfo(Long userId, String username, Integer role) {
            this.userId = userId;
            this.username = username;
            this.role = role;
        }
    }
}
