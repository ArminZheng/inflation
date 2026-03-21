package com.arminzheng.im.client.command;

import com.arminzheng.im.client.sender.ChatSender;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Slf4j
@Component
public class ChatConsoleCommand implements ConsoleCommand {

    private final ChatSender chatSender;
    private final Channel channel;

    public ChatConsoleCommand(ChatSender chatSender, Channel channel) {
        this.chatSender = chatSender;
        this.channel = channel;
    }

    @Override
    public void exec(Scanner scanner) {
        log.info("请输入聊天信息(toUserId:content):");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return;
        }
        String[] chatInfo = input.split(":", 2);
        if (chatInfo.length != 2) {
            log.warn("输入格式错误，请重新输入");
            return;
        }
        String toUserId = chatInfo[0];
        String content = chatInfo[1];
        chatSender.sendChatRequest(channel, toUserId, content);
    }
}
