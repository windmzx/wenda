package com.mzx.intercepter;


import com.mzx.dao.LoginTicketDAO;
import com.mzx.dao.UserDAO;
import com.mzx.model.HostHolder;
import com.mzx.model.LoginTicket;
import com.mzx.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created by mengz on 2017/5/18.
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {


    @Autowired
    LoginTicketDAO loginTicketDAO;
    @Autowired
    UserDAO userDAO;
    @Autowired
    HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String ticket = null;
        if (httpServletRequest.getCookies() != null) {
            for (Cookie cookie : httpServletRequest.getCookies()) {
                if (cookie.getName().equals("ticket"))
                    ticket = cookie.getValue();
            }
        }

        if (ticket != null) {
            LoginTicket ticket1 = loginTicketDAO.selectTicketByTicket(ticket);
            if (ticket1 != null && ticket1.getExpired().after(new Date())) {
                User user = userDAO.selectById(ticket1.getUserId());
                if (user != null)
                    hostHolder.setUser(user);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null && hostHolder.getUser() != null)
            modelAndView.addObject("user", hostHolder.getUser());
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        hostHolder.clear();
    }
}
