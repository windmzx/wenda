package com.mzx.controller;

import com.mzx.async.EventModel;
import com.mzx.async.EventProducer;
import com.mzx.async.EventType;
import com.mzx.model.*;
import com.mzx.service.CommentService;
import com.mzx.service.LikeService;
import com.mzx.service.MessageService;
import com.mzx.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * Created by mzx on 2017/5/28.
 */

@Controller
public class LikeController {

    @Autowired
    HostHolder hostHolder;

    @Autowired
    LikeService likeService;

    @Autowired
    MessageService messageService;

    @Autowired
    CommentService commentService;

    @Autowired
    EventProducer eventProducer;

    @ResponseBody
    @RequestMapping(path = {"/like"}, method = {RequestMethod.POST})
    public String like(@RequestParam("commentId") int commentId) {
        User user = hostHolder.getUser();
        if (user == null) {
            return WendaUtil.getJsonString(999);
        }

        long like = likeService.like(user.getId(), EntityType.ENTITY_COMMENT, commentId);

        Comment comment = commentService.getCommentById(commentId);

        EventModel eventModel = new EventModel();
        eventModel.setActorId(user.getId());
        eventModel.setEntityId(commentId);
        eventModel.setEntityType(EntityType.ENTITY_COMMENT);
        eventModel.setType(EventType.LIKE);
        eventModel.setEntityOwnerId(comment.getUserId());
        eventProducer.produce(eventModel);
        Message message = new Message();
        message.setHasRead(0);
        message.setToId(hostHolder.getUser().getId());
        message.setFromId(hostHolder.getUser().getId());
        message.setContent("用户赞了你的评论");
        message.setCreatedDate(new Date());
        messageService.addMessage(message);
        return WendaUtil.getJsonString(0, String.valueOf(like));
    }

    @ResponseBody
    @RequestMapping(path = {"/dislike"}, method = {RequestMethod.POST})
    public String dislike(@RequestParam("commentId") int commentId) {
        User user = hostHolder.getUser();
        if (user == null) {
            return WendaUtil.getJsonString(999);
        }

        likeService.dislike(user.getId(), EntityType.ENTITY_COMMENT, commentId);
        long like = likeService.getLikeCount(EntityType.ENTITY_COMMENT, commentId);
        return WendaUtil.getJsonString(0, String.valueOf(like));
    }


}
