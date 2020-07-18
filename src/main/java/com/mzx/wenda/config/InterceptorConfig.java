package com.mzx.wenda.config;

import com.mzx.wenda.intercepter.LoginInterceptor;
import com.mzx.wenda.intercepter.LoginRequiredInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by mengz on 2017/5/19.
 */
@Component
public class InterceptorConfig extends WebMvcConfigurerAdapter {

    @Autowired
    LoginInterceptor loginInterceptor;

    @Autowired
    LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor);
        registry.addInterceptor(loginRequiredInterceptor).addPathPatterns("/addcomment").addPathPatterns("/user/*").addPathPatterns("/question/add");
        super.addInterceptors(registry);
    }
}
