package com.mzx;

import com.mzx.dao.LoginTicketDAO;
import com.mzx.dao.QuestionDAO;
import com.mzx.dao.UserDAO;
import com.mzx.model.LoginTicket;
import com.mzx.model.Question;
import com.mzx.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * Created by mzx on 4/30/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WendaApplication.class)
@WebAppConfiguration
@Sql("/init-schema.sql")
public class DbTest {
    @Autowired
    UserDAO userDAO;
    @Autowired
    QuestionDAO questionDAO;
    @Autowired
    LoginTicketDAO loginTicketDAO;

    @Test
    public void dbTest() {

        for (int i = 0; i < 10; i++) {

            User user = new User();
            user.setName("user" + i);
            user.setHeadUrl("https://images.nowcoder.com/images/20170427/5053505_1493259924805_994987825265532932EF8585A3068669@0e_100w_100h_0c_1i_1o_90Q_1x");
            user.setPassword("password" + i);
            user.setSalt("salt");
            userDAO.addUser(user);
        }


        for (int i = 0; i < 10; i++) {
            Question question = new Question();
            question.setCommentCount(i);
            question.setContent("content" + i);
            question.setCreatedDate(new Date());
            question.setTitle("title" + i);
            question.setUserId(i);
            questionDAO.addQuestion(question);
        }
        LoginTicket ticket = new LoginTicket();
        ticket.setExpired(new Date());
        ticket.setStatus(0);
        ticket.setUserId(1);
        String tickets = UUID.randomUUID().toString().replaceAll("-", "");
        ticket.setTicket(tickets);

        loginTicketDAO.insertTicket(ticket);
        LoginTicket tickettest=loginTicketDAO.selectTicketByTicket(tickets);
        loginTicketDAO.upDateTicket(1, tickettest.getTicket());


    }
}
