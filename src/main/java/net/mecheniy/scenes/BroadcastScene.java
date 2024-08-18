package net.mecheniy.scenes;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.ArrayList;
import java.util.List;

public class BroadcastScene {
    private static final MongoDatabase database;

    static {
        Dotenv dotenv = Dotenv.load();
        MongoClient mongoClient = MongoClients.create(dotenv.get("MONGODB_URI"));
        database = mongoClient.getDatabase(dotenv.get("MONGODB_DATABASE"));
    }

    public static void sendBroadcastMessage(long chatId, String broadcastMessage, MyBot bot) {
        MongoCollection<Document> collection = database.getCollection("users");
        Document user = collection.find(Filters.eq("chatId", chatId)).first();

        if (user == null) {
            bot.sendTextMessage(chatId, "User not found.");
            return;
        }

        int role = user.getInteger("role", 1);
        if (role < 2) {
            bot.sendTextMessage(chatId, "You do not have permission to send broadcast messages.");
            return;
        }

        List<Document> users = collection.find().into(new ArrayList<>());
        for (Document doc : users) {
            long recipientChatId = doc.getLong("chatId");
            bot.sendTextMessage(recipientChatId, broadcastMessage);
        }

        bot.sendTextMessage(chatId, "Broadcast message sent.");
    }
}
