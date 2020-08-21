package com.mzx.wenda.controller;


import com.mzx.wenda.service.UserService;
import com.mzx.wenda.util.JedisAdapter;
import com.mzx.wenda.util.WendaUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;


import org.apache.logging.log4j.MarkerManager;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mzx on 5/1/2017.
 */

@Slf4j
@Controller
public class LoginController {

    private static final Marker LOGIN_MARKER = MarkerFactory.getMarker("LOGIN");

    @Autowired
    UserService userService;

    @Autowired
    HttpServletRequest request;

    @Autowired
    JedisAdapter jedisAdapter;

    @RequestMapping(path = {"/login"}, method = {RequestMethod.POST})
    public String register(Model model,
                           @RequestParam("username") String username,
                           @RequestParam("password") String password,
                           @RequestParam(value = "next", required = false) String callback,
                           HttpServletResponse response) {

        String logfail_key = "logfail" + username;
        if (jedisAdapter.get(logfail_key) != null) {
            model.addAttribute("msg", new HashMap<String, String>() {
                {
                    put("msg", "失败次数过多，请稍后再试");
                }
            });
            return "login";

        }
        Map<String, String> map = userService.login(username, password);
        if (map.containsKey("msg")) {
            model.addAttribute("msg", map.get("msg"));
            //登录失败
            log.info(LOGIN_MARKER, "envent:{},requestUri:{},ip:{},username:{},{}", "login", "/login", WendaUtil.getIpAddress(request), username, "fail");
            return "login";
        }


        if (map.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket", map.get("ticket"));
            cookie.setPath("/");
            cookie.setMaxAge(3600 * 24 * 7);
            response.addCookie(cookie);
            if (StringUtils.isNotBlank(callback)) {
                log.info(LOGIN_MARKER, "envent:{},requestType:{},ip:{},username:{},{}", "login", "/login", WendaUtil.getIpAddress(request), username, "success");

                return "redirect:" + callback;
            }
            log.info(LOGIN_MARKER, "envent:{},requestType:{},ip:{},username:{},{}", "login", "/login", WendaUtil.getIpAddress(request), username, "success");
            return "redirect:/";

        }
        log.info(LOGIN_MARKER, "envent:{},requestType:{},ip:{},username:{},{}", "login", "/login", WendaUtil.getIpAddress(request), username, "fail");
        return "login";
    }

    @RequestMapping(path = {"/register"}, method = {RequestMethod.POST})
    public String login(Model model,
                        @RequestParam("username") String username,
                        @RequestParam("password") String password,
                        HttpServletResponse response) {
        Map<String, String> map = userService.register(username, password);
        if (map.containsKey("msg")) {
            model.addAttribute("msg", map.get("msg"));
            return "login";
        }
        Cookie cookie = new Cookie("ticket", map.get("ticket"));
        cookie.setPath("/");
        response.addCookie(cookie);
        return "redirect:/";
    }

    @RequestMapping(path = {"/loginpage"}, method = {RequestMethod.GET})
    public String loginpage(Model model, @RequestParam(value = "callback", required = false) String callback) {
        model.addAttribute("callback", callback);
        return "login";
    }

    @RequestMapping(path = {"/logout"}, method = {RequestMethod.GET})
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/";
    }

    @RequestMapping(path = {"/reglogin"}, method = {RequestMethod.GET})
    public String relogin(@RequestParam(value = "next", required = true) String next) {
        return "login";
    }
}
