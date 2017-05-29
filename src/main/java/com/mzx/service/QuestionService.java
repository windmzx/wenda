package com.mzx.service;

import com.mzx.dao.QuestionDAO;
import com.mzx.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * Created by mzx on 4/30/2017.
 */
@Service
public class QuestionService {
    @Autowired
    QuestionDAO questionDAO;

    public List<Question> getLastQuestion(int userId, int offset, int limit) {
        return questionDAO.selectLatestQuestions(userId, offset, limit);
    }

    public int addQuestion(Question question) {
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        return questionDAO.addQuestion(question);
    }

    public Question getQuestion(int id) {
        return questionDAO.getQuestion(id);
    }


    public int updateCommentCount(int qid, int count) {
        return questionDAO.updateCommentCount(qid, count);
    }
}
