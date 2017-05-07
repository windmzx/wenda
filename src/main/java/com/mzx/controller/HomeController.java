package com.mzx.controller;

import com.mzx.model.Question;
import com.mzx.model.User;
import com.mzx.model.ViewObject;
import com.mzx.service.QuestionService;
import com.mzx.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mzx on 4/30/2017.
 */
@Controller
public class HomeController {

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET})
    String index(Model model) {

        model.addAttribute("vos", getQuestions(0, 0, 10));
        return "index";
    }

    @RequestMapping(path = {"/user/{userId}"}, method = {RequestMethod.GET})
    String userIndex(Model model, @PathVariable("userId") int userId) {
        model.addAttribute("vos", getQuestions(userId, 0, 10));
        return "index";
    }


    private List<ViewObject> getQuestions(int userId, int offset, int limit) {
        List<Question> list = questionService.getLastQuestion(userId, offset, limit);
        List<ViewObject> vos = new ArrayList<>();
        for (Question question : list) {
            ViewObject viewObject = new ViewObject();
            viewObject.set("question", question);
            User user = userService.getUserById(question.getUserId());
            viewObject.set("user", user);
            vos.add(viewObject);
        }
        return vos;
    }
}
