package com.github.sd4324530.webChat.controller;

import com.github.sd4324530.webChat.webSocket.WebSocketCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author peiyu
 */
@Controller
@RequestMapping(value = "chat")
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private WebSocketCache cache = WebSocketCache.me();

    @RequestMapping(method = RequestMethod.GET)
    public String chat(ModelMap map) {
        log.debug("进入聊天室页面......");
        map.put("msg", this.cache.getAll().size());
        return "chat";
    }

    /**
     * 轮询获取当前在线用户数
     * @return 在线用户数
     */
    @ResponseBody
    @RequestMapping(value = "/getSum", method = RequestMethod.GET)
    public String getSum() {
        int size = this.cache.getAll().size();
        return String.valueOf(size);
    }
}
