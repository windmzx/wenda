package com.mzx.wenda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan("com.mzx.wenda.*")
public class WendaApplication {

    public static void main(String[] args) {
        SpringApplication.run(WendaApplication.class, args);
    }
}
