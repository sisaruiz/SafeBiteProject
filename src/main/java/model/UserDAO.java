package model;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

public class UserDAO {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> usersCollection;

    public UserDAO() {
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        database = mongoClient.getDatabase("SafeBite");
        usersCollection = database.getCollection("Users");
    }

    public User getUserByUsername(String username) {
        Document userDocument = usersCollection.find(new Document("user_name", username)).first();
        if (userDocument != null) {
            User user = new User(username, userDocument.getString("email"), userDocument.getString("password"),
            		"https://i.ibb.co/0MCmLLM/360-F-353110097-nbpmfn9i-Hlxef4-EDIh-XB1td-TD0lc-Wh-G9.jpg",
            		userDocument.getString("country"), userDocument.getString("date_of_birth"), userDocument.getString("gender"),
            		userDocument.getString("diet_type"), userDocument.get("allergy", Document.class).getList("allergens", String.class));
            return user;
        }
        return null;
    }
}