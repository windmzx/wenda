package com.mzx.controller;

import com.mzx.model.EntityType;
import com.mzx.model.HostHolder;
import com.mzx.model.User;
import com.mzx.service.LikeService;
import com.mzx.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by mzx on 2017/5/28.
 */

@Controller
public class LikeController {

    @Autowired
    HostHolder hostHolder;

    @Autowired
    LikeService likeService;

    @ResponseBody
    @RequestMapping(path = {"/like"}, method = {RequestMethod.GET})
    public String like(@RequestParam("commentId") int commentId) {
        User user = hostHolder.getUser();
        if (user == null) {
            return WendaUtil.getJsonString(999);
        }

        long like = likeService.like(user.getId(), EntityType.ENTITY_COMMENT, commentId);
        return WendaUtil.getJsonString(0, String.valueOf(like));
    }

    @ResponseBody
    @RequestMapping(path = {"/dislike"}, method = {RequestMethod.GET})
    public String dislike(@RequestParam("commentId") int commentId) {
        User user = hostHolder.getUser();
        if (user == null) {
            return WendaUtil.getJsonString(999);
        }

        long like = likeService.dislike(user.getId(), EntityType.ENTITY_COMMENT, commentId);
        return WendaUtil.getJsonString(0, String.valueOf(like));
    }


}
