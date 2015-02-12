package com.github.sd4324530.webChat.config;

import com.github.sd4324530.webChat.webSocket.EchoHandler;
import com.github.sd4324530.webChat.webSocket.WebSocketHandshakeInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.PerConnectionWebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

/**
 * @author peiyu
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(echoWebSocketHandler(), "/echo"); //提供符合W3C标准的Websocket数据
        registry.addHandler(snakeWebSocketHandler(), "/snake");
        registry.addHandler(snakeWebSocketHandler(), "/websocket");

        registry.addHandler(echoWebSocketHandler(), "/sockjs/echo").addInterceptors(handshakeInterceptor()).withSockJS();//提供符合SockJS的数据
        registry.addHandler(snakeWebSocketHandler(), "/sockjs/snake").addInterceptors(handshakeInterceptor()).withSockJS();
        registry.addHandler(snakeWebSocketHandler(), "/sockjs/websocket").addInterceptors(handshakeInterceptor()).withSockJS();
    }

    @Bean
    public WebSocketHandler echoWebSocketHandler() {
        return new EchoHandler();
    }

    @Bean
    public WebSocketHandler snakeWebSocketHandler() {
        return new PerConnectionWebSocketHandler(EchoHandler.class);
    }

    @Bean
    public HandshakeInterceptor handshakeInterceptor() {
        return new WebSocketHandshakeInterceptor();
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
    }
}
