package com.arminzheng.im.client;

import com.arminzheng.im.client.command.ChatConsoleCommand;
import com.arminzheng.im.client.command.LoginConsoleCommand;
import com.arminzheng.im.client.handler.ChatMsgHandler;
import com.arminzheng.im.client.handler.ChatResponseHandler;
import com.arminzheng.im.client.handler.LoginResponseHandler;
import com.arminzheng.im.client.sender.ChatSender;
import com.arminzheng.im.client.sender.LoginSender;
import com.arminzheng.im.protocol.ProtobufDecoder;
import com.arminzheng.im.protocol.ProtobufEncoder;
import com.arminzheng.im.session.SessionUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class NettyClient {
    private static final int MAX_RETRY = 5;
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8000;

    public static void main(String[] args) {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new ProtobufDecoder());
                        ch.pipeline().addLast(new ProtobufEncoder());
                        ch.pipeline().addLast(new LoginResponseHandler());
                        ch.pipeline().addLast(new ChatResponseHandler());
                        ch.pipeline().addLast(new ChatMsgHandler());
                    }
                });

        connect(bootstrap, HOST, PORT, MAX_RETRY);
    }

    private static void connect(Bootstrap bootstrap, String host, int port, int retry) {
        bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println(new Date() + ": 连接成功，启动控制台线程……");
                Channel channel = ((ChannelFuture) future).channel();
                startConsoleThread(channel);
            } else if (retry == 0) {
                System.err.println("重试次数已用完，放弃连接！");
            } else {
                int order = (MAX_RETRY - retry) + 1;
                int delay = 1 << order;
                System.err.println(new Date() + ": 连接失败，第" + order + "次重连……");
                bootstrap.config().group().schedule(() -> connect(bootstrap, host, port, retry - 1), delay,
                        TimeUnit.SECONDS);
            }
        });
    }

    private static void startConsoleThread(Channel channel) {
        Scanner sc = new Scanner(System.in);
        LoginConsoleCommand loginConsoleCommand = new LoginConsoleCommand(new LoginSender(), channel);
        ChatConsoleCommand chatConsoleCommand = new ChatConsoleCommand(new ChatSender(), channel);

        new Thread(() -> {
            while (!Thread.interrupted()) {
                if (!SessionUtil.hasLogin(channel)) {
                    loginConsoleCommand.exec(sc);
                    waitForLogin(channel);
                } else {
                    chatConsoleCommand.exec(sc);
                }
            }
        }, "im-console").start();
    }

    private static void waitForLogin(Channel channel) {
        int maxWait = 50;
        while (!SessionUtil.hasLogin(channel) && maxWait-- > 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        if (!SessionUtil.hasLogin(channel)) {
            System.out.println("登录未完成，请重试");
        }
    }
}
