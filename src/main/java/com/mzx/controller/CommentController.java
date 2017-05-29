package com.mzx.controller;

import com.mzx.model.Comment;
import com.mzx.model.EntityType;
import com.mzx.model.HostHolder;
import com.mzx.model.Question;
import com.mzx.service.CommentService;
import com.mzx.service.QuestionService;
import com.mzx.service.SensitivewordsfilterServce;
import com.mzx.util.WendaUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;


/**
 * Created by mzx on 2017/5/24.
 */
@Controller
public class CommentController {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CommentController.class);

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
        logger.debug("id" + hostHolder.getUser().getId());
        comment.setUserId(hostHolder.getUser().getId());
        commentService.addComment(comment);
        int count = commentService.getCommentCount(questionId, EntityType.ENTITY_QUESTION);
        questionService.updateCommentCount(questionId, count);
        return "redirect:/question/" + questionId;
    }

}
