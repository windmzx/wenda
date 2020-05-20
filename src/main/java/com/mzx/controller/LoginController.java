package com.mzx.controller;


import com.mzx.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by mzx on 5/1/2017.
 */
@Controller
public class LoginController {
    @Autowired
    UserService userService;

    @RequestMapping(path = {"/login"}, method = {RequestMethod.POST})
    public String register(Model model,
                           @RequestParam("username") String username,
                           @RequestParam("password") String password,
                           @RequestParam(value = "next", required = false) String callback,
                           HttpServletResponse response) {
        Map<String, String> map = userService.login(username, password);
        if (map.containsKey("msg")) {
            model.addAttribute("msg", map.get("msg"));
            return "login";
        }
        if (map.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket", map.get("ticket"));
            cookie.setPath("/");
            cookie.setMaxAge(3600 * 24 * 7);
            response.addCookie(cookie);
            if (StringUtils.isNotBlank(callback)) {
                return "redirect:" + callback;
            }
            return "redirect:/";
        }
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
