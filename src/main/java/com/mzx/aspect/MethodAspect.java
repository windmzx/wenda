package com.mzx.aspect;

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

@Aspect
@Component
public class MethodAspect {
    private Logger logger = LoggerFactory.getLogger(Aspect.class);


    @Before("execution(* com.mzx.controller.*Controller.*(..))")
    public void printMehod(JoinPoint joinPoint) {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        logger.info("URI = " + request.getRequestURI());

    }

    @After("execution( * com.mzx.controller.*Controller.*(..))")
    public void printres(JoinPoint joinPoint) {

    }
}
