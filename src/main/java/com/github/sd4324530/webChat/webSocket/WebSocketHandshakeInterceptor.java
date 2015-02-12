package com.github.sd4324530.webChat.webSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author peiyu
 */
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger log = LoggerFactory.getLogger(WebSocketHandshakeInterceptor.class);

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        log.debug("beforeHandshake..............");
        HttpSession session = getSession(request);
        if (session != null) {
            log.debug("session:{}", session.toString());
            Enumeration<String> names = session.getAttributeNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                Object value = session.getAttribute(name);
                log.debug("name:{}, value:{}", name, value);
                attributes.put(name, session.getAttribute(name));
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }

    private HttpSession getSession(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
            HttpServletRequest servletRequest = serverRequest.getServletRequest();
            String requestURI = servletRequest.getRequestURI();
            log.debug("requestURL;{}", requestURI);
            Enumeration<String> parameterNames = servletRequest.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String name = parameterNames.nextElement();
                Object value = servletRequest.getAttribute(name);
                log.debug("parameterName:{}, parameterName:{}", name, value);
            }
//            Enumeration<String> names = servletRequest.getAttributeNames();
//            while (names.hasMoreElements()) {
//                String name = names.nextElement();
//                Object value = servletRequest.getAttribute(name);
//                log.debug("name:{}, value:{}", name, value);
//            }
            return servletRequest.getSession(false);
        }
        return null;
    }
}
