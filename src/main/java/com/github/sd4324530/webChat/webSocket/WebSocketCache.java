package com.github.sd4324530.webChat.webSocket;

import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户信息缓存
 * @author peiyu
 */
public class WebSocketCache {

    private Map<String, Object[]> cacheMap;

    private static WebSocketCache me;

    private WebSocketCache() {
        this.cacheMap = new HashMap<>();
    }

    public static WebSocketCache me() {
        if(null == me) {
            me = new WebSocketCache();
        }
        return me;
    }

    public void addCache(String userName, WebSocketSession session) {
        if(!this.cacheMap.containsKey(session.getId())) {
            Object[] objects = new Object[]{userName, session};
            this.cacheMap.put(session.getId(), objects);
        }
    }

    public String getUserName(String id) {
        final String[] name = {null};
        this.cacheMap.forEach((key, value) -> {
            if(key.equals(id)) {
                name[0] = value[0].toString();
            }
        });
        return name[0];
    }

    public List<WebSocketSession> getAll() {
        return this.cacheMap.values().stream().map(obj -> (WebSocketSession)obj[1]).collect(Collectors.toList());
    }

    public void deleteCache(String id) {
        this.cacheMap.remove(id);
    }
}
