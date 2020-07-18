package com.mzx.wenda;

import com.mzx.wenda.model.EntityType;
import com.mzx.wenda.service.FollowService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Created by mzx on 2017/6/4.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WendaApplication.class)
@WebAppConfiguration
public class FollowTest {

    @Autowired
    FollowService followService;

    @Test
    public void test() {
        for (int i = 1; i < 12; i++) {

            followService.follow(11, EntityType.USER, i);
        }
    }

}
