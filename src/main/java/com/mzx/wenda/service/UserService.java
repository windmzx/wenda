package com.mzx.wenda.service;

import com.mzx.wenda.dao.LoginTicketDAO;
import com.mzx.wenda.dao.UserDAO;
import com.mzx.wenda.model.LoginTicket;
import com.mzx.wenda.model.User;
import com.mzx.wenda.util.WendaUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by mzx on 4/30/2017.
 */
@Service
public class UserService {
    @Autowired
    UserDAO userDAO;

    @Autowired
    LoginTicketDAO loginTicketDAO;

    public User getUserById(int id) {
        return userDAO.selectById(id);
    }

    public User getUserByName(String name) {
        return userDAO.selectByName(name);
    }

    public void logout(String ticket) {
        loginTicketDAO.upDateTicket(0, ticket);
    }

    public Map<String, String> register(String username, String userPassword) {
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isBlank(username) || StringUtils.isBlank(userPassword)) {
            map.put("msg", "用户名密码不能为空");
            return map;
        }

        User user = userDAO.selectByName(username);
        if (user != null) {
            map.put("msg", "用户名已经被注册");
            return map;
        }
        Random random = new Random();
        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0, 5));
        user.setPassword(WendaUtil.MD5(userPassword + user.getSalt()));
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
        userDAO.addUser(user);
        String ticket = addTicket(user.getId());
        map.put("ticket", ticket);
        return map;
    }

    public Map<String, String> login(String username, String password) {
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            map.put("msg", "用户名密码不能为空");
            return map;
        }
        User user = userDAO.selectByName(username);
        if (user != null && password.equals(user.getPassword())) {
            password = WendaUtil.MD5(password + user.getSalt());
            //密码符合成功登陆
            String ticket = addTicket(user.getId());
            //TODO 使之前的ticket失效
            map.put("ticket", ticket);
        } else {
            map.put("msg", "用户名密码不正确");
        }
        return map;
    }


    public String addTicket(int userId) {
        LoginTicket ticket = new LoginTicket();
        ticket.setStatus(1);
        ticket.setUserId(userId);
        Date date = new Date();
        date.setTime(1000 * 3600 * 24 * 7 + date.getTime());
        ticket.setExpired(date);
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        loginTicketDAO.insertTicket(ticket);
        return ticket.getTicket();
    }
}
