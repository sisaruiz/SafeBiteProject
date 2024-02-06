package dao;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bson.Document;



public class UserDAO {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> usersCollection;
    // Neo4j connection details
    private Neo4jManager neo4jManager;


    public UserDAO() {
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        database = mongoClient.getDatabase("SafeBite");
        usersCollection = database.getCollection("Users");
        
        neo4jManager = new Neo4jManager();
        
    }

    public User getUserByUsername(String username) {
        Document userDocument = usersCollection.find(new Document("user_name", username)).first();
        if (userDocument != null) {
            User user = new User(username, userDocument.getString("email"), userDocument.getString("password"),
            		"https://i.ibb.co/0MCmLLM/360-F-353110097-nbpmfn9i-Hlxef4-EDIh-XB1td-TD0lc-Wh-G9.jpg",
            		userDocument.getString("country"), userDocument.getString("date_of_birth"), userDocument.getString("gender"),
            		userDocument.getString("diet_type"), userDocument.getList("friends", String.class) , 
            		userDocument.get("allergy", Document.class).getList("allergens", String.class));
            
            // Add the user data to Neo4j
            createUserInNeo4j(user);
            
            return user;
        }
        return null;
    }
    
    
    public List<User> searchUsers(String searchTerm) {
        List<User> users = new ArrayList<>();

        // Ensure usersCollection is not null
        if (usersCollection != null) {
            try {
                // Query the 'Products' collection for matching documents
                Pattern pattern = Pattern.compile(searchTerm, Pattern.CASE_INSENSITIVE);
                Document query = new Document("user_name", pattern);
                System.out.println("Constructed MongoDB Query: " + query.toJson());

                for (Document userDoc : usersCollection.find(query)) {
                    String userName = userDoc.getString("user_name");
                    // Create a Product instance
                    User user = new User(userName);
                    users.add(user);
                }
            } catch (Exception e) {
                System.out.println("Error executing search query:");
                e.printStackTrace();
            }
        } else {
            System.out.println("usersCollection is null. Check MongoDB connection.");
        }

        return users;
    }
    
    
    public void createUserInNeo4j(User user) {
        neo4jManager.createNeo4jUserNode(user.getName());
        
        if (user.getDiet() != null) {
            neo4jManager.createNeo4jUserDietRelationship(user.getName(), user.getDiet());
        }

        if (user.getListAllergens() != null && !user.getListAllergens().isEmpty()) {
            for (String allergen : user.getListAllergens()) {
                neo4jManager.createNeo4jAllergyNode(allergen);
                neo4jManager.createNeo4jUserAllergyRelationship(user.getName(), allergen);
            }
        }
    }
    
    
    public void updateUserProfile(User user) {
        // Assuming 'user_name' is the unique identifier in your MongoDB collection
        Document query = new Document("user_name", user.getName());

        // Create an update document with the modified user information
        Document update = new Document("$set", new Document()
                .append("diet_type", user.getDiet())
                .append("allergy", new Document("allergens", user.getListAllergens())));

        // Update the document in the MongoDB collection
        usersCollection.updateOne(query, update);
        
        
        
     // Update Neo4j nodes and relationships
        neo4jManager.createNeo4jUserNode(user.getName());
        
        if (user.getDiet() != null) {
            neo4jManager.createNeo4jUserDietRelationship(user.getName(), user.getDiet());
        }

        if (user.getListAllergens() != null && !user.getListAllergens().isEmpty()) {
            for (String allergen : user.getListAllergens()) {
                neo4jManager.createNeo4jAllergyNode(allergen);
                neo4jManager.createNeo4jUserAllergyRelationship(user.getName(), allergen);
            }
        }
        
    }
    
    
    public void addFriend(String user1Username, String user2Username) {
    	updateFriendsListInDatabase(user1Username, user2Username);
        updateFriendsListInDatabase(user2Username, user1Username);
    }

    
    private void updateFriendsListInDatabase(String username, String friendUsername) {
        // Assuming 'user_name' is the unique identifier in your MongoDB collection
        Document query = new Document("user_name", username);

        // Add friend to the friends list if not already present
        Document update = new Document("$addToSet", new Document("friends", friendUsername));

        // Update the document in the MongoDB collection
        usersCollection.updateOne(query, update);
    }
    
    public boolean areFriends(String user1, String user2) {
        try {
            // Assuming you have a method to get a user by username
            User user = getUserByUsername(user1);

            // Check if user2 is in the friends list of user1
            return user.getFriends() != null && user.getFriends().contains(user2);
        } catch (Exception e) {
            // Handle exceptions appropriately
            e.printStackTrace();
            return false;
        }
    }

    public void deleteUser(String username) {
        // Assuming 'user_name' is the unique identifier in your MongoDB collection
        Document query = new Document("user_name", username);

        // Delete the document from the MongoDB collection
        usersCollection.deleteOne(query);
        
        // Delete Neo4j nodes related to the user
        neo4jManager.deleteNeo4jUserNodes(username);

    }
}