package com.mzx.dao;

import com.mzx.model.LoginTicket;
import org.apache.ibatis.annotations.*;

/**
 * Created by mzx on 5/5/2017.
 */
@Mapper
public interface LoginTicketDAO {
    String TABLE_NAME = " login_ticket ";
    String INSERT_FIELDS = " user_id, ticket, expired ,status";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({" INSERT INTO ", TABLE_NAME, " ( ", INSERT_FIELDS, " ) VALUES ( #{userId}, #{ticket},#{expired},#{status})"})
    int insertTicket(LoginTicket ticket);


    @Update({" UPDATE ", TABLE_NAME, " SET status=#{status} where ticket=#{ticket}"})
    int upDateTicket(@Param("status") int status, @Param("ticket") String ticket);


    @Select({" SELECT ", SELECT_FIELDS, " FROM ", TABLE_NAME, " where ticket=#{ticket} and status=1"})
    LoginTicket selectTicketByTicket(@Param("ticket")String ticket);
}
