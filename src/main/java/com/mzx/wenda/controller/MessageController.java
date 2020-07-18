package com.mzx.wenda.controller;

import com.mzx.wenda.model.HostHolder;
import com.mzx.wenda.model.Message;
import com.mzx.wenda.model.User;
import com.mzx.wenda.model.ViewObject;
import com.mzx.wenda.service.MessageService;
import com.mzx.wenda.service.SensitivewordsfilterServce;
import com.mzx.wenda.service.UserService;
import com.mzx.wenda.util.WendaUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mzx on 2017/5/26.
 */
@Slf4j
@Controller
public class MessageController {

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    MessageService messageService;

    @Autowired
    SensitivewordsfilterServce sensitivewordsfilterServce;



    @ResponseBody
    @RequestMapping(path = {"/msg/addMessage"}, method = {RequestMethod.POST})
    public String addMessage(@RequestParam("toName") String toName, @RequestParam("content") String content) {
        User user = userService.getUserByName(toName);

        if (user == null) {
            return WendaUtil.getJsonString(1, "用户不存在");
        }

        Message message = new Message();
        message.setContent(sensitivewordsfilterServce.filter(content));
        message.setCreatedDate(new Date());
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(user.getId());
        message.setHasRead(0);

        messageService.addMessage(message);
        return WendaUtil.getJsonString(0);
    }

    @RequestMapping(path = {"/msg/list"}, method = {RequestMethod.GET})
    public String getMsgList(Model model) {
        User user = hostHolder.getUser();
        List<Message> conversationList = messageService.getConversationList(user.getId(), 0, 10);
        List<ViewObject> vos = new ArrayList<>();
        for (Message message : conversationList) {
            messageService.upDateUnreadStatus(message.getId());
            int unread = messageService.getConvesationUnreadCount(hostHolder.getUser().getId(), message.getConversationId());
            log.info("未读消息有" + unread + "条");
            ViewObject viewObject = new ViewObject();
            viewObject.set("message", message);
            viewObject.set("user", userService.getUserById(message.getFromId()));
            viewObject.set("unread", unread);
            vos.add(viewObject);
        }
        model.addAttribute("conlist", vos);
        return "letter";
    }

    @RequestMapping(path = {"/msg/detail"}, method = {RequestMethod.GET})
    public String messageDetail(Model model, @RequestParam("converstaionid") String conserstaionId) {
        List<Message> messageList = messageService.getConversationDetail(conserstaionId, 0, 10);
        log.info("使用" + conserstaionId + "查出" + messageList.size() + "记录");


        List<ViewObject> vos = new ArrayList<>();
        ViewObject vo;
        for (Message message : messageList) {
            vo = new ViewObject();
            vo.set("message", message);
            vo.set("user", userService.getUserById(message.getFromId()));
            vos.add(vo);
        }
        model.addAttribute("messages", vos);
        return "letterDetail";
    }
}
