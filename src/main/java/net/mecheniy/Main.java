package net.mecheniy;

import net.mecheniy.scenes.MyBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import io.github.cdimascio.dotenv.Dotenv;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        String token = dotenv.get("TOKEN");
        String username = dotenv.get("USERNAME");

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            MyBot bot = new MyBot(username, token);
            botsApi.registerBot(bot);

            // Определенный chatId пользователя, которому нужно отправить сообщение
            long chatId = 5114324539L;  // Замените на реальный chatId

            // Явное создание строки с использованием UTF-8
            byte[] bytes = "Привет!".getBytes(StandardCharsets.UTF_8);
            String messageText = new String(bytes, StandardCharsets.UTF_8);

            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(messageText);

            // Отправка сообщения
            bot.execute(message);

        } catch (TelegramApiException e) {
            System.err.println("Ошибка при отправке сообщения: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Произошла ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

