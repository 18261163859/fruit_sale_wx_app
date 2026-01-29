package com.fruit.sale.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fruit.sale.common.Result;
import com.fruit.sale.common.ResultCode;
import com.fruit.sale.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * 认证拦截器
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Bearer ";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String requestURI = request.getRequestURI();
        log.debug("拦截器处理请求: {}", requestURI);

        // 从请求头中获取 Token
        String token = request.getHeader(HEADER);

        if (!StringUtils.hasText(token)) {
            log.warn("请求 {} 缺少Token", requestURI);
            writeErrorResponse(response, ResultCode.UNAUTHORIZED);
            return false;
        }

        // 去除 Token 前缀
        if (token.startsWith(PREFIX)) {
            token = token.substring(PREFIX.length()).trim();
        }

        // 验证 Token
        if (!JwtUtils.validateToken(token)) {
            log.warn("请求 {} 的Token无效", requestURI);
            writeErrorResponse(response, ResultCode.TOKEN_INVALID);
            return false;
        }

        // 判断 Token 是否过期
        if (JwtUtils.isTokenExpired(token)) {
            log.warn("请求 {} 的Token已过期", requestURI);
            writeErrorResponse(response, ResultCode.TOKEN_EXPIRED);
            return false;
        }

        // 从 Token 中获取用户ID并设置到请求属性中
        Long userId = JwtUtils.getUserIdFromToken(token);
        request.setAttribute("userId", userId);
        log.debug("请求 {} 认证成功, userId: {}", requestURI, userId);

        return true;
    }

    /**
     * 写入错误响应
     */
    private void writeErrorResponse(HttpServletResponse response, ResultCode resultCode) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        Result<Void> result = Result.error(resultCode);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
