package com.mzx.wenda.controller;

import com.mzx.wenda.async.EventModel;
import com.mzx.wenda.async.EventProducer;
import com.mzx.wenda.async.EventType;
import com.mzx.wenda.model.*;
import com.mzx.wenda.service.CommentService;
import com.mzx.wenda.service.FollowService;
import com.mzx.wenda.service.QuestionService;
import com.mzx.wenda.service.UserService;
import com.mzx.wenda.util.WendaUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by mzx on 2017/5/31.
 */
@Slf4j
@Controller
public class FollowController {


    @Autowired
    FollowService followService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;


    @Autowired
    CommentService commentService;

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
        eventProducer.produce(new EventModel().setActorId(user.getId())
                .setEntityId(entityId)
                .setEntityOwnerId(questionService.getQuestion(entityId).getUserId())
                .setType(EventType.FOLLOW).setEntityType(EntityType.ENTITY_QUESTION));

        String refer = request.getHeader("Referer");
        return "redirect:" + refer;
    }

    @ResponseBody
    @RequestMapping(path = {"/followQuestion"}, method = {RequestMethod.POST})
    public String follow(@RequestParam("questionId") int questionId) {

        if (hostHolder.getUser() == null) {
            return WendaUtil.getJsonString(999);
        }

        Question q = questionService.getQuestion(questionId);
        if (q == null) {
            return WendaUtil.getJsonString(1, "问题不存在");
        }

        boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);

//        eventProducer.produce(new EventModel(EventType.FOLLOW)
//                .setActorId(hostHolder.getUser().getId()).setEntityId(questionId)
//                .setEntityType(EntityType.ENTITY_QUESTION).setEntityOwnerId(q.getUserId()));

        Map<String, Object> info = new HashMap<>();
        info.put("headUrl", hostHolder.getUser().getHeadUrl());
        info.put("name", hostHolder.getUser().getName());
        info.put("id", hostHolder.getUser().getId());
        info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));
        return WendaUtil.getJSONString(ret ? 0 : 1, info);
    }


    @ResponseBody
    @RequestMapping(path = {"/unfollowQuestion"}, method = {RequestMethod.POST})
    public String unfollow(@RequestParam("questionId") int questionId) {

        if (hostHolder.getUser() == null) {
            return WendaUtil.getJsonString(999);
        }

        Question q = questionService.getQuestion(questionId);
        if (q == null) {
            return WendaUtil.getJsonString(1, "问题不存在");
        }

        boolean ret = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);

//        eventProducer.produce(new EventModel(EventType.FOLLOW)
//                .setActorId(hostHolder.getUser().getId()).setEntityId(questionId)
//                .setEntityType(EntityType.ENTITY_QUESTION).setEntityOwnerId(q.getUserId()));

        Map<String, Object> info = new HashMap<>();
        info.put("id", hostHolder.getUser().getId());
        info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));
        return WendaUtil.getJSONString(ret ? 0 : 1, info);
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


    @RequestMapping(path = {"/user/{uid}/followees"}, method = {RequestMethod.GET})
    public String followee(@PathVariable("uid") int userId, Model model, @RequestParam(value = "offset", required = false) Integer offset) {
        List<Integer> followeeIds;

        log.info("offset" + offset);
        if (offset != null) {
            followeeIds = followService.getFollowee(EntityType.USER, userId, offset, 10);

        } else {
            followeeIds = followService.getFollowee(EntityType.USER, userId, 10);
        }

        if (hostHolder.getUser() != null) {
            model.addAttribute("followees", getUsersInfo(hostHolder.getUser().getId(), followeeIds));
        } else {
            model.addAttribute("followees", getUsersInfo(0, followeeIds));
        }
        model.addAttribute("followeeCount", followService.getFolloweeCount(userId, EntityType.USER));
        model.addAttribute("curUser", userService.getUserById(userId));
        model.addAttribute("uid", userId);
        if (offset != null) {
            if (followeeIds.size() < 10) {
                model.addAttribute("next", offset);
            } else {
                model.addAttribute("next", offset + 10);

            }
        } else {
            model.addAttribute("next", 10);
        }
        return "followees";
    }


    @RequestMapping(path = {"/user/{uid}/followers"}, method = {RequestMethod.GET})
    public String follower(@PathVariable("uid") int userId, Model model) {
        List<Integer> followeeIds = followService.getFollower(EntityType.USER, userId, 10);
        if (hostHolder.getUser() != null) {
            model.addAttribute("followers", getUsersInfo(hostHolder.getUser().getId(), followeeIds));
        } else {
            model.addAttribute("followers", getUsersInfo(0, followeeIds));
        }
        model.addAttribute("followerCount", followService.getFollowerCount(EntityType.USER, userId));
        model.addAttribute("curUser", userService.getUserById(userId));


        return "followers";
    }

    private List<ViewObject> getUsersInfo(int localUserId, List<Integer> userIds) {
        List<ViewObject> userInfos = new ArrayList<ViewObject>();
        for (Integer uid : userIds) {
            User user = userService.getUserById(uid);
            if (user == null) {
                continue;
            }
            ViewObject vo = new ViewObject();
            vo.set("user", user);
            //   vo.set("commentCount", commentService.getUserCommentCount(uid));
            vo.set("followerCount", followService.getFollowerCount(EntityType.USER, uid));
            vo.set("followeeCount", followService.getFolloweeCount(uid, EntityType.USER));
            vo.set("commentCount", commentService.getCommentCount(user.getId()));
            if (localUserId != 0) {
                vo.set("followed", followService.isFollower(localUserId, EntityType.USER, uid));
            } else {
                vo.set("followed", false);
            }
            userInfos.add(vo);
        }
        return userInfos;
    }


}
