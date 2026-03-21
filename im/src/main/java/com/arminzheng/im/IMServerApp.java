package com.arminzheng.im;

import com.arminzheng.im.protocol.ProtobufDecoder;
import com.arminzheng.im.protocol.ProtobufEncoder;
import com.arminzheng.im.server.handler.ChatRedirectHandler;
import com.arminzheng.im.server.handler.UserLoginHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Date;

@SpringBootApplication(scanBasePackages = "com.arminzheng.im.server")
public class IMServerApp {

    private static final int PORT = 8000;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(IMServerApp.class, args);
        UserLoginHandler userLoginHandler = context.getBean(UserLoginHandler.class);
        ChatRedirectHandler chatRedirectHandler = context.getBean(ChatRedirectHandler.class);

        NioEventLoopGroup boosGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        final ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(boosGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline().addLast(new ProtobufDecoder());
                        ch.pipeline().addLast(new ProtobufEncoder());
                        ch.pipeline().addLast(userLoginHandler);
                        ch.pipeline().addLast(chatRedirectHandler);
                    }
                });

        bind(serverBootstrap, PORT);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            context.close();
        }));
    }

    private static void bind(final ServerBootstrap serverBootstrap, final int port) {
        serverBootstrap.bind(port).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println(new Date() + ": 端口[" + port + "]绑定成功!");
            } else {
                System.err.println("端口[" + port + "]绑定失败!");
            }
        });
    }
}
