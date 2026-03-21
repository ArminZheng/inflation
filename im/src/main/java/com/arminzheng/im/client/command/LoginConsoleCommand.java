package com.arminzheng.im.client.command;

import com.arminzheng.im.client.sender.LoginSender;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Slf4j
@Component
public class LoginConsoleCommand implements ConsoleCommand {

    private final LoginSender loginSender;
    private final Channel channel;

    public LoginConsoleCommand(LoginSender loginSender, Channel channel) {
        this.loginSender = loginSender;
        this.channel = channel;
    }

    @Override
    public void exec(Scanner scanner) {
        log.info("请输入用户信息(id:password):");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return;
        }
        String[] userInfo = input.split(":");
        if (userInfo.length != 2) {
            log.warn("输入格式错误，请重新输入");
            return;
        }
        String userId = userInfo[0];
        String password = userInfo[1];
        loginSender.sendLoginRequest(channel, userId, password);
    }
}
