package com.mzx.wenda.util;

import com.mzx.wenda.model.HostHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Aspect
@Slf4j
@Component
public class SystemLogAspect {
    @Pointcut("@annotation(com.mzx.wenda.util.SystemLog)")
    public void logPointCut() {
    }

    @Autowired
    HostHolder hostHolder;

    @Before(value = "logPointCut()")
    public void before(JoinPoint joinPoint) {
        /*从切面值入点获取植入点方法*/
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        /*获取切入点方法*/
        Method method = signature.getMethod();
        /*获取方法上的值*/
        SystemLog systemControllerLog = method.getAnnotation(SystemLog.class);

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String requestUri = request.getRequestURI();/*获取请求地址*/
        String requestMethod = request.getMethod();/*获取请求方式*/
//        String remoteAddr1 = request.getRemoteAddr();/*获取请求IP*/
        String remoteAddr = this.getIpAddress(request);

        log.info("time:{},method:{},requestType:{},ip:{},userid:{}", System.currentTimeMillis(), method, requestUri, remoteAddr,hostHolder.getUser());
    }


    public String getIpAddress(HttpServletRequest request) {

        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ipAddress = inet.getHostAddress();
            }
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > 15) { //"***.***.***.***".length() = 15
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }


}
