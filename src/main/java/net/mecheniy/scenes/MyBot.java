package net.mecheniy.scenes;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import io.github.cdimascio.dotenv.Dotenv;

public class MyBot extends TelegramLongPollingBot {
    private final String username;
    private final String token;

    public MyBot(String username, String token) {
        Dotenv dotenv = Dotenv.load();
        this.username = username;
        this.token = token;
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            if (messageText.equals("/start")) {
                StartScene.handleStart(chatId, this);
            } else if (messageText.startsWith("/send ")) {
                String broadcastMessage = messageText.substring(6);
                BroadcastScene.sendBroadcastMessage(chatId, broadcastMessage, this);
            }
        } else if (update.hasMessage() && update.getMessage().hasContact()) {
            long chatId = update.getMessage().getChatId();
            String phoneNumber = update.getMessage().getContact().getPhoneNumber();
            String userName = update.getMessage().getFrom().getUserName();
            StartScene.saveUserInfo(chatId, phoneNumber, userName, this);
        }
    }

    public void sendTextMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void sendMessageToUser(long chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
