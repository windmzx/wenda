package com.mzx.wenda.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by mengz on 2017/5/18.
 */

@Slf4j
@Aspect
@Component
public class MethodAspect {


    @Before("execution(* com.mzx.wenda.controller.*Controller.*(..))")
    public void printMehod(JoinPoint joinPoint) {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        log.info("URI = " + request.getRequestURI());

    }

    @After("execution( * com.mzx.wenda.controller.*Controller.*(..))")
    public void printres(JoinPoint joinPoint) {

    }
}
