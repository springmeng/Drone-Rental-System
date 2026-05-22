package com.drone.rental.security;

import com.drone.rental.common.Constants;
import com.drone.rental.common.Result;
import com.drone.rental.common.ResultCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 管理员权限拦截器
 */
@Slf4j
@Component
public class AdminInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 检查当前用户是否为管理员
        Integer role = UserContext.getCurrentRole();
        if (role == null || role != Constants.ROLE_ADMIN) {
            writeErrorResponse(response, ResultCode.FORBIDDEN);
            return false;
        }

        return true;
    }

    /**
     * 写入错误响应
     */
    private void writeErrorResponse(HttpServletResponse response, ResultCode resultCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        Result<Void> result = Result.error(resultCode);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
