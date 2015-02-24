package com.github.sd4324530.webChat.webSocket;

import com.github.sd4324530.webChat.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

/**
 * websocket消息处理器类
 * @author peiyu
 */
public class EchoHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(EchoHandler.class);

    //登录者信息缓存，主要用于把session id和昵称关联起来，用于发送消息
    private WebSocketCache cache = WebSocketCache.me();

    //记录每个session id发送过快的时间，防止用户恶意刷屏
    private Map<String, Date> timeLimitMap = new Hashtable<>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String msg = message.getPayload().trim();
        log.debug("你说的内容:{}", msg);
        String responseMessage = null;
        //用于限制用户发送信息的频率，防止恶意刷屏
        Date timeLimit = DateUtils.addSeconds(new Date(), -2);
        if (null != msg && !"".equals(msg.trim())) {
            try {
                //暂时只能用这个蛋疼的方式拿到登录者的昵称- -
                if (msg.startsWith("|")) {
                    String username = msg.substring(1);
                    if (username.length() > 10) {
                        session.close(CloseStatus.BAD_DATA);
                        return;
                    }
                    long count = this.cache.getAll().stream().filter(s -> s.getId().equals(session.getId())).count();
                    if (count > 0) {
                        //防止用户发送过长的消息，前端已经限制，但是用户可以绕过js检查，所以在后端再检查一次
                        if (msg.length() > 200) {
                            session.sendMessage(new TextMessage("太长，刷屏是不对的（这条只有你能看到，嘿嘿）"));
                            return;
                        }
                        boolean before = timeLimit.before(this.timeLimitMap.get(session.getId()));
                        log.debug("是否过快{}", before);
                        if (before) {
                            session.sendMessage(new TextMessage("说的太频繁了！刷屏是不对的！"));
                            return;
                        }
                        String name = this.cache.getUserName(session.getId());
                        responseMessage = name + "说：" + msg;
                        this.timeLimitMap.put(session.getId(), new Date());
                    } else {
                        log.debug("{}上线!`sessionid:{}", username, session.getId());
                        this.cache.addCache(username, session);
                        this.timeLimitMap.put(session.getId(), new Date(0));
                        responseMessage = username + "上线啦!";
                    }
                } else {
                    if (msg.length() > 200) {
                        session.sendMessage(new TextMessage("太长，刷屏是不对的（这条只有你能看到，嘿嘿）"));
                        return;
                    }
                    boolean before = timeLimit.before(this.timeLimitMap.get(session.getId()));
                    log.debug("是否过快{}", before);
                    if (before) {
                        session.sendMessage(new TextMessage("说的太频繁了！刷屏是不对的！"));
                        return;
                    }
                    String name = this.cache.getUserName(session.getId());
                    responseMessage = name + "说：" + msg;
                    this.timeLimitMap.put(session.getId(), new Date());
//                    responseMessage = this.simsimiService.send(msg);
                }
            } catch (Exception e) {
                log.error("发送异常:", e);
            }
        } else {
        }
        if (null != responseMessage && !"".equals(responseMessage.trim())) {
            log.debug("回复内容:{}", responseMessage);
            String now = DateUtils.date2String(new Date());
            responseMessage = now + " " + responseMessage;
            TextMessage textMessage = new TextMessage(responseMessage);
            this.cache.getAll().forEach(s -> {
                try {
                    log.debug("发送消息，sessionid:{}", s.getId());
                    if (s.isOpen()) {
                        s.sendMessage(textMessage);
                    }
                } catch (Exception e) {
                    log.error("发送异常", e);
                }
            });
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.debug("建立链接");
        //在这里发现了一个从session中获取额外参数的方法，但是在前端还没找到方法可以将数据放进这个里面，如果可以将昵称放进去，那么就不用那么蛋疼的记录登录者的昵称了
        Map<String, Object> attributes = session.getAttributes();
        if (null == attributes || attributes.isEmpty()) {
            log.debug("没有参数....");
        } else {
            attributes.forEach((key, value) -> log.debug("key:{}, value:{}", key, value));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.debug("关闭链接");
        String name = this.cache.getUserName(session.getId());
        this.cache.deleteCache(session.getId());
        this.timeLimitMap.remove(session.getId());
        if (null != name && !"".equals(name)) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(name + "下线啦!"));
            }
            this.cache.getAll().forEach(s -> {
                try {
                    String now = DateUtils.date2String(new Date());
                    s.sendMessage(new TextMessage(now + " " + name + "下线啦!"));
                } catch (IOException e) {
                    log.error("异常", e);
                }
            });
        }
    }


    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        log.debug("handleBinaryMessage:{}", message.toString());
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        log.debug("handlePongMessage:{}", message.toString());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("handleTransportError:", exception);
        String name = this.cache.getUserName(session.getId());
        this.cache.deleteCache(session.getId());
        this.timeLimitMap.remove(session.getId());
        if(session.isOpen()) {
            session.close();
        }
        if (null != name && !"".equals(name)) {
//            if (session.isOpen()) {
//                session.sendMessage(new TextMessage(name + "下线啦!"));
//            }
            this.cache.getAll().forEach(s -> {
                try {
                    String now = DateUtils.date2String(new Date());
                    s.sendMessage(new TextMessage(now + " " + name + "下线啦!"));
                } catch (IOException e) {
                    log.error("异常", e);
                }
            });
        }
    }
}
