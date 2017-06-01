package com.mzx.controller;

import com.mzx.model.EntityType;
import com.mzx.model.HostHolder;
import com.mzx.model.User;
import com.mzx.service.FollowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;


/**
 * Created by mzx on 2017/5/31.
 */
@Controller
public class FollowController {
    public static final Logger logger = LoggerFactory.getLogger(FollowController.class);


    @Autowired
    FollowService followService;

    @Autowired
    HostHolder hostHolder;


    @RequestMapping(path = {"/follow/{entityType}/{entityId}"}, method = {RequestMethod.GET})
    public String follow(@PathVariable("entityType") String entityType,
                         @PathVariable("entityId") int entityId, HttpServletRequest request) {

        User user = hostHolder.getUser();
        int type = 0;
        if (entityType.equals("question"))
            type = EntityType.ENTITY_QUESTION;
        if (entityType.equals("user"))
            type = EntityType.USER;
        followService.follow(user.getId(), type, entityId);

        String refer = request.getHeader("Referer");
        return "redirect:" + refer;
    }

    @RequestMapping(path = {"/unfollow/{entityType}/{entityId}"}, method = {RequestMethod.GET})
    public String unfollow(@PathVariable("entityType") String entityType,
                           @PathVariable("entityId") int entityId,
                           HttpServletRequest request) {

        User user = hostHolder.getUser();
        int type = 0;
        if (entityType.equals("question"))
            type = EntityType.ENTITY_QUESTION;
        if (entityType.equals("user"))
            type = EntityType.USER;
        followService.unfollow(user.getId(), type, entityId);
        String refer = request.getHeader("Referer");
        return "redirect:" + refer;
    }
}
