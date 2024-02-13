package dao;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bson.Document;
import org.neo4j.driver.exceptions.Neo4jException;



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
            Document allergyDocument = userDocument.get("allergy", Document.class);
            
            List<String> allergens = (allergyDocument != null) ? allergyDocument.getList("allergens", String.class) : null;

            User user = new User(username, userDocument.getString("email"), userDocument.getString("password"),
                    "https://i.ibb.co/0MCmLLM/360-F-353110097-nbpmfn9i-Hlxef4-EDIh-XB1td-TD0lc-Wh-G9.jpg",
                    userDocument.getString("country"), userDocument.getString("date_of_birth"), userDocument.getString("gender"),
                    userDocument.getString("diet_type"), userDocument.getList("friends", String.class),
                    allergens);

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
                
                // Add a condition to exclude users with 'admin' set to true
                Document query = new Document("user_name", pattern)
                                        .append("admin", false);

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
    
    
    
    public void updateUserProfile(User user) {
    	
        try {
            // MongoDB
            updateMongoDB(user);
            // Neo4j
            updateNeo4j(user);

        } catch (MongoException e) {
            // Handle MongoDB exception
            e.printStackTrace();
            System.out.println("Error updating user profile in MongoDB: " + e.getMessage());

        } catch (Neo4jException e) {
            // Handle Neo4j exception
            e.printStackTrace();
            System.out.println("Error updating user profile in Neo4j: " + e.getMessage());
            revertMongoDBUpdate(user.getName(), getExistingUserData(user.getName()));

        } catch (Exception e) {
            // Handle other exceptions
            e.printStackTrace();
            System.out.println("Error updating user profile: " + e.getMessage());         
        }
    }
    
    
    public User getExistingUserData(String username) {
        Document userDocument = usersCollection.find(new Document("user_name", username)).first();
        if (userDocument != null) {
            Document allergyDocument = userDocument.get("allergy", Document.class);

            List<String> allergens = (allergyDocument != null) ? allergyDocument.getList("allergens", String.class) : null;

            return new User(username, userDocument.getString("email"), userDocument.getString("password"),
                    "https://i.ibb.co/0MCmLLM/360-F-353110097-nbpmfn9i-Hlxef4-EDIh-XB1td-TD0lc-Wh-G9.jpg",
                    userDocument.getString("country"), userDocument.getString("date_of_birth"),
                    userDocument.getString("gender"), userDocument.getString("diet_type"),
                    userDocument.getList("friends", String.class), allergens);
        }
        return null;
    }
    
    public void revertMongoDBUpdate(String username, User existingUserData) {
        if (existingUserData != null) {
            // Create an update document with the existing user information
            Document update = new Document("$set", new Document()
                    .append("diet_type", existingUserData.getDiet())
                    .append("allergy", new Document("allergens", existingUserData.getListAllergens())));

            // Update the document in the MongoDB collection with the existing data
            usersCollection.updateOne(new Document("user_name", username), update);
        }
    }
	
	private void updateMongoDB(User user) {
		Document query = new Document("user_name", user.getName());
	    Document update = new Document("$set", new Document()
	                .append("diet_type", user.getDiet())
	                .append("allergy", new Document("allergens", user.getListAllergens())));
	        usersCollection.updateOne(query, update);
	}
	    
	private void updateNeo4j(User user) {
	    try {
	        // Neo4j
	        neo4jManager.deleteNeo4jUserDietRelationship(user.getName());
	        neo4jManager.deleteNeo4jUserAllergenRelationships(user.getName());
	        neo4jManager.createNeo4jUserDietRelationship(user.getName(), user.getDiet());

	        if (user.getListAllergens() != null && !user.getListAllergens().isEmpty()) {
	            for (String allergen : user.getListAllergens()) {
	                neo4jManager.createNeo4jUserAllergyRelationship(user.getName(), allergen);
	            }
	        }
	    } catch (Exception e) {
	        // Convert Neo4j exceptions to a specific Neo4jException (if needed)
	        throw new Neo4jException("Error updating user profile in Neo4j: " + e.getMessage(), e);
	    }
	}    
	
	
	// Add a Neo4jException class (if not already available)
	public class Neo4jException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public Neo4jException(String message, Throwable cause) {
	        super(message, cause);
	    }
	}
    
    /*
    public void updateUserProfile(User user) {
    	
    	// MongoDB
    	
        // Assuming 'user_name' is the unique identifier in your MongoDB collection
        Document query = new Document("user_name", user.getName());

        // Create an update document with the modified user information
        Document update = new Document("$set", new Document()
                .append("diet_type", user.getDiet())
                .append("allergy", new Document("allergens", user.getListAllergens())));

        // Update the document in the MongoDB collection
        usersCollection.updateOne(query, update);
        
        
        // Neo4j
        
        neo4jManager.deleteNeo4jUserDietRelationship(user.getName());
        neo4jManager.deleteNeo4jUserAllergenRelationships(user.getName());
        
        neo4jManager.createNeo4jUserDietRelationship(user.getName(), user.getDiet());
        
        if (user.getListAllergens() != null && !user.getListAllergens().isEmpty()) {
            for (String allergen : user.getListAllergens()) {
                neo4jManager.createNeo4jUserAllergyRelationship(user.getName(), allergen);
            }
        }
    }
    */

    public void deleteUser(String username) {
        // Assuming 'user_name' is the unique identifier in your MongoDB collection
        Document query = new Document("user_name", username);

        // Delete the document from the MongoDB collection
        usersCollection.deleteOne(query);
        
        // Delete Neo4j nodes related to the user
        neo4jManager.deleteNeo4jUserNodes(username);

    }
}