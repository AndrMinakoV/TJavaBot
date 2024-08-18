package net.mecheniy.scenes;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class StartScene {
    private static final MongoDatabase database;

    static {
        Dotenv dotenv = Dotenv.load();
        MongoClient mongoClient = MongoClients.create(dotenv.get("MONGODB_URI"));
        database = mongoClient.getDatabase(dotenv.get("MONGODB_DATABASE"));
    }

    public static void handleStart(long chatId, MyBot bot) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Поделитесь телефоном и вы сможете покамать!");

        // Create keyboard with a button to share phone number
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        KeyboardButton button = new KeyboardButton();
        button.setText("Share phone number");
        button.setRequestContact(true);
        row.add(button);
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);

        message.setReplyMarkup(keyboardMarkup);

        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void saveUserInfo(long chatId, String phoneNumber, String userName, MyBot bot) {
        MongoCollection<Document> collection = database.getCollection("users");
        Document doc = new Document("chatId", chatId)
                .append("phoneNumber", phoneNumber)
                .append("userName", userName)
                .append("role", 1) // Assign role 1 (user) by default
                .append("timestamp", new Timestamp(System.currentTimeMillis()));

        // Upsert: update if exists, otherwise insert
        collection.updateOne(
                new Document("phoneNumber", phoneNumber),
                new Document("$set", doc),
                new UpdateOptions().upsert(true)
        );

        bot.sendTextMessage(chatId, "Теперь вам доступны наши функции.");
    }
}
