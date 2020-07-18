package com.mzx.wenda.controller;

import com.mzx.wenda.model.Comment;
import com.mzx.wenda.model.EntityType;
import com.mzx.wenda.model.HostHolder;
import com.mzx.wenda.service.CommentService;
import com.mzx.wenda.service.QuestionService;
import com.mzx.wenda.service.SensitivewordsfilterServce;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;


/**
 * Created by mzx on 2017/5/24.
 */
@Slf4j
@Controller
public class CommentController {

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;

    @Autowired
    SensitivewordsfilterServce sensitivewordsfilterServce;

    @Autowired
    QuestionService questionService;

    @RequestMapping(path = {"/addcomment"}, method = {RequestMethod.POST})
    public String addComment(@RequestParam("questionId") int questionId, @RequestParam("content") String content) {

        content = sensitivewordsfilterServce.filter(content);

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setEntityType(EntityType.ENTITY_QUESTION);
        comment.setCreatedDate(new Date());
        comment.setStatus(1);
        comment.setEntityId(questionId);
        log.debug("id" + hostHolder.getUser().getId());
        comment.setUserId(hostHolder.getUser().getId());
        commentService.addComment(comment);
        int count = commentService.getCommentCount(questionId, EntityType.ENTITY_QUESTION);
        questionService.updateCommentCount(questionId, count);
        return "redirect:/question/" + questionId;
    }

}
