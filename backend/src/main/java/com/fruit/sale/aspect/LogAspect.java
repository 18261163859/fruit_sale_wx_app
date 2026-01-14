package com.fruit.sale.aspect;

import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 日志切面
 *
 * @author fruit-sale
 * @since 2024-09-30
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    /**
     * 定义切入点：拦截所有 Controller
     */
    @Pointcut("execution(* com.fruit.sale.controller..*.*(..))")
    public void controllerPointcut() {
    }

    /**
     * 环绕通知
     */
    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            // 打印请求信息
            log.info("========================================");
            log.info("请求地址: {}", request.getRequestURL().toString());
            log.info("请求方式: {}", request.getMethod());
            log.info("请求类方法: {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            log.info("请求参数: {}", JSONUtil.toJsonStr(joinPoint.getArgs()));
        }

        // 执行方法
        Object result = joinPoint.proceed();

        // 打印响应信息
        long endTime = System.currentTimeMillis();
        log.info("响应结果: {}", JSONUtil.toJsonStr(result));
        log.info("执行时间: {} ms", endTime - startTime);
        log.info("========================================");

        return result;
    }
}
