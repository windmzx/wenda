package com.mzx.controller;

import com.mzx.model.*;
import com.mzx.service.*;
import com.mzx.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mengz on 2017/5/20.
 */
@Controller
public class QuestionController {
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    QuestionService questionService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    SensitivewordsfilterServce sensitivewordsfilterServce;

    @Autowired
    CommentService commentService;

    @Autowired
    UserService userService;

    @Autowired
    LikeService likeService;

    @ResponseBody
    @RequestMapping(path = {"/question/add"}, method = {RequestMethod.POST})
    public String addQuestion(@RequestParam("title") String title, @RequestParam("content") String content) {
        Question question = new Question();
        question.setCommentCount(0);

        question.setContent(sensitivewordsfilterServce.filter(content));
        question.setTitle(title);
        question.setUserId(hostHolder.getUser().getId());
        question.setCreatedDate(new Date());
        try {
            questionService.addQuestion(question);

            return WendaUtil.getJsonString(0);
        } catch (Exception e) {
            logger.error("添加提问失败" + e.getMessage());
            return WendaUtil.getJsonString(1, "提问失败");
        }
    }

    @RequestMapping(path = {"/question/{qid}"}, method = {RequestMethod.GET})
    public String getquestion(@PathVariable("qid") int qid, Model model) {
        Question question = questionService.getQuestion(qid);
        if (question != null) {
            List<Comment> commentList = commentService.getCommentsByEntity(qid, EntityType.ENTITY_QUESTION);
            List<ViewObject> vos = new ArrayList<>();
            for (Comment comment : commentList) {
                ViewObject vo = new ViewObject();
                User user = hostHolder.getUser();
                if (user == null) {
                    vo.set("liked", 0);
                } else {
                    vo.set("liked", likeService.getLikeStatus(user.getId(), EntityType.ENTITY_COMMENT, qid));
                }

                vo.set("comment", comment);
                vo.set("user", userService.getUserById(comment.getUserId()));
                vo.set("likeCount", likeService.getLikeCount(EntityType.ENTITY_COMMENT, qid));
                vos.add(vo);
            }

            model.addAttribute("comments", vos);
            model.addAttribute("question", question);
            return "detail";
        } else {
            return "redirect:/";
        }

    }
}
